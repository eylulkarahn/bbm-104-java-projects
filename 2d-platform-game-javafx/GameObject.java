
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import java.io.File;

public abstract class GameObject extends StackPane {
    protected final ImageView view;
    protected final double width, height;
    private int frameCounter = 0, currentFrame = 0;

    public GameObject(String path, double w, double h) {
        this.width = w;
        this.height = h;
        this.view = new ImageView(new Image(new File(path).toURI().toString()));
        this.view.setViewport(new Rectangle2D(0, 0, w, h));
        getChildren().add(view);
    }

    protected void animate(int totalFrames, int speed) {
        if (++frameCounter >= speed) {
            currentFrame = (currentFrame + 1) % totalFrames;
            view.setViewport(new Rectangle2D(currentFrame * width, 0, width, height));
            frameCounter = 0;
        }
    }

    public void update() {
    } // Default empty implementation to be overridden by subclasses

    public double getObjectWidth() {
        return width;
    }

    public double getObjectHeight() {
        return height;
    }
}
