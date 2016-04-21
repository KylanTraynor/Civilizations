package com.kylantraynor.civilizations;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.kylantraynor.civilizations.groups.settlements.Settlement;
import com.kylantraynor.civilizations.hook.towny.TownyTown;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Economy {
	
	private static net.milkbowl.vault.economy.Economy vault = null;
	private static Plugin vaultPlugin;

	/**
	 * Tries to load the Vault plugin. Returns true if successfully loaded, returns false otherwise.
	 * @param manager
	 * @return boolean
	 */
	public static boolean load(PluginManager manager) {
		if((vaultPlugin = manager.getPlugin("Vault")) != null){
			return setupVaultEconomy();
		}
		return false;
	}
	
	/**
	 * Setup VaultEconomy.
	 * @return True if correctly setup. False otherwise.
	 */
	private static boolean setupVaultEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = 
        		Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            return false;
        }
        vault = rsp.getProvider();
        return vault != null;
    }
	
	/**
	 * Gets the state of the Vault hook. Returns true if the plugin is loaded and enabled, returns false otherwise.
	 * @return boolean
	 */
	public static boolean isVaultEnabled(){
		if(vaultPlugin != null){
			return vaultPlugin.isEnabled();
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the Vault Economy object.
	 * @return Economy
	 */
	public static net.milkbowl.vault.economy.Economy getVault(){
		return vault;
	}
	
	/**
	 * Deposts the given amount of money into the player's account.
	 * @param player
	 * @param amount
	 * @return True if the transaction was successful, False otherwise.
	 */
	public static boolean depositPlayer(OfflinePlayer player, double amount){
		if(isVaultEnabled()){
			EconomyResponse r = vault.depositPlayer(player, amount);
			if(r.transactionSuccess()){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Withdraws the given amount from the player's account.
	 * @param player
	 * @param amount
	 * @return True if the transaction was successful, False otherwise.
	 */
	public static boolean withdrawPlayer(OfflinePlayer player, double amount){
		if(isVaultEnabled()){
			EconomyResponse r = vault.withdrawPlayer(player, amount);
			if(r.transactionSuccess()){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static boolean depositSettlement(Settlement settlement, double amount){
		if(settlement instanceof TownyTown && isVaultEnabled()){
			EconomyResponse r = vault.bankDeposit("town_" + settlement.getName(), amount);
			if(r.transactionSuccess()){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
