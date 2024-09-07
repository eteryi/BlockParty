package games.blockwars.event.game.blockparty.config

import games.blockwars.event.game.blockparty.runnable.BlockRunnable
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.max

class Config(private val plugin : JavaPlugin) {
    var roundDuration : Int = 60 * 3
        private set
    var location : Location? = null
        get() = field?.clone()
        private set

    fun read() {
        roundDuration = max(plugin.config.getInt("round_duration", 60 * 3), 1)
        location = plugin.config.getLocation("game_location", null)

        BlockRunnable { // Expensive IO-Blocking operation, let's run it async!
            plugin.saveDefaultConfig()
        }.runTaskAsynchronously(plugin)
    }
}