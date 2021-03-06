package com.kylantraynor.civilizations.settings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.kylantraynor.civilizations.Civilizations;
import com.kylantraynor.civilizations.shapes.Shape;
import com.kylantraynor.civilizations.utils.SimpleIdentifier;
import com.kylantraynor.civilizations.utils.Utils;

import com.kylantraynor.civilizations.economy.EconomicEntity;
import com.kylantraynor.civilizations.groups.settlements.plots.PlotType;

public class PlotSettings extends GroupSettings{
	
	private UUID settlement;
	private EconomicEntity owner;
	private Boolean forRent;
	private Boolean forSale;
	private EconomicEntity renter;
	private Double rent;
	private Double price;
	private Instant nextPayment;
	private List<Shape> shapes;

	/**
	 * Gets the settlement this plot belongs to.
	 * @return
	 */
	public UUID getSettlementId() {
		if(settlement == null){
			if(this.contains("General.Settlement")){
				UUID id = UUID.fromString(this.getString("General.Settlement"));
				if(id != null){
					settlement = id;
				}
			}
		}
		return settlement;
	}

	/**
	 * Sets the settlement this plot belongs to.
	 * @param settlement
	 */
	public void setSettlementId(UUID settlement) {
		if(settlement != null){
			this.set("General.Settlement", settlement.toString());
		} else {
			this.set("General.Settlement", null);
		}
		this.settlement = settlement;
		setChanged(true);
	}

    /**
     * Gets the owner group id of this plot.
     * @return {@link UUID}
     */
    public UUID getOwnerGroupId() {
        if(this.contains("General.OwnerGroup")){
            return UUID.fromString(this.getString("General.OwnerGroup"));
        }
        return null;
    }

    /**
     * Sets the owner of this plot.
     * @param id {@link UUID}
     */
    public void setOwnerGroupId(UUID id) {
        if(id != null){
            this.set("General.OwnerGroup", id.toString());
        } else {
            this.set("General.OwnerGroup", null);
        }
        this.setChanged(true);
    }

    /**
     * Gets the renter group id of this plot.
     * @return {@link UUID}
     */
    public UUID getRenterGroupId() {
        if(this.contains("General.RenterGroup")){
            return UUID.fromString(this.getString("General.RenterGroup"));
        }
        return null;
    }

    /**
     * Sets the renter group of this plot.
     * @param id {@link UUID}
     */
    public void setRenterGroupId(UUID id) {
        if(id != null){
            this.set("General.RenterGroup", id.toString());
        } else {
            this.set("General.RenterGroup", null);
        }
        this.setChanged(true);
    }

	/**
	 * Gets the rent of this plot.
	 * @return
	 */
	public double getRent() {
		if(rent != null) return rent;
		if(this.contains("Economy.Rent")){
			rent = this.getDouble("Economy.Rent");
		} else {
			rent = 1.0;
		}
		return rent;
	}

	/**
	 * Sets the rent of this plot.
	 * @param rent
	 */
	public void setRent(double rent) {
		rent = Math.max(rent, 1.0);
		this.rent = rent;
		this.set("Economy.Rent", rent);
		this.setChanged(true);
	}
	
	/**
	 * Gets the price of this plot.
	 * @return
	 */
	public double getPrice() {
		if(price != null) return price;
		if(this.contains("Economy.Price")){
			price = this.getDouble("Economy.Price");
		} else {
			price = 1.0;
		}
		return price;
	}

	/**
	 * Sets the price of this plot.
	 * @param price
	 */
	public void setPrice(double price) {
		price = Math.max(price, 1.0);
		this.price = price;
		this.set("Economy.Price", price);
		this.setChanged(true);
	}

	/**
	 * Checks if the plot is for rent.
	 * @return
	 */
	public boolean isForRent() {
		if(forRent != null) return forRent;
		if(this.contains("Economy.ForRent")){
			forRent = this.getBoolean("Economy.ForRent");
		} else {
			forRent = false;
		}
		return forRent;
	}

	/**
	 * Sets if the plot is for rent.
	 * @param forRent
	 */
	public void setForRent(boolean forRent) {
		this.forRent = forRent;
		this.set("Economy.ForRent", forRent);
		this.setChanged(true);
	}
	
	/**
	 * Checks if the plot is for sale.
	 * @return
	 */
	public boolean isForSale() {
		if(forSale != null) return forSale;
		if(this.contains("Economy.ForSale")){
			forSale = this.getBoolean("Economy.ForSale");
		} else {
			forSale = false;
		}
		return forSale;
	}

	/**
	 * Sets if the plot is for sale.
	 * @param forSale
	 */
	public void setForSale(boolean forSale) {
		this.forSale = forSale;
		this.set("Economy.ForSale", forSale);
		this.setChanged(true);
	}

	/**
	 * Gets the time of the next rent payment.
	 * @return
	 */
	public Instant getNextPayment() {
		if(nextPayment != null) return nextPayment;
		nextPayment = Instant.now();
		if(this.contains("Economy.NextPayment")){
			try{
				nextPayment = Instant.parse(this.getString("Economy.NextPayment"));
			} catch (Exception e){}
		}
		return nextPayment;
	}

	/**
	 * Sets the time of the next payment.
	 * @param date
	 */
	public void setNextPayment(Instant date) {
		nextPayment = date == null ? Instant.now() : date;
		this.set("Economy.NextPayment", nextPayment.toString());
		this.setChanged(true);
	}
	
	public PlotType getPlotType(){
		String s = this.getString("General.Type");
		if(s == null) return null;
		return PlotType.valueOf(s);
	}
	
	public void setPlotType(PlotType type){
		this.set("General.Type", type.toString());
		this.setChanged(true);
	}

    public void setShapes(List<Shape> shapes) {
        if(shapes != null){
            try{
                this.set("Protection.Shape", Utils.getShapesString(shapes));
                this.shapes = shapes;
            } catch(Exception e){
                Civilizations.currentInstance.getLogger().warning("Couldn't save protection shapes for " + this.getName() + ".");
                e.printStackTrace();
            }
        } else {
            this.set("Protection.Shape", null);
            this.shapes = null;
        }
    }

    public List<Shape> getShapes(){
	    if(shapes != null) return shapes;
        if(this.contains("Protection.Shape")){
            shapes = Utils.parseShapes(this.getString("Protection.Shape"));
        } else {
            shapes = new ArrayList<>();
        }
        return shapes;
    }

    public void addShape(Shape s){
	    shapes = getShapes();
	    shapes.add(s);
	    setShapes(shapes);
    }
}
