package games.blockwars.event.game.blockparty.game

import games.blockwars.event.game.blockparty.generator.*
import games.blockwars.event.game.blockparty.runnable.BlockRunnable
import games.blockwars.event.game.blockparty.toColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.time.Duration
import java.util.*

class BlockPartyGame(
    private val plugin: JavaPlugin,
    private val _location: Location,
    private val gameDuration: Duration
) {
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

    private var level: Level = lobbyLevel

    val location: Location
        get() = _location.clone()

    val spawnLocation: Location
        get() = location.add(GRID_SIZE / 2.0, 2.0, GRID_SIZE / 2.0)

    private val playersUUID: HashSet<UUID> = hashSetOf()
    val players: Collection<Player>
        get() = playersUUID.mapNotNull { Bukkit.getPlayer(it) }

    private var tasks: ArrayList<BukkitTask> = arrayListOf()
    private var timerTask: BukkitTask? = null
    var state: State = State.QUEUE
        private set
    private var startTime: Long = 0L
    private val playerSurvivalTime: HashMap<String, Long> = hashMapOf()

    private val validPatterns = arrayListOf(
        { StripePatternGenerator() },
        { LinePatternGenerator() },
        { BigPatternGenerator() },
        { RandomPatternGenerator() }
    )

    init {
        startQueue()
    }

    fun addPattern(pattern: () -> PatternGenerator) = validPatterns.add(pattern)

    private fun startQueue() {
        startTime = 0
        playerSurvivalTime.clear()
        level = lobbyLevel
        state = State.QUEUE
        lobbyLevel.generate()
        playersUUID.clear()
        Bukkit.getOnlinePlayers().forEach { addPlayer(it) }
    }

    fun start() {
        if (this.state != State.QUEUE) {
            return // We don't want to start the game if it's not being queued
        }

        this.state = State.ACTIVE
        this.startTime = System.currentTimeMillis()

        timerTask = BlockRunnable {
            val elapsedTime = System.currentTimeMillis() - startTime
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
        if (this.state != State.ACTIVE) {
            return // We don't want to end the game if it hasn't started in the first place.
        }

        this.state = State.ENDING

        Bukkit.getOnlinePlayers().forEach {
            eliminate(it)
        }

        Bukkit.broadcast(Component.text("Game has ended on round ${level.roundNumber}!", NamedTextColor.RED))
        playerSurvivalTime
            .keys
            .sortedBy { playerSurvivalTime[it] }
            .reversed()
            .forEach { p ->
                val time = Duration.ofMillis(playerSurvivalTime[p]!!).toSeconds()
                // I know this isn't ideal formatting... probably...? I really can't think of a better way.
                val min = if ((time / 60) >= 10) "${time / 60}" else "0${time / 60}"
                val sec = if ((time % 60) >= 10) "${time % 60}" else "0${time % 60}"

                Bukkit.broadcast(
                    Component.text("    - ", NamedTextColor.YELLOW)
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
                println("Ended the game!")
                startQueue()
            }
        }.runTaskLater(plugin, 60L)
    }

    private fun revealColor() {
        val block = level.chosenBlock
        val display = Component.translatable("block.minecraft." + block.name.lowercase(), block.toColor())
        val item = ItemStack(block)
        item.editMeta {
            it.displayName(display.decoration(TextDecoration.ITALIC, false))
        }

        players.forEach { it.inventory.setItem(4, item) }

        Bukkit.broadcast(
            Component.text("The block is", NamedTextColor.GRAY)
                .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                .append(display)
        )
    }

    private fun startRound() {
        // A failsafe for the end() check on the eliminate function, just so the game doesn't accidentally go on forever
        if (players.isEmpty()) {
            end()
            return
        }

        // Clearing the last round's tasks that have already been... wait.
        tasks.clear()
        for (player in players) {
            player.inventory.clear()
        }

        val pattern = validPatterns.random()()
        level = Level(location, pattern, level.roundNumber + 1)
        level.generate()
        Bukkit.broadcast(
            Component.text("Round ${level.roundNumber} ", NamedTextColor.YELLOW)
                .append(Component.text("(", NamedTextColor.DARK_GRAY))
                .append(Component.text("${String.format("%.2f", level.reactionTime)}s", NamedTextColor.RED))
                .append(Component.text(")", NamedTextColor.DARK_GRAY))
                .append(Component.text(" has started!", NamedTextColor.YELLOW))
        )

        val reactionTime: Long = (level.reactionTime * 20.0).toLong()
        val timeUntilReveal = 15L

        tasks.add(BlockRunnable {
            revealColor()
        }.runTaskLater(plugin, timeUntilReveal))

        tasks.add(BlockRunnable {
            level.reveal()
        }.runTaskLater(plugin, timeUntilReveal + reactionTime))

        // 2 seconds after reactionTime has ended, we start the next round
        tasks.add(BlockRunnable {
            startRound()
        }.runTaskLater(plugin, timeUntilReveal + reactionTime + 40L))
    }

    fun addPlayer(p: Player) {
        p.sendMessage(Component.text("You've been added to the Block Party!", NamedTextColor.GREEN))
        p.teleport(spawnLocation)
        p.gameMode = GameMode.ADVENTURE
        p.inventory.clear()
    }

    fun addSpectator(p: Player) {
        p.gameMode = GameMode.SPECTATOR
        Bukkit.getOnlinePlayers().filter { it != p }.forEach {
            p.showPlayer(plugin, it)
        }
    }


    fun eliminate(p: Player) {
        if (!playersUUID.remove(p.uniqueId)) return
        addSpectator(p)

        Bukkit.broadcast(
            Component.text(p.name, NamedTextColor.WHITE)
                .append(Component.text(" has been eliminated", NamedTextColor.RED))
        )
        playerSurvivalTime[p.name] = System.currentTimeMillis() - startTime

        if (players.isEmpty()) {
            end()
        }
    }
}