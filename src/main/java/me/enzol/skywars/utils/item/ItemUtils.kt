package me.enzol.skywars.utils.item

import me.enzol.skywars.arena.island.chest.itemchest.ItemChest
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


object ItemUtils {

    fun isAttackItem(itemStack: ItemStack?) : Boolean = itemStack != null && (itemStack.type.name.contains("SWORD") || itemStack.type.name.contains("AXE"))

    fun isSame(itemStack: ItemStack, itemStack2: ItemStack) : Boolean =
         (itemStack.isSimilar(itemStack2) ||
                (isArmor(itemStack) && isArmor(itemStack2) &&
                    ((isHelmet(itemStack) && isHelmet(itemStack2)) ||
                    (isChestPlate(itemStack) && isChestPlate(itemStack2)) ||
                    (isLeggings(itemStack) && isLeggings(itemStack2)) ||
                    (isBoots(itemStack) && isBoots(itemStack2)))) ||
                (isSword(itemStack) && isSword(itemStack2)) ||
                (isPickAxe(itemStack) && isPickAxe(itemStack2)) ||
                (isAxe(itemStack) && isAxe(itemStack2)));


    fun removeSame(itemStack: ItemStack, items : MutableList<ItemChest>){
        val iterator = items.iterator()
        while(iterator.hasNext()){
            val it = iterator.next()
            if(isSame(it.itemStack, itemStack)){
                iterator.remove()
            }
        }
    }

    fun isArmor(itemStack: ItemStack) : Boolean = isHelmet(itemStack) || isChestPlate(itemStack) || isLeggings(itemStack) || isBoots(itemStack)

    fun isHelmet(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("HELMET")

    fun isChestPlate(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("CHESTPLATE")

    fun isLeggings(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("LEGGINGS")

    fun isBoots(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("BOOTS")

    fun isSword(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("SWORD")

    fun isAxe(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("SWORD")

    fun isPickAxe(itemStack: ItemStack) : Boolean = itemStack.type.name.contains("PICKAXE")

    fun serialize(itemStack: ItemStack): String {
        val serialized = StringBuilder()
        serialized.append("material:").append(itemStack.type).append(";")
            .append("durability:").append(itemStack.durability.toInt()).append(";")
            .append("amount:").append(itemStack.amount).append(";")
        if (itemStack.hasItemMeta()) {
            val meta = itemStack.itemMeta
            if (meta!!.hasDisplayName()) {
                serialized.append("name:").append(meta.displayName).append(";")
            }
            if (meta.hasLore()) {
                serialized.append("lore:").append(java.lang.String.join(",", meta.lore)).append(";")
            }
        }
        return serialized.toString()
    }

    fun deSerialized(string: String): ItemStack {
        lateinit var itemBuilder: ItemBuilder
        val serializedSplit = string.split(";").toTypedArray()
        for (str in serializedSplit) {
            if (str.contains("material")) {
                val material = str.replace("material:", "")
                itemBuilder = ItemBuilder(Material.valueOf(material))
            } else if (str.contains("durability")) {
                val durabity = str.replace("durability:", "").toInt()
                itemBuilder.durability(durabity)
            } else if (str.contains("name")) {
                val name = str.replace("name:", "")
                itemBuilder.name(name)
            } else if (str.contains("lore")) {
                val lore = str.replace("lore:", "")
                itemBuilder.lore(lore.split(",").toList())
            } else if (str.contains("amount")) {
                itemBuilder.amount(str.replace("amount:", "").toInt())
            }
        }
        return itemBuilder.build()
    }

}