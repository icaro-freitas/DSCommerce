package com.devsuperior.dscommerce.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order_item")
public class OrderItem {

	@EmbeddedId
	private OrderItemPK id = new OrderItemPK();

	private Integer quantitiy;
	private Double price;

	public OrderItem() {
	}

	public OrderItem(Order order, Product product, Integer quantitiy, Double price) {
		this.id.setOrder(order);
		this.id.setProduct(product);
		this.quantitiy = quantitiy;
		this.price = price;
	}

	public Order getOrder() {
		return this.id.getOrder();
	}

	public void setOrder(Order order) {
		this.id.setOrder(order);
	}

	public Product getProduct() {
		return id.getProduct();
	}

	public void setProduct(Product product) {
		this.id.setProduct(product);
	}

	public Integer getQuantitiy() {
		return quantitiy;
	}

	public void setQuantitiy(Integer quantitiy) {
		this.quantitiy = quantitiy;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
