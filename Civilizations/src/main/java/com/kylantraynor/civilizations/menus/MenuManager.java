package com.kylantraynor.civilizations.menus;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class MenuManager {
	private static HashMap<Player, GroupMenu> menus = new HashMap<Player, GroupMenu>();

	public static HashMap<Player, GroupMenu> getMenus() {
		return menus;
	}

	public static void setMenus(HashMap<Player, GroupMenu> menus) {
		MenuManager.menus = menus;
	}

	public static void openMenu(GroupMenu groupMenu, Player player) {
		menus.put(player, groupMenu);
		groupMenu.open(player, GroupMenu.Page.MAIN);
	}
}
