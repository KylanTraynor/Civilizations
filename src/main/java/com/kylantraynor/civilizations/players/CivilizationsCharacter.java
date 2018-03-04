package com.kylantraynor.civilizations.players;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kylantraynor.civilizations.settings.CharacterSettings;
import com.kylantraynor.civilizations.utils.DoubleIdentifier;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.territories.HonorificTitle;
import com.kylantraynor.civilizations.territories.Influence;

/**
 * A character. One player can have multiple characters.
 * Should they die, they will be able to make another character.
 * Either the child of their current one if they
 * were married (inheriting titles) or not (losing everything).
 * 
 * @author Baptiste
 *
 */
public class CivilizationsCharacter extends EconomicEntity {
	
	public enum Gender {
		MALE, FEMALE
	}

	private CivilizationsCharacter[] parents = new CivilizationsCharacter[2];
	private List<UUID> marriedTo = new ArrayList<>();
	private final CharacterSettings settings;
	
	public CivilizationsCharacter(UUID accountId, UUID id){
		super(id);
		this.settings = new CharacterSettings();
		this.settings.setUniqueid(id);
		this.settings.setAccountId(accountId);
	}

	public CivilizationsCharacter(CharacterSettings settings){
		super(settings.getUniqueId());
		this.settings = settings;
	}

    public CharacterSettings getSettings() {
         return settings;
    }

    public UUID getIdentifier() {
	    return settings.getUniqueId();
	}
	
	public UUID getAccountId(){
		return settings.getAccountId();
	}
	
	public String getName(){
		return settings.getFirstName();
	}
	
	public void setName(String string) {
		settings.setFirstName(string);
	}
	
	public String getFamilyName(){
		return settings.getLastName();
	}
	
	public void setFamilyName(String string) {
		settings.setLastName(string);
	}
	
	public Gender getGender(){
		return settings.getGender();
	}
	
	public void setGender(Gender gender) {
		settings.setGender(gender);
	}
	
	public Instant getBirthDate(){
		return settings.getBirthdate();
	}
	
	public void setBirthDate(Instant t){
		settings.setBirthdate(t);
	}
	
	public Influence getInfluence(){
		return settings.getInfluence();
	}
	
	public void setInfluence(Influence influence) {
		settings.setInfluence(influence);
	}

	public Location getLocation() {
		return settings.getLocation();
	}
	
	public void setLocation(Location location) {
		settings.setLocation(location);
	}

	public ItemStack[] getInventory() {
		return settings.getInventory();
	}

	public void setInventory(ItemStack[] contents){
	    settings.setInventory(contents);
    }

	public ItemStack[] getArmor() { return settings.getArmor(); }

	public void setArmor(ItemStack[] contents){
        settings.setArmor(contents);
    }
	
	public ItemStack[] getEnderChest() {
		return settings.getEnderChest();
	}

	public void setEnderChest(ItemStack[] contents){
	    settings.setEnderChest(contents);
    }

	public void update() {
		updateLocation();
		updateInventories();
	}

    public void updateLocation(){
        OfflinePlayer op = getOfflinePlayer();
        if(op.isOnline()){
            Player p = op.getPlayer();

            setLocation(p.getLocation());
        }
    }

	private void updateInventories() {
		OfflinePlayer op = getOfflinePlayer();
		if(op.isOnline()){
			Player p = op.getPlayer();
			
			setInventory(p.getInventory().getContents());
			setArmor(p.getInventory().getArmorContents());
			setEnderChest(p.getEnderChest().getContents());
		}
	}

	public void restore() {
		OfflinePlayer op = getOfflinePlayer();
		if(op.isOnline()){
			Player p = op.getPlayer();
			
			p.teleport(getLocation(), TeleportCause.PLUGIN);
			p.getInventory().setContents(getInventory());
			p.getInventory().setArmorContents(getArmor());
			p.getEnderChest().setContents(getEnderChest());
		}
	}

	public OfflinePlayer getOfflinePlayer(){
	    for(OfflinePlayer op : Bukkit.getOfflinePlayers()){
	        if(op.getUniqueId().equals(getAccountId())) return op;
        }
        return null;
    }

    public HonorificTitle[] getTitles(){
        List<HonorificTitle> result = new ArrayList<HonorificTitle>();
        for(Group g : Group.getList()){
            if(g instanceof HonorificTitle){
                if(g.isMember(this)){
                    result.add((HonorificTitle) g);
                }
            }
        }
        return result.toArray(new HonorificTitle[result.size()]);
    }

    public boolean addTitle(HonorificTitle t){
        return t.addMember(this);
    }

    public boolean removeTitle(HonorificTitle t){
        return t.removeMember(this);
    }
}
