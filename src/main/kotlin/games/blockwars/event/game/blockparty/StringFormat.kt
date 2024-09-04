package games.blockwars.event.game.blockparty

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

private val small = hashMapOf(
    'a' to "ᴀ",
    'b' to "ʙ",
    'c' to "ᴄ",
    'd' to "ᴅ",
    'e' to "ᴇ",
    'f' to "ꜰ",
    'g' to "ɢ",
    'h' to "ʜ",
    'i' to "ɪ",
    'j' to "ᴊ",
    'k' to "ᴋ",
    'l' to "ʟ",
    'm' to "ᴍ",
    'n' to "ɴ",
    'o' to "ᴏ",
    'p' to "ᴘ",
    'q' to "ꞯ",
    'r' to "ʀ",
    's' to "ꜱ",
    't' to "ᴛ",
    'u' to "ᴜ",
    'v' to "ᴠ",
    'w' to "ᴡ",
    'x' to "x",
    'y' to "ʏ",
    'z' to "ᴢ"
)

private val color = hashMapOf(
    Material.YELLOW_CONCRETE to NamedTextColor.YELLOW,
    Material.ORANGE_CONCRETE to NamedTextColor.GOLD,
    Material.RED_CONCRETE to NamedTextColor.RED,
    Material.CYAN_CONCRETE to NamedTextColor.DARK_GRAY,
    Material.LIGHT_BLUE_CONCRETE to NamedTextColor.AQUA,
    Material.BLUE_CONCRETE to NamedTextColor.BLUE,
    Material.GRAY_CONCRETE to NamedTextColor.DARK_GRAY,
    Material.LIGHT_GRAY_CONCRETE to NamedTextColor.GRAY,
    Material.GREEN_CONCRETE to NamedTextColor.DARK_GREEN,
    Material.LIME_CONCRETE to NamedTextColor.GREEN,
    Material.BROWN_CONCRETE to TextColor.fromCSSHexString("#964B00"),
    Material.MAGENTA_CONCRETE to NamedTextColor.LIGHT_PURPLE,
    Material.PURPLE_CONCRETE to NamedTextColor.DARK_PURPLE,
    Material.PINK_CONCRETE to TextColor.fromCSSHexString("#ff82ec"),
    Material.BLACK_CONCRETE to NamedTextColor.BLACK
)

fun String.small() : String {
    return this.lowercase()
        .map { return@map small[it] ?: it }
        .joinToString(prefix = "", postfix = "", separator = "")
}

fun Material.toColor() : TextColor {
    return color[this] ?: NamedTextColor.WHITE
}