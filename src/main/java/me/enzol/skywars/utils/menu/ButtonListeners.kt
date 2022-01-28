package me.enzol.skywars.utils.menu

import me.enzol.skywars.Skywars
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class MenuListener(val plugin : Skywars) : Listener {



    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun onButtonPress(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val openMenu = Menu.currentlyOpenedMenus[player.name]
        if (openMenu != null) {
            if (event.clickedInventory === player.inventory) {
                if (event.isShiftClick) {
                    openMenu.onPlace(player, event.currentItem, event)
                    return
                }
                return
            }
            if (openMenu.buttons.containsKey(event.slot)) {
                val button = openMenu.buttons[event.slot]
                val cancel = button!!.shouldCancel(player, event.click)
                if (!cancel && (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT)) {
                    event.isCancelled = true
                    if (event.currentItem != null) {
                        player.inventory.addItem(event.currentItem)
                    }
                } else {
                    event.isCancelled = cancel
                }
                button.clicked(event)
                button.clicked(player, event.click)
                button.clicked(player, event.slot, event.click, event.hotbarButton)
                if (Menu.currentlyOpenedMenus.containsKey(player.name)) {
                    val newMenu = Menu.currentlyOpenedMenus[player.name]
                    if (newMenu === openMenu) {
                        if (newMenu.updateAfterClick) {
                            openMenu.closedByMenu = true
                            newMenu.open(player)
                        }
                    }
                } else if (button.shouldUpdate(player, event.click)) {
                    openMenu.closedByMenu = true
                    openMenu.open(player)
                }
                if (event.isCancelled) {
                    Bukkit.getScheduler().runTaskLater(plugin, Runnable { player.updateInventory() }, 1L)
                }
            } else {
                /**/
                val action = event.action
                run@when (action) {
                    InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                        val from = event.clickedInventory
                        val to = event.inventory
                        if (from == null) {
                            event.isCancelled = true
                        }
                        if (to === player.inventory) {
                            event.isCancelled = true
                        }
                        openMenu.onPlace(player, event.currentItem, event)
                    }
                    InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME -> {
                        run {
                            if (event.clickedInventory !== player.inventory) {
                                openMenu.onPlace(player, event.cursor, event)
                            }
                        }
                        run {
                            if (event.clickedInventory !== player.inventory) {
                                event.isCancelled = true
                            }
                        }
                    }
                    else -> {
                        if (event.clickedInventory !== player.inventory) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        val openMenu = Menu.currentlyOpenedMenus[player.name]
        if (openMenu != null) {
            openMenu.onClose(player)
            Menu.currentlyOpenedMenus.remove(player.name)
        }
    }
}