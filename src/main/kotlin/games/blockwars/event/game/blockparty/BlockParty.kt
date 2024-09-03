package games.blockwars.event.game.blockparty

import games.blockwars.event.game.blockparty.command.GameCommand
import games.blockwars.event.game.blockparty.generator.BigGenerator
import games.blockwars.event.game.blockparty.generator.LineGenerator
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.LegacyPaperCommandManager

class BlockParty : JavaPlugin() {
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

        GameCommand().init(manager)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
