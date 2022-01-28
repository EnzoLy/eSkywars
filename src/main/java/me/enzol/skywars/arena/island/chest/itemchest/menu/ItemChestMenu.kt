package me.enzol.skywars.arena.island.chest.itemchest.menu


import me.enzol.skywars.arena.island.chest.itemchest.ItemChest
import me.enzol.skywars.utils.menu.Button
import me.enzol.skywars.utils.menu.Menu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemChestMenu(val items : List<ItemChest>) : Menu() {

    override fun getTitle(player: Player): String {
        return ""
    }

    class ItemButton(val itemChest: ItemChest) : Button(){
        override fun getButtonItem(player: Player): ItemStack {
            TODO("Not yet implemented")
        }
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = HashMap<Int, Button>()
        items.forEach {
            buttons[buttons.size] = ItemButton(it)
        }
        return buttons
    }
}