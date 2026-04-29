// Merchant.java
// Concrete Character (Template Method) AND Observer of the
// GameClock.  Every 3 ticks the Merchant restocks and her
// prices fluctuate, so what she sells changes over time.
import java.util.ArrayList;

public class Merchant extends Character implements Observer {
    private ArrayList<Item> stock;
    private int torchPrice;
    private int potionPrice;
    private int swordPrice;
    private int tickCounter;

    public Merchant() {
        super("Merchant", "Village Square");
        this.stock        = new ArrayList<Item>();
        this.torchPrice   = 10;
        this.potionPrice  = 20;
        this.swordPrice   = 25;
        this.tickCounter  = 0;
        restock();
    }

    private synchronized void restock() {
        stock.clear();
        stock.add(new Torch());
        stock.add(new HealingPotion());
        // The sword is a rarer item - only sometimes in stock
        if (Math.random() < 0.5) stock.add(new Sword());
    }

    // Called by GameClock every tick (Observer pattern).
    public synchronized void update() {
        tickCounter++;
        if (tickCounter % 3 == 0) {
            restock();
            // jitter prices by +/-5 (but never below a floor)
            torchPrice  = Math.max(5,  torchPrice  + (Math.random() < 0.5 ? -5 : 5));
            potionPrice = Math.max(10, potionPrice + (Math.random() < 0.5 ? -5 : 5));
            swordPrice  = Math.max(15, swordPrice  + (Math.random() < 0.5 ? -5 : 5));
        }
    }

    private synchronized int priceOf(Item it) {
        if (it instanceof Torch)         return torchPrice;
        if (it instanceof HealingPotion) return potionPrice;
        if (it instanceof Sword)         return swordPrice;
        return 5;
    }

    protected void showAppearance(Player p, World w) {
        System.out.println("A weathered merchant stands behind a small table of goods.");
    }

    // The whole shopping menu is one synchronized block on the
    // merchant herself - while the player is choosing an item,
    // the clock thread cannot restock under their feet.
    protected void performAction(Player p, World w) {
        synchronized (this) {
            if (stock.isEmpty()) {
                System.out.println("Merchant: \"Sold out, friend. Come back later.\"");
                return;
            }
            System.out.println("Merchant: \"Welcome! You have " + p.getGold() + " gold.\"");
            System.out.println("Today's wares:");
            for (int i = 0; i < stock.size(); i++) {
                Item it = stock.get(i);
                System.out.println("  " + (i + 1) + ". " + it.describe()
                                   + " - " + priceOf(it) + " gold");
            }
            System.out.println("  0. Leave");
            System.out.print("Choose: ");

            String line = w.getInput().nextLine().trim();
            int choice = -1;
            try { choice = Integer.parseInt(line); } catch (Exception e) { choice = -1; }

            if (choice == 0) {
                System.out.println("Merchant: \"Safe travels, then.\"");
                return;
            }
            if (choice < 1 || choice > stock.size()) {
                System.out.println("Merchant: \"That's not on the table.\"");
                return;
            }

            Item bought = stock.get(choice - 1);
            int   price = priceOf(bought);
            if (p.getGold() < price) {
                System.out.println("Merchant: \"Your purse is too light, traveler.\"");
                return;
            }
            p.spendGold(price);
            p.addToInventory(bought);
            stock.remove(bought);
            // Newly bought time-aware items must be observers
            // of the clock so a torch you bought starts burning
            // when you light it, etc.
            if (bought instanceof Observer) {
                w.getClock().registerObserver((Observer) bought);
            }
            System.out.println("You bought a " + bought.getName() + ".");
        }
    }
}
