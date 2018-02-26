package com.kylantraynor.civilizations.economy;

public class TransactionResult {
	public boolean success = false;
	public String info = "";
	
	public boolean wasSuccessful(){
		return success;
	}
	
	public String getInfo(){
		return info;
	}
}