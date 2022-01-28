package me.enzol.skywars.board

import dev._2lstudios.swiftboard.swift.SidebarProvider
import me.enzol.skywars.game.GameState
import me.enzol.skywars.game.player.PlayerState
import me.enzol.skywars.game.player.SkywarsPlayer
import me.enzol.skywars.profile.Profile
import me.enzol.skywars.profile.Statistics
import me.enzol.skywars.utils.color.CC
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

class SkywarsBoard : SidebarProvider {

    override fun getTitle(player: Player): String = CC.translate("&b&lSkywars")

    override fun getLines(player: Player): LinkedList<String> {
        val lines = LinkedList<String>()

        val skywarsPlayer = SkywarsPlayer.get(player)

        when(skywarsPlayer.state){
            PlayerState.LOBBY ->{
                lines.addAll(getLobbyBoard(player))
            }
            PlayerState.IN_GAME, PlayerState.WAITING -> {
                lines.addAll(getGameBoard(player))
            }
            else -> {}
        }

        return lines
    }

    fun getLobbyBoard(player : Player) : MutableList<String>{
        val lines = ArrayList<String>()
        val profile = Profile.get(player)

        lines.add("")
        lines.add("&7Location: &a${player.location.x}")

        lines.add("&fYour Rank:&7&o (Silver I)")
        lines.add("")
        lines.add("&fSolo kills:&b " + profile.statistics[Statistics.SOLO_KILLS])
        lines.add("&fSolo Wins:&b " + profile.statistics[Statistics.SOLO_WINS])
        lines.add("")
        lines.add("&fDuo kills:&b " + profile.statistics[Statistics.DUO_KILLS])
        lines.add("&fDuo Wins:&b " + profile.statistics[Statistics.DUO_WINS])
        lines.add("")
        lines.add("&fRanked kills:&b " + profile.statistics[Statistics.RANKED_WINS])
        lines.add("&fRanked Wins:&b " + profile.statistics[Statistics.RANKED_KILLS])

        return lines
    }

    fun getGameBoard(player : Player) : MutableList<String>{
        val lines = ArrayList<String>()

        val skywarsPlayer = SkywarsPlayer.get(player)

        val game = skywarsPlayer.game

        game?.let {

            when (game.state) {
                GameState.WAITING -> {
                    lines.add("")
                    lines.add("&fPlayers&b ${game.players.size}&7/&b${game.maxPlayers}")
                    lines.add("")
                    lines.add("&fStarting...")
                    lines.add("")
                    lines.add("Map:&b ${game.map.schematic}")
                    lines.add("Mode:&b ${game.gameType.relName}")
                }
                GameState.STARTING -> {
                    lines.add("")
                    lines.add("&fPlayers&b ${game.players.size}&7/&b${game.maxPlayers}")
                    lines.add("")
                    lines.add("&fStarting in:&b ${game.countdown!!.secondsRemaining}")
                    lines.add("")
                    lines.add("Map:&b ${game.map.schematic}")
                    lines.add("Mode:&b ${game.gameType.relName}")
                }
                GameState.PLAYING -> {
                    lines.add("")
                    lines.add("&fPlayers Left&b ${game.players.size}&7/&b${game.maxPlayers}")
                    lines.add("")
                    lines.add("&fYour kills:&b " + skywarsPlayer.kills)
                    lines.add("")
                    if(game.getNextRefill() != "") lines.add("Next refill in:&c ${game.getNextRefill()}")
                    lines.add("Duration:&b ${game.getDuration()}")
                }
                GameState.END -> {
                    lines.add("")
                    lines.add("Duration:&b ${game.getDuration()}")
                    lines.add("Winner:&b ")
                    lines.add("")
                    lines.add("&fYour kills:&b " + skywarsPlayer.kills)
                    lines.add("")
                }
            }
        }
        return lines
    }

    /*companion object{
        fun create(): ScoreboardConfiguration? {
            val sc = ScoreboardConfiguration()
            sc.scoreGetter = SkywarsBoard()
            return sc
        }
    }*/
}