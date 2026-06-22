import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
    private BufferedWriter writer;

    public OutputWriter(String filename) {
        try {
            this.writer = new BufferedWriter(new FileWriter(filename));
        } catch (IOException e) {
            System.err.println("Error opening file: " + e.getMessage());
        }
    }

    public void writeError(String message) {
        try {
            if (writer != null) {
                writer.write("ERROR: " + message + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void writeSuccess(String message) {
        try {
            if (writer != null) {
                writer.write("SUCCESS: " + message + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void writeCommand(String message) {
        try {
            if (writer != null) {
                writer.write("COMMAND: " + message + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void write(String message) {
        try {
            if (writer != null) {
                writer.write(message + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing file: " + e.getMessage());
        }
    }
}
