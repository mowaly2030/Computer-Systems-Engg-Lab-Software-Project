// PassiveBehavior.java
// Concrete Strategy.  The monster lurks in its current scene
// and does not move or attack.  This is the daytime default.
public class PassiveBehavior implements MonsterBehavior {

    public void act(Monster m, World w) {
        // Lurking - nothing to do.
        // (Even if the player walks into the monster's scene
        //  during the day, the monster does not initiate combat.)
    }

    public String describe() { return "lurking quietly"; }
    public int    getDamage(){ return 5;  }
}
