package com.kylantraynor.civilizations.economy;

import com.kylantraynor.civilizations.Civilizations;
import org.bukkit.Material;

import java.util.*;

public class Currency {

    private static Map<String, Currency> all = new HashMap<>();

    private final String name;
    private final String shortName;
    private double rate;
    private Map<Material, Denomination> denominations = new HashMap<>();

    public Currency(String name, String shortName, double rate){
        this.name = name;
        this.shortName = shortName;
        this.rate = rate;
    }

    public String getName(){
        return name;
    }

    public String getShortName(){
        return shortName;
    }

    public int getValue(Material mat){
        return denominations.get(mat).getValue();
    }

    public List<Denomination> getDenominations(){
        if(denominations.isEmpty()){
            denominations.put(Material.GOLD_NUGGET, new Denomination(Material.GOLD_NUGGET, Civilizations.getSettings().getGoldNuggetValue()));
            denominations.put(Material.IRON_NUGGET, new Denomination(Material.IRON_NUGGET, Civilizations.getSettings().getIronNuggetValue()));
        }
        return new ArrayList<>(denominations.values());
    }

    public static Currency get(String shortName){
        return all.get(shortName);
    }

    public static boolean register(String name, String shortName, double rate){
        if(all.containsKey(shortName)){
            return false;
        } else {
            all.put(shortName, new Currency(name, shortName, rate));
            return true;
        }
    }

    public class Denomination implements Comparable<Denomination>{
        private int baseValue;
        private Material mat;
        public Denomination(Material mat, int value){
            this.mat = mat;
            this.baseValue = value;
        }

        public Material getMaterial(){
            return mat;
        }

        public int getValue(){
            return (int) (baseValue * rate);
        }

        @Override
        public int compareTo(Denomination o) {
            return Integer.compare(this.baseValue, o.baseValue);
        }
    }
}
