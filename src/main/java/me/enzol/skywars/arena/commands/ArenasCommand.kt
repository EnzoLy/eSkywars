package me.enzol.skywars.arena.commands

import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import com.boydti.fawe.util.TaskManager
import me.enzol.skywars.Skywars
import me.enzol.skywars.arena.ArenaHandler
import me.enzol.skywars.arena.ArenaSchematic
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.io.File

@CommandAlias("arena")
@CommandPermission("skywars.setup.arena")
class ArenasCommand(private val plugin: Skywars) {

    @Default
    fun arena(player : Player, help : CommandHelp) {
        help.showHelp()
    }

    @Subcommand("create")
    fun create(sender : Player, schematicName : String) {

        val handler: ArenaHandler = plugin.arenaHandler

        if (handler.getSchematic(schematicName) != null) {
            sender.sendMessage(ChatColor.RED.toString() + "Schematic " + schematicName + " already exists")
            return
        }

        val schematic = ArenaSchematic(schematicName)
        val file: File = schematic.schematicFile
        if (!file.exists()) {
            sender.sendMessage(ChatColor.RED.toString() + "No file for " + schematic + " found. (" + file.path + ")")
            return
        }

        handler.registerSchematic(schematic)

        try {
            TaskManager.IMP.async {
                try {
                    schematic.pasteModelArena();
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
            handler.saveSchematics()
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }

        sender.sendMessage(ChatColor.YELLOW.toString() + "Arena " + ChatColor.BLUE + schematic + ChatColor.YELLOW + " has been created!")
    }

    @Subcommand("edit")
    fun edit(player : Player){

    }

}