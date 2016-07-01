package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.groups.settlements.Settlement;

public class PlotSettings extends GroupSettings{
	
	private Settlement settlement;
	private OfflinePlayer owner;
	private Boolean forRent;
	private OfflinePlayer renter;
	private Double rent;
	private Instant nextPayment;

	/**
	 * Gets the settlement this plot belongs to.
	 * @return
	 */
	public Settlement getSettlement() {
		if(settlement != null) return settlement;
		if(this.contains("General.Settlement")){
			Settlement s = (Settlement) Group.get(UUID.fromString(this.getString("General.Settlement")));
			if(s != null){
				settlement = s;
			}
		}
		return settlement;
	}

	/**
	 * Sets the settlement this plot belongs to.
	 * @param settlement
	 */
	public void setSettlement(Settlement settlement) {
		if(settlement != null){
			this.set("General.Settlement", settlement.getSettings().getUniqueId().toString());
		} else {
			this.set("General.Settlement", null);
		}
		this.settlement = settlement;
		setChanged(true);
	}

	/**
	 * Gets the owner of this plot.
	 * @return
	 */
	public OfflinePlayer getOwner() {
		if(owner != null) return owner;
		if(this.contains("Economy.Owner")){
			owner = Bukkit.getOfflinePlayer(UUID.fromString(this.getString("Economy.Owner")));
		}
		return owner;
	}

	/**
	 * Sets the owner of this plot.
	 * @param owner
	 */
	public void setOwner(OfflinePlayer owner) {
		this.owner = owner;
		if(owner != null){
			this.set("Economy.Owner", owner.getUniqueId().toString());
		} else {
			this.set("Economy.Owner", null);
		}
		this.setChanged(true);
	}

	/**
	 * Gets the renter of this plot.
	 * @return
	 */
	public OfflinePlayer getRenter() {
		if(renter != null) return renter;
		if(this.contains("Economy.Renter")){
			renter = Bukkit.getOfflinePlayer(UUID.fromString(this.getString("Economy.Renter")));
		}
		return renter;
	}

	/**
	 * Sets the renter of this plot.
	 * @param renter
	 */
	public void setRenter(OfflinePlayer renter) {
		this.renter = renter;
		if(renter != null){
			this.set("Economy.Renter", renter.getUniqueId().toString());
		} else {
			this.set("Economy.Renter", null);
		}
		this.setChanged(true);
	}

	/**
	 * Gets the rent of this plot.
	 * @return
	 */
	public double getRent() {
		if(rent != null) return rent;
		if(this.contains("Economy.Rent")){
			rent = this.getDouble("Economy.Rent");
		} else {
			rent = 1.0;
		}
		return rent;
	}

	/**
	 * Sets the rent of this plot.
	 * @param rent
	 */
	public void setRent(double rent) {
		rent = Math.max(rent, 1.0);
		this.rent = rent;
		this.set("Economy.Rent", rent);
		this.setChanged(true);
	}

	/**
	 * Checks if the plot is for rent.
	 * @return
	 */
	public boolean isForRent() {
		if(forRent != null) return forRent;
		if(this.contains("Economy.ForRent")){
			forRent = this.getBoolean("Economy.ForRent");
		} else {
			forRent = false;
		}
		return forRent;
	}

	/**
	 * Sets if the plot is for rent.
	 * @param forRent
	 */
	public void setForRent(boolean forRent) {
		this.forRent = forRent;
		this.set("Economy.ForRent", forRent);
		this.setChanged(true);
	}

	/**
	 * Gets the time of the next rent payment.
	 * @return
	 */
	public Instant getNextPayment() {
		if(nextPayment != null) return nextPayment;
		nextPayment = Instant.now();
		if(this.contains("Economy.NextPayment")){
			try{
				nextPayment = Instant.parse(this.getString("Economy.NextPayment"));
			} catch (Exception e){}
		}
		return nextPayment;
	}

	/**
	 * Sets the time of the next payment.
	 * @param date
	 */
	public void setNextPayment(Instant date) {
		nextPayment = date == null ? Instant.now() : date;
		this.set("Economy.NextPayment", nextPayment.toString());
		this.setChanged(true);
	}
	
	
}
