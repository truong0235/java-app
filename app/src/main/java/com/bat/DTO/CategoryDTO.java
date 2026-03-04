package com.bat.DTO;

public class CategoryDTO {
    private int categoryId;
    private String categoryName;
    private String description;
    private int status;

    public CategoryDTO() {}

    public CategoryDTO(int categoryId, String categoryName, String description, int status) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.status = status;
    }

    // --- Getters & Setters ---
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}