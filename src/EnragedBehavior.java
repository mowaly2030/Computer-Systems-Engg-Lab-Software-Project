// EnragedBehavior.java
// Concrete Strategy.  Triggered when the monster senses the
// player is weak (HP below 30).  The monster teleports to
// the player's scene and deals heavier damage - a classic
// "smell blood" mechanic.
public class EnragedBehavior implements MonsterBehavior {

    public void act(Monster m, World w) {
        String playerLoc  = w.getPlayer().getLocation();
        String monsterLoc = m.getLocation();

        if (monsterLoc.equals(playerLoc)) {
            int dmg = getDamage();
            System.out.println("\n[The ENRAGED monster slashes you for "
                               + dmg + " damage!]");
            w.getPlayer().takeDamage(dmg);
        } else {
            // Chase relentlessly - jump straight to the
            // player's location.
            m.setLocation(playerLoc);
            System.out.println("\n[You hear heavy footsteps approach...]");
        }
    }

    public String describe() { return "ENRAGED, hunting you"; }
    public int    getDamage(){ return 35; }
}
