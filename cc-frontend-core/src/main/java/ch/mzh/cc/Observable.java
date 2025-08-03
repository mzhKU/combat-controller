package ch.mzh.cc;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
}
