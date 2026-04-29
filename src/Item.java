// Item.java
// Base class for everything that can be picked up,
// carried in inventory, dropped, or used.
// Concrete items extend this and override describe()
// when their description depends on state (e.g., a Torch
// describes itself differently when lit vs. burned out).
public abstract class Item {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName()       { return name; }
    public String describe()      { return name; }
    public String toString()      { return name; }
}
