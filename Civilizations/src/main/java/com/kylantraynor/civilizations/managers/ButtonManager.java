package com.kylantraynor.civilizations.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.menus.Button;

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
		if(buttons.contains(btn)){
			btn.run();
		}
	}
	
	public static boolean isButton(ItemStack stk){
		if(stk == null) return false;
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
	
	public static Button getButton(ItemStack stk, HumanEntity humanEntity){
		if(stk == null) return null;
		for(Button btn : buttons){
			if(btn.getType().equals(stk.getType()) && humanEntity.equals(btn.getPlayer())){
				ItemMeta im = stk.getItemMeta();
				if(im == null && btn.getItemMeta() == null) return btn;
				if(im.getDisplayName() == null && btn.getItemMeta().getDisplayName() == null){
					if(im.getLore() == null && btn.getItemMeta().getLore() == null){
						return btn;
					}
					if(im.getLore().equals(btn.getItemMeta().getLore())){
						return btn;
					}
				} else {
					if(im.getDisplayName() == null && btn.getItemMeta().getDisplayName() != null)
						continue;
					if(!im.getDisplayName().equals(btn.getItemMeta().getDisplayName()))
						continue;
					if(im.getLore() == null && btn.getItemMeta().getLore() == null)
						return btn;
					if(im.getLore().equals(btn.getItemMeta().getLore()))
						return btn;
				}
			}
		}
		return null;
	}

	public static void clearButtons(HumanEntity player) {
		for(Button btn : buttons.toArray(new Button[buttons.size()])){
			if(btn.getPlayer() == player){
				Civilizations.DEBUG("Removing button \"" + btn.getName() + "\" for player " + player.getName() + ".");
				buttons.remove(btn);
			}
		}
	}
}
