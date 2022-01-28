package me.enzol.skywars.utils.countdown

import com.google.common.base.Preconditions
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

class CountdownBuilder(var seconds: Int) {

    lateinit var message: String
    var broadcastAt: MutableList<Int> = ArrayList()
    var tickHandler: Runnable? = null
    var broadcastHandler: Runnable? = null
    var finishHandler: Runnable? = null
    var messageFilter: Predicate<Player>? = null
    var playerList: List<Player>? = null

    init {
        Preconditions.checkArgument(seconds >= 0, "Seconds cannot must be greater than or equal to 0!")
    }

    fun withMessage(message: String): CountdownBuilder {
        this.message = message
        return this
    }

    fun broadcastAt(amount: Int, unit: TimeUnit): CountdownBuilder {
        broadcastAt.add(unit.toSeconds(amount.toLong()).toInt())
        return this
    }

    fun onTick(tickHandler: Runnable): CountdownBuilder {
        this.tickHandler = tickHandler
        return this
    }

    fun onBroadcast(broadcastHandler: Runnable): CountdownBuilder {
        this.broadcastHandler = broadcastHandler
        return this
    }

    fun onFinish(finishHandler: Runnable): CountdownBuilder {
        this.finishHandler = finishHandler
        return this
    }

    fun withMessageFilter(messageFilter: Predicate<Player>): CountdownBuilder {
        this.messageFilter = messageFilter
        return this
    }

    fun players(playerList: List<Player>): CountdownBuilder {
        this.playerList = playerList
        return this
    }

    fun start(): Countdown? {
        // Preconditions.checkNotNull((Object) this.message, "Message cannot be null!");
        if (broadcastAt.isEmpty()) {
            broadcastAt(10, TimeUnit.MINUTES)
            broadcastAt(5, TimeUnit.MINUTES)
            broadcastAt(4, TimeUnit.MINUTES)
            broadcastAt(3, TimeUnit.MINUTES)
            broadcastAt(2, TimeUnit.MINUTES)
            broadcastAt(1, TimeUnit.MINUTES)
            broadcastAt(30, TimeUnit.SECONDS)
            broadcastAt(15, TimeUnit.SECONDS)
            broadcastAt(10, TimeUnit.SECONDS)
            broadcastAt(5, TimeUnit.SECONDS)
            broadcastAt(4, TimeUnit.SECONDS)
            broadcastAt(3, TimeUnit.SECONDS)
            broadcastAt(2, TimeUnit.SECONDS)
            broadcastAt(1, TimeUnit.SECONDS)
        }

        return Countdown(
            seconds,
            message,
            tickHandler,
            broadcastHandler,
            finishHandler,
            messageFilter,
            playerList,
            convertIntegers(
                broadcastAt
            )
        )
    }

    private fun convertIntegers(integers: List<Int>): IntArray {
        val ret = IntArray(integers.size)
        for (i in ret.indices) {
            ret[i] = integers[i]
        }
        return ret
    }

}