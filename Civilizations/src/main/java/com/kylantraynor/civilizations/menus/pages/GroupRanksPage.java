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
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;

public class GroupRanksPage implements MenuPage{

	private Player player;
	private Group group;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	private Map<Rank, MenuPage> rankPages = new HashMap<Rank, MenuPage>();
	
	public GroupRanksPage(Player player, Group group){
		this.player = player;
		this.group = group;
		for(Rank r : group.getProtection().getRanks()){
			rankPages.put(r, new GroupRankPage(player, group, r));
		}
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void refresh(Menu menu) {
		if(!(menu instanceof GroupMenu)) return;
		GroupMenu gMenu = (GroupMenu) menu;
		buttons.clear();
		if(gMenu.getBottomInventory().getSize() / 9 <= getRows()){
			return;
		}
		int i = gMenu.pos(0, 1);
		for(Rank r : group.getProtection().getRanks()){
			buttons.put(i, rankPages.get(r).getIconButton());
			i++;
		}
	}

	@Override
	public Button getIconButton() {
		MenuPage page = this;
		Button ranksButton = new Button(player, Material.EMERALD_BLOCK, "Manage " + group.getType() + " ranks", null,
				new BukkitRunnable(){
					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(page);
					}
			
		}, true);
		return ranksButton;
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.BOLD + ChatColor.GOLD + "Manage " + group.getName() + " Ranks";
	}

}
