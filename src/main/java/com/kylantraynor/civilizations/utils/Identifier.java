package com.kylantraynor.civilizations.utils;

import java.util.UUID;

public interface Identifier extends Comparable<Identifier> {
    int size();
    UUID[] getArray();
    UUID get(int n);
    boolean equals(Identifier o);
}
