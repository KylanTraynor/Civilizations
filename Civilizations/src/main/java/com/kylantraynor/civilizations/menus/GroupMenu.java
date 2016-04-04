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
	private Material invalidButton = Material.IRON_BLOCK;
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
		//Draw the info icon with the basic info of the group.
		ItemStack infoIcon = getInfoIcon();
		ItemStack manageButton = getManageButton();
		// Draw the icons and buttons in the inventory.
		bottom.setItem(pos(4,0), infoIcon);
		bottom.setItem(pos(4,1), manageButton);
	}
	private void updateForManage() {
		
	}
	private void updateForRanks() {
		
	}
	
	public ItemStack getInfoIcon(){
		ItemStack infoIcon = new ItemStack(Material.GOLD_BLOCK);
		ItemMeta im = infoIcon.getItemMeta();
		im.setDisplayName(group.getChatHeader());
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "Type: " + ChatColor.GOLD + group.getType());
		lore.add(ChatColor.WHITE + "Members: " + ChatColor.GOLD + group.getMembers().size());
		im.setLore(lore);
		infoIcon.setItemMeta(im);
		return infoIcon;
	}
	
	public ItemStack getManageButton(){
		ItemStack manageButton = null;
		if(group.hasPermission(PermissionType.MANAGE, null, player)){
			manageButton = new ItemStack(validButton);
		} else {
			manageButton = new ItemStack(invalidButton);
		}
		ItemMeta im = manageButton.getItemMeta();
		im.setDisplayName("Manage " + group.getType());
		manageButton.setItemMeta(im);
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
