package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;

public class GroupMenu extends InventoryView{
	
	public enum Page{
		MAIN, //Page with the general info of the group.
		MANAGE, //Page with all the stuff required to edit the basic stuff.
		RANK, //Page with the tools to edit ranks.
		RANKS_SELECTION //Page where the user needs to select a rank of create a new one.
	}
	
	private Group group;
	private Inventory top;
	private Inventory bottom;
	private Player player;
	private int linesTop = 1; // Navigation Bar
	private int linesBottom = 4; // Menus
	
	private Material validButton = Material.EMERALD_BLOCK;
	private Material permissionLackButton = Material.REDSTONE_BLOCK;
	private Material navigationValid = Material.ARROW;
	private Material navigationInvalid = Material.BARRIER;
	private Page currentPage;
	private String currentGoal;
	private String currentSubPage;
	
	public GroupMenu(Group g){
		this.group = g;
		initInventories();
	}
	
	private void initInventories() {
		this.top = Bukkit.createInventory(null, 9 * linesTop, this.group.getName());
		this.bottom = Bukkit.createInventory(null, 9 * linesBottom, this.group.getName());
		
		this.top.setMaxStackSize(1);
		this.bottom.setMaxStackSize(1);
	}
	
	public int pos(int column, int line){
		return column + line * 9;
	}
	
	public void changePage(Page p){
		currentPage = p;
		update();
	}
	
	public void update(){
		this.bottom.clear();
		this.top.clear();
		switch(currentPage){
		case MAIN:
			updateForMain();
			break;
		case MANAGE:
			updateForManage();
			break;
		case RANK:
			updateForRank(group.getProtection().getRank(currentSubPage));
			break;
		case RANKS_SELECTION:
			updateForRankSelection();
			break;
		}
		player.closeInventory();
		player.openInventory(this);
	}

	private void updateForMain() {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(4,0), mainButton);
		bottom.setItem(pos(4,0), manageButton);
	}
	private void updateForManage() {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		ItemStack ranksButton = getRanksButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(0,0), mainButton);
		top.setItem(pos(4,0), manageButton);
		bottom.setItem(pos(4,0), ranksButton);
	}
	private void updateForRank(Rank rank) {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		ItemStack rankButton = getRanksButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(0,0), mainButton);
		top.setItem(pos(3,0), manageButton);
		top.setItem(pos(4,0), rankButton);
		
		Button parentButton = getParentButton(rank);
		bottom.setItem(pos(8, 1), parentButton);
	}
	
	private void updateForRankSelection() {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		ItemStack ranksButton = getRanksButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(0,0), mainButton);
		top.setItem(pos(3,0), manageButton);
		top.setItem(pos(4,0), ranksButton);
		//Get the buttons.
		List<Button> buttons = new ArrayList<Button>();
		for(Rank r : group.getProtection().getRanks()){
			Button rankButton = getRankButton(r);
			buttons.add(rankButton);
		}
		int i = 9;
		for(Button btn : buttons){
			bottom.setItem(i, btn);
		}
	}

	public Button getMainButton(){
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Type: " + ChatColor.GOLD + group.getType());
		lore.add(ChatColor.WHITE + "Members: " + ChatColor.GOLD + group.getMembers().size());
		Button mainButton = new Button(player, Material.GOLD_BLOCK, group.getChatHeader(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				MenuManager.getMenus().get(player).changePage(Page.MAIN);
			}
			
		}, true);
		return mainButton;
	}
	
	public Button getManageButton(){
		Button manageButton = new Button(player, validButton, "Manage " + group.getType(), null,
				new BukkitRunnable(){

					@Override
					public void run() {
						MenuManager.getMenus().get(player).changePage(Page.MANAGE);
					}
			
		}, group.hasPermission(PermissionType.MANAGE, null, player));
		return manageButton;
	}
	
	public Button getRanksButton(){
		Button manageButton = new Button(player, validButton, "Ranks", null,
				new BukkitRunnable(){

					@Override
					public void run() {
						MenuManager.getMenus().get(player).startSelection(Page.RANKS_SELECTION, "RANK_SELECTION");
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return manageButton;
	}
	
	public Button getParentButton(Rank rank){
		List<String> lore = new ArrayList<String>();
		if(rank.getParent() != null){
			lore.add("Current: " + rank.getParent().getName());
		} else {
			lore.add("None");
		}
		Button parentButton = new Button(player, validButton, "Parent Rank", lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						MenuManager.getMenus().get(player).startSelection(Page.RANKS_SELECTION, "PARENT_RANK_SELECTION");
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return parentButton;
	}
	
	protected void startSelection(Page page, String string) {
		this.currentGoal = string;
		changePage(page);
		this.currentGoal = null;
	}

	private Button getRankButton(final Rank r) {
		List<String> lore = new ArrayList<String>();
		Button rankButton = new Button(player, Material.GOLD_BLOCK, r.getName(), lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						MenuManager.getMenus().get(player).selectionReturned(r.getName());
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return rankButton;
	}

	protected void selectionReturned(String name) {
		switch(currentGoal.toUpperCase()){
		case "RANK_SELECTION":
			this.currentSubPage = name;
			changePage(Page.RANK);
			break;
		case "PARENT_RANK_SELECTION":
			group.getProtection().getRank(currentSubPage).setParent(group.getProtection().getRank(name));
			changePage(Page.RANK);
			break;
		}
	}

	/**
	 * Opens this view to the given player at the given page.
	 * @param player
	 * @param p
	 */
	public void open(Player player, Page p){
		if(p == null){
			p = Page.MAIN;
		}
		this.currentPage = p;
		this.player = player;
		update();
	}

	@Override
	public Inventory getBottomInventory() {
		return this.bottom;
	}

	@Override
	public HumanEntity getPlayer() {
		return this.player;
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
