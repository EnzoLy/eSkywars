package me.enzol.skywars.utils.color

import org.bukkit.ChatColor

object CC {

    fun translate(`in`: String?): String {
        return ChatColor.translateAlternateColorCodes('&', `in`!!)
    }

    fun translate(lines: List<String?>): List<String> {
        val toReturn: MutableList<String> = ArrayList()
        for (line in lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line!!))
        }
        return toReturn
    }

}