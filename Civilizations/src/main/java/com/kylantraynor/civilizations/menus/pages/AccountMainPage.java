package com.kylantraynor.civilizations.menus.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class AccountMainPage implements MenuPage{

	private Player player;
	private CivilizationsAccount account;
	
	private MenuPage charactersPage;
	
	private Map<Integer, Button> buttons = new HashMap<Integer, Button>();

	public AccountMainPage(Player player, CivilizationsAccount account){
		this.player = player;
		this.account = account;
		charactersPage = new AccountCharactersPage(player, account);
	}
	
	@Override
	public int getRows() {
		return 2;
	}

	@Override
	public void refresh(Menu menu) {
		if(!(menu instanceof AccountMenu)) return;
		AccountMenu aMenu = (AccountMenu) menu;
		buttons.put(aMenu.pos(4, 0), charactersPage.getIconButton());
	}

	@Override
	public Button getIconButton() {
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Id: " + ChatColor.GOLD + account.getPlayerId());
		lore.add(ChatColor.WHITE + "Characters: " + ChatColor.GOLD + account.getCharacterIds().length);
		MenuPage page = this;
		Button mainButton = new Button(player, Material.GOLD_BLOCK, account.getOfflinePlayer().getName(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				((AccountMenu)MenuManager.getMenus().get(player)).changePage(page);
			}
			
		}, true);
		return mainButton;
	}

	@Override
	public Map<Integer, Button> getButtons() {
		return buttons;
	}

	@Override
	public String getTitle() {
		return "" + ChatColor.WHITE + ChatColor.BOLD + account.getOfflinePlayer().getName() + "'s Account";
	}

}
