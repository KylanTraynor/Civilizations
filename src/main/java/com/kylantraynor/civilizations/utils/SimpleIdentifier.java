package com.kylantraynor.civilizations.utils;

import org.apache.commons.lang.ObjectUtils;

import java.util.UUID;

public class SimpleIdentifier implements Identifier {
    private final UUID id;

    public SimpleIdentifier(UUID id){
        this.id = id;
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public int size(){
        return 1;
    }

    @Override
    public UUID get(int n){
        if(n != 0) throw new IllegalArgumentException();
        return id;
    }

    @Override
    public UUID[] getArray() {
        return new UUID[]{id};
    }

    @Override
    public boolean equals(Identifier o) {
        return o instanceof SimpleIdentifier && this.id.equals(((SimpleIdentifier) o).id);
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
        if(o instanceof SimpleIdentifier){
            return id.compareTo(((SimpleIdentifier) o).id);
        } else {
            int result = id.compareTo(get(0));
            if(result == 0 && o.size() > 1) return -1;
            return result;
        }
    }

    @Override
    public String toString(){
        return id.toString();
    }

    public static SimpleIdentifier parse(String s){
        return new SimpleIdentifier(UUID.fromString(s));
    }

    public static SimpleIdentifier random(){
        return new SimpleIdentifier(UUID.randomUUID());
    }
}
