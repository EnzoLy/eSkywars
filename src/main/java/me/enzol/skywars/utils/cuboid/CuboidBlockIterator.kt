package me.enzol.skywars.utils.cuboid

import org.bukkit.World
import org.bukkit.block.Block
import kotlin.math.abs

/**
 * Not mine
 */
class CuboidBlockIterator internal constructor(
    private val world: World,
    private val baseX: Int,
    private val baseY: Int,
    private val baseZ: Int,
    x2: Int,
    y2: Int,
    z2: Int) :
    MutableIterator<Block> {


    private val sizeX: Int = abs(x2 - baseX) + 1
    private val sizeY: Int = abs(y2 - baseY) + 1
    private val sizeZ: Int = abs(z2 - baseZ) + 1
    private var x: Int = 0
    private var y: Int = 0
    private var z: Int = 0


    override fun hasNext(): Boolean {
        return x < sizeX && y < sizeY && z < sizeZ
    }

    override fun next(): Block {
        val block = world.getBlockAt(baseX + x, baseY + y, baseZ + z)
        if (++x >= sizeX) {
            x = 0
            if (++y >= sizeY) {
                y = 0
                ++z
            }
        }
        return block
    }

    @Throws(UnsupportedOperationException::class)
    override fun remove() {
        throw UnsupportedOperationException()
    }

    init {
        y = 0
        x = 0
    }
}