package com.kylantraynor.civilizations.players;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.settings.CharacterSettings;
import com.kylantraynor.civilizations.territories.Influence;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import org.bukkit.Location;

import java.util.UUID;

public class CivilizationsCharacterFactory {

    private CharacterSettings settings;

    public CivilizationsCharacterFactory(UUID id){
        settings.setAccountId(id);
        settings.setUniqueid(UUID.randomUUID());
        settings.setLocation(Civilizations.getNewCharacterSpawn());
    }

    public CivilizationsCharacterFactory setFirstName(String name){
        settings.setFirstName(name);
        return this;
    }

    public CivilizationsCharacterFactory setFamilyName(String name){
        settings.setLastName(name);
        return this;
    }

    public CivilizationsCharacterFactory setGender(CivilizationsCharacter.Gender gender){
        settings.setGender(gender);
        return this;
    }

    public CivilizationsCharacterFactory setLocation(Location location){
        settings.setLocation(location);
        return this;
    }

    public CivilizationsCharacterFactory setInfluence(Influence influence){
        settings.setInfluence(influence);
        return this;
    }

    public CivilizationsCharacter build(){
        settings.save();
        return new CivilizationsCharacter(settings);
    }

}
