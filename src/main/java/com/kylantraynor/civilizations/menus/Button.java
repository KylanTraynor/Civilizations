package com.kylantraynor.civilizations.menus;

import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.managers.ButtonManager;
import com.kylantraynor.civilizations.managers.MenuManager;

public class Button extends ItemStack{
	
	private Material invalidButton = Material.IRON_BLOCK;
	private boolean isEnabled;
	private Player player;
	private BukkitRunnable runnable;
	
	public Button(Player p, Material mat, String title, List<String> description, BukkitRunnable runnable, boolean isEnabled){
		super(mat);
		this.setPlayer(p);
		if(!isEnabled) this.setType(invalidButton);
		ItemMeta im = getItemMeta();
		im.setDisplayName(title);
		im.setLore(description);
		this.isEnabled = isEnabled;
		this.runnable = runnable;
		setItemMeta(im);
		ButtonManager.registerButton(this);
	}

	public Button(Player p, ItemStack is, String title, List<String> description, BukkitRunnable runnable, boolean isEnabled){
		super(is.getType());
		this.setPlayer(p);
		if(!isEnabled) this.setType(invalidButton);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(title);
		im.setLore(description);
		this.isEnabled = isEnabled;
		this.runnable = runnable;
		setItemMeta(im);
		ButtonManager.registerButton(this);
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void run(){
		if(isEnabled && this.runnable != null){
			try{
				this.runnable.runTask(Civilizations.currentInstance);
				ButtonManager.buttons.remove(this);
			} catch (Exception e){
				MenuManager.getMenus().get(getPlayer()).close();
				getPlayer().sendMessage("" + ChatColor.BOLD + ChatColor.RED + "An error happened clicking on this button. Please report it to a staff member.");
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		if(this.getItemMeta() != null){
			return this.getItemMeta().getDisplayName();
		} else {
			return this.getType().toString();
		}
	}
}
