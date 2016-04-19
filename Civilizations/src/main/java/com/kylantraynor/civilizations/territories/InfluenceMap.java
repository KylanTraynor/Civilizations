package com.kylantraynor.civilizations.territories;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.hook.worldborder.WorldBorderHook;

public class InfluenceMap {
	
	private static int oceanLevel = 48;
	private static int precision = 128; // (1 px = 128 blocks)
	private static Map<Fort, BufferedImage> image = new HashMap<Fort, BufferedImage>();
	
	
	public static BufferedImage getImage(Fort f){
		if(!image.containsKey(f)){
			if(WorldBorderHook.isActive()){
				if(WorldBorderHook.getWorldRadiusX(f.getLocation().getWorld()) == 0) return null;
				image.put(f, new BufferedImage(
						WorldBorderHook.getWorldRadiusX(f.getLocation().getWorld()) * 2 / precision,
						WorldBorderHook.getWorldRadiusZ(f.getLocation().getWorld()) * 2 / precision,
						BufferedImage.TYPE_BYTE_GRAY));
				return image.get(f);
			} else {
				return null;
			}
		} else {
			return image.get(f);
		}
	}
	/**
	 * Saves the influence Map.
	 * @param f
	 */
	public static void saveInfluenceMap(Fort f){
		BufferedImage img = getImage(f);
		if(img != null){
			File file = new File(Civilizations.currentInstance.getDataFolder(), f.getName() + " Influence.jpg");
			try {
				ImageIO.write(img, "JPEG", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Fort getInfluentFortAt(Location l){
		Fort influent = null;
		double influence = 0.0;
		for(Fort f : Fort.getAll()){
			double finf = getFortInfluenceAt(f, l);
			if(influence < finf){
				influence = finf;
				influent = f;
			}
		}
		return influent;
	}
	
	public static double getFortInfluenceAt(Fort f, Location l){
		if(!f.getLocation().getWorld().equals(l.getWorld())) return 0.0;
		l = l.clone();
		l.setY(255);
		while(l.getBlock().getType() == Material.AIR || l.getBlock().isLiquid()){
			l.setY(l.getY() - 1);
			if(l.getY() < oceanLevel - 1) break;
		}
		
		if(l.getBlock().getBiome() == Biome.OCEAN || l.getBlock().getBiome() == Biome.DEEP_OCEAN){
			if(l.getY() < oceanLevel - 1) return 0.0;
		}
		
		double fx = f.getLocation().getX();
		double fy = f.getLocation().getY();
		double fz = f.getLocation().getZ();
		
		double xzCoeff = Math.sqrt((fx - l.getX()) * (fx - l.getX()) + (fz - l.getZ()) * (fz - l.getZ()));
		double yCoeff = fy - l.getY();
		
		double totalCoeff = Math.max(xzCoeff - yCoeff, 0.1);
		double result = Math.min(Math.max((f.getInfluence() * 100.0) / totalCoeff, 0.1), 100.0);
		BufferedImage img = getImage(f);
		if(img != null){
			imgSetPixelAtLocation(l, img, (int)(result * 255 / 100));
		}
		return result;
	}
	
	public static void imgSetPixelAtLocation(Location l, BufferedImage img, int data){
		if(!WorldBorderHook.isActive()) return;
		if(img == null) return;
		if(l == null) return;
		
		int imgX = 0;
		int imgY = 0;
		
		int imgMinX = -WorldBorderHook.getWorldRadiusX(l.getWorld());
		imgMinX += WorldBorderHook.getWorldCenter(l.getWorld()).getBlockX();
		
		int imgMinZ = -WorldBorderHook.getWorldRadiusZ(l.getWorld());
		imgMinZ += WorldBorderHook.getWorldCenter(l.getWorld()).getBlockZ();
		
		imgX = l.getBlockX() - imgMinX;
		imgY = l.getBlockZ() - imgMinZ;
		
		imgX /= precision;
		imgY /= precision;
		
		Bukkit.getServer().getLogger().log(Level.INFO, "Writing in image at " + imgX + ", " + imgY + ". (" + data + ")");
		
		img.setRGB(imgX, imgY, data);
	}
	
}
