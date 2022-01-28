package me.enzol.skywars.game

import me.enzol.skywars.game.player.PlayerState
import me.enzol.skywars.game.player.SkywarsPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent

class GameListeners : Listener{

    @EventHandler
    fun onDeath(event : PlayerDeathEvent){
        val player = event.entity
        val skywarsPlayer = SkywarsPlayer.get(player)
        if(skywarsPlayer.state == PlayerState.IN_GAME){
            val game = skywarsPlayer.game
            game?.death(player, player.killer)
        }
    }

    @EventHandler
    fun onDeath(event : PlayerQuitEvent){
        val player = event.player
        val skywarsPlayer = SkywarsPlayer.get(player)
        if(skywarsPlayer.state == PlayerState.IN_GAME){
            val game = skywarsPlayer.game
            game?.death(player, player.killer)
        }
    }

    @EventHandler
    fun onBreak(event : BlockBreakEvent){
        val player = event.player
        val skywarsPlayer = SkywarsPlayer.get(player)
        if(skywarsPlayer.state == PlayerState.IN_GAME){
            val game = skywarsPlayer.game
            game?.let {
                if(it.state != GameState.PLAYING){
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onBreak(event : BlockPlaceEvent){
        val player = event.player
        val skywarsPlayer = SkywarsPlayer.get(player)
        if(skywarsPlayer.state == PlayerState.IN_GAME){
            val game = skywarsPlayer.game
            game?.let {
                if(it.state != GameState.PLAYING){
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onDamage(event : EntityDamageByEntityEvent){

        if(event.entity !is Player) return
        if(event.damager !is Player) return

        val player = event.entity as Player
        val damager = event.damager as Player
        val skywarsPlayer = SkywarsPlayer.get(player)
        val skywarsPlayerDamager = SkywarsPlayer.get(damager)

        if(skywarsPlayerDamager.state != PlayerState.IN_GAME || skywarsPlayer.state != PlayerState.IN_GAME){
            event.isCancelled = true
            return
        }

        if(skywarsPlayer.state == PlayerState.IN_GAME){
            val game = skywarsPlayer.game
            game?.let {
                if(it.state != GameState.PLAYING){
                    event.isCancelled = true
                }else{
                    if(!it.players.contains(skywarsPlayerDamager)){
                        event.isCancelled = true
                    }
                }
            }
        }
    }

}