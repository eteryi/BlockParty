package games.blockwars.event.game.blockparty.generator

import games.blockwars.event.game.blockparty.game.BlockPartyGame
import org.bukkit.Material

class LinePatternGenerator : PatternGenerator{
    private val blocks = Array(BlockPartyGame.GRID_SIZE) { PatternGenerator.validBlocks.random() }

    override fun blockAt(x: Int, y: Int): Material {
        return blocks[x]
    }
}