package games.blockwars.event.game.blockparty.command

import games.blockwars.event.game.blockparty.game.Game
import games.blockwars.event.game.blockparty.small
import org.bukkit.command.CommandSender
import org.incendo.cloud.CommandManager

object GameCommand {
    fun init(game: Game, m : CommandManager<CommandSender>) {
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("start")
                .handler {
                    it.sender().sendMessage("BLOCK WARS".small())
                    game.start()
                }
        )
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("stop")
                .handler {
                    it.sender().sendMessage("Stopping the game!")
                    game.end()
                }
        )

        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("players")
                .handler { player ->
                    game.players.forEach {
                        player.sender().sendMessage(it.name)
                    }
                }
        )
    }
}