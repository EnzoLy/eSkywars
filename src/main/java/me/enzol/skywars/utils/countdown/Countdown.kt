package me.enzol.skywars.utils.countdown

import me.enzol.skywars.Skywars
import me.enzol.skywars.utils.color.CC
import me.enzol.skywars.utils.time.TimeUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

class Countdown(
    var secondsRemaining: Int,
    broadcastMessage: String,
    var tickHandler: Runnable?,
    var broadcastHandler: Runnable?,
    var finishHandler: Runnable?,
    var messageFilter: Predicate<Player>?,
    var playerList: List<Player>?,
    var broadcastAt: IntArray
) : Runnable{

    private var broadcastMessage: String? = broadcastMessage

    private var first = false

    // Our scheduled task's assigned id, needed for canceling
    var assignedTaskId: Int =0

    init {
        first = true
        assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Skywars.instance, this, 0L, 20L)
    }

    companion object{
        fun of(amount: Int, unit: TimeUnit): CountdownBuilder {
            return CountdownBuilder(unit.toSeconds(amount.toLong()).toInt())
        }
    }

    override fun run() {
        if (!first) {
            --secondsRemaining
        } else {
            first = false
        }

       broadcastAt.forEach { index ->
            if (secondsRemaining == index) {
                if (broadcastMessage != null) {
                    val message = broadcastMessage!!.replace("{time}", TimeUtils.formatIntoDetailedString(secondsRemaining))
                    if (playerList == null) {
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (messageFilter == null || messageFilter!!.test(player)) {
                                player.sendMessage(CC.translate(message))
                            }
                        }
                    } else {
                        for (player in playerList!!) {
                            if (player.isOnline && messageFilter == null || messageFilter!!.test(player)) {
                                player.sendMessage(CC.translate(message))
                            }
                        }
                    }
                }
                if (broadcastHandler != null) {
                    broadcastHandler!!.run()
                }
            }
        }

        if (secondsRemaining == 0) {
            if (finishHandler != null) {
                finishHandler!!.run()
            }
            Bukkit.getScheduler().cancelTask(assignedTaskId)
        } else if (tickHandler != null) {
            tickHandler!!.run()
        }
    }

    fun stop() {
        Bukkit.getScheduler().cancelTask(assignedTaskId)
    }

}