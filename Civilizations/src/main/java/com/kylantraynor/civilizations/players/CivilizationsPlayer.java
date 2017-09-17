package com.kylantraynor.civilizations.players;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.territories.HonorificTitle;
import com.kylantraynor.civilizations.territories.Influence;

public class CivilizationsPlayer extends EconomicEntity {
	
	public enum Gender {
		MALE, FEMALE
	}
	
	private UUID accountId;
	private UUID id;
	private String name = "";
	private String familyName = "";
	private Gender gender = Gender.MALE;
	private List<HonorificTitle> titles = new ArrayList<HonorificTitle>();
	private Influence influence = new Influence();
	
	public CivilizationsPlayer(Player p){
		accountId = p.getUniqueId();
		id = UUID.randomUUID();
	}
	
	public UUID getAccountId(){
		return accountId;
	}
	
	public UUID getUniqueId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getFamilyName(){
		return familyName;
	}
	
	public Gender getGender(){
		return gender;
	}
	
	public HonorificTitle[] getTitles(){
		if(titles.size() == 0) return new HonorificTitle[0];
		return titles.toArray(new HonorificTitle[titles.size()]);
	}
	
	public Influence getInfluence(){
		return influence;
	}
}
