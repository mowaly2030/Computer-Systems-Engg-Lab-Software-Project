// Player.java
// Holds the player's mutable state: HP, gold, location,
// and inventory.  Many of these fields are read AND written
// from multiple threads (main thread, clock thread,
// monster thread), so every accessor is synchronized.
import java.util.ArrayList;

public class Player {
    private int hp;
    private final int maxHp;
    private int gold;
    private String location;
    private ArrayList<Item> inventory;

    public Player(String startLocation) {
        this.hp        = 100;
        this.maxHp     = 100;
        this.gold      = 30;
        this.location  = startLocation;
        this.inventory = new ArrayList<Item>();
        // Player starts with a sword (basic combat ability).
        inventory.add(new Sword());
    }

    public synchronized int     getHp()       { return hp; }
    public synchronized int     getMaxHp()    { return maxHp; }
    public synchronized int     getGold()     { return gold; }
    public synchronized String  getLocation() { return location; }
    public synchronized boolean isAlive()     { return hp > 0; }

    public synchronized void setLocation(String loc) { this.location = loc; }
    public synchronized void addGold(int g)         { this.gold += g; }
    public synchronized void spendGold(int g)       { this.gold -= g; }

    public synchronized void takeDamage(int d) {
        hp -= d;
        if (hp < 0) hp = 0;
    }

    public synchronized void heal(int h) {
        hp += h;
        if (hp > maxHp) hp = maxHp;
    }

    public synchronized void addToInventory(Item it)      { inventory.add(it); }
    public synchronized void removeFromInventory(Item it) { inventory.remove(it); }

    // Defensive copy so the caller can iterate without
    // worrying about another thread modifying the list.
    public synchronized ArrayList<Item> getInventory() {
        return new ArrayList<Item>(inventory);
    }

    // Look up a carried item by name (case-insensitive).
    public synchronized Item findItem(String name) {
        for (int i = 0; i < inventory.size(); i++) {
            Item it = inventory.get(i);
            if (it.getName().equalsIgnoreCase(name)) return it;
        }
        return null;
    }

    // Convenience checks used by characters that react to
    // what the player is carrying (e.g., Cursed Spirit).
    public synchronized boolean hasCharm() {
        for (int i = 0; i < inventory.size(); i++)
            if (inventory.get(i) instanceof ProtectiveCharm) return true;
        return false;
    }
    public synchronized boolean hasScroll() {
        for (int i = 0; i < inventory.size(); i++)
            if (inventory.get(i) instanceof AncientScroll) return true;
        return false;
    }
    public synchronized boolean hasKey() {
        for (int i = 0; i < inventory.size(); i++)
            if (inventory.get(i) instanceof MagicKey) return true;
        return false;
    }
    public synchronized boolean hasSword() {
        for (int i = 0; i < inventory.size(); i++)
            if (inventory.get(i) instanceof Sword) return true;
        return false;
    }

    // Used by the Village Elder: only a fresh apple counts.
    public synchronized Apple findFreshApple() {
        for (int i = 0; i < inventory.size(); i++) {
            Item it = inventory.get(i);
            if (it instanceof Apple && !((Apple) it).isRotten())
                return (Apple) it;
        }
        return null;
    }
}
