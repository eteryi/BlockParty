package games.blockwars.event.game.blockparty

import org.bukkit.Material
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class VoidChunkGenerator : ChunkGenerator() {
    override fun generateSurface(worldInfo: WorldInfo, random: Random, chunkX: Int, chunkZ: Int, chunkData: ChunkData) {
        for (y in worldInfo.minHeight until worldInfo.maxHeight) {
            chunkData.setBlock(chunkX, y, chunkZ, Material.AIR)
        }
    }
}