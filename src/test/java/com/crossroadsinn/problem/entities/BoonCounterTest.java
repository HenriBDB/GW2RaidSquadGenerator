package com.crossroadsinn.problem.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoonCounterTest {

    @Test
    public void addNonExistantBoon() {
        BoonCounter sut = new BoonCounter();

        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);

        Assertions.assertEquals(5, sut.getCount(BoonCounter.Boon.ALACRITY));
    }

    @Test
    public void addExistantBoon() {
        BoonCounter sut = new BoonCounter();

        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.addBoon(BoonCounter.Boon.ALACRITY, 2);

        Assertions.assertEquals(7, sut.getCount(BoonCounter.Boon.ALACRITY));
    }

    @Test
    public void removeNonExistantBoon() {
        BoonCounter sut = new BoonCounter();

        sut.removeBoon(BoonCounter.Boon.ALACRITY, 5);

        Assertions.assertEquals(0, sut.getCount(BoonCounter.Boon.ALACRITY));
    }

    @Test
    public void reduceExistantBoon() {
        BoonCounter sut = new BoonCounter();

        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.removeBoon(BoonCounter.Boon.ALACRITY, 2);

        Assertions.assertEquals(3, sut.getCount(BoonCounter.Boon.ALACRITY));
    }

    @Test
    public void removeExistantBoon() {
        BoonCounter sut = new BoonCounter();

        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.removeBoon(BoonCounter.Boon.ALACRITY, 5);

        Assertions.assertEquals(0, sut.getCount(BoonCounter.Boon.ALACRITY));
    }

    @Test
    public void addBoonCounterNoCommonBoons() {
        BoonCounter sut = new BoonCounter();
        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.addBoon(BoonCounter.Boon.MIGHT, 20);
        BoonCounter other = new BoonCounter();
        other.addBoon(BoonCounter.Boon.QUICKNESS, 3);

        sut.addBoonCounter(other);

        Assertions.assertEquals(5, sut.getCount(BoonCounter.Boon.ALACRITY));
        Assertions.assertEquals(20, sut.getCount(BoonCounter.Boon.MIGHT));
        Assertions.assertEquals(3, sut.getCount(BoonCounter.Boon.QUICKNESS));
    }

    @Test
    public void addBoonCounterWithCommonBoons() {
        BoonCounter sut = new BoonCounter();
        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.addBoon(BoonCounter.Boon.MIGHT, 20);
        BoonCounter other = new BoonCounter();
        other.addBoon(BoonCounter.Boon.ALACRITY, 4);
        other.addBoon(BoonCounter.Boon.QUICKNESS, 3);
        other.addBoon(BoonCounter.Boon.MIGHT, 5);

        sut.addBoonCounter(other);

        Assertions.assertEquals(9, sut.getCount(BoonCounter.Boon.ALACRITY));
        Assertions.assertEquals(25, sut.getCount(BoonCounter.Boon.MIGHT));
        Assertions.assertEquals(3, sut.getCount(BoonCounter.Boon.QUICKNESS));
    }

    @Test
    public void containsAllBoonsTrue() {
        BoonCounter sut = new BoonCounter();
        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.addBoon(BoonCounter.Boon.MIGHT, 20);
        sut.addBoon(BoonCounter.Boon.QUICKNESS, 3);
        BoonCounter other = new BoonCounter();
        other.addBoon(BoonCounter.Boon.ALACRITY, 4);
        other.addBoon(BoonCounter.Boon.MIGHT, 20);

        Assertions.assertTrue(sut.containsAllBoons(other));
    }

    @Test
    public void containsAllBoonsFalse() {
        BoonCounter sut = new BoonCounter();
        sut.addBoon(BoonCounter.Boon.ALACRITY, 5);
        sut.addBoon(BoonCounter.Boon.MIGHT, 20);
        sut.addBoon(BoonCounter.Boon.QUICKNESS, 3);
        BoonCounter other = new BoonCounter();
        other.addBoon(BoonCounter.Boon.ALACRITY, 6);
        other.addBoon(BoonCounter.Boon.MIGHT, 20);

        Assertions.assertFalse(sut.containsAllBoons(other));
    }
}
