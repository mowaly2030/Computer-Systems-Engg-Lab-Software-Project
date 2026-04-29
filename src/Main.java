// Main.java
// Entry point.  Just builds a Game and runs it.
public class Main {
    public static void main(String[] args) {
        Game g;
        // Optional debug arg: a numeric first argument
        // overrides the tick speed in milliseconds.
        if (args.length > 0) {
            try {
                int ms = Integer.parseInt(args[0]);
                g = new Game(ms);
            } catch (NumberFormatException e) {
                g = new Game();
            }
        } else {
            g = new Game();
        }
        g.play();
    }
}
