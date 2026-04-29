// Scene.java
// A single location in the game world.  Holds a list of
// items currently in the scene and the names of the scenes
// reachable from it.  Characters are NOT stored inside
// Scene; instead each Character knows which scene it is in.
import java.util.ArrayList;

public class Scene {
    private String name;
    private String dayDescription;
    private String nightDescription;
    private ArrayList<Item>   items;
    private ArrayList<String> exits;
    private boolean accessible;     // false until unlocked

    public Scene(String name, String dayDesc, String nightDesc) {
        this.name             = name;
        this.dayDescription   = dayDesc;
        this.nightDescription = nightDesc;
        this.items            = new ArrayList<Item>();
        this.exits            = new ArrayList<String>();
        this.accessible       = true;
    }

    public String getName() { return name; }
    public String getDescription(boolean night) {
        return night ? nightDescription : dayDescription;
    }

    public synchronized void addItem(Item it)    { items.add(it); }
    public synchronized void removeItem(Item it) { items.remove(it); }
    public synchronized ArrayList<Item> getItems() {
        return new ArrayList<Item>(items);  // defensive copy
    }
    public synchronized Item findItem(String n) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equalsIgnoreCase(n)) return items.get(i);
        }
        return null;
    }

    public void addExit(String sceneName)  { exits.add(sceneName); }
    public ArrayList<String> getExits()    { return exits; }

    public void    setAccessible(boolean a){ this.accessible = a; }
    public boolean isAccessible()          { return accessible; }
}
