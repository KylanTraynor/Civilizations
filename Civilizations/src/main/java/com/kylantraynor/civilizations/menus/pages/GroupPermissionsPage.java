package com.kylantraynor.civilizations.menus.pages;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.protection.PermissionType;

public class GroupPermissionsPage implements MenuPage {
	
	private Player player;
	private Group group;
	
	public GroupPermissionsPage(Player player, Group group){
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
		Button permissionsButton = new Button(player, Material.EMERALD_BLOCK, "Permissions for " + group.getType(), null,
				new BukkitRunnable(){
					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(new GroupManagePage(player, group));
					}
			
		}, group.hasPermission(PermissionType.MANAGE, null, player));
		return permissionsButton;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
