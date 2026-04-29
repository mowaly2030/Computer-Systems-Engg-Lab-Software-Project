// Monster.java
// The Forest Monster.  Combines THREE concepts:
//
//   1. Character (Template Method) - so the player can "talk"
//      / approach it and the recipe is enforced.
//   2. Runnable - the monster runs on its OWN thread,
//      independent of the clock thread, so it acts on its own.
//   3. Strategy - the monster switches between
//      Passive / Aggressive / Enraged behaviors at runtime.
//
// All shared state (hp, alive, behavior, location) is
// guarded by synchronized methods so the main thread, clock
// thread and monster thread can all read/write it safely.
public class Monster extends Character implements Runnable {
    private int hp;
    private boolean alive;
    private MonsterBehavior behavior;
    private final Thread t;
    private final World world;
    private final int actionMs;
    private boolean shouldRun;

    public Monster(World w, int actionMs) {
        super("Forest Monster", "Cave");
        this.world     = w;
        this.actionMs  = actionMs;
        this.hp        = 80;
        this.alive     = true;
        this.shouldRun = true;
        this.behavior  = new PassiveBehavior();   // initial strategy
        // Build the worker thread that drives the monster's actions.
        t = new Thread(this);
        t.start();
    }

    public synchronized boolean isAlive() { return alive; }
    public synchronized int     getHp()   { return hp;    }

    public synchronized void takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            alive = false;
            System.out.println("\n[The Forest Monster collapses with a guttural roar... it lies still.]");
        }
    }

    // The Strategy "setter".
    public synchronized void setBehavior(MonsterBehavior b) {
        // Only swap if the strategy actually changed,
        // so we don't allocate a new object every tick.
        if (!this.behavior.getClass().equals(b.getClass())) {
            this.behavior = b;
        }
    }
    public synchronized MonsterBehavior getBehavior() { return behavior; }

    public Thread getThread() { return t; }

    // Stop the monster's thread cleanly when the game ends.
    public void stopRunning() {
        synchronized (this) { shouldRun = false; }
        t.interrupt();
    }

    // ----- Template Method primitive operations ------------
    protected boolean canInteract(Player p, World w) {
        // Only "interact" with the monster if it's alive AND
        // in the same scene as the player.
        return isAlive() && getLocation().equals(p.getLocation());
    }

    protected void describeBlocked(Player p, World w) {
        System.out.println("There is no monster here.");
    }

    protected void showAppearance(Player p, World w) {
        System.out.println("The Forest Monster eyes you, " + behavior.describe() + ".");
    }

    protected void performAction(Player p, World w) {
        // "Talking to" a monster always ends in being bitten.
        int dmg = behavior.getDamage();
        System.out.println("The Monster bites you for " + dmg + " damage!");
        p.takeDamage(dmg);
    }
    // -------------------------------------------------------

    // ----- Runnable.run() - the monster's own thread -------
    public void run() {
        while (true) {
            try {
                Thread.sleep(actionMs);
            } catch (InterruptedException e) {
                return;
            }
            // Stop conditions
            synchronized (this) {
                if (!shouldRun || !alive) return;
            }

            // ---- Pick the right strategy for the situation ----
            GameClock clk = world.getClock();
            Player    p   = world.getPlayer();

            if (p.getHp() < 30) {
                setBehavior(new EnragedBehavior());
            } else if (clk.isNight()) {
                setBehavior(new AggressiveBehavior());
            } else {
                setBehavior(new PassiveBehavior());
            }

            // ---- Execute the chosen strategy ------------------
            // Take a snapshot of the strategy reference under
            // the lock, then call act() outside the lock so
            // we don't hold the monster's lock while we touch
            // the player and world objects.
            MonsterBehavior current;
            synchronized (this) { current = this.behavior; }
            current.act(this, world);
        }
    }
    // -------------------------------------------------------
}
