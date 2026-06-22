
public class Player extends GameObject {
    public Player(String path, double w, double h) {
        super(path, w, h);
    }

    @Override
    public void update() {
        animate(11, 6);
    }
}
