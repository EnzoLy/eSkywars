package me.enzol.skywars.profile.box

import me.enzol.skywars.utils.mongo.DocumentSerializer
import org.bson.Document
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Box(val name : String) : DocumentSerializer {

    var materials = ArrayList<ItemStack>()

    fun setUp(location: Location){
        location.block.type = Material.GLASS
    }

    override fun serialize(): Document {
        return Document()
    }
}