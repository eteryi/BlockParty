package games.blockwars.event.game.blockparty.generator

import org.bukkit.Material

class RandomGenerator : Generator {
    override fun blockAt(x: Int, y: Int): Material {
        return Generator.validBlocks.random()
    }
}