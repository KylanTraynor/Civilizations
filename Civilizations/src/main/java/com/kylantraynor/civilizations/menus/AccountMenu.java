package com.kylantraynor.civilizations.menus;

import java.util.Stack;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.ButtonManager;
import com.kylantraynor.civilizations.menus.pages.MenuPage;
import com.kylantraynor.civilizations.players.CivilizationsAccount;

public class AccountMenu extends Menu{
	
	public enum Page{
		MAIN,
		CHARACTERS
	}

	private Inventory top;
	private final int linesTop = 6;
	private final CivilizationsAccount account;
	private MenuPage currentPage;
	private Stack<MenuPage> pageStack = new Stack<MenuPage>();

	public AccountMenu(CivilizationsAccount account) {
		this.account = account;
		initInventories();
	}
	
	/**
	 * Initialize the Menu Inventories.
	 */
	private void initInventories() {
		this.top = Bukkit.createInventory(null, 9 * linesTop , ChatColor.BOLD + this.account.getOfflinePlayer().getName());
		
		this.top.setMaxStackSize(1);
	}
	
	/**
	 * Gets the index of a slot depending on its column and line.
	 * @param column
	 * @param line
	 * @return
	 */
	public int pos(int column, int line){
		return column + line * 9;
	}

	@Override
	public void update() {
		ButtonManager.clearButtons(getPlayer());
		//this.bottom.clear();
		this.top.clear();
		currentPage.refresh(this);
		// Draw Page Content
		for(Entry<Integer, Button> e : currentPage.getButtons().entrySet()){
			this.top.setItem(e.getKey() + 18, e.getValue());
		}
		// Draw Navigation Bar Border
		for(int i = 9 ; i < 18; i++){
			this.top.setItem(i, new ItemStack(Material.THIN_GLASS));
		}
		// Draw Navigation Bar
		if(pageStack.size() > 0){
			this.top.setItem(pos(0,0), pageStack.peek().getIconButton());
		}
		this.top.setItem(4, currentPage.getIconButton());
		
		getPlayer().updateInventory();
	}
	
	public void changePage(MenuPage page) {
		if(pageStack.size() > 0){
			if(page != pageStack.peek()){
				pageStack.add(currentPage);
			} else {
				pageStack.pop();
			}
		} else {
			pageStack.add(currentPage);
		}
		Civilizations.DEBUG("Going from menu page \"" + currentPage.getTitle() + "\" to page \"" + page.getTitle() + "\".");
		currentPage = page;
		update();
	}

	@Override
	public Inventory getBottomInventory() {
		return getPlayer().getInventory();
	}

	@Override
	public Inventory getTopInventory() {
		return this.top;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

}
