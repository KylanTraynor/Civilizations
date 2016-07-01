package com.kylantraynor.civilizations.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.kylantraynor.civilizations.shapes.Shape;

public class Util {

	/**
	 * Gets a list of shapes from a string of the format returned by getShapesString.
	 * @param str
	 * @return
	 */
	public static List<Shape> parseShapes(String str){
		String[] shapes = str.split(" ");
		List<Shape> list = new ArrayList<Shape>();
		for(String shape : shapes){
			Shape s = Shape.parse(shape);
			if(s != null){
				list.add(s);
			}
		}
		return list;
	}
	
	/**
	 * Gets a string describing the list of shapes.
	 * @param list
	 * @return
	 */
	public static String getShapesString(List<Shape> list){
		StringBuilder sb = new StringBuilder();
		for(Shape s : list){
			sb.append(s.toString() + " ");
		}
		return sb.toString();
	}
	
	/**
	 * Concatenates all the items of the list into a single string.
	 * @param list
	 * @param link
	 * @return
	 */
	public static String join(Iterable<String> list, String link){
		if(link == null) link = " ";
		String result = "";
		Iterator<String> it = list.iterator();
		while(it.hasNext()){
			result = result + it.next();
			if(it.hasNext()){
				result = result + link;
			}
		}
		return result;
	}
	
	/**
	 * Gets the material's name of the item.
	 * @param item
	 * @return
	 */
	public static String getMaterialName(ItemStack item) {
		return getDataName(item.getType(), item.getData().getData(), item.getItemMeta());
	}

	/**
	 * Converts a name like IRON_INGOT into Iron Ingot to improve readability
	 * @param ugly The string such as IRON_INGOT
	 * @return A nicer version, such as Iron Ingot
	 * 
	 * Credits to mikenon on GitHub!
	 */
	public static String prettifyText(String ugly){
		if(!ugly.contains("_") && (!ugly.equals(ugly.toUpperCase()))) return ugly;
		String fin = "";
		ugly = ugly.toLowerCase();
		if(ugly.contains("_")){
			String[] splt = ugly.split("_");
			int i = 0;
			for(String s : splt){
				i += 1;
				fin += Character.toUpperCase(s.charAt(0)) + s.substring(1);
				if(i<splt.length) fin += " ";
			}
		} else {
			fin += Character.toUpperCase(ugly.charAt(0)) + ugly.substring(1);
		}
		return fin;
	}
	/**
	 * Converts a given material and data value into a format similar to Material.<?>.toString().
	 * Upper case, with underscores.  Includes material name in result.
	 * @param mat The base material.
	 * @param data The date of the material.
	 * @return A string with the name of the item.
	 * 
	 * @author maxgamer
	 * @author Modified by KylanTraynor
	 */
	private static String getDataName(Material mat, short data, ItemMeta meta){
		switch(mat){
		case WOOL: 
			switch((int) data){
				case 0: return "WHITE_WOOL";
				case 1: return "ORANGE_WOOL";
				case 2: return "MAGENTA_WOOL";
				case 3: return "LIGHT_BLUE_WOOL";
				case 4: return "YELLOW_WOOL";
				case 5: return "LIME_WOOL";
				case 6: return "PINK_WOOL";
				case 7: return "GRAY_WOOL";
				case 8: return "LIGHT_GRAY_WOOL";
				case 9: return "CYAN_WOOL";
				case 10: return "PURPLE_WOOL";
				case 11: return "BLUE_WOOL";
				case 12: return "BROWN_WOOL";
				case 13: return "GREEN_WOOL";
				case 14: return "RED_WOOL";
				case 15: return "BLACK_WOOL";
			}
			return mat.toString();
		case STAINED_CLAY:
			switch((int) data){
			case 0: return "WHITE_CLAY";
			case 1: return "ORANGE_CLAY";
			case 2: return "MAGENTA_CLAY";
			case 3: return "LIGHT_BLUE_CLAY";
			case 4: return "YELLOW_CLAY";
			case 5: return "LIME_CLAY";
			case 6: return "PINK_CLAY";
			case 7: return "GRAY_CLAY";
			case 8: return "LIGHT_GRAY_CLAY";
			case 9: return "CYAN_CLAY";
			case 10: return "PURPLE_CLAY";
			case 11: return "BLUE_CLAY";
			case 12: return "BROWN_CLAY";
			case 13: return "GREEN_CLAY";
			case 14: return "RED_CLAY";
			case 15: return "BLACK_CLAY";
			default: return mat.toString();
			}
		case STAINED_GLASS:
			switch((int) data){
			case 0: return "WHITE_GLASS";
			case 1: return "ORANGE_GLASS";
			case 2: return "MAGENTA_GLASS";
			case 3: return "LIGHT_BLUE_GLASS";
			case 4: return "YELLOW_GLASS";
			case 5: return "LIME_GLASS";
			case 6: return "PINK_GLASS";
			case 7: return "GRAY_GLASS";
			case 8: return "LIGHT_GRAY_GLASS";
			case 9: return "CYAN_GLASS";
			case 10: return "PURPLE_GLASS";
			case 11: return "BLUE_GLASS";
			case 12: return "BROWN_GLASS";
			case 13: return "GREEN_GLASS";
			case 14: return "RED_GLASS";
			case 15: return "BLACK_GLASS";
			default: return mat.toString();
			}
		case STAINED_GLASS_PANE:
			switch((int) data){
			case 0: return "WHITE_GLASS_PANE";
			case 1: return "ORANGE_GLASS_PANE";
			case 2: return "MAGENTA_GLASS_PANE";
			case 3: return "LIGHT_BLUE_GLASS_PANE";
			case 4: return "YELLOW_GLASS_PANE";
			case 5: return "LIME_GLASS_PANE";
			case 6: return "PINK_GLASS_PANE";
			case 7: return "GRAY_GLASS_PANE";
			case 8: return "LIGHT_GRAY_GLASS_PANE";
			case 9: return "CYAN_GLASS_PANE";
			case 10: return "PURPLE_GLASS_PANE";
			case 11: return "BLUE_GLASS_PANE";
			case 12: return "BROWN_GLASS_PANE";
			case 13: return "GREEN_GLASS_PANE";
			case 14: return "RED_GLASS_PANE";
			case 15: return "BLACK_GLASS_PANE";
			default: return mat.toString();
			}
		case INK_SACK:
			switch((int) data){
				case 0: return "INK_SACK";
				case 1: return "ROSE_RED";
				case 2: return "CACTUS_GREEN";
				case 3: return "COCOA_BEANS";
				case 4: return "LAPIS_LAZULI";
				case 5: return "PURPLE_DYE";
				case 6: return "CYAN_DYE";
				case 7: return "LIGHT_GRAY_DYE";
				case 8: return "GRAY_DYE";
				case 9: return "PINK_DYE";
				case 10: return "LIME_DYE";
				case 11: return "DANDELION_YELLOW";
				case 12: return "LIGHT_BLUE_DYE";
				case 13: return "MAGENTA_DYE";
				case 14: return "ORANGE_DYE";
				case 15: return "BONE_MEAL";
			}
			return mat.toString();
		case SMOOTH_BRICK:
			switch((int) data){
				case 0: return "STONE_BRICKS";
				case 1: return "MOSSY_STONE_BRICKS";
				case 2: return "CRACKED_STONE_BRICKS";
				case 3: return "CHISELED_STONE_BRICKS";
			}
			return mat.toString();
		case POTION:
			PotionMeta pot;
			try{
				pot = (PotionMeta) meta;
			}
			catch(Exception e){ return "CUSTOM_POTION"; }
			if(pot.getBasePotionData().getType() == PotionType.WATER){
				return "WATER_BOTTLE";
			} else {
				if(pot.getBasePotionData().isExtended()){
					return "EXTENDED_POTION_OF_" + pot.getBasePotionData().getType();
				} else {
					return "POTION_OF_" + pot.getBasePotionData().getType();
				}
			}
		case PRISMARINE:
			switch((int) data){
			case 0: return "PRISMARINE";
			case 1: return "PRISMARINE_BRICKS";
			case 2: return "DARK_PRISMARINE";
			}
		case SAPLING:
			switch((int) data){
				case 0: return "OAK_SAPLING";
				case 1: return "PINE_SAPLING";
				case 2: return "BIRCH_SAPLING";
				case 3: return "JUNGLE_TREE_SAPLING";
			}
			return mat.toString();
		
		case WOOD:
			switch((int) data){
				case 0: return "OAK_PLANKS";
				case 1: return "PINE_PLANKS";
				case 2: return "BIRCH_PLANKS";
				case 3: return "JUNGLE_PLANKS";
			}
			return mat.toString();
		case LOG:
			switch(data){
				case 0: return "OAK_LOG";
				case 1: return "SPRUCE_LOG";
				case 2: return "BIRCH_LOG";
				case 3: return "JUNGLE_LOG";
			}
			return mat.toString();
		case LOG_2:
			switch(data){
				case 0: return "ACACIA_LOG";
				case 1: return "DARK_OAK_LOG";
			}
			return mat.toString();
		case LEAVES:
			data = (short) (data%4);
			switch(data){
				case 0: return "OAK_LEAVES";
				case 1: return "SPRUCE_LEAVES";
				case 2: return "BIRCH_LEAVES";
				case 3: return "JUNGLE_LEAVES";
			}
		case LEAVES_2:
			switch(data){
				case 0: return "ACACIA_LEAVES";
				case 1: return "DARK_OAK_LEAVES";
			}
			return mat.toString();
		case COAL:
			switch(data){
				case 0: return "COAL";
				case 1: return "CHARCOAL";
			}
			return mat.toString();
		case SANDSTONE:
			switch((int) data){
				case 0: return "SANDSTONE";
				case 1: return "CHISELED_SANDSTONE";
				case 2: return "SMOOTH_SANDSTONE";
			}
			return mat.toString();
		case LONG_GRASS:
			switch((int) data){
				case 0: return "DEAD_SHRUB";
				case 1: return "TALL_GRASS";
				case 2: return "FERN";
			}
			return mat.toString();
		case STEP:
			switch((int) data){
				case 0: return "STONE_SLAB";
				case 1: return "SANDSTONE_SLAB";
				case 2: return "WOODEN_SLAB";
				case 3: return "COBBLESTONE_SLAB";
				case 4: return "BRICK_SLAB";
				case 5: return "STONE_BRICK_SLAB";
			}
			return mat.toString();
		case MONSTER_EGG:
			switch((int) data){
				case 50: return "CREEPER_EGG";
				case 51: return "SKELETON_EGG";
				case 52: return "SPIDER_EGG";
				case 53: return "GIANT_EGG";
				case 54: return "ZOMBIE_EGG";
				case 55: return "SLIME_EGG";
				case 56: return "GHAST_EGG";
				case 57: return "ZOMBIE_PIGMAN_EGG";
				case 58: return "ENDERMAN_EGG";
				case 59: return "CAVE_SPIDER_EGG";
				case 60: return "SILVERFISH_EGG";
				case 61: return "BLAZE_EGG";
				case 62: return "MAGMA_CUBE_EGG";
				case 63: return "ENDER_DRAGON_EGG";
				case 90: return "PIG_EGG";
				case 91: return "SHEEP_EGG";
				case 92: return "COW_EGG";
				case 93: return "CHICKEN_EGG";
				case 94: return "SQUID_EGG";
				case 95: return "WOLF_EGG";
				case 96: return "MOOSHROOM_EGG";
				case 97: return "SNOW_GOLEM_EGG";
				case 98: return "OCELOT_EGG";
				case 99: return "IRON_GOLEM_EGG";
				case 120: return "VILLAGER_EGG";
				case 200: return "ENDER_CRYSTAL_EGG";
				case 14: return "PRIMED_TNT_EGG";
				case 66: return "WITCH_EGG";
				case 65: return "BAT_EGG";
			}
			return mat.toString();
		case SKULL: case SKULL_ITEM:
			switch((int) data){
				case 0: return "SKELETON_SKULL";
				case 1: return "WITHER_SKULL";
				case 2: return "ZOMBIE_HEAD";
				case 3: return "PLAYER_HEAD";
				case 4: return "CREEPER_HEAD";
			}
			break;
		case REDSTONE_TORCH_ON: case REDSTONE_TORCH_OFF:
			return "REDSTONE_TORCH";
		case IRON_FENCE:
			return "IRON_BARS";
		case REDSTONE_LAMP_OFF: case REDSTONE_LAMP_ON:
			return "REDSTONE_LAMP";
		case GOLDEN_APPLE:
			switch((int) data){
				case 0: return "GOLDEN_APPLE";
				case 1: return "ENCHANTED_GOLDEN_APPLE";
			}
			break;
		case NETHER_STALK:
			return "NETHER_WART";
		case ANVIL:
			switch((int) data){
				case 0: return "ANVIL";
				case 1: return "SLIGHTLY_DAMAGED_ANVIL";
				case 2: return "VERY_DAMAGED:ANVIL";
			}
			break;
		case EXP_BOTTLE:
			return "BOTTLE_O'_ENCHANTING";
		case ENCHANTED_BOOK:
			EnchantmentStorageMeta book;
			try{
				book = (EnchantmentStorageMeta) meta;
			} catch (Exception e){
				return "NOT_SO_ENCHANTED_BOOK";
			}
			if(book.getStoredEnchants().size() == 0){
				return "ENCHANTED_BOOK";
			}
			StringBuilder sb = new StringBuilder("BOOK_OF_");
			int i = 0;
			for(Entry<Enchantment, Integer> e : book.getStoredEnchants().entrySet()){
				if(i != 0){
					sb.append(",_");
				}
				sb.append(e.getKey().getName().toUpperCase());
				sb.append("_" + e.getValue());
				i += 1;
			}
			return sb.toString();
		default:
			break;
		}
		
		if(data == 0) return mat.toString();
		return mat.toString()+ ":" + data;
	}
}
