package com.kylantraynor.civilizations.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class QuestionMenu extends Menu {
	
	private Player player;
	private Inventory top;
	private String question;
	private String[] answers;
	private Button[] buttons;
	private MenuReturnFunction function;

	public QuestionMenu(Player player, String question, String[] answers, MenuReturnFunction function){
		this.player = player;
		this.question = question;
		this.answers = answers;
		this.function = function;
		initInventory();
	}

	private void initInventory() {
		top = Bukkit.createInventory(null, answers.length + (9 - (answers.length % 9)), "Question");
		for(int i = 0; i < answers.length; i++){
			List<String> lore = new ArrayList<String>();
			lore.add(answers[i]);
			final int id = i;
			buttons[id] = new Button(getPlayer(), Material.PAPER, question, lore, new BukkitRunnable(){
				@Override
				public void run() {
					function.setReturnedValue(id);
					function.run();
				}
			}, true);
		}
	}

	@Override
	public void update() {
		top.clear();
		for(int i = 0; i < answers.length; i++){
			top.setItem(i, buttons[i]);
		}
		getPlayer().updateInventory();
	}

	@Override
	public Inventory getBottomInventory() {
		return getPlayer().getInventory();
	}

	@Override
	public Player getPlayer() {
		return this.player;
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
