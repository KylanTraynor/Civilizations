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
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.protection.PermissionTarget;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Permissions;
import com.kylantraynor.civilizations.util.Util;

public class GroupPermissionsPage implements MenuPage {
	
	private Player player;
	private Group group;
	private PermissionTarget target;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	
	public GroupPermissionsPage(Player player, Group group, PermissionTarget target){
		this.player = player;
		this.group = group;
		this.target = target;
	}
	
	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void refresh(Menu menu) {
		int i = 0;
		for(PermissionType pt : PermissionType.values()){
			buttons.put(i++, getPermissionButton(pt));
		}
	}

	private Button getPermissionButton(PermissionType pt) {
		
		boolean isSet = false;
		boolean value = false;
		
		List<String> lore = new ArrayList<String>();
		
		lore.add(ChatColor.GRAY + pt.getDescription());
		
		Permissions perms = group.getProtection().getPermissionSet().get(target);
		isSet = perms != null;
		if(isSet){
			isSet = perms.contains(pt);
			if(isSet){
				value = perms.get(pt);
			}
		}
		
		Material mat;
		mat = isSet ? (value ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK) : Material.IRON_BLOCK;
		if(isSet){
			lore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);
		} else {
			lore.add(ChatColor.WHITE + "Not set" + ChatColor.GRAY + " (Will inherit parent's value)");
		}
		
		final boolean set = isSet;
		final boolean val = value;
		Button permissionButton = new Button(player,mat, ChatColor.WHITE + Util.prettifyText(pt.toString()), lore, 
			new BukkitRunnable(){
				@Override
				public void run() {
					if(set){
						if(val){
							ProtectionManager.setPermission(group.getProtection(), target, pt, false);
						} else {
							ProtectionManager.unsetPermission(group.getProtection(), target, pt);
						}
					} else {
						ProtectionManager.setPermission(group.getProtection(), target, pt, true);
					}
					((GroupMenu)MenuManager.getMenus().get(player)).update();
				}
			}
		,true);
		return permissionButton;
	}

	@Override
	public Button getIconButton() {
		MenuPage page = this;
		Button permissionsButton = new Button(player, Material.PAPER, ChatColor.WHITE + target.getName() + " permissions for " + group.getName(), null,
				new BukkitRunnable(){
					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(page);
					}
			
		}, ProtectionManager.hasPermission(group.getProtection(), PermissionType.MANAGE, player, false));
		return permissionsButton;
	}

	@Override
	public String getTitle() {
		
		return "" + ChatColor.BOLD + ChatColor.GOLD + target.getName() + " Permissions";
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

}
