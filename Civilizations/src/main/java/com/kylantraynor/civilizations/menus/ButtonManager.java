package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ButtonManager {
	
	public static List<Button> buttons = new ArrayList<Button>();
	
	public static boolean registerButton(Button bt){
		if(buttons.contains(bt)){
			return false;
		} else {
			buttons.add(bt);
			return true;
		}
	}
	
	public static void run(Button btn){
		btn.getPlayer().sendMessage("Debug1");
		if(buttons.contains(btn)){
			btn.getPlayer().sendMessage("Debug2");
			btn.run();
			btn.getPlayer().sendMessage("Debug3");
		}
	}
	
	public static boolean isButton(ItemStack stk){
		for(Button btn : buttons){
			if(btn.getType().equals(stk.getType())){
				ItemMeta im = stk.getItemMeta();
				if(im.getDisplayName().equals(btn.getItemMeta().getDisplayName())){
					if(im.getLore() == null && btn.getItemMeta().getLore() == null){
						return true;
					}
					if(im.getLore().equals(btn.getItemMeta().getLore())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Button getButton(ItemStack stk, List<HumanEntity> list){
		for(Button btn : buttons){
			if(btn.getType().equals(stk.getType()) && list.contains(btn.getPlayer())){
				ItemMeta im = stk.getItemMeta();
				if(im.getDisplayName().equals(btn.getItemMeta().getDisplayName())){
					if(im.getLore() == null && btn.getItemMeta().getLore() == null){
						return btn;
					}
					if(im.getLore().equals(btn.getItemMeta().getLore())){
						return btn;
					}
				}
			}
		}
		return null;
	}
}
