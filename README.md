<div align="center">

# BlockParty
A simple version of the popular minigame Block Party, also known as Pixel Party on Hypixel, a movement game made to test your reaction time, with randomly generated floor patterns! Made specifically for my interview process at Block Wars.

---

### Compilation

BlockParty is built using Gradle. To build it by yourself you must have it installed.
Compile it by running `./gradlew build` in the root project folder, it will create the shadowJar (BlockParty-X.X-all.jar) which contains all dependencies. 



### Usage
When joining the server, you'll be automatically teleported to the middle of the 25x25 square on which the game will be played on.
**To start a match, you must use the `/game start` command**.
You may **force-stop a match** by using the `/game stop` command and check the remaining active players with `/game players` (Note that these commands are only available to **server operators**)



### Configuration
As of right now, you can configure how long a match will last (in seconds) using the `round_duration` configuration, and you can also change where the game will take place using the, commented, `game_location` configuration.

Please do note that if you decide to use a custom map with this plugin, the coordinate you input must be the vertice with the **lowest x and z**. If not, you risk getting your map consumed by the generated 25x25 square that will appear. And also beware that the plugin will not load the world for you, like it does with its own generated void world. 



### Making new Patterns

For now, the process of making new patterns to appear in the Block Party arena is not fully where I want it to be at, due to a lack of time, so if you wish to add new randomly generated patterns to this plugin, please consider forking this project and checking out the generator package, the examples provided there are more than enough to get started making your own Pattern Generators!

As there's currently no good system of adding those, consider adding them to the `validPatterns` on the `BlockPartyGame.kt` file or adding it on the entrypoint by using `game.addPattern { YourNewPattern() }`.


