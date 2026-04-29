// MagicKey.java
// One of the three relics required to enter the Hidden
// Underground Chamber.  Static item, no special behavior.
public class MagicKey extends Item {
    public MagicKey() {
        super("Magic Key");
    }

    public String describe() {
        return "Magic Key (opens the underground chamber)";
    }
}
