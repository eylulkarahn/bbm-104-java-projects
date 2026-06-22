import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class WriteOutput {

    public static void write(File output, ArrayList<PathElement> bestPath, Player player, boolean anyPathReachedExit) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(output))) {
            if (bestPath.isEmpty()) {
                writer.println("RESULT: FAILURE");
                writer.println("MAX_HEALTH: " + player.getMaxHealth());
                if (!anyPathReachedExit) {
                    // if no path reached exit
                    writer.print("REASON: Player died before reaching the exit.");
                } else if (!player.getHasKey()) {
                    // if the player reached exit but has no key
                    writer.print("REASON: No key found.");
                }
            } else {
                // success case
                writer.println("RESULT: SUCCESS");
                writer.println("MAX_HEALTH: " + player.getMaxHealth());
                writer.println("REMAINING_HEALTH: " + bestPath.get(bestPath.size() - 1).health);
                writer.println("STEPS: " + (bestPath.size() - 1));
                writer.println("PATH:");
                for (int i = 0; i < bestPath.size(); i++) {
                    PathElement p = bestPath.get(i);
                    writer.print("(" + p.x + "," + p.y + "," + p.health + ")");
                    if (i < bestPath.size() - 1) {
                        writer.print(" -> ");
                    }
                }
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }
}
