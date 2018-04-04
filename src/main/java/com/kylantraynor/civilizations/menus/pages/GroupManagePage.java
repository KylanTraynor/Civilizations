package com.kylantraynor.civilizations.menus.pages;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.protection.PermissionType;

public class GroupManagePage implements MenuPage {

	final private Player player;
	final private Group group;

	private MenuPage membersPermissions;
	private MenuPage outsiderPermissions;
	
	private Map<Integer, Button> buttons = new HashMap<>();

	public GroupManagePage(Player player, Group group){
		this.player = player;
		this.group = group;

		membersPermissions = new GroupPermissionsPage(player, group, group.getIdentifier());
		outsiderPermissions = new GroupPermissionsPage(player, group, null);
	}
	
	@Override
	public int getRows() {
		return 2;
	}

	@Override
	public void refresh(Menu menu) {
		buttons.put(4, membersPermissions.getIconButton());
	    buttons.put(4+9, outsiderPermissions.getIconButton());
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
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE, group, player, true));
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
