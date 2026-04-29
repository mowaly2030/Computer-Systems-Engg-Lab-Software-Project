// AggressiveBehavior.java
// Concrete Strategy used at night (or when the monster
// otherwise becomes hostile).  The monster moves toward
// the player and attacks if they are in the same scene.
public class AggressiveBehavior implements MonsterBehavior {

    public void act(Monster m, World w) {
        String playerLoc  = w.getPlayer().getLocation();
        String monsterLoc = m.getLocation();

        if (monsterLoc.equals(playerLoc)) {
            // Same scene - attack.
            int dmg = getDamage();
            System.out.println("\n[The Forest Monster lunges at you and bites for "
                               + dmg + " damage!]");
            w.getPlayer().takeDamage(dmg);
        } else {
            // Different scene - hunt.  Move along the
            // monster's small territory toward the village.
            if (monsterLoc.equals("Cave")) {
                m.setLocation("Dark Forest");
            } else if (monsterLoc.equals("Dark Forest")) {
                // 30% chance the monster ventures into the village
                if (Math.random() < 0.3) m.setLocation("Village Square");
                else                     m.setLocation("Cave");
            } else {
                // Anywhere else: retreat to the forest
                m.setLocation("Dark Forest");
            }
        }
    }

    public String describe() { return "stalking aggressively"; }
    public int    getDamage(){ return 20; }
}
