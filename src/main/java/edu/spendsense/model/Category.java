package edu.spendsense.model;

public class Category {
    private final int categoryId;
    private final String categoryName;
    private final String categoryType;

    public Category(int categoryId, String categoryName, String categoryType) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    public int getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getCategoryType() { return categoryType; }

    @Override
    public String toString() {
        return categoryName + " (" + categoryType + ")";
    }
}
