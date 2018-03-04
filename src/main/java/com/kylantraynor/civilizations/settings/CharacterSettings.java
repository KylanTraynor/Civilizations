package com.kylantraynor.civilizations.settings;

import com.kylantraynor.civilizations.managers.AccountManager;
import com.kylantraynor.civilizations.players.CivilizationsCharacter;
import com.kylantraynor.civilizations.territories.Influence;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import com.kylantraynor.civilizations.utils.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CharacterSettings extends YamlConfiguration {
    private static final String ACCOUNTID = "AccountId";
    private static final String UNIQUEID = "UniqueId";
    private static final String FIRSTNAME = "Name.FirstName";
    private static final String LASTNAME = "Name.LastName";
    private static final String GENDER = "Gender";
    private static final String BIRTHDATE = "BirthDate";
    private static final String LOCATION = "Location";
    private static final String INVENTORY = "Inventory.BaseInventory";
    private static final String ARMOR = "Inventory.Armor";
    private static final String ENDERCHEST = "Inventory.EnderChest";
    private static final String INFLUENCE = "Influence";

    public UUID getUniqueId(){
        return UUID.fromString(this.getString(UNIQUEID));
    }

    public void setUniqueid(UUID id){
        this.set(UNIQUEID, id.toString());
    }

    public UUID getAccountId(){
        return UUID.fromString(this.getString(ACCOUNTID));
    }

    public void setAccountId(UUID id){
        this.set(ACCOUNTID, id.toString());
    }

    public String getFirstName(){
        return this.getString(FIRSTNAME);
    }

    public void setFirstName(String newName){
        this.set(FIRSTNAME, newName);
    }

    public String getLastName(){
        return this.getString(LASTNAME);
    }

    public void setLastName(String newName){
        this.set(LASTNAME, newName);
    }

    public CivilizationsCharacter.Gender getGender(){
        return CivilizationsCharacter.Gender.valueOf(this.getString(GENDER));
    }

    public void setGender(CivilizationsCharacter.Gender gender){
        this.set(GENDER, gender.toString());
    }

    public Instant getBirthdate(){
        return Instant.parse(this.getString(BIRTHDATE));
    }

    public void setBirthdate(Instant birthdate){
        this.set(BIRTHDATE, birthdate.toString());
    }

    public Location getLocation(){
        return Utils.parseLocation(this.getString(LOCATION));
    }

    public void setLocation(Location location){
        this.set(LOCATION, Utils.locationToString(location));
    }

    public ItemStack[] getInventory(){
        return ((List<ItemStack>) this.get(INVENTORY)).toArray(new ItemStack[0]);
    }

    public void setInventory(ItemStack[] contents){
        this.set(INVENTORY, contents);
    }

    public ItemStack[] getArmor(){
        return ((List<ItemStack>) this.get(ARMOR)).toArray(new ItemStack[0]);
    }

    public void setArmor(ItemStack[] contents){
        this.set(ARMOR, contents);
    }

    public ItemStack[] getEnderChest(){
        return ((List<ItemStack>) this.get(ENDERCHEST)).toArray(new ItemStack[0]);
    }

    public void setEnderChest(ItemStack[] contents){
        this.set(ENDERCHEST, contents);
    }

    public Influence getInfluence(){
        return Influence.parse(this.getString(INFLUENCE));
    }

    public void setInfluence(Influence influence){
        this.set(INFLUENCE, influence);
    }

    public File getFile(){
        File f = AccountManager.getCharactersDirectory();
        if(!f.exists()) f.mkdirs();
        return new File(f, getUniqueId().toString() + ".yml");
    }

    public void reload(){
        File f = getFile();
        if(f.exists()){
            try { this.load(f); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void save(){
        File f = getFile();
        if(!f.exists()) {
            try { f.createNewFile(); } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try { this.save(f); } catch (IOException e) { e.printStackTrace(); }
    }
}
