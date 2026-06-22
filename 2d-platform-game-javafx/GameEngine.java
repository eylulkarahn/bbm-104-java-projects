import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GameEngine extends Application {
    private final Pane gameRoot = new Pane();
    private final StackPane mainRoot = new StackPane();
    private static final int GAME_WIDTH = 960, GAME_HEIGHT = 480;
    private static final int VIEWPORT_WIDTH = 480, VIEWPORT_HEIGHT = 480;
    private Label scoreLabel;
    private Label spikesLabel;
    private Label applesLabel;

    private Player player;
    private final List<Terrain> terrains = new ArrayList<>();
    private final List<Block> blocks = new ArrayList<>();
    private final List<Apple> apples = new ArrayList<>();
    private final List<Spike> spikes = new ArrayList<>();

    private int score = 0;
    private int appleCount = 0;
    private int spikeCount = 0;
    private int collectedApplesCount = 0;
    private double velocity = 0.0;
    private boolean isOnGround = false;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean spacePressed = false;

    private long lastSpawnTime = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mainRoot.getChildren().add(gameRoot);
        gameRoot.setMinSize(GAME_WIDTH, GAME_HEIGHT);
        gameRoot.setMaxSize(GAME_WIDTH, GAME_HEIGHT);
        gameRoot.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        StackPane.setAlignment(gameRoot, Pos.TOP_LEFT);

        reset();

        gameRoot.setBackground(new Background(new BackgroundImage(
                new Image(new File("assets/Background(960x480x1).png").toURI().toString()), null, null, null, null)));

        Scene scene = new Scene(mainRoot, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT)
                leftPressed = true;
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT)
                rightPressed = true;
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP)
                jumpPressed = true;
            if (e.getCode() == KeyCode.SPACE)
                spacePressed = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT)
                leftPressed = false;
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT)
                rightPressed = false;
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP)
                jumpPressed = false;
            if (e.getCode() == KeyCode.SPACE)
                spacePressed = false;
        });

        stage.setTitle("BBM104 Assignment 3 Platform Game");
        stage.setScene(scene);
        stage.show();
        gameRoot.requestFocus();

        new GameLoop().start();
    }

    private void reset() {
        // Resets the game world state and reinitializes UI labels.
        gameRoot.getChildren().clear();
        gameRoot.setTranslateX(0);
        gameRoot.setTranslateY(0);

        if (scoreLabel == null) {
            scoreLabel = new Label("Score: 0");
            scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(scoreLabel, Pos.BOTTOM_LEFT);
            StackPane.setMargin(scoreLabel, new Insets(0, 0, 5, 10));
            mainRoot.getChildren().add(scoreLabel);

            spikesLabel = new Label("Spikes: 0");
            spikesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(spikesLabel, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(spikesLabel, new Insets(0, 10, 5, 0));
            mainRoot.getChildren().add(spikesLabel);

            applesLabel = new Label("Apples: 0");
            applesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(applesLabel, Pos.BOTTOM_CENTER);
            StackPane.setMargin(applesLabel, new Insets(0, 0, 5, 0));
            mainRoot.getChildren().add(applesLabel);
        } else {
            scoreLabel.setText("Score: 0");
            spikesLabel.setText("Spikes: 0");
            applesLabel.setText("Apples: 0");
        }

        scoreLabel.toFront();
        spikesLabel.toFront();
        applesLabel.toFront();

        terrains.clear();
        blocks.clear();
        apples.clear();
        spikes.clear();
        player = null;

        score = 0;
        appleCount = 0;
        spikeCount = 0;
        collectedApplesCount = 0;
        velocity = 0.0;
        isOnGround = false;
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        spacePressed = false;

        try (BufferedReader br = new BufferedReader(new FileReader("level.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] p = line.split(",");
                if (p.length < 3)
                    continue;
                String type = p[0];
                GameObject obj = null;

                if (type.equals("Terrain")) {
                    obj = new Terrain("assets/Terrain(48x48x1).png", 48.0, 48.0);
                    obj.setLayoutX(Double.parseDouble(p[1]));
                    obj.setLayoutY(Double.parseDouble(p[2]));
                    terrains.add((Terrain) obj);
                } else if (type.equals("Block")) {
                    obj = new Block("assets/Block(20x20x1).png", 20.0, 20.0);
                    obj.setLayoutX(Double.parseDouble(p[1]));
                    obj.setLayoutY(Double.parseDouble(p[2]));
                    blocks.add((Block) obj);
                } else if (type.equals("Player")) {
                    player = new Player("assets/Player(22x26x11).png", 22.0, 26.0);
                    player.setLayoutX(Double.parseDouble(p[1]));
                    player.setLayoutY(Double.parseDouble(p[2]));
                    obj = player;
                }
                if (obj != null) {
                    gameRoot.getChildren().add(obj);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        spawn();
        lastSpawnTime = System.currentTimeMillis();
    }

    private boolean isInvalid(GameObject obj) {
        if (obj.getLayoutX() < 0 ||
                obj.getLayoutX() + obj.getObjectWidth() > GAME_WIDTH ||
                obj.getLayoutY() < 0 ||
                obj.getLayoutY() + obj.getObjectHeight() > GAME_HEIGHT) {
            return true;
        }

        javafx.geometry.Bounds bounds = obj.getBoundsInParent();
        javafx.geometry.Bounds adjusted = new javafx.geometry.BoundingBox(
                bounds.getMinX() + 0.1,
                bounds.getMinY() + 0.1,
                bounds.getWidth() - 0.2,
                bounds.getHeight() - 0.2);

        for (Terrain t : terrains) {
            if (t != obj && adjusted.intersects(t.getBoundsInParent())) {
                return true;
            }
        }
        for (Block b : blocks) {
            if (b != obj && adjusted.intersects(b.getBoundsInParent())) {
                return true;
            }
        }

        return false;
    }

    private void spawn() {
        List<GameObject> platforms = new ArrayList<>(); // spikes can appear on platforms
        platforms.addAll(terrains);
        platforms.addAll(blocks);

        Random rand = new Random();

        if (!platforms.isEmpty()) {
            boolean spikeSpawned = false;
            int retries = 0;
            while (!spikeSpawned && retries < 100) {
                retries++;
                GameObject platform = platforms.get(rand.nextInt(platforms.size()));
                double pX = platform.getLayoutX();
                double pY = platform.getLayoutY();
                double pW = platform.getObjectWidth();

                double minX = pX;
                double maxX = pX + pW - 14.0;

                if (maxX >= minX) {
                    double x = minX + rand.nextDouble() * (maxX - minX);
                    double y = pY - 7.0;

                    Spike spike = new Spike("assets/Spike(14x7x1).png", 14.0, 7.0);
                    spike.setLayoutX(x);
                    spike.setLayoutY(y);

                    if (!isInvalid(spike)) {
                        spikes.add(spike);
                        gameRoot.getChildren().add(spike);
                        spikeCount++;
                        spikesLabel.setText("Spikes: " + spikeCount);
                        spikeSpawned = true;
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            boolean appleSpawned = false;
            int retries = 0;
            while (!appleSpawned && retries < 100) {
                retries++;
                double x = rand.nextDouble() * (GAME_WIDTH - 16.0);
                double y = rand.nextDouble() * (GAME_HEIGHT - 18.0);

                Apple apple = new Apple("assets/Apple(16x18x17).png", 16.0, 18.0);
                apple.setLayoutX(x);
                apple.setLayoutY(y);

                if (!isInvalid(apple)) {
                    apples.add(apple);
                    gameRoot.getChildren().add(apple);
                    appleCount++;
                    applesLabel.setText("Apples: " + appleCount);
                    appleSpawned = true;
                }
            }
        }
    }

    private void update() {

        // Updates the state of all active game entities.
        for (javafx.scene.Node node : gameRoot.getChildren()) {
            if (node instanceof GameObject) {
                ((GameObject) node).update();
            }
        }

        double originalX = player.getLayoutX();
        if (leftPressed && !rightPressed) {
            player.setLayoutX(originalX - 1);
            player.setScaleX(-1);
            if (isInvalid(player)) {
                player.setLayoutX(originalX);
            }
        } else if (rightPressed && !leftPressed) {
            player.setLayoutX(originalX + 1);
            player.setScaleX(1);
            if (isInvalid(player)) {
                player.setLayoutX(originalX);
            }
        }

        if (player != null) {
            double originalY = player.getLayoutY();
            player.setLayoutY(originalY + 1.0);
            isOnGround = isInvalid(player);
            player.setLayoutY(originalY);
        } else {
            isOnGround = false;
        }

        if (isOnGround) {
            velocity = 0;
            if (jumpPressed) {
                velocity = -3.4;
                isOnGround = false;
            }
        } else {
            velocity += 0.1;
        }

        if (velocity != 0.0) {
            double originalY = player.getLayoutY();
            player.setLayoutY(originalY + velocity);

            if (isInvalid(player)) {
                player.setLayoutY(originalY);
                if (velocity > 0) {
                    double step = 0.1;
                    while (!isInvalid(player)) {
                        player.setLayoutY(player.getLayoutY() + step);
                    }
                    player.setLayoutY(player.getLayoutY() - step);
                    velocity = 0.0;
                    isOnGround = true;
                } else {
                    double step = 0.1;
                    while (!isInvalid(player)) {
                        player.setLayoutY(player.getLayoutY() - step);
                    }
                    player.setLayoutY(player.getLayoutY() + step);
                    velocity = 0.0;
                }
            }
        }

        boolean hitSpike = false;
        for (Spike s : spikes) {
            if (player.getBoundsInParent().intersects(s.getBoundsInParent())) {
                hitSpike = true;
                break;
            }
        }
        if (hitSpike) {
            reset();
            return;
        }

        List<Apple> collectedApples = new ArrayList<>();
        for (Apple a : apples) {
            if (player.getBoundsInParent().intersects(a.getBoundsInParent())) {
                collectedApples.add(a);
            }
        }

        for (Apple a : collectedApples) {
            apples.remove(a);
            gameRoot.getChildren().remove(a);
            appleCount--;
            applesLabel.setText("Apples: " + appleCount);

            collectedApplesCount++;
            if (collectedApplesCount == 2) {
                collectedApplesCount = 0;
                score++;
                scoreLabel.setText("Score: " + score);

                // Remove a random spike from game world
                if (!spikes.isEmpty()) {
                    int index = new Random().nextInt(spikes.size());
                    Spike removedSpike = spikes.remove(index);
                    gameRoot.getChildren().remove(removedSpike);
                    spikeCount--;
                    spikesLabel.setText("Spikes: " + spikeCount);
                }
            }
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= 3000) {
            spawn();
            lastSpawnTime = currentTime;
        }

        double targetTranslateX = 240.0 - (player.getLayoutX() + player.getObjectWidth() / 2.0);
        targetTranslateX = Math.max(-480.0, Math.min(0.0, targetTranslateX));
        gameRoot.setTranslateX(targetTranslateX);

        if (spacePressed)

        {
            spacePressed = false;
            if (score >= 5 && !spikes.isEmpty()) {
                Spike nearestSpike = null;
                double minDist = Double.MAX_VALUE;
                double pCenterX = player.getLayoutX() + player.getObjectWidth() / 2.0;
                double pCenterY = player.getLayoutY() + player.getObjectHeight() / 2.0;

                for (Spike s : spikes) {
                    double sCenterX = s.getLayoutX() + s.getObjectWidth() / 2.0;
                    double sCenterY = s.getLayoutY() + s.getObjectHeight() / 2.0;
                    double dist = Math.sqrt(Math.pow(pCenterX - sCenterX, 2) + Math.pow(pCenterY - sCenterY, 2));
                    if (dist < minDist) {
                        minDist = dist;
                        nearestSpike = s;
                    }
                }

                if (nearestSpike != null) {
                    spikes.remove(nearestSpike);
                    gameRoot.getChildren().remove(nearestSpike);
                    spikeCount--;
                    score -= 5;
                    spikesLabel.setText("Spikes: " + spikeCount);
                    scoreLabel.setText("Score: " + score);
                }
            }
        }

    }

    private class GameLoop extends AnimationTimer {
        @Override
        public void handle(long now) {
            update();
        }
    }
}
