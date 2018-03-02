package com.kylantraynor.civilizations.settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.TaxBase;
import com.kylantraynor.civilizations.economy.TaxInfo;
import com.kylantraynor.civilizations.economy.TaxType;
import com.kylantraynor.civilizations.protection.GroupTarget;
import com.kylantraynor.civilizations.protection.PermissionSet;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.util.Util;

public class GroupSettings extends YamlConfiguration{
	
	private UUID uniqueId;
	private UUID parentId;
	private Instant creationDate;
	private List<UUID> members;
	private boolean changed = true;
	
	private static final String PERMISSIONSROOT = "Permissions";
	private static final String PERMISSIONSLEVEL = PERMISSIONSROOT + ".%s.Level";
	private static final String PERMISSIONS = PERMISSIONSROOT + ".%s.%s";

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
	 * Gets the unique ID of the group.
	 * @return
	 */
	public UUID getUniqueId(){
		if(uniqueId != null) return uniqueId;
		if(this.contains("General.UniqueId")){
			uniqueId = UUID.fromString(this.getString("General.UniqueId"));
		} else {
			setUniqueId(null);
		}
		return uniqueId;
	}
	
	/**
	 * Sets the unique ID of the group. Null will set a new random Unique ID.
	 * @param id
	 */
	public void setUniqueId(UUID id){
		if(id == null) id = UUID.randomUUID();
		this.set("General.UniqueId", id.toString());
		uniqueId = id;
	}

	/**
	 * Gets the {@linkplain UUID} of the {@linkplain com.kylantraynor.civilizations.groups.Group}.
	 * @return Null if there is no parent.
	 */
	public UUID getParentId(){
		if(parentId != null) return parentId;
		if(this.contains("General.ParentId")){
			parentId = UUID.fromString(this.getString("General.ParentId"));
		}
		return parentId;
	}

	/**
	 * Sets the {@linkplain UUID} of the {@linkplain com.kylantraynor.civilizations.groups.Group}.
	 * @param id
	 */
	public void setParentId(UUID id){
		if(id != null) {
            this.set("General.ParentId", id.toString());
        } else {
		    this.set("General.ParentId", null);
        }
		parentId = id;
	}
	
	/**
	 * Gets the group's balance.
	 * @return
	 */
	public double getBalance(){
		if(this.contains("Economy.Balance")){
			return this.getDouble("Economy.Balance");
		} else {
			return 0;
		}
	}
	/**
	 * Sets the group's balance.
	 * @param newBalance
	 */
	public void setBalance(double newBalance){
		this.set("Economy.Balance", newBalance);
	}
	
	/**
	 * Gets the creation date of the group.
	 * @return Instant
	 */
	public Instant getCreationDate() {
		if(creationDate != null) return creationDate;
		creationDate = Instant.now();
		if(this.contains("General.CreationDate")){
			try{
				creationDate = Instant.parse(this.getString("General.CreationDate"));
			} catch (Exception e){}
		}
		return creationDate;
	}
	
	/**
	 * Sets the creation date of the group.
	 * @param date
	 */
	public void setCreationDate(Instant date){
		creationDate = date == null ? Instant.now() : date;
		this.set("General.CreationDate", creationDate.toString());
		this.setChanged(true);
	}
	
	/**
	 * Gets the name of the group.
	 * @return String
	 */
	public String getName(){
		if(this.contains("General.Name")){
			return this.getString("General.Name");
		}
		return "Group";
	}
	
	/**
	 * Sets the name of the group.
	 * @param newName
	 */
	public void setName(String newName){
		if(newName == null) return;
		this.set("General.Name", newName);
		this.setChanged(true);
	}
	
	/**
	 * Gets the list of members of this group.
	 * @return List<UUID>
	 */
	public List<UUID> getMembers(){
		if(this.members == null) {
			this.members = new ArrayList<UUID>();
			if(this.contains("Members")){
				List<?> l = this.getList("Members");
				for(Object o : l){
					if(o instanceof String){
						this.members.add(UUID.fromString((String) o));
					}
				}
			}
		}
		return this.members;
	}
	
	/**
	 * Sets the list of members of this group.
	 * @param list
	 */
	public void setMembers(List<UUID> list){
		List<String> l = new ArrayList<String>();
		if(list != null){
			for(UUID id : list){
				l.add(id.toString());
			}
		}
		this.members = list;
		this.set("Members", l);
		this.setChanged(true);
	}

	public double getTax(TaxType type){
		return this.getDouble("Economy.Taxes." + type.toString(), 0.01);
	}
	
	public void setTax(TaxType type, double tax){
		this.set("Economy.Taxes." + type.toString(), tax);
		this.setChanged(true);
	}
	
	/**
	 * Gets the tax on stalls rental.
	 * @return
	 */
	public double getStallRentTax() {
		return this.getDouble("Economy.Taxes.Rental.Stalls", 0.01);
	}
	
	/**
	 * Sets the tax on stalls rental.
	 * @param newTax
	 */
	public void setStallRentTax(double newTax){
		this.set("Economy.Taxes.Rental.Stalls", newTax);
		this.setChanged(true);
	}

	/**
	 * Gets the tax on shop transactions.
	 * @return
	 */
	public double getTransactionTax() {
		return this.getDouble("Economy.Taxes.Transactions", 0.01);
	}
	
	/**
	 * Sets the tax on shop transactions.
	 * @param newTax
	 */
	public void setTransactionTax(double newTax){
		this.set("Economy.Taxes.Transactions", newTax);
		this.setChanged(true);
	}
	
	/**
	 * Gets the state of the given permission for the entity with the given
	 * {@linkplain UUID}.
	 * @param id as {@link UUID}
	 * @param permission as {@link String}
	 * @return {@link Boolean}, or Null if the permission was not set.
	 */
	public Boolean getPermission(UUID id, String permission){
		String path = String.format(PERMISSIONS, id.toString(), permission);
		if(!this.contains(path)) return null;
		return this.getBoolean(path);
	}
	
	/**
	 * Gets the permission level of entity with the given {@linkplain UUID}.
	 * @param id as {@link UUID}
	 * @return {@link Integer} containing the value, or Null if the
	 * permission was not set.
	 */
	public Integer getPermissionLevel(UUID id){
		String path = String.format(PERMISSIONSLEVEL, id.toString());
		if(!this.contains(path)) return null;
		return this.getInt(path);
	}
	
	/**
	 * Sets the given permission to the given value or the entity with the given {@linkplain UUID}.
	 * @param id as {@link UUID}
	 * @param permission as {@link String}
	 * @param value as {@link Boolean}
	 * @return {@link Boolean} of the previous value of the permission, or Null if it was not set.
	 */
	public Boolean setPermission(UUID id, String permission, Boolean value){
		Boolean oldValue = getPermission(id, permission);
		this.set(String.format(PERMISSIONS, id.toString(), permission), value);
		return oldValue;
	}
	
	/**
	 * Sets the permission level for the entity with the given {@linkplain UUID}.
	 * @param id as {@link UUID}
	 * @param value as {@link Integer}
	 * @return {@link Integer} containing the previous level, or Null
	 * if it was not set.
	 */
	public Integer setPermissionLevel(UUID id, Integer value){
		Integer oldValue = getPermissionLevel(id);
		this.set(String.format(PERMISSIONSLEVEL, id.toString()), value);
		return oldValue;
	}
	
	@Override
	public void save(File file){
		if(file == null) return;
		try{
			super.save(file);
			this.setChanged(false);
		} catch (IOException e){
			e.printStackTrace();
			this.setChanged(true);
		}
	}
	
	public void asyncSave(File file){
		BukkitRunnable run = new BukkitRunnable(){
			@Override
			public void run() {
				save(file);
			}
		};
		run.runTaskAsynchronously(Civilizations.currentInstance);
		this.setChanged(false);
	}
	
	/**
	 * Saves the protection. Should be used each time the protection is changed, or
	 * once before saving the settings.
	 * @param prot
	 */
	public void saveProtection(Protection prot){
		if(!prot.getShapes().isEmpty()){
			setShapes(prot.getShapes());
		}
		if(!prot.getRanks().isEmpty()){
			setRanks(prot.getRanks());
		}
		if(prot.getPermissionSet() != null){
			setPermissionSet(prot.getPermissionSet());
		}
	}

	public void setShapes(List<Shape> shapes) {
		if(shapes != null){
			try{
				this.set("Protection.Shape", Util.getShapesString(shapes));
			} catch(Exception e){
				Civilizations.currentInstance.getLogger().warning("Couldn't save protection shapes for " + this.getName() + ".");
				e.printStackTrace();
			}
		} else {
			this.set("Protection.Shape", null);
		}
	}
	
	public List<Shape> getShapes(){
		if(this.contains("Protection.Shape")){
			return Util.parseShapes(this.getString("Protection.Shape"));
		}
		return new ArrayList<Shape>();
	}
	
	public void setRanks(List<Rank> ranks){
		if(ranks != null){
			for(Rank r : ranks){
				List<String> idList = new ArrayList<String>();
				for(UUID id : r.getUniqueIds()){
					idList.add(id.toString());
				}
				this.set("Protection.Ranks." + r.getName() + ".ID", r.getUniqueId().toString());
				if(r.getParentId() != null) {
					boolean real = false;
					for(Rank r1 : ranks){
						if(r1.getUniqueId() == r.getParentId()){
							real = true;
							break;
						}
					}
					if(real){
						this.set("Protection.Ranks." + r.getName() + ".Parent", r.getParentId().toString());
					} else {
						this.set("Protection.Ranks." + r.getName() + ".Parent", null);
					}
				}
				this.set("Protection.Ranks." + r.getName() + ".Level", r.getLevel());
				this.set("Protection.Ranks." + r.getName() + ".Members", idList);
			}
		}
	}
	
	public List<Rank> getRanks(){
		List<Rank> ranks = new ArrayList<Rank>();
		
		if(this.contains("Protection.Ranks")){
			ConfigurationSection cs = this.getConfigurationSection("Protection.Ranks");
			for(String s : cs.getKeys(false)){
				Rank r;
				try{
					r = new Rank(UUID.fromString(cs.getString(s + ".ID")), s, UUID.fromString(cs.getString(s + ".Parent")));
				}catch (Exception e){
					r = new Rank(UUID.randomUUID(), s, null);
				}
				r.setLevel(cs.getInt(s + ".Level"));
				for(Object o : cs.getList(s + ".Members")){
					if(o instanceof String){
						r.addId(UUID.fromString((String) o));
					}
				}
				ranks.add(r);
			}
		}
		
		return ranks;
	}
	
	public void setPermissionSet(PermissionSet permissionSet) {
		for(PermissionTarget target : permissionSet.getTargets()){
			if(target instanceof Rank){
				this.set("Protection.Permissions.Ranks." + ((Rank) target).getName(), permissionSet.get(target).getTypesAsString());
			} else if(target instanceof GroupTarget){
				this.set("Protection.Permissions.Groups." + ((GroupTarget) target).getGroup().getSettings().getUniqueId().toString(), permissionSet.get(target).getTypesAsString());
			} else {
				this.set("Protection.Permissions." + target.getType().toString(), permissionSet.get(target).getTypesAsString());
			}
		}
	}

	public TaxInfo getTaxInfo(String tax) {
		String root = "Economy.Taxes.";
		if(this.contains(root + tax)){
			double value = this.getDouble(root + tax + ".value", 0);
			boolean isPercent = this.getBoolean(root + tax + ".isPercent", false);
			TaxBase base = TaxBase.valueOf(this.getString(root + tax + ".base", "FromBalance"));
			return new TaxInfo(tax, base, value, isPercent);
		} else {
			return null;
		}
	}

	public void setTaxInfo(String tax, TaxBase base, double value,
			boolean isPercent) {
		
		String root = "Economy.Taxes.";
		this.set(root + tax + ".value", value);
		this.set(root + tax + ".isPercent", isPercent);
		this.set(root + tax + ".base", base.toString());
		
		this.setChanged(true);
	}

	public Set<String> getTaxes() {
		String root = "Economy.Taxes";
		return this.getConfigurationSection(root).getKeys(false);
	}
}
