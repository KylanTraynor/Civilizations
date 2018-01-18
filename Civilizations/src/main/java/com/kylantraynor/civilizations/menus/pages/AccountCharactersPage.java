package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.menus.AccountMenu;
import com.kylantraynor.civilizations.menus.Button;
import com.kylantraynor.civilizations.menus.GroupMenu;
import com.kylantraynor.civilizations.menus.Menu;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.players.CivilizationsCharacter;

public class AccountCharactersPage implements MenuPage {

	private Player player;
	private CivilizationsAccount account;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();
	
	public AccountCharactersPage(Player player, CivilizationsAccount account) {
		this.player = player;
		this.account = account;
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void refresh(Menu menu) {
		if(!(menu instanceof AccountMenu)) return;
		AccountMenu aMenu = (AccountMenu) menu;
		buttons.clear();
		if(aMenu.getTopInventory().getSize() / 9 <= getRows()){
			return;
		}
		UUID[] ids = account.getCharacterIds();
		buttons.put(aMenu.pos(0, 0), getNewCharacterButton());
		for(int i = 0; i < ids.length; i++){
			buttons.put(aMenu.pos((i + 1) % 9, Math.floorDiv((i + 1),9)), getCharacterButton(ids[i]));
		}
	}

	@Override
	public Button getIconButton() {
		List<String> lore = new ArrayList<String>();
		CivilizationsCharacter current = account.getCurrentCharacter();
		if(current != null){
			lore.add(ChatColor.WHITE + "Current: " + ChatColor.GOLD + current.getName() + " " +current.getFamilyName());
		} else {
			lore.add(ChatColor.WHITE + "Current: " + ChatColor.GRAY + "No character selected");
		}
		MenuPage page = this;
		Button mainButton = new Button(player, Material.GOLD_BLOCK, "Change Character", lore, new BukkitRunnable(){

			@Override
			public void run() {
				((AccountMenu)MenuManager.getMenus().get(player)).changePage(page);
			}
			
		}, true);
		return mainButton;
	}
	
	public Button getNewCharacterButton() {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD + "Clicking will save the inventory and location of your");
		lore.add(ChatColor.GOLD + "current character, then send you to the starting");
		lore.add(ChatColor.GOLD + "location of new characters.");
		Button button = new Button(player, Material.GOLD_BLOCK, "Change Character", lore, new BukkitRunnable(){

			@Override
			public void run() {
				CivilizationsCharacter c = account.createNewCharacter();
				account.setCurrentCharacter(c);
				((AccountMenu)MenuManager.getMenus().get(player)).close();
			}
			
		}, true);
		return button;
	}
	
	public Button getCharacterButton(UUID id){
		CivilizationsCharacter cc = account.getCharacter(id);
		if(cc == null) return null;
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Name: " + ChatColor.GOLD + cc.getName());
		lore.add(ChatColor.WHITE + "Family: " + ChatColor.GOLD + cc.getFamilyName());
		lore.add(ChatColor.WHITE + "Gender: " + ChatColor.GOLD + cc.getGender().toString());
		lore.add(ChatColor.GOLD + "Clicking will save the inventory and location of your");
		lore.add(ChatColor.GOLD + "current character, then send you to where this");
		lore.add(ChatColor.GOLD + "character was, and restore its own inventory.");
		Button button = new Button(player, Material.EMERALD_BLOCK, "Change Character", lore, new BukkitRunnable(){

			@Override
			public void run() {
				account.setCurrentCharacter(cc);
				((AccountMenu)MenuManager.getMenus().get(player)).close();
			}
			
		}, true);
		return button;
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.WHITE + ChatColor.BOLD + "Change Character";
	}

}
