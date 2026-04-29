// ConcreteSubject.java
// Part of the Observer Design Pattern.
// Generic base class that maintains a list of observers and
// notifies all of them when notifyObservers() is called.
// We extend this for any subject (e.g., GameClock).
import java.util.ArrayList;

public class ConcreteSubject implements Subject {
    private ArrayList<Observer> observers;

    public ConcreteSubject() {
        observers = new ArrayList<Observer>();
    }

    // synchronized so threads can safely register/remove
    public synchronized void registerObserver(Observer o) {
        observers.add(o);
    }

    public synchronized void removeObserver(Observer o) {
        observers.remove(o);
    }

    public synchronized void notifyObservers() {
        // Take a snapshot before iterating so observers can safely
        // remove themselves (e.g., a torch that just burned out)
        // without breaking the loop.
        ArrayList<Observer> snapshot = new ArrayList<Observer>(observers);
        for (int i = 0; i < snapshot.size(); i++) {
            snapshot.get(i).update();
        }
    }
}
