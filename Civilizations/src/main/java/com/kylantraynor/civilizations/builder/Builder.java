package com.kylantraynor.civilizations.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.settings.BuilderSettings;
import com.kylantraynor.civilizations.settings.GroupSettings;
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
		if(getProjects().isEmpty()) return;
		if(currentProject == null) currentProject = getProjects().get((int) Math.floor((Math.random() * getProjects().size())));
		
		if(currentProject.isDone()){
			removeProject(currentProject);
			warnProjectComleted(currentProject);
			currentProject = null;
			return;
		}
		
		ItemStack plan = currentProject.getNext();
		if(plan == null){
			currentProject = null;
			return;
		} else if(Util.getItemFromBlock(plan) == null){
			currentProject.buildNext();
			this.getSettings().setChanged(true);
		}
		ItemStack supply = getSupplies(plan);
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
	
	private void warnLackOfSupplies(ItemStack supply){
		if(getOwner() != null){
			getOwner().sendNotification(Level.INFO, "Warehouses lack of " + Util.prettifyText(Util.getMaterialName(supply)) + "!");
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
	
}
