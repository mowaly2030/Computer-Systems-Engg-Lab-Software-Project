// Subject.java
// Part of the Observer Design Pattern.
// A Subject generates data/events and notifies its observers.
public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
