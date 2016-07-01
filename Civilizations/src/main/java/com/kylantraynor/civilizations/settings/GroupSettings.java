package com.kylantraynor.civilizations.settings;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

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

	public void setShapes(List<Shape> shapes) {
		if(shapes != null){
			this.set("Shape", Util.getShapesString(shapes));
		} else {
			this.set("Shape", null);
		}
	}
	
	public List<Shape> getShapes(){
		if(this.contains("Shape")){
			return Util.parseShapes(this.getString("Shape"));
		}
		return new ArrayList<Shape>();
	}
}
