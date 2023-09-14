package com.example.restaurantapp.models;

public class    ModelProduct {
    private String productID,productTitle,productDescription,productQuantity,productCategory,productIcon,productprice,timestamp,uid;

    public ModelProduct() {
    }

    public ModelProduct(String productID, String productTitle, String productDescription, String productQuantity, String productCategory, String productIcon, String productprice, String timestamp, String uid){
        this.productID = productID;

        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productQuantity = productQuantity;
        this.productCategory = productCategory;
        this.productIcon = productIcon;
        this.productprice = productprice;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getProductprice() {
        return productprice;
    }

    public void setProductprice(String productprice) {
        this.productprice = productprice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
