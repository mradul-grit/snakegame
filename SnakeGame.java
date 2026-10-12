import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Single Player", "Multiplayer"};
            int choice = JOptionPane.showOptionDialog(null, "Choose a mode:", "Snake",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if (choice == 1) {
                new LobbyFrame();
            } else {
                new GameFrame();
            }
        });
    }
}
