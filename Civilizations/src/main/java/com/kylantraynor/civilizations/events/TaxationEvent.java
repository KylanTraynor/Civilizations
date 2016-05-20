package com.kylantraynor.civilizations.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class TaxationEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private double taxedAmount;
	private int groupId;
	private TaxType type;
	
	public TaxationEvent(double taxedAmount, Settlement taxingGroup, TaxType type) {
		this.setTaxedAmount(taxedAmount);
		this.setTaxingGroup(taxingGroup);
		this.setType(type);
	}

	private void setTaxingGroup(Settlement taxingGroup) {
		groupId = taxingGroup.getId();
	}
	
	public Settlement getTaxingGroup(){
		return (Settlement) Group.get(groupId);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public double getTaxedAmount() {
		return taxedAmount;
	}

	public void setTaxedAmount(double taxedAmount) {
		this.taxedAmount = taxedAmount;
	}
	
	public double getRemainingAmount(){
		switch(getType()){
		case RENT:
			return getTaxedAmount() * (1.0 - getTaxingGroup().getStallRentTax());
		case TRANSACTION:
			return getTaxedAmount() * (1.0 - getTaxingGroup().getTransactionTax());
		default:
			return getTaxedAmount();
		}
	}
	
	public double getTaxes(){
		switch(getType()){
		case RENT:
			return getTaxedAmount() * (getTaxingGroup().getStallRentTax());
		case TRANSACTION:
			return getTaxedAmount() * (getTaxingGroup().getTransactionTax());
		default:
			return 0;
		}
	}

	public TaxType getType() {
		return type;
	}

	public void setType(TaxType type) {
		this.type = type;
	}
}
