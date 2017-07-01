package com.kylantraynor.civilizations.economy;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class BudgetEntry {
	private UUID emiter;
	private UUID receiver;
	private String label = "";
	private double amount = 0.0;
	private Instant instant;
	
	public BudgetEntry(UUID emiter, UUID receiver, String label, double amount, Instant instant){
		this.setEmiter(emiter);
		this.setReceiver(receiver);
		this.setLabel(label);
		this.setAmount(amount);
		this.setInstant(instant);
	}
	
	public BudgetEntry(EconomicEntity emiter, EconomicEntity receiver, String label, double amount, Instant instant){
		if(emiter != null)
			this.setEmiter(emiter.getUniqueId());
		if(receiver != null)
			this.setReceiver(receiver.getUniqueId());
		this.setLabel(label);
		this.setAmount(amount);
		this.setInstant(instant);
	}
	
	public BudgetEntry(OfflinePlayer emiter, OfflinePlayer receiver, String label, double amount, Instant instant){
		if(emiter != null)
			this.setEmiter(emiter.getUniqueId());
		if(receiver != null)
			this.setReceiver(receiver.getUniqueId());
		this.setLabel(label);
		this.setAmount(amount);
		this.setInstant(instant);
	}
	
	public BudgetEntry(EconomicEntity emiter, OfflinePlayer receiver, String label, double amount, Instant instant){
		if(emiter != null)
			this.setEmiter(emiter.getUniqueId());
		if(receiver != null)
			this.setReceiver(receiver.getUniqueId());
		this.setLabel(label);
		this.setAmount(amount);
		this.setInstant(instant);
	}
	
	public BudgetEntry(OfflinePlayer emiter, EconomicEntity receiver, String label, double amount, Instant instant){
		if(emiter != null)
			this.setEmiter(emiter.getUniqueId());
		if(receiver != null)
			this.setReceiver(receiver.getUniqueId());
		this.setLabel(label);
		this.setAmount(amount);
		this.setInstant(instant);
	}

	public UUID getEmiter() {
		return emiter;
	}

	public void setEmiter(UUID emiter) {
		this.emiter = emiter;
	}

	public UUID getReceiver() {
		return receiver;
	}

	public void setReceiver(UUID receiver) {
		this.receiver = receiver;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(Instant instant) {
		this.instant = instant;
	}
}