package com.kylantraynor.civilizations.managers;

import com.kylantraynor.civilizations.groups.settlements.Camp;
import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.groups.settlements.plots.Plot;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;
import com.sun.istack.internal.NotNull;
import org.bukkit.Location;

import javax.annotation.Nullable;

public class SettlementManager {
	/**
	 * Adds a plot to the given settlement and updates
	 * the settlement type if it used to be a camp.
	 * @param s as Settlement
	 * @param p as Plot
	 */
	public static void addPlotAndUpdate(Settlement s, Plot p){
		Settlement settlement = s;
		if(s instanceof Camp){
			if(p.getPlotType() == PlotType.HOUSE){
				settlement = updateCampToSettlement((Camp) s);
			}
		}
		addPlot(settlement,p);
	}

	/**
	 * Adds a plot to the given settlement and updates 
	 * the settlement's plots list.
	 * @param s as Settlement
	 * @param p as Plot
	 * @return true if the plot has been added, false otherwise.
	 */
	public static boolean addPlot(Settlement s, Plot p){
		if(p == null) throw new NullPointerException();
		if(s == null) throw new NullPointerException();
		
		if(s instanceof Camp){
			return false;
		}
		
		Settlement old = p.getSettlement();
		if(old != null){
			old.removePlot(p);
		}
		
		boolean result = s.addPlot(p);
		p.setSettlement(s);
		
		return result;
	}
	
	/**
	 * Removes a plot completely (updates its settlement plots list too).
	 * @param p
	 */
	@SuppressWarnings("deprecation")
	public static void removePlot(Plot p){
		if(p == null) throw new NullPointerException();
		Settlement s = p.getSettlement();
		if(s != null){
			s.removePlot(p);
		}
		p.remove();
	}
	
	public static Settlement updateCampToSettlement(Camp c){
		Settlement result = new Settlement(c.getLocation());
		
		result.setMembers(c.getMembers());
		result.setName(c.getName());
		result.setUniqueId(c.getUniqueId());
		result.getSettings().setCreationDate(c.getSettings().getCreationDate());
		
		return c;
	}

    /**
     * Returns the {@linkplain Settlement} at the given {@linkplain Location} if one exists there.
     * @param location as {@link Location}
     * @return the {@link Settlement} or Null.
     */
	public static Settlement getSettlementAt(@NotNull Location location){
	    if(location == null) throw new NullPointerException("Location can't be Null!");
		return Settlement.getAt(location);
	}

    /**
     * Returns the {@linkplain Plot} at the given {@linkplain Location} if one exists there.
     * @param location as {@link Location}
     * @return the {@link Plot} or Null.
     */
	public static Plot getPlotAt(@NotNull Location location){
	    if(location == null) throw new NullPointerException("Location can't be Null!");
	    return Plot.getAt(location);
    }
}
