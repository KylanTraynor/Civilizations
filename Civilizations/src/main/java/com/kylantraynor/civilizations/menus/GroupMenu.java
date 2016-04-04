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

public class GroupMenu extends InventoryView{
	
	public enum Page{
		MAIN, //Page with the general info of the group.
		MANAGE, //Page with all the stuff required to edit the basic stuff.
		RANKS, //Page with the tools to edit ranks.
	}
	
	private Group group;
	private Inventory top;
	private Inventory bottom;
	private Player player;
	private int linesTop = 1; // Navigation Bar
	private int linesBottom = 5; // Menus
	
	private Material validButton = Material.EMERALD_BLOCK;
	private Material permissionLackButton = Material.REDSTONE_BLOCK;
	private Material navigationValid = Material.ARROW;
	private Material navigationInvalid = Material.BARRIER;
	private Page currentPage;
	
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
	
	public void update(){
		this.bottom.clear();
		this.top.clear();
		switch(currentPage){
		case MAIN:
			updateForMain();
		case MANAGE:
			updateForManage();
		case RANKS:
			updateForRanks();
		}
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
	private void updateForRanks() {
		//Get the Buttons.
		ItemStack mainButton = getMainButton();
		ItemStack manageButton = getManageButton();
		ItemStack ranksButton = getRanksButton();
		// Draw the icons and buttons in the inventory.
		top.setItem(pos(0,0), mainButton);
		top.setItem(pos(3,0), manageButton);
		top.setItem(pos(4,0), ranksButton);
	}
	
	public ItemStack getMainButton(){
		final GroupMenu self = this;
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Type: " + ChatColor.GOLD + group.getType());
		lore.add(ChatColor.WHITE + "Members: " + ChatColor.GOLD + group.getMembers().size());
		Button mainButton = new Button(Material.GOLD_BLOCK, group.getChatHeader(), lore, new BukkitRunnable(){

			@Override
			public void run() {
				self.currentPage = Page.MAIN;
				self.update();
			}
			
		}, true);
		return mainButton;
	}
	
	public Button getManageButton(){
		final GroupMenu self = this;
		Button manageButton = new Button(validButton, "Manage " + group.getType(), null,
				new BukkitRunnable(){

					@Override
					public void run() {
						self.currentPage = Page.MANAGE;
						self.update();
					}
			
		}, group.hasPermission(PermissionType.MANAGE, null, player));
		return manageButton;
	}
	
	public Button getRanksButton(){
		final GroupMenu self = this;
		Button manageButton = new Button(validButton, "Ranks", null,
				new BukkitRunnable(){

					@Override
					public void run() {
						self.currentPage = Page.RANKS;
						self.update();
					}
			
		}, group.hasPermission(PermissionType.MANAGE_RANKS, null, player));
		return manageButton;
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
		player.openInventory(this);;
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
