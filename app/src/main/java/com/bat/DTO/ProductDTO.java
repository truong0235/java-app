package com.bat.DTO;

import java.math.BigDecimal;

public class ProductDTO {
    private int productId;
    private String productName;
    private String pic;
    private int categoryId;
    private String publisher;
    private int publishYear;
    private String author;
    private String language;
    private BigDecimal price;
    private int quantity;
    private int status;

    public ProductDTO() {}
    public ProductDTO(int productId, String productName, String pic, int categoryId, String publisher, int publishYear, String author, String language, BigDecimal price, int quantity, int status) {
        this.productId = productId;
        this.productName = productName;
        this.pic = pic;
        this.categoryId = categoryId;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.author = author;
        this.language = language;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getPic() {
        return pic;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public String getAuthor() {
        return author;
    }

    public String getLanguage() {
        return language;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStatus() {
        return status;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // toString
    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", pic='" + pic + '\'' +
                ", categoryId=" + categoryId +
                ", publisher='" + publisher + '\'' +
                ", publishYear=" + publishYear +
                ", author='" + author + '\'' +
                ", language='" + language + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", status=" + status +
                '}';
    }
}
