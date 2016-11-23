package com.kylantraynor.civilizations.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.builder.Blueprint;
import com.kylantraynor.civilizations.builder.BuildProject;
import com.kylantraynor.civilizations.builder.HasBuilder;
import com.kylantraynor.civilizations.groups.Group;

public class BuilderSettings extends YamlConfiguration{
	
	private List<BuildProject> projects;
	private boolean hasChanged = true;
	private HasBuilder owner = null;
	private UUID id;

	public void setOwner(HasBuilder owner){
		if(owner == null){
			this.set("Owner", null);
		} else {
			this.set("Owner", owner.getUniqueId().toString());
		}
		this.owner = owner;
		setChanged(true);
	}
	
	public HasBuilder getOwner(){
		if(owner != null) return owner;
		try {
			String ids = this.getString("Owner");
			if(ids == null) return null;
			UUID id = UUID.fromString(ids);
			for(Group g : Group.getList()){
				if(g instanceof HasBuilder){
					if(g.getUniqueId().equals(id)){
						owner = (HasBuilder) g;
						return (HasBuilder)g;
					}
				}
			}
			return null;
		} catch (IllegalArgumentException e){
			return null;
		}
	}
	
	public void setUniqueId(UUID id){
		this.set("ID", id.toString());
		this.id = id;
		setChanged(true);
	}
	
	public UUID getUniqueId(){
		if(id != null) return id;
		String ids = this.getString("ID");
		if(ids != null){
			try{
				return UUID.fromString(ids);
			} catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
		setUniqueId(UUID.randomUUID());
		return id;
	}
	
	/**
	 * Sets the list of projects of the builder.
	 * @param list
	 */
	public void setProjects(List<BuildProject> list){
		if(list == null || list.isEmpty()){
			projects = new ArrayList<BuildProject>();
			this.set("Projects", null);
		} else {
			this.projects = list;
			this.set("Projects", null);
			for(int i = 0; i < projects.size(); i++){
				this.set("Projects." + i + ".id", projects.get(i).getBlueprint().getUniqueId().toString());
				this.set("Projects." + i + ".world", projects.get(i).getLocation().getWorld().getName());
				this.set("Projects." + i + ".x", projects.get(i).getLocation().getBlockX());
				this.set("Projects." + i + ".y", projects.get(i).getLocation().getBlockY());
				this.set("Projects." + i + ".z", projects.get(i).getLocation().getBlockZ());
				this.set("Projects." + i + ".currentX", projects.get(i).getCurrentX());
				this.set("Projects." + i + ".currentY", projects.get(i).getCurrentY());
				this.set("Projects." + i + ".currentZ", projects.get(i).getCurrentZ());
				this.set("Projects." + i + ".rotation", projects.get(i).getRotation());
				this.set("Projects." + i + ".setAir", projects.get(i).setAir());
				projects.get(i).getBlueprint().save();
			}
		}
		setChanged(true);
	}
	
	public List<BuildProject> getProjects(){
		if(projects != null) return projects;
		ConfigurationSection cs = null;
		projects = new ArrayList<BuildProject>();
		int i = 0;
		while((cs = this.getConfigurationSection("Projects." + i)) != null){
			World w = Bukkit.getServer().getWorld(cs.getString("world"));
			if(w == null) continue;
			int x = cs.getInt("x");
			int y = cs.getInt("y");
			int z = cs.getInt("z");
			Location l = new Location(w, x, y, z);
			UUID bpId = UUID.fromString(cs.getString("id"));
			Blueprint bpt = Blueprint.get(bpId);
			if(bpt == null) continue;
			boolean setAir = cs.getBoolean("setAir");
			BuildProject bp = new BuildProject(l, bpt, setAir);
			int cx = cs.getInt("currentX");
			int cy = cs.getInt("currentY");
			int cz = cs.getInt("currentZ");
			int r = cs.getInt("rotation");
			bp.setRotation(r);
			bp.setCurrent(cx, cy, cz);
			projects.add(bp);
		}
		return projects;
	}
	
	private void setChanged(boolean b) {
		hasChanged = b;
	}
	
	public boolean hasChanged(){
		return hasChanged;
	}
	
	public void save(){
		File f = new File(Civilizations.getBuilderDirectory(), this.getUniqueId().toString() + ".yml");
		this.save(f);
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
}
