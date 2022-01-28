package me.enzol.skywars.utils.menu

import me.enzol.skywars.utils.color.CC
import me.enzol.skywars.utils.item.UMaterial
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.ceil
abstract class Menu {

    val buttons: MutableMap<Int, Button> = ConcurrentHashMap()

    var autoUpdate = false
    var updateAfterClick = true
    var closedByMenu = false
    var placeholder = false

    private val placeholderButton = Button.placeholder(UMaterial.RED_STAINED_GLASS_PANE.material, " ")

    private fun createItemStack(player: Player, button: Button): ItemStack {
        return button.getButtonItem(player)
    }

    fun open(player: Player) {
        val invButtons = getButtons(player)
        val previousMenu = currentlyOpenedMenus[player.name]
        var update = false
        val size = if (size == -1) size(invButtons) else size
        var title: String = CC.translate(getTitle(player))
        if (title.length > 32) {
            title = title.substring(0, 32)
        }
        var inventory = Bukkit.createInventory(player, size, title)
        if (player.openInventory.title == title) {
            if (previousMenu == null) {
                player.closeInventory()
            } else {
                val previousSize = player.openInventory.topInventory.size
                if (previousSize == size && player.openInventory.title == title) {
                    inventory = player.openInventory.topInventory
                    update = true
                } else {
                    previousMenu.closedByMenu = true
                    player.closeInventory()
                }
            }
        }
        inventory.contents = arrayOfNulls(inventory.size)

        invButtons.entries.forEach{
            buttons[it.key] = it.value
            inventory.setItem(it.key, placeholderButton.getButtonItem(player))
        }


        if (placeholder) {
            for (index in 0 until size) {
                if (buttons[index] == null) {
                    buttons[index] = placeholderButton
                    inventory.setItem(index, placeholderButton.getButtonItem(player))
                }
            }
        }
        if (update) {
            player.updateInventory()
        } else {
            player.openInventory(inventory)
        }
        onOpen(player)
        closedByMenu = false
        currentlyOpenedMenus[player.name] = this
    }

    fun size(buttons: Map<Int, Button>): Int {
        var highest = 0
        for (buttonValue in buttons.keys) {
            if (buttonValue > highest) {
                highest = buttonValue
            }
        }
        return (ceil((highest + 1) / 9.0) * 9.0).toInt()
    }

    val size: Int = -1

    fun getSlot(x: Int, y: Int): Int = 9 * y + x

    abstract fun getTitle(player: Player): String

    abstract fun getButtons(player: Player): Map<Int, Button>

    fun onOpen(player: Player) {}

    fun onClose(player: Player) {}

    fun onPlace(player: Player, itemStack: ItemStack?, event: InventoryClickEvent) {
        event.isCancelled = true
    }

    companion object {
        var currentlyOpenedMenus: MutableMap<String, Menu> = HashMap()
    }
}
