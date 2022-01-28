package me.enzol.skywars.game.player

import me.enzol.skywars.game.Game
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class SkywarsPlayer(val uuid: UUID) {

    var state = PlayerState.LOBBY
    var kills = 0
    var game : Game? = null

    fun isAlive() : Boolean = state == PlayerState.IN_GAME

    fun isDead() : Boolean = state == PlayerState.SPECTATING

    fun isInLobby() : Boolean = state == PlayerState.LOBBY

    fun isWaiting() : Boolean = state == PlayerState.WAITING

    fun toPlayer() : Player = Bukkit.getPlayer(uuid)!!

    companion object{
        val players = HashMap<UUID, SkywarsPlayer>()

        fun get(player: Player) : SkywarsPlayer =
            if(players.containsKey(player.uniqueId)) players[player.uniqueId]!!
            else SkywarsPlayer(player.uniqueId).also { players[player.uniqueId] = it }
    }

}
