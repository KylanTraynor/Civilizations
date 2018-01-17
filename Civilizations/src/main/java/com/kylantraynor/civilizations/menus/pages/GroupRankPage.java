package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;

public class GroupRankPage implements MenuPage{

	private Player player;
	private Group group;
	private Rank rank;
	private GroupPermissionsPage permsPage;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	
	public GroupRankPage(Player player, Group group, Rank r) {
		this.player = player;
		this.group = group;
		this.rank = r;
		permsPage = new GroupPermissionsPage(player, group, r);
	}

	@Override
	public int getRows() {
		return 2;
	}

	@Override
	public void refresh(Menu menu) {
		buttons.put(4, permsPage.getIconButton());
	}

	@Override
	public Button getIconButton() {
		List<String> lore = new ArrayList<String>();
		String parentName = "None";
		int count = rank.getPlayers().size();
		if(rank.getParentId() != null){
			parentName = group.getProtection().getRank(rank.getParentId()).getName();
		}
		lore.add(ChatColor.WHITE + "Parent: " + ChatColor.GOLD + parentName);
		lore.add(ChatColor.WHITE + "Members: ");
		for(OfflinePlayer p : rank.getPlayers()){
			lore.add(ChatColor.GOLD + "    " + p.getName());
		}
		MenuPage page = this;
		Button rankButton = new Button(player, Material.GOLD_BLOCK, rank.getName(), lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(page);
					}
			
		}, ProtectionManager.hasPermission(group.getProtection(), PermissionType.MANAGE_RANKS, player, false));
		rankButton.setAmount(count);
		return rankButton;
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.BOLD + ChatColor.GOLD + rank.getName();
	}

}
