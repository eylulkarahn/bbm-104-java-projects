import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Smart {
    private String name;
    private String status; // On or Off
    private LocalDateTime switchTime; // When to switch next
    private LocalDateTime lastOnTime; // Last time it was turned on (for consumption)
    private int initialOrder;

    public Smart(String name) {
        this.name = name;
        this.status = "Off";
    }

    public Smart(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public void setOn(LocalDateTime currentTime) {
        if (!this.status.equals("On")) {
            calculateUsage(currentTime);
            this.status = "On";
            this.lastOnTime = currentTime;
        }
    }

    public void setOff(LocalDateTime currentTime) {
        if (!this.status.equals("Off")) {
            calculateUsage(currentTime);
            this.status = "Off";
            this.lastOnTime = null;
        }
    }


    public abstract void calculateUsage(LocalDateTime currentTime);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOn() {
        return this.status.equals("On");
    }

    public LocalDateTime getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(LocalDateTime switchTime) {
        this.switchTime = switchTime;
    }

    public int getInitialOrder() {
        return initialOrder;
    }

    public void setInitialOrder(int initialOrder) {
        this.initialOrder = initialOrder;
    }

    public LocalDateTime getLastOnTime() {
        return lastOnTime;
    }

    public void setLastOnTime(LocalDateTime lastOnTime) {
        this.lastOnTime = lastOnTime;
    }


    public abstract String getDescription();

    public String getStatusString() {
        return status.toLowerCase();
    }

    public String getSwitchTimeString() {
        if (switchTime == null)
            return "null";
        return switchTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
    }
}
