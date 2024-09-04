package games.blockwars.event.game.blockparty.runnable

import org.bukkit.scheduler.BukkitRunnable

class BlockRunnable(private val r : Runnable) : BukkitRunnable() {
    override fun run() {
        r.run()
    }
}