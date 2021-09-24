package com.crossroadsinn.datatypes;

import java.util.Collection;
import java.util.HashSet;

public class HashBlockingSet<T> extends HashSet<T> {

    private final int maxCapacity;

    public HashBlockingSet(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean addAll(Collection<? extends T> c) {
        return super.addAll(c);
    }
}
