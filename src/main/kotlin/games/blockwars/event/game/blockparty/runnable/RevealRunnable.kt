package games.blockwars.event.game.blockparty.runnable

import games.blockwars.event.game.blockparty.game.Level
import org.bukkit.scheduler.BukkitRunnable

class RevealRunnable(private val level : Level) : BukkitRunnable() {
    override fun run() {
        level.reveal()
    }
}