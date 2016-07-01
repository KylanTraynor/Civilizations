package com.kylantraynor.civilizations.settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.protection.PermissionSet;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.Protection;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.util.Util;

public class GroupSettings extends YamlConfiguration{
	
	private UUID uniqueId;
	private Instant creationDate;
	private List<UUID> members;
	private boolean changed = true;

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
	public void setChanged(boolean changed) {
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
	protected void setUniqueId(UUID id){
		if(id == null) id = UUID.randomUUID();
		this.set("General.UniqueId", id.toString());
		uniqueId = id;
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

	/**
	 * Gets the tax on stalls rental.
	 * @return
	 */
	public double getStallRentTax() {
		return this.getDouble("Taxes.Rental.Stalls", 0.01);
	}
	
	/**
	 * Sets the tax on stalls rental.
	 * @param newTax
	 */
	public void setStallRentTax(double newTax){
		this.set("Taxes.Rental.Stalls", newTax);
		this.setChanged(true);
	}

	/**
	 * Gets the tax on shop transactions.
	 * @return
	 */
	public double getTransactionTax() {
		return this.getDouble("Taxes.Transactions", 0.01);
	}
	
	/**
	 * Sets the tax on shop transactions.
	 * @param newTax
	 */
	public void setTransactionTax(double newTax){
		this.set("Taxes.Transactions", newTax);
		this.setChanged(true);
	}
	
	@Override
	public void save(File file){
		if(file == null) return;
		try{
			super.save(file);
			this.setChanged(false);
		} catch (IOException e){
			e.printStackTrace();
		}
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
			this.set("Protection.Shape", Util.getShapesString(shapes));
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
				this.set("Protection.Ranks." + r.getName() + ".Parent", r.getParent());
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
				Rank r = new Rank(s, cs.getString(s + ".Parent"));
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
			} else {
				this.set("Protection.Permissions." + target.getType().toString(), permissionSet.get(target).getTypesAsString());
			}
		}
	}
}
