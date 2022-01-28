package me.enzol.skywars.database.impl

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import me.enzol.skywars.database.Database
import me.enzol.skywars.arena.island.chest.RefillType
import me.enzol.skywars.arena.island.chest.itemchest.ItemChest
import me.enzol.skywars.profile.Profile
import me.enzol.skywars.profile.Statistics
import me.enzol.skywars.utils.item.ItemUtils
import org.bson.Document

class MongoDatabase : Database {

    lateinit var database : MongoDatabase
    lateinit var profileCollection: MongoCollection<Document>
    lateinit var mapsCollection: MongoCollection<Document>
    lateinit var itemsCollection: MongoCollection<Document>

    override fun onEnable() {
        database = MongoClient("localhost").getDatabase("skywars")
        profileCollection = database.getCollection("profiles")
        mapsCollection = database.getCollection("maps")
        itemsCollection = database.getCollection("items")
        loadMaps()
        loadItems()
    }


    override fun load(profile: Profile) {
        val document : Document? = profileCollection.find(Filters.eq("uuid", profile.uuid.toString())).first()

        document?.let {
            profile.name = document.getString("name")
            if(document.containsKey("statistics")){
                val statisticsDocument : Document = document["statistics"] as Document

                for (value in Statistics.values()) {
                    profile.statistics[value] = statisticsDocument.getInteger(value.name)
                }
            }

        } ?: run {
            save(profile)
        }
    }

    override fun loadMaps() {
        /*mapsCollection.find().forEach{ document ->

            val map = Map(document.getString("name"))

            if(document.containsKey("min") && document.containsKey("max")){
                val min = LocationUtil.deserialize(document.getString("min"))

                val max = LocationUtil.deserialize(document.getString("max"))

                map.cuboid = Cuboid(min, max)
            }

            val midDocument : Document? = document["midisland"] as Document?

            midDocument?.let {
                val cuboid = Cuboid(LocationUtil.deserialize(it.getString("first")), LocationUtil.deserialize(it.getString("second")))

                val chests = ArrayList<SkywarsChest>()

                it.getList("chests", Document::class.java).forEach{ chestsDocument ->
                    val location = LocationUtil.deserialize(chestsDocument.getString("location"))
                    //val items = ArrayList<Item>()

                    /*chestsDocument.getList("items", Document::class.java).forEach { itemsDocument ->
                        items.add(Item((ItemUtils.deSerialized(itemsDocument.getString("item"))), itemsDocument.getInteger("chance")))
                    }*/

                    val chest = SkywarsChest(location)

                    //chest.items = items

                    chests.add(chest)
                }

                val island = MidIsland(cuboid)

                island.chests = chests

                map.midIsland = island
            }


            document.getList("islands", Document::class.java).forEach{

                val cuboid = Cuboid(LocationUtil.deserialize(it.getString("first")), LocationUtil.deserialize(it.getString("second")))

                val chests = ArrayList<SkywarsChest>()

                it.getList("chests", Document::class.java).forEach{ chestsDocument ->
                    val location = LocationUtil.deserialize(chestsDocument.getString("location"))

                    val chest = SkywarsChest(location)

                    chests.add(chest)
                }

                val spawns : List<Location> = it.getList("spawns", String::class.java).map { spawnsDocument ->
                    LocationUtil.deserialize(spawnsDocument)
                }

                val island = Island(cuboid)

                island.spawns = ArrayList(spawns)

                island.chests = chests

                map.islands.add(island)
            }

            Map.maps[map.name] = map
        }*/
    }

    override fun saveMaps() {

        /*Map.maps.values.forEach{
            val document = Document()

            document["name"] = it.name
            document["islands"] = it.islands.map { it.serilize() }
            document["midisland"] = it.midIsland?.serilize()
            document["min"] = LocationUtil.serialize(it.cuboid.minimumPoint)
            document["max"] = LocationUtil.serialize(it.cuboid.maximumPoint)

            val name = it.name

            mapsCollection.replaceOne(Filters.eq("name", name), document, ReplaceOptions().upsert(true))
        }*/

    }

    override fun save(profile: Profile) {
        var document : Document? = profileCollection.find(Filters.eq("uuid", profile.uuid.toString())).first()

        if(document == null) document = Document()

        document["uuid"] = profile.uuid.toString()

        document["name"] = profile.name

        if(profile.statistics.isNotEmpty()){
            val statisticsDocument = Document()

            profile.statistics.forEach { statisticsDocument[it.key.name] = it.value }

            document["statistics"] = statisticsDocument
        }

        document["boxes"] = profile.boxes.map { it.serialize() }

        profileCollection.replaceOne(Filters.eq("uuid", profile.uuid.toString()), document, ReplaceOptions().upsert(true))
    }


    override fun loadItems(){
        itemsCollection.find().forEach{ document ->

            val chance = document["chance"] as Int
            val item = ItemUtils.deSerialized(document["item"] as String)
            val refill = RefillType.valueOf(document["refill"] as String)

            ItemChest.items.add(ItemChest(chance, item, refill))
        }
    }

    override fun saveItems(){
        ItemChest.items.forEach {
            val document = Document()

            document["chance"] = it.chance
            document["items"] = ItemUtils.serialize(it.itemStack)
            document["refill"] = it.refillType.name

            itemsCollection.insertOne(document)
        }
    }
}