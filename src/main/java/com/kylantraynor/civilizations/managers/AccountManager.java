package com.kylantraynor.civilizations.managers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.players.CivilizationsAccount;
import com.kylantraynor.civilizations.players.CivilizationsCharacter;
import com.kylantraynor.civilizations.settings.AccountSettings;
import com.kylantraynor.civilizations.settings.CharacterSettings;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AccountManager {
    private static final File accountDirectory = new File(Civilizations.currentInstance.getDataFolder(), "Accounts");
    private static final File characterDirectory = new File(accountDirectory, "Characters");
    private static final Map<UUID, CivilizationsAccount> activeAccounts = new LinkedHashMap<>();

    private static final LoadingCache<UUID, CivilizationsAccount> accounts = CacheBuilder.newBuilder()
            .weakValues()
            .build( new CacheLoader<UUID, CivilizationsAccount>(){
                @Override
                public CivilizationsAccount load(UUID uuid) throws IOException, InvalidConfigurationException {
                    return loadAccount(uuid);
                }
            });

    private static final LoadingCache<UUID, CivilizationsCharacter> characters = CacheBuilder.newBuilder()
            .weakValues()
            .build( new CacheLoader<UUID, CivilizationsCharacter>() {
                @Override
                public CivilizationsCharacter load(UUID key) throws IOException, InvalidConfigurationException {
                    return loadCharacter(key);
                }
            });

    public static File getAccountsDirectory(){
        return accountDirectory;
    }
    public static File getCharactersDirectory(){
        return characterDirectory;
    }

    /**
     * Loads a {@linkplain CivilizationsCharacter} from its file.
     * @param id {@link SimpleIdentifier}
     * @return {@link CivilizationsCharacter}
     * @throws IOException
     * @throws InvalidConfigurationException
     * @throws IllegalArgumentException If such a character did not exist.
     */
    private static CivilizationsCharacter loadCharacter(UUID id)
            throws IOException, InvalidConfigurationException, IllegalArgumentException {
        if(!accountDirectory.exists()) accountDirectory.mkdir();
        if(!characterDirectory.exists()) characterDirectory.mkdir();
        File f = new File(characterDirectory, id.toString() + ".yml");
        if(f.exists()){
            CharacterSettings s = new CharacterSettings();
            s.load(f);
            return new CivilizationsCharacter(s);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Loads a {@linkplain CivilizationsAccount} from its file.
     * @param id {@link UUID}
     * @return {@link CivilizationsAccount}
     * @throws IOException
     * @throws InvalidConfigurationException
     */
    private static CivilizationsAccount loadAccount(UUID id) throws IOException, InvalidConfigurationException {
        if(!accountDirectory.exists()) accountDirectory.mkdir();
        File f = new File(accountDirectory, id.toString() + ".yml");
        if(f.exists()){
            AccountSettings s = new AccountSettings();
            s.load(f);
            return new CivilizationsAccount(s);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Gets the {@linkplain CivilizationsAccount} associated to the given player ID,
     * or creates one if none exists.
     * @param playerId as {@link UUID}
     * @throws {@link ExecutionException}
     * @return {@link CivilizationsAccount}
     */
    public static CivilizationsAccount getAccount(UUID playerId) throws ExecutionException {
        return accounts.get(playerId);
    }

    /**
     * Gets the {@linkplain CivilizationsAccount} associated to the given player ID,
     * or creates one if none exists.
     * @param characterId as {@link UUID}
     * @throws {@link ExecutionException}
     * @return {@link CivilizationsAccount}
     */
    public static CivilizationsCharacter getCharacter(UUID characterId) throws ExecutionException {
        if(isCharacterRegistered(characterId)) throw new IllegalArgumentException("Character "+ characterId.toString() +" does not exist!");
        return characters.get(characterId);
    }

    /**
     * Gets all the {@linkplain UUID} registered on the server.
     * @return Array of {@link UUID}
     */
    public static UUID[] getAllCharacterIds(){
        if(!characterDirectory.exists()) return new UUID[0];
        List<UUID> result = new ArrayList<>();
        for(String s : characterDirectory.list()){
            if(s.endsWith(".yml")){
                result.add(UUID.fromString(s.substring(0, s.length() - 4)));
            }
        }
        return result.toArray(new UUID[result.size()]);
    }

    /**
     * Checks if the {@linkplain CivilizationsCharacter} with the given {@linkplain UUID}
     * is registered on the server.
     * @param id {@link UUID}
     * @return {@code true} if the character is registered.
     */
    public static boolean isCharacterRegistered(UUID id){
        String fileName = id.toString() + ".yml";
        return (new File(characterDirectory, fileName)).exists();
    }

    /**
     * Gets the {@linkplain CivilizationsAccount} associated to the given player ID,
     * if it is active (has been logged in).
     * @param playerId The {@link UUID} of the player.
     * @return {@link CivilizationsAccount} or {@code null} if the account is not active.
     */
    public static CivilizationsAccount getActive(UUID playerId){
        return activeAccounts.get(playerId);
    }

    /**
     * Checks if the {@linkplain CivilizationsAccount} associated to the given player ID
     * is active (has been logged in).
     * @param playerId The {@link UUID} of the player.
     * @return {@code true} if the account is logged in.
     */
    public static boolean isActive(UUID playerId){
        return activeAccounts.containsKey(playerId);
    }

    /**
     * Deactivates the {@linkplain CivilizationsAccount} with the given {@linkplain UUID}.
     * @param accountId
     * @return
     */
    public static boolean deactivateAccount(UUID accountId){
        return activeAccounts.remove(accountId) != null;
    }

    /**
     * Gets the current {@linkplain UUID} associated to the given player ID.
     * If the player is not logged in, returns the last {@linkplain UUID} it
     * used.
     * @param op {@link OfflinePlayer}
     * @return {@link UUID}
     */
    public static UUID getCurrentIdentifier(OfflinePlayer op){
        try{
            CivilizationsAccount ca = getAccount(op.getUniqueId());
            if(ca.getCurrentCharacterId() != null){
                return ca.getCurrentCharacterId();
            } else {
                return ca.getPlayerId();
            }
        } catch (ExecutionException ex){
            ex.printStackTrace();
            return op.getUniqueId();
        }
    }

    /**
     * Gets the current {@linkplain EconomicEntity} associated to the given player ID.
     * @param p {@link Player} to check.
     * @return Either the {@link EconomicEntity} representing the player's account, or the current {@link CivilizationsCharacter}.
     */
    public static EconomicEntity getEconomicEntity(OfflinePlayer p){
        try {
            CivilizationsAccount ca = getAccount(p.getUniqueId());
            if(ca.getCurrentCharacterId() != null){
                return ca.getCurrentCharacter();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return EconomicEntity.get(p.getUniqueId());
    }

    /**
     * Reloads the data of the account attached to the given {@linkplain Player}
     * then adds the resulting {@linkplain CivilizationsAccount} to the list of active accounts.
     * @param p as {@link Player}
     * @param loadCharacter
     * @throws {@link ExecutionException}
     * @return {@link CivilizationsAccount}
     */
    public static CivilizationsAccount login(Player p, boolean loadCharacter) throws ExecutionException {
        CivilizationsAccount ac = activeAccounts.get(p.getUniqueId());
        if(ac != null){
            return ac;
        }
        ac = getAccount(p.getUniqueId());
        if(loadCharacter){
            if(ac.getCurrentCharacterId() == null){
                Location loc = ac.getSettings().getBaseLocation();
                ItemStack[] inventory = ac.getSettings().getBaseInventory();
                ItemStack[] armor = ac.getSettings().getBaseArmor();
                ItemStack[] ec = ac.getSettings().getBaseEnderChest();
                if(loc != null && inventory != null && armor != null && ec != null){
                    p.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    p.getInventory().setContents(inventory);
                    p.getInventory().setArmorContents(armor);
                    p.getEnderChest().setContents(ec);
                }
            } else {
                CivilizationsCharacter current = ac.getCurrentCharacter();
                current.restore();
            }
        }
        activeAccounts.put(ac.getPlayerId(), ac);
        return ac;
    }

    /**
     * Saves all the data of the account and removes it from the list of active accounts.
     * @param p {@link Player} to log out.
     * @return {@link CivilizationsAccount} of that player, or {@code null} if it was not active.
     */
    public static CivilizationsAccount logout(Player p){
        CivilizationsAccount ca = getActive(p.getUniqueId());
        if(ca != null){
            ca.logout();
            return ca;
        } else return null;
    }

    /**
     * Saves the data of {@linkplain CivilizationsAccount} and current
     * {@linkplain CivilizationsCharacter} for all the active accounts.
     */
    public static void logoutAllPlayers(){
        for(CivilizationsAccount ca : activeAccounts.values().toArray(new CivilizationsAccount[activeAccounts.size()])){
            if(ca != null){
                ca.logout();
            }
        }
    }
}
