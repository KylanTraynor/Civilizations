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

public class Banner {
	
	static private List<Banner> all = new ArrayList<Banner>();
	
	List<Pattern> patterns = new ArrayList<Pattern>();
	private org.bukkit.block.Banner banner;
	private DyeColor baseColor;
	
	public static boolean exist(BannerMeta bm){
		for(Banner b : all){
			if(b.isSimilar(bm)) return true;
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
	
	public static Banner get(org.bukkit.block.Banner banner){
		for(Banner b : all){
			if(b.isSimilar(banner)) return b;
		}
		return new Banner(banner.getBaseColor(), banner.getPatterns());
	}
	
	public static Banner get(BannerMeta bm) {
		for(Banner b : all){
			if(b.isSimilar(bm)) return b;
		}
		return new Banner(bm.getBaseColor(), bm.getPatterns());
	}
	
	public static void add(Banner banner){
		for(Banner b : all){
			if(b.isSimilar(banner)) return;
		}
		all.add(banner);
	}
	
	public boolean isSimilar(BannerMeta banner){
		if(banner == null) return false;
		if(!banner.getBaseColor().equals(this.getBaseColor()))return false;
		if(banner.getPatterns().size() != this.getPatterns().size()) return false;
		for(int i = 0; i < banner.getPatterns().size(); i++){
			if(!banner.getPatterns().get(i).getPattern().equals(this.getPatterns().get(i).getPattern())) return false;
			if(!banner.getPatterns().get(i).getColor().equals(this.getPatterns().get(i).getColor())) return false;
		}
		return true;
	}
	
	public boolean isSimilar(org.bukkit.block.Banner banner){
		if(banner == null) return false;
		if(!banner.getBaseColor().equals(this.getBaseColor()))return false;
		if(banner.getPatterns().size() != this.getPatterns().size()) return false;
		for(int i = 0; i < banner.getPatterns().size(); i++){
			if(!banner.getPatterns().get(i).getPattern().equals(this.getPatterns().get(i).getPattern())) return false;
			if(!banner.getPatterns().get(i).getColor().equals(this.getPatterns().get(i).getColor())) return false;
		}
		return true;
	}
	
	public boolean isSimilar(Banner banner){
		if(banner == null) return false;
		if(!banner.getBaseColor().equals(this.getBaseColor()))return false;
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
	
	public DyeColor getBaseColor(){
		return baseColor;
	}
	
	public Banner(DyeColor baseColor, List<Pattern> patterns){
		this.patterns = patterns;
		this.baseColor = baseColor;
		Banner.add(this);
	}
	
	public ItemStack getItemStack(){
		ItemStack is = new ItemStack(Material.BANNER, 1);
		BannerMeta bm = (BannerMeta) is.getItemMeta();
		bm.setBaseColor(getBaseColor());
		bm.setPatterns(getPatterns());
		is.setItemMeta(bm);
		return is;
	}
	
	public static Banner parse(String s){
		try{
			DyeColor baseColor = null;
			List<Pattern> patterns = new ArrayList<Pattern>();
			String[] components = s.split(";");
			if(components.length >= 2){
				baseColor = DyeColor.valueOf(components[1]);
			}
			for(int i = 2; i < components.length; i++){
				String str = components[i];
				str = str.replaceAll("[{}]", "");
				String[] pat = str.split(":");
				patterns.add(new Pattern(DyeColor.valueOf(pat[1]), PatternType.valueOf(pat[0])));
			}
			if(baseColor != null){
				return new Banner(baseColor, patterns);
			} else {
				return null;
			}
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("BANNER;");
		sb.append(getBaseColor().toString() + ";");
		for(Pattern p : getPatterns()){
			sb.append("{" + p.getPattern().toString() + ":" + p.getColor().toString() + "};");
		}
		return sb.toString();
	}
}
