import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Camera extends Smart {
    private double storageRate; // mbs per minute
    private double usedStorage = 0; // total used storage

    public Camera(String name, double storageRate) {
        super(name);
        this.storageRate = storageRate;
    }

    public Camera(String name, double storageRate, String status) {
        super(name, status);
        this.storageRate = storageRate;
    }

    @Override
    public void calculateUsage(LocalDateTime currentTime) {
        if (isOn() && getLastOnTime() != null) {
            long seconds = ChronoUnit.SECONDS.between(getLastOnTime(), currentTime);
            usedStorage += (storageRate * seconds) / 60.0;
            setLastOnTime(currentTime);
        }
    }

    @Override
    public String getDescription() {
        return "Smart Camera " + getName() + " is " + getStatusString() + " and used " + String.format("%.2f", usedStorage) + " MB of storage so far (excluding present usage), and its time to switch its status is " + getSwitchTimeString() + ".";
    }
}
