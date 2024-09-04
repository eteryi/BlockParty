package games.blockwars.event.game.blockparty.game

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GameHUD(private val g : Game) {
    private val bossBar : BossBar = BossBar.bossBar(
        Component.text("Default Bossbar!"),
        1.0f,
        BossBar.Color.WHITE,
        BossBar.Overlay.PROGRESS
    )

    fun show(p : Player) {
        p.showBossBar(bossBar)
    }

    fun update(timeElapsedSeconds : Int) {
        val remainingSeconds = g.gameDuration.toSeconds() - timeElapsedSeconds
        val minutes = if ((remainingSeconds / 60) >= 10) "${remainingSeconds / 60}" else "0${remainingSeconds / 60}"
        val seconds = if ((remainingSeconds % 60) >= 10) "${remainingSeconds % 60}" else "0${remainingSeconds % 60}"

        val reactionTime : Double = (10.0) - (g.roundNumber.toDouble() / 2.0)

        bossBar.name(
            Component.text("Round: ${g.roundNumber} $minutes:$seconds Reaction Time: $reactionTime")
        )
    }

    fun default() {
        bossBar.name(Component.text("Default Bossbar!"))
    }

}