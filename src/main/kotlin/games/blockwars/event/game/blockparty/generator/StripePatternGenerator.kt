package games.blockwars.event.game.blockparty.generator

import games.blockwars.event.game.blockparty.Game
import org.bukkit.Material

class StripePatternGenerator : PatternGenerator {
    private val blocks = Array((Game.GRID_SIZE * 2) - 1) { PatternGenerator.validBlocks.random() }

    override fun blockAt(x: Int, y: Int): Material {
        val stripe = (x - y) + (Game.GRID_SIZE - 1)
        return blocks[stripe]
    }
}