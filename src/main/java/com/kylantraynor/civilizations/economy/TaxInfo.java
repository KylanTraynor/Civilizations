package com.kylantraynor.civilizations.economy;

public class TaxInfo {
	private double value;
	private String type;
	private boolean isPercent;
	private TaxBase base;
	
	public TaxInfo(String tax, TaxBase base, double value, boolean isPercent){
		this.setValue(value);
		this.setType(tax);
		this.setBase(base);
		this.setPercent(isPercent);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String tax) {
		this.type = tax;
	}

	public boolean isPercent() {
		return isPercent;
	}

	public void setPercent(boolean isPercent) {
		this.isPercent = isPercent;
	}

	public TaxBase getBase() {
		return base;
	}

	public void setBase(TaxBase base) {
		this.base = base;
	}
}
