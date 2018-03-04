package com.kylantraynor.civilizations.utils;

import java.util.Objects;
import java.util.UUID;

public class DoubleIdentifier implements Identifier {

    private final UUID[] ids;
    private final int hash;

    public DoubleIdentifier(UUID id1, UUID id2){
        ids = new UUID[2];
        ids[0] = id1;
        ids[1] = id2;
        hash = Objects.hash(ids[0], ids[1]);
    }

    @Override
    public int size(){
        return 2;
    }

    @Override
    public int hashCode(){
        return hash;
    }

    @Override
    public UUID get(int n){
        switch(n){
            case 0: case 1:
                return ids[n];
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public UUID[] getArray() {
        return ids.clone();
    }

    @Override
    public boolean equals(Identifier o) {
        return o instanceof DoubleIdentifier &&
                ids[0].equals(((DoubleIdentifier) o).ids[0]) &&
                ids[1].equals(((DoubleIdentifier) o).ids[1]);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Identifier o) {
        if(o == null) throw new NullPointerException();
        int result = 0;
        for(int i = 0; i < 2 && i < o.size(); i++){
            if(result != 0) return result;
            result = ids[i].compareTo(o.get(i));
        }
        if(result == 0 && o.size() < 2) return 1;
        if(result == 0 && o.size() > 2) return -1;
        return result;
    }

    @Override
    public String toString(){
        return ids[0].toString() + ":" + ids[1].toString();
    }

    public static DoubleIdentifier parse(String s){
        String[] ar = s.split(":");
        return new DoubleIdentifier(UUID.fromString(ar[0]), UUID.fromString(ar[1]));
    }

    public static DoubleIdentifier parse(String s1, String s2){
        return new DoubleIdentifier(UUID.fromString(s1), UUID.fromString(s2));
    }
}
