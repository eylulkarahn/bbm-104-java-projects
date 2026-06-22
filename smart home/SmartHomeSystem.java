import java.util.Locale;

public class SmartHomeSystem {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        InputProcessor inputProcessor = new InputProcessor(args[0]);
        OutputWriter outputWriter = new OutputWriter(args[1]);
        inputProcessor.processCommands(outputWriter);

        outputWriter.close();
    }

}
