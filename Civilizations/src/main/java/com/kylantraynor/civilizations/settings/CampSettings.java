package com.kylantraynor.civilizations.settings;

import java.time.Instant;

public class CampSettings extends SettlementSettings{
	
	private Instant expiryDate;

	/**
	 * Gets the expiry date of the camp.
	 * @return Instant
	 */
	public Instant getExpiryDate() {
		if(expiryDate != null) return expiryDate;
		expiryDate = Instant.now();
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
		expiryDate = date == null ? Instant.now() : date;
		this.set("General.ExpiryDate", expiryDate.toString());
	}
	
}
