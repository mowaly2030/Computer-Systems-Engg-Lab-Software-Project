// Apple.java
// Dynamic item.  Every clock tick it loses one unit of
// freshness.  When freshness hits 0 the apple rots and
// becomes useless (the Village Elder will refuse a rotten
// apple, so the player has to act quickly).
//
// Like Torch, Apple is an Observer of the GameClock.
public class Apple extends Item implements Observer {
    private int freshness;
    private boolean rotten;

    public Apple() {
        super("Apple");
        this.freshness = 5;
        this.rotten = false;
    }

    public synchronized boolean isRotten() { return rotten; }

    public synchronized void update() {
        if (!rotten) {
            freshness--;
            if (freshness <= 0) {
                rotten = true;
                this.name = "Rotten Apple";
                System.out.println("\n[An apple has rotted away.]");
            }
        }
    }

    public synchronized String describe() {
        if (rotten) return "Rotten Apple (foul-smelling, useless)";
        return         "Apple (fresh, " + freshness + " ticks before rotting)";
    }
}
