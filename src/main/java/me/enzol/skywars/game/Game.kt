package me.enzol.skywars.game

import me.enzol.skywars.Skywars
import me.enzol.skywars.arena.Arena
import me.enzol.skywars.game.player.PlayerState
import me.enzol.skywars.game.player.SkywarsPlayer
import me.enzol.skywars.arena.island.chest.RefillType
import me.enzol.skywars.arena.island.chest.SkywarsChest
import me.enzol.skywars.arena.island.chest.itemchest.ItemChest
import me.enzol.skywars.utils.color.CC
import me.enzol.skywars.utils.countdown.Countdown
import me.enzol.skywars.utils.player.PlayerUtils
import me.enzol.skywars.utils.runnable.TaskUtil
import me.enzol.skywars.utils.time.TimeUtils
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

abstract class Game(val map : Arena) {

    private val plugin = Skywars.instance

    val players = ArrayList<SkywarsPlayer>()
    abstract val gameType : GameType
    var startAt : Long = 0
    abstract val maxPlayers : Int
    var state = GameState.WAITING
    var countdown : Countdown? = null
    lateinit var refill : Refill
    var refillNum = 1
    var endTime : Long = 0

    open fun preStart(){
        state = GameState.STARTING

        broadcast("&bStarting game...")

        countdown = Countdown.of(3, TimeUnit.SECONDS)
            .broadcastAt(20, TimeUnit.SECONDS)
            .broadcastAt(15, TimeUnit.SECONDS)
            .broadcastAt(10, TimeUnit.SECONDS)
            .broadcastAt(5, TimeUnit.SECONDS)
            .broadcastAt(4, TimeUnit.SECONDS)
            .broadcastAt(3, TimeUnit.SECONDS)
            .broadcastAt(2, TimeUnit.SECONDS)
            .broadcastAt(1, TimeUnit.SECONDS)
            .onBroadcast(Runnable {
                getBukkitPlayers().forEach {it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_PLING, 2f, 2f) }
            })
            .withMessage("&bGame start in: &f{time}")
            .players(getBukkitPlayers())
            .onFinish {
                start()
            }
            .start()
    }

    open fun start(){
        startAt = System.currentTimeMillis()
        refill = Refill(startAt + TimeUnit.MINUTES.toMillis(3), map)
        endTime = startAt + TimeUnit.MINUTES.toMillis(15)
        state = GameState.PLAYING

        broadcast("&bGame has been started.")

        players.forEach { skywarsPlayer ->
            skywarsPlayer.state = PlayerState.IN_GAME
        }

        map.islands.forEach{ island ->
            val items = ItemChest.items.toMutableList()
            island.chests.forEach{chest ->
                run {
                    chest.refill(RefillType.FIRST, items)
                }
            }
            SkywarsChest.verifyItems(island)
        }

        val items = ItemChest.items.toMutableList()

        map.midIsland.chests.forEach{
            it.refill(RefillType.THIRD, items)
        }
    }

    open fun end(){
        state = GameState.END

        broadcast("&6Game ended.")

        TaskUtil.runLater({
            getBukkitPlayers().forEach { PlayerUtils.sendToSpawn(it) }
            players.clear()
        }, 20L * 3L)

        plugin.arenaHandler.releaseArena(map)
    }

    fun death(player: Player, killer : Player?){
        killer?.let{
            val killerSkywarsPlayer = SkywarsPlayer.get(killer)

            killerSkywarsPlayer.kills++

            broadcast("${player.name} killed by ${killer.name}")
        }.also {
            broadcast("${player.name} death")
        }
        val skywarsPlayer = SkywarsPlayer.get(player)
        skywarsPlayer.state = PlayerState.SPECTATING

        getBukkitPlayers().forEach { it.hidePlayer(player) }

        if(player.isDead) player.spigot().respawn()

        player.gameMode = GameMode.SPECTATOR

        player.teleport(map.midIsland.cuboid.center)

        if(getAlivesPlayers().size <= 1){
            if(getAlivesPlayers().size == 1) winner(getAlivesPlayers().first().toPlayer())
            end()
        }
    }

    fun winner(player: Player){
        broadcast("&6Winner: &b${player.name}")
    }

    open fun setupPlayer(player: Player){
        PlayerUtils.reset(player)
    }

    fun broadcast(msg : String){
        getBukkitPlayers().forEach { it.sendMessage(CC.translate(msg)) }
    }

    fun getBukkitPlayers() : List<Player> = players.filter { skywarsPlayer -> skywarsPlayer.toPlayer().isOnline }.map { skywarsPlayer -> skywarsPlayer.toPlayer() }

    fun getAlivesPlayers() : List<SkywarsPlayer> = players.filter { skywarsPlayer ->  skywarsPlayer.isAlive()}

    fun getDuration(): String {
        if(startAt == endTime){
            end()
        }
        return if (state === GameState.END) {
            "Ending"
        } else {
            TimeUtils.millisToTimer(System.currentTimeMillis() - startAt)
        }
    }

    fun getNextRefill() : String{

        if(refillNum == 3) return ""

        if(refill.time <= System.currentTimeMillis()){
            when (refillNum) {
                1 -> {
                    refill.refill(RefillType.FIRST)
                }
                2 -> {
                    refill.refill(RefillType.SECOND)
                }
                3 -> {
                    refill.refill(RefillType.THIRD)
                }
            }
            refillNum++
            broadcast("&aAll chest has been refilled")
        }

        return TimeUtils.millisToTimer(refill.time - System.currentTimeMillis())
    }

}