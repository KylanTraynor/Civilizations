package com.kylantraynor.civilizations.menus;

import java.util.HashMap;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class MenuManager {
	private static HashMap<Player, Menu> menus = new HashMap<Player, Menu>();

	public static HashMap<Player, Menu> getMenus() {
		return menus;
	}

	public static void setMenus(HashMap<Player, Menu> menus) {
		MenuManager.menus = menus;
	}

	public static void openMenu(Menu menu, Player player) {
		menus.put(player, menu);
		if(menu instanceof GroupMenu){
			((GroupMenu) menu).open(player, GroupMenu.Page.MAIN);
		} else {
			menu.open(player);
		}
	}

	public static void clearMenu(HumanEntity player) {
		if(menus.containsKey(player)){
			menus.remove(player);
		}
	}
}
