public class PlayerState {
    public final String name;
    public int score;
    public boolean alive;

    public PlayerState(String name, int score, boolean alive) {
        this.name = name;
        this.score = score;
        this.alive = alive;
    }
}
