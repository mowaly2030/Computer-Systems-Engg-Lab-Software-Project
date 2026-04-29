// VillageElder.java
// Concrete Character (Template Method).  Sleeps at night
// (canInteract overridden), and gives the Protective Charm
// to the player after they bring him a fresh apple.
public class VillageElder extends Character {
    private boolean trusted;

    public VillageElder() {
        super("Village Elder", "Village Square");
        this.trusted = false;
    }

    // HOOK override: elder is asleep at night
    protected boolean canInteract(Player p, World w) {
        return !w.getClock().isNight();
    }

    protected void describeBlocked(Player p, World w) {
        System.out.println("The Village Elder is asleep. Come back during the day.");
    }

    protected void showAppearance(Player p, World w) {
        System.out.println("An old man with weary eyes sits on a wooden bench.");
    }

    protected void performAction(Player p, World w) {
        if (!trusted) {
            Apple apple = p.findFreshApple();
            if (apple != null) {
                System.out.println("Elder: \"You bring me food in these dark times. Bless you.\"");
                System.out.println("Elder: \"Take this charm. It will calm the cursed spirit in the church.\"");
                p.removeFromInventory(apple);
                // Apple was consumed -- stop the clock from
                // notifying it (otherwise it would keep ticking
                // and eventually print "an apple has rotted",
                // even though the elder already ate it).
                w.getClock().removeObserver(apple);
                p.addToInventory(new ProtectiveCharm());
                trusted = true;
            } else {
                System.out.println("Elder: \"Stranger, our village is cursed.\"");
                System.out.println("Elder: \"If you wish my help, bring me a fresh apple. Hunger weakens me.\"");
                System.out.println("Elder: \"You may find one in the abandoned house up the road.\"");
            }
        } else {
            System.out.println("Elder: \"You have all you need, friend.\"");
            System.out.println("Elder: \"The scroll lies in the abandoned house. The key is hidden in the cave beyond the forest.\"");
            System.out.println("Elder: \"Take all three to the guard at the church. The chamber is the only place the curse can be broken.\"");
        }
    }
}
