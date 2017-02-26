package com.kylantraynor.civilizations.menus.pages;

import java.util.HashMap;
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
import com.kylantraynor.civilizations.protection.PermissionType;

public class GroupManagePage implements MenuPage {

	private Player player;
	private Group group;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();

	public GroupManagePage(Player player, Group group){
		this.player = player;
		this.group = group;
	}
	
	@Override
	public int getRows() {
		return 2;
	}

	@Override
	public void refresh(Menu menu) {
		// TODO Auto-generated method stub

	}

	@Override
	public Button getIconButton() {
		MenuPage page = this;
		Button manageButton = new Button(player, Material.EMERALD_BLOCK, "Manage " + group.getType(), null,
				new BukkitRunnable(){
					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(page);
					}
			
		}, group.hasPermission(PermissionType.MANAGE, null, player));
		return manageButton;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.BOLD + ChatColor.GOLD + "Manage " + group.getName();
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

}
