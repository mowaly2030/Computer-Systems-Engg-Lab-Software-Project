// Game.java
// The main game controller.  It:
//   - builds the world, the player, the clock, the monster
//     and all NPCs;
//   - registers itself and every time-aware item/character
//     as Observers of the GameClock;
//   - reads commands from stdin in a loop and dispatches
//     them to the right handler;
//   - cleanly stops the threads when the game ends.
//
// Game also implements Observer so the clock notifies it
// every tick - that's how the curse-times-out check is done.
import java.util.ArrayList;
import java.util.Scanner;

public class Game implements Observer {
    private final World     world;
    private final Player    player;
    private final GameClock clock;
    private final Monster   monster;
    private final Scanner   input;

    private boolean gameOver;
    private boolean playerWon;
    private String  endMessage;

    private static final int MAX_DAYS = 3;   // game ends after 3 days
    private static final int TICK_MS  = 10_000; // 10 sec per tick (per spec)

    public Game() {
        this(TICK_MS);
    }

    // Constructor that lets us override the tick speed.
    // (Used by scripted test runs - production uses 10s.)
    public Game(int tickMs) {
        this.input  = new Scanner(System.in);
        this.clock  = new GameClock(tickMs);
        this.world  = new World(input, clock);
        this.player = new Player("Village Square");
        world.setPlayer(player);

        // ---- Build the cast of NPCs ---------------------
        VillageElder elder    = new VillageElder();
        Merchant     merchant = new Merchant();
        LostChild    child    = new LostChild();
        CursedSpirit spirit   = new CursedSpirit();
        Guard        guard    = new Guard();
        // The Lost Child needs the world to know whether
        // a wander step takes her out of the player's scene.
        child.setWorld(world);

        world.addCharacter(elder);
        world.addCharacter(merchant);
        world.addCharacter(child);
        world.addCharacter(spirit);
        world.addCharacter(guard);

        // ---- The monster runs on its own thread ---------
        // (acts every 8 sec, INDEPENDENT of the 10-sec clock)
        monster = new Monster(world, 8_000);
        world.setMonster(monster);

        // ---- Place items in scenes ---------------------
        world.getScene("Abandoned House").addItem(new Apple());
        world.getScene("Abandoned House").addItem(new AncientScroll());
        world.getScene("Cave").addItem(new MagicKey());

        // ---- Register Observers with the clock ---------
        // (everything that changes "with time")
        clock.registerObserver(merchant);
        clock.registerObserver(child);
        clock.registerObserver(this);   // for the time-out check

        // NOTE: Items currently lying in scenes (the apple in
        // the abandoned house, the scroll, the magic key, ...)
        // are NOT yet registered with the clock.  They only
        // start ticking once the player picks them up
        // (see cmdTake below).  This way the apple does not
        // rot in the corner of the house while the player has
        // never even visited.
    }

    // Observer.update() - called every tick by GameClock.
    // Used here to check the "3 days have passed" loss condition.
    public synchronized void update() {
        if (clock.getDay() > MAX_DAYS && !gameOver) {
            gameOver   = true;
            playerWon  = false;
            endMessage = "Three nights have passed. The curse has consumed the village.";
            System.out.println("\n[" + endMessage + "]");
            System.out.println("(Press Enter to exit.)");
        }
    }

    // ===== Main game loop =====
    public void play() {
        printIntro();
        printScene();

        while (!gameOver && player.isAlive()) {
            System.out.print("\n> ");
            String line;
            try {
                line = input.nextLine().trim();
            } catch (Exception e) { break; }

            if (line.isEmpty()) continue;
            handleCommand(line);

            // Did the last command (or a parallel thread) kill us?
            if (!player.isAlive()) {
                gameOver   = true;
                playerWon  = false;
                endMessage = "You have died. The village remains cursed.";
            }
        }

        // ---- Default end message if none was set ----
        if (endMessage == null) {
            endMessage = "The story ends here.";
        }

        // ---- Clean shutdown ----
        clock.stop();
        if (monster != null) monster.stopRunning();

        // Wait for the threads to actually finish - the
        // using join() to wait for clean thread termination.
        try { clock.getThread().join();   } catch (InterruptedException ignored) {}
        try { monster.getThread().join(); } catch (InterruptedException ignored) {}

        System.out.println("\n========================================");
        if (playerWon) {
            System.out.println("                VICTORY!                ");
        } else {
            System.out.println("               GAME OVER                ");
        }
        System.out.println(endMessage);
        System.out.println("========================================");
    }

    // ===== Intro / scene printing =====
    private void printIntro() {
        System.out.println("============================================");
        System.out.println("           THE CURSED VILLAGE");
        System.out.println("============================================");
        System.out.println("You wake at the edge of a forgotten village.");
        System.out.println("A dark curse has trapped you here. Each night");
        System.out.println("the danger grows. You have three days to find");
        System.out.println("the relics and break the curse - or be lost");
        System.out.println("with the village forever.");
        System.out.println();
        System.out.println("Type 'help' for the list of commands.");
        System.out.println("============================================");
    }

    private void printScene() {
        Scene s = world.getCurrentScene();
        boolean night = clock.isNight();
        System.out.println("\n--- " + s.getName()
            + " (Day " + clock.getDay()
            + ", " + clock.getTimeOfDay() + ") ---");
        System.out.println(s.getDescription(night));

        // Items in scene
        ArrayList<Item> items = s.getItems();
        if (!items.isEmpty()) {
            System.out.print("Items here: ");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(items.get(i).getName());
            }
            System.out.println();
        }

        // Characters in scene (skip a dead monster)
        ArrayList<Character> chars = world.getCharactersAt(s.getName());
        ArrayList<Character> visible = new ArrayList<Character>();
        for (int i = 0; i < chars.size(); i++) {
            Character c = chars.get(i);
            if (c instanceof Monster && !((Monster) c).isAlive()) continue;
            visible.add(c);
        }
        if (!visible.isEmpty()) {
            System.out.print("Characters here: ");
            for (int i = 0; i < visible.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(visible.get(i).getName());
            }
            System.out.println();
        }

        // Exits
        ArrayList<String> exits = s.getExits();
        System.out.print("Exits: ");
        for (int i = 0; i < exits.size(); i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(exits.get(i));
        }
        System.out.println();
        System.out.println("HP: " + player.getHp() + "/" + player.getMaxHp()
                           + "   Gold: " + player.getGold());
    }

    // ===== Command parsing =====
    private void handleCommand(String line) {
        // Split into <verb> <rest>
        String cmd, arg;
        int sp = line.indexOf(' ');
        if (sp < 0) {
            cmd = line.toLowerCase();
            arg = "";
        } else {
            cmd = line.substring(0, sp).toLowerCase();
            arg = line.substring(sp + 1).trim();
        }

        if      (cmd.equals("look") || cmd.equals("l"))                 printScene();
        else if (cmd.equals("go")   || cmd.equals("move"))              cmdGo(arg);
        else if (cmd.equals("take") || cmd.equals("get") || cmd.equals("pick")) cmdTake(arg);
        else if (cmd.equals("drop"))                                    cmdDrop(arg);
        else if (cmd.equals("inventory") || cmd.equals("inv") || cmd.equals("i")) cmdInventory();
        else if (cmd.equals("talk") || cmd.equals("speak"))             cmdTalk(arg);
        else if (cmd.equals("attack") || cmd.equals("fight"))           cmdAttack(arg);
        else if (cmd.equals("use"))                                     cmdUse(arg);
        else if (cmd.equals("give"))                                    cmdGive(arg);
        else if (cmd.equals("light"))                                   cmdLight(arg);
        else if (cmd.equals("status") || cmd.equals("stats"))           cmdStatus();
        else if (cmd.equals("help")   || cmd.equals("h") || cmd.equals("?")) cmdHelp();
        else if (cmd.equals("quit")   || cmd.equals("exit")) {
            gameOver   = true;
            playerWon  = false;
            endMessage = "You fled the village. The curse remains.";
        }
        else System.out.println("I don't understand. Type 'help' for commands.");
    }

    private void cmdHelp() {
        System.out.println("Commands:");
        System.out.println("  look                 - look around the current scene");
        System.out.println("  go <scene>           - travel to a connected scene");
        System.out.println("  take <item>          - pick up an item from the scene");
        System.out.println("  drop <item>          - drop an item from your inventory");
        System.out.println("  inventory            - list what you are carrying");
        System.out.println("  status               - show your HP and gold");
        System.out.println("  talk <character>     - talk to a character here");
        System.out.println("  attack <character>   - attack a character (the monster)");
        System.out.println("  use <item>           - use an item from your inventory");
        System.out.println("  give <item> to <character> - give an item to a character");
        System.out.println("  light <item>         - light a torch");
        System.out.println("  quit                 - leave the village (forfeit)");
    }

    private void cmdGo(String dest) {
        if (dest.isEmpty()) { System.out.println("Go where?"); return; }
        // Allow lower-case and partial input by matching against exits
        Scene cur = world.getCurrentScene();
        String match = null;
        for (int i = 0; i < cur.getExits().size(); i++) {
            String e = cur.getExits().get(i);
            if (e.equalsIgnoreCase(dest) || e.toLowerCase().startsWith(dest.toLowerCase())) {
                match = e; break;
            }
        }
        if (match == null) {
            System.out.println("You can't go there from here.");
            return;
        }
        if (!world.canMoveTo(match)) {
            System.out.println("Something blocks the way.");
            return;
        }
        // ---- Forest at night without a lit torch is risky ---
        if (match.equals("Dark Forest") && clock.isNight()) {
            Item torch = player.findItem("Torch");
            boolean torchLit = (torch instanceof Torch) && ((Torch) torch).isLit();
            if (!torchLit) {
                System.out.println("\nYou stumble into the dark forest with no light.");
                System.out.println("Branches tear at you in the dark. (-15 HP)");
                player.takeDamage(15);
            }
        }
        player.setLocation(match);
        printScene();
    }

    private void cmdTake(String name) {
        if (name.isEmpty()) { System.out.println("Take what?"); return; }
        Scene s = world.getCurrentScene();
        Item it = s.findItem(name);
        if (it == null) { System.out.println("There's no '" + name + "' here."); return; }
        s.removeItem(it);
        player.addToInventory(it);
        // The item is now in the player's inventory.  If it is
        // a time-aware Observer (Apple, Torch, ...) start
        // notifying it of clock ticks so it can decay.
        if (it instanceof Observer) {
            clock.registerObserver((Observer) it);
        }
        System.out.println("You take the " + it.getName() + ".");
    }

    private void cmdDrop(String name) {
        if (name.isEmpty()) { System.out.println("Drop what?"); return; }
        Item it = player.findItem(name);
        if (it == null) { System.out.println("You aren't carrying that."); return; }
        player.removeFromInventory(it);
        // Stop time-aware items decaying once they leave the
        // player's hands - mirrors the rule above.
        if (it instanceof Observer) {
            clock.removeObserver((Observer) it);
        }
        world.getCurrentScene().addItem(it);
        System.out.println("You drop the " + it.getName() + ".");
    }

    private void cmdInventory() {
        ArrayList<Item> inv = player.getInventory();
        if (inv.isEmpty()) {
            System.out.println("You are carrying nothing.");
            return;
        }
        System.out.println("You are carrying:");
        for (int i = 0; i < inv.size(); i++) {
            System.out.println("  - " + inv.get(i).describe());
        }
    }

    private void cmdStatus() {
        System.out.println("HP: " + player.getHp() + "/" + player.getMaxHp()
                           + "   Gold: " + player.getGold()
                           + "   Day " + clock.getDay()
                           + " (" + clock.getTimeOfDay() + ")");
    }

    private void cmdTalk(String name) {
        if (name.isEmpty()) { System.out.println("Talk to whom?"); return; }
        Character c = findCharacterHere(name);
        if (c == null) {
            System.out.println("There's no one called '" + name + "' here.");
            return;
        }
        // Template-method call:
        c.interact(player, world);
    }

    private void cmdAttack(String name) {
        if (name.isEmpty()) { System.out.println("Attack whom?"); return; }
        Character c = findCharacterHere(name);
        if (c == null) {
            System.out.println("There's no '" + name + "' here.");
            return;
        }
        if (!(c instanceof Monster)) {
            System.out.println("Attacking " + c.getName()
                + " would only make things worse. (Refused.)");
            return;
        }
        Monster m = (Monster) c;
        if (!m.isAlive()) { System.out.println("It's already dead."); return; }
        // Player swings sword
        if (!player.hasSword()) {
            System.out.println("You strike with your bare hands for 5 damage.");
            m.takeDamage(5);
        } else {
            Sword sw = (Sword) player.findItem("Sword");
            int dmg = sw.getDamage();
            System.out.println("You slash the monster for " + dmg + " damage.");
            m.takeDamage(dmg);
        }
        // Monster strikes back if still alive
        if (m.isAlive()) {
            int back = m.getBehavior().getDamage();
            System.out.println("The monster bites back for " + back + " damage.");
            player.takeDamage(back);
        }
    }

    private void cmdUse(String name) {
        if (name.isEmpty()) { System.out.println("Use what?"); return; }
        Item it = player.findItem(name);
        if (it == null) { System.out.println("You aren't carrying that."); return; }

        if (it instanceof HealingPotion) {
            HealingPotion hp = (HealingPotion) it;
            int before = player.getHp();
            player.heal(hp.getHealAmount());
            int after = player.getHp();
            player.removeFromInventory(it);
            System.out.println("You drink the potion. (+" + (after - before) + " HP)");
            return;
        }
        if (it instanceof AncientScroll) {
            // Only meaningful at the chamber's altar
            String here = player.getLocation();
            if (here.equals("Hidden Underground Chamber")) {
                System.out.println("You unfurl the ancient scroll on the altar...");
                System.out.println("The runes burn bright. The walls of the village shake.");
                System.out.println("The cursed spirit screams - and is silenced.");
                System.out.println("Sunlight pours into the chamber.");
                gameOver   = true;
                playerWon  = true;
                endMessage = "The curse is broken. The village is saved.";
            } else {
                System.out.println("Reading the scroll here would be wasted. The altar in the chamber is the only place its words can take effect.");
            }
            return;
        }
        if (it instanceof Torch) {
            cmdLight("torch");
            return;
        }
        System.out.println("You can't think of a way to use that.");
    }

    private void cmdLight(String name) {
        if (name.isEmpty()) { System.out.println("Light what?"); return; }
        Item it = player.findItem(name);
        if (!(it instanceof Torch)) {
            System.out.println("That isn't something you can light.");
            return;
        }
        Torch t = (Torch) it;
        if (t.getFuel() <= 0) {
            System.out.println("The torch is burned out. You need a new one.");
            return;
        }
        if (t.isLit()) {
            System.out.println("The torch is already lit.");
            return;
        }
        t.light();
        System.out.println("You light the torch. It will burn for "
                           + t.getFuel() + " ticks.");
    }

    // give <item> to <character>
    private void cmdGive(String arg) {
        // accept "give X to Y" or "give X Y"
        String itemName, who;
        int idx = arg.toLowerCase().indexOf(" to ");
        if (idx >= 0) {
            itemName = arg.substring(0, idx).trim();
            who      = arg.substring(idx + 4).trim();
        } else {
            int sp = arg.indexOf(' ');
            if (sp < 0) { System.out.println("Give what to whom?"); return; }
            itemName = arg.substring(0, sp).trim();
            who      = arg.substring(sp + 1).trim();
        }
        Item it = player.findItem(itemName);
        if (it == null) { System.out.println("You aren't carrying " + itemName + "."); return; }
        Character c = findCharacterHere(who);
        if (c == null) { System.out.println("No '" + who + "' is here."); return; }
        // The simplest way is to "talk" - the character's
        // performAction will see what's in the inventory.
        // But for the elder we want to deliver the apple
        // explicitly: just route to interact() and let the
        // elder handle it.
        c.interact(player, world);
    }

    // Helper: look up an NPC in the current scene by name
    // (case-insensitive, accepts a prefix so "elder" works
    // for "Village Elder").
    private Character findCharacterHere(String name) {
        ArrayList<Character> here =
            world.getCharactersAt(player.getLocation());
        // exact match first
        for (int i = 0; i < here.size(); i++) {
            if (here.get(i).getName().equalsIgnoreCase(name)) return here.get(i);
        }
        // prefix / substring match
        String want = name.toLowerCase();
        for (int i = 0; i < here.size(); i++) {
            String n = here.get(i).getName().toLowerCase();
            if (n.contains(want)) {
                // exclude a dead monster
                Character c = here.get(i);
                if (c instanceof Monster && !((Monster) c).isAlive()) continue;
                return c;
            }
        }
        return null;
    }
}
