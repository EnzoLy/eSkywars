package me.enzol.skywars.game

import me.enzol.skywars.arena.Arena
import me.enzol.skywars.arena.island.chest.itemchest.ItemChest
import me.enzol.skywars.arena.island.chest.RefillType
import java.util.concurrent.TimeUnit

class Refill(var time : Long,val map : Arena) {

    fun refill(reffilType : RefillType){
        map.islands.forEach{ island ->
            val items = ItemChest.items.toMutableList()
            island.chests.forEach{
                it.refill(reffilType, items)
            }
        }
        val items = ItemChest.items.toMutableList()
        map.midIsland.chests.forEach{
            it.refill(reffilType, items)
        }
        time = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3)
    }

}