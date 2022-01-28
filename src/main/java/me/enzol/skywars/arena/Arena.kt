package me.enzol.skywars.arena

import me.enzol.skywars.Skywars
import me.enzol.skywars.arena.island.Island
import me.enzol.skywars.arena.island.MidIsland
import me.enzol.skywars.utils.Callback
import me.enzol.skywars.utils.cuboid.Cuboid
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import java.util.*
import kotlin.random.Random

/**
 * Represents a pasted instance of an [ArenaSchematic].
 * See [live.ghostly.practice.arena] for a comparision of
 * [Arena]s and [ArenaSchematic]s.
 */
class Arena {

    lateinit var schematic: String
    var copy = 0
    lateinit var bounds: Cuboid
    private var spectatorSpawn: Location? = null
    val islands = ArrayList<Island>()
    lateinit var midIsland : MidIsland

    @Transient var inUse = false

    constructor() {}

    constructor(schematic: String, copy: Int, bounds: Cuboid) {
        this.schematic = schematic
        this.copy = copy
        this.bounds = bounds
        Bukkit.getScheduler().runTaskLater(Skywars.instance, Runnable { scanLocations() }, 20L)
    }

    fun getRandomIsland() : Island?{
        val islandsFree = islands.filter { island -> island.free }
        if(islandsFree.isEmpty()) return null
        return islandsFree[Random.nextInt(islandsFree.size)]
    }

    fun forEachBlock(callback: (Block) -> Unit) {
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

    fun scanLocations() {
        forEachBlock { block ->
            val below: Block = block.getRelative(BlockFace.DOWN)
            val type: Material = block.getType()


        }
    }

    fun forEachChunk(callback: Callback<Chunk?>) {
        val lowerX: Int = bounds.x1 shr 4
        val lowerZ: Int = bounds.z1 shr 4
        val upperX: Int = bounds.x2 shr 4
        val upperZ: Int = bounds.z2 shr 4
        val world: World = bounds.world
        for (x in lowerX..upperX) {
            for (z in lowerZ..upperZ) {
                callback.call(world.getChunkAt(x, z))
            }
        }
    }

    val maxBuildHeight: Int
        get() = spectatorSpawn!!.blockY

    val arenaSchematic: ArenaSchematic
        get() = Skywars.instance.arenaHandler.getSchematic(schematic)!!

    override fun equals(`object`: Any?): Boolean {
        return if (`object` is Arena) {
            val arena = `object`
            arena.schematic == schematic && arena.copy == copy
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(schematic, copy)
    }
}