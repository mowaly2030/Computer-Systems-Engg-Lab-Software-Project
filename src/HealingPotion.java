// HealingPotion.java
// One-shot consumable that restores HP when used.
public class HealingPotion extends Item {
    private int healAmount;

    public HealingPotion() {
        super("Healing Potion");
        this.healAmount = 50;
    }

    public int getHealAmount() { return healAmount; }

    public String describe() {
        return "Healing Potion (restores " + healAmount + " HP)";
    }
}
