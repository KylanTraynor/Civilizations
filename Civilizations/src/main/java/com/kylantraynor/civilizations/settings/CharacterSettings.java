package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.players.CivilizationsCharacter;

public class CharacterSettings extends YamlConfiguration{
	
	private final static String UNIQUEID = "General.UniqueId";
	private final static String BIRTHDATE = "General.BirthDate";
	private final static String GENDER = "General.Gender";
	private final static String FIRSTNAME = "General.FirstName";
	private final static String FAMILYNAME = "General.FamilyName";
	
	
	private CivilizationsCharacter.Gender gender;
	private UUID uniqueId;
	private Instant birthDate;
	private boolean changed = true;
	private String firstName;
	private String familyName;

	/**
	 * Checks if the settings need to be saved.
	 * @return
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * Sets whether or not the settings should be saved.
	 * @param changed
	 */
	public synchronized void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	/**
	 * Gets the unique ID of the character.
	 * @return
	 */
	public UUID getUniqueId(){
		if(uniqueId != null) return uniqueId;
		if(this.contains(UNIQUEID)){
			uniqueId = UUID.fromString(this.getString(UNIQUEID));
		} else {
			setUniqueId(null);
		}
		return uniqueId;
	}
	
	/**
	 * Sets the unique ID of the character. Null will set a new random Unique ID.
	 * @param id
	 */
	public void setUniqueId(UUID id){
		if(id == null) id = UUID.randomUUID();
		this.set(UNIQUEID, id.toString());
		uniqueId = id;
	}
	
	/**
	 * Gets the first name of the character.
	 * @return String
	 */
	public String getFirstName(){
		if(firstName != null) return firstName;
		if(this.contains(FIRSTNAME)){
			firstName = this.getString(FIRSTNAME);
		}
		return firstName;
	}
	
	/**
	 * Sets the first name of the character.
	 * @param newName
	 */
	public void setFirstName(String newName){
		if(newName == null) return;
		this.set(FIRSTNAME, newName);
		this.setChanged(true);
	}
	
	/**
	 * Gets the family name of the character.
	 * @return String
	 */
	public String getFamilyName(){
		if(familyName != null) return familyName;
		if(this.contains(FAMILYNAME)){
			familyName = this.getString(FAMILYNAME);
		}
		return familyName;
	}
	
	/**
	 * Sets the family name of the character.
	 * @param newName
	 */
	public void setFamilyName(String newName){
		if(newName == null) return;
		this.set(FAMILYNAME, newName);
		this.setChanged(true);
	}
	
	/**
	 * Gets the birth date of the character.
	 * @return Instant
	 */
	public Instant getCreationDate() {
		if(birthDate != null) return birthDate;
		birthDate = Instant.now();
		if(this.contains(BIRTHDATE)){
			try{
				birthDate = Instant.parse(this.getString(BIRTHDATE));
			} catch (Exception e){}
		}
		return birthDate;
	}
	
	/**
	 * Sets the birth date of the character.
	 * @param date
	 */
	public void setCreationDate(Instant date){
		birthDate = date == null ? Instant.now() : date;
		this.set(BIRTHDATE, birthDate.toString());
		this.setChanged(true);
	}
	
	/**
	 * Gets the gender of the character.
	 * @return Gender
	 */
	public CivilizationsCharacter.Gender getGender() {
		if(gender != null) return gender;
		gender = CivilizationsCharacter.Gender.MALE;
		if(this.contains(GENDER)){
			try{
				gender = CivilizationsCharacter.Gender.valueOf(this.getString(GENDER));
			} catch (Exception e){}
		}
		return gender;
	}
	
	/**
	 * Sets the gender of the character.
	 * @param Gender
	 */
	public void setGender(CivilizationsCharacter.Gender gender){
		this.gender = gender;
		this.set(GENDER, gender.toString());
		this.setChanged(true);
	}
}
