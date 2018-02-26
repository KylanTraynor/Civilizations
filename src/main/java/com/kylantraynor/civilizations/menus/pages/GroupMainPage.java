package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;

public class GroupMainPage implements MenuPage {

	private Player player;
	private Group group;
	
	private MenuPage managePage;
	private MenuPage ranksPage;
	private MenuPage parentPage;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();

	public GroupMainPage(Player player, Group group){
		this.player = player;
		this.group = group;
		managePage = new GroupManagePage(player, group);
		ranksPage = new GroupRanksPage(player, group);
		if(group.getProtection().getParent() != null){
			parentPage = new GroupMainPage(player, group.getProtection().getParent().getGroup());
		}
	}
	
	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void refresh(Menu menu) {
		if(!(menu instanceof GroupMenu)) return;
		GroupMenu gMenu = (GroupMenu) menu;
		buttons.clear();
		if(gMenu.getTopInventory().getSize() / 9 <= getRows()){
			return;
		}
		if(parentPage != null){
			buttons.put(gMenu.pos(0, 0), parentPage.getIconButton());
		}
		buttons.put(gMenu.pos(4, 0), managePage.getIconButton());
		buttons.put(gMenu.pos(4, 1), ranksPage.getIconButton());
	}

	@Override
	public Button getIconButton() {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Type: " + ChatColor.GOLD + group.getType());
		lore.add(ChatColor.WHITE + "Members: " + ChatColor.GOLD + group.getMembers().size());
		MenuPage page = this;
		Button mainButton = new Button(player, Material.GOLD_BLOCK, group.getChatHeader(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				((GroupMenu)MenuManager.getMenus().get(player)).changePage(page);
			}
			
		}, true);
		return mainButton;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.BOLD + ChatColor.GOLD + group.getName();
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

}
