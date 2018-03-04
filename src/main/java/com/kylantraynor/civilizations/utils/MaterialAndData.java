package com.kylantraynor.civilizations.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.kylantraynor.civilizations.Civilizations;

public class MaterialAndData {
	
	final static MaterialAndData Air = new MaterialAndData(Material.AIR, (byte) 0);
	final static MaterialAndData Dirt = new MaterialAndData(Material.DIRT, (byte) 0);
	
	private static Map<MaterialAndData, MaterialAndData> pasteReplacements = new HashMap<MaterialAndData, MaterialAndData>();
	
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
	
	public String toString(){
		if(data != 0){
			return material.toString() + ":" + data;
		} else {
			return material.toString();
		}
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof MaterialAndData){
			if(((MaterialAndData) obj).getMaterial() == material && ((MaterialAndData) obj).getData() == data){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int result = 7;
		result *= material.hashCode();
		result *= Byte.hashCode(data);
		return result;
	}
	
	public MaterialAndData getDefault() {
		switch(material){
		case GRASS:
		case GRASS_PATH:
		case SOIL:
			return new MaterialAndData(Material.DIRT, (byte) 0);
		case DAYLIGHT_DETECTOR_INVERTED:
			return new MaterialAndData(Material.DAYLIGHT_DETECTOR, (byte) 0);
		case REDSTONE_WIRE:
			return new MaterialAndData(Material.REDSTONE, (byte) 0);
		case TRIPWIRE:
			return new MaterialAndData(Material.STRING, (byte) 0);
		case SUGAR_CANE_BLOCK:
			return new MaterialAndData(Material.SUGAR_CANE, (byte) 0);
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			return new MaterialAndData(Material.REDSTONE_TORCH_ON, (byte) 0);
		case REDSTONE_LAMP_OFF:
		case REDSTONE_LAMP_ON:
			return new MaterialAndData(Material.REDSTONE_LAMP_ON, (byte) 0);
		case BURNING_FURNACE:
			return new MaterialAndData(Material.FURNACE, (byte) 0);
		case WALL_SIGN:
		case SIGN_POST:
			return new MaterialAndData(Material.SIGN, (byte) 0);
		case PUMPKIN_STEM:
			return new MaterialAndData(Material.PUMPKIN_SEEDS, (byte) 0);
		case MELON_STEM:
			return new MaterialAndData(Material.MELON_SEEDS, (byte) 0); 
		case CAKE_BLOCK:
			return new MaterialAndData(Material.CAKE, (byte) 0);
		case CROPS:
			return new MaterialAndData(Material.SEEDS, (byte) 0);
		case DIODE_BLOCK_ON:
		case DIODE_BLOCK_OFF:
			return new MaterialAndData(Material.DIODE, (byte) 0);
		case REDSTONE_COMPARATOR_ON:
		case REDSTONE_COMPARATOR_OFF:
			return new MaterialAndData(Material.REDSTONE_COMPARATOR, (byte) 0);
		case DOUBLE_STEP:
			switch(data){
			case 0: return new MaterialAndData(Material.STONE, (byte) 0);
			case 1: return new MaterialAndData(Material.SANDSTONE, (byte) 0);
			case 2:
				break;
			case 3: return new MaterialAndData(Material.COBBLESTONE, (byte) 0);
			case 4: return new MaterialAndData(Material.BRICK, (byte) 0);
			case 5: return new MaterialAndData(Material.SMOOTH_BRICK, (byte) 0);
			case 6: return new MaterialAndData(Material.NETHER_BRICK, (byte) 0); 
			case 7: return new MaterialAndData(Material.QUARTZ_BLOCK, (byte) 0);
			case 8: return new MaterialAndData(Material.STONE, (byte) 0);
			case 9: return new MaterialAndData(Material.SANDSTONE, (byte) 0);
			case 15: return new MaterialAndData(Material.QUARTZ_BLOCK, (byte) 0);
			}
		case DOUBLE_STONE_SLAB2:
			return new MaterialAndData(Material.RED_SANDSTONE, (byte) 0);
		case WOOD_DOUBLE_STEP:
			return new MaterialAndData(Material.WOOD, data);
		case PURPUR_DOUBLE_SLAB:
			return new MaterialAndData(Material.PURPUR_BLOCK, (byte) 0);
		default:
			return this;
		}
	}
	
	public boolean isSimilar(MaterialAndData md) {
		MaterialAndData mdDefault = md.getDefault();
		MaterialAndData thisDefault = this.getDefault();
		return ((this.getMaterial() == md.getMaterial()) && (this.data == md.data)) || ((thisDefault.getMaterial() == mdDefault.getMaterial()) && (thisDefault.getData() == mdDefault.getData()));
	}
	
	public boolean itemIsSimilar(ItemStack item){
		if(material != item.getType()){
			switch(material){
			case GRASS_PATH:
				if(item.getType() == Material.GRASS || item.getType() == Material.DIRT) return true; break;
			case DAYLIGHT_DETECTOR_INVERTED:
				if(item.getType() == Material.DAYLIGHT_DETECTOR) return true; break;
			case REDSTONE_WIRE:
				if(item.getType() == Material.REDSTONE) return true; break;
			case TRIPWIRE:
				if(item.getType() == Material.STRING) return true; break;
			case SUGAR_CANE_BLOCK:
				if(item.getType() == Material.SUGAR_CANE) return true; break;
			case REDSTONE_TORCH_OFF:
			case REDSTONE_TORCH_ON:
				if((item.getType() == Material.REDSTONE_TORCH_OFF || item.getType() == Material.REDSTONE_TORCH_ON)) return true; break;
			case REDSTONE_LAMP_OFF:
			case REDSTONE_LAMP_ON:
				if((item.getType() == Material.REDSTONE_LAMP_OFF || item.getType() == Material.REDSTONE_LAMP_ON)) return true; break;
			case BURNING_FURNACE:
				if(item.getType() == Material.FURNACE) return true; break;
			case WALL_SIGN:
			case SIGN_POST:
				if(item.getType() == Material.SIGN) return true; break;
			case SKULL:
				if(item.getType() == Material.SKULL_ITEM) return true; break;
			case PUMPKIN_STEM:
				if(item.getType() == Material.PUMPKIN_SEEDS) return true; break;
			case MELON_STEM:
				if(item.getType() == Material.MELON_SEEDS) return true; break;
			case CAKE_BLOCK:
				if(item.getType() == Material.CAKE) return true; break;
			case FLOWER_POT:
				if(item.getType() == Material.FLOWER_POT_ITEM) return true; break;
			case BREWING_STAND:
				if(item.getType() == Material.BREWING_STAND_ITEM) return true; break;
			case CROPS:
				if(item.getType() == Material.SEEDS) return true; break;
			case IRON_DOOR_BLOCK:
				if(item.getType() == Material.IRON_DOOR && data < 8) return true; break;
			case BIRCH_DOOR:
				if(item.getType() == Material.BIRCH_DOOR_ITEM && data < 8) return true; break;
			case WOODEN_DOOR: 
				if(item.getType() == Material.WOOD_DOOR && data < 8) return true; break;
			case WOOD_DOOR:
				if(item.getType() == Material.WOODEN_DOOR && data < 8) return true; break;
			case ACACIA_DOOR:
				if(item.getType() == Material.ACACIA_DOOR_ITEM && data < 8) return true; break;
			case SPRUCE_DOOR:
				if(item.getType() == Material.SPRUCE_DOOR_ITEM && data < 8) return true; break;
			case JUNGLE_DOOR:
				if(item.getType() == Material.JUNGLE_DOOR_ITEM && data < 8) return true; break;
			case DARK_OAK_DOOR:
				if(item.getType() == Material.DARK_OAK_DOOR_ITEM && data < 8) return true; break;
			case DIODE_BLOCK_ON:
			case DIODE_BLOCK_OFF:
				if(item.getType() == Material.DIODE) return true; break;
			case REDSTONE_COMPARATOR_ON:
			case REDSTONE_COMPARATOR_OFF:
				if(item.getType() == Material.REDSTONE_COMPARATOR) return true; break;
			case CAULDRON:
				if(item.getType() == Material.CAULDRON_ITEM) return true; break;
			case DOUBLE_STEP:
				switch(data){
				case 0: if(item.getType() == Material.STONE && item.getData().getData() == 0) return true;
					break;
				case 1: if(item.getType() == Material.SANDSTONE) return true;
					break;
				case 2:
					break;
				case 3: if(item.getType() == Material.COBBLESTONE) return true;
					break;
				case 4: if(item.getType() == Material.BRICK) return true;
					break;
				case 5: if(item.getType() == Material.SMOOTH_BRICK) return true;
					break;
				case 6: if(item.getType() == Material.NETHER_BRICK) return true;
					break;
				case 7: if(item.getType() == Material.QUARTZ_BLOCK) return true;
					break;
				case 8: if(item.getType() == Material.STONE && item.getData().getData() == 0) return true;
					break;
				case 9: if(item.getType() == Material.SANDSTONE) return true;
					break;
				case 15: if(item.getType() == Material.QUARTZ_BLOCK) return true;
					break;
				}
				break;
			case DOUBLE_STONE_SLAB2:
				if(item.getType() == Material.RED_SANDSTONE) return true; break;
			case WOOD_DOUBLE_STEP:
				if(item.getType() == Material.WOOD && item.getData().getData() == data) return true; break;
			case PURPUR_DOUBLE_SLAB:
				if(item.getType() == Material.PURPUR_BLOCK) return true; break;
			default:
				return false;
			}
			return false;
		}
		switch(material){
		case ACACIA_FENCE:
		case ACACIA_FENCE_GATE:
		case ACACIA_STAIRS:
		case ACTIVATOR_RAIL:
		case AIR:
		case ANVIL:
		case ARMOR_STAND:
			return true;
		case BANNER:
			break;
		case BARRIER:
		case BEACON:
			return true;
		case BED:
			break;
		case BEDROCK:
			break;
		case BED_BLOCK:
			break;
		case BEETROOT:
			break;
		case BEETROOT_BLOCK:
			break;
		case BEETROOT_SEEDS:
			break;
		case BEETROOT_SOUP:
			break;
		case BIRCH_FENCE:
		case BIRCH_FENCE_GATE:
		case BIRCH_WOOD_STAIRS:
		case BOOKSHELF:
		case BRICK:
		case BRICK_STAIRS:
			return true;
		case BROWN_MUSHROOM:
			break;
		case CACTUS:
			break;
		case CARPET:
			if(data == item.getData().getData()) return true;
			return false;
		case CARROT:
			break;
		case CARROT_ITEM:
			break;
		case CHEST:
			return true;
		case CHORUS_FLOWER:
			return true;
		case CHORUS_FRUIT:
			break;
		case CHORUS_FRUIT_POPPED:
			break;
		case CHORUS_PLANT:
			break;
		case CLAY:
		case COAL_BLOCK:
		case COAL_ORE:
		case COBBLESTONE:
		case COBBLESTONE_STAIRS:
		case COBBLE_WALL:
			return true;
		case COCOA:
			break;
		case COMMAND:
			break;
		case COMMAND_CHAIN:
			break;
		case COMMAND_MINECART:
			break;
		case COMMAND_REPEATING:
			break;
		case DARK_OAK_FENCE:
		case DARK_OAK_FENCE_GATE:
		case DARK_OAK_STAIRS:
		case DAYLIGHT_DETECTOR:
		case DEAD_BUSH:
		case DETECTOR_RAIL:
			return true;
		case DIAMOND_BARDING:
			break;
		case DIAMOND_BLOCK:
		case DIAMOND_ORE:
			return true;
		case DIRT:
			if(data == item.getData().getData()) return true;
			return false;
		case DISPENSER:
			return true;
		case DOUBLE_PLANT:
			break;
		case DOUBLE_STEP:
			break;
		case DOUBLE_STONE_SLAB2:
			break;
		case DRAGON_EGG:
		case DROPPER:
		case EMERALD_BLOCK:
		case EMERALD_ORE:
			return true;
		case EMPTY_MAP:
			break;
		case ENCHANTMENT_TABLE:
		case ENDER_CHEST:
		case ENDER_PORTAL:
		case ENDER_PORTAL_FRAME:
		case ENDER_STONE:
		case END_BRICKS:
			return true;
		case END_CRYSTAL:
			break;
		case END_GATEWAY:
			break;
		case END_ROD:
		case FENCE:
		case FENCE_GATE:
		case FROSTED_ICE:
		case FURNACE:
		case GLASS:
			return true;
		case GLOWING_REDSTONE_ORE:
			return true;
		case GLOWSTONE:
			return true;
		case GOLD_BARDING:
			break;
		case GOLD_BLOCK:
		case GOLD_ORE:
		case GRASS:
		case GRASS_PATH:
		case GRAVEL:
		case HARD_CLAY:
		case HAY_BLOCK:
		case HOPPER:
		case HUGE_MUSHROOM_1:
		case HUGE_MUSHROOM_2:
		case ICE:
			return true;
		case IRON_BARDING:
			break;
		case IRON_BLOCK:
		case IRON_FENCE:
		case IRON_ORE:
		case IRON_TRAPDOOR:
		case ITEM_FRAME:
		case JACK_O_LANTERN:
		case JUKEBOX:
		case JUNGLE_FENCE:
		case JUNGLE_FENCE_GATE:
		case JUNGLE_WOOD_STAIRS:
		case LADDER:
		case LAPIS_BLOCK:
		case LAPIS_ORE:
		case LAVA:
			return true;
		case LAVA_BUCKET:
			return true;
		case LEAVES:
		case LEAVES_2:
		case LEVER:
			return true;
		case LOG:
		case LOG_2:
			if(data % 4 == item.getData().getData()) return true;
			return false;
		case LONG_GRASS:
			break;
		case MAP:
			break;
		case MOB_SPAWNER:
		case MOSSY_COBBLESTONE:
		case MYCEL:
		case NETHERRACK:
			return true;
		case NETHER_BRICK:
			break;
		case NETHER_BRICK_ITEM:
			break;
		case NETHER_BRICK_STAIRS:
		case NETHER_FENCE:
			return true;
		case NETHER_STALK:
			break;
		case NETHER_WARTS:
			break;
		case NOTE_BLOCK:
		case OBSIDIAN:
		case PACKED_ICE:
		case PAINTING:
			return true;
		case PISTON_BASE:
			break;
		case PISTON_EXTENSION:
			break;
		case PISTON_MOVING_PIECE:
			break;
		case PISTON_STICKY_BASE:
			break;
		case PORTAL:
			break;
		case POTATO:
			break;
		case POTATO_ITEM:
			break;
		case POWERED_RAIL:
		case PRISMARINE:
		case PURPUR_BLOCK:
			return true;
		case PURPUR_DOUBLE_SLAB:
			break;
		case PURPUR_PILLAR:
			return true;
		case PURPUR_SLAB:
			if((data % 8) == item.getData().getData()) return true;
			return false;
		case PURPUR_STAIRS:
		case QUARTZ_BLOCK:
		case QUARTZ_ORE:
		case QUARTZ_STAIRS:
		case RAILS:
			return true;
		case REDSTONE:
			break;
		case REDSTONE_BLOCK:
		case REDSTONE_ORE:
			return true;
		case REDSTONE_WIRE:
			return true;
		case RED_MUSHROOM:
		case RED_ROSE:
			return true;
		case RED_SANDSTONE:
			if(data == item.getData().getData()) return true;
			return false;
		case RED_SANDSTONE_STAIRS:
		case SAND:
			return true;
		case SANDSTONE:
			if(data == item.getData().getData()) return true;
			return false;
		case SANDSTONE_STAIRS:
		case SAPLING:
		case SEA_LANTERN:
		case SLIME_BLOCK:
		case SMOOTH_BRICK:
		case SMOOTH_STAIRS:
		case SNOW_BLOCK:
		case SOIL:
		case SOUL_SAND:
		case SPONGE:
		case SPRUCE_FENCE:
		case SPRUCE_FENCE_GATE:
		case SPRUCE_WOOD_STAIRS:
			return true;
		case STAINED_CLAY:
		case STAINED_GLASS:
		case STAINED_GLASS_PANE:
			if(data == item.getData().getData()) return true;
			return false;
		case STANDING_BANNER:
			break;
		case STATIONARY_LAVA:
			break;
		case STATIONARY_WATER:
			break;
		case STEP:
			if((data % 8) == item.getData().getData()) return true;
			return false;
		case STONE:
			if(data == item.getData().getData()) return true;
			return false;
		case STONE_BUTTON:
			return true;
		case STONE_SLAB2:
			if((data % 8) == item.getData().getData()) return true;
			return false;
		case STRUCTURE_BLOCK:
		case THIN_GLASS:
		case TNT:
		case TORCH:
		case TRAPPED_CHEST:
		case TRAP_DOOR:
		case TRIPWIRE_HOOK:
		case VINE:
			return true;
		case WALL_BANNER:
			break;
		case WATER:
			break;
		case WATER_LILY:
		case WEB:
			return true;
		case WOOD:
			if(data == item.getData().getData()) return true;
			return false;
		case WOOD_BUTTON:
			return true;
		case WOOD_DOUBLE_STEP:
			break;
		case WOOL:
			if(data == item.getData().getData()) return true;
			return false;
		case WORKBENCH:
		case YELLOW_FLOWER:
			return true;
		default:
			return true;
		}
		return false;
	}
	
	public MaterialAndData rotateXZ90(){
		switch(material){
		case ACACIA_DOOR:
			break;
		case ACACIA_STAIRS: case BIRCH_WOOD_STAIRS: case WOOD_STAIRS: case SPRUCE_WOOD_STAIRS: case SMOOTH_STAIRS:
		case SANDSTONE_STAIRS: case RED_SANDSTONE_STAIRS: case QUARTZ_STAIRS: case PURPUR_STAIRS: case NETHER_BRICK_STAIRS:
		case JUNGLE_WOOD_STAIRS: case DARK_OAK_STAIRS: case COBBLESTONE_STAIRS: case BRICK_STAIRS:
			switch(data){
			case 0: return new MaterialAndData(material, (byte) 2);
            case 1: return new MaterialAndData(material, (byte) 3);
            case 2: return new MaterialAndData(material, (byte) 1);
            case 3: return new MaterialAndData(material, (byte) 0);
            case 4: return new MaterialAndData(material, (byte) 6);
            case 5: return new MaterialAndData(material, (byte) 7);
            case 6: return new MaterialAndData(material, (byte) 5);
            case 7: return new MaterialAndData(material, (byte) 4);
			}
		case ACACIA_FENCE_GATE: case BIRCH_FENCE_GATE: case JUNGLE_FENCE_GATE: case DARK_OAK_FENCE_GATE: case FENCE_GATE: case SPRUCE_FENCE_GATE:
			/*if(data > 4)
				return new MaterialAndData(material, (byte) (((data + times) % 4) + 4));
			else
				return new MaterialAndData(material, (byte) (data + times % 4));
				*/
			break;
		case ACTIVATOR_RAIL:
			break;
		case ANVIL:
			break;
		case BANNER:
			break;
		case BED:
			break;
		case BED_BLOCK:
			break;
		case BIRCH_DOOR:
			break;
		case BLACK_SHULKER_BOX:
			break;
		case BLUE_SHULKER_BOX:
			break;
		case BONE_BLOCK:
			break;
		case BREWING_STAND:
			break;
		case BROWN_SHULKER_BOX:
			break;
		case CHORUS_FLOWER:
			break;
		case CHORUS_FRUIT:
			break;
		case CHORUS_FRUIT_POPPED:
			break;
		case CHORUS_PLANT:
			break;
		case COCOA:
			break;
		case COMMAND:
			break;
		case COMMAND_CHAIN:
			break;
		case COMMAND_MINECART:
			break;
		case COMMAND_REPEATING:
			break;
		case CYAN_SHULKER_BOX:
			break;
		case DARK_OAK_DOOR:
			break;
		case DETECTOR_RAIL:
			break;
		case REDSTONE_COMPARATOR_ON:
		case REDSTONE_COMPARATOR_OFF:
		case DIODE_BLOCK_ON:
		case DIODE_BLOCK_OFF:
			int dir = data & 0x03;
			int delay = data - dir;
			switch(dir){
			case 0: return new MaterialAndData(material, (byte) (1 | delay));
			case 1: return new MaterialAndData(material, (byte) (2 | delay));
			case 2: return new MaterialAndData(material, (byte) (3 | delay));
			case 3: return new MaterialAndData(material, (byte) (0 | delay));
			}
			break;
		case ENCHANTMENT_TABLE:
			break;
		case ENDER_PORTAL:
			break;
		case ENDER_PORTAL_FRAME:
			break;
		case END_GATEWAY:
			break;
		case END_ROD:
			break;
		case FIRE:
			break;
		case GRAY_SHULKER_BOX:
			break;
		case GREEN_SHULKER_BOX:
			break;
		case LADDER:
		case WALL_SIGN:
		case WALL_BANNER:
		case CHEST:
		case FURNACE:
		case BURNING_FURNACE:
		case ENDER_CHEST:
		case TRAPPED_CHEST:
		case OBSERVER:
		case DISPENSER:
		case DROPPER:
		case HOPPER:
			int extra = data & 0x8;
			int withoutFlags = data & ~0x8;
			switch(withoutFlags){
			case 2: return new MaterialAndData(material, (byte) (5 | extra));
            case 3: return new MaterialAndData(material, (byte) (4 | extra));
            case 4: return new MaterialAndData(material, (byte) (2 | extra));
            case 5: return new MaterialAndData(material, (byte) (3 | extra));
			}
			break;
		case IRON_DOOR:
			break;
		case IRON_DOOR_BLOCK:
			break;
		case ITEM_FRAME:
			break;
		case PUMPKIN:
		case JACK_O_LANTERN:
			switch(data){
			case 0: return new MaterialAndData(material, (byte) 1);
			case 1: return new MaterialAndData(material, (byte) 2);
			case 2: return new MaterialAndData(material, (byte) 3);
			case 3: return new MaterialAndData(material, (byte) 0);
			}
			break;
		case JUNGLE_DOOR:
			break;
		case LEVER:
			int thrown = data & 0x8;
            switch (data & ~0x8) {
            case 1: return new MaterialAndData(material, (byte) (3 | thrown));
            case 2: return new MaterialAndData(material, (byte) (4 | thrown));
            case 3: return new MaterialAndData(material, (byte) (2 | thrown));
            case 4: return new MaterialAndData(material, (byte) (1 | thrown));
            case 5: return new MaterialAndData(material, (byte) (6 | thrown));
            case 6: return new MaterialAndData(material, (byte) (5 | thrown));
            case 7: return new MaterialAndData(material, (byte) (0 | thrown));
            case 0: return new MaterialAndData(material, (byte) (7 | thrown));
            }
			break;
		case LIGHT_BLUE_SHULKER_BOX:
			break;
		case LIME_SHULKER_BOX:
			break;
		case HAY_BLOCK:
		case LOG:
		case LOG_2:
			if (data >= 4 && data <= 11)
				return new MaterialAndData(material, (byte) (data ^ 0xc));
			break;
		case MAGENTA_SHULKER_BOX:
			break;
		case ORANGE_SHULKER_BOX:
			break;
		case PAINTING:
			break;
		case PINK_SHULKER_BOX:
			break;
		case PISTON_BASE:
		case PISTON_STICKY_BASE:
		case PISTON_EXTENSION:
			final int rest = data & ~0x7;
            switch (data & 0x7) {
            case 2: return new MaterialAndData(material, (byte) (5 | rest));
            case 3: return new MaterialAndData(material, (byte) (4 | rest));
            case 4: return new MaterialAndData(material, (byte) (2 | rest));
            case 5: return new MaterialAndData(material, (byte) (3 | rest));
            }
            break;
		case PISTON_MOVING_PIECE:
			break;
		case POWERED_MINECART:
			break;
		case POWERED_RAIL:
			break;
		case PURPLE_SHULKER_BOX:
			break;
		case RAILS:
			break;
		case TORCH:
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			switch(data){
			case 1: return new MaterialAndData(material, (byte) 3);
			case 2: return new MaterialAndData(material, (byte) 4);
			case 3: return new MaterialAndData(material, (byte) 2);
			case 4: return new MaterialAndData(material, (byte) 1);
			}
			break;
		case REDSTONE_WIRE:
			break;
		case RED_SHULKER_BOX:
			break;
		case SIGN:
			break;
		case STANDING_BANNER:
		case SIGN_POST:
			return new MaterialAndData(material, (byte) ((data + 4) % 16));
		case SILVER_SHULKER_BOX:
			break;
		case SKULL:
			break;
		case TIPPED_ARROW:
			break;
		case TOTEM:
			break;
		case TRAP_DOOR:
		case IRON_TRAPDOOR:
			int withoutOrientation = data & ~0x3;
            int orientation = data & 0x3;
            switch (orientation) {
            case 0: return new MaterialAndData(material, (byte) (3 | withoutOrientation));
            case 1: return new MaterialAndData(material, (byte) (2 | withoutOrientation));
            case 2: return new MaterialAndData(material, (byte) (0 | withoutOrientation));
            case 3: return new MaterialAndData(material, (byte) (1 | withoutOrientation));
            }
			break;
		case TRIPWIRE:
			break;
		case TRIPWIRE_HOOK:
			break;
		case VINE:
			return new MaterialAndData(material, (byte) (((data << 1) | (data >> 3)) & 0xf));
		case WHITE_SHULKER_BOX:
			break;
		case WOODEN_DOOR:
			break;
		case STONE_BUTTON:
		case WOOD_BUTTON:
			int thrown1 = data & 0x8;
            switch (data & ~0x8) {
            case 1: return new MaterialAndData(material, (byte) (3 | thrown1));
            case 2: return new MaterialAndData(material, (byte) (4 | thrown1));
            case 3: return new MaterialAndData(material, (byte) (2 | thrown1));
            case 4: return new MaterialAndData(material, (byte) (1 | thrown1));
            }
			break;
		case WOOD_DOOR:
			break;
		case YELLOW_SHULKER_BOX:
			break;
		default:
			return this;
		}
		return this;
	}
	
	public boolean requiresSupply() {
		switch(material){
		case LONG_GRASS:
		case WATER:
		case STATIONARY_WATER:
		case LAVA:
		case STATIONARY_LAVA:
		case AIR:
			return false;
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case WOODEN_DOOR:
		case WOOD_DOOR:
		case IRON_DOOR_BLOCK:
			if(data >= 8) return false;
			return true;
		default:
			return true;
		}
	}
	
	public MaterialAndData changeForPaste(){
		
		MaterialAndData replacement = pasteReplacements.get(this);
		if(replacement == null){
			return this;
		} else {
			return replacement;
		}
	}
	
	public ItemStack toItemStack() {
		return toItemStack(1);
	}
	
	public ItemStack toItemStack(int amount){
		return new MaterialData(material, data).toItemStack(amount);
	}
	
	// STATIC
	
	public static MaterialAndData getFrom(Block block){
		return new MaterialAndData(block.getType(), block.getData());
	}
	
	public static MaterialAndData getFrom(ItemStack itemStack){
		return new MaterialAndData(itemStack.getType(), itemStack.getData().getData());
	}
	
	public static void addPasteReplacementFor(String block, String replacement){
		MaterialAndData blockMat;
		MaterialAndData replacementMat;
		
		blockMat = MaterialAndData.parse(block);
		replacementMat = MaterialAndData.parse(replacement);
		
		pasteReplacements.put(blockMat, replacementMat);
	}
	
	public static MaterialAndData parse(String s){
		if(s.contains(":")){
			return new MaterialAndData(Material.valueOf(s.split("\\:")[0]), Byte.parseByte(s.split("\\:")[1]));
		} else {
			return new MaterialAndData(Material.valueOf(s), (byte) 0);
		}
	}

	public static void reloadFromConfig(FileConfiguration config) {
		ConfigurationSection s = config.getConfigurationSection("Materials");
		if(s != null){
			ConfigurationSection replacements = s.getConfigurationSection("PasteReplacements");
			if(replacements != null){
				for(Entry<String, Object> e : replacements.getValues(false).entrySet()){
					if(e.getValue() instanceof String){
						try{
							pasteReplacements.put(MaterialAndData.parse(e.getKey()), MaterialAndData.parse((String) e.getValue())); 
						} catch (Exception ex){
							Civilizations.currentInstance.getLogger().severe(ChatColor.RED + "Failed to add replacement " + e.getValue() + " for " + e.getKey() + ".");
						}
					}
				}
				return;
			}
		}
		
		setDefaultReplacementValues();
	}
	
	private static void setDefaultReplacementValues() {
		pasteReplacements.clear();
		pasteReplacements.put(new MaterialAndData(Material.LONG_GRASS, (byte) 0), MaterialAndData.Air);
		pasteReplacements.put(new MaterialAndData(Material.WATER, (byte) 0), MaterialAndData.Air);
		pasteReplacements.put(new MaterialAndData(Material.STATIONARY_WATER, (byte) 0), MaterialAndData.Air);
		pasteReplacements.put(new MaterialAndData(Material.LAVA, (byte) 0), MaterialAndData.Air);
		pasteReplacements.put(new MaterialAndData(Material.STATIONARY_LAVA, (byte) 0), MaterialAndData.Air);
		pasteReplacements.put(new MaterialAndData(Material.GRASS, (byte) 0), MaterialAndData.Dirt);
	}

	public static void saveToConfig(FileConfiguration config){
		config.set("Materials.PasteReplacements", null);
		for(Entry<MaterialAndData, MaterialAndData> e : pasteReplacements.entrySet()){
			config.set("Materials.PasteReplacements." + e.getKey().toString(), e.getValue().toString());
		}
	}
}
