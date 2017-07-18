package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.ButtonManager;
import com.kylantraynor.civilizations.managers.LockManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.protection.LockpickSession;
import com.kylantraynor.draggydata.PlayerData;

public class LockpickMenu extends Menu{

	private LockpickSession session;
	private int linesTop = 2;
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
		this.top = Bukkit.createInventory(null, 9 * linesTop, "" + ChatColor.BLACK + ChatColor.BOLD + "Lockpick");
		this.bottom = session.getPlayer().getInventory();
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
	public synchronized void update(){
		ButtonManager.clearButtons(player);
		currentHighlight = (currentHighlight + 1) % 9;
		
		this.top.clear();
		int code = session.getCodeForCurrentStage();
		
		for(int i = 0; i < 9; i++){
			if(i == currentHighlight){
				top.setItem(pos(i, 0), new Button(player, Material.GOLD_BLOCK, " ", new ArrayList<String>(), null, true));
			} else if(i == code){
				top.setItem(pos(i, 0), new Button(player, Material.LAPIS_BLOCK, " ", new ArrayList<String>(), null, true));
			} else {
				top.setItem(pos(i, 0), new Button(player, Material.IRON_BLOCK, " ", new ArrayList<String>(), null, true));
			}
		}
		
		top.setItem(pos(8,1), getPickButton());
		top.setItem(pos(0,1), getPickingLevelButton());
		player.updateInventory();
		/*
		BukkitRunnable bk = new BukkitRunnable(){
			@Override
			public void run() {
				if(MenuManager.getMenus().get(player) != null){
					((LockpickMenu)MenuManager.getMenus().get(player)).update();
				}
			}
		};
		*/
		if(MenuManager.getMenus().get(player) != null){
			PlayerData pd = PlayerData.get(player.getUniqueId());
			BukkitRunnable br = new BukkitRunnable(){
				@Override
				public void run() {
					BukkitRunnable b = new BukkitRunnable(){
						@Override
						public void run() {
							if(MenuManager.getMenus().get(player) != null){
								((LockpickMenu)MenuManager.getMenus().get(player)).update();
							};
						}
					};
					b.runTask(Civilizations.currentInstance);
				}
			};
			br.runTaskLaterAsynchronously(Civilizations.currentInstance, Math.min(Math.max(pd.getSkillLevel("Lock Picking") - session.getLockLevel(), 3),10));
			/*Timer timer = new Timer();
			timer.schedule (new TimerTask() {
				public void run()
		        {
					if(MenuManager.getMenus().get(player) != null){
						((LockpickMenu)MenuManager.getMenus().get(player)).update();
					};
		        }
		    }, Math.min(Math.max(pd.getSkillLevel("Lock Picking") - session.getLockLevel(), 3),10) * 50);*/
			//bk.runTaskLater(Civilizations.currentInstance, Math.min(Math.max(pd.getSkillLevel("Lock Picking") - session.getLockLevel(), 3),10));
		}
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
		mainButton.setAmount(session.getStage());
		return mainButton;
	}
	
	/**
	 * Gets the Button linking to the Main Screen.
	 * @return
	 */
	public Button getPickingLevelButton(){
		List<String> lore = new ArrayList<String>();
		PlayerData pd = PlayerData.get(player.getUniqueId());
		lore.add("Exp: " + pd.getSkillLevelExp("Lock Picking") + "/" + pd.getSkillExpToNextLevel("Lock Picking"));
		Button mainButton = new Button(player, Material.EMERALD_BLOCK, "Lock Picking Level", lore, null, true);
		mainButton.setAmount(PlayerData.get(player.getUniqueId()).getSkillLevel("Lock Picking"));
		return mainButton;
	}
	
	protected void tryPick() {
		if(isValidPick()){
			PlayerData.get(player.getUniqueId()).giveSkillExperience("Lock Picking", 1);
			if(session.getStage() == 1){
				LockManager.removePickFromInventory(player.getInventory(), 1);
				session.end();
				this.close();
			}
			session.passStage();
		} else {
			LockManager.removePickFromInventory(player.getInventory(), 1);
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
			PlayerData.get(player.getUniqueId()).giveSkillExperience("Lock Picking", 1);
			if(!player.getInventory().containsAtLeast(LockManager.getLockpick(1), 1)){
				session.end();
				this.close();
			} else {
				session.reset();
			}
		}
	}
	
	/**
	 * Opens this view to the given player at the given page.
	 * @param player
	 * @param p
	 */
	public void open(Player player){
		this.player = player;
		update();
		player.openInventory(this);
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
