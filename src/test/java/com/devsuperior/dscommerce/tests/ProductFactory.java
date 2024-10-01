package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {
	public static Product createProduct() {
		Category category = CategoryFactory.createdCategory();
		Product product = new Product(1L, "Console Playstation 5", "labore et dolore magna aliqua", 3999.99,
				"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
		product.getCategories().add(category);
		return product;
	}

	public static Product createProduct(String name) {
		Product product = createProduct();
		product.setName(name);
		return product;
	}

}
