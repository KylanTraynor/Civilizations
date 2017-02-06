package com.kylantraynor.civilizations.economy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Budget {

	class BudgetEntry{
		
		private String label;
		private double amount;
		private Instant date;
		
		public BudgetEntry(String label, double amount, Instant date){
			this.label = label;
			this.amount = amount;
			this.date = date;
		}
		
		public String getLabel(){
			return label;
		}
		
		public double getAmount(){
			return amount;
		}
		
		public Instant getDate(){
			return date;
		}
	}
	
	private List<BudgetEntry> entries = new ArrayList<BudgetEntry>();
	private double balance = 0;
	
	public boolean addEntry(String label, double amount) {
		BudgetEntry newEntry = new BudgetEntry(label, amount, Instant.now());
		BudgetEntry similar = getRecentSimilar(newEntry);
		if(similar != null){
			similar.amount += amount;
			balance += amount;
			return true;
		}
		if(entries.add(newEntry)){
			balance += amount;
			return true;
		} else {
			return false;
		}
	}
	
	public BudgetEntry getRecentSimilar(BudgetEntry entry){
		for(BudgetEntry e : entries){
			if(e.label.equalsIgnoreCase(entry.getLabel())){
				if(e.getDate().isAfter(entry.getDate().minus(1, ChronoUnit.MONTHS))){
					return e;
				}
			}
		}
		return null;
	}
	
	public double getBalance(){
		return balance;
	}

}
