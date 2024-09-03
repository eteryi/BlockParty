package games.blockwars.event.game.blockparty.command

import games.blockwars.event.game.blockparty.Game
import games.blockwars.event.game.blockparty.generator.LineGenerator
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.CommandManager

class GameCommand() {

    fun init(m : CommandManager<CommandSender>) {
        m.command(
            m.commandBuilder("game", "blockparty")
                .literal("start")
                .handler {
                    it.sender().sendMessage("Sender is sending")
                    it.sender().sendMessage("Hello World!")
                    Game(LineGenerator()).create((it.sender() as Player).location)
                }
        )
    }
}