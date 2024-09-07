package games.blockwars.event.game.blockparty.runnable

import org.bukkit.scheduler.BukkitRunnable

// Made for the sole reason that Bukkit Runnable are terrible to write on Kotlin.
class BlockRunnable(private val r : Runnable) : BukkitRunnable() {
    override fun run() {
        r.run()
    }
}