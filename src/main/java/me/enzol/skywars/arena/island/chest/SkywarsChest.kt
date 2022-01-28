package me.enzol.skywars.arena.island.chest

import me.enzol.skywars.arena.island.Island
import me.enzol.skywars.arena.island.chest.itemchest.ItemChest
import me.enzol.skywars.utils.item.ItemUtils
import me.enzol.skywars.utils.item.UMaterial
import org.bukkit.Location
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack

class SkywarsChest(){

    lateinit var location : Location

    constructor(location: Location) : this() {
        this.location = location;
    }

    fun refill(reffilType : RefillType, items : MutableList<ItemChest>){
        val chest = location.block.state as Chest
        chest.blockInventory.clear()
        var sum = 0
        var slot = 0
        val maxItemsChance = 500

        while (slot != 5){
            if(items.isEmpty()) break

            val item = items.random()

            if((item.chance + sum) > maxItemsChance) break

            if(reffilType == RefillType.FIRST && item.refillType != RefillType.FIRST) continue
            if(reffilType == RefillType.SECOND && item.refillType == RefillType.THIRD) continue

            if((item.chance + sum) <= maxItemsChance){
                sum += item.chance
                chest.blockInventory.addItem(item.itemStack)
                ItemUtils.removeSame(item.itemStack, items)
                items.remove(item)
                slot++
                items.shuffled()
            }
        }

    }

    companion object{
        fun verifyItems(island : Island){
            var attackItem = false
            island.chests.forEach label@{ chest ->
                run {
                    val chestBlock = chest.location.block.state as Chest
                    chestBlock.blockInventory.forEach{
                        if(ItemUtils.isAttackItem(it)){
                            attackItem = true
                        }
                        if(attackItem) return@label
                    }
                }
            }

            if(!attackItem){
                val chest =  island.chests.random().location.block.state as Chest
                chest.blockInventory.addItem(ItemStack(UMaterial.DIAMOND_SWORD.material)) //CHANGE TO RANDOM SWORD
            }
        }
    }
}

enum class RefillType{
    FIRST,
    SECOND,
    THIRD;

    companion object{
        fun valueOfOrNull(name: String): RefillType? {
            return values().firstOrNull { it.name == name }
        }
    }
}
