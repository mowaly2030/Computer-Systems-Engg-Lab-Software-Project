// Torch.java
// Dynamic item.  When LIT it consumes one unit of fuel
// every clock tick.  Once fuel runs out the torch is
// burned out and useless.
//
// The Torch implements Observer so that the GameClock can
// notify it every tick (Observer pattern).
public class Torch extends Item implements Observer {
    private int fuel;
    private boolean lit;

    public Torch() {
        super("Torch");
        this.fuel = 5;     // 5 ticks of light when lit
        this.lit  = false;
    }

    // synchronized: lit/fuel are read by both the player
    // (main thread) and the clock (clock thread).
    public synchronized boolean light() {
        if (fuel <= 0) return false;
        lit = true;
        return true;
    }

    public synchronized boolean isLit()  { return lit && fuel > 0; }
    public synchronized int     getFuel(){ return fuel; }

    // Called by the GameClock every tick.
    public synchronized void update() {
        if (lit && fuel > 0) {
            fuel--;
            if (fuel == 0) {
                lit = false;
                System.out.println("\n[Your torch flickers and burns out.]");
            }
        }
    }

    public synchronized String describe() {
        if (fuel == 0) return "Torch (burned out)";
        if (lit)       return "Torch (lit, fuel: " + fuel + " ticks)";
        return            "Torch (unlit, fuel: " + fuel + " ticks)";
    }
}
