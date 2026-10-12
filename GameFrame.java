import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameFrame extends JFrame {

    GameFrame() {
        this(null, null);
    }

    GameFrame(GameClient client, String playerName) {
        GamePanel panel = new GamePanel(client);
        this.setTitle(playerName == null ? "Snake" : "Snake - " + playerName);
        this.setResizable(false);

        if (client != null) {
            LeaderboardPanel leaderboard = new LeaderboardPanel();
            this.setLayout(new BorderLayout());
            this.add(panel, BorderLayout.CENTER);
            this.add(leaderboard, BorderLayout.EAST);

            client.setListener(new GameClient.NetworkListener() {
                @Override
                public void onPlayersUpdate(List<PlayerState> players) {
                    leaderboard.update(players);
                }

                @Override
                public void onStart() {
                }

                @Override
                public void onFinal(List<PlayerState> players) {
                    showFinalLeaderboard(players);
                }
            });
        } else {
            this.add(panel);
        }

        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    private void showFinalLeaderboard(List<PlayerState> players) {
        List<PlayerState> sorted = new ArrayList<>(players);
        sorted.sort((a, b) -> b.score - a.score);

        StringBuilder sb = new StringBuilder("Final Leaderboard:\n\n");
        int rank = 1;
        for (PlayerState p : sorted) {
            sb.append(rank++).append(". ").append(p.name).append(" - ").append(p.score).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Game Over - Results", JOptionPane.INFORMATION_MESSAGE);
    }
}
