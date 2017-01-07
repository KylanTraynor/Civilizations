package com.kylantraynor.civilizations.territories;

import com.kylantraynor.voronoi.VCell;
import com.kylantraynor.voronoi.VSite;
import com.kylantraynor.voronoi.Voronoi;

public class InfluenceCell extends VCell {

	public InfluenceCell(VSite site, Voronoi<InfluenceCell> v) {
		super(site, v);
	}

}
