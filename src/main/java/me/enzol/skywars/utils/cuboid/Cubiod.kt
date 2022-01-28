package me.enzol.skywars.utils.cuboid

import com.google.common.base.Preconditions
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.collections.set

class Cuboid : Iterable<Block?>, Cloneable, ConfigurationSerializable {
    var worldName: String? = null
    var x1: Int = 0
    var y1: Int = 0
    var z1: Int = 0
    var x2: Int = 0
    var y2: Int = 0
    var z2: Int = 0

    constructor(
        world: World,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) : this(Preconditions.checkNotNull<World>(world).name, x1, y1, z1, x2, y2, z2)

    private constructor(worldName: String?, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
        this.worldName = worldName
        this.x1 = x1.coerceAtMost(x2)
        this.y1 = y1.coerceAtMost(y2)
        this.z1 = z1.coerceAtMost(z2)
        this.x2 = x1.coerceAtLeast(x2)
        this.y2 = y1.coerceAtLeast(y2)
        this.z2 = z1.coerceAtLeast(z2)
    }

    constructor(first: Location, second: Location) {
        worldName = first.world!!.name
        x1 = first.blockX.coerceAtMost(second.blockX)
        y1 = first.blockY.coerceAtMost(second.blockY)
        z1 = first.blockZ.coerceAtMost(second.blockZ)
        x2 = first.blockX.coerceAtLeast(second.blockX)
        y2 = first.blockY.coerceAtLeast(second.blockY)
        z2 = first.blockZ.coerceAtLeast(second.blockZ)
    }

    constructor(map: Map<String?, Any?>) {
        Cuboid(
            map["worldName"] as String?,
            map["x1"] as Int,
            map["y1"] as Int,
            map["z1"] as Int,
            map["x2"] as Int,
            map["y2"] as Int,
            map["z2"] as Int
        )
    }

    override fun serialize(): Map<String, Any> {
        val map: MutableMap<String, Any> = LinkedHashMap()
        map["worldName"] = (worldName)!!
        map["x1"] = x1
        map["y1"] = y1
        map["z1"] = z1
        map["x2"] = x2
        map["y2"] = y2
        map["z2"] = z2
        return map
    }

    fun hasBothPositionsSet(): Boolean {
        return minimumPoint != null && maximumPoint != null
    }

    val minimumX: Int
        get() = x1.coerceAtMost(x2)
    val minimumZ: Int
        get() = z1.coerceAtMost(z2)
    val maximumX: Int
        get() = x1.coerceAtLeast(x2)
    val maximumZ: Int
        get() = z1.coerceAtLeast(z2)

    @JvmOverloads
    fun edges(fixedMinX: Int = -1, fixedMaxX: Int = -1, fixedMinZ: Int = -1, fixedMaxZ: Int = -1): List<Vector> {
        val v1: Vector = minimumPoint!!.toVector()
        val v2: Vector = maximumPoint!!.toVector()
        val minX: Int = v1.blockX
        val maxX: Int = v2.blockX
        val minZ: Int = v1.blockZ
        val maxZ: Int = v2.blockZ
        var capacity: Int = (maxX - minX) * 4 + (maxZ - minZ) * 4
        capacity += 4
        val result: MutableList<Vector> = ArrayList(capacity)
        if (capacity <= 0) {
            return result
        }
        val minY: Int = v1.blockY
        val maxY: Int = v1.blockY
        for (x in minX..maxX) {
            result.add(Vector(x, minY, minZ))
            result.add(Vector(x, minY, maxZ))
            result.add(Vector(x, maxY, minZ))
            result.add(Vector(x, maxY, maxZ))
        }
        for (z in minZ..maxZ) {
            result.add(Vector(minX, minY, z))
            result.add(Vector(minX, maxY, z))
            result.add(Vector(maxX, minY, z))
            result.add(Vector(maxX, maxY, z))
        }
        return result
    }

    val players: Set<Player>
        get() {
            val players: MutableSet<Player> = HashSet()
            for (player: Player in Bukkit.getOnlinePlayers()) {
                if (this.contains(player)) {
                    players.add(player)
                }
            }
            return players
        }
    val lowerNE: Location
        get() = Location(world, x1.toDouble(), y1.toDouble(), z1.toDouble())
    val upperSW: Location
        get() = Location(world, x2.toDouble(), y2.toDouble(), z2.toDouble())
    val center: Location
        get() {
            val x1: Int = x2 + 1
            val y1: Int = y2 + 1
            val z1: Int = z2 + 1
            return Location(
                world,
                this.x1 + (x1 - this.x1) / 2.0,
                this.y1 + (y1 - this.y1) / 2.0,
                this.z1 + (z1 - this.z1) / 2.0
            )
        }

    val world: World
        get() = Bukkit.getWorld(worldName!!)!!
    val sizeX: Int
        get() = x2 - x1 + 1
    val sizeY: Int
        get() = y2 - y1 + 1
    val sizeZ: Int
        get() = z2 - z1 + 1

    val cornerLocations: Array<Location?>
        get() {
            val result: Array<Location?> = arrayOfNulls(8)
            val cornerBlocks: Array<Block?> = cornerBlocks
            for (i in cornerBlocks.indices) {
                result[i] = cornerBlocks.get(i)!!.location
            }
            return result
        }

    val cornerBlocks: Array<Block?>
        get() {
            val result: Array<Block?> = arrayOfNulls(8)
            val world: World = world
            result[0] = world.getBlockAt(x1, y1, z1)
            result[1] = world.getBlockAt(x1, y1, z2)
            result[2] = world.getBlockAt(x1, y2, z1)
            result[3] = world.getBlockAt(x1, y2, z2)
            result[4] = world.getBlockAt(x2, y1, z1)
            result[5] = world.getBlockAt(x2, y1, z2)
            result[6] = world.getBlockAt(x2, y2, z1)
            result[7] = world.getBlockAt(x2, y2, z2)
            return result
        }

    operator fun contains(cuboid: Cuboid): Boolean {
        return this.contains(cuboid.minimumPoint) || this.contains(cuboid.maximumPoint)
    }

    operator fun contains(player: Player): Boolean {
        return this.contains(player.location)
    }

    fun contains(world: World?, x: Int, z: Int): Boolean {
        return (world == null || (this.world == world)) && (x >= x1) && (x <= x2) && (z >= z1) && (z <= z2)
    }

    fun contains(x: Int, y: Int, z: Int): Boolean {
        return (x >= x1) && (x <= x2) && (y >= y1) && (y <= y2) && (z >= z1) && (z <= z2)
    }

    operator fun contains(block: Block): Boolean {
        return this.contains(block.location)
    }

    operator fun contains(location: Location?): Boolean {
        if (location == null || worldName == null) return false
        val world: World? = location.world
        return (world != null) && (worldName == location.world!!.name) && this.contains(
            location.blockX,
            location.blockY,
            location.blockZ
        )
    }

    val volume: Int
        get() = sizeX * sizeY * sizeZ
    val area: Int
        get() {
            val min: Location? = minimumPoint
            val max: Location? = maximumPoint
            return (max!!.blockX - min!!.blockX + 1) * (max.blockZ - min.blockZ + 1)
        }
    val averageLightLevel: Byte
        get() {
            var total: Long = 0L
            var count: Int = 0
            for (block: Block in this) {
                if (block.isEmpty) {
                    total += block.lightLevel.toLong()
                    ++count
                }
            }
            return if ((count > 0)) ((total / count).toByte()) else 0
        }
    val minimumPoint: Location?
        get() = Location(
            world,
            Math.min(x1, x2).toDouble(),
            Math.min(y1, y2).toDouble(), Math.min(z1, z2).toDouble()
        )
    val maximumPoint: Location?
        get() {
            return Location(
                world,
                Math.max(x1, x2).toDouble(),
                Math.max(y1, y2).toDouble(), Math.max(z1, z2).toDouble()
            )
        }
    val width: Int
        get() {
            return maximumPoint!!.blockX - minimumPoint!!.blockX
        }
    val height: Int
        get() {
            return maximumPoint!!.blockY - minimumPoint!!.blockY
        }
    val length: Int
        get() {
            return maximumPoint!!.blockZ - minimumPoint!!.blockZ
        }

    fun containsOnly(material: Material): Boolean {
        for (block: Block in this) {
            if (block.type != material) {
                return false
            }
        }
        return true
    }

    fun getBoundingCuboid(other: Cuboid?): Cuboid {
        if (other == null) return this
        val xMin: Int = Math.min(x1, other.x1)
        val yMin: Int = Math.min(y1, other.y1)
        val zMin: Int = Math.min(z1, other.z1)
        val xMax: Int = Math.max(x2, other.x2)
        val yMax: Int = Math.max(y2, other.y2)
        val zMax: Int = Math.max(z2, other.z2)
        return Cuboid(worldName, xMin, yMin, zMin, xMax, yMax, zMax)
    }

    fun getRelativeBlock(x: Int, y: Int, z: Int): Block {
        return world.getBlockAt(x1 + x, y1 + y, z1 + z)
    }

    fun getRelativeBlock(world: World, x: Int, y: Int, z: Int): Block {
        return world.getBlockAt(x1 + x, y1 + y, z1 + z)
    }

    val chunks: List<Chunk>
        get() {
            val world: World = world
            val x1: Int = x1 and -0x10
            val x2: Int = x2 and -0x10
            val z1: Int = z1 and -0x10
            val z2: Int = z2 and -0x10
            val result: MutableList<Chunk> = ArrayList((x2 - x1) + 16 + ((z2 - z1) * 16))
            var x3: Int = x1
            while (x3 <= x2) {
                var z3: Int = z1
                while (z3 <= z2) {
                    result.add(world.getChunkAt(x3 shr 4, z3 shr 4))
                    z3 += 16
                }
                x3 += 16
            }
            return result
        }

    override fun iterator(): MutableIterator<Block> {
        return CuboidBlockIterator(world, x1, y1, z1, x2, y2, z2)
    }

    fun locationIterator(): MutableIterator<Block>  {
        return CuboidBlockIterator(world, x1, y1, z1, x2, y2, z2)
    }

    public override fun clone(): Cuboid {
        try {
            return super.clone() as Cuboid
        } catch (ex: CloneNotSupportedException) {
            throw RuntimeException("This could never happen", ex)
        }
    }

    override fun toString(): String {
        return "Cuboid: $worldName,$x1,$y1,$z1=>$x2,$y2,$z2"
    }

    companion object {
        fun deserialize(map: Map<String?, Any?>): Cuboid {
            return Cuboid(
                map["worldName"] as String?,
                map["x1"] as Int,
                map["y1"] as Int,
                map["z1"] as Int,
                map["x2"] as Int,
                map["y2"] as Int,
                map["z2"] as Int
            )
        }
    }
}