package games.blockwars.event.game.blockparty

import games.blockwars.event.game.blockparty.command.GameCommand
import games.blockwars.event.game.blockparty.config.Config
import games.blockwars.event.game.blockparty.event.GameListener
import games.blockwars.event.game.blockparty.game.BlockPartyGame
import org.bukkit.*

import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager
import java.time.Duration

class BlockParty : JavaPlugin(), Listener {
    private lateinit var world : World

    override fun onEnable() {
        // Plugin startup login
        val manager = LegacyPaperCommandManager(
            this,
            ExecutionCoordinator.simpleCoordinator(),
            SenderMapper.identity()
        )

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier()
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions()
        }

        val config = Config(this)
        config.read()

        var loc : Location? = config.location
        if (loc == null) {
            /*
            If the location hasn't been provided,
            it will default to creating an internal void world to be used
            */
            val wc = WorldCreator("blockparty_world")
            wc.generator(VoidChunkGenerator())
            wc.generateStructures(false)
            wc.type(WorldType.FLAT)
            world = wc.createWorld() ?: throw RuntimeException("Couldn't create Void World!")
            world.difficulty = Difficulty.PEACEFUL
            world.time = 0
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)

            loc = Location(world, 0.0, 0.0, 0.0)
        }

        val game = BlockPartyGame(this, loc, Duration.ofSeconds(config.roundDuration.toLong()))

        server.pluginManager.registerEvents(GameListener(game), this)

        GameCommand.init(game, manager)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
