package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.UUID;

import com.kylantraynor.civilizations.protection.Permissions;
import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.kylantraynor.civilizations.managers.ButtonManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.managers.ProtectionManager;
import com.kylantraynor.civilizations.menus.pages.GroupMainPage;
import com.kylantraynor.civilizations.menus.pages.MenuPage;
import com.kylantraynor.civilizations.protection.PermissionType;

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
	private int linesTop = 6; // Navigation Bar
	private int linesBottom = 6; // Menus
	
	private Stack<MenuPage> pageStack = new Stack<MenuPage>();
	
	private Material validButton = Material.EMERALD_BLOCK;
	//private Material permissionLackButton = Material.REDSTONE_BLOCK;
	//private Material navigationValid = Material.ARROW;
	//private Material navigationInvalid = Material.BARRIER;
	private MenuPage currentPage;
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
		//this.bottom = player.getInventory();//Bukkit.createInventory(null, 9 * linesBottom, this.group.getName());
		
		this.top.setMaxStackSize(1);
		//this.bottom.setMaxStackSize(1);
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
	/*
	public void changePage(Page p){
		currentPage = p;
		update();
	}
	*/
	/**
	 * Updates the Menu depending on the active page.
	 */
	@Override
	public void update(){
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
			this.top.setItem(i, new ItemStack(Material.GLASS_PANE));
		}
		// Draw Navigation Bar
		if(pageStack.size() > 0){
			this.top.setItem(pos(0,0), pageStack.peek().getIconButton());
		}
		this.top.setItem(4, currentPage.getIconButton());
		
		/*
		switch(currentPage)
		{
		case MAIN: updateForMain(); break;
		case MANAGE: updateForManage(); break;
		case RANK: updateForRank(group.getProtection().getRank(currentSubPage)); break;
		case RANKS_SELECTION: updateForRankSelection(); break;
		}
		*/
		getPlayer().updateInventory();
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
	private void updateForRank(Group rank) {
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
		for(Permissions p : group.getSettings().getPermissions()){
		    Group g = Group.get(p.getTarget());
		    if(g == null) continue;
			Button rankButton = getRankButton(g);
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
		Button mainButton = new Button(getPlayer(), Material.GOLD_BLOCK, group.getChatHeader(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				//((GroupMenu)MenuManager.getMenus().get(player)).changePage(Page.MAIN);
			}
			
		}, true);
		return mainButton;
	}
	/**
	 * Gets the button linking to the Group Management Screen.
	 * @return
	 */
	public Button getManageButton(){
		Button manageButton = new Button(getPlayer(), validButton, "Manage " + group.getType(), null,
				new BukkitRunnable(){

					@Override
					public void run() {
						//((GroupMenu)MenuManager.getMenus().get(player)).changePage(Page.MANAGE);
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE, group, getPlayer(), true).getResult());
		return manageButton;
	}
	/**
	 * Get the button linking to the Rank Selection screen.
	 * @return
	 */
	public Button getRanksButton(){
		Button manageButton = new Button(getPlayer(), validButton, "Ranks", null,
				new BukkitRunnable(){

					@Override
					public void run() {
						//((GroupMenu)MenuManager.getMenus().get(player)).startSelection(Page.RANKS_SELECTION, "RANK_SELECTION");
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE_RANKS, group, getPlayer(), true).getResult());
		return manageButton;
	}
	/**
	 * Gets the button allowing to add a new rank.
	 * @return
	 */
	public Button getNewRankButton(){
		List<String> lore = new ArrayList<String>();
		lore.add("Create a new rank");
		Button button = new Button(getPlayer(), validButton, "Ranks", lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(getPlayer())).initTextInput("RANK_NEW", null);
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE_RANKS, group, getPlayer(), true).getResult());
		return button;
	}
	/**
	 * Gets the Change Name button linking to the name change screen.
	 * @param rank
	 * @return
	 */
	public Button getChangeRankNameButton(final Group rank){
		List<String> lore = new ArrayList<String>();
		lore.add("Changes the name of this rank");
		Button nameButton = new Button(getPlayer(), validButton, "Change Name", lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(getPlayer())).initTextInput("RANK_NAMING", rank.getName());
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE_RANKS, group, getPlayer(), true).getResult());
		return nameButton;
	}
	/**
	 * Gets the Rank Parent Button linking to the Rank Selection Screen.
	 * @param rank
	 * @return
	 */
	public Button getParentButton(Group rank){
		List<String> lore = new ArrayList<String>();
		/*if(rank.getParentId() != null){
			lore.add("Current: " + group.getProtection().getRank(rank.getParentId()).getName());
		} else {
			lore.add("None");
		}*/
		Button parentButton = new Button(getPlayer(), validButton, "Parent Rank", lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						//((GroupMenu)MenuManager.getMenus().get(player)).startSelection(Page.RANKS_SELECTION, "PARENT_RANK_SELECTION");
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE_RANKS, group, getPlayer(), true).getResult());
		return parentButton;
	}
	/**
	 * Starts a Selection on the given page with the given goal.
	 * @param page
	 * @param string
	 */
	protected void startSelection(MenuPage page, String string) {
		this.currentGoal = string;
		changePage(page);
	}
	/**
	 * Get a button for the given Rank linking to the Management screen of this rank.
	 * @param r
	 * @return
	 */
	private Button getRankButton(final Group r) {
		List<String> lore = new ArrayList<String>();
		//String parentName = "None";
		/*if(r.getParentId() != null){
			parentName = group.getProtection().getRank(r.getParentId()).getName();
		}
		lore.add("Parent: " + parentName);*/
		Button rankButton = new Button(getPlayer(), Material.GOLD_BLOCK, r.getName(), lore,
				new BukkitRunnable(){

					@Override
					public void run() {
						((GroupMenu)MenuManager.getMenus().get(getPlayer())).selectionReturned(r.getName());
					}
			
		}, ProtectionManager.hasPermission(PermissionType.MANAGE_RANKS, group, getPlayer(), true).getResult());
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
			//changePage(Page.RANK);
			break;
		case "PARENT_RANK_SELECTION":
			//group.getProtection().getRank(currentSubPage).setParent(group.getProtection().getRank(name));
			currentGoal = null;
			//changePage(Page.RANK);
			break;
		}
	}

	/**
	 * Opens this view to the given player at the given page.
	 * @param player
	 * @param p
	 */
	public void open(Player player, MenuPage p){
		if(p == null){
			p = new GroupMainPage(player, this.group);
		}
		this.currentPage = p;
		this.open(player);
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
			Group.get((UUID) argument).setName(result);
			currentSubPage = result;
			//changePage(Page.RANK);
			break;
		case "RANK_NEW":
			//group.getProtection().addRank(new Rank(result, (Rank)null));
			//changePage(Page.RANKS_SELECTION);
			break;
		}
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
	
	public Group getGroup() {
		return group;
	}

}
