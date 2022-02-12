package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.ProductPriceHistory;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.ProductSearchCriteria;

public interface ProductService {

	List<Product> getAllProducts();
	
	// TODO: Review references to this
	List<Product> getAllActiveProducts();
	
	List<Product> getAllActiveProducts(PricingScheme pricingScheme);
	
	Product findProductByCode(String code);
	
	Product findProductByCodeAndPricingScheme(String code, PricingScheme pricingScheme);
	
	Product getProduct(long id);

	Product getProduct(long id, PricingScheme pricingScheme);

	void save(Product product);
	
	void addProductSupplier(Product product, Supplier supplier);

	List<Supplier> getProductSuppliers(Product product);

	List<Supplier> getAvailableSuppliers(Product product);

	void deleteProductSupplier(Product product, Supplier supplier);
	
	void saveUnitCostsAndPrices(Product product, PricingScheme pricingScheme);

	List<Product> getAllActiveProductsBySupplier(Supplier supplier);
	
	List<Product> searchProducts(ProductSearchCriteria criteria);

	boolean canDeleteProduct(Product product);

	void deleteProduct(Product product);

	List<ProductPriceHistory> getProductPriceHistory(Product product, PricingScheme pricingScheme);

	void updateMaximumStockLevel(List<Product> products);

	/**
	 * @return true if quantities are saved, false if not (probably existing already)
	 */
	boolean saveDailyProductStartingQuantities();

    void updateProduct(Product product);

	List<String> getAllActiveProductCodes();

	void updateProductsAsInactive(List<String> activeProductCodes);

	void subtractAvailableQuantity(Product product, int quantity);

	void addAvailableQuantity(Product product, int quantity);

	void subtractAvailableQuantity(Product product, int quantity, boolean allowNegative);
	
}