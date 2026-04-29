// Sword.java
// Static item: deals damage when the player attacks the monster.
public class Sword extends Item {
    private int damage;

    public Sword() {
        super("Sword");
        this.damage = 25;
    }

    public int getDamage() { return damage; }

    public String describe() {
        return "Sword (deals " + damage + " damage)";
    }
}
