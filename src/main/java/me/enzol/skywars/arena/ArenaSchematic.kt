package me.enzol.skywars.arena

import com.google.common.base.Preconditions
import com.sk89q.worldedit.Vector
import me.enzol.skywars.utils.item.UMaterial
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File

/**
 * Represents an arena schematic. See [live.ghostly.practice.arena]
 * for a comparision of [Arena]s and [ArenaSchematic]s.
 */
class ArenaSchematic {
    /**
     * Name of this schematic (ex "Candyland")
     */
    lateinit var name: String

    private var icon = DEFAULT_ICON

    /**
     * If matches can be scheduled on an instance of this arena.
     * Only impacts match scheduling, admin commands are (ignoring visual differences) nonchanged
     */
    val enabled = false

    /**
     * Maximum number of players that can occupy an instance of this arena.
     * Some small schematics should only be used for smaller fights
     */
    var maxPlayerCount = 15

    /**
     * Minimum number of players that can occupy an instance of this arena.
     * Some large schematics should only be used for larger fights
     */
    var minPlayerCount = 2

    /**
     * Index on the X axis on the grid (and in calculations regarding model arenas)
     * @see ArenaGrid
     */
    var gridIndex = 0

    constructor() {}
    constructor(name: String) {
        this.name = Preconditions.checkNotNull(name, "name")
    }

    val displayName: String
        get() = name!!.replace("_", " ")

    fun getIcon(): ItemStack {
        val item = icon.clone()
        val meta = item.itemMeta
        meta!!.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
        return item
    }

    fun setIcon(icon: ItemStack?) {
        this.icon = if (icon == null || icon.type == Material.AIR) DEFAULT_ICON else icon.clone()
    }

    val schematicFile: File
        get() = File(ArenaHandler.WORLD_EDIT_SCHEMATICS_FOLDER, "$name.schematic")
    val modelArenaLocation: Vector
        get() {
            val start: Vector = ArenaGrid.STARTING_POINT
            return Vector(start.blockX - ArenaGrid.GRID_SPACING_X * gridIndex, start.blockY, start.blockZ)
        }

    @Throws(Exception::class)
    fun pasteModelArena() {
        WorldEditUtils.paste(this, modelArenaLocation)
    }

    @Throws(Exception::class)
    fun removeModelArena() {
        val start = modelArenaLocation
        WorldEditUtils.clear(start, start.add(WorldEditUtils.readSchematicSize(this)))
    }

    override fun equals(`object`: Any?): Boolean {
        return `object` is ArenaSchematic && `object`.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    companion object {
        val DEFAULT_ICON: ItemStack = ItemStack(UMaterial.MAP.material)
    }
}