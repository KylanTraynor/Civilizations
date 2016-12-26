package com.kylantraynor.civilizations.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

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
	
	public MaterialAndData rotateXZ90(int times){
		switch(material){
		case ACACIA_DOOR:
			break;
		case ACACIA_STAIRS: case BIRCH_WOOD_STAIRS: case WOOD_STAIRS: case SPRUCE_WOOD_STAIRS: case SMOOTH_STAIRS:
		case SANDSTONE_STAIRS: case RED_SANDSTONE_STAIRS: case QUARTZ_STAIRS: case PURPUR_STAIRS: case NETHER_BRICK_STAIRS:
		case JUNGLE_WOOD_STAIRS: case DARK_OAK_STAIRS: case COBBLESTONE_STAIRS: case BRICK_STAIRS:
			
		case ACACIA_FENCE_GATE: case BIRCH_FENCE_GATE: case JUNGLE_FENCE_GATE: case DARK_OAK_FENCE_GATE: case FENCE_GATE: case SPRUCE_FENCE_GATE:
			if(data > 4)
				return new MaterialAndData(material, (byte) (((data + times) % 4) + 4));
			else
				return new MaterialAndData(material, (byte) (data + times % 4));
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
		case BURNING_FURNACE:
			break;
		case CHEST: case TRAPPED_CHEST: case ENDER_CHEST:
			return new MaterialAndData(material, (byte) (((data - 2 + times) % 4) + 2));
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
		case DIODE:
			break;
		case DIODE_BLOCK_OFF:
			break;
		case DIODE_BLOCK_ON:
			break;
		case DISPENSER:
			break;
		case DROPPER:
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
		case FURNACE:
			break;
		case GRAY_SHULKER_BOX:
			break;
		case GREEN_SHULKER_BOX:
			break;
		case HOPPER:
			break;
		case IRON_DOOR:
			break;
		case IRON_DOOR_BLOCK:
			break;
		case IRON_TRAPDOOR:
			break;
		case ITEM_FRAME:
			break;
		case JACK_O_LANTERN:
			break;
		case JUNGLE_DOOR:
			break;
		case LADDER:
			break;
		case LEVER:
			break;
		case LIGHT_BLUE_SHULKER_BOX:
			break;
		case LIME_SHULKER_BOX:
			break;
		case LOG:
		case LOG_2:
			byte direction = this.data % 4;
		case MAGENTA_SHULKER_BOX:
			break;
		case OBSERVER:
			break;
		case ORANGE_SHULKER_BOX:
			break;
		case PAINTING:
			break;
		case PINK_SHULKER_BOX:
			break;
		case PISTON_BASE:
			break;
		case PISTON_EXTENSION:
			break;
		case PISTON_MOVING_PIECE:
			break;
		case PISTON_STICKY_BASE:
			break;
		case POWERED_MINECART:
			break;
		case POWERED_RAIL:
			break;
		case PURPLE_SHULKER_BOX:
			break;
		case RAILS:
			break;
		case REDSTONE_COMPARATOR:
			break;
		case REDSTONE_COMPARATOR_OFF:
			break;
		case REDSTONE_COMPARATOR_ON:
			break;
		case REDSTONE_TORCH_OFF:
			break;
		case REDSTONE_TORCH_ON:
			break;
		case REDSTONE_WIRE:
			break;
		case RED_SHULKER_BOX:
			break;
		case SIGN:
			break;
		case SIGN_POST:
			break;
		case SILVER_SHULKER_BOX:
			break;
		case SKULL:
			break;
		case STANDING_BANNER:
			break;
		case STONE_BUTTON:
			break;
		case TIPPED_ARROW:
			break;
		case TORCH:
			break;
		case TOTEM:
			break;
		case TRAP_DOOR:
			break;
		case TRIPWIRE:
			break;
		case TRIPWIRE_HOOK:
			break;
		case VINE:
			break;
		case WALL_BANNER:
			break;
		case WALL_SIGN:
			break;
		case WHITE_SHULKER_BOX:
			break;
		case WOODEN_DOOR:
			break;
		case WOOD_BUTTON:
			break;
		case WOOD_DOOR:
			break;
		case YELLOW_SHULKER_BOX:
			break;
		default:
			return this;
		}
	}
	
	// STATIC
	
	public static MaterialAndData getFrom(Block block){
		return new MaterialAndData(block.getType(), block.getData());
	}
}
