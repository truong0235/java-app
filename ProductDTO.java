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

    // --- Getters & Setters ---
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getPic() { return pic; }
    public void setPic(String pic) { this.pic = pic; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPublishYear() { return publishYear; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}