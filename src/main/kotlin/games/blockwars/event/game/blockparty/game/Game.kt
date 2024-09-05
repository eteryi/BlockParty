package games.blockwars.event.game.blockparty.game

import games.blockwars.event.game.blockparty.generator.*
import games.blockwars.event.game.blockparty.runnable.BlockRunnable
import games.blockwars.event.game.blockparty.runnable.RevealRunnable
import games.blockwars.event.game.blockparty.toColor
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
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import java.util.*

class Game(private val plugin : JavaPlugin, private val location : Location, val gameDuration: Duration) : Listener {
    companion object {
        const val GRID_SIZE = 25
    }

    enum class State {
        QUEUE,
        ACTIVE,
        ENDING
    }

    private val lobbyLevel = Level(location, PatternGenerator { _, _ ->
        return@PatternGenerator Material.RED_CONCRETE
    }, 0)

    var level : Level = lobbyLevel
        private set
    private val spawnLocation : Location = location.clone().add(GRID_SIZE / 2.0, 2.0, GRID_SIZE / 2.0)

    private val playersUUID : HashSet<UUID> = hashSetOf()
    val players : Collection<Player>
        get() = playersUUID.mapNotNull { Bukkit.getPlayer(it) }

    private var tasks : ArrayList<BukkitTask> = arrayListOf()
    private val hud : GameHUD = GameHUD(this)
    private var timerTask : BukkitTask? = null
    private var gameState: State = State.QUEUE
    private var startTime : Long = 0L
    private val playerSurvivalTime : HashMap<String, Long> = hashMapOf()

    private val validPatterns = listOf(
        { StripePatternGenerator() },
        { LinePatternGenerator() },
        { BigPatternGenerator() },
        { RandomPatternGenerator() }
    )

    init {
        level.generate()
    }

    fun start() {
        if (this.gameState != State.QUEUE) {
            return // We don't want to start the game if it's not being queued
        }

        this.gameState = State.ACTIVE
        this.startTime = System.currentTimeMillis()

        timerTask = BlockRunnable {
            val elapsedTime = System.currentTimeMillis() - startTime
            hud.update(elapsedTime.toInt() / 1000)
            if (gameDuration.toMillis() - elapsedTime <= 0) {
                end()
            }
        }.runTaskTimer(plugin, 0L, 10L)

        Bukkit.getOnlinePlayers().forEach { p ->
            playersUUID.add(p.uniqueId)
            Bukkit.getOnlinePlayers().filter { it != p }.forEach {
                p.hidePlayer(plugin, it)
            }
        }

        startRound()
    }

    fun end() {
        if (this.gameState != State.ACTIVE) {
            return
        }

        this.gameState = State.ENDING

        Bukkit.getOnlinePlayers().forEach {
            eliminate(it)
        }

        Bukkit.broadcast(Component.text("Game has ended!", NamedTextColor.RED))
        playerSurvivalTime
            .keys
            .sortedBy { playerSurvivalTime[it] }
            .reversed()
            .forEach { p ->
                val time = Duration.ofMillis(playerSurvivalTime[p]!!).toSeconds()
                val min = if ((time / 60) >= 10) "${time / 60}" else "0${time / 60}"
                val sec = if ((time % 60) >= 10) "${time % 60}" else "0${time % 60}"

                Bukkit.broadcast(Component.text("  - ", NamedTextColor.YELLOW)
                    .append(Component.text(p, NamedTextColor.WHITE))
                    .append(Component.text(" $min", NamedTextColor.GRAY))
                    .append(Component.text(":", NamedTextColor.DARK_GRAY))
                    .append(Component.text(sec, NamedTextColor.GRAY))
                )
        }

        this.timerTask?.cancel()

        tasks.forEach { it.cancel() }
        tasks.clear()

        object : BukkitRunnable() {
            override fun run() {
                hud.default()
                startTime = 0
                playerSurvivalTime.clear()
                level = lobbyLevel
                gameState = State.QUEUE
                println("Ended the game!")
                lobbyLevel.generate()
                playersUUID.clear()
                Bukkit.getOnlinePlayers().forEach { addPlayer(it) }
            }
        }.runTaskLater(plugin, 60L)
    }

    private fun revealColor() {
        val block = level.chosenBlock
        players.forEach { it.inventory.setItem(4, ItemStack(block)) }

        Bukkit.broadcast(
            Component.text("The block is", NamedTextColor.GRAY)
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(Component.translatable("block.minecraft." + block.name.lowercase(), block.toColor()))
        )
    }

    private fun startRound() {
        // A failsafe for the end() check on the eliminate function, just so the game doesn't accidentally go on forever
        if (players.isEmpty()) {
            end()
            return
        }

        tasks.clear()
        for (player in players) {
            player.inventory.clear()
        }

        val pattern = validPatterns.random()()
        level = Level(location, pattern, level.roundNumber + 1)
        level.generate()
        Bukkit.broadcast(Component.text("Round has started!", NamedTextColor.YELLOW))

        val reactionTime : Long = (level.reactionTime * 20.0).toLong()
        val timeUntilReveal = 35L

        tasks.add(BlockRunnable {
            revealColor()
        }.runTaskLater(plugin, timeUntilReveal))

        tasks.add(RevealRunnable(level).runTaskLater(plugin, timeUntilReveal + reactionTime))

        // 2 seconds after reactionTime has ended, we start the next round
        tasks.add(BlockRunnable {
                startRound()
        }.runTaskLater(plugin, timeUntilReveal + reactionTime + 40L))
    }

    private fun addPlayer(p : Player) {
        p.sendMessage(Component.text("You've been added to the Block Party!", NamedTextColor.GREEN))
        p.teleport(spawnLocation)
        p.gameMode = GameMode.ADVENTURE
        p.inventory.clear()
        hud.show(p)
    }

    private fun spectate(p : Player) {
        p.gameMode = GameMode.SPECTATOR
        Bukkit.getOnlinePlayers().filter { it != p }.forEach {
            p.showPlayer(plugin, it)
        }
    }


    private fun eliminate(p : Player) {
        if (!playersUUID.remove(p.uniqueId)) return
        this.spectate(p)

        Bukkit.broadcast(
            Component.text(p.name, NamedTextColor.WHITE)
                .append(Component.text(" has been eliminated", NamedTextColor.RED))
        )
        playerSurvivalTime[p.name] = System.currentTimeMillis() - startTime

        if (this.players.isEmpty()) {
            end()
        }
    }

    @EventHandler
    private fun onJoin(e: PlayerJoinEvent) {
        e.joinMessage(
            Component.text("[", NamedTextColor.GREEN)
                .append(Component.text("+", NamedTextColor.WHITE))
                .append(Component.text("] ${e.player.name}", NamedTextColor.GREEN))
        )

        if (this.gameState != State.QUEUE) {
            spectate(e.player)
            return
        }

        addPlayer(e.player)
    }

    @EventHandler
    private fun onMove(e : PlayerMoveEvent) {
        if (e.to.y <= location.y - 5) {
            if (this.gameState == State.ACTIVE) {
                eliminate(e.player)
                return
            }
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

        if (players.contains(e.player)) {
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

    @EventHandler
    private fun onDropItem(e : PlayerDropItemEvent) {
        if (e.player.gameMode != GameMode.CREATIVE) e.isCancelled = true
    }
}