package me.enzol.skywars.utils.menu.menus

import me.enzol.skywars.utils.Callback
import me.enzol.skywars.utils.menu.Button
import me.enzol.skywars.utils.menu.Menu
import org.bukkit.entity.Player
import me.enzol.skywars.utils.menu.buttons.BooleanButton
import org.bukkit.Material
import java.util.HashMap

class ConfirmMenu(private val title: String, private val callback: Callback<Boolean>) : Menu() {

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons = HashMap<Int, Button>()

        for (i in 0..8) {
            when (i) {
                3 -> buttons[i] = BooleanButton(true, callback)
                5 -> buttons[i] = BooleanButton(false, callback)
                else -> buttons[i] = Button.placeholder(Material.LEGACY_STAINED_GLASS_PANE)
            }
        }

        return buttons
    }

    override fun getTitle(player: Player): String {
        return title
    }

}