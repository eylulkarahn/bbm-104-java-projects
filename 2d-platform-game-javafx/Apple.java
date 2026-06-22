
public class Apple extends GameObject {
    public Apple(String path, double w, double h) {
        super(path, w, h);
    }

    @Override
    public void update() {
        animate(17, 4);
    }
}
