package me.enzol.skywars.arena

import com.boydti.fawe.`object`.schematic.Schematic
import com.sk89q.worldedit.CuboidClipboard
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.blocks.BaseBlock
import com.sk89q.worldedit.bukkit.BukkitUtil
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.schematic.SchematicFormat
import com.sk89q.worldedit.world.World
import me.enzol.skywars.Skywars
import me.enzol.skywars.utils.cuboid.Cuboid
import org.bukkit.Location
import org.bukkit.Material
import java.io.File
import java.io.IOException

object WorldEditUtils {
    var session: EditSession? = null
    var worldEditWorld: World? = null


    fun primeWorldEditApi() {
        if (session != null) return
        session = WorldEdit.getInstance().editSessionFactory.getEditSession(
            BukkitWorld(
                Skywars.instance.arenaHandler.arenaWorld
            ).also {
                worldEditWorld = it
            }, -1
        )

        session!!.setFastMode(true)
    }

    @Throws(IOException::class)
    fun paste(arena: ArenaSchematic, pasteAt: Vector?): Clipboard {
        primeWorldEditApi()

        // systems like the ArenaGrid assume that pastes will 'begin' directly at the Vector
        // provided. to ensure we can do this, we manually clear any offset (distance from
        // corner of schematic to player) to ensure our pastes aren't dependant on the
        // location of the player when copied
        val clipboard: Clipboard
        val file: File = arena.schematicFile
        val schematic: Schematic = ClipboardFormat.findByFile(file)!!.load(file)
        clipboard = schematic.getClipboard()!!
        schematic.paste(
            BukkitUtil.getLocalWorld(Skywars.instance.arenaHandler.arenaWorld),
            pasteAt,
            false,
            true,
            null
        ).flushQueue()
        return clipboard
    }

    @Throws(Exception::class)
    fun save(schematic: ArenaSchematic, saveFrom: Vector?) {
        primeWorldEditApi()
        val clipboard = CuboidClipboard(readSchematicSize(schematic), saveFrom)
        clipboard.copy(session)
        SchematicFormat.MCEDIT.save(clipboard, schematic.schematicFile)
    }

    fun clear(lower: Vector?, upper: Vector?) {
        primeWorldEditApi()
        session!!.setBlocks(CuboidRegion(worldEditWorld, lower, upper), BaseBlock(Material.AIR.id))
    }

    @Throws(Exception::class)
    fun readSchematicSize(arena: ArenaSchematic): Vector {
        val file: File = arena.schematicFile
        val clipboard = ClipboardFormat.findByFile(file)!!
            .load(file).clipboard ?: return Vector(arena.modelArenaLocation)
        return clipboard.dimensions
    }

    fun vectorToLocation(vector: Vector): Location {
        return Location(
            Skywars.instance.arenaHandler.arenaWorld,
            vector.blockX.toDouble(), vector.blockY.toDouble(), vector.blockZ.toDouble()
        )
    }
}