
import java.io.File;

/*if there is a problem with heal can you check heal method 
in Player.java i assumed if heal exceeds maxHealth no heal happens
*/
public class Main {

    public static void main(String[] args) {
        File input = new File(args[0]);
        File output = new File(args[1]);

        // reading input
        ReadInput reader = new ReadInput(input);

        // Starting coordinates
        int startX = reader.getX();
        int startY = reader.getY();

        // Initializing player
        Player player = new Player(reader.getMaxHealth(), reader.getMaxHealth(), false, 0, startX, startY, false,
                false);
        Room[][] roomGrid = reader.getRoomGrid();

        // Apply starting room effect
        roomGrid[startX][startY].applyEffect(player);

        // Solve dungeon using FindPath class
        FindPath solver = new FindPath(roomGrid, reader.getRows(), reader.getCols());
        solver.solve(player, startX, startY);

        // Writing output
        WriteOutput.write(output, solver.getBestPath(), player, solver.isAnyPathReachedExit());
    }
}
