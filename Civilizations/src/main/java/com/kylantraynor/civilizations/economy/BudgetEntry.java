package com.kylantraynor.civilizations.economy;

public class BudgetEntry {
	private String label = "";
	private double amount = 0.0;
	
	public BudgetEntry(String label, double amount){
		this.label = label;
		this.amount = amount;
	}
}