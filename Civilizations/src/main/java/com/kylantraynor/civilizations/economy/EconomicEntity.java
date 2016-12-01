package com.kylantraynor.civilizations.economy;

public interface EconomicEntity {
	public boolean addFunds(double amount);
	public boolean removeFunds(double amount);
	public boolean tryTakeFunds(double amount);
}