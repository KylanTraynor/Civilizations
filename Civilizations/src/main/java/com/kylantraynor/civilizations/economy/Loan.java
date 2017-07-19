package com.kylantraynor.civilizations.economy;

import java.time.Duration;
import java.time.Instant;

public class Loan {
	
	private EconomicEntity lender;
	private EconomicEntity borrower;
	private double principal;
	private float interestRate;
	private Instant start;
	private Instant end;
	private double payment;
	private Duration duration;
	
	public Loan(EconomicEntity lender, EconomicEntity borrower, double principal, float interestRate, Instant start, Instant end){
		this.lender = lender;
		this.borrower = borrower;
		this.principal = principal;
		this.interestRate = interestRate;
		this.start = start;
		this.end = end;
		this.payment = getLoanPayment();
		this.duration = Duration.between(start, end);
	}
	
	public double getLoanPayment(){
		if(getDuration() < 1) return principal;
		return principal * (interestRate*Math.pow((1+interestRate), getDuration()))/(Math.pow(1 + interestRate, getDuration()) - 1);
	}
	
	public int getDuration(){
		return (int) duration.toDays();
	}
	
	public boolean processPayment(){
		if(end.isAfter(Instant.now())){
			return Economy.tryTransferFunds(borrower, lender, "Loan of " + Economy.format(principal) + " interests.", payment);
		} else {
			return false;
		}
	}
}
