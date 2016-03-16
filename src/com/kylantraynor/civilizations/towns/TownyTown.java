package com.kylantraynor.civilizations.towns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.Cache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.Group;
import com.kylantraynor.civilizations.Settlement;
import com.kylantraynor.civilizations.permissions.Permission;
import com.kylantraynor.civilizations.permissions.PermissionTarget;
import com.kylantraynor.civilizations.permissions.PermissionType;
import com.kylantraynor.civilizations.plots.Plot;
import com.kylantraynor.civilizations.shapes.Prism;
import com.kylantraynor.civilizations.shapes.Shape;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;

public class TownyTown extends Settlement{
	
	public static HashMap<Resident, UUID> residentCache = new HashMap<Resident, UUID>();
	
	public static void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"Null", "INFO"};
		if(!args[0].equals("Null") && args.length >= 2){
			TownyTown t = TownyTown.get(args[0]);
			if(t == null) return;
			switch(args[1].toUpperCase()){
			case "INFO":
				
				break;
			case "MEMBERS":
				if(sender instanceof Player){
					Player p = (Player) sender;
					if(args.length > 2){
						t.getInteractiveMembersList(Integer.getInteger(args[2])).send(p);
					} else {
						t.getInteractiveMembersList().send(p);
					}
				}
				break;
			}
		}
	}
	
	private static TownyTown get(String string) {
		for(TownyTown t : getTownyTownList()){
			if(t.getName().equalsIgnoreCase(string)){
				return t;
			}
		}
		return null;
	}

	Town townyTown;
	
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
					
					p.getProtection().setPermission(new Permission(this, PermissionTarget.MEMBERS, null, resPerm));
					p.getProtection().setPermission(new Permission(this, PermissionTarget.ALLIES, null, allyPerm));
					p.getProtection().setPermission(new Permission(this, PermissionTarget.OUTSIDERS, null, outsiderPerm));
					this.addPlot(p);
					this.getProtection().add(s);
				} else {
					this.getProtection().add(s, false);
				}
			}
			i++;
			if(i % 50 == 0){
				Civilizations.currentInstance.log(Level.INFO, "Loading " + t.getName() + ": " + Math.round(((double)i/tl.size())*100) + "% (" + i + "/"+ tl.size()+ ")");
			}
		}
		Cache.townyTownListChanged = true;
	}
	
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
	
	private void importTownPermissions() {
		Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> allyPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> outsiderPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> serverPerm = new HashMap<PermissionType, Boolean>();
		
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
		
		getProtection().setPermission(new Permission(this, PermissionTarget.MEMBERS, null, resPerm));
		getProtection().setPermission(new Permission(this, PermissionTarget.ALLIES, null, allyPerm));
		getProtection().setPermission(new Permission(this, PermissionTarget.OUTSIDERS, null, outsiderPerm));
		getProtection().setPermission(new Permission(this, PermissionTarget.SERVER, null, serverPerm));
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
			if(residentCache.containsKey(r)){
				list.add(residentCache.get(r));
			} else {
				OfflinePlayer p = Bukkit.getServer().getOfflinePlayer((r.getName()));
				if(p != null){
					residentCache.put(r, p.getUniqueId());
					list.add(p.getUniqueId());
				} else {
					Civilizations.currentInstance.log(Level.WARNING, "Couldn't find player for resident " + r.getName() + ".");
				}
			}
		}
		return list;
	}
	
	@Override
	public boolean addMember(OfflinePlayer p){
		return false;
	}
}
