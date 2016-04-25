package com.kylantraynor.civilizations.protection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.kylantraynor.civilizations.hook.lwc.LWCHook;

public class LockManager {
	
	private static Map<Player, LockpickSession> sessions = new HashMap<Player, LockpickSession>();
	
	public LockManager(){
		addLockpickRecipe();
	}
	
	private void addLockpickRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(getLockpick(4));
		recipe.shape("A", "A");
		recipe.setIngredient('A', Material.IRON_INGOT);
		Bukkit.getServer().addRecipe(recipe);
	}

	public boolean isLockpick(ItemStack is){
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

	public static void startLockPicking(Player player, Block block) {
		player.sendMessage("Lockpicking is yet to be implemented.");
		for(LockpickSession session : sessions.values()){
			if(session.getBlock().equals(block)){
				player.sendMessage("This is already being lockpicked.");
			}
		}
		/*
		sessions.put(player, new LockpickSession(player, block)) 
		*/
	}
}
