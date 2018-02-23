package com.kylantraynor.civilizations.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.civilizations.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.settings.BuilderSettings;
import com.kylantraynor.civilizations.settings.GroupSettings;
import com.kylantraynor.civilizations.util.MaterialAndData;
import com.kylantraynor.civilizations.util.Util;

public class Builder {
	private static List<Builder> builders = new ArrayList<Builder>();
	private BuildProject currentProject;
	private BuilderSettings settings;
	
	public Builder(HasBuilder group) {
		settings = new BuilderSettings();
		getSettings().setOwner(group);
		builders.add(this);
	}
	
	public Builder(BuilderSettings settings){
		this.settings = settings;
		builders.add(this);
	}

	public ItemStack getSupplies(ItemStack is){
		if(getOwner() == null) return null;
		return getOwner().getSuppliesAndRemove(is);
	}
	
	public void update(){
		if(getProjects().isEmpty()){
			currentProject = null;
			return;
		}
		if(currentProject == null) currentProject = getProjects().get((int) Math.floor((Math.random() * getProjects().size())));
		
		if(currentProject.isDone()){
			removeProject(currentProject);
			warnProjectComleted(currentProject);
			currentProject = null;
			Civilizations.DEBUG("Project was completed");
			return;
		}
		/* The builder nees to check what block needs to be built.
		 * It will compare the planned block to what is already at the location.
		 * It will then check the materials available to it.
		 * It will try to build.
		 */
		
		Civilizations.DEBUG("Trying to build project.");
		while(true){
			MaterialAndData plan = currentProject.getNext();
			// If there is no plan, then the build is likely one or was remove.
			if(plan == null){
				currentProject = null;
				break;
			}
			// Checks if the next plan requires supply to be built.
			if(!currentProject.nextRequiresSupply()){
				currentProject.buildNext();
				this.getSettings().setChanged(true);
				Civilizations.DEBUG("Did not require supplies. Built.");
			} else {
				Civilizations.DEBUG("Trying to get the supplies.");
				ItemStack supply = getSupplies(plan.toItemStack());
				if(supply == null){
					Civilizations.DEBUG("Getting Default supplies.");
					supply = getSupplies(plan.getDefault().toItemStack());
				}
				if(supply == null){
					if(!currentProject.trySkipNext()){
						if(plan.changeForPaste().getMaterial() == Material.AIR){
							currentProject.buildInstead(plan.changeForPaste());
							this.getSettings().setChanged(true);
							break;
						}
						warnLackOfSupplies(plan);
						currentProject = null;
						break;
					} else {
						Civilizations.DEBUG("Supply was NULL and skipped next.");
						this.getSettings().setChanged(true);
						break;
					}
				}
				currentProject.buildNext();
				this.getSettings().setChanged(true);
				break;
			}
		}
		
		/*
		MaterialAndData plan = currentProject.getNext();
		if(plan == null){
			currentProject = null;
			return;
		} else if(!plan.requiresSupply()){
			currentProject.buildNext();
			this.getSettings().setChanged(true);
			return;
		}
		ItemStack supply = getSupplies(plan.getDefault().toItemStack());
		if(supply == null){
			warnLackOfSupplies(plan);
			if(!currentProject.trySkipNext()){
				currentProject = null;
			} else {
				this.getSettings().setChanged(true);
			}
		} else {
			currentProject.buildNext();
			this.getSettings().setChanged(true);
		}
		*/
	}

	public boolean removeProject(BuildProject project) {
		List<BuildProject> projects = getProjects();
		if(projects.remove(project)){
			getSettings().setProjects(projects);
			return true;
		}
		return false;
	}
	
	public boolean addProject(BuildProject project){
		List<BuildProject> projects = getProjects();
		if(!projects.contains(project)){
			projects.add(project);
			getSettings().setProjects(projects);
			return true;
		}
		return false;
	}

	public HasBuilder getOwner() {
		return getSettings().getOwner();
	}

	public void setOwner(HasBuilder owner) {
		getSettings().setOwner(owner);
	}
	
	public List<BuildProject> getProjects(){
		return getSettings().getProjects();
	}
	
	public BuildProject getCurrentProject(){
		return currentProject;
	}
	
	private Instant lastWarning = Instant.now();
	private void warnLackOfSupplies(MaterialAndData supply){
		if(getOwner() != null && lastWarning.isBefore(Instant.now().minusSeconds(60))){
			FancyMessage notification = new FancyMessage(((Group)getOwner()).getChatHeader());
			notification.then("Warehouses lack of ")
			.then(Util.prettifyText(Util.getMaterialName(supply.getDefault())))
			.tooltip("Actual block : " + Util.getMaterialName(supply))
			.color(ChatColor.GOLD)
			.then("! Click ")
			.then("HERE").command("/group " + ((Group)getOwner()).getUniqueId().toString() + " builder skip " + supply.getMaterial().toString() + " " + supply.getData())
			.color(ChatColor.GOLD)
			.then(" to skip this type of block.");
			((Group)getOwner()).sendMessage(notification, PermissionType.BLUEPRINT_NOTIFICATIONS);
			lastWarning = Instant.now();
		}
	}
	
	private void warnProjectComleted(BuildProject currentProject2) {
		if(getOwner() != null){
			getOwner().sendNotification(Level.ALL, "Build project completed!");
		}
	}

	public BuilderSettings getSettings() {
		return settings;
	}

	public static Builder get(UUID id) {
		Iterator<Builder> it = builders.iterator();
		while(it.hasNext()){
			Builder b = it.next();
			if(b!=null){
				if(b.getSettings().getUniqueId().equals(id)) return b;
			}
		}
		return null;
	}

	public void clearProjects() {
		List<BuildProject> p = new ArrayList<BuildProject>();
		getSettings().setProjects(p);
		currentProject = null;
	}
	
}
