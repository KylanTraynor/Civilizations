package com.kylantraynor.civilizations.utils;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import com.kylantraynor.civilizations.shapes.Shape;

public class Utils {

    /**
     * Parse a string such as "world,12,22,32,0.5,0.5" to a location.
     * @param s {@link String} to convert
     * @return Resulting {@link Location}, or {@code null} if couldn't parse properly.
     */
	public static Location parseLocation(String s){
		String[] ss = s.split(",");
		try {
			if(ss.length != 6){
				throw new ParseException("Couldn't parse location from: " + s + ".", 0);
			} else {
				World w = Bukkit.getWorld(UUID.fromString(ss[0]));
				double x = Double.parseDouble(ss[1]);
				double y = Double.parseDouble(ss[2]);
				double z = Double.parseDouble(ss[3]);
				float yaw = Float.parseFloat(ss[4]);
				float pitch = Float.parseFloat(ss[5]);
				return new Location(w, x, y, z, yaw, pitch);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Identifier parseIdentifier(String s){
		String[] ar = s.split(":");
		switch(ar.length){
			case 1: return SimpleIdentifier.parse(s);
			case 2: return DoubleIdentifier.parse(ar[0],ar[1]);
			default: throw new IllegalArgumentException();
		}
	}

    /**
     * Turn a {@link Location} into a {@link String} that can be parsed with {@link #parseLocation(String)}.
     * @param loc {@link Location} to convert
     * @return Resulting {@link String}
     */
	public static String locationToString(Location loc){
		//String format = "%s,%d,%d,%d,%f,%f";
		return loc.getWorld().getUID().toString() +","+ loc.getX() +","+ loc.getY() +","+ loc.getZ() +","+ loc.getYaw() +","+ loc.getPitch();
	}
	
	public static UUID asUuid(byte[] bytes) {
	    ByteBuffer bb = ByteBuffer.wrap(bytes);
	    long firstLong = bb.getLong();
	    long secondLong = bb.getLong();
	    return new UUID(firstLong, secondLong);
	}

	public static byte[] asBytes(UUID uuid) {
	    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    return bb.array();
	}
	
	public static double det(double x1, double y1, double x2, double y2){
		return x1 * y2 - x2 * y1;
	}
	/**
	 * Turns a difference between two instants into a readable string.
	 * @param from
	 * @param to
	 * @return
	 */
	public static String durationToString(Instant from, Instant to){
		Duration duration = Duration.between(from, to);
		long years = 0;
		long months = 0;
		long days = duration.toDays();
		if(days > 30){
			months = days / 30;
		}
		if(months > 12){
			years = months / 12;
		}
		long hours = duration.toHours();
		long minutes = duration.toMinutes();
		if(years > 1){
			return "" + years + " years";
		} else if(years == 1){
			return "a year";
		} else if(months > 1){
			return "" + months + " months";
		} else if(months == 1){
			return "a month";
		} else if(days > 1){
			return "" + days + " days";
		} else if(hours > 1){
			return "" + hours + " hours";
		} else if(hours == 1){
			return "an hour";
		} else if(minutes > 1){
			return "" + minutes + " minutes";
		} else if(minutes == 1){
			return "a minute";
		} else {
			return "less than a minute";
		}
	}
	
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
			sb.append(s.toString()).append(" ");
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
		StringBuilder result = new StringBuilder();
		Iterator<String> it = list.iterator();
		while(it.hasNext()){
			result.append(it.next());
			if(it.hasNext()){
				result.append(link);
			}
		}
		return result.toString();
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
	 * Gets the material's name of the MaterialAndData.
	 * @param materialAndData
	 * @return
	 */
	public static String getMaterialName(MaterialAndData materialAndData){
		return getDataName(materialAndData.getMaterial(), materialAndData.getData(), null);
	}

	/**
	 * Converts a name like IRON_INGOT into Iron Ingot to improve readability
	 * @param ugly A string such as IRON_INGOT
	 * @return A nicer version, such as Iron Ingot
	 * 
	 * Credits to mikenon on GitHub!
	 */
	public static String prettifyText(String ugly){
		if(!ugly.contains("_") && (!ugly.equals(ugly.toUpperCase()))) return ugly;
		StringBuilder fin = new StringBuilder();
		ugly = ugly.toLowerCase();
		if(ugly.contains("_")){
			String[] splt = ugly.split("_");
			int i = 0;
			for(String s : splt){
				i += 1;
				fin.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
				if(i<splt.length) fin.append(" ");
			}
		} else {
			fin.append(Character.toUpperCase(ugly.charAt(0))).append(ugly.substring(1));
		}
		return fin.toString();
	}
	/**
	 * Converts a given material and data value into a format similar to Material.<?>.toString().
	 * Upper case, with underscores.  Includes material name in result.
	 * @param mat The base material.
	 * @param data The date of the material.
	 * @param meta The ItemMeta of the item.
	 * @return A string with the name of the item.
	 * 
	 * @author maxgamer
	 * @author Modified by KylanTraynor
	 */
	private static String getDataName(Material mat, short data, ItemMeta meta){
		switch(mat){
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
				sb.append("_").append(e.getValue());
				i += 1;
			}
			return sb.toString();
		default:
			break;
		}
		
		return mat.toString();
	}
	
	/**
	 * Get the {@link ChatColor} that is as close as possible to the given {@link DyeColor}.
	 * @param color {@link DyeColor} to convert
	 * @return The converted {@link ChatColor}
	 */
	public static ChatColor getChatColor(DyeColor color){
        switch(color){
            case BLACK: return ChatColor.BLACK;
            case BLUE: return ChatColor.DARK_BLUE;
            case BROWN: return ChatColor.GOLD;
            case CYAN: return ChatColor.AQUA;
            case GRAY: return ChatColor.DARK_GRAY;
            case GREEN: return ChatColor.DARK_GREEN;
            case LIGHT_BLUE: return ChatColor.BLUE;
            case LIGHT_GRAY: return ChatColor.GRAY;
            case LIME: return ChatColor.GREEN;
            case MAGENTA: return ChatColor.LIGHT_PURPLE;
            case ORANGE: return ChatColor.GOLD;
            case PINK: return ChatColor.LIGHT_PURPLE;
            case PURPLE: return ChatColor.DARK_PURPLE;
            case RED: return ChatColor.DARK_RED;
            case WHITE: return ChatColor.WHITE;
            case YELLOW: return ChatColor.YELLOW;
            default:
                return null;
        }
	}

    /**
     * Check if the {@link BlockData} requires any supplies to be built.
     * @param bd {@link BlockData}
     * @return {@code true} if supplies are needed, {@code false} otherwise
     */
	public static boolean requireSupplies(BlockData bd){
	    switch(bd.getMaterial()){
            case AIR:
                return false;
        }
        return true;
    }

    /**
     * Check if the {@link Material} can match the given {@link BlockData}.
     * @param block {@link BlockData} to check against
     * @param item {@link Material} to check
     * @return {@code true} if the two match, {@code false} otherwise
     */
	public static boolean isSameBlock(BlockData block, Material item){
        if(block.getMaterial() == Material.GRASS_PATH) {
            switch(item){
                case GRASS_BLOCK:
                case DIRT:
                case COARSE_DIRT:
                case PODZOL:
                    return true;
            }
            return false;
        }
        if(block.getMaterial() == Material.WALL_SIGN && item == Material.SIGN) return true;
        if(block.getMaterial() == Material.WALL_TORCH && item == Material.TORCH) return true;
        if(block.getMaterial() == Material.REDSTONE_WIRE && item == Material.REDSTONE) return true;
        if(block.getMaterial() == Material.REDSTONE_WALL_TORCH && item == Material.REDSTONE_TORCH) return true;
        if(block.getMaterial() == Material.TRIPWIRE && item == Material.STRING) return true;

        if(block instanceof Slab){
            Slab slab = (Slab) block;
            if(slab.getType() == Slab.Type.DOUBLE){
                if(block.getMaterial() == Material.SANDSTONE_SLAB && item == Material.SANDSTONE) return true;
                if(block.getMaterial() == Material.ACACIA_SLAB && item == Material.ACACIA_PLANKS) return true;
                if(block.getMaterial() == Material.BIRCH_SLAB && item == Material.BIRCH_PLANKS) return true;
                if(block.getMaterial() == Material.BRICK_SLAB && item == Material.BRICKS) return true;
                if(block.getMaterial() == Material.COBBLESTONE_SLAB && item == Material.COBBLESTONE) return true;
                if(block.getMaterial() == Material.DARK_OAK_SLAB && item == Material.DARK_OAK_PLANKS) return true;
                if(block.getMaterial() == Material.DARK_PRISMARINE_SLAB && item == Material.DARK_PRISMARINE) return true;
                if(block.getMaterial() == Material.JUNGLE_SLAB && item == Material.JUNGLE_PLANKS) return true;
                if(block.getMaterial() == Material.NETHER_BRICK_SLAB && item == Material.NETHER_BRICKS) return true;
                if(block.getMaterial() == Material.OAK_SLAB && item == Material.OAK_PLANKS) return true;
                if(block.getMaterial() == Material.PETRIFIED_OAK_SLAB && item == Material.DIRT) return true;
                if(block.getMaterial() == Material.PRISMARINE_BRICK_SLAB && item == Material.PRISMARINE_BRICKS) return true;
                if(block.getMaterial() == Material.PRISMARINE_SLAB && item == Material.PRISMARINE) return true;
                if(block.getMaterial() == Material.PURPUR_SLAB && item == Material.PURPUR_BLOCK) return true;
                if(block.getMaterial() == Material.QUARTZ_SLAB && item == Material.QUARTZ_BLOCK) return true;
                if(block.getMaterial() == Material.RED_SANDSTONE_SLAB && item == Material.RED_SANDSTONE) return true;
                if(block.getMaterial() == Material.SPRUCE_SLAB && item == Material.SPRUCE_PLANKS) return true;
                if(block.getMaterial() == Material.STONE_BRICK_SLAB && item == Material.STONE_BRICKS) return true;
                if(block.getMaterial() == Material.STONE_SLAB && item == Material.STONE) return true;
            }
        }

	    return block.getMaterial() == item;
	}

    /**
     * Check if the given {@link Material} is a {@code DOOR} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code DOOR}, {@code false} otherwise
     */
	public static boolean isDoor(Material type){
        switch(type){
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code TRAPDOOR} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code TRAPDOOR}, {@code false} otherwise
     */
    public static boolean isTrapdoor(Material type){
        switch(type){
            case DARK_OAK_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case IRON_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code WOOL} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code WOOL}, {@code false} otherwise
     */
	public static boolean isWool(Material type){
	    switch(type){
            case WHITE_WOOL:
            case BLACK_WOOL:
            case BLUE_WOOL:
            case BROWN_WOOL:
            case CYAN_WOOL:
            case GRAY_WOOL:
            case GREEN_WOOL:
            case LIGHT_BLUE_WOOL:
            case LIGHT_GRAY_WOOL:
            case LIME_WOOL:
            case MAGENTA_WOOL:
            case ORANGE_WOOL:
            case PINK_WOOL:
            case PURPLE_WOOL:
            case RED_WOOL:
            case YELLOW_WOOL:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code BANNER} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code BANNER}, {@code false} otherwise
     */
    public static boolean isBanner(Material type){
	    switch(type){
            case BLACK_BANNER:
            case BLUE_BANNER:
            case BROWN_BANNER:
            case CYAN_BANNER:
            case GRAY_BANNER:
            case GREEN_BANNER:
            case LIGHT_BLUE_BANNER:
            case LIGHT_GRAY_BANNER:
            case LIME_BANNER:
            case MAGENTA_BANNER:
            case ORANGE_BANNER:
            case PINK_BANNER:
            case PURPLE_BANNER:
            case RED_BANNER:
            case WHITE_BANNER:
            case YELLOW_BANNER:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code WALL_BANNER} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code WALL_BANNER}, {@code false} otherwise
     */
    public static boolean isWallBanner(Material type){
        switch(type){
            case BLACK_WALL_BANNER:
            case BLUE_WALL_BANNER:
            case BROWN_WALL_BANNER:
            case CYAN_WALL_BANNER:
            case GRAY_WALL_BANNER:
            case GREEN_WALL_BANNER:
            case LIGHT_BLUE_WALL_BANNER:
            case LIGHT_GRAY_WALL_BANNER:
            case LIME_WALL_BANNER:
            case MAGENTA_WALL_BANNER:
            case ORANGE_WALL_BANNER:
            case PINK_WALL_BANNER:
            case PURPLE_WALL_BANNER:
            case RED_WALL_BANNER:
            case WHITE_WALL_BANNER:
            case YELLOW_WALL_BANNER:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code BED} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code BED}, {@code false} otherwise
     */
    public static boolean isBed(Material type){
        switch(type){
            case BLACK_BED:
            case BLUE_BED:
            case BROWN_BED:
            case CYAN_BED:
            case GRAY_BED:
            case GREEN_BED:
            case LIGHT_BLUE_BED:
            case LIGHT_GRAY_BED:
            case LIME_BED:
            case MAGENTA_BED:
            case ORANGE_BED:
            case PINK_BED:
            case PURPLE_BED:
            case RED_BED:
            case WHITE_BED:
            case YELLOW_BED:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code GLASS_BLOCK} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code GLASS_BLOCK}, {@code false} otherwise
     */
    public static boolean isGlassBlock(Material type){
	    switch(type){
            case GLASS:
            case BLACK_STAINED_GLASS:
            case BLUE_STAINED_GLASS:
            case BROWN_STAINED_GLASS:
            case CYAN_STAINED_GLASS:
            case GRAY_STAINED_GLASS:
            case GREEN_STAINED_GLASS:
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_GRAY_STAINED_GLASS:
            case LIME_STAINED_GLASS:
            case MAGENTA_STAINED_GLASS:
            case ORANGE_STAINED_GLASS:
            case PINK_STAINED_GLASS:
            case PURPLE_STAINED_GLASS:
            case RED_STAINED_GLASS:
            case WHITE_STAINED_GLASS:
            case YELLOW_STAINED_GLASS:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code GLASS_PANE} or its colored variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code GLASS_PANE}, {@code false} otherwise
     */
    public static boolean isGlassPane(Material type){
	    switch(type){
            case GLASS_PANE:
            case BLACK_STAINED_GLASS_PANE:
            case BLUE_STAINED_GLASS_PANE:
            case BROWN_STAINED_GLASS_PANE:
            case CYAN_STAINED_GLASS_PANE:
            case GRAY_STAINED_GLASS_PANE:
            case GREEN_STAINED_GLASS_PANE:
            case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIGHT_GRAY_STAINED_GLASS_PANE:
            case LIME_STAINED_GLASS_PANE:
            case MAGENTA_STAINED_GLASS_PANE:
            case ORANGE_STAINED_GLASS_PANE:
            case PINK_STAINED_GLASS_PANE:
            case PURPLE_STAINED_GLASS_PANE:
            case RED_STAINED_GLASS_PANE:
            case WHITE_STAINED_GLASS_PANE:
            case YELLOW_STAINED_GLASS_PANE:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code LOG} or its variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code LOG}, {@code false} otherwise
     */
    public static boolean isLog(Material type){
        switch(type){
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
                return true;
        }
        return false;
    }

    /**
     * Check if the given {@link Material} is a {@code STRIPPED_LOG} or its variants.
     * @param type {@link Material}
     * @return {@code true} if it is a {@code STRIPPED_LOG}, {@code false} otherwise
     */
    public static boolean isStrippedLog(Material type){
        switch(type){
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_OAK_LOG:
            case STRIPPED_SPRUCE_LOG:
                return true;
        }
        return false;
    }

    /**
     * Get the sound corresponding to the material being broken.
     * @param type {@link Material}
     * @return {@link Sound}
     */
    public static Sound getBreakSoundFromMaterial(Material type) {
        if(isWool(type)){
            return Sound.BLOCK_WOOL_BREAK;
        } else if(isGlassBlock(type) || isGlassPane(type)){
            return Sound.BLOCK_GLASS_BREAK;
        } else if(isLog(type) || isStrippedLog(type)){
            return Sound.BLOCK_WOOD_BREAK;
        }
        switch(type){
            case SLIME_BLOCK:
                return Sound.BLOCK_SLIME_BLOCK_BREAK;
            case SAND:
                return Sound.BLOCK_SAND_BREAK;
            case GRAVEL:
                return Sound.BLOCK_GRAVEL_BREAK;
            case GRASS_BLOCK:
                return Sound.BLOCK_GRASS_BREAK;
            case IRON_BLOCK: case GOLD_BLOCK: case DIAMOND_BLOCK:
                return Sound.BLOCK_METAL_BREAK;
            case SNOW_BLOCK: case SNOW:
                return Sound.BLOCK_SNOW_BREAK;
            default:
                return Sound.BLOCK_STONE_BREAK;
        }
    }

    /**
     * Get the sound corresponding to the material being placed.
     * @param type {@link Material}
     * @return {@link Sound}
     */
	public static Sound getPlaceSoundFromMaterial(Material type) {
	    if(isWool(type)){
            return Sound.BLOCK_WOOL_PLACE;
        } else if(isGlassBlock(type) || isGlassPane(type)){
	        return Sound.BLOCK_GLASS_PLACE;
        } else if(isLog(type) || isStrippedLog(type)){
	        return Sound.BLOCK_WOOD_PLACE;
        }
		switch(type){
            case SLIME_BLOCK:
                return Sound.BLOCK_SLIME_BLOCK_PLACE;
            case SAND:
                return Sound.BLOCK_SAND_PLACE;
            case GRAVEL:
                return Sound.BLOCK_GRAVEL_PLACE;
            case GRASS_BLOCK:
                return Sound.BLOCK_GRASS_PLACE;
            case IRON_BLOCK: case GOLD_BLOCK: case DIAMOND_BLOCK:
                return Sound.BLOCK_METAL_PLACE;
            case SNOW_BLOCK: case SNOW:
                return Sound.BLOCK_SNOW_PLACE;
            default:
                return Sound.BLOCK_STONE_PLACE;
		}
	}

    /**
     * Get the {@link DyeColor} corresponding to the given colored {@link Material}.
     * @param material {@link Material}
     * @return Corresponding {@link DyeColor}
     */
    public static DyeColor getColor(Material material){

        switch(material){
            case BLACK_BANNER: case BLACK_WALL_BANNER: case BLACK_BED: case BLACK_CARPET:
            case BLACK_CONCRETE: case BLACK_CONCRETE_POWDER: case BLACK_GLAZED_TERRACOTTA:
            case BLACK_SHULKER_BOX: case BLACK_STAINED_GLASS: case BLACK_STAINED_GLASS_PANE:
            case BLACK_TERRACOTTA: case BLACK_WOOL:
                return DyeColor.BLACK;
            case BLUE_BANNER: case BLUE_WALL_BANNER: case BLUE_BED: case BLUE_CARPET:
            case BLUE_CONCRETE: case BLUE_CONCRETE_POWDER: case BLUE_GLAZED_TERRACOTTA:
            case BLUE_SHULKER_BOX: case BLUE_STAINED_GLASS: case BLUE_STAINED_GLASS_PANE:
            case BLUE_TERRACOTTA: case BLUE_WOOL:
                return DyeColor.BLUE;
            case BROWN_BANNER: case BROWN_WALL_BANNER: case BROWN_BED: case BROWN_CARPET:
            case BROWN_CONCRETE: case BROWN_CONCRETE_POWDER: case BROWN_GLAZED_TERRACOTTA:
            case BROWN_SHULKER_BOX: case BROWN_STAINED_GLASS: case BROWN_STAINED_GLASS_PANE:
            case BROWN_TERRACOTTA: case BROWN_WOOL:
                return DyeColor.BROWN;
            case CYAN_BANNER: case CYAN_WALL_BANNER: case CYAN_BED: case CYAN_CARPET:
            case CYAN_CONCRETE: case CYAN_CONCRETE_POWDER: case CYAN_GLAZED_TERRACOTTA:
            case CYAN_SHULKER_BOX: case CYAN_STAINED_GLASS: case CYAN_STAINED_GLASS_PANE:
            case CYAN_TERRACOTTA: case CYAN_WOOL:
                return DyeColor.CYAN;
            case GRAY_BANNER: case GRAY_WALL_BANNER: case GRAY_BED: case GRAY_CARPET:
            case GRAY_CONCRETE: case GRAY_CONCRETE_POWDER: case GRAY_GLAZED_TERRACOTTA:
            case GRAY_SHULKER_BOX: case GRAY_STAINED_GLASS: case GRAY_STAINED_GLASS_PANE:
            case GRAY_TERRACOTTA: case GRAY_WOOL:
                return DyeColor.GRAY;
            case GREEN_BANNER: case GREEN_WALL_BANNER: case GREEN_BED: case GREEN_CARPET:
            case GREEN_CONCRETE: case GREEN_CONCRETE_POWDER: case GREEN_GLAZED_TERRACOTTA:
            case GREEN_SHULKER_BOX: case GREEN_STAINED_GLASS: case GREEN_STAINED_GLASS_PANE:
            case GREEN_TERRACOTTA: case GREEN_WOOL:
                return DyeColor.GREEN;
            case LIGHT_BLUE_BANNER: case LIGHT_BLUE_WALL_BANNER: case LIGHT_BLUE_BED: case LIGHT_BLUE_CARPET:
            case LIGHT_BLUE_CONCRETE: case LIGHT_BLUE_CONCRETE_POWDER: case LIGHT_BLUE_GLAZED_TERRACOTTA:
            case LIGHT_BLUE_SHULKER_BOX: case LIGHT_BLUE_STAINED_GLASS: case LIGHT_BLUE_STAINED_GLASS_PANE:
            case LIGHT_BLUE_TERRACOTTA: case LIGHT_BLUE_WOOL:
                return DyeColor.LIGHT_BLUE;
            case LIGHT_GRAY_BANNER: case LIGHT_GRAY_WALL_BANNER: case LIGHT_GRAY_BED: case LIGHT_GRAY_CARPET:
            case LIGHT_GRAY_CONCRETE: case LIGHT_GRAY_CONCRETE_POWDER: case LIGHT_GRAY_GLAZED_TERRACOTTA:
            case LIGHT_GRAY_SHULKER_BOX: case LIGHT_GRAY_STAINED_GLASS: case LIGHT_GRAY_STAINED_GLASS_PANE:
            case LIGHT_GRAY_TERRACOTTA: case LIGHT_GRAY_WOOL:
                return DyeColor.LIGHT_GRAY;
            case LIME_BANNER: case LIME_WALL_BANNER: case LIME_BED: case LIME_CARPET:
            case LIME_CONCRETE: case LIME_CONCRETE_POWDER: case LIME_GLAZED_TERRACOTTA:
            case LIME_SHULKER_BOX: case LIME_STAINED_GLASS: case LIME_STAINED_GLASS_PANE:
            case LIME_TERRACOTTA: case LIME_WOOL:
                return DyeColor.LIME;
            case MAGENTA_BANNER: case MAGENTA_WALL_BANNER: case MAGENTA_BED: case MAGENTA_CARPET:
            case MAGENTA_CONCRETE: case MAGENTA_CONCRETE_POWDER: case MAGENTA_GLAZED_TERRACOTTA:
            case MAGENTA_SHULKER_BOX: case MAGENTA_STAINED_GLASS: case MAGENTA_STAINED_GLASS_PANE:
            case MAGENTA_TERRACOTTA: case MAGENTA_WOOL:
                return DyeColor.MAGENTA;
            case ORANGE_BANNER: case ORANGE_WALL_BANNER: case ORANGE_BED: case ORANGE_CARPET:
            case ORANGE_CONCRETE: case ORANGE_CONCRETE_POWDER: case ORANGE_GLAZED_TERRACOTTA:
            case ORANGE_SHULKER_BOX: case ORANGE_STAINED_GLASS: case ORANGE_STAINED_GLASS_PANE:
            case ORANGE_TERRACOTTA: case ORANGE_WOOL:
                return DyeColor.ORANGE;
            case PINK_BANNER: case PINK_WALL_BANNER: case PINK_BED: case PINK_CARPET:
            case PINK_CONCRETE: case PINK_CONCRETE_POWDER: case PINK_GLAZED_TERRACOTTA:
            case PINK_SHULKER_BOX: case PINK_STAINED_GLASS: case PINK_STAINED_GLASS_PANE:
            case PINK_TERRACOTTA: case PINK_WOOL:
                return DyeColor.PINK;
            case PURPLE_BANNER: case PURPLE_WALL_BANNER: case PURPLE_BED: case PURPLE_CARPET:
            case PURPLE_CONCRETE: case PURPLE_CONCRETE_POWDER: case PURPLE_GLAZED_TERRACOTTA:
            case PURPLE_SHULKER_BOX: case PURPLE_STAINED_GLASS: case PURPLE_STAINED_GLASS_PANE:
            case PURPLE_TERRACOTTA: case PURPLE_WOOL:
                return DyeColor.PURPLE;
            case RED_BANNER: case RED_WALL_BANNER: case RED_BED: case RED_CARPET:
            case RED_CONCRETE: case RED_CONCRETE_POWDER: case RED_GLAZED_TERRACOTTA:
            case RED_SHULKER_BOX: case RED_STAINED_GLASS: case RED_STAINED_GLASS_PANE:
            case RED_TERRACOTTA: case RED_WOOL:
                return DyeColor.RED;
            case WHITE_BANNER: case WHITE_WALL_BANNER: case WHITE_BED: case WHITE_CARPET:
            case WHITE_CONCRETE: case WHITE_CONCRETE_POWDER: case WHITE_GLAZED_TERRACOTTA:
            case WHITE_SHULKER_BOX: case WHITE_STAINED_GLASS: case WHITE_STAINED_GLASS_PANE:
            case WHITE_TERRACOTTA: case WHITE_WOOL:
                return DyeColor.WHITE;
            case YELLOW_BANNER: case YELLOW_WALL_BANNER: case YELLOW_BED: case YELLOW_CARPET:
            case YELLOW_CONCRETE: case YELLOW_CONCRETE_POWDER: case YELLOW_GLAZED_TERRACOTTA:
            case YELLOW_SHULKER_BOX: case YELLOW_STAINED_GLASS: case YELLOW_STAINED_GLASS_PANE:
            case YELLOW_TERRACOTTA: case YELLOW_WOOL:
                return DyeColor.YELLOW;
            default:
                return DyeColor.BLACK;
        }
    }
}
