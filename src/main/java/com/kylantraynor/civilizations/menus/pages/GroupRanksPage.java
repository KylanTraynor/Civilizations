package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.kylantraynor.civilizations.protection.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;

public class GroupRanksPage implements MenuPage{

	private Player player;
	private Group group;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	private Map<Group, MenuPage> subPages = new HashMap<>();
	TreeSet<Group> groups = new TreeSet<>();
	
	public GroupRanksPage(Player player, Group group){
		this.player = player;
		this.group = group;
		for(Permissions p : group.getSettings().getPermissions()){
			Group g = Group.get(p.getTarget());
			if(g == null) continue;
			if(groups.contains(g)) continue;
			groups.add(g);
		}
		for(Group g : groups){
			subPages.put(g, new GroupRankPage(player, group, g));
		}
	}

	@Override
	public int getRows() {
		return (int) (Math.ceil(group.getSettings().getPermissions().length / 9.0) + 1);
	}

	@Override
	public void refresh(Menu menu) {
		if(!(menu instanceof GroupMenu)) return;
		GroupMenu gMenu = (GroupMenu) menu;
		buttons.clear();
		/*if(gMenu.getBottomInventory().getSize() / 9 <= getRows()){
			return;
		}*/
		for(Permissions p : group.getSettings().getPermissions()){
			Group g = Group.get(p.getTarget());
			if(g == null) continue;
			if(groups.contains(g)) continue;
			groups.add(g);
		}
		int i = gMenu.pos(0, 0);
		for(Group g : groups){
			MenuPage rp = subPages.get(g);
			if(rp == null){
				rp = new GroupRankPage(player, group, g);
				subPages.put(g, rp);
			}
			buttons.put(i, rp.getIconButton());
			i++;
		}
	}

	@Override
	public Button getIconButton() {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Groups: ");
		for(Group g : groups){
			lore.add(ChatColor.GOLD + "    " + g.getName());
		}
		MenuPage page = this;
		Button ranksButton = new Button(player, Material.EMERALD_BLOCK, "Manage " + group.getType() + " groups", lore,
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
		return "" + ChatColor.BOLD + ChatColor.GOLD + "Manage " + group.getName() + " Groups";
	}

}
