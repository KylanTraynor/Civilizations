package com.kylantraynor.civilizations.banners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Banner {
	
	static private List<Banner> all = new ArrayList<Banner>();
	
	List<Pattern> patterns;
	private org.bukkit.block.Banner banner;
	private Material base;

    public static boolean exist(Material base, BannerMeta meta){
        for(Banner b : all){
            if(b.isSimilar(base, meta)) return true;
        }
        return false;
    }

	public static boolean exist(org.bukkit.block.Banner banner){
		for(Banner b : all){
			if(b.isSimilar(banner)) return true;
		}
		return false;
	}
	
	public static boolean exist(Banner banner){
		for(Banner b : all){
			if(b.isSimilar(banner)) return true;
		}
		return false;
	}

    public static Banner get(Material material, BannerMeta meta){
        for(Banner b : all){
            if(b.isSimilar(material, meta)) return b;
        }
        return new Banner(material, meta.getPatterns());
    }

	public static Banner get(org.bukkit.block.Banner banner){
		for(Banner b : all){
			if(b.isSimilar(banner)) return b;
		}
		return new Banner(banner.getType(), banner.getPatterns());
	}
	
	public static void add(Banner banner){
		for(Banner b : all){
			if(b.isSimilar(banner)) return;
		}
		all.add(banner);
	}

    public boolean isSimilar(Material material, BannerMeta meta){
        if(!(material == this.getBase()))return false;
        if(meta.getPatterns().size() != this.getPatterns().size()) return false;
        for(int i = 0; i < meta.getPatterns().size(); i++){
            if(!meta.getPatterns().get(i).getPattern().equals(this.getPatterns().get(i).getPattern())) return false;
            if(!meta.getPatterns().get(i).getColor().equals(this.getPatterns().get(i).getColor())) return false;
        }
        return true;
    }

	public boolean isSimilar(org.bukkit.block.Banner banner){
		if(banner == null) return false;
		if(!(banner.getType() == this.getBase()))return false;
		if(banner.getPatterns().size() != this.getPatterns().size()) return false;
		for(int i = 0; i < banner.getPatterns().size(); i++){
			if(!banner.getPatterns().get(i).getPattern().equals(this.getPatterns().get(i).getPattern())) return false;
			if(!banner.getPatterns().get(i).getColor().equals(this.getPatterns().get(i).getColor())) return false;
		}
		return true;
	}
	
	public boolean isSimilar(Banner banner){
		if(banner == null) return false;
		if(!(banner.getBase() == this.getBase()))return false;
		if(banner.getPatterns().size() != this.getPatterns().size()) return false;
		for(int i = 0; i < banner.getPatterns().size(); i++){
			if(!banner.getPatterns().get(i).getPattern().equals(this.getPatterns().get(i).getPattern())) return false;
			if(!banner.getPatterns().get(i).getColor().equals(this.getPatterns().get(i).getColor())) return false;
		}
		return true;
	}
	
	public List<Pattern> getPatterns(){
		return patterns;
	}
	
	public Material getBase(){
		return base;
	}
	
	public Banner(Material base, List<Pattern> patterns){
		this.patterns = patterns;
		this.base = base;
		Banner.add(this);
	}
	
	public ItemStack getItemStack(){
		ItemStack is = new ItemStack(getBase(), 1);
		BannerMeta bm = (BannerMeta) is.getItemMeta();
		bm.setPatterns(getPatterns());
		is.setItemMeta(bm);
		return is;
	}
	
	public static Banner parse(String s){
		try{
			Material base = null;
			List<Pattern> patterns = new ArrayList<Pattern>();
			String[] components = s.split(";");
			if(components.length >= 2){
				base = Material.valueOf(components[1]);
			}
			for(int i = 2; i < components.length; i++){
				String str = components[i];
				str = str.replaceAll("[{}]", "");
				String[] pat = str.split(":");
				patterns.add(new Pattern(DyeColor.valueOf(pat[1]), PatternType.valueOf(pat[0])));
			}
			return new Banner(base, patterns);
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("BANNER;");
		sb.append(getBase().toString() + ";");
		for(Pattern p : getPatterns()){
			sb.append("{" + p.getPattern().toString() + ":" + p.getColor().toString() + "};");
		}
		return sb.toString();
	}
}
