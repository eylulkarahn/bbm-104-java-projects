public class Player {
    private int maxHealth;
    private int health;
    private boolean shield;
    private int shieldPower;
    private int x;
    private int y;
    private boolean hasKey;
    private boolean reachedExit;

    public Player(int maxHealth, int health, boolean shield, int shieldPower, int x, int y, boolean hasKey,
            boolean reachedExit) {
        this.maxHealth = maxHealth;
        this.health = health;
        this.shield = shield;
        this.shieldPower = shieldPower;
        this.x = x;
        this.y = y;
        this.hasKey = hasKey;
        this.reachedExit = reachedExit;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // i am assuming if heal exceeds maxHealth no change happens
    public void heal(int amount) {
        int newHealth = this.health + amount;
        if (newHealth <= this.maxHealth) {
            this.health = newHealth;
        }
    }

    // takes damage accordingly checking shield
    public void takeDamage(int amount) {
        if (this.shield) {
            this.health -= (amount - this.shieldPower);
            this.shield = false;
            this.shieldPower = 0;
        } else {
            this.health -= amount;
        }
    }

    // this function is called when encountered a shield
    public void pickShield(int power) {
        this.shield = true;
        this.shieldPower = power;
    }

    public boolean isDead() {
        return this.health <= 0;
    }

    // getters
    public int getHealth() {
        return health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean getHasKey() {
        return hasKey;
    }

    public boolean getShield() {
        return shield;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getShieldPower() {
        return shieldPower;
    }

    public boolean getReachedExit() {
        return reachedExit;
    }

    // setters
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public void setReachedExit(boolean reachedExit) {
        this.reachedExit = reachedExit;
    }
}
