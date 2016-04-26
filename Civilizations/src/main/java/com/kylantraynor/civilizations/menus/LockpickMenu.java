package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.menus.GroupMenu.Page;
import com.kylantraynor.civilizations.protection.LockManager;
import com.kylantraynor.civilizations.protection.LockpickSession;

public class LockpickMenu extends Menu{

	private LockpickSession session;
	private int linesTop = 1;
	private int linesBottom = 5;
	private Inventory top;
	private Inventory bottom;
	private Player player;
	private int currentHighlight = 0;

	public LockpickMenu(LockpickSession s){
		this.session = s;
		initInventories();
	}
	/**
	 * Initialize the Menu Inventories.
	 */
	private void initInventories() {
		this.top = Bukkit.createInventory(null, 9 * linesTop, ChatColor.BOLD + "Lockpick");
		this.bottom = Bukkit.createInventory(null, 9 * linesBottom, "");
		
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
	
	/**
	 * Updates the Menu depending on the active page.
	 */
	@Override
	public void update(){
		currentHighlight = (currentHighlight + 1) % 9;
		this.bottom.clear();
		this.top.clear();
		int code = session.getCodeForCurrentStage();
		
		for(int i = 0; i < 9; i++){
			if(i == currentHighlight && i == code){
				top.setItem(pos(i, 0), new Button(player, Material.GOLD_BLOCK, "", new ArrayList<String>(), null, false));
			} else if(i == currentHighlight){
				top.setItem(pos(i, 0), new Button(player, Material.LAPIS_BLOCK, "", new ArrayList<String>(), null, false));
			} else {
				top.setItem(pos(i, 0), new Button(player, Material.IRON_BLOCK, "", new ArrayList<String>(), null, false));
			}
		}
		
		top.setItem(pos(8,1), getPickButton());
		
		player.closeInventory();
		player.openInventory(this);
	}
	
	public boolean isValidPick(){
		return currentHighlight == session.getCodeForCurrentStage();
	}
	
	/**
	 * Gets the Button linking to the Main Screen.
	 * @return
	 */
	public Button getPickButton(){
		List<String> lore = new ArrayList<String>();
		Button mainButton = new Button(player, Material.GOLD_BLOCK, "Pick", lore, new BukkitRunnable(){

			@Override
			public void run() {
				((LockpickMenu)MenuManager.getMenus().get(player)).tryPick();
			}
			
		}, true);
		mainButton.setAmount(session.getStage() + 1);
		return mainButton;
	}
	
	protected void tryPick() {
		if(isValidPick()){
			if(session.getStage() == 1){
				this.close();
			}
			session.passStage();
		} else {
			player.getInventory().remove(LockManager.getLockpick(1));
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
			if(player.getInventory().containsAtLeast(LockManager.getLockpick(1), 1)) this.close();
		}
		update();
	}
	/**
	 * Opens this view to the given player at the given page.
	 * @param player
	 * @param p
	 */
	public void open(Player player){
		this.player = player;
		update();
	}
	
	@Override
	public Inventory getBottomInventory() {
		return bottom;
	}

	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	@Override
	public Inventory getTopInventory() {
		return top;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}
	
}
