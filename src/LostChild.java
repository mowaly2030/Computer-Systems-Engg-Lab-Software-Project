// LostChild.java
// Concrete Character (Template Method) AND Observer.  Every
// clock tick the child has a chance to wander to a new scene,
// so the player has to find her.  When found she rewards the
// player with gold (covers an early purchase from the merchant).
public class LostChild extends Character implements Observer {
    private boolean rewardGiven;
    private String[] possibleScenes = {
        "Village Square", "Abandoned House", "Dark Forest",
        "Cave",           "Old Church"
    };
    private World world;   // set by Game so update() can see player location

    public LostChild() {
        super("Lost Child", "Abandoned House");
        this.rewardGiven = false;
    }

    // Game calls this right after construction so the child
    // can tell whether the player can see her when she moves.
    public void setWorld(World w) { this.world = w; }

    // Observer.update(): chance to relocate every tick.
    public synchronized void update() {
        if (Math.random() < 0.4) {
            int idx = (int) (Math.random() * possibleScenes.length);
            String newLoc = possibleScenes[idx];
            String oldLoc = getLocation();

            // If the child is leaving the scene the player is
            // standing in, let the player hear it - otherwise
            // a wandering child is invisible and you find
            // yourself "talking" to no one.
            if (world != null
                && oldLoc.equals(world.getPlayer().getLocation())
                && !newLoc.equals(oldLoc)) {
                System.out.println("\n[The Lost Child slips away into the shadows.]");
            }
            setLocation(newLoc);
        }
    }

    protected void showAppearance(Player p, World w) {
        System.out.println("A small child huddles in the shadows, clutching a torn doll.");
    }

    protected void performAction(Player p, World w) {
        if (!rewardGiven) {
            System.out.println("Child: \"You found me! Thank you, stranger.\"");
            System.out.println("Child: \"Mama said the spirit hides where the chamber sleeps.\"");
            System.out.println("Child: \"Here, take this - I found it in the cave.\"");
            p.addGold(20);
            System.out.println("(You receive 20 gold.)");
            rewardGiven = true;
        } else {
            System.out.println("Child: \"I want to go home...\"");
        }
    }
}
