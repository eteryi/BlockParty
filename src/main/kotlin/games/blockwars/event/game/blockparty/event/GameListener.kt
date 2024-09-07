package games.blockwars.event.game.blockparty.event

import games.blockwars.event.game.blockparty.game.BlockPartyGame
import games.blockwars.event.game.blockparty.game.BlockPartyGame.State
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class GameListener(private val game : BlockPartyGame) : Listener {
    private enum class Status(val icon : String, val color : TextColor) {
        JOIN("+", NamedTextColor.GREEN),
        QUIT("-", NamedTextColor.RED)
    }


    private fun statusMessage(p : Player, status: Status) : Component =
        Component.text("[", status.color)
            .append(Component.text(status.icon, NamedTextColor.WHITE))
            .append(Component.text("] ${p.name}", status.color))


    @EventHandler private fun onJoin(e: PlayerJoinEvent) {
        val player = e.player

        e.joinMessage(statusMessage(player, Status.JOIN))

        if (game.state != State.QUEUE) {
            game.addSpectator(player)
            return
        }
        game.addPlayer(player)
    }

    @EventHandler private fun onQuit(e : PlayerQuitEvent) {
        val player = e.player

        e.quitMessage(statusMessage(player, Status.QUIT))

        if (!game.players.contains(player)) return
        game.eliminate(e.player)
    }

    @EventHandler private fun onMove(e : PlayerMoveEvent) {
        if (e.to.y >= game.location.y - 5) return

        val player = e.player

        if (game.state != State.ACTIVE) {
            player.teleport(game.spawnLocation)
        }
        game.eliminate(player)
    }

    @EventHandler private fun onDropItem(e : PlayerDropItemEvent) {
        if (e.player.gameMode != GameMode.CREATIVE) e.isCancelled = true
    }

    @EventHandler private fun onFoodSaturation(e : FoodLevelChangeEvent) {
        e.isCancelled = true
    }

    @EventHandler private fun onDamage(e : EntityDamageEvent) {
        e.isCancelled = true
    }

    @EventHandler private fun onEntitySpawn(e : EntitySpawnEvent) {
        if (e !is Player) e.isCancelled = true
    }
}