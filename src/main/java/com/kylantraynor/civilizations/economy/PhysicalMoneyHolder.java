package com.kylantraynor.civilizations.economy;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PhysicalMoneyHolder {

    public List<PhysicalMoney> getPhysicalMoney();

    public long givePhysicalMoney(long cents);
    public long takePhysicalMoney(long cents);

}