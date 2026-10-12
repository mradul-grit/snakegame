# Snake Game

A classic Snake game built in Java with Swing, featuring both single-player and LAN multiplayer modes with a live leaderboard.

## Features

- Classic single-player Snake gameplay
- LAN multiplayer: host or join a room with friends on the same network
- Each player plays on their own board; scores update live for everyone in a side leaderboard
- A final leaderboard is shown once every player's game has ended

## Requirements

- [Java](https://adoptium.net/) 8 or later installed on your machine (running `java -version` in a terminal should print a version number)

## Download and play

1. Download `snakegame.jar` from this repository (click on the file, then click **Download** / **Raw**).
2. Open a terminal in the folder where you downloaded it.
3. Run:
   ```
   java -jar snakegame.jar
   ```
4. Choose **Single Player** or **Multiplayer** in the dialog that appears.

### Single player

- Use the **arrow keys** to move the snake and eat the red apples.
- Press **Space** to restart after a game over.

### Multiplayer (LAN)

1. One player clicks **Host Game**, enters a name, and shares the shown IP address and port with the others (everyone must be on the same local network / Wi-Fi).
2. Other players click **Join Game** and enter the host's IP address and port.
3. Once everyone has joined, the host clicks **Start Game**.
4. Everyone plays their own board at the same time. Scores update live in the leaderboard panel.
5. When all players have finished, a final leaderboard is shown, ranked by score.

## Building from source

If you'd rather build it yourself instead of using the provided jar:

```
javac *.java
jar cfm snakegame.jar manifest.txt *.class
java -jar snakegame.jar
```

## Project structure

| File | Purpose |
|---|---|
| `SnakeGame.java` | Entry point; lets you pick single-player or multiplayer |
| `GamePanel.java` | Core game loop, rendering, and snake movement |
| `GameFrame.java` | Game window, including the leaderboard panel in multiplayer |
| `LobbyFrame.java` | Lobby UI for hosting or joining a multiplayer room |
| `GameServer.java` | Hosts a multiplayer room and tracks all players' scores |
| `GameClient.java` | Connects to a multiplayer room and syncs score/leaderboard updates |
| `LeaderboardPanel.java` | Renders the live leaderboard during a multiplayer game |
| `PlayerState.java` | Simple data holder for a player's name, score, and alive status |

## Author

Mradul Tripathi
