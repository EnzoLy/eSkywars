package me.enzol.skywars.nametag

import dev._2lstudios.swiftboard.swift.NameTagProvider
import org.bukkit.entity.Player

class SkywarsNameTag : NameTagProvider {

    override fun getPrefix(player: Player): String {
        return "&b&l"
    }

    override fun getSuffix(player: Player): String {
        return "[1]"
    }
}