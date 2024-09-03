package games.blockwars.event.game.blockparty.command

import games.blockwars.event.game.blockparty.Game
import org.bukkit.command.CommandSender
import org.incendo.cloud.CommandManager

class GameCommand(private val game : Game) {
    fun init(m : CommandManager<CommandSender>) {
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("start")
                .handler {
                    it.sender().sendMessage("Starting the streeeam!")
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
                    game.players().forEach {
                        player.sender().sendMessage(it.name)
                    }
                }
        )
    }
}