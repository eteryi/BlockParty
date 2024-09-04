package games.blockwars.event.game.blockparty

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

fun String.small() : String {
    return this.lowercase()
        .map { return@map small[it] ?: it }
        .joinToString(prefix = "", postfix = "", separator = "")
}