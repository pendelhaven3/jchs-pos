package com.pj.magic.model.search;

import com.pj.magic.Constants;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.Supplier;

public class ProductSearchCriteria {

	private Boolean active;
	private PricingScheme pricingScheme = new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID);
	private Manufacturer manufacturer;
	private ProductCategory category;
	private ProductSubcategory subcategory;
	private String codeOrDescriptionLike;
	private Supplier supplier;
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public ProductSubcategory getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(ProductSubcategory subcategory) {
		this.subcategory = subcategory;
	}

	public String getCodeOrDescriptionLike() {
		return codeOrDescriptionLike;
	}

	public void setCodeOrDescriptionLike(String codeOrDescriptionLike) {
		this.codeOrDescriptionLike = codeOrDescriptionLike;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

}
