package me.enzol.skywars.arena.island.chest.itemchest

import me.enzol.skywars.arena.island.chest.RefillType
import org.bukkit.inventory.ItemStack

class ItemChest() {

    var chance = 100
    lateinit var itemStack: ItemStack
    lateinit var refillType: RefillType

    constructor(chance: Int, itemStack: ItemStack, refillType: RefillType): this() {
        this.chance = chance
        this.itemStack = itemStack
        this.refillType = refillType
    }

    companion object{
        val items = ArrayList<ItemChest>()

        fun getByType(type : RefillType) : List<ItemChest> = items.filter { it.refillType == type }
    }

}