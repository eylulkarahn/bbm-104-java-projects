public class ColorLamp extends Lamp {
    private int color; // hexadecimal color value
    private boolean colorMode; // true if hex color is used

    public ColorLamp(String name) {
        super(name);
        this.color = 0;
        this.colorMode = false;
    }

    public ColorLamp(String name, String status) {
        super(name, status);
        this.color = 0;
        this.colorMode = false;
    }

    public ColorLamp(String name, String status, int kelvin, int brightness) {
        super(name, status, kelvin, brightness);
        this.color = 0;
        this.colorMode = false;
    }

    public ColorLamp(String name, String status, int color, int kelvin, int brightness) {
        super(name, status, kelvin, brightness);
        this.color = color;
        this.colorMode = true;
    }

    // Setters

    public void setColor(int color) {
        this.color = color;
        this.colorMode = true;
    }

    public void setWhite(int kelvin, int brightness) {
        setKelvin(kelvin);
        setBrightness(brightness);
        this.colorMode = false;
    }

    @Override
    public String getDescription() {
        String colorVal = colorMode ? String.format("0x%06X", color) : getKelvin() + "K";
        return String.format(
                "Smart Color Lamp %s is %s and its color value is %s with %d%% brightness, and its time to switch its status is %s.",
                getName(), getStatusString(), colorVal, getBrightness(), getSwitchTimeString());
    }
}
