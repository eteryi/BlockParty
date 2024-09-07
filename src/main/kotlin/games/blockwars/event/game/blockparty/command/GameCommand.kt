package games.blockwars.event.game.blockparty.command

import games.blockwars.event.game.blockparty.game.BlockPartyGame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender
import org.incendo.cloud.CommandManager
import org.incendo.cloud.permission.PredicatePermission

object GameCommand {
    fun init(game: BlockPartyGame, m : CommandManager<CommandSender>) {
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("start")
                .permission(PredicatePermission.of {
                    it.isOp
                })
                .handler { ctx ->
                    val sender = ctx.sender()
                    sender.sendMessage(Component.text("Starting the Game...", NamedTextColor.GREEN))
                    game.start()
                }
        )
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("stop")
                .permission(PredicatePermission.of {
                    it.isOp
                })
                .handler { ctx ->
                    val sender = ctx.sender()
                    sender.sendMessage(Component.text("Stopping the game!", NamedTextColor.RED))
                    game.end()
                }
        )

        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("players")
                .permission(PredicatePermission.of {
                    it.isOp
                })
                .handler { ctx ->
                    val sender = ctx.sender()

                    game.players.forEach {
                        sender.sendMessage(it.name)
                    }
                }
        )
    }
}