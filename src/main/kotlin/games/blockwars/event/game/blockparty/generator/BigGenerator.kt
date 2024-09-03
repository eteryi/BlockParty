package games.blockwars.event.game.blockparty.generator

import org.bukkit.Material

class BigGenerator : Generator {
    private val blocks = Array(5) { Array(5) { Generator.validBlocks.random() } }

    override fun blockAt(x: Int, y: Int) : Material {
        return blocks[x / 5][y / 5]

    }
}