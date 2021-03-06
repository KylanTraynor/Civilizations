package com.kylantraynor.civilizations.builder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kylantraynor.civilizations.utils.BlockDataVolume;
import com.kylantraynor.civilizations.utils.JSONSerializable;
import org.bukkit.Location;
import org.bukkit.Material;

import com.google.common.io.Files;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.selection.Selection;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.utils.MaterialAndData;
import org.bukkit.block.data.BlockData;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Blueprint implements JSONSerializable {
	
	private static Map<UUID, Blueprint> loadedBlueprints = new HashMap<UUID, Blueprint>();

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("id", id.toString());
        result.put("name", name);
        result.put("data", blockData.toJSON());
        return result;
    }

    @Override
    public void load(JSONObject json) {
        this.id = UUID.fromString((String) json.get("id"));
        this.name = (String) json.get("name");
        this.blockData = new BlockDataVolume((JSONObject) json.get("data"));
    }

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
	private BlockDataVolume blockData;
	private String name;
	private UUID id;

	public Blueprint(){
		this.id = UUID.randomUUID();
	}

	public Blueprint(JSONObject json){
	    load(json);
    }

	public Blueprint(int width, int height, int depth) {
		data = new Code[width][height][depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.id = UUID.randomUUID();
	}
	
	public boolean save(File f){
		/*StringBuilder sb = new StringBuilder();
		// Save list of materials
		if(name != null)
			sb.append(name);
		else
			sb.append("");

		sb.append(",").append(width).append(",").append(height).append(",").append(depth).append(",");
		for(Material m : materialCodes){
			sb.append(m.toString()).append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		// Save list of blocks
		for(int y = 0; y < height; y ++){
			sb.append("\n\n");
			for(int x = 0; x < width; x++){
				sb.append("\n");
				for(int z = 0; z < depth; z++){
					sb.append("").append(data[x][y][z].material).append(":").append(data[x][y][z].data).append(",");
				}
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
		}*/
		if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
		try {
			Files.write(toJSON().toJSONString(), f, Charset.defaultCharset());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;	
		}
	}
	
	public static Blueprint load(File f){
		// load list of materials
		/*List<Material> materialCodes = new ArrayList<Material>();
		String id = f.getName().replace(".bpt", "");
		String s;
		try {
			s = Files.readFirstLine(f, Charset.defaultCharset());
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		String[] sa = s.split(",");
		String name = sa[0];
		int width = Integer.parseInt(sa[1]);
		int height = Integer.parseInt(sa[2]);
		int depth = Integer.parseInt(sa[3]);
		
		Code[][][] data = new Code[width][height][depth];
		
		materialCodes.add(Material.AIR);
		for(int i = 4; i < sa.length; i++){
			materialCodes.add(Material.getMaterial(sa[i]));
		}
		
		// load list of blocks
		List<String> lines;
		try {
			lines = Files.readLines(f, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try{
			lines.remove(0);
			for(int y = 0; y < height; y ++){
				lines.remove(0);
				lines.remove(0);
				for(int x = 0; x < width; x++){
					String current = lines.get(0);
					String[] codes = current.split(",");
					for(int z = 0; z < codes.length; z++){
						try{
							data[x][y][z] = new Code(Integer.parseInt(codes[z].split(":")[0]), Byte.valueOf(codes[z].split(":")[1]));
						} catch (Exception e){
							e.printStackTrace();
							data[x][y][z] = new Code(0,(byte)0);
						}
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
		if(!name.isEmpty())
			bp.setName(name);
		
		bp.setUniqueId(UUID.fromString(id));*/
		Blueprint bp = null;
        try {
            String s = Files.toString(f, Charset.defaultCharset());
            JSONObject obj = (JSONObject) (new JSONParser().parse(s));
            bp = new Blueprint(obj);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return bp;
	}
	
	private void setUniqueId(UUID id) {
		this.id = id;
	}

	public List<Material> getMaterialCodes(){
		return materialCodes;
	}
	
	public void fillDataFrom(Selection s){
		fillDataFrom(s.getLocation(), (int)((Shape)s).getWidth(), (int)((Shape)s).getHeight(), (int)((Shape)s).getLength());
	}
	
	public void fillDataFrom(Location l, int width, int height, int depth){
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.blockData = new BlockDataVolume(width, height, depth);

		Location current = l.clone();
		for(int x = 0; x < width; x++){
			current.setX(l.getBlockX() + x);
			for(int y = 0; y < height; y++){
				current.setY(l.getBlockY() + y);
				for(int z = 0; z < depth; z++){
					current.setZ(l.getBlockZ() + z);
					blockData.setAt(x,y,z, current.getBlock().getBlockData());
					/*if(current.getBlock().getType() == Material.AIR){
						data[x][y][z] = new Code(0, current.getBlock().getData());
					} else {
						if(!getMaterialCodes().contains(current.getBlock().getType())){
							getMaterialCodes().add(current.getBlock().getType());
						}
						data[x][y][z] = new Code(getMaterialCodes().indexOf(current.getBlock().getType()), current.getBlock().getData());
					}*/
					
				}
			}
		}
	}

	public BlockData getDataAt(int x, int y, int z) {
	    return blockData.getAt(x, y, z);
		/*if(x < width && y < height && z < depth){
			Code c = data[x][y][z];
			Material m = getMaterialCodes().get(c.material);
			byte data = c.data;
			return new MaterialAndData(m, data);
		}
		return null;*/
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

	public UUID getUniqueId() {
		return id;
	}

	public static Blueprint get(UUID bpId) {
		Blueprint bp = loadedBlueprints.get(bpId);
		if(bp != null) return bp;
		for(File f : Civilizations.getBlueprintDirectory().listFiles()){
			if(f.getName().replace(".bpt", "").equalsIgnoreCase(bpId.toString())){
				bp = Blueprint.load(f);
				if(bp != null){
					loadedBlueprints.put(bp.id, bp);
					return bp;
				}
			} else if(f.getName().replace(".yml","").equalsIgnoreCase(bpId.toString())){
				bp = Blueprint.load(f);
				if(bp != null){
					loadedBlueprints.put(bp.id, bp);
					return bp;
				}
			}
		}
		return null;
	}
	
	public static Blueprint get(String name){
		for(Blueprint bp : loadedBlueprints.values()){
			if(bp.getName() != null){
				if(bp.getName().equalsIgnoreCase(name)) return bp;
			}
		}
		return null;
	}

	public boolean save() {
		File f = new File(Civilizations.getBlueprintDirectory(), this.getUniqueId().toString() + ".bpt");
		return save(f);
	}
}
