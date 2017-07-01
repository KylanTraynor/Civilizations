package com.kylantraynor.civilizations.economy;

import java.util.UUID;

public abstract class EconomicEntity {
	private Budget budget;
	private double balance = 0;
	private UUID id = UUID.randomUUID();
	
	public UUID getUniqueId(){
		return id;
	}
	
	public void setUniqueId(UUID id){
		this.id = id;
	}
	
	public Budget getBudget() {
		if(budget == null) budget = new Budget();
		return budget;
	}
	
	public void setBudget(Budget b){
		budget = b;
	}
	
	public double getBalance(){
		return balance;
	}
	public void setBalance(double newBalance){
		balance = newBalance;
	}
	public void giveFunds(double amount){
		balance += amount;
	}
	public void takeFunds(double amount){
		balance -= amount;
	}
}