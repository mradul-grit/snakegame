import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class LeaderboardPanel extends JPanel {

    private List<PlayerState> players = new ArrayList<>();

    public LeaderboardPanel() {
        setPreferredSize(new Dimension(220, GamePanel.SCREEN_HEIGHT));
        setBackground(Color.darkGray);
    }

    public void update(List<PlayerState> players) {
        this.players = players;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Leaderboard", 15, 30);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        List<PlayerState> sorted = new ArrayList<>(players);
        sorted.sort((a, b) -> b.score - a.score);

        int y = 65;
        for (PlayerState p : sorted) {
            g.setColor(p.alive ? Color.white : Color.gray);
            String label = p.name + ": " + p.score + (p.alive ? "" : " (out)");
            g.drawString(label, 15, y);
            y += 28;
        }
    }
}
