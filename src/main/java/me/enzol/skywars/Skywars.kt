package me.enzol.skywars

import co.aikar.commands.PaperCommandManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev._2lstudios.swiftboard.SwiftBoard
import me.enzol.skywars.arena.ArenaHandler
import me.enzol.skywars.board.SkywarsBoard
import me.enzol.skywars.database.Database
import me.enzol.skywars.database.impl.MongoDatabase
import me.enzol.skywars.game.GameListeners
import me.enzol.skywars.nametag.SkywarsNameTag
import me.enzol.skywars.profile.listeners.ProfileListeners
import me.enzol.skywars.utils.serialization.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.util.BlockVector
import org.bukkit.util.Vector

class Skywars : JavaPlugin(){

    companion object {
        lateinit var instance : Skywars
        val gson: Gson = GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect::class.java, PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location::class.java, LocationAdapter())
            .registerTypeHierarchyAdapter(Vector::class.java, VectorAdapter())
            .registerTypeAdapter(BlockVector::class.java, BlockVectorAdapter())
            .serializeNulls()
            .create()
    }

    lateinit var database: Database
    lateinit var arenaHandler: ArenaHandler
    lateinit var manager: PaperCommandManager

    override fun onEnable() {
        instance = this

        database = MongoDatabase()
        database.onEnable()

        loadManagers()
        registerListeners()
        registerCommands()
        registerScoreboard()
    }

    override fun onDisable() {
        database.saveMaps()
        database.saveItems()
    }

    private fun registerScoreboard() {
        SwiftBoard.getSwiftSidebar().provider = SkywarsBoard()
        SwiftBoard.getSwiftNametag().nameTagProvider = SkywarsNameTag()
    }

    private fun loadManagers(){
        arenaHandler = ArenaHandler(this)
    }

    private fun registerListeners(){
        arrayListOf(
            ProfileListeners(),
            GameListeners()
        ).forEach { Bukkit.getPluginManager().registerEvents(it, this) }
    }

    private fun registerCommands(){
        manager = PaperCommandManager(this)
       //commandProvider.registerCommand(SetupCommand())
        //commandProvider.registerCommand(GameCommand())
    }
}