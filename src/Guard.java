// Guard.java
// Concrete Character (Template Method).  Blocks the entrance
// to the Hidden Underground Chamber until the player is
// carrying ALL three relics: Charm, Scroll and Magic Key.
//
// When the conditions are met the guard tells the World to
// unlock the chamber.
public class Guard extends Character {
    public Guard() {
        super("Guard", "Old Church");
    }

    protected void showAppearance(Player p, World w) {
        System.out.println("A stern guard stands before a stairway leading down into darkness.");
    }

    protected void performAction(Player p, World w) {
        boolean hasKey    = p.hasKey();
        boolean hasScroll = p.hasScroll();
        boolean hasCharm  = p.hasCharm();

        if (hasKey && hasScroll && hasCharm) {
            System.out.println("Guard: \"You carry all three relics. The chamber awaits.\"");
            System.out.println("(The path to the Hidden Underground Chamber is now open.)");
            w.unlockChamber();
        } else {
            System.out.println("Guard: \"None shall pass without the three relics - Charm, Scroll, and Key.\"");
            System.out.print("You still need:");
            if (!hasCharm)  System.out.print(" Charm");
            if (!hasScroll) System.out.print(" Scroll");
            if (!hasKey)    System.out.print(" Key");
            System.out.println(".");
        }
    }
}
