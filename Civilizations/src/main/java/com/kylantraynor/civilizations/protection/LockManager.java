package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.kylantraynor.civilizations.hook.lwc.LWCHook;

public class LockManager {
	
	private static Map<Player, LockpickSession> sessions = new HashMap<Player, LockpickSession>();
	
	public static void init() {
		addLockpickRecipe();
	}
	
	private static void addLockpickRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(getLockpick(4));
		recipe.shape("A", "A");
		recipe.setIngredient('A', Material.IRON_INGOT);
		Bukkit.getServer().addRecipe(recipe);
	}

	public static boolean isLockpick(ItemStack is){
		if(is.getType() != Material.TRIPWIRE_HOOK) return false;
		ItemMeta im = is.getItemMeta();
		if(!im.getDisplayName().equalsIgnoreCase("Lockpick")) return false;
		if(!im.getLore().get(0).equalsIgnoreCase("Can be used to pick locks.")) return false;
		return true;
	}
	
	public static ItemStack getLockpick(int amount){
		ItemMeta im = Bukkit.getItemFactory().getItemMeta(Material.TRIPWIRE_HOOK);
		im.setDisplayName("Lockpick");
		List<String> lore = new ArrayList<String>();
		lore.add("Can be used to pick locks.");
		im.setLore(lore);
		ItemStack is = new ItemStack(Material.TRIPWIRE_HOOK);
		is.setItemMeta(im);
		is.setAmount(amount);
		return is;
	}

	public static boolean isLockable(Block block) {
		if(block.getType() == Material.CHEST) return true;
		if(block.getType() == Material.TRAPPED_CHEST) return true;
		if(block.getType() == Material.WOOD_DOOR) return true;
		if(block.getType() == Material.TRAP_DOOR) return true;
		if(block.getType() == Material.IRON_TRAPDOOR) return true;
		return false;
	}

	public static boolean isLocked(Block block) {
		if(LWCHook.isActive()){
			return LWCHook.hasPortection(block);
		}
		return false;
	}
	
	public static boolean hasAccess(Player player, Block block){
		if(LWCHook.isActive()){
			return LWCHook.canAccessProtection(player, block);
		}
		return false;
	}
	
	public static void removePickFromInventory(Inventory inventory, int amount){
		for( int i = 0 ; i < inventory.getContents().length; i++){
			if(inventory.getContents()[i] != null){
				if(isLockpick(inventory.getContents()[i])){
					if(inventory.getContents()[i].getAmount() > 1){
						inventory.getContents()[i].setAmount(inventory.getContents()[i].getAmount() - 1);
					} else {
						inventory.removeItem(inventory.getContents()[i]);
					}
					break;
				}
			}
		}
	}

	public static void startLockpicking(Player player, Block block) {
		for(LockpickSession session : sessions.values()){
			if(session.getBlock().equals(block)){
				player.sendMessage("This is already being lockpicked.");
				return;
			}
		}
		if(block.getLocation().distance(player.getLocation()) <= 3){
			sessions.put(player, new LockpickSession(player, block));
		} else {
			player.sendMessage("You're too far to try picking this lock.");
		}
	}

	public static void unlock(Block block) {
		if(isLockable(block)){
			if(isLocked(block)){
				if(LWCHook.isActive()){
					LWCHook.unlock(block);
				} else {
					
				}
			}
		}
	}
}
