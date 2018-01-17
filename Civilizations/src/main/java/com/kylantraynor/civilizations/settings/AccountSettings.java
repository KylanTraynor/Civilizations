package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Utils;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.players.CivilizationsCharacter;
import com.kylantraynor.civilizations.players.CivilizationsCharacter.Gender;

public class AccountSettings extends YamlConfiguration{
	
	private static final String PLAYERID = "PlayerId";
	private static final String CURRENTID = "CurrentId";
	private static final String CHARACTERFIRSTNAME = "Characters.%s.FirstName";
	private static final String CHARACTERLASTNAME = "Characters.%s.LastName";
	private static final String CHARACTERGENDER = "Characters.%s.Gender";
	private static final String CHARACTERBIRTHDATE = "Characters.%s.BirthDate";
	private static final String CHARACTERLOCATION = "Characters.%s.Location";
	private static final String CHARACTERINVENTORY = "Characters.%s.Inventory";
	private static final String CHARACTERARMOR = "Characters.%s.Armor";
	private static final String CHARACTERENDERCHEST = "Characters.%s.EnderChest";
	
	public UUID playerId;
	public UUID currentCharacter;
	
	
	public UUID getPlayerId(){
		if(playerId != null) return playerId;
		try{
			playerId = UUID.fromString(this.getString(PLAYERID));
		} catch (Exception e){
			
		}
		return playerId;
	}
	
	public void setPlayerId(UUID id){
		playerId = id;
		this.set(PLAYERID, id.toString());
	}
	
	public UUID getCurrentId(){
		if(currentCharacter != null) return currentCharacter;
		try{
			currentCharacter = UUID.fromString(this.getString(CURRENTID));
		} catch (Exception e){
			
		}
		return currentCharacter;
		
	}
	
	public void setCurrentId(UUID id){
		currentCharacter = id;
		this.set(CURRENTID, id.toString());
	}
	
	public void setCharacter(CivilizationsCharacter cc){
		if(cc == null) throw new NullPointerException();
		set(String.format(CHARACTERFIRSTNAME, cc.getUniqueId().toString()), cc.getName());
		set(String.format(CHARACTERLASTNAME, cc.getUniqueId().toString()), cc.getFamilyName());
		set(String.format(CHARACTERGENDER, cc.getUniqueId().toString()), cc.getGender().toString());
		set(String.format(CHARACTERBIRTHDATE, cc.getUniqueId().toString()), cc.getBirthDate().toString());
		set(String.format(CHARACTERLOCATION, cc.getUniqueId().toString()), Utils.locationToString(cc.getLocation()));
		set(String.format(CHARACTERINVENTORY, cc.getUniqueId().toString()), cc.getInventory().getContents());
		set(String.format(CHARACTERARMOR, cc.getUniqueId().toString()), cc.getInventory().getArmorContents());
		set(String.format(CHARACTERENDERCHEST, cc.getUniqueId().toString()), cc.getEnderChest().getContents());
	}
	
	public CivilizationsCharacter getCharacter(UUID id){
		if(id == null) throw new NullPointerException();
		CivilizationsCharacter cc = (CivilizationsCharacter) CivilizationsCharacter.getOrNull(id);
		if(cc == null){
			cc = new CivilizationsCharacter(CivilizationsAccount.get(playerId));
			cc.setUniqueId(id);
		}
		cc.setName(this.getString(String.format(CHARACTERFIRSTNAME, id.toString())));
		cc.setFamilyName(this.getString(String.format(CHARACTERLASTNAME, id.toString())));
		cc.setGender(Gender.valueOf(this.getString(String.format(CHARACTERGENDER, id.toString()))));
		cc.setBirthDate(Instant.parse(this.getString(String.format(CHARACTERBIRTHDATE, id.toString()))));
		cc.setLocation(Utils.parseLocation(this.getString(String.format(CHARACTERLOCATION, id.toString()))));
		ItemStack[] contents = ((List<ItemStack>) this.get(String.format(CHARACTERINVENTORY, id.toString()))).toArray(new ItemStack[0]);
		ItemStack[] armor = ((List<ItemStack>) this.get(String.format(CHARACTERARMOR, id.toString()))).toArray(new ItemStack[0]);
		ItemStack[] enderChest = ((List<ItemStack>) this.get(String.format(CHARACTERENDERCHEST, id.toString()))).toArray(new ItemStack[0]);
		cc.getInventory().setContents(contents);
		cc.getInventory().setArmorContents(armor);
		cc.getEnderChest().setContents(enderChest);
		return cc;
	}

	public boolean removeCharacter(UUID id) {
		if(id == null) throw new NullPointerException();
		ConfigurationSection s = getConfigurationSection("Characters");
		if(s == null) return false;
		if(s.contains(id.toString())){
			s.set(id.toString(),null);
			return true;
		} else {
			return false;
		}
	}
	
	public UUID[] getCharacterIds(){
		List<UUID> result = new ArrayList<UUID>();
		ConfigurationSection s = this.getConfigurationSection("Characters");
		Set<String> keys = s.getKeys(false);
		for(String key : keys){
			result.add(UUID.fromString(key));
		}
		return result.toArray(new UUID[0]);
	}
}