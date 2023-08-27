package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.pj.magic.Constants;
import com.pj.magic.util.NumberUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product2 implements Comparable<Product2>, Serializable {

    private static final long serialVersionUID = -6522062463737148407L;
    
    private Long id;
	private String description;
	private List<String> units = new ArrayList<>();
	private List<String> activeUnits = new ArrayList<>();
	private List<UnitPrice> unitPrices = new ArrayList<>();
	private List<UnitQuantity> unitQuantities = new ArrayList<>();
	private int minimumStockLevel = 0;
	private int maximumStockLevel = 0;
	private boolean active;
	private Manufacturer manufacturer;
	private ProductCategory category;
	private ProductSubcategory subcategory;
	private List<UnitConversion> unitConversions = new ArrayList<>();
	private List<UnitCost> unitCosts = new ArrayList<>();
	private BigDecimal companyListPrice;
	private List<UnitSku> unitSkus = new ArrayList<>();

	public Product2() {
	}
	
	public Product2(Long id) {
		this.id = id;
	}
	
	public boolean hasUnit(String unit) {
		return units.contains(unit);
	}
	
	public int getUnitQuantity(String unit) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity.getUnit())) {
				return unitQuantity.getQuantity();
			}
		}
		return 0;
	}
	
	public BigDecimal getUnitPrice(String unit) {
		for (UnitPrice unitPrice : unitPrices) {
			if (unitPrice.getUnit().equals(unit)) {
				return unitPrice.getPrice();
			}
		}
		if (hasUnit(unit)) {
			unitPrices.add(new UnitPrice(unit, Constants.ZERO));
		}
		return Constants.ZERO;
	}
	
	public boolean hasAvailableUnitQuantity(String unit) {
		return hasAvailableUnitQuantity(unit, 1);
	}
	
	public boolean hasAvailableUnitQuantity(String unit, int quantity) {
		return getUnitQuantity(unit) >= quantity;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof Product2)) {
            return false;
        }
        Product2 other = (Product2)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<UnitPrice> getUnitPrices() {
		return unitPrices;
	}

	public void setUnitPrices(List<UnitPrice> unitPrices) {
		this.unitPrices = unitPrices;
	}

	public List<UnitQuantity> getUnitQuantities() {
		return unitQuantities;
	}
	
	public void setUnitQuantities(List<UnitQuantity> unitQuantities) {
		this.unitQuantities = unitQuantities;
	}

	public void subtractUnitQuantity(String unit, Integer quantity) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity.getUnit())) {
				unitQuantity.setQuantity(unitQuantity.getQuantity() - quantity);
			}
		}
	}
	
	public void addUnitQuantity(String unit, int quantity) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity.getUnit())) {
				unitQuantity.setQuantity(unitQuantity.getQuantity() + quantity);
				return;
			}
		}
		unitQuantities.add(new UnitQuantity(unit, quantity));
	}

	public void setUnitPrice(String unit, BigDecimal price) {
		for (UnitPrice unitPrice : unitPrices) {
			if (unit.equals(unitPrice.getUnit())) {
				unitPrice.setPrice(price);
				return;
			}
		}
		unitPrices.add(new UnitPrice(unit, price));
	}

	@Override
	public int compareTo(Product2 o) {
		return id.compareTo(o.getId());
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public int getMinimumStockLevel() {
		return minimumStockLevel;
	}

	public void setMinimumStockLevel(int minimumStockLevel) {
		this.minimumStockLevel = minimumStockLevel;
	}

	public int getMaximumStockLevel() {
		return maximumStockLevel;
	}

	public void setMaximumStockLevel(int maximumStockLevel) {
		this.maximumStockLevel = maximumStockLevel;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	
	public void addUnit(String unit) {
		if (!units.contains(unit)) {
			units.add(unit);
		}
	}

	public void addActiveUnit(String unit) {
		if (!activeUnits.contains(unit)) {
			activeUnits.add(unit);
		}
	}

	public List<UnitConversion> getUnitConversions() {
		return unitConversions;
	}

	public void setUnitConversions(List<UnitConversion> unitConversions) {
		this.unitConversions = unitConversions;
	}

	public int getUnitConversion(String unit) {
		for (UnitConversion unitConversion : unitConversions) {
			if (unit.equals(unitConversion.getUnit())) {
				return unitConversion.getQuantity();
			}
		}
		return 0;
	}

	public void setUnitConversion(String unit, int quantity) {
		for (UnitConversion unitConversion : unitConversions) {
			if (unit.equals(unitConversion.getUnit())) {
				unitConversion.setQuantity(quantity);
				return;
			}
		}
		unitConversions.add(new UnitConversion(unit, quantity));
	}

	public List<UnitCost> getUnitCosts() {
		return unitCosts;
	}

	public void setUnitCosts(List<UnitCost> unitCosts) {
		this.unitCosts = unitCosts;
	}

	public void setGrossCost(String unit, BigDecimal grossCost) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				unitCost.setGrossCost(grossCost);
				return;
			}
		}
		unitCosts.add(new UnitCost(unit, grossCost, BigDecimal.ZERO));
	}

	public BigDecimal getGrossCost(String unit) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				return unitCost.getGrossCost();
			}
		}
		if (hasUnit(unit)) {
			unitCosts.add(new UnitCost(unit, Constants.ZERO, Constants.ZERO));
		}
		return Constants.ZERO;
	}

	public void setFinalCost(String unit, BigDecimal finalCost) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				unitCost.setFinalCost(finalCost);
				return;
			}
		}
		unitCosts.add(new UnitCost(unit, Constants.ZERO, finalCost));
	}

	public BigDecimal getFinalCost(String unit) {
		for (UnitCost unitCost : unitCosts) {
			if (unit.equals(unitCost.getUnit())) {
				return unitCost.getFinalCost();
			}
		}
		if (hasUnit(unit)) {
			unitCosts.add(new UnitCost(unit, Constants.ZERO, Constants.ZERO));
		}
		return Constants.ZERO;
	}
	
	public boolean isMaxUnit(String unit) {
		String maxUnit = Collections.max(units, new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2);
			}
		});
		return maxUnit.equals(unit);
	}
	
	public int getSuggestedOrder(String unit) {
		int currentQuantity = getUnitQuantity(unit);
		if (currentQuantity > maximumStockLevel) {
			return 0;
		} else {
			return maximumStockLevel - currentQuantity;
		}
	}

	public BigDecimal getPercentProfit(String unit) {
		BigDecimal sellingPrice = getUnitPrice(unit);
		if (sellingPrice.equals(Constants.ZERO)) {
			return Constants.ZERO;
		}
		
		BigDecimal finalCost = getFinalCost(unit);
		if (finalCost.equals(Constants.ZERO)) {
			return Constants.ONE_HUNDRED;
		}
		
		return Constants.ONE_HUNDRED.subtract(
				(Constants.ONE_HUNDRED.multiply(finalCost)).divide(sellingPrice, 2, RoundingMode.HALF_UP));
			
	}

	public BigDecimal getFlatProfit(String unit) {
		return getUnitPrice(unit).subtract(getFinalCost(unit));
	}

	public void setPercentProfit(String unit, BigDecimal profit) {
		BigDecimal unitPrice = getFinalCost(unit).divide(
				(Constants.ONE_HUNDRED.subtract(profit)).divide(Constants.ONE_HUNDRED, 4, RoundingMode.HALF_UP),
				2, RoundingMode.HALF_UP);
		setUnitPrice(unit, NumberUtil.roundUpToNearestFiveCents(unitPrice));
	}

	public void setFlatProfit(String unit, BigDecimal profit) {
		BigDecimal unitPrice = getFinalCost(unit).add(profit);
		setUnitPrice(unit, unitPrice);
	}

	public void autoCalculatePricesOfSmallerUnits() {
		Collections.sort(units, new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2) * -1;
			}
		});
		
		String maxUnit = units.get(0);
		BigDecimal priceOfMaxUnit = getUnitPrice(maxUnit);
		int conversionOfMaxUnit = getUnitConversion(maxUnit);
		for (int i = 1; i < units.size(); i++) {
			String unit = units.get(i);
			BigDecimal unitPrice = priceOfMaxUnit.divide(new BigDecimal(conversionOfMaxUnit / getUnitConversion(unit)), 
					2, RoundingMode.HALF_UP);
			setUnitPrice(unit, NumberUtil.roundUpToNearestFiveCents(unitPrice));
		}
	}

	public String getMaxUnit() {
		Collections.sort(units, new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2) * -1;
			}
		});
		
		return units.get(0);
	}
	
    public void autoCalculateCostsOfSmallerUnits() {
        autoCalculateCostsOfSmallerUnits(getMaxUnit());
    }
    
	public void autoCalculateCostsOfSmallerUnits(String referenceUnit) {
		Collections.sort(units, new Comparator<String>() {

			@Override
			public int compare(String unit1, String unit2) {
				return Unit.compare(unit1, unit2) * -1;
			}
		});
		
		String maxUnit = referenceUnit;
		BigDecimal grossCostOfMaxUnit = getGrossCost(maxUnit);
		BigDecimal finalCostOfMaxUnit = getFinalCost(maxUnit);
		int conversionOfMaxUnit = getUnitConversion(maxUnit);
		for (int i = (units.indexOf(referenceUnit) + 1); i < units.size(); i++) {
			String unit = units.get(i);
			BigDecimal grossCost = grossCostOfMaxUnit.divide(new BigDecimal(conversionOfMaxUnit / getUnitConversion(unit)), 
					2, RoundingMode.HALF_UP);
			BigDecimal finalCost = finalCostOfMaxUnit.divide(new BigDecimal(conversionOfMaxUnit / getUnitConversion(unit)), 
					2, RoundingMode.HALF_UP);
			setGrossCost(unit, grossCost);
			setFinalCost(unit, finalCost);
		}
	}
	
	public boolean hasNoSellingPrice(String unit) {
		return getUnitPrice(unit).equals(Constants.ZERO);
	}

	public ProductSubcategory getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(ProductSubcategory subcategory) {
		this.subcategory = subcategory;
	}

	public boolean hasSellingPriceLessThanCost(String unit) {
		return getUnitPrice(unit).compareTo(getFinalCost(unit)) == -1;
	}

	public BigDecimal getCompanyListPrice() {
		return companyListPrice;
	}

	public void setCompanyListPrice(BigDecimal companyListPrice) {
		this.companyListPrice = companyListPrice;
	}

	public BigDecimal getTotalValue(String unit) {
		return getFinalCost(unit).multiply(new BigDecimal(getUnitQuantity(unit)));
	}

	public List<String> getActiveUnits() {
		return activeUnits;
	}

	public void setActiveUnits(List<String> activeUnits) {
		this.activeUnits = activeUnits;
	}
	
	public boolean hasActiveUnit(String unit) {
		return activeUnits.contains(unit);
	}
	
	public BigDecimal getActiveUnitPrice(String unit) {
		if (hasActiveUnit(unit)) {
			return getUnitPrice(unit);
		} else {
			return Constants.ZERO;
		}
	}
	
	public int getConvertedQuantity(String fromUnit, String toUnit) {
		return getUnitConversion(fromUnit) / getUnitConversion(toUnit);
	}

	public void setUnitQuanty(String unit, int newQuantity) {
		for (UnitQuantity unitQuantity : unitQuantities) {
			if (unit.equals(unitQuantity.getUnit())) {
				unitQuantity.setQuantity(newQuantity);
			}
		}
	}

    public boolean areFieldsEqual(Product2 other) {
        if (units.size() != other.getUnits().size()) {
            return false;
        }
        
        if (unitConversions.size() != other.getUnitConversions().size()) {
            return false;
        }
        
        if (units.size() == 2) {
            return new EqualsBuilder()
                    .append(description, other.getDescription())
                    .append(units.get(0), other.getUnits().get(0))
                    .append(units.get(1), other.getUnits().get(1))
                    .append(unitConversions.get(0), other.getUnitConversions().get(0))
                    .append(unitConversions.get(1), other.getUnitConversions().get(1))
                    .isEquals();
        } else {
            return new EqualsBuilder()
                    .append(description, other.getDescription())
                    .append(units.get(0), other.getUnits().get(0))
                    .append(unitConversions.get(0), other.getUnitConversions().get(0))
                    .isEquals();
        }
    }

	public void addUnitSku(String unit, String sku) {
		for (UnitSku unitSku : unitSkus) {
			if (unit.equals(unitSku.getUnit())) {
				unitSku.setSku(sku);
				return;
			}
		}
		unitSkus.add(new UnitSku(unit, sku));
	}
	
	public String getUnitSku(String unit) {
		for (UnitSku unitSku : unitSkus) {
			if (unit.equals(unitSku.getUnit())) {
				return unitSku.getSku();
			}
		}
		return null;
	}
	
}