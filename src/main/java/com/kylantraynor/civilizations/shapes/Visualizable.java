package com.kylantraynor.civilizations.shapes;

import java.util.List;

import org.bukkit.entity.Player;

public interface Visualizable {
	/**
	 * Gets the list of players this object is displayed to.
	 * @return
	 */
	List<Player> getPlayers();
	/**
	 * Sets the list of players this object is displayed to.
	 * @param players
	 */
	void setPlayers(List<Player> players);
	/**
	 * Adds a player to the list of players this object is displayed to.
	 * @param player
	 * @return
	 */
	boolean addPlayer(Player player);
	/**
	 * Removes a player from the list of players this object is displayed to.
	 * @param player
	 * @return
	 */
	boolean removePlayer(Player player);
	/**
	 * Shows to a player this object.
	 * @param player
	 */
	void show(Player player);
	/**
	 * Hides this object from a player.
	 * @param player
	 */
	void hide(Player player);
}
