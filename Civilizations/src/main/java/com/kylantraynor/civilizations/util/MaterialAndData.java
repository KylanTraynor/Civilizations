package com.kylantraynor.civilizations.util;

import org.bukkit.Material;

public class MaterialAndData {
	final Material material;
	final byte data;
	
	public MaterialAndData(Material material, byte data){
		this.material = material;
		this.data = data;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public byte getData(){
		return data;
	}
}
