package me.enzol.skywars.utils.menu

import me.enzol.skywars.utils.XSound
import me.enzol.skywars.utils.item.ItemBuilder
import org.apache.commons.lang.StringUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class Button {
    abstract fun getButtonItem(player: Player): ItemStack

    fun clicked(player: Player, clickType: ClickType) {}

    fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarSlot: Int) {}

    fun clicked(event: InventoryClickEvent) {}

    fun shouldCancel(player: Player, clickType: ClickType): Boolean {
        return true
    }

    fun shouldUpdate(player: Player, clickType: ClickType): Boolean {
        return false
    }

    companion object {
        fun placeholder(material: Material, title: String, vararg lore : String): Button = object : Button() {
            override fun getButtonItem(player: Player): ItemStack = ItemBuilder(material).name(title).lore(lore).build()
        }

        fun placeholder(material: Material, title: String): Button = object : Button() {
            override fun getButtonItem(player: Player): ItemStack = ItemBuilder(material).name(title).build()
        }

        fun playFail(player: Player) {
            player.playSound(player.location, XSound.BLOCK_NOTE_BLOCK_BASS.parseSound()!!, 20f, 0.1f)
        }

        fun playSuccess(player: Player) {
            player.playSound(player.location, XSound.BLOCK_NOTE_BLOCK_PLING.parseSound()!!, 20f, 15f)
        }

        fun playNeutral(player: Player) {
            player.playSound(player.location, XSound.UI_BUTTON_CLICK.parseSound()!!, 20f, 1f)
        }
    }
}