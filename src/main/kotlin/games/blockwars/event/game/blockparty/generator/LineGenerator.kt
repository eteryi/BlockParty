package games.blockwars.event.game.blockparty.generator

import org.bukkit.Material

class LineGenerator : Generator{
    private val blocks = Array(25) { Generator.validBlocks.random() }

    override fun blockAt(x: Int, y: Int): Material {
        return blocks[x]
    }
}