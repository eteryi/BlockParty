package games.blockwars.event.game.blockparty

import games.blockwars.event.game.blockparty.generator.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

class Game(private val plugin : JavaPlugin, private val location : Location) : Listener {
    companion object {
        const val GRID_SIZE = 25
    }

    private val lobbyLevel = Level(location, PatternGenerator { _, _ ->
        return@PatternGenerator Material.RED_CONCRETE
    })

    private var level : Level = lobbyLevel
    private val spawnLocation : Location = location.clone().add(GRID_SIZE / 2.0, 2.0, GRID_SIZE / 2.0)

    private val players : HashSet<UUID> = hashSetOf()
    private var tasks : ArrayList<BukkitTask> = arrayListOf()
    private var isActive : Boolean = false

    init {
        level.generate()
    }

    fun start() {
        if (!this.isActive) {
            this.isActive = true
            players().forEach { p ->
                players().filter { it != p }.forEach {
                    p.hidePlayer(plugin, it)
                }
            }
            startRound()
        }
    }

    fun end() {
        if (!this.isActive) return
        Bukkit.broadcast(Component.text("Game has ended!", NamedTextColor.RED))
        Bukkit.getOnlinePlayers().forEach { spectate(it) }
        this.isActive = false
        object : BukkitRunnable() {
            override fun run() {
                tasks.forEach { it.cancel() }
                tasks.clear()
                level = lobbyLevel
                println("Ended the game!")
                lobbyLevel.generate()
                players.clear()
                Bukkit.getOnlinePlayers().forEach { addPlayer(it) }
            }
        }.runTaskLater(plugin, 60L)
    }

    private fun revealColor() {
        val block = level.chosenBlock
        players().forEach { it.inventory.setItem(4, ItemStack(block)) }
        Bukkit.broadcast(
            Component.text("The block is: ")
                .append(Component.translatable("block.minecraft." + block.name.lowercase()))
        )
    }

    private fun startRound() {
        if (players.size <= 0) {
            end()
            return
        }
        val list = listOf(RandomPatternGenerator(), StripePatternGenerator(), LinePatternGenerator(), BigPatternGenerator())
        tasks.clear()
        for (player in players()) {
            player.inventory.clear()
        }
        level = Level(location, list.random())
        level.generate()
        Bukkit.broadcast(Component.text("Round has started!", NamedTextColor.YELLOW))

        val reactionTime : Long = 8L * 20

        tasks.add(object : BukkitRunnable() {
            override fun run() {
                revealColor()
            }
        }.runTaskLater(plugin, 60L))

        tasks.add(object : BukkitRunnable() {
            override fun run() {
                level.reveal()
                println("Reveal!")
            }
        }.runTaskLater(plugin, 60L + reactionTime))

        tasks.add(object : BukkitRunnable() {
            override fun run() {
                startRound()
            }
        }.runTaskLater(plugin, 60L + reactionTime + 40L)) // 2 seconds after reactionTime has ended
    }

    private fun addPlayer(p : Player) {
        players.add(p.uniqueId)
        p.sendMessage(Component.text("You've been added to the Block Party!", NamedTextColor.GREEN))
        p.teleport(spawnLocation)
        p.gameMode = GameMode.ADVENTURE
        p.inventory.clear()
    }

    private fun spectate(p : Player) {
        p.gameMode = GameMode.SPECTATOR
        Bukkit.getOnlinePlayers().filter { it != p }.forEach {
            p.showPlayer(plugin, it)
        }
    }


    private fun eliminate(p : Player) {
        if (!this.isActive) return
        println("elimination was called")
        players.remove(p.uniqueId)
        this.spectate(p)
        Bukkit.broadcast(
            Component.text(p.name, NamedTextColor.WHITE)
                .append(Component.text(" has been eliminated", NamedTextColor.RED))
        )

        if (this.players.size <= 0) {
            end()
        }
    }

    @EventHandler
    private fun onJoin(e : PlayerJoinEvent) {
        e.joinMessage(
            Component.text("[", NamedTextColor.GREEN)
                .append(Component.text("+", NamedTextColor.WHITE))
                .append(Component.text("] ${e.player.name}", NamedTextColor.GREEN))
        )
        if (this.isActive) {
            spectate(e.player)
            return
        }
        addPlayer(e.player)
    }

    @EventHandler
    private fun onMove(e : PlayerMoveEvent) {
        if (e.to.y <= -2 && players.contains(e.player.uniqueId)) {
            eliminate(e.player)
            e.player.teleport(spawnLocation)
        }
    }

    @EventHandler
    private fun onQuit(e : PlayerQuitEvent) {
        e.quitMessage(
            Component.text("[", NamedTextColor.RED)
                .append(Component.text("-", NamedTextColor.WHITE))
                .append(Component.text("] ${e.player.name}", NamedTextColor.RED))
        )
        if (players.contains(e.player.uniqueId)) {
            if (!this.isActive) {
                players.remove(e.player.uniqueId)
                return
            }
            eliminate(e.player)
        }
    }

    @EventHandler
    private fun onFoodSaturation(e : FoodLevelChangeEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onDamage(e : EntityDamageEvent) {
        e.isCancelled = true
    }

    @EventHandler
    private fun onEntitySpawn(e : EntitySpawnEvent) {
        if (e !is Player) e.isCancelled = true
    }

    fun players() : Collection<Player> {
        return players.mapNotNull { Bukkit.getPlayer(it) }
    }
}