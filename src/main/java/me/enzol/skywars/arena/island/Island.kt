package me.enzol.skywars.arena.island

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import me.enzol.skywars.Skywars
import me.enzol.skywars.arena.ArenaHandler
import me.enzol.skywars.arena.island.chest.SkywarsChest
import me.enzol.skywars.utils.cuboid.Cuboid
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import java.io.File
import java.io.IOException

open class Island(){

    var chests = ArrayList<SkywarsChest>()
    var spawns = ArrayList<Location>()
    var free = true
    lateinit var bounds: Cuboid
    lateinit var schematic: String

    @Transient
    private var clipboard: Clipboard? = null

    @Transient
    private var pasteAt: Location? = null

    //@Transient
    //private val selection: Selection = Selection()

    constructor(schematic: String, clipboard: Clipboard, pasteAt: Location) :  this(){
        this.schematic = schematic
        this.clipboard = clipboard
        this.pasteAt = pasteAt
        Bukkit.getScheduler().runTaskLater(Skywars.instance, Runnable { this.scanLocations() }, 20L)
    }

    fun forEachBlock(callback: (Block) -> Unit) {

        if (clipboard == null) {
            try {
                val file: File = File(ArenaHandler.WORLD_EDIT_SCHEMATICS_FOLDER, "$schematic.schematic")
                clipboard = ClipboardFormat.findByFile(file)!!.load(file).clipboard
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val start: Location = bounds.lowerNE
        val end: Location = bounds.upperSW
        val world: World = bounds.world
        for (x in start.blockX until end.blockX) {
            for (y in start.blockY until end.blockY) {
                for (z in start.blockZ until end.blockZ) {
                    callback.invoke(world.getBlockAt(x, y, z))
                }
            }
        }
    }

    open fun scanLocations() {
        forEachBlock { block ->
            val below: Block = block.getRelative(BlockFace.DOWN)
            val type: Material = block.getType()

        };
    }

    fun getChest(location: Location) : SkywarsChest?{
        chests.forEach { if (it.location == location) return it }
        return null
    }

    open fun getSpawn(location: Location) : Location?{
        spawns.forEach { if (it == location) return it }
        return null
    }
}