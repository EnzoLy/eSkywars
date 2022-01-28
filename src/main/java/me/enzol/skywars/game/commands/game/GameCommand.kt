package me.enzol.skywars.game.commands.game


import me.enzol.skywars.game.commands.game.sub.EndSubCommand
import me.enzol.skywars.game.commands.game.sub.StartSubCommand
import com.google.common.collect.Sets
import me.enzol.skywars.utils.color.CC
import org.bukkit.entity.Player

class GameCommand  {

    /*override fun getPermissionNode(): String {
        return "skywars.game"
    }

    fun execute(player: Player) {
        player.sendMessage(CC.translate("/game start solo"))
        player.sendMessage(CC.translate("/game start duo"))
        player.sendMessage(CC.translate("/game end"))
    }

    override fun getSubCommands(): MutableSet<BaseCommand> {
        return Sets.newHashSet(StartSubCommand(), EndSubCommand());
    }*/

}