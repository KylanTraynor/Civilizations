package com.kylantraynor.civilizations.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.kylantraynor.civilizations.managers.AccountManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.utils.Utils;

public class AccountSettings extends YamlConfiguration{
	private static final String PLAYERID = "PlayerId";
	private static final String CURRENTID = "CurrentId";
	private static final String BASELOCATION = "Base.Location";
	private static final String BASEINVENTORY = "Base.Inventory";
	private static final String BASEARMOR = "Base.Armor";
	private static final String BASEENDERCHEST = "Base.EnderChest";
	
	public UUID playerId;
	public UUID currentCharacter;
	
	
	public UUID getPlayerId(){
		if(playerId != null) return playerId;
		playerId = UUID.fromString(this.getString(PLAYERID));
		return playerId;
	}
	
	public void setPlayerId(UUID id){
		playerId = id;
		this.set(PLAYERID, id.toString());
	}
	
	public UUID getCurrentId(){
		if(currentCharacter != null) return currentCharacter;
		currentCharacter = UUID.fromString(this.getString(CURRENTID));
		return currentCharacter;
		
	}
	
	public void setCurrentId(UUID id){
		currentCharacter = id;
		this.set(CURRENTID, id.toString());
	}
	
	public Location getBaseLocation(){
		String s = this.getString(BASELOCATION);
		if(s == null) return null;
		try{
			return Utils.parseLocation(s);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setBaseLocation(Location l){
		this.set(BASELOCATION, Utils.locationToString(l));
	}
	
	public ItemStack[] getBaseInventory(){
		List<ItemStack> list = ((List<ItemStack>) this.get(BASEINVENTORY));
		if(list == null) return null;
		return list.toArray(new ItemStack[0]);
	}
	
	public void setBaseInventory(ItemStack[] contents){
		this.set(BASEINVENTORY, contents);
	}
	
	public ItemStack[] getBaseArmor(){
		List<ItemStack> list = ((List<ItemStack>) this.get(BASEARMOR));
		if(list == null) return null;
		return list.toArray(new ItemStack[0]);
	}
	
	public void setBaseArmor(ItemStack[] contents){
		this.set(BASEARMOR, contents);
	}
	
	public ItemStack[] getBaseEnderChest(){
		List<ItemStack> list = ((List<ItemStack>) this.get(BASEENDERCHEST));
		if(list == null) return null;
		return list.toArray(new ItemStack[0]);
	}
	
	public void setBaseEnderChest(ItemStack[] contents){
		this.set(BASEENDERCHEST, contents);
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
		List<UUID> result = new ArrayList<>();
		ConfigurationSection s = this.getConfigurationSection("Characters");
		if(s == null) return new UUID[0];
		Set<String> keys = s.getKeys(false);
		for(String key : keys){
			result.add(UUID.fromString(key));
		}
		return result.toArray(new UUID[result.size()]);
	}

    public File getFile(){
        File f = AccountManager.getAccountsDirectory();
        if(!f.exists()) f.mkdirs();
        return new File(f, getPlayerId().toString() + ".yml");
    }

    public void reload(){
        File f = getFile();
        if(f.exists()){
            try { this.load(f); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void save(){
        File f = getFile();
        if(!f.exists()) {
            try { f.createNewFile(); } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try { this.save(f); } catch (IOException e) { e.printStackTrace(); }
    }
}