package me.enzol.skywars.database

import me.enzol.skywars.profile.Profile

interface Database {

    fun onEnable()

    fun load(profile: Profile)

    fun save(profile: Profile)

    fun loadMaps()

    fun loadItems()

    fun saveMaps()

    fun saveItems()

}