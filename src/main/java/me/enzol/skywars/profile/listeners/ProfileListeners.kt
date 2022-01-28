package me.enzol.skywars.profile.listeners

import me.enzol.skywars.profile.Profile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class ProfileListeners : Listener{

    @EventHandler
    fun onAsyncLogin(event : AsyncPlayerPreLoginEvent){

        if(event.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) return

        val uuid = event.uniqueId
        Profile.profiles[uuid] = Profile.load(uuid, event.name)
    }

}