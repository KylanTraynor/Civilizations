package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.protection.Rank;

public class GroupRanksPage implements MenuPage{

	private Player player;
	private Group group;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	private Map<Rank, MenuPage> rankPages = new HashMap<Rank, MenuPage>();
	TreeSet<Rank> ranks = new TreeSet<Rank>(getRankComparator());
	
	public GroupRanksPage(Player player, Group group){
		this.player = player;
		this.group = group;
		for(Rank r : group.getProtection().getRanks()){
			if(ranks.contains(r)) continue;
			ranks.add(r);
		}
		for(Rank r : ranks){
			rankPages.put(r, new GroupRankPage(player, group, r));
		}
	}

	@Override
	public int getRows() {
		return (int) (Math.ceil(group.getProtection().getRanks().size() / 9.0) + 1);
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
		List<String> lore = new ArrayList<String>();
		lore.add("Ranks: ");
		for(Rank r : ranks){
			lore.add("    " + r.getName());
		}
		MenuPage page = this;
		Button ranksButton = new Button(player, Material.EMERALD_BLOCK, "Manage " + group.getType() + " ranks", lore,
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
	
	public Comparator<Rank> getRankComparator(){
		return (a, b) -> { // Rank from lowest to highest
			if(a.getLevel() < b.getLevel()) return -1;
			if(a.getLevel() > b.getLevel()) return 1;
			return 0;
		};
	}

}
