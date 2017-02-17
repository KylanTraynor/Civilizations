package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.menus.GroupMenu.Page;

public class GroupMainPage implements MenuPage {

	private Player player;
	private Group group;
	
	private MenuPage managePage;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();

	public GroupMainPage(Player player, Group group){
		this.player = player;
		this.group = group;
		managePage = new GroupManagePage(player, group);
	}
	
	@Override
	public int getRows() {
		return 2;
	}

	@Override
	public void refresh(Menu menu) {
		if(!(menu instanceof GroupMenu)) return;
		GroupMenu gMenu = (GroupMenu) menu;
		if(gMenu.getBottomInventory().getSize() / 9 <= getRows()){
			return;
		}
		buttons.put(gMenu.pos(4, 1), managePage.getIconButton());
	}

	@Override
	public Button getIconButton() {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Type: " + ChatColor.GOLD + group.getType());
		lore.add(ChatColor.WHITE + "Members: " + ChatColor.GOLD + group.getMembers().size());
		Button mainButton = new Button(player, Material.GOLD_BLOCK, group.getChatHeader(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				((GroupMenu)MenuManager.getMenus().get(player)).changePage(new GroupMainPage(player, group));
			}
			
		}, true);
		return mainButton;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.BOLD + ChatColor.GOLD + group.getName();
	}

}
