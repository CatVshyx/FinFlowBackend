package com.example.FinFlow.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@Entity
@JsonFilter("productFilter")
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Integer id;

    private String productName;
    private float price;
    private long amount;
    private Category category;
    private String supplier;
    // orphanRemoval means that after deleting this entity it`s connection disables and that entity also removes
    @JsonIgnore
    private int sold;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval=true)
    @JoinColumn(name = "discount_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Discount discount;

    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Long company;
    public Product(String productName, float price, long amount, Category category) {
        this.productName = productName;
        this.price = price;
        this.amount = amount;
        this.category = category;
    }

    public Product(){}
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {

        this.productName = productName;
    }

    public float getPrice() {
        return price;
    }

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", category=" + category +
                ", supplier='" + supplier + '\'' +
                ", sold=" + sold +
                ", discount=" + discount +
                ", company=" + company +
                '}';
    }
}
