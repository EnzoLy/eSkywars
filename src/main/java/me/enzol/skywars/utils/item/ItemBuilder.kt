package me.enzol.skywars.utils.item

import me.enzol.skywars.utils.color.CC
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta

class ItemBuilder : Listener {
    private val itemStack: ItemStack
    private val itemMeta: ItemMeta

    constructor(mat: Material) {
        itemStack = ItemStack(mat)
        itemMeta = itemStack.itemMeta!!
    }

    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack
        itemMeta = itemStack.itemMeta!!
    }

    fun amount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    fun color(color: Color): ItemBuilder {
        return if (itemStack.type == Material.LEATHER_BOOTS || itemStack.type == Material.LEATHER_CHESTPLATE || itemStack.type == Material.LEATHER_HELMET || itemStack.type == Material.LEATHER_LEGGINGS) {
            val meta = itemMeta as LeatherArmorMeta
            meta.setColor(color)
            itemStack.itemMeta = meta
            this
        } else {
            throw IllegalArgumentException("color() only applicable for leather armor!")
        }
    }

    fun name(name: String): ItemBuilder {
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name))
        itemStack.itemMeta = itemMeta
        return this
    }

    fun lore(name: String): ItemBuilder {
        itemMeta
        var lore = itemMeta.lore
        if (lore == null) {
            lore = ArrayList()
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', name))
        itemMeta.lore = lore
        itemStack.itemMeta = itemMeta
        return this
    }

    fun lore(lore: Array<out String>): ItemBuilder {
        val toSet: MutableList<String> = ArrayList()

        for (string in lore) {
            toSet.add(CC.translate(string))
        }
        itemMeta.lore = toSet
        itemStack.itemMeta = itemMeta
        return this
    }

    fun lore(lore: List<String>): ItemBuilder {
        val toSet: MutableList<String> = ArrayList()
        for (string in lore) {
            toSet.add(CC.translate(string))
        }
        itemMeta.lore = toSet
        itemStack.itemMeta = itemMeta
        return this
    }

    fun durability(durability: Int): ItemBuilder {
        itemStack.durability = durability.toShort()
        return this
    }

    fun enchantment(enchantment: Enchantment, level: Int): ItemBuilder {
        itemStack.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun enchantment(enchantment: Enchantment): ItemBuilder {
        itemStack.addUnsafeEnchantment(enchantment, 1)
        return this
    }

    fun type(material: Material): ItemBuilder {
        itemStack.type = material
        return this
    }

    fun clearLore(): ItemBuilder {
        itemMeta.lore = ArrayList()
        itemStack.itemMeta = itemMeta
        return this
    }

    fun clearEnchantments(): ItemBuilder {
        for (e in itemStack.enchantments.keys) {
            itemStack.removeEnchantment(e)
        }
        return this
    }

    fun addItemFlag(itemFlag: ItemFlag): ItemBuilder {
        itemMeta.addItemFlags(itemFlag)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun addItemFlags(vararg itemFlag: ItemFlag): ItemBuilder {
        itemMeta.addItemFlags(*itemFlag)
        itemStack.itemMeta = itemMeta
        return this
    }

    fun build(): ItemStack {
        addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
        return itemStack
    }
}