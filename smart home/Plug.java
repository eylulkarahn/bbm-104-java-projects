import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Plug extends Smart {
    private double ampere;
    private double consumedWatt = 0; // consumed energy

    public Plug(String name) {
        super(name);
    }

    public Plug(String name, String status) {
        super(name, status);
    }

    public Plug(String name, String status, double ampere) {
        super(name, status);
        this.ampere = ampere;
    }

    public void setAmpere(double ampere, LocalDateTime currentTime) {
        calculateUsage(currentTime);
        this.ampere = ampere;
    }

    @Override
    public void calculateUsage(LocalDateTime currentTime) {
        if (isOn() && getLastOnTime() != null) {
            long seconds = ChronoUnit.SECONDS.between(getLastOnTime(), currentTime);
            consumedWatt += (220 * ampere * seconds) / 3600.0;
            setLastOnTime(currentTime);
        }
    }

    public void plugIn(double ampere, LocalDateTime currentTime) {
        setAmpere(ampere, currentTime);
    }

    public void plugOut(LocalDateTime currentTime) {
        setAmpere(0, currentTime);
    }

    public boolean isOccupied() {
        return ampere > 0;
    }

    @Override
    public String getDescription() {
        return String.format(
                "Smart Plug %s is %s and consumed %.2fW so far (excluding present usage), and its time to switch its status is %s.",
                getName(), getStatusString(), consumedWatt, getSwitchTimeString());
    }
}
