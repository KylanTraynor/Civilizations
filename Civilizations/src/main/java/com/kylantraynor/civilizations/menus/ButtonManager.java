package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;

public class ButtonManager {
	
	public static Map<Button, BukkitRunnable> buttons = new HashMap<Button, BukkitRunnable>();
	
	public static boolean registerButton(Button bt, BukkitRunnable run){
		if(buttons.containsKey(bt)){
			return false;
		} else {
			buttons.put(bt, run);
			return true;
		}
	}
	
	public static void run(Button btn){
		if(buttons.containsKey(btn)){
			if(btn.isEnabled()){
				buttons.get(btn).runTask(Civilizations.currentInstance);
			}
		}
	}
	
	public static boolean isButton(ItemStack stk){
		for(Button btn : buttons.keySet()){
			if(btn.getType().equals(stk.getType())){
				ItemMeta im = stk.getItemMeta();
				if(im.getDisplayName().equals(btn.getItemMeta().getDisplayName())){
					if(im.getLore().equals(btn.getItemMeta().getLore())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Button getButton(ItemStack stk, List<HumanEntity> list){
		for(Button btn : buttons.keySet()){
			if(btn.getType().equals(stk.getType()) && list.contains(btn.getPlayer())){
				ItemMeta im = stk.getItemMeta();
				if(im.getDisplayName().equals(btn.getItemMeta().getDisplayName())){
					if(im.getLore().equals(btn.getItemMeta().getLore())){
						return btn;
					}
				}
			}
		}
		return null;
	}
}
