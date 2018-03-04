package com.kylantraynor.civilizations.utils;

import java.util.UUID;

public interface Identifier extends Comparable<Identifier> {
    public int size();
    public UUID[] getArray();
    public UUID get(int n);
    public boolean equals(Identifier o);
}
