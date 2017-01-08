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
	
	// STATIC
	
	public static MaterialAndData getFrom(Block block){
		return new MaterialAndData(block.getType(), block.getData());
	}
}
