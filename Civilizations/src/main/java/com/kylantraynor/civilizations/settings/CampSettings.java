package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CampSettings extends SettlementSettings{
	
	private Instant expiryDate;

	/**
	 * Gets the expiry date of the camp.
	 * @return Instant
	 */
	public Instant getExpiryDate() {
		if(expiryDate != null) return expiryDate;
		expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
		if(this.contains("General.ExpiryDate")){
			try{
				expiryDate = Instant.parse(this.getString("General.ExpiryDate"));
			} catch (Exception e){}
		}
		return expiryDate;
	}
	
	/**
	 * Sets the expiry date of the camp.
	 * @param date
	 */
	public void setExpiryDate(Instant date){
		expiryDate = date == null ? Instant.now().plus(1, ChronoUnit.DAYS) : date;
		this.set("General.ExpiryDate", expiryDate.toString());
		this.setChanged(true);
	}
	
}
