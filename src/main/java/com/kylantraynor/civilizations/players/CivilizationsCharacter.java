package com.kylantraynor.civilizations.players;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
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
	
	private CivilizationsAccount account;
	private CivilizationsCharacter[] parents = new CivilizationsCharacter[2];
	private Location location = Civilizations.getNewCharacterSpawn();
	private Instant birthday;
	private List<UUID> marriedTo = new ArrayList<UUID>();
	private String name = "Jon";
	private String familyName = "Sea";
	private Gender gender = Gender.MALE;
	private Influence influence = new Influence();
	private PlayerInventory inventory = (PlayerInventory) Bukkit.createInventory(null, InventoryType.PLAYER);
	private Inventory enderChest = Bukkit.createInventory(null, InventoryType.ENDER_CHEST);
	
	public CivilizationsCharacter(CivilizationsAccount account){
		super(UUID.randomUUID());
		this.account = account;
	}
	
	public UUID getAccountId(){
		return account.getPlayerId();
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String string) {
		name = string;
	}
	
	public String getFamilyName(){
		return familyName;
	}
	
	public void setFamilyName(String string) {
		familyName = string;
	}
	
	public Gender getGender(){
		return gender;
	}
	
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public Instant getBirthDate(){
		return birthday;
	}
	
	public void setBirthDate(Instant t){
		birthday = t;
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
	
	public Influence getInfluence(){
		return influence;
	}
	
	public void setInfluence(Influence influence) {
		this.influence = influence;
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public void updateLocation(){
		OfflinePlayer op = account.getOfflinePlayer();
		if(op.isOnline()){
			Player p = op.getPlayer();
			
			location = p.getLocation();
		}
	}

	public PlayerInventory getInventory() {
		return inventory;
	}
	
	public Inventory getEnderChest() {
		return enderChest;
	}

	public void update() {
		updateLocation();
		updateInventories();
	}

	private void updateInventories() {
		OfflinePlayer op = account.getOfflinePlayer();
		if(op.isOnline()){
			Player p = op.getPlayer();
			
			inventory.setContents(p.getInventory().getContents());
			inventory.setArmorContents(p.getInventory().getArmorContents());
			enderChest.setContents(p.getEnderChest().getContents());
		}
	}

	public void restore() {
		OfflinePlayer op = account.getOfflinePlayer();
		if(op.isOnline()){
			Player p = op.getPlayer();
			
			p.teleport(location, TeleportCause.PLUGIN);
			p.getInventory().setContents(inventory.getContents());
			p.getInventory().setArmorContents(inventory.getArmorContents());
			p.getEnderChest().setContents(enderChest.getContents());
		}
	}
}
