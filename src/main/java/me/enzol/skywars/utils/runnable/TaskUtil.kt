package me.enzol.skywars.utils.runnable

import me.enzol.skywars.Skywars
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

object TaskUtil {

    private val plugin = Skywars.instance

    fun run(runnable: Runnable) {
        plugin.server.scheduler.runTask(plugin, runnable)
    }

    fun runTimer(runnable: Runnable, delay: Long, timer: Long) {
        plugin.server.scheduler.runTaskTimer(plugin, runnable, delay, timer)
    }

    fun runTimer(runnable: BukkitRunnable, delay: Long, timer: Long) {
        runnable.runTaskTimer(plugin, delay, timer)
    }

    fun runLater(runnable: Runnable, delay: Long) {
        plugin.server.scheduler.runTaskLater(plugin, runnable, delay)
    }

    fun runAsync(runnable: Runnable) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, runnable)
    }

    fun runTimerAsync(runnable: Runnable, delay: Long, timer: Long) {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, timer)
    }
}