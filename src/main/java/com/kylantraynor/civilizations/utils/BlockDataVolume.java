package com.kylantraynor.civilizations.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BlockDataVolume implements JSONSerializable{

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("width", width);
        result.put("height", height);
        result.put("depth", depth);
        JSONArray array = new JSONArray();
        for(BlockData d : blockData){
            array.add(d.getAsString());
        }
        result.put("data", array);
        return null;
    }

    @Override
    public void load(JSONObject json) {
        this.width = (int) json.get("width");
        this.height = (int) json.get("height");
        this.depth = (int) json.get("depth");
        JSONArray array = (JSONArray) json.get("data");
        blockData = new BlockData[width*height*depth];
        for(int i = 0; i < blockData.length; i++){
            blockData[i] = Bukkit.getServer().createBlockData((String) array.get(i));
        }
        strideZ = width;
        strideY = width * depth;
    }

    private int width;
    private int height;
    private int depth;

    private int strideZ;
    private int strideY;

    private BlockData[] blockData;

    public BlockDataVolume(JSONObject json){
        load(json);
    }

    public BlockDataVolume(int width, int height, int depth){
        this.width = width; this.height = height; this.depth = depth;
        strideZ = width;
        strideY = width * depth;
        this.blockData = new BlockData[width*height*depth];
    }

    public BlockData getAt(int x, int y, int z){
        if(x < 0 || x >= width) return null;
        if(y < 0 || y >= width) return null;
        if(z < 0 || z >= width) return null;

        int index = y * strideY + z * strideZ + x;
        return get(index);
    }

    public BlockData get(int index){
        if(index < 0 || index >= blockData.length) throw
                new IndexOutOfBoundsException(
                        "Cannot get index " + index + " from an array of " + blockData.length + " elements.");
        return blockData[index];
    }

    public void setAt(int x, int y, int z, BlockData value){
        if(x < 0 || x >= width) return;
        if(y < 0 || y >= width) return;
        if(z < 0 || z >= width) return;

        int index = y * strideY + z * strideZ + x;
        set(index, value);
    }

    public void set(int index, BlockData value){
        if(index < 0 || index >= blockData.length) throw
                new IndexOutOfBoundsException(
                        "Cannot set index " + index + " in an array of " + blockData.length + " elements.");
        blockData[index] = value;
    }
}
