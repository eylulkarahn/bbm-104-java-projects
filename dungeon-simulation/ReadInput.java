import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadInput {
    private int rows;
    private int cols;
    private int health;
    private int x;
    private int y;
    private Room[][] roomGrid;

    public ReadInput(File input) {
        // try-catch block to handle file not found exception
        try {
            Scanner scanner = new Scanner(input);

            if (scanner.hasNextInt()) {
                this.rows = scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                this.cols = scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                this.health = scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                this.x = scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                this.y = scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                scanner.nextInt();
            }
            if (scanner.hasNextInt()) {
                scanner.nextInt();
            }

            // this loop creates the room grid
            this.roomGrid = new Room[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (scanner.hasNext()) {
                        String type = scanner.next();
                        roomGrid[i][j] = Room.create(i, j, type);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }
    }

    // getters
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMaxHealth() {
        return health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Room[][] getRoomGrid() {
        return roomGrid;
    }
}
