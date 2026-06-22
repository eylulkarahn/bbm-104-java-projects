public abstract class Room { // main room class
    private int x;
    private int y;
    private String type;

    public Room(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    // every room ha an effect that will be defined in the subclasses
    public abstract void applyEffect(Player player);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    /*
     * created subclasses to follow oop principles though i
     * think these classes were not necessary for this assignment
     */
    public static Room create(int x, int y, String type) {
        switch (type) {
            case "M":
                return new MonsterRoom(x, y);
            case "T":
                return new TrapRoom(x, y);
            case "L":
                return new ArtifactRoom(x, y, "L");
            case "H":
                return new HealingRoom(x, y);
            case "A":
                return new ArtifactRoom(x, y, "A");
            case "B":
                return new ArtifactRoom(x, y, "B");
            case "K":
                return new KeyRoom(x, y);
            case "X":
                return new ExitRoom(x, y);
            default:
                return new EmptyRoom(x, y);
        }
    }
}

class MonsterRoom extends Room {
    public MonsterRoom(int x, int y) {
        super(x, y, "M");
    }

    @Override
    // monster room takes 30 health from player
    public void applyEffect(Player player) {
        player.takeDamage(30);
    }
}

class TrapRoom extends Room {
    public TrapRoom(int x, int y) {
        super(x, y, "T");
    }

    @Override
    // trap room takes 20 health from player
    public void applyEffect(Player player) {
        player.takeDamage(20);
    }
}

class HealingRoom extends Room {
    public HealingRoom(int x, int y) {
        super(x, y, "H");
    }

    @Override
    // heals 15 health
    public void applyEffect(Player player) {
        player.heal(15);
    }
}

class ArtifactRoom extends Room {
    private int power;

    public ArtifactRoom(int x, int y, String type) {
        super(x, y, type);
        if (type.equals("L")) {
            this.power = 10;
        } else {
            if (type.equals("A")) {
                this.power = 6;
            } else {
                this.power = 4;
            }
        }
    }

    @Override
    public void applyEffect(Player player) {
        if (getType().equals("L")) { // if relicHeal heals 10
            player.heal(power);
        } else { // if shields sets shield
            player.pickShield(power);
        }
    }
}

class KeyRoom extends Room {
    public KeyRoom(int x, int y) {
        super(x, y, "K");
    }

    @Override
    // if player encounters key room, player has key
    public void applyEffect(Player player) {
        player.setHasKey(true);
    }
}

class ExitRoom extends Room {
    public ExitRoom(int x, int y) {
        super(x, y, "X");
    }

    @Override
    // if exit room and
    public void applyEffect(Player player) {
        player.setReachedExit(true);

    }
}

class EmptyRoom extends Room {
    public EmptyRoom(int x, int y) {
        super(x, y, "E");
    }

    @Override // empty room has no effect
    public void applyEffect(Player player) {
    }
}
