package me.enzol.skywars.arena.island

import me.enzol.skywars.utils.cuboid.Cuboid
import org.bukkit.Location

class MidIsland(var cuboid : Cuboid) : Island() {

    override fun getSpawn(location: Location) : Location{
        return bounds.center
    }

}