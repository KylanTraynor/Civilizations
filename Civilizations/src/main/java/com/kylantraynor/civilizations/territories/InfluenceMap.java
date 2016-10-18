package com.kylantraynor.civilizations.territories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.forts.Fort;
import com.kylantraynor.civilizations.hook.dynmap.DynmapHook;
import com.kylantraynor.civilizations.hook.worldborder.WorldBorderHook;
import com.kylantraynor.voronoi.VCell;
import com.kylantraynor.voronoi.VSite;
import com.kylantraynor.voronoi.VectorXZ;
import com.kylantraynor.voronoi.Voronoi;

public class InfluenceMap {
	
	private Voronoi voronoi;
	private int oceanLevel = 48;
	private Map<VSite, InfluentSite> influentSites = new HashMap<VSite, InfluentSite>();
	
	public void generateFull(){
		for(Settlement s : Settlement.getSettlementList())
			if(s instanceof InfluentSite){
				influentSites.put(
						new VSite(
								((InfluentSite) s).getX(),
								((InfluentSite) s).getZ(),
								((InfluentSite) s).getInfluence()),
								(InfluentSite) s);
			}
		World w = Bukkit.getWorld("world");
		VSite[] a = influentSites.keySet().toArray(new VSite[influentSites.size()]);
		if(w == null) return;
		float xCenter = (float) WorldBorderHook.getWorldCenter(w).getX();
		float xRadius = WorldBorderHook.getWorldRadiusX(w);
		float zCenter = (float) WorldBorderHook.getWorldCenter(w).getZ();
		float zRadius = WorldBorderHook.getWorldRadiusZ(w);
		float xmin = xCenter - xRadius;
		float zmin = zCenter - zRadius;
		float xmax = xCenter + xRadius;
		float zmax = zCenter + zRadius;
		voronoi =  new Voronoi(a, xmin, zmin, xmax, zmax);
		voronoi.generate();
		if(DynmapHook.isEnabled()){
			DynmapHook.updateInfluenceMap(this);
		}
	}
	
	public boolean isGenerated(){
		if(voronoi == null)
			return false;
		else
			return voronoi.isDone();
	}
	
	public InfluentSite getInfluentSiteAt(Location l){
		VectorXZ location = new VectorXZ((float) l.getX(), (float)l.getZ());
		if(isGenerated()){
			VCell c = voronoi.getCellAt(location);
			VSite s = null;
			if(c != null)
				s = c.getSite();
			if(s != null)
				return influentSites.get(s);
		}
		return null;
	}
	
	/*public Fort getInfluentFortAt(Location l){
		Fort influent = null;
		double influence = 0.0;
		for(Fort f : Fort.getAll()){
			double finf = getFortInfluenceAt(f, l);
			if(influence < finf){
				influence = finf;
				influent = f;
			}
		}
		if(influence > 0.0){
			return influent;
		} else {return null;}
	}
	*/
	
	public double getFortInfluenceAt(Fort f, Location l){
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
		
		double totalCoeff = xzCoeff - yCoeff;
		double result = Math.max(((double)f.getInfluence()) - totalCoeff * 0.001, 0.0);
		return result;
	}
	
	public VCell[] getCells(){
		if(isGenerated()){
			return voronoi.getCells();
		} else {
			return null;
		}
	}
}
