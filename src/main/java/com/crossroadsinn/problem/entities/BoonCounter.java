package com.crossroadsinn.problem.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple boon counter which can be used to keep track of boon requirements.
 *
 * @author Eren Bole
 */
public class BoonCounter {

    Map<Boon, Integer> counter;

    public BoonCounter() {
        counter = new HashMap<>();
    }

    public BoonCounter(BoonCounter copy) {
        counter = new HashMap<>(copy.counter);
    }

    public void addBoon(Boon boon, int addition) {
        int currentCount = counter.getOrDefault(boon, 0);
        counter.put(boon, currentCount+addition);
    }

    public void removeBoon(Boon boon, int subtraction) {
        if (!counter.containsKey(boon)) return; // Ignore if boon not present
        int newCount = counter.getOrDefault(boon, 0)-subtraction;
        if (newCount <= 0) counter.remove(boon);
        else counter.put(boon, newCount);
    }

    public void addBoonCounter(BoonCounter boonCounter) {
        boonCounter.counter.forEach(this::addBoon);
    }

    public boolean containsAllBoons(BoonCounter other) {
        return other.counter.entrySet().stream().allMatch(
                e -> counter.get(e.getKey()) >= e.getValue());
    }

    public int getCount(Boon boon) {
        return counter.getOrDefault(boon, 0);
    }

    public boolean isEmpty() {
        if (counter.isEmpty()) return true;
        return counter.entrySet().stream().allMatch(e -> e.getValue() == 0);
    }

    public enum Boon {
        MIGHT, ALACRITY, QUICKNESS
    }
}
