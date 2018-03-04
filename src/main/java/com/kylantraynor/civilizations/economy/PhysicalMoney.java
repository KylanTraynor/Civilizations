package com.kylantraynor.civilizations.economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhysicalMoney implements Comparable<PhysicalMoney>{

    private final Currency currency;
    private final ItemStack is;

    public PhysicalMoney(Currency currency, Material type){
        this.currency = currency;
        this.is = new ItemStack(type);
        refreshItemMeta();
    }

    public PhysicalMoney(Currency currency, Material type, int amount){
        this(currency,type);
        this.is.setAmount(amount);
    }

    public PhysicalMoney(Currency currency, ItemStack is){
        this.currency = currency;
        this.is = is;
        refreshItemMeta();
    }

    private void refreshItemMeta(){
        ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(getName());
        im.setLore(new ArrayList<>(Arrays.asList("Currency: " + currency.getShortName())));
        this.is.setItemMeta(im);
    }

    public Currency getCurrency(){
        return currency;
    }

    public String getName(){
        switch(this.is.getType()){
            case GOLD_NUGGET:
                return ChatColor.RESET + "Golden " + currency.getName();
            case IRON_NUGGET:
                return ChatColor.RESET + "Iron " + currency.getName();
        }
        return this.is.getItemMeta().getDisplayName();
    }

    /**
     * Gets the value of one element of that kind.
     * @return The value of one element in cents.
     */
    public int getValue(){
        return getCurrency().getValue(this.is.getType());
    }

    /**
     * Gets the value of the full stack.
     * @return The value of the stack in cents.
     */
    public int getTotalValue(){
        return getCurrency().getValue(this.is.getType()) * this.is.getAmount();
    }

    public static PhysicalMoney get(ItemStack is){
        Currency currency = null;
        List<String> lore = is.getItemMeta().getLore();
        Pattern rg = Pattern.compile("Currency:\\s+([A-Z]+)");
        for(String s : lore){
            Matcher m = rg.matcher(s);
            if(m.matches()){
                currency = Currency.get(m.group(1));
                break;
            }
        }
        if(currency != null){
            return new PhysicalMoney(currency, is);
        } else {
            return null;
        }
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
    public int compareTo(PhysicalMoney o) {
        if(o == null) throw new NullPointerException();
        int result = Integer.compare(this.getValue(), o.getValue());
        return result == 0 ? Integer.compare(this.is.getAmount(), o.is.getAmount()) : result;
    }
}
