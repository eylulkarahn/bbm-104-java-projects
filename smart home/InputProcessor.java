import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InputProcessor {
    private static class TerminationException extends RuntimeException { // exception for when first command is not
                                                                         // SetInitialTime
    }

    private Scanner scanner;
    private Map<String, Smart> smartDevices;
    private LocalDateTime currentTime;
    private boolean initialTimeSet = false;
    private int deviceCounter = 0;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    public InputProcessor(String filename) { // opens input
        this.smartDevices = new LinkedHashMap<>();
        try {
            this.scanner = new Scanner(new File(filename));
        } catch (Exception e) {
            System.out.println("Error opening file: " + e.getMessage());
        }
    }

    public void processCommands(OutputWriter outputWriter) {
        String lastCommand = "";
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); // read next line
                if (line.isEmpty())
                    continue;
                String[] parts = line.split("\t"); // split by tab
                String command = parts[0]; // first part is the command name
                outputWriter.writeCommand(line);
                lastCommand = command;

                if (!initialTimeSet) {
                    if (!command.equals("SetInitialTime") || parts.length != 2) {
                        outputWriter
                                .writeError("First command must be set initial time! Program is going to terminate!");
                        throw new TerminationException();
                    }
                }

                switch (command) {
                    case "SetInitialTime":
                        handleSetInitialTime(parts, outputWriter);
                        break;
                    case "SetTime":
                        handleSetTime(parts, outputWriter);
                        break;
                    case "SkipMinutes":
                        handleSkipMinutes(parts, outputWriter);
                        break;
                    case "Nop":
                        handleNop(parts, outputWriter);
                        break;
                    case "Add":
                        handleAdd(parts, outputWriter);
                        break;
                    case "Remove":
                        handleRemove(parts, outputWriter);
                        break;
                    case "SetSwitchTime":
                        handleSetSwitchTime(parts, outputWriter);
                        break;
                    case "Switch":
                        handleSwitch(parts, outputWriter);
                        break;
                    case "ChangeName":
                        handleChangeName(parts, outputWriter);
                        break;
                    case "PlugIn":
                        handlePlugIn(parts, outputWriter);
                        break;
                    case "PlugOut":
                        handlePlugOut(parts, outputWriter);
                        break;
                    case "SetKelvin":
                        handleSetKelvin(parts, outputWriter);
                        break;
                    case "SetBrightness":
                        handleSetBrightness(parts, outputWriter);
                        break;
                    case "SetColorCode":
                        handleSetColorCode(parts, outputWriter);
                        break;
                    case "SetColor":
                        handleSetColor(parts, outputWriter);
                        break;
                    case "SetWhite":
                        handleSetWhite(parts, outputWriter);
                        break;
                    case "ZReport":
                        handleZReport(parts, outputWriter);
                        break;
                    default:
                        outputWriter.writeError("Erroneous command!");
                        break;
                }
            }
        } catch (TerminationException e) {
            // Processing stops
        }
        if (initialTimeSet) {
            if (!lastCommand.equals("ZReport")) { // at the end of each case print report
                outputWriter.write("ZReport:");
                handleZReport(new String[] { "ZReport" }, outputWriter);
            }
        }
    }

    // handlers that calls necessary methods and checks error cases

    private void handleSetInitialTime(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 2) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (initialTimeSet) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        try {
            currentTime = LocalDateTime.parse(parts[1], formatter);
            initialTimeSet = true;
            outputWriter.writeSuccess("Time has been set to " + parts[1] + "!");
        } catch (Exception e) {
            outputWriter.writeError("Format of the initial date is wrong! Program is going to terminate!");
            throw new TerminationException();
        }
    }

    private void handleSetTime(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 2) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        try {
            LocalDateTime newTime = LocalDateTime.parse(parts[1], formatter);
            if (newTime.isBefore(currentTime)) {
                outputWriter.writeError("Time cannot be reversed!");
                return;
            }
            if (newTime.isEqual(currentTime)) {
                outputWriter.writeError("There is nothing to change!");
                return;
            }
            executeScheduledEvents(newTime); // execute events that happen between currentTime and newTime
            currentTime = newTime;
        } catch (Exception e) {
            outputWriter.writeError("Time format is not correct!");
        }
    }

    private void handleSkipMinutes(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 2) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        int minutes;
        try {
            minutes = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (minutes < 0) {
            outputWriter.writeError("Time cannot be reversed!");
            return;
        }
        if (minutes == 0) {
            outputWriter.writeError("There is nothing to skip!");
            return;
        }
        executeScheduledEvents(currentTime.plusMinutes(minutes));
        currentTime = currentTime.plusMinutes(minutes);
    }

    private void handleNop(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 1) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        LocalDateTime nextEventTime = getNextEventTime();
        if (nextEventTime == null) {
            outputWriter.writeError("There is nothing to switch!");
            return;
        }
        executeScheduledEvents(nextEventTime);
        currentTime = nextEventTime;
    }

    // handles add for different types of smart devices
    private void handleAdd(String[] parts, OutputWriter outputWriter) {
        if (parts.length < 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        String type = parts[1];
        String name = parts[2];

        Smart device = null;
        switch (type) {
            case "SmartPlug":
                if (parts.length == 3) {
                    device = new Plug(name);
                } else if (parts.length == 4) {
                    if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    device = new Plug(name, parts[3]);
                } else if (parts.length == 5) {
                    double ampere;
                    try {
                        ampere = Double.parseDouble(parts[4]);
                    } catch (NumberFormatException e) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    if (ampere <= 0) {
                        outputWriter.writeError("Ampere value must be a positive number!");
                        return;
                    }
                    if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    device = new Plug(name, parts[3], ampere);
                } else {
                    outputWriter.writeError("Erroneous command!");
                    return;
                }
                break;
            case "SmartCamera":
                if (parts.length >= 4) {
                    double storage;
                    try {
                        storage = Double.parseDouble(parts[3]);
                    } catch (NumberFormatException e) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    if (storage <= 0) {
                        outputWriter.writeError("Megabyte value must be a positive number!");
                        return;
                    }
                    if (parts.length == 5) {
                        if (!parts[4].equals("On") && !parts[4].equals("Off")) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        device = new Camera(name, storage, parts[4]);
                    } else if (parts.length == 4) {
                        device = new Camera(name, storage);
                    } else {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                } else {
                    outputWriter.writeError("Erroneous command!");
                    return;
                }
                break;
            case "SmartLamp":
                if (parts.length == 3) {
                    device = new Lamp(name);
                } else if (parts.length == 4) {
                    if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    device = new Lamp(name, parts[3]);
                } else if (parts.length == 6) {
                    int kelvin;
                    try {
                        kelvin = Integer.parseInt(parts[4]);
                    } catch (NumberFormatException e) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    if (kelvin < 2000 || kelvin > 6500) {
                        outputWriter.writeError("Kelvin value must be in range of 2000K-6500K!");
                        return;
                    }
                    int brightness;
                    try {
                        brightness = Integer.parseInt(parts[5]);
                    } catch (NumberFormatException e) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    if (brightness < 0 || brightness > 100) {
                        outputWriter.writeError("Brightness must be in range of 0%-100%!");
                        return;
                    }
                    if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    device = new Lamp(name, parts[3], kelvin, brightness);
                } else {
                    outputWriter.writeError("Erroneous command!");
                    return;
                }
                break;
            case "SmartColorLamp":
                if (parts.length == 3) {
                    device = new ColorLamp(name);
                } else if (parts.length == 4) {
                    if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                        outputWriter.writeError("Erroneous command!");
                        return;
                    }
                    device = new ColorLamp(name, parts[3]);
                } else if (parts.length == 6) {
                    if (parts[4].startsWith("0x")) {
                        int color;
                        try {
                            color = Integer.parseInt(parts[4].substring(2), 16);
                        } catch (NumberFormatException e) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        if (color < 0 || color > 0xFFFFFF) {
                            outputWriter.writeError("Color code value must be in range of 0x0-0xFFFFFF!");
                            return;
                        }
                        int brightness;
                        try {
                            brightness = Integer.parseInt(parts[5]);
                        } catch (NumberFormatException e) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        if (brightness < 0 || brightness > 100) {
                            outputWriter.writeError("Brightness must be in range of 0%-100%!");
                            return;
                        }
                        if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        device = new ColorLamp(name, parts[3], color, 4000, brightness);
                    } else {
                        int kelvin;
                        try {
                            kelvin = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException e) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        if (kelvin < 2000 || kelvin > 6500) {
                            outputWriter.writeError("Kelvin value must be in range of 2000K-6500K!");
                            return;
                        }
                        int brightness;
                        try {
                            brightness = Integer.parseInt(parts[5]);
                        } catch (NumberFormatException e) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        if (brightness < 0 || brightness > 100) {
                            outputWriter.writeError("Brightness must be in range of 0%-100%!");
                            return;
                        }
                        if (!parts[3].equals("On") && !parts[3].equals("Off")) {
                            outputWriter.writeError("Erroneous command!");
                            return;
                        }
                        device = new ColorLamp(name, parts[3], kelvin, brightness);
                    }
                } else {
                    outputWriter.writeError("Erroneous command!");
                    return;
                }
                break;
            default:
                outputWriter.writeError("Erroneous command!");
                return;
        }

        if (device != null) {
            if (smartDevices.containsKey(name)) {
                outputWriter.writeError("There is already a smart device with same name!");
                return;
            }
            device.setInitialOrder(deviceCounter++);
            smartDevices.put(name, device);
            if (device.isOn()) {
                device.setLastOnTime(currentTime);
            }
        }
    }

    private void handleRemove(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 2) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.remove(parts[1]);
        device.setOff(currentTime);
        outputWriter.writeSuccess("Information about removed smart device is as follows:");
        outputWriter.write(device.getDescription());
    }

    private void handleSetSwitchTime(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        try {
            LocalDateTime switchTime = LocalDateTime.parse(parts[2], formatter);
            if (switchTime.isBefore(currentTime)) {
                outputWriter.writeError("Switch time cannot be in the past!");
                return;
            }
            Smart device = smartDevices.get(parts[1]);
            device.setSwitchTime(switchTime);
            reorderDevices(); // update the order w.r.t switch time
            if (switchTime.isEqual(currentTime)) {
                if (device.isOn()) {
                    device.setOff(currentTime);
                } else {
                    device.setOn(currentTime);
                }
                device.setSwitchTime(null);
                reorderDevices();
            }
        } catch (Exception e) {
            outputWriter.writeError("Time format is not correct!");
        }
    }

    private void handleSwitch(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        String status = parts[2];
        if (status.equals("On")) {
            if (device.isOn()) {
                outputWriter.writeError("This device is already switched on!");
            } else {
                device.setOn(currentTime);
                device.setSwitchTime(null);
            }
        } else if (status.equals("Off")) {
            if (!device.isOn()) {
                outputWriter.writeError("This device is already switched off!");
            } else {
                device.setOff(currentTime);
                device.setSwitchTime(null);
            }
        } else {
            outputWriter.writeError("Erroneous command!");
        }
    }

    private void handleChangeName(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        String oldName = parts[1];
        String newName = parts[2];
        if (oldName.equals(newName)) {
            outputWriter.writeError("Both of the names are the same, nothing changed!");
            return;
        }
        if (!smartDevices.containsKey(oldName)) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        if (smartDevices.containsKey(newName)) {
            outputWriter.writeError("There is already a smart device with same name!");
            return;
        }

        Map<String, Smart> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Smart> entry : smartDevices.entrySet()) {
            if (entry.getKey().equals(oldName)) { // find device with old name
                Smart device = entry.getValue();
                device.setName(newName);
                newMap.put(newName, device);
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        smartDevices = newMap;
    }

    private void handlePlugIn(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof Plug)) {
            outputWriter.writeError("This device is not a smart plug!");
            return;
        }
        Plug plug = (Plug) device;
        if (plug.isOccupied()) {
            outputWriter.writeError("There is already an item plugged in to that plug!");
            return;
        }
        double ampere;
        try {
            ampere = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (ampere <= 0) {
            outputWriter.writeError("Ampere value must be a positive number!");
            return;
        }
        plug.plugIn(ampere, currentTime);
    }

    private void handlePlugOut(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 2) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof Plug)) {
            outputWriter.writeError("This device is not a smart plug!");
            return;
        }
        Plug plug = (Plug) device;
        if (!plug.isOccupied()) {
            outputWriter.writeError("This plug has no item to plug out from that plug!");
            return;
        }
        plug.plugOut(currentTime);
    }

    private void handleSetKelvin(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof Lamp)) {
            outputWriter.writeError("This device is not a smart lamp!");
            return;
        }
        int kelvin;
        try {
            kelvin = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (kelvin < 2000 || kelvin > 6500) {
            outputWriter.writeError("Kelvin value must be in range of 2000K-6500K!");
            return;
        }
        ((Lamp) device).setKelvin(kelvin);
    }

    private void handleSetBrightness(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof Lamp)) {
            outputWriter.writeError("This device is not a smart lamp!");
            return;
        }
        int brightness;
        try {
            brightness = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (brightness < 0 || brightness > 100) {
            outputWriter.writeError("Brightness must be in range of 0%-100%!");
            return;
        }
        ((Lamp) device).setBrightness(brightness);
    }

    private void handleSetColorCode(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 3) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof ColorLamp)) {
            outputWriter.writeError("This device is not a smart color lamp!");
            return;
        }
        if (!parts[2].startsWith("0x")) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        int color;
        try {
            color = Integer.parseInt(parts[2].substring(2), 16);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (color < 0 || color > 0xFFFFFF) {
            outputWriter.writeError("Color code value must be in range of 0x0-0xFFFFFF!");
            return;
        }
        ((ColorLamp) device).setColor(color);
    }

    private void handleSetColor(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 4) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof ColorLamp)) {
            outputWriter.writeError("This device is not a smart color lamp!");
            return;
        }
        if (!parts[2].startsWith("0x")) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        int color;
        try {
            color = Integer.parseInt(parts[2].substring(2), 16);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (color < 0 || color > 0xFFFFFF) {
            outputWriter.writeError("Color code value must be in range of 0x0-0xFFFFFF!");
            return;
        }
        int brightness;
        try {
            brightness = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (brightness < 0 || brightness > 100) {
            outputWriter.writeError("Brightness must be in range of 0%-100%!");
            return;
        }
        ((ColorLamp) device).setColor(color);
        ((ColorLamp) device).setBrightness(brightness);
    }

    private void handleSetWhite(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 4) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (!smartDevices.containsKey(parts[1])) {
            outputWriter.writeError("There is not such a device!");
            return;
        }
        Smart device = smartDevices.get(parts[1]);
        if (!(device instanceof Lamp)) {
            outputWriter.writeError("This device is not a smart lamp!");
            return;
        }
        int kelvin;
        try {
            kelvin = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (kelvin < 2000 || kelvin > 6500) {
            outputWriter.writeError("Kelvin value must be in range of 2000K-6500K!");
            return;
        }
        int brightness;
        try {
            brightness = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        if (brightness < 0 || brightness > 100) {
            outputWriter.writeError("Brightness must be in range of 0%-100%!");
            return;
        }
        if (device instanceof ColorLamp) {
            ((ColorLamp) device).setWhite(kelvin, brightness);
        } else {
            ((Lamp) device).setKelvin(kelvin);
            ((Lamp) device).setBrightness(brightness);
        }
    }

    private void handleZReport(String[] parts, OutputWriter outputWriter) {
        if (parts.length != 1) {
            outputWriter.writeError("Erroneous command!");
            return;
        }
        outputWriter.write("Time is:\t" + currentTime.format(formatter));
        for (Smart device : smartDevices.values()) {
            outputWriter.write(device.getDescription());
        }
    }

    private void reorderDevices() {
        // sort the list of devices based on switch times
        List<Smart> devices = new ArrayList<>(smartDevices.values());
        final List<Smart> currentOrder = new ArrayList<>(smartDevices.values());

        Collections.sort(devices, new Comparator<Smart>() {
            @Override
            public int compare(Smart d1, Smart d2) {
                LocalDateTime t1 = d1.getSwitchTime();
                LocalDateTime t2 = d2.getSwitchTime();

                if (t1 == null && t2 == null) { // if both have no switch time
                    return Integer.compare(currentOrder.indexOf(d1), currentOrder.indexOf(d2));
                }
                if (t1 == null) {
                    return 1;
                }
                if (t2 == null) {
                    return -1;
                }

                int res = t1.compareTo(t2);
                if (res != 0) {
                    return res;
                }

                return Integer.compare(currentOrder.indexOf(d1), currentOrder.indexOf(d2));
            }
        });

        Map<String, Smart> newMap = new LinkedHashMap<>();
        for (Smart d : devices) {
            newMap.put(d.getName(), d);
        }
        smartDevices = newMap;
    }

    private void executeScheduledEvents(LocalDateTime untilTime) {
        // run the events one by one until reaching the time
        while (true) {
            LocalDateTime nextTime = null;
            for (Smart device : smartDevices.values()) {
                LocalDateTime switchTime = device.getSwitchTime();
                if (switchTime != null) {
                    if (nextTime == null || switchTime.isBefore(nextTime)) {
                        nextTime = switchTime;
                    }
                }
            }

            if (nextTime == null || (untilTime != null && nextTime.isAfter(untilTime))) {
                break;
            }

            for (Smart device : smartDevices.values()) {
                LocalDateTime switchTime = device.getSwitchTime();
                if (switchTime != null && switchTime.isEqual(nextTime)) {
                    if (device.isOn()) {
                        device.setOff(switchTime);
                    } else {
                        device.setOn(switchTime);
                    }
                    device.setSwitchTime(null);
                }
            }
            reorderDevices();
        }
    }

    private LocalDateTime getNextEventTime() {
        // search for the earliest event time in all devices
        LocalDateTime nextTime = null;
        for (Smart device : smartDevices.values()) {
            LocalDateTime switchTime = device.getSwitchTime();
            if (switchTime != null) {
                if (nextTime == null || switchTime.isBefore(nextTime)) {
                    nextTime = switchTime;
                }
            }
        }
        return nextTime;
    }
}
