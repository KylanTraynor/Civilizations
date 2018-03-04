package com.kylantraynor.civilizations.menus.pages;

import java.util.*;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.utils.Identifier;
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
import com.kylantraynor.civilizations.utils.Utils;

public class GroupPermissionsPage implements MenuPage {
	
	private Player player;
	private Group group;
	private UUID target;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	
	public GroupPermissionsPage(Player player, Group group, UUID target){
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
		List<String> lore = new ArrayList<String>();
		
		lore.add(ChatColor.GRAY + pt.getDescription());

		Boolean value = group.getSettings().getPermission(target, pt.toString());
		Boolean inheritedValue = ProtectionManager.hasPermission(pt, group, group, true);
		Material mat = value != null ? (value ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK) : Material.IRON_BLOCK;
		if(value != null){
			lore.add(ChatColor.WHITE + "Value: " + ChatColor.GOLD + value);
		} else {
			lore.add(ChatColor.WHITE + "Not set" + ChatColor.GRAY + " (Will inherit parent's value : " + ChatColor.GOLD + inheritedValue + ChatColor.GRAY + ")");
		}
		
		final boolean set = value != null;
		final boolean val = value;
		Button permissionButton = new Button(player,mat, ChatColor.WHITE + Utils.prettifyText(pt.toString()), lore,
			new BukkitRunnable(){
				@Override
				public void run() {
					if(set){
						if(val){
							ProtectionManager.setPermission(pt, group, target, false);
						} else {
							ProtectionManager.setPermission(pt, group, target, null);
						}
					} else {
						ProtectionManager.setPermission(pt, group, target, true);
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
		Button permissionsButton = new Button(player, Material.PAPER, ChatColor.WHITE + EconomicEntity.get(target).getName() + " permissions for " + group.getName(), null,
				new BukkitRunnable(){
					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(page);
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE, group, player, true));
		return permissionsButton;
	}

	@Override
	public String getTitle() {
		
		return "" + ChatColor.BOLD + ChatColor.GOLD + EconomicEntity.get(target).getName() + " Permissions";
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}
}
