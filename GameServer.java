import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {

    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Map<String, PlayerState> playerStates = new ConcurrentHashMap<>();

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void start() {
        Thread acceptThread = new Thread(this::acceptLoop, "server-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void acceptLoop() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                handler.start();
            } catch (IOException e) {
                break;
            }
        }
    }

    void registerPlayer(String name) {
        playerStates.put(name, new PlayerState(name, 0, true));
        broadcastLeaderboard();
    }

    void updateScore(String name, int score) {
        PlayerState state = playerStates.get(name);
        if (state != null) {
            state.score = score;
            broadcastLeaderboard();
        }
    }

    void markDead(String name, int finalScore) {
        PlayerState state = playerStates.get(name);
        if (state != null) {
            state.score = finalScore;
            state.alive = false;
            broadcastLeaderboard();
            checkAllFinished();
        }
    }

    public void broadcastStart() {
        broadcastAll("START");
    }

    private void checkAllFinished() {
        if (!playerStates.isEmpty() && playerStates.values().stream().noneMatch(p -> p.alive)) {
            broadcastAll("FINAL:" + encodePlayers());
        }
    }

    private void broadcastLeaderboard() {
        broadcastAll("PLAYERS:" + encodePlayers());
    }

    private String encodePlayers() {
        List<PlayerState> sorted = new ArrayList<>(playerStates.values());
        sorted.sort((a, b) -> b.score - a.score);
        StringBuilder sb = new StringBuilder();
        for (PlayerState p : sorted) {
            if (sb.length() > 0) sb.append(",");
            sb.append(p.name).append(":").append(p.score).append(":").append(p.alive);
        }
        return sb.toString();
    }

    private void broadcastAll(String message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private final GameServer server;
        private PrintWriter out;
        private String playerName;

        ClientHandler(Socket socket, GameServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);
                String line;
                while ((line = in.readLine()) != null) {
                    handleLine(line);
                }
            } catch (IOException ignored) {
            } finally {
                close();
            }
        }

        private void handleLine(String line) {
            int idx = line.indexOf(':');
            String cmd = idx == -1 ? line : line.substring(0, idx);
            String arg = idx == -1 ? "" : line.substring(idx + 1);
            switch (cmd) {
                case "JOIN":
                    playerName = arg;
                    server.registerPlayer(playerName);
                    break;
                case "SCORE":
                    server.updateScore(playerName, Integer.parseInt(arg));
                    break;
                case "DEAD":
                    server.markDead(playerName, Integer.parseInt(arg));
                    break;
                default:
                    break;
            }
        }

        void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        void close() {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
