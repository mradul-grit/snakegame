import java.awt.BorderLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class LobbyFrame extends JFrame {

    private static final int DEFAULT_PORT = 5555;

    private GameServer server;
    private GameClient client;
    private String playerName;
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public LobbyFrame() {
        setTitle("Snake - Multiplayer Lobby");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 360);
        setLocationRelativeTo(null);
        setVisible(true);
        chooseMode();
    }

    private void chooseMode() {
        String enteredName = JOptionPane.showInputDialog(this, "Enter your name:", "Player");
        playerName = (enteredName == null || enteredName.isBlank()) ? "Player" : enteredName.trim();

        Object[] options = {"Host Game", "Join Game"};
        int choice = JOptionPane.showOptionDialog(this, "Host a new room or join one?",
                "Multiplayer", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            hostGame();
        } else {
            joinGame();
        }
    }

    private void hostGame() {
        try {
            server = new GameServer(DEFAULT_PORT);
            server.start();
            client = new GameClient("127.0.0.1", server.getPort(), playerName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not start room: " + e.getMessage());
            dispose();
            return;
        }

        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (IOException e) {
            ip = "127.0.0.1";
        }

        buildLobbyUI("Hosting room at " + ip + ":" + server.getPort(), true);
    }

    private void joinGame() {
        String ip = JOptionPane.showInputDialog(this, "Host IP address:", "127.0.0.1");
        if (ip == null) {
            dispose();
            return;
        }
        String portStr = JOptionPane.showInputDialog(this, "Port:", String.valueOf(DEFAULT_PORT));
        if (portStr == null) {
            dispose();
            return;
        }

        try {
            int port = Integer.parseInt(portStr.trim());
            client = new GameClient(ip.trim(), port, playerName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not join room: " + e.getMessage());
            dispose();
            return;
        }

        buildLobbyUI("Joined room at " + ip.trim() + ":" + portStr.trim(), false);
    }

    private void buildLobbyUI(String info, boolean isHost) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(new JLabel(info, SwingConstants.CENTER), BorderLayout.NORTH);

        JList<String> list = new JList<>(listModel);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JButton startButton = new JButton(isHost ? "Start Game" : "Waiting for host to start...");
        startButton.setEnabled(isHost);
        startButton.addActionListener(e -> server.broadcastStart());
        add(startButton, BorderLayout.SOUTH);

        client.setListener(new GameClient.NetworkListener() {
            @Override
            public void onPlayersUpdate(List<PlayerState> players) {
                listModel.clear();
                for (PlayerState p : players) {
                    listModel.addElement(p.name);
                }
            }

            @Override
            public void onStart() {
                dispose();
                new GameFrame(client, playerName);
            }

            @Override
            public void onFinal(List<PlayerState> players) {
            }
        });

        revalidate();
        repaint();
    }
}
