package me.enzol.skywars.arena

import com.boydti.fawe.util.TaskManager
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.extent.clipboard.Clipboard
import me.enzol.skywars.Skywars
import me.enzol.skywars.utils.cuboid.Cuboid
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import java.io.IOException

/**
 * Represents the grid on the world
 *
 * Z ------------->
 * X  (1,1) (1,2)
 * |  (2,1) (2,2)
 * |  (3,1) (3,2)
 * |  (4,1) (4,2)
 * V
 *
 * X is per [ArenaSchematic] and is stored in [ArenaSchematic.getGridIndex].
 * Z is per [Arena] and is the [Arena]'s [Arena.getCopy].
 *
 * Each arena is allocated [.GRID_SPACING_Z] by [.GRID_SPACING_X] blocks
 *
 * @author Mazen Kotb
 */
class ArenaGrid {

    private var busy = false
    fun free() {
        busy = false
    }

    private fun deleteArenas(schematic: ArenaSchematic, currentCopies: Int, toDelete: Int, callback: Runnable) {
        val handler: ArenaHandler = Skywars.instance.arenaHandler
        object : BukkitRunnable() {
            var deleted = 0
            override fun run() {
                val copy = currentCopies - deleted
                val existing: Arena? = handler.getArena(schematic, copy)
                if (existing != null) {
                    existing.forEachBlock { block ->
                        //BukkitUtils.setBlockInNativeWorld(block.getLocation(), Material.AIR, 0.toByte(), false)
                    }
                    handler.unregisterArena(existing)
                }
                deleted++
                if (deleted == toDelete) {
                    callback.run()
                    cancel()
                }
            }
        }.runTaskTimer(Skywars.instance, 8L, 8L)
    }

    private fun createArena(schematic: ArenaSchematic, xStart: Int, zStart: Int, copy: Int): Arena {
        val clipboard: Clipboard
        val pasteAt: Vector = Vector(xStart, STARTING_POINT.y.toInt(), zStart)
        val name: String = schematic.name
        try {
            clipboard = WorldEditUtils.paste(schematic, pasteAt)
            Skywars.instance.logger
                .info("[ArenaGrid] Pasted new arena copy ($copy) of $name at $xStart, $zStart.")
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }
        return Arena(
            name,
            copy,
            Cuboid(
                WorldEditUtils.vectorToLocation(pasteAt),
                WorldEditUtils.vectorToLocation(pasteAt.add(clipboard.dimensions))
            )
        )
    }

    private fun createArenas(schematic: ArenaSchematic, currentCopies: Int, toCreate: Int, callback: Runnable) {
        val plugin: Skywars = Skywars.instance
        val handler: ArenaHandler = plugin.arenaHandler
        object : BukkitRunnable() {
            var created = 0
            override fun run() {
                val copy = currentCopies + created + 1 // arenas are 1-indexed, not 0
                val xStart: Int = STARTING_POINT.blockX + GRID_SPACING_X * schematic.gridIndex
                val zStart = STARTING_POINT.blockZ + GRID_SPACING_Z * copy
                try {
                    TaskManager.IMP.async {
                        handler.registerArena(
                            createArena(
                                schematic,
                                xStart,
                                zStart,
                                copy
                            )
                        )
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    callback.run()
                    cancel()
                }
                created++
                if (created == toCreate) {
                    callback.run()
                    cancel()
                }
            }
        }.runTaskTimer(plugin, 8L, 8L)
    }

    fun scaleCopies(schematic: ArenaSchematic, desiredCopies: Int, callback: Runnable) {
        check(!busy) { "Grid is busy!" }
        busy = true
        val arenaHandler: ArenaHandler = Skywars.instance.arenaHandler
        val currentCopies = arenaHandler.countArenas(schematic)
        val saveWrapper = Runnable {
            try {
                arenaHandler.saveArenas()
            } catch (ex: IOException) {
                throw RuntimeException(ex)
            }
            busy = false
            callback.run()
        }
        if (currentCopies > desiredCopies) {
            deleteArenas(schematic, currentCopies, currentCopies - desiredCopies, saveWrapper)
        } else if (currentCopies < desiredCopies) {
            createArenas(schematic, currentCopies, desiredCopies - currentCopies, saveWrapper)
        } else {
            saveWrapper.run()
        }
    }

    companion object {
        /**
         * 'Starting' point of the grid. Expands (+, +) from this point.
         */
        val STARTING_POINT = Vector(1000, 80, 1000)
        const val GRID_SPACING_X = 1000
        const val GRID_SPACING_Z = 1000
    }
}