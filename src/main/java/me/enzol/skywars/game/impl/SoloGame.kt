package me.enzol.skywars.game.impl

import me.enzol.skywars.arena.Arena
import me.enzol.skywars.game.Game
import me.enzol.skywars.game.GameType
import me.enzol.skywars.game.player.PlayerState
import me.enzol.skywars.profile.Profile
import me.enzol.skywars.utils.color.CC
import me.enzol.skywars.utils.player.PlayerUtils

class SoloGame(arena : Arena) : Game(arena) {

    override val gameType = GameType.SOLO
    override val maxPlayers: Int = arena.islands.size

    override fun preStart() {
        players.forEach { skywarsPlayer ->
            val player = skywarsPlayer.toPlayer()
            setupPlayer(player)
            val island = map.getRandomIsland() ?: run{
                players.remove(skywarsPlayer)
                PlayerUtils.sendToSpawn(player)
                player.sendMessage(CC.translate("&cGame is full"))
                return
            }

            val spawn = island.spawns[0]

            Profile.get(player).selectedBox.setUp(spawn)

            player.teleport(spawn)
            skywarsPlayer.state = PlayerState.WAITING
            island.free = false
        }
        super.preStart()
    }

}