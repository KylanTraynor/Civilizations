package com.kylantraynor.civilizations.economy;

public interface EconomicEntity {
	public Budget getBudget();
	public boolean addFunds(String label, double amount);
	public boolean removeFunds(String label, double amount);
	public boolean tryTakeFunds(String label, double amount);
}