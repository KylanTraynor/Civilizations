package com.kylantraynor.civilizations.managers;

import java.util.HashMap;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.menus.GroupMenu.Page;

public class MenuManager {
	private static HashMap<Player, Menu> menus = new HashMap<Player, Menu>();

	public static HashMap<Player, Menu> getMenus() {
		return menus;
	}

	public static void setMenus(HashMap<Player, Menu> menus) {
		MenuManager.menus = menus;
	}

	public static Menu openMenu(Menu menu, Player player) {
		menus.put(player, menu);
		if(menu instanceof GroupMenu){
			((GroupMenu) menu).open(player, GroupMenu.Page.MAIN);
		} else {
			menu.open(player);
		}
		return menu;
	}

	public static void clearMenu(HumanEntity player) {
		if(menus.containsKey(player)){
			menus.remove(player);
		}
	}
}
