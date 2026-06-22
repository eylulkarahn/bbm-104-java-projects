import java.time.LocalDateTime;

public class Lamp extends Smart {
    private int kelvin = 2700; // default kelvin
    private int brightness = 100; // default brightness in percent

    // Constructors

    public Lamp(String name) {
        super(name);
        this.kelvin = 4000;
        this.brightness = 100;
    }

    public Lamp(String name, String status) {
        super(name, status);
        this.kelvin = 4000;
        this.brightness = 100;
    }

    public Lamp(String name, String status, int kelvin, int brightness) {
        super(name, status);
        this.kelvin = kelvin;
        this.brightness = brightness;
    }

    // getters and setters

    public int getKelvin() {
        return kelvin;
    }

    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public void calculateUsage(LocalDateTime currentTime) {
    }

    @Override
    public String getDescription() {
        return String.format(
                "Smart Lamp %s is %s and its kelvin value is %dK with %d%% brightness, and its time to switch its status is %s.",
                getName(), getStatusString(), kelvin, brightness, getSwitchTimeString());
    }
}
