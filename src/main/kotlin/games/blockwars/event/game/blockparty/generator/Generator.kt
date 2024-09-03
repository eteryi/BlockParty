package games.blockwars.event.game.blockparty.generator

import org.bukkit.Material

fun interface Generator {
    companion object {
        val validBlocks: List<Material> = Material.entries.stream().filter {
                return@filter it.name.contains(
                    "concrete",
                    ignoreCase = true
                ) && !it.name.contains("powder", true) && !it.name.contains("legacy", true)
            }.toList()
    }
    fun blockAt(x : Int, y : Int) : Material
}