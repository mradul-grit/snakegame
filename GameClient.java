import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class GameClient {

    public interface NetworkListener {
        void onPlayersUpdate(List<PlayerState> players);
        void onStart();
        void onFinal(List<PlayerState> players);
    }

    private final Socket socket;
    private final PrintWriter out;
    private final String playerName;
    private volatile NetworkListener listener;

    public GameClient(String host, int port, String playerName) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.playerName = playerName;

        Thread listenThread = new Thread(this::listenLoop, "client-listen");
        listenThread.setDaemon(true);
        listenThread.start();

        send("JOIN:" + playerName);
    }

    public void setListener(NetworkListener listener) {
        this.listener = listener;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void sendScore(int score) {
        send("SCORE:" + score);
    }

    public void sendDead(int finalScore) {
        send("DEAD:" + finalScore);
    }

    private void send(String message) {
        out.println(message);
    }

    private void listenLoop() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                handleLine(line);
            }
        } catch (IOException ignored) {
        }
    }

    private void handleLine(String line) {
        NetworkListener currentListener = listener;
        if (currentListener == null) {
            return;
        }
        if (line.startsWith("PLAYERS:")) {
            List<PlayerState> players = decodePlayers(line.substring("PLAYERS:".length()));
            SwingUtilities.invokeLater(() -> currentListener.onPlayersUpdate(players));
        } else if (line.equals("START")) {
            SwingUtilities.invokeLater(currentListener::onStart);
        } else if (line.startsWith("FINAL:")) {
            List<PlayerState> players = decodePlayers(line.substring("FINAL:".length()));
            SwingUtilities.invokeLater(() -> currentListener.onFinal(players));
        }
    }

    private List<PlayerState> decodePlayers(String data) {
        List<PlayerState> players = new ArrayList<>();
        if (data.isEmpty()) {
            return players;
        }
        for (String part : data.split(",")) {
            String[] fields = part.split(":");
            players.add(new PlayerState(fields[0], Integer.parseInt(fields[1]), Boolean.parseBoolean(fields[2])));
        }
        return players;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
