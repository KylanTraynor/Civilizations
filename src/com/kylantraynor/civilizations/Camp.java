package com.kylantraynor.civilizations;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import mkremins.fanciful.FancyMessage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.permissions.Permission;
import com.kylantraynor.civilizations.permissions.PermissionTarget;
import com.kylantraynor.civilizations.permissions.PermissionType;
import com.kylantraynor.civilizations.questions.ClearQuestion;
import com.kylantraynor.civilizations.questions.JoinQuestion;
import com.kylantraynor.civilizations.questions.LeaveQuestion;
import com.kylantraynor.civilizations.shapes.Sphere;

public class Camp extends Settlement {
	
	public static String messageHeader = ChatColor.GOLD + "[" + ChatColor.GREEN + "CAMP" + ChatColor.GOLD + "] ";
	private Instant expireOn;
	
	public Camp(Location l) {
		super(l);
		this.getProtection().add(new Sphere(l, Camp.getSize()), false);
		Cache.campListChanged = true;
		setChatColor(ChatColor.GREEN);
	}
	
	@Override
	public boolean isUpgradable(){
		return false;
	}
	
	@Override
	public Type getType(){
		return Type.CAMP;
	}

	@Override
	public String getIcon(){
		return "camp";
	}
	
	@Override
	public String getName(){
		return "Camp";
	}
	
	@Override
	public void update(){
		if(Instant.now().isAfter(expireOn)) remove();
		super.update();
	}
	
	@Override
	public boolean remove(){
		Cache.campListChanged = true;
		return super.remove();
	}

	public static void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) args = new String[]{"INFO"};
		switch (args[0].toUpperCase()){
		case "HERE":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp closest = getClosest(p.getLocation());
				if(closest != null){
					if(closest.getLocation().distance(p.getLocation()) <= getSize() * 2){
						p.sendMessage(messageHeader + ChatColor.RED + "Too close to another camp.");
						return;
					}
				}
				Camp c = new Camp(p.getLocation());
				c.addMember(p);
				c.setCreationDate(Instant.now());
				c.setDefaultPermissions();
				c.setExpireOn(Instant.now().plus(1, ChronoUnit.DAYS));
				p.sendMessage(messageHeader + ChatColor.GREEN + "Camp created!");
				p.sendMessage(messageHeader + ChatColor.GREEN + "Camps only last a day. Make sure to Renew it on the " + ChatColor.GOLD + "/camp" + ChatColor.GREEN + " screen!");
			}
			break;
		case "RENEW":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					p.sendMessage(messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					if(ChronoUnit.HOURS.between(Instant.now(), c.getExpireOn()) > 22){
						p.sendMessage(messageHeader + ChatColor.RED + "You can only renew the camp once a day.");
					} else {
						c.setExpireOn(Instant.now().plus(1, ChronoUnit.DAYS));
						p.sendMessage(messageHeader + ChatColor.GREEN + "Camp renewed for a day!");
					}
				}
			}
			break;
		case "CLEAR":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					p.sendMessage(messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					new ClearQuestion(c, p).ask();
				}
			}
			break;
		case "JOIN":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(c.isMember(p)){
					p.sendMessage(messageHeader + ChatColor.RED + "You're already part of this camp.");
				} else {
					if(c.hasOneMemberOnline()){
						p.sendMessage(messageHeader + ChatColor.BLUE + "You've requested to join this camp. Please wait for an answer.");
						new JoinQuestion(c, p).ask();
					} else {
						p.sendMessage(messageHeader + ChatColor.RED + "No member of this camp is online to accept your request.");
					}
				}
			}
			break;
		case "LEAVE":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(messageHeader + ChatColor.RED + "There is no camp here.");
				} else if(!c.isMember(p)){
					p.sendMessage(messageHeader + ChatColor.RED + "You're not part of this camp.");
				} else {
					new LeaveQuestion(c, p).ask();
				}
			}
			break;
		case "MEMBERS":
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					p.chat("/group " + c.getId() + " members");
				}
			}
			break;
		
		case "INFO": default:
			if(sender instanceof Player){
				Player p = (Player) sender;
				Camp c = Camp.getCampAt(p.getLocation());
				if(c == null){
					p.sendMessage(messageHeader + ChatColor.RED + "There is no camp here.");
				} else {
					c.getCampFancyInfo(p).send(p);
				}
			}
			break;
		}
	}
	
	private void setDefaultPermissions() {
		Protection p = this.getProtection();
		Map<PermissionType, Boolean> resPerm = new HashMap<PermissionType, Boolean>();
		Map<PermissionType, Boolean> serverPerm = new HashMap<PermissionType, Boolean>();
		
		resPerm.put(PermissionType.BREAK, true);
		resPerm.put(PermissionType.PLACE, true);
		resPerm.put(PermissionType.FIRE, true);
		resPerm.put(PermissionType.INVITE, true);
		
		serverPerm.put(PermissionType.EXPLOSION, false);
		serverPerm.put(PermissionType.FIRE, true);
		serverPerm.put(PermissionType.FIRESPREAD, false);
		serverPerm.put(PermissionType.DEGRADATION, false);
		serverPerm.put(PermissionType.MOBSPAWNING, false);
		
		p.setPermission(new Permission(this, PermissionTarget.MEMBERS, null, resPerm));
		p.setPermission(new Permission(this, PermissionTarget.SERVER, null, serverPerm));
	}

	private FancyMessage getCampFancyInfo(Player p) {
		FancyMessage fm = new FancyMessage("============ CAMP ============")
			.color(ChatColor.GOLD)
			.then("\nProtection expires in ").color(ChatColor.GRAY)
			.then("" + ChronoUnit.HOURS.between(Instant.now(), getExpireOn()) + " hours").color(ChatColor.GOLD)
			.then("\nMembers: ").color(ChatColor.GRAY)
			.command("/group " + this.getId() + " members")
			.then("" + getMembers().size()).color(ChatColor.GOLD)
			.command("/group " + this.getId() + " members")
			.then("\nActions: ").color(ChatColor.GRAY);
		if(this.isMember(p)){
			fm.then("\nClear").color(ChatColor.GOLD).tooltip("Clear camp").command("/camp clear");
			fm.then(" - ").color(ChatColor.GRAY);
			fm.then("Leave").color(ChatColor.GOLD).tooltip("Leave the camp").command("/camp leave");
			fm.then(" - ").color(ChatColor.GRAY);
			if(ChronoUnit.HOURS.between(Instant.now(), this.getExpireOn()) > 22){
				fm.then("Renew").color(ChatColor.GRAY).tooltip("Wait for an hour before renewing");
			} else {
				fm.then("Renew").color(ChatColor.GOLD).tooltip("Keep the camp for one more day").command("/camp renew");
			}
			fm.then(" - ").color(ChatColor.GRAY);
			if(isUpgradable()){
				fm.then("Upgrade").color(ChatColor.GOLD).tooltip("Upgrade the camp").command("/camp upgrade");
			} else {
				fm.then("Upgrade").color(ChatColor.GRAY).tooltip("No upgrade available");
			}
		} else {
			fm.then("\nJoin");
			if(hasOneMemberOnline()){
				fm.color(ChatColor.GOLD).tooltip("Ask members of the camp if you can join.").command("/camp join");
			} else {
				fm.color(ChatColor.RED).tooltip("One member needs to be online to join the camp.");
			}
		}
		fm.then("\n==============================").color(ChatColor.GOLD);
		return fm;
	} 

	private static Camp getCampAt(Location location) {
		for(Camp c : getCampList()){
			if(c.protects(location)){
				return c;
			}
		}
		return null;
	}

	private static int getSize() {
		return 8;
	}

	public Instant getExpireOn() {
		return expireOn;
	}

	public void setExpireOn(Instant expireOn) {
		this.expireOn = expireOn;
		setChanged(true);
	}
	
	public static List<Camp> getCampList(){
		return Cache.getCampList();
	}
	
	@Override
	public File getFile(){
		File f = new File(Civilizations.getCampDirectory(), "" + this.getId() + ".yml");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	
	public static Camp load(YamlConfiguration cf){
		World w = Civilizations.currentInstance.getServer().getWorld(cf.getString("Location.World"));
		double x = cf.getDouble("Location.X");
		double y = cf.getDouble("Location.Y");
		double z = cf.getDouble("Location.Z");
		Instant creation;
		Instant expireOn;
		if(cf.getString("Creation") != null){
			creation = Instant.parse(cf.getString("Creation"));
		} else {
			creation = Instant.now();
			Civilizations.currentInstance.log(Level.WARNING, "Couldn't find creation date for a group. Replacing it by NOW.");
		}
		if(cf.getString("ExpireOn") != null){
			expireOn = Instant.parse(cf.getString("ExpireOn"));
		} else {
			expireOn = Instant.now().plus(1, ChronoUnit.DAYS);
			Civilizations.currentInstance.log(Level.WARNING, "Couldn't find creation date for a group. Replacing it by 1 day from NOW.");
		}
		
		Camp c = new Camp(new Location(w, x, y, z));
		c.setCreationDate(creation);
		c.setExpireOn(expireOn);
		
		int i = 0;
		while(cf.contains("Members." + i)){
			c.getMembers().add(UUID.fromString((cf.getString("Members."+i))));
			i+=1;
		}
		
		return c;
	}
	
	@Override
	public boolean save(){
		File f = getFile();
		if(f == null) return false;
		YamlConfiguration fc = new YamlConfiguration();
		
		fc.set("Location.World", getLocation().getWorld().getName());
		fc.set("Location.X", getLocation().getBlockX());
		fc.set("Location.Y", getLocation().getBlockY());
		fc.set("Location.Z", getLocation().getBlockZ());
		
		fc.set("Creation", getCreationDate().toString());
		fc.set("ExpireOn", getExpireOn().toString());
		
		int i = 0;
		for(UUID id : getMembers()){
			fc.set("Members." + i, id.toString());
			i += 1;
		}
		
		try {
			fc.save(f);
			setChanged(false);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static Camp getClosest(Location l){
		Double distance = null;
		Camp closest = null;
		for(Camp s : getCampList()){
			if(distance == null){
				closest = s;
			} else if(distance > l.distance(s.getLocation())) {
				distance = l.distance(s.getLocation());
				closest = s;
			}
		}
		return closest;
	}
}
