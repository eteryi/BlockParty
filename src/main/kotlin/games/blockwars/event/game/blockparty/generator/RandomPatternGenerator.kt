package games.blockwars.event.game.blockparty.generator

import org.bukkit.Material

class RandomPatternGenerator : PatternGenerator {
    override fun blockAt(x: Int, y: Int): Material {
        return PatternGenerator.validBlocks.random()
    }
}