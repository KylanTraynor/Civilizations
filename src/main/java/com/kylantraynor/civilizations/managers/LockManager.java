package com.kylantraynor.civilizations.managers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kylantraynor.civilizations.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.griefcraft.model.Protection.Type;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.events.PlayerLockpickEvent;
import com.kylantraynor.civilizations.hook.HookManager;
import com.kylantraynor.civilizations.hook.lwc.LWCHook;
import com.kylantraynor.civilizations.hook.towny.TownyHook;
import com.kylantraynor.civilizations.protection.LockpickSession;

public class LockManager {
	
	private static long minutesAfterLogout = 15;
	private static long daysSinceLastSeen = 15;
	
	private static Map<Player, LockpickSession> sessions = new HashMap<Player, LockpickSession>();
	
	public static void init() {
		addLockpickRecipe();
	}
	
	private static void addLockpickRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Civilizations.currentInstance, "lockpick"), getLockpick(4));
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
		return getLockLevel(block) > 0;
	}

	public static boolean isLocked(Block block) {
		if(HookManager.getLWC() != null){
			return HookManager.getLWC().hasProtection(block);
		}
		return false;
	}
	
	public static boolean hasAccess(Player player, Block block){
		if(HookManager.getLWC() != null){
			return HookManager.getLWC().canAccessProtection(player, block);
		}
		return false;
	}
	
	public static void removePickFromInventory(Inventory inventory){
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
	
	public static void removePickFromInventory(Inventory inventory, int amount){
		for(int i = 0; i < amount; i++){
			removePickFromInventory(inventory);
		}
	}

	public static void startLockpicking(Player player, Block block) {
		for(LockpickSession session : sessions.values()){
			if(session.getBlock().equals(block)){
				player.sendMessage("This is already being lockpicked.");
				return;
			}
		}
		if(HookManager.getTowny() != null){
			if(!HookManager.getTowny().hasSwitchPerm(player, block)){
				player.sendMessage("You can't pick a lock here.");
				return;
			}
		}
		if(player.getGameMode() == GameMode.CREATIVE){
			player.sendMessage("You can't pick locks in creative.");
			return;
		}
		if(HookManager.getLWC() != null){
			if(HookManager.getLWC().getLockType(block) == Type.PASSWORD){
				player.sendMessage("This type of lock can't be picked.");
				return;
			}
			if(!HookManager.getLWC().getLockOwner(block).isOnline()){
				if(Instant.ofEpochMilli(HookManager.getLWC().getLockOwner(block).getLastPlayed())
						.isBefore(Instant.now().minus(minutesAfterLogout, ChronoUnit.MINUTES))){
					if(!Instant.ofEpochMilli(HookManager.getLWC().getLockOwner(block).getLastPlayed())
							.isBefore(Instant.now().minus(daysSinceLastSeen, ChronoUnit.DAYS))){
						player.sendMessage("You can only unlock a chest if its owner is online, or past "+ daysSinceLastSeen +" days of absence.");
						return;
					}
				}
			}
		}
		if(block.getLocation().distance(player.getLocation()) < 3.0){
			PlayerLockpickEvent event = new PlayerLockpickEvent(player, block);
			Civilizations.callEvent(event);
			if(!event.isCancelled()){
				sessions.put(player, new LockpickSession(player, block));
			}
		} else {
			player.sendMessage("You're too far to try picking this lock.");
		}
	}


	public static void stopLockpicking(HumanEntity humanEntity){
		if(sessions.containsKey(humanEntity)){
			sessions.remove(humanEntity);
		}
	}

	public static void unlock(Block block) {
		if(isLockable(block)){
			if(isLocked(block)){
				if(HookManager.getLWC() != null){
					HookManager.getLWC().unlock(block);
				} else {
					
				}
			}
		}
	}

	public static int getLockLevel(Block block) {
		if(block.getType() == Material.CHEST) return 4;
		if(block.getType() == Material.TRAPPED_CHEST) return 5;
		if(block.getType() == Material.IRON_DOOR) return 10;
		if(Utils.isDoor(block.getType())) return 5;
		if(block.getType() == Material.FURNACE) return 1;
        if(block.getType() == Material.IRON_TRAPDOOR) return 8;
		if(Utils.isTrapdoor(block.getType())) return 4;
		if(block.getType() == Material.OAK_FENCE_GATE) return 4;
		if(block.getType() == Material.SPRUCE_FENCE_GATE) return 4;
		if(block.getType() == Material.DARK_OAK_FENCE_GATE) return 4;
		if(block.getType() == Material.BIRCH_FENCE_GATE) return 4;
		if(block.getType() == Material.ACACIA_FENCE_GATE) return 4;
		if(block.getType() == Material.JUNGLE_FENCE_GATE) return 4;
		return 0;
	}
}
