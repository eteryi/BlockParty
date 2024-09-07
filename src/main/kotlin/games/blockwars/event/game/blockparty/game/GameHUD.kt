package games.blockwars.event.game.blockparty.game

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class GameHUD(private val g : BlockPartyGame) {
    private val bossBar : BossBar = BossBar.bossBar(
        Component.text("Block Party"),
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

        val reactionTime : Double = g.level.reactionTime

        bossBar.name(
            Component.text("Round: ${g.level.roundNumber}       $minutes:$seconds        Reaction Time: ${String.format("%.2f", reactionTime)}")
        )
    }

    fun default() {
        bossBar.name(Component.text("Block Party!"))
    }

}