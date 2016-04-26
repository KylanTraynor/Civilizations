package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.protection.PermissionType;
import com.kylantraynor.civilizations.protection.Rank;

public class GroupMenu extends Menu{
	
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
	//private Material permissionLackButton = Material.REDSTONE_BLOCK;
	//private Material navigationValid = Material.ARROW;
	//private Material navigationInvalid = Material.BARRIER;
	private Page currentPage;
	private String currentGoal;
	private String currentSubPage;
	
	public GroupMenu(Group g){
		this.group = g;
		initInventories();
	}
	/**
	 * Initialize the Menu Inventories.
	 */
	private void initInventories() {
		this.top = Bukkit.createInventory(null, 9 * linesTop, ChatColor.BOLD + this.group.getName());
		this.bottom = Bukkit.createInventory(null, 9 * linesBottom, this.group.getName());
		
		this.top.setMaxStackSize(1);
		this.bottom.setMaxStackSize(1);
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
	/**
	 * Changes the page and updates the menu.
	 * @param p
	 */
	public void changePage(Page p){
		currentPage = p;
		update();
	}
	/**
	 * Updates the Menu depending on the active page.
	 */
	@Override
	public void update(){
		this.bottom.clear();
		this.top.clear();
		switch(currentPage)
		{
		case MAIN: updateForMain(); break;
		case MANAGE: updateForManage(); break;
		case RANK: updateForRank(group.getProtection().getRank(currentSubPage)); break;
		case RANKS_SELECTION: updateForRankSelection(); break;
		}
		player.closeInventory();
		player.openInventory(this);
	}
	/**
	 * Update the menu to display the Main Screen.
	 */
	private void updateForMain() {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(4,0), mainButton);
		bottom.setItem(pos(4,0), manageButton);
	}
	/**
	 * Update the menu to display the Group Management Screen.
	 */
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
	/**
	 * Update the menu to display the Mangement screen of the given Rank.
	 * @param rank
	 */
	private void updateForRank(Rank rank) {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		ItemStack rankButton = getRanksButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(0,0), mainButton);
		top.setItem(pos(3,0), manageButton);
		top.setItem(pos(4,0), rankButton);
		
		Button nameButton = getChangeRankNameButton(rank);
		Button parentButton = getParentButton(rank);
		bottom.setItem(pos(0, 1), nameButton);
		bottom.setItem(pos(8, 1), parentButton);
	}
	/**
	 * Updates the menu to display the Rank selection screen.
	 */
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
		Button newRankButton = getNewRankButton();
		bottom.setItem(pos(8,0), newRankButton);
		List<Button> buttons = new ArrayList<Button>();
		for(Rank r : group.getProtection().getRanks()){
			Button rankButton = getRankButton(r);
			buttons.add(rankButton);
		}
		int i = 9;
		for(Button btn : buttons){
			bottom.setItem(i, btn);
			i++;
		}
	}
	/**
	 * Gets the Button linking to the Main Screen.
	 * @return
	 */
	public Button getMainButton(){
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Type: " + ChatColor.GOLD + group.getType());
		lore.add(ChatColor.WHITE + "Members: " + ChatColor.GOLD + group.getMembers().size());
		Button mainButton = new Button(player, Material.GOLD_BLOCK, group.getChatHeader(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				((GroupMenu)MenuManager.getMenus().get(player)).changePage(Page.MAIN);
			}
			
		}, true);
		return mainButton;
	}
	/**
	 * Gets the button linking to the Group Management Screen.
	 * @return
	 */
	public Button getManageButton(){
		Button manageButton = new Button(player, validButton, "Manage " + group.getType(), null,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).changePage(Page.MANAGE);
					}
			
		}, group.hasPermission(PermissionType.MANAGE, null, player));
		return manageButton;
	}
	/**
	 * Get the button linking to the Rank Selection screen.
	 * @return
	 */
	public Button getRanksButton(){
		Button manageButton = new Button(player, validButton, "Ranks", null,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).startSelection(Page.RANKS_SELECTION, "RANK_SELECTION");
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return manageButton;
	}
	/**
	 * Gets the button allowing to add a new rank.
	 * @return
	 */
	public Button getNewRankButton(){
		List<String> lore = new ArrayList<String>();
		lore.add("Create a new rank");
		Button button = new Button(player, validButton, "Ranks", lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).initTextInput("RANK_NEW", null);
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return button;
	}
	/**
	 * Gets the Change Name button linking to the name change screen.
	 * @param rank
	 * @return
	 */
	public Button getChangeRankNameButton(final Rank rank){
		List<String> lore = new ArrayList<String>();
		lore.add("Changes the name of this rank");
		Button nameButton = new Button(player, validButton, "Change Name", lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).initTextInput("RANK_NAMING", rank.getName());
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return nameButton;
	}
	/**
	 * Gets the Rank Parent Button linking to the Rank Selection Screen.
	 * @param rank
	 * @return
	 */
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
						((GroupMenu)MenuManager.getMenus().get(player)).startSelection(Page.RANKS_SELECTION, "PARENT_RANK_SELECTION");
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return parentButton;
	}
	/**
	 * Starts a Selection on the given page with the given goal.
	 * @param page
	 * @param string
	 */
	protected void startSelection(Page page, String string) {
		this.currentGoal = string;
		changePage(page);
	}
	/**
	 * Get a button for the given Rank linking to the Management screen of this rank.
	 * @param r
	 * @return
	 */
	private Button getRankButton(final Rank r) {
		List<String> lore = new ArrayList<String>();
		String parentName = "None";
		if(r.getParent() != null){
			parentName = r.getParent().getName();
		}
		lore.add("Parent: " + parentName);
		Button rankButton = new Button(player, Material.GOLD_BLOCK, r.getName(), lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(player)).selectionReturned(r.getName());
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return rankButton;
	}
	/**
	 * Sends the returned value of a selection back to the menu.
	 * @param name
	 */
	protected void selectionReturned(String name) {
		switch(currentGoal.toUpperCase()){
		case "RANK_SELECTION":
			this.currentSubPage = name;
			currentGoal = null;
			changePage(Page.RANK);
			break;
		case "PARENT_RANK_SELECTION":
			group.getProtection().getRank(currentSubPage).setParent(group.getProtection().getRank(name));
			currentGoal = null;
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
	public Player getPlayer() {
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
	public void initTextInput(String goal, String argument){
		getPlayer().closeInventory();
		ConversationFactory cf = new ConversationFactory(Civilizations.currentInstance);
		Conversation c = cf.withFirstPrompt(new GetInputStringPrompt(this, goal, argument)).withLocalEcho(false)
				.withEscapeSequence("CANCEL").buildConversation(getPlayer());
		c.begin();
	}
	public void textInputResult(String result, String reason, Object argument) {
		switch(reason.toUpperCase()){
		case "RANK_NAMING":
			group.getProtection().getRank((String) argument).setName(result);
			currentSubPage = result;
			changePage(Page.RANK);
			break;
		case "RANK_NEW":
			group.getProtection().addRank(new Rank(result, null));
			changePage(Page.RANKS_SELECTION);
			break;
		}
	}
}
