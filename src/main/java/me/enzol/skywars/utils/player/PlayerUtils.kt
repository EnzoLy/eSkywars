package me.enzol.skywars.utils.player

import me.enzol.skywars.game.player.PlayerState
import me.enzol.skywars.game.player.SkywarsPlayer
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import java.util.function.Consumer

object PlayerUtils {

    fun reset(player: Player){
        player.health = 20.0
        player.saturation = 20.0f
        player.fallDistance = 0.0f
        player.foodLevel = 20
        player.fireTicks = 0
        player.maximumNoDamageTicks = 20
        player.exp = 0.0f
        player.level = 0
        player.allowFlight = false
        player.isFlying = false
        player.gameMode = GameMode.SURVIVAL

        player.inventory.setArmorContents(arrayOfNulls(4))
        player.inventory.contents = arrayOfNulls(36)
        player.updateInventory()

        player.activePotionEffects.forEach(Consumer { effect: PotionEffect ->
            player.removePotionEffect(
                effect.type
            )
        })
    }

    fun sendToSpawn(player: Player){
        reset(player)
        val skywarsPlayer = SkywarsPlayer.get(player)

        skywarsPlayer.state = PlayerState.LOBBY

        player.teleport(player.world.spawnLocation)
    }

}