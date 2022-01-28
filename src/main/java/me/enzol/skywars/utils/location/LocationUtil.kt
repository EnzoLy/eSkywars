package me.enzol.skywars.utils.location

import org.bukkit.Bukkit
import org.bukkit.Location

object LocationUtil {

    fun getFaces(start: Location): Array<Location?> {
        val faces = arrayOfNulls<Location>(4)
        faces[0] = Location(start.world, start.x + 1, start.y, start.z)
        faces[1] = Location(start.world, start.x - 1, start.y, start.z)
        faces[2] = Location(start.world, start.x, start.y + 1, start.z)
        faces[3] = Location(start.world, start.x, start.y - 1, start.z)
        return faces
    }

    fun serialize(location: Location?): String {
        return if (location == null) {
            "null"
        } else location.world!!.name + ":" + location.x + ":" + location.y + ":" + location.z +
                ":" + location.yaw + ":" + location.pitch
    }

    fun deserialize(source: String?): Location? {
        if (source == null) {
            return null
        }
        val split = source.split(":").toTypedArray()
        val world = Bukkit.getServer().getWorld(split[0]) ?: return null
        return Location(
            world,
            split[1].toDouble(),
            split[2].toDouble(),
            split[3].toDouble(),
            split[4].toFloat(),
            split[5].toFloat()
        )
    }

}