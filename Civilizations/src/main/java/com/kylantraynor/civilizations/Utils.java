package com.kylantraynor.civilizations;

import java.text.ParseException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
	public static Location parseLocation(String s){
		String[] ss = s.split(",");
		try {
			if(ss.length != 6){
				throw new ParseException("Couldn't parse location from: " + s + ".", 0);
			} else {
				World w = Bukkit.getWorld(UUID.fromString(ss[0]));
				double x = Double.parseDouble(ss[1]);
				double y = Double.parseDouble(ss[2]);
				double z = Double.parseDouble(ss[3]);
				float yaw = Float.parseFloat(ss[4]);
				float pitch = Float.parseFloat(ss[5]);
				return new Location(w, x, y, z, yaw, pitch);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String locationToString(Location loc){
		String format = "%s,%d,%d,%d,%f,%f";
		return String.format(format, loc.getWorld().getUID().toString(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
}
