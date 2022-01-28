package me.enzol.skywars.utils.time

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import java.util.regex.Pattern
import kotlin.math.abs

object TimeUtils {

    private var mmssBuilder: ThreadLocal<StringBuilder> = ThreadLocal
        .withInitial(Supplier { StringBuilder() } as Supplier<out java.lang.StringBuilder>)
    private var dateFormat: SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")

    private const val HOUR_FORMAT = "%02d:%02d:%02d"
    private const val MINUTE_FORMAT = "%02d:%02d"

    fun formatIntoHHMMSS(secs: Int): String {
        return formatIntoMMSS(secs)
    }

    fun formatLongIntoHHMMSS(secs: Long): String {
        val unconvertedSeconds = secs.toInt()
        return formatIntoMMSS(unconvertedSeconds)
    }

    fun millisToTimer(millis: Long): String {
        val seconds = millis / 1000L
        return if (seconds > 3600L) {
            String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L)
        } else {
            String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L)
        }
    }

    fun parse(input: String): Long {
        if (input == null || input.isEmpty()) {
            return -1L
        }
        var result = 0L
        var number = StringBuilder()
        for (i in 0 until input.length) {
            val c = input[i]
            if (Character.isDigit(c)) {
                number.append(c)
            } else {
                var str = ""
                if (Character.isLetter(c) && !number.toString().also { str = it }.isEmpty()) {
                    result += convert(str.toInt(), c)
                    number = StringBuilder()
                }
            }
        }
        return result
    }

    private fun convert(value: Int, unit: Char): Long {
        return when (unit) {
            'y' -> {
                value * TimeUnit.DAYS.toMillis(365L)
            }
            'M' -> {
                value * TimeUnit.DAYS.toMillis(30L)
            }
            'd' -> {
                value * TimeUnit.DAYS.toMillis(1L)
            }
            'h' -> {
                value * TimeUnit.HOURS.toMillis(1L)
            }
            'm' -> {
                value * TimeUnit.MINUTES.toMillis(1L)
            }
            's' -> {
                value * TimeUnit.SECONDS.toMillis(1L)
            }
            else -> {
                -1L
            }
        }
    }

    fun formatIntoMMSS(secs: Int): String {
        var secs = secs
        val seconds = secs % 60
        secs -= seconds
        var minutesCount = (secs / 60).toLong()
        val minutes = minutesCount % 60L
        minutesCount -= minutes
        val hours = minutesCount / 60L
        val result = mmssBuilder!!.get()
        result.setLength(0)
        if (hours > 0L) {
            if (hours < 10L) {
                result.append("0")
            }
            result.append(hours)
            result.append(":")
        }
        if (minutes < 10L) {
            result.append("0")
        }
        result.append(minutes)
        result.append(":")
        if (seconds < 10) {
            result.append("0")
        }
        result.append(seconds)
        return result.toString()
    }

    fun formatLongIntoMMSS(secs: Long): String {
        val unconvertedSeconds = secs.toInt()
        return formatIntoMMSS(unconvertedSeconds)
    }

    fun formatIntoDetailedString(secs: Int): String {
        if (secs == 0) {
            return "0 seconds"
        }
        val remainder = secs % 86400
        val days = secs / 86400
        val hours = remainder / 3600
        val minutes = remainder / 60 - hours * 60
        val seconds = remainder % 3600 - minutes * 60
        val fDays = if (days > 0) " " + days + " day" + if (days > 1) "s" else "" else ""
        val fHours = if (hours > 0) " " + hours + " hour" + if (hours > 1) "s" else "" else ""
        val fMinutes = if (minutes > 0) " " + minutes + " minute" + if (minutes > 1) "s" else "" else ""
        val fSeconds = if (seconds > 0) " " + seconds + " second" + if (seconds > 1) "s" else "" else ""
        return (fDays + fHours + fMinutes + fSeconds).trim { it <= ' ' }
    }

    fun formatLongIntoDetailedString(secs: Long): String {
        val unconvertedSeconds = secs.toInt()
        return formatIntoDetailedString(unconvertedSeconds)
    }

    fun formatIntoCalendarString(date: Date?): String {
        return dateFormat.format(date)
    }

    fun parseTime(time: String): Int {
        if (time == "0" || time == "") {
            return 0
        }
        val lifeMatch = arrayOf("w", "d", "h", "m", "s")
        val lifeInterval = intArrayOf(604800, 86400, 3600, 60, 1)
        var seconds = -1
        for (i in lifeMatch.indices) {
            val matcher = Pattern.compile("([0-9]+)" + lifeMatch[i]).matcher(time)
            while (matcher.find()) {
                if (seconds == -1) {
                    seconds = 0
                }
                seconds += matcher.group(1).toInt() * lifeInterval[i]
            }
        }
        require(seconds != -1) { "Invalid time provided." }
        return seconds
    }

    fun parseTimeToLong(time: String): Long {
        return parseTime(time).toLong()
    }

    fun getSecondsBetween(a: Date, b: Date): Int {
        return getSecondsBetweenLong(a, b).toInt()
    }

    fun getSecondsBetweenLong(a: Date, b: Date): Long {
        val diff = a.time - b.time
        val absDiff = abs(diff)
        return absDiff / 1000L
    }

    fun formatTime(time: Int): String {
        val seconds = (time -
                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(time.toLong()))).toInt()
        val minutes = TimeUnit.SECONDS.toMinutes(time.toLong()).toInt()
        val secondFormated: String = if (seconds < 10) {
            "0$seconds"
        } else {
            seconds.toString()
        }
        var minutesFormated = "00"
        if (minutes > 0) {
            minutesFormated = if (minutes < 10) {
                "0$minutes"
            } else {
                minutes.toString()
            }
        }
        return "$minutesFormated:$secondFormated"
    }

    fun format(time: Int): String {
        val seconds = (time -
                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(time.toLong()))).toInt()
        val minutes = TimeUnit.SECONDS.toMinutes(time.toLong()).toInt()
        val formated: String = if (minutes > 0) {
            minutes.toString() + "m"
        } else {
            seconds.toString() + "s"
        }
        return formated
    }

}