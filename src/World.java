// World.java
// Holds the entire game state:
//   - all the Scenes (the map)
//   - the player
//   - the list of characters
//   - the GameClock
//   - the shared Scanner for user input
//
// Every method that touches shared mutable state is
// synchronized so the main thread (player commands), the
// clock thread (ticks) and the monster thread (movement)
// cannot stomp on each other.
import java.util.ArrayList;
import java.util.Scanner;

public class World {
    private ArrayList<Scene>     scenes;
    private ArrayList<Character> characters;
    private Player               player;
    private Monster              monster;
    private final Scanner        input;
    private final GameClock      clock;
    private boolean              chamberUnlocked;

    public World(Scanner input, GameClock clock) {
        this.input           = input;
        this.clock           = clock;
        this.scenes          = new ArrayList<Scene>();
        this.characters      = new ArrayList<Character>();
        this.chamberUnlocked = false;
        buildMap();
    }

    // Set up the six scenes and connect them.
    private void buildMap() {
        Scene vs = new Scene("Village Square",
            "A quiet village square with cobblestones. A crumbling fountain sits in the middle.",
            "The village square is silent under a sickly moon. Shutters are drawn tight on every window.");
        Scene ah = new Scene("Abandoned House",
            "Dust covers everything in this old house. A wooden chest sits in the corner.",
            "The abandoned house creaks in the dark. Something feels deeply wrong here.");
        Scene df = new Scene("Dark Forest",
            "Thick trees surround you. A narrow path winds toward a yawning cave.",
            "The dark forest pulses with malice. Eyes watch from every shadow.");
        Scene cv = new Scene("Cave",
            "A damp cave dripping with water. The walls glitter with something metallic.",
            "The cave is pitch black. You hear breathing that is not your own.");
        Scene oc = new Scene("Old Church",
            "An old stone church with a broken altar. A stairway descends into darkness, blocked by a guard.",
            "The church looms in the dark. The altar glows faintly with cursed energy.");
        Scene hc = new Scene("Hidden Underground Chamber",
            "A vast stone chamber with an ancient altar at its center. The air hums with magic.",
            "The chamber pulses with cursed light. The altar awaits.");

        // map connections
        vs.addExit("Abandoned House");
        vs.addExit("Dark Forest");
        vs.addExit("Old Church");
        ah.addExit("Village Square");
        df.addExit("Village Square");
        df.addExit("Cave");
        cv.addExit("Dark Forest");
        oc.addExit("Village Square");
        oc.addExit("Hidden Underground Chamber");
        hc.addExit("Old Church");

        // The chamber is locked at the start - the guard
        // unlocks it when the player has all three relics.
        hc.setAccessible(false);

        scenes.add(vs);
        scenes.add(ah);
        scenes.add(df);
        scenes.add(cv);
        scenes.add(oc);
        scenes.add(hc);
    }

    // -------- accessors that don't need synchronization ----
    public Scanner   getInput() { return input; }
    public GameClock getClock() { return clock; }

    // -------- player and characters -----------------------
    public synchronized Player  getPlayer()         { return player; }
    public synchronized Monster getMonster()        { return monster; }
    public synchronized void    setPlayer(Player p) { this.player = p; }

    public synchronized void setMonster(Monster m) {
        this.monster = m;
        this.characters.add(m);
    }
    public synchronized void addCharacter(Character c) {
        this.characters.add(c);
    }

    // All characters currently in a given scene.
    public synchronized ArrayList<Character> getCharactersAt(String location) {
        ArrayList<Character> here = new ArrayList<Character>();
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            if (c.getLocation().equalsIgnoreCase(location)) here.add(c);
        }
        return here;
    }

    // Look up a character by (location, name).
    public synchronized Character findCharacter(String location, String name) {
        for (int i = 0; i < characters.size(); i++) {
            Character c = characters.get(i);
            if (c.getLocation().equalsIgnoreCase(location)
                && c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    // -------- scenes --------------------------------------
    public synchronized Scene getScene(String name) {
        for (int i = 0; i < scenes.size(); i++) {
            if (scenes.get(i).getName().equalsIgnoreCase(name)) return scenes.get(i);
        }
        return null;
    }

    public synchronized Scene getCurrentScene() {
        return getScene(player.getLocation());
    }

    public synchronized ArrayList<Scene> getAllScenes() {
        return new ArrayList<Scene>(scenes);
    }

    // -------- movement / unlocking ------------------------
    public synchronized boolean canMoveTo(String dest) {
        Scene cur = getCurrentScene();
        if (cur == null) return false;
        // Must be a listed exit AND target must be accessible.
        boolean isExit = false;
        for (int i = 0; i < cur.getExits().size(); i++) {
            if (cur.getExits().get(i).equalsIgnoreCase(dest)) { isExit = true; break; }
        }
        if (!isExit) return false;
        Scene target = getScene(dest);
        if (target == null) return false;
        return target.isAccessible();
    }

    public synchronized void unlockChamber() {
        Scene hc = getScene("Hidden Underground Chamber");
        if (hc != null) {
            hc.setAccessible(true);
            chamberUnlocked = true;
        }
    }
    public synchronized boolean isChamberUnlocked() { return chamberUnlocked; }
}
