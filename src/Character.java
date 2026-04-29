// Character.java
// =========================================================
// TEMPLATE METHOD DESIGN PATTERN
// =========================================================
// Every interactive character follows the SAME recipe when
// the player interacts with them:
//
//      1. canInteract()         (hook  - default true)
//      2. describeBlocked()     (hook  - default message)
//      3. showAppearance()      (abstract - each is unique)
//      4. performAction()       (abstract - each is unique)
//      5. afterInteraction()    (hook  - default no-op)
//
// The recipe is implemented in interact(), which is `final`
// so subclasses CANNOT change the order of the steps -
// exactly the behavior the Template Method pattern is
// designed to enforce.
//
// Concrete subclasses (VillageElder, Merchant, LostChild,
// CursedSpirit, Guard, Monster) only override the steps
// that differ between them.
public abstract class Character {
    protected String name;
    protected String location;   // name of the scene the character is in

    public Character(String name, String startLocation) {
        this.name     = name;
        this.location = startLocation;
    }

    public String getName() { return name; }

    // synchronized because Monster's location can be changed
    // by its own thread while the main thread is reading it.
    public synchronized String getLocation()         { return location; }
    public synchronized void   setLocation(String l) { this.location = l; }

    // ----- TEMPLATE METHOD ---------------------------------
    // final = subclasses cannot break the recipe
    public final void interact(Player p, World w) {
        if (!canInteract(p, w)) {
            describeBlocked(p, w);
            return;
        }
        showAppearance(p, w);
        performAction(p, w);
        afterInteraction(p, w);
    }
    // -------------------------------------------------------

    // ----- HOOKS (default implementations) -----------------
    protected boolean canInteract(Player p, World w) { return true; }

    protected void describeBlocked(Player p, World w) {
        System.out.println(name + " ignores you.");
    }

    protected void afterInteraction(Player p, World w) { /* no-op */ }
    // -------------------------------------------------------

    // ----- PRIMITIVE OPERATIONS (must be overridden) -------
    protected abstract void showAppearance(Player p, World w);
    protected abstract void performAction(Player p, World w);
    // -------------------------------------------------------
}
