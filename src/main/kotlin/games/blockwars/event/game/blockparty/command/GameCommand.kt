package games.blockwars.event.game.blockparty.command

import games.blockwars.event.game.blockparty.game.BlockPartyGame
import games.blockwars.event.game.blockparty.small
import org.bukkit.command.CommandSender
import org.incendo.cloud.CommandManager

object GameCommand {
    fun init(game: BlockPartyGame, m : CommandManager<CommandSender>) {
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("start")
                .handler {
                    if (!it.sender().isOp) return@handler
                    it.sender().sendMessage("BLOCK WARS".small())
                    game.start()
                }
        )
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("stop")
                .handler {
                    if (!it.sender().isOp) return@handler
                    it.sender().sendMessage("Stopping the game!")
                    game.end()
                }
        )

        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("players")
                .handler { player ->
                    if (!player.sender().isOp) return@handler
                    game.players.forEach {
                        player.sender().sendMessage(it.name)
                    }
                }
        )
    }
}