package me.enzol.skywars.arena

import com.google.common.base.Charsets
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableSet
import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import me.enzol.skywars.Skywars
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Predicate


/**
 * Facilitates easy access to [ArenaSchematic]s and to [Arena]s
 * based on their schematic+copy pair
 */
class ArenaHandler(private val plugin: Skywars) {

    // schematic -> (instance id -> Arena instance)
    private val arenaInstances: MutableMap<String, MutableMap<Int, Arena>?> = HashMap()

    // schematic name -> ArenaSchematic instance
    private val schematics: MutableMap<String, ArenaSchematic> = HashMap()

    init {
        //plugin.getServer().getPluginManager().registerEvents(ArenaItemResetListener(), plugin)
        val worldFolder = arenaWorld.worldFolder
        val arenaInstancesFile = File(worldFolder, ARENA_INSTANCES_FILE_NAME)
        val schematicsFile = File(worldFolder, SCHEMATICS_FILE_NAME)
        try {
            val gson: Gson = Skywars.gson
            if (arenaInstancesFile.exists()) {
                Files.newReader(arenaInstancesFile, Charsets.UTF_8).use { reader ->
                    val arenas: List<Arena> = gson.fromJson<List<Arena>>(
                        reader,
                        object : TypeToken<List<Arena?>?>() {}.type
                    )
                    for (arena in arenas) {
                        val schematic: String = arena.schematic
                        arenaInstances.computeIfAbsent(
                            schematic
                        ) { inner: String? -> HashMap<Int, Arena>() }
                        arenaInstances[schematic]!![arena.copy] = arena
                    }
                }
            }
            if (schematicsFile.exists()) {
                Files.newReader(schematicsFile, Charsets.UTF_8).use { reader ->
                    val arenaSchematics: List<ArenaSchematic> =
                        gson.fromJson<List<ArenaSchematic>>(
                            reader,
                            object :
                                TypeToken<List<ArenaSchematic?>?>() {}.type
                        )
                    for (schematic in arenaSchematics) {
                        schematics[schematic.name] = schematic
                    }
                }
            }
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
    }

    private val grid: ArenaGrid = ArenaGrid()
    val arenaWorld: World
        get() = Bukkit.getWorlds()[0]

    @Throws(IOException::class)
    fun saveSchematics() {
        Files.write(
            Skywars.gson.toJson(schematics.values),
            File(arenaWorld.worldFolder, SCHEMATICS_FILE_NAME),
            Charsets.UTF_8
        )
    }

    @Throws(IOException::class)
    fun saveArenas() {
        val arenas: MutableList<Arena> = ArrayList<Arena>()
        arenaInstances.forEach { (schematic: String?, copies: Map<Int, Arena>?) ->
            arenas.addAll(
                copies!!.values
            )
        }
        Files.write(
            Skywars.gson.toJson(arenas),
            File(arenaWorld.worldFolder, ARENA_INSTANCES_FILE_NAME),
            Charsets.UTF_8
        )
    }

    fun registerSchematic(schematic: ArenaSchematic) {
        // assign a grid index upon creation. currently this will not reuse
        // lower grid indexes from deleted arenas.
        var lastGridIndex = 0
        for (otherSchematic in schematics.values) {
            lastGridIndex = lastGridIndex.coerceAtLeast(otherSchematic.gridIndex)
        }
        schematic.gridIndex = lastGridIndex + 1
        schematics[schematic.name] = schematic
    }

    fun unregisterSchematic(schematic: ArenaSchematic) {
        schematics.remove(schematic.name)
    }

    fun registerArena(arena: Arena) {
        arenaInstances.computeIfAbsent(
            arena.schematic
        ) { empty: String? -> HashMap<Int, Arena>() }!![arena.copy] = arena
    }

    fun unregisterArena(arena: Arena) {
        val copies: MutableMap<Int, Arena>? = arenaInstances[arena.schematic]
        copies?.remove(arena.copy)
    }

    /**
     * Finds an arena by its schematic and copy pair
     *
     * @param schematic ArenaSchematic to use when looking up arena
     * @param copy      copy of arena to look up
     * @return Arena object existing for specified schematic and copy pair, if one exists
     */
    fun getArena(schematic: ArenaSchematic, copy: Int): Arena? {
        val arenaCopies: Map<Int, Arena>? = arenaInstances[schematic.name]
        return arenaCopies?.get(copy)
    }

    /**
     * Finds all arena instances for the given schematic
     *
     * @param schematic schematic to look up arenas for
     * @return immutable set of all arenas for given schematic
     */
    fun getArenas(schematic: ArenaSchematic): Set<Arena> {
        val arenaCopies: Map<Int, Arena>? = arenaInstances[schematic.name]
        return if (arenaCopies != null) {
            ImmutableSet.copyOf(arenaCopies.values)
        } else {
            ImmutableSet.of<Arena>()
        }
    }

    /**
     * Counts the number of arena instances present for the given schematic
     *
     * @param schematic schematic to count arenas for
     * @return number of copies present of the given schematic
     */
    fun countArenas(schematic: ArenaSchematic): Int {
        val arenaCopies: Map<Int, Arena>? = arenaInstances[schematic.name]
        return arenaCopies?.size ?: 0
    }

    /**
     * Finds all schematic instances registered
     *
     * @return immutable set of all schematics registered
     */
    fun getSchematics(): Set<ArenaSchematic> {
        return ImmutableSet.copyOf(schematics.values)
    }

    /**
     * Finds an ArenaSchematic by its id
     *
     * @param schematicName schematic id to search with
     * @return ArenaSchematic present for the given id, if one exists
     */
    fun getSchematic(schematicName: String): ArenaSchematic? {
        return schematics[schematicName]
    }

    /**
     * Attempts to allocate an arena for use, using the Predicate provided to determine
     * which arenas are eligible for use. Handles calling [ArenaAllocatedEvent]
     * automatically.
     *
     * @param acceptableSchematicPredicate Predicate to use to determine if an [ArenaSchematic]
     * is eligible for use.
     * @return The arena which has been allocated for use, or null, if one was not found.
     */
    fun allocateUnusedArena(
        acceptableSchematicPredicate: Predicate<ArenaSchematic?>,
        arenaSchematic: ArenaSchematic?,
        markAsUsed: Boolean
    ): Optional<Arena> {
        val acceptableArenas: MutableList<Arena> = ArrayList<Arena>()
        for (schematic in schematics.values) {
            if (!acceptableSchematicPredicate.test(schematic)) continue
            if (arenaSchematic != null && !arenaSchematic.equals(schematic)) continue
            if (!arenaInstances.containsKey(schematic.name)) continue
            for (arena in arenaInstances[schematic.name]!!.values) {
                if (!arena.inUse) {
                    acceptableArenas.add(arena)
                }
            }
        }
        if (acceptableArenas.isEmpty()) return Optional.empty<Arena>()
        val selected: Arena = acceptableArenas[ThreadLocalRandom.current().nextInt(acceptableArenas.size)]
        if (markAsUsed) {
            selected.inUse = true
        }
        //Bukkit.getPluginManager().callEvent(ArenaAllocatedEvent(selected))
        return Optional.of<Arena>(selected)
    }

    fun allocateUnusedArena(
        acceptableSchematicPredicate: Predicate<ArenaSchematic?>,
        arenaSchematic: ArenaSchematic?
    ): Optional<Arena> {
        return allocateUnusedArena(acceptableSchematicPredicate, arenaSchematic, true)
    }

    /**
     * Releases (unallocates) an arena so that it may be used again. Handles calling
     * [ArenaReleasedEvent] automatically.
     *
     * @param arena the arena to release
     */
    fun releaseArena(arena: Arena) {
        Preconditions.checkArgument(arena.inUse, "Cannot release arena not in use.")
        arena.inUse = false
        //Bukkit.getPluginManager().callEvent(ArenaReleasedEvent(arena))
    }

    companion object {
        val WORLD_EDIT_SCHEMATICS_FOLDER = File(
            JavaPlugin.getPlugin(
                WorldEditPlugin::class.java
            ).dataFolder, "schematics"
        )
        private const val ARENA_INSTANCES_FILE_NAME = "arenaInstances.json"
        private const val SCHEMATICS_FILE_NAME = "schematics.json"
    }
}