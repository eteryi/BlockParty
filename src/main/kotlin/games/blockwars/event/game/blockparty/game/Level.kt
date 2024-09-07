package games.blockwars.event.game.blockparty.game

import games.blockwars.event.game.blockparty.generator.PatternGenerator
import org.bukkit.Location
import org.bukkit.Material
import kotlin.math.max
import kotlin.random.Random

class Level(private val loc : Location, private val g : PatternGenerator, val roundNumber : Int) {
    var chosenBlock : Material = Material.AIR
        private set

    val reactionTime : Double
        get() = max((10.0) - ((roundNumber.toDouble() - 1) / 1.75), 0.75)

    fun generate() {
        var i = 0
        val rand = Random(System.currentTimeMillis()).nextInt(0, 625)
        for (x in 0 until BlockPartyGame.GRID_SIZE) {
            for (y in 0 until BlockPartyGame.GRID_SIZE) {
                val block = g.blockAt(x, y)
                if (i++ == rand) chosenBlock = block
                loc.world.getBlockAt((loc.x + x).toInt(), loc.y.toInt(), (loc.z + y).toInt()).type = block
            }
        }
    }

    fun reveal() {
        for (x in 0 until BlockPartyGame.GRID_SIZE) {
            for (y in 0 until BlockPartyGame.GRID_SIZE) {
                val block = loc.world.getBlockAt((loc.x + x).toInt(), loc.y.toInt(), (loc.z + y).toInt())
                if (block.type != chosenBlock) block.type = Material.AIR
            }
        }
    }

}