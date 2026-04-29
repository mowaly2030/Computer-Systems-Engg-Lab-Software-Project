// MonsterBehavior.java
// =========================================================
// STRATEGY DESIGN PATTERN
// =========================================================
// The Forest Monster's behavior changes at runtime based on:
//   - the time of day (passive during day, aggressive at night)
//   - how wounded the player is (enraged when player is weak)
//
// Instead of stuffing all of this into a single Monster class
// with big if-else chains, we encapsulate each behavior as
// its own class implementing this interface.  At runtime the
// Monster just swaps in the appropriate behavior, exactly
// like a generic Strategy interface where the algorithm can
// be swapped at runtime.
public interface MonsterBehavior {
    // What the monster does this turn (move, attack, etc.)
    void act(Monster m, World w);

    // Short label printed when the player looks at the monster
    String describe();

    // Damage dealt when this behavior attacks
    int getDamage();
}
