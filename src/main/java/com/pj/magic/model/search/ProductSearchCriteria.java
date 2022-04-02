package com.pj.magic.model.search;

import com.pj.magic.Constants;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.ProductCategory;
import com.pj.magic.model.ProductSubcategory;
import com.pj.magic.model.Supplier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCriteria {

	private Boolean active;
	private PricingScheme pricingScheme = new PricingScheme(Constants.CANVASSER_PRICING_SCHEME_ID);
	private Manufacturer manufacturer;
	private ProductCategory category;
	private ProductSubcategory subcategory;
	private String codeOrDescriptionLike;
	private Supplier supplier;
	private Boolean withMoreThanTwoBarcodes;
	private String customCode;
	
}
