package me.enzol.skywars.profile

import me.enzol.skywars.Skywars
import me.enzol.skywars.profile.box.Box
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Profile(val uuid: UUID) {

    lateinit var name : String
    val statistics = HashMap<Statistics, Int>()
    val boxes = ArrayList<Box>()
    var selectedBox : Box = Box("default")

    companion object{

        val profiles = HashMap<UUID, Profile>()

        fun load(player : Player) : Profile = load(player.uniqueId, player.name)

        fun load(uuid: UUID, name : String) : Profile {

            val profile = Profile(uuid)

            profile.name = name

            Statistics.values().forEach { profile.statistics[it] = 0 }

            Skywars.instance.database.load(profile)

            return profile
        }

        fun get(player: Player) : Profile = get(player.uniqueId)

        fun get(uuid: UUID) : Profile = profiles[uuid]!!
    }

    fun getStatistic(statistic: Statistics) : Int = if(statistics.containsKey(statistic)) statistics[statistic]!! else 0
}