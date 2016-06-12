package com.kylantraynor.civilizations.settings;

import java.time.Instant;

public class CampSettings extends GroupSettings{
	
	private Instant expiryDate;

	/**
	 * Gets the expiry date of the camp.
	 * @return Instant
	 */
	public Instant getExpiryDate() {
		if(expiryDate != null) return expiryDate;
		expiryDate = Instant.now();
		if(this.contains("general.expiryDate")){
			try{
				expiryDate = Instant.parse(this.getString("general.expiryDate"));
			} catch (Exception e){}
		}
		return expiryDate;
	}
	
	/**
	 * Sets the expiry date of the camp.
	 * @param date
	 */
	public void setCreationDate(Instant date){
		expiryDate = date == null ? Instant.now() : date;
		this.set("general.expiryDate", expiryDate.toString());
	}
	
}
