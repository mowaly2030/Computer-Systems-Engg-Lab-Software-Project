// GameClock.java
// The "engine" of the game.  Implements the Subject role of the
// Observer pattern: it extends ConcreteSubject and implements
// Runnable so it can run on its own thread and notify all
// observers every tick.
//
// In our game one tick = 10 seconds of real time.
// 6 ticks  = day, next 6 ticks = night, so a full day/night
// cycle is 12 ticks (2 minutes).  After 3 day/night cycles
// the curse wins.
public class GameClock extends ConcreteSubject implements Runnable {
    private int tick;
    private boolean running;
    private final int tickMs;
    private Thread t;

    public GameClock(int tickMs) {
        super();
        this.tickMs = tickMs;
        this.tick = 0;
        this.running = true;
        // Build the worker thread and start it.
        t = new Thread(this);
        t.start();
    }

    public synchronized int getTick()  { return tick; }
    public synchronized int getDay()   { return tick / 12 + 1; }
    public synchronized boolean isNight() { return (tick % 12) >= 6; }
    public synchronized String getTimeOfDay() {
        return isNight() ? "Night" : "Day";
    }

    public Thread getThread() { return t; }

    // Tell the clock to stop (called when the game ends).
    public void stop() {
        synchronized (this) { running = false; }
        t.interrupt();
    }

    // The Runnable contract.  Sleeps for tickMs, increments
    // the tick counter, then notifies every registered observer.
    public void run() {
        while (true) {
            try {
                Thread.sleep(tickMs);
            } catch (InterruptedException e) {
                return;
            }
            synchronized (this) {
                if (!running) return;
                tick++;
            }
            notifyObservers();
        }
    }
}
