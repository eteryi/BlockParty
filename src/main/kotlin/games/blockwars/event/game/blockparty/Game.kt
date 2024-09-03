package games.blockwars.event.game.blockparty

import games.blockwars.event.game.blockparty.generator.Generator
import org.bukkit.Location

class Game(private val g : Generator) {
    fun create(l : Location) {
        for (x in 0 until 25) {
            for (y in 0 until 25) {
                val block = g.blockAt(x, y)

                // TODO async this block place
                println("Set block to : {$block}")
                l.world.getBlockAt((l.x + x).toInt(), l.y.toInt(), (l.z + y).toInt()).type = block
            }
        }
    }
}