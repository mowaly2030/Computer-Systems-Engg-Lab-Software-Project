// CursedSpirit.java
// Concrete Character (Template Method).  Behavior depends on
// what the player is carrying.
//
//   - Player has Charm     -> spirit is calm, lets them pass
//   - Player has Scroll
//     but NO Charm         -> spirit is enraged (heavy damage)
//   - Player has neither   -> spirit warns and pushes back
public class CursedSpirit extends Character {
    public CursedSpirit() {
        super("Cursed Spirit", "Old Church");
    }

    protected void showAppearance(Player p, World w) {
        if (p.hasCharm()) {
            System.out.println("A pale spirit drifts above the altar, calmed by your charm.");
        } else if (p.hasScroll()) {
            System.out.println("The spirit SCREAMS at the sight of the scroll in your hands!");
        } else {
            System.out.println("A pale spirit drifts above the altar, watching you suspiciously.");
        }
    }

    protected void performAction(Player p, World w) {
        if (p.hasCharm()) {
            System.out.println("Spirit: \"You carry the elder's blessing. Pass, mortal.\"");
        } else if (p.hasScroll()) {
            System.out.println("Spirit: \"YOU DARE BRING THAT HERE WITHOUT PROTECTION!\"");
            System.out.println("The spirit lashes at you for 30 damage!");
            p.takeDamage(30);
        } else {
            System.out.println("Spirit: \"Leave this place. The curse is not yours to bear.\"");
            System.out.println("A cold wind shoves you back. You take 10 damage.");
            p.takeDamage(10);
        }
    }
}
