package com.kylantraynor.civilizations.hook.towny;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.protection.Permission;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;
import com.kylantraynor.civilizations.protection.TargetType;
import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

public class TownyTown extends Settlement{
	
	static TownyTown get(String string) {
		for(TownyTown t : getTownyTownList()){
			if(t.getName().equalsIgnoreCase(string)){
				return t;
			}
		}
		return null;
	}

	Town townyTown;
	/**
	 * Gets the cached list of Towns from Towny.
	 * @return List<TownyTown> of Towns.
	 */
	public static List<TownyTown> getTownyTownList(){
		return Cache.getTownyTownList();
	}

	public TownyTown(Location l) {
		super(l);
		Cache.townyTownListChanged = true;
	}
	
	public TownyTown(Town t) throws TownyException{
		super(t.getSpawn());
		this.townyTown = t;
		List<TownBlock> tl = t.getTownBlocks();
		importTownPermissions();
		int i = 0;
		for(TownBlock tb : tl){
			World w = Bukkit.getServer().getWorld(tb.getWorld().getName());
			if(w == null){
				throw new TownyException();
			} else {
				int x = tb.getX() * Coord.getCellSize();
				int z = tb.getZ() * Coord.getCellSize();
				Location l = new Location(w, x, 0, z);
				int width = Coord.getCellSize();
				int length = Coord.getCellSize();
				Shape s = new Prism(l, width, 255, length);
				if(isPlot(tb)){
					Plot p = new Plot(tb.getName(), s, this);
					
					Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
					Map<PermissionType, Boolean> allyPerm = new HashMap<PermissionType, Boolean>();
					Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
					
					if(tb.getPermissions().residentBuild != t.getPermissions().residentBuild) resPerm.put(PermissionType.PLACE, tb.getPermissions().residentBuild);
					if(tb.getPermissions().residentDestroy != t.getPermissions().residentDestroy) resPerm.put(PermissionType.BREAK, tb.getPermissions().residentDestroy);
					
					if(tb.getPermissions().allyBuild != t.getPermissions().allyBuild) allyPerm.put(PermissionType.PLACE, tb.getPermissions().allyBuild);
					if(tb.getPermissions().allyDestroy != t.getPermissions().allyDestroy) allyPerm.put(PermissionType.BREAK, tb.getPermissions().allyDestroy);
					
					if(tb.getPermissions().outsiderBuild != t.getPermissions().outsiderBuild) outsiderPerm.put(PermissionType.PLACE, tb.getPermissions().outsiderBuild);
					if(tb.getPermissions().outsiderDestroy != t.getPermissions().outsiderDestroy) outsiderPerm.put(PermissionType.BREAK, tb.getPermissions().outsiderDestroy);
					
					p.getProtection().setPermissions(new PermissionTarget(TargetType.MEMBERS), new Permission(this, resPerm));
					p.getProtection().setPermissions(new PermissionTarget(TargetType.ALLIES), new Permission(this, allyPerm));
					p.getProtection().setPermissions(new PermissionTarget(TargetType.OUTSIDERS), new Permission(this, outsiderPerm));
					this.addPlot(p);
					this.getProtection().add(s);
				} else {
					this.getProtection().add(s, false);
				}
			}
			i++;
			if(i % 50 == 0){
				Civilizations.log("INFO", "Loading " + t.getName() + ": " + Math.round(((double)i/tl.size())*100) + "% (" + i + "/"+ tl.size()+ ")");
			}
		}
		Cache.townyTownListChanged = true;
	}
	/**
	 * Checks if the given TownBlock is a plot or just part of the town's protection.
	 * @param tb
	 * @return true if the TownBlock is a plot, false otherwise.
	 */
	private boolean isPlot(TownBlock tb){
		if(tb.getPermissions().residentBuild != townyTown.getPermissions().residentBuild) return true;
		if(tb.getPermissions().residentDestroy != townyTown.getPermissions().residentDestroy) return true;
		if(tb.getPermissions().allyBuild != townyTown.getPermissions().allyBuild) return true;
		if(tb.getPermissions().allyDestroy != townyTown.getPermissions().allyDestroy) return true;
		if(tb.getPermissions().outsiderBuild != townyTown.getPermissions().outsiderBuild) return true;
		if(tb.getPermissions().outsiderDestroy != townyTown.getPermissions().outsiderDestroy) return true;
		if(!tb.getName().isEmpty()) return true;
		if(tb.isForSale()) return true;
		if(tb.hasResident()) return true;
		return false;
		
	}
	/**
	 * Imports permissions from Towny.
	 */
	private void importTownPermissions() {
		Map<PermissionType, Boolean> mayorPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> assistantPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> allyPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> serverPerm = new HashMap<PermissionType, Boolean>();
		
		mayorPerm.put(PermissionType.PLACE, true);
		mayorPerm.put(PermissionType.BREAK, true);
		mayorPerm.put(PermissionType.INVITE, true);
		mayorPerm.put(PermissionType.KICK, true);
		mayorPerm.put(PermissionType.CLAIM, true);
		mayorPerm.put(PermissionType.UNCLAIM, true);
		mayorPerm.put(PermissionType.UPGRADE, true);
		
		assistantPerm.put(PermissionType.UPGRADE, false);
		
		resPerm.put(PermissionType.PLACE, this.townyTown.getPermissions().residentBuild);
		resPerm.put(PermissionType.BREAK, this.townyTown.getPermissions().residentDestroy);
		
		allyPerm.put(PermissionType.PLACE, this.townyTown.getPermissions().allyBuild);
		allyPerm.put(PermissionType.BREAK, this.townyTown.getPermissions().allyDestroy);
		
		outsiderPerm.put(PermissionType.PLACE, this.townyTown.getPermissions().outsiderBuild);
		outsiderPerm.put(PermissionType.BREAK, this.townyTown.getPermissions().outsiderDestroy);
		
		serverPerm.put(PermissionType.EXPLOSION, false);
		serverPerm.put(PermissionType.FIRE, true);
		serverPerm.put(PermissionType.FIRESPREAD, false);
		serverPerm.put(PermissionType.DEGRADATION, false);
		serverPerm.put(PermissionType.MOBSPAWNING, false);
		
		Rank mayor = new Rank("Mayor", null);
		mayor.addPlayer(TownyHook.getPlayer(townyTown.getMayor()));
		
		Rank assistant = new Rank("Assistant", mayor);
		for(Resident r : townyTown.getResidents()){
			for(String rank : r.getTownRanks()){
				if(rank.equalsIgnoreCase(assistant.getName())){
					assistant.addPlayer(TownyHook.getPlayer(r));
				}
			}
		}
		
		getProtection().setPermissions(mayor, new Permission(this, mayorPerm));
		getProtection().setPermissions(assistant, new Permission(this, assistantPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.MEMBERS), new Permission(this, resPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.ALLIES), new Permission(this, allyPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.OUTSIDERS), new Permission(this, outsiderPerm));
		getProtection().setPermissions(new PermissionTarget(TargetType.SERVER), new Permission(this, serverPerm));
	}

	@Override
	public Type getType(){
		return Type.TOWNY;
	}

	@Override
	public String getIcon(){
		return "townhall";
	}
	
	@Override
	public String getName(){
		return this.townyTown.getName();
	}
	
	@Override
	public boolean remove(){
		Cache.townyTownListChanged = true;
		return super.remove();
	}
	
	@Override
	public boolean save(){
		return false;
	}
	
	@Override
	public List<UUID> getMembers(){
		List<UUID> list = new ArrayList<UUID>();
		for(Resident r : this.townyTown.getResidents()){
			OfflinePlayer p = TownyHook.getPlayer(r);
			if(p != null){
				list.add(p.getUniqueId());
			}
		}
		return list;
	}
	
	@Override
	public boolean addMember(OfflinePlayer p){
		return false;
	}
}
