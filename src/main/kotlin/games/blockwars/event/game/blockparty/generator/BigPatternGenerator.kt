package games.blockwars.event.game.blockparty.generator

import org.bukkit.Material

class BigPatternGenerator : PatternGenerator {
    private val blocks = Array(5) { Array(5) { PatternGenerator.validBlocks.random() } }

    override fun blockAt(x: Int, y: Int) : Material {
        return blocks[x / 5][y / 5]
    }
}