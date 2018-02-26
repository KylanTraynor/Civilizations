package com.kylantraynor.civilizations.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.menus.Button;

public class ButtonManager {
	
	public static Map<Player, List<Button>> buttons = new HashMap<Player, List<Button>>();
	
	public static boolean registerButton(Button bt){
		List<Button> playerButtons = buttons.get(bt.getPlayer());
		if(playerButtons == null){
			playerButtons = new ArrayList<Button>();
			buttons.put(bt.getPlayer(), playerButtons);
		}
		if(playerButtons.contains(bt)){
			Civilizations.DEBUG("Button is already registered \"" + bt.getName() + "\" for player " + bt.getPlayer().getName() + ".");
			return false;
		} else {
			playerButtons.add(bt);
			Civilizations.DEBUG("Button \"" + bt.getName() + "\" for player " + bt.getPlayer().getName() + " has been registered.");
			return true;
		}
	}
	
	public static void run(Button btn){
		List<Button> playerButtons = buttons.get(btn.getPlayer());
		if(playerButtons == null) return;
		if(playerButtons.contains(btn)){
			btn.run();
		}
	}
	
	public static boolean isButton(ItemStack stk, Player player){
		if(stk == null) return false;
		List<Button> playerButtons = buttons.get(player);
		if(playerButtons == null) return false;
		for(Button btn : playerButtons){
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
		List<Button> playerButtons = buttons.get(humanEntity);
		if(playerButtons == null) return null;
		for(Button btn : playerButtons){
			if(btn.getType().equals(stk.getType())){// && humanEntity.equals(btn.getPlayer())){
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
		List<Button> playerButtons = buttons.get(player);
		if(playerButtons == null) return;
		playerButtons.clear();
		/*for(Button btn : buttons.toArray(new Button[buttons.size()])){
			if(btn.getPlayer() == player){
				Civilizations.DEBUG("Removing button \"" + btn.getName() + "\" for player " + player.getName() + ".");
				buttons.remove(btn);
			}
		}*/
	}
}
