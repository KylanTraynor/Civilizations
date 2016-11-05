package com.kylantraynor.civilizations.builder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.google.common.io.Files;
import com.kylantraynor.civilizations.selection.Selection;

public class Blueprint{
	
	public static class Code{
		public int material;
		public byte data;
		
		public Code(int m, byte data){
			this.material = m;
			this.data = data;
		}
	}
	
	private int width = 1;
	private int height = 1;
	private int depth = 1;
	private List<Material> materialCodes = new ArrayList<Material>();
	private Code[][][] data;
	private String name;

	public Blueprint(){
		
	}
	
	public Blueprint(int width, int height, int depth) {
		data = new Code[width][height][depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
		
	}
	
	public boolean save(File f){
		StringBuilder sb = new StringBuilder();
		// Save list of materials
		sb.append("" + width + "," + height + "," + depth + ",");
		for(Material m : materialCodes){
			sb.append(m.toString());
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		// Save list of blocks
		for(int y = 0; y < width; y ++){
			sb.append("\n\n");
			for(int x = 0; x < height; x++){
				sb.append("\n");
				for(int z = 0; z < depth; z++){
					sb.append("" + data[x][y][z].material + ":" + data[x][y][z].data);
					sb.append(",");
				}
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
		}
		try {
			Files.write(sb.subSequence(0, sb.length() - 1), f, Charset.defaultCharset());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;	
		}
	}
	
	public static Blueprint load(File f){
		// load list of materials
		List<Material> materialCodes = new ArrayList<Material>();
		String name = f.getName().replace(".bpt", "");
		String s;
		try {
			s = Files.readFirstLine(f, Charset.defaultCharset());
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		String[] sa = s.split(",");
		int width = Integer.getInteger(sa[0]);
		int height = Integer.getInteger(sa[1]);
		int depth = Integer.getInteger(sa[2]);
		
		Code[][][] data = new Code[width][height][depth];
		
		materialCodes.add(Material.AIR);
		for(int i = 3; i < sa.length; i++){
			materialCodes.add(Material.getMaterial(sa[i]));
		}
		
		// load list of blocks
		List<String> lines = new ArrayList<String>();
		try {
			lines = Files.readLines(f, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try{
			lines.remove(0);
			for(int y = 0; y < width; y ++){
				lines.remove(0);
				for(int x = 0; x < height; x++){
					String current = lines.get(0);
					String[] codes = current.split(",");
					for(int z = 0; z < codes.length; z++){
						data[x][y][z] = new Code(Integer.getInteger(codes[z].split(":")[0]), Byte.valueOf(codes[z].split(":")[1]));
					}
					lines.remove(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Blueprint bp = new Blueprint(width, height, depth);
		bp.data = data;
		bp.materialCodes = materialCodes;
		bp.setName(name);
		return bp;
	}
	
	public List<Material> getMaterialCodes(){
		return materialCodes;
	}
	
	public void fillDataFrom(Selection s){
		fillDataFrom(s.getLocation(), s.getWidth(), s.getHeight(), s.getLength());
	}
	
	public void fillDataFrom(Location l, int width, int height, int depth){
		this.width = width;
		this.height = height;
		this.depth = depth;
		materialCodes = new ArrayList<Material>();
		materialCodes.add(Material.AIR);
		data = new Code[width][height][depth];
		Location current = l.clone();
		for(int x = 0; x < width; x++){
			current.setX(l.getBlockX() + x);
			for(int y = 0; y < height; y++){
				current.setY(l.getBlockY() + y);
				for(int z = 0; z < depth; z++){
					current.setZ(l.getBlockZ() + z);
					
					if(current.getBlock().getType() == Material.AIR){
						data[x][y][z] = new Code(0,(byte) current.getBlock().getData());
					} else {
						if(!getMaterialCodes().contains(current.getBlock().getType())){
							getMaterialCodes().add(current.getBlock().getType());
						}
						data[x][y][z] = new Code(getMaterialCodes().indexOf(current.getBlock().getType()),(byte) current.getBlock().getData());
					}
					
				}
			}
		}
	}

	public ItemStack getDataAt(int x, int y, int z) {
		if(x < width && y < height && z < depth){
			Code c = data[x][y][z];
			Material m = getMaterialCodes().get(c.material);
			byte data = c.data;
			MaterialData md = new MaterialData(m, data);
			return md.toItemStack(1);
		}
		return null;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	public int getDepth() {
		return depth;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getName() {
		return name;
	}
}
