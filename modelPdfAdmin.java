package com.example.carati;

public class modelPdfAdmin {
    String id, uid,name, category, author,description,price, url;
    long timestamp, viewsCount;

    public modelPdfAdmin() {
    }
    //constructor to get books from the database
    public modelPdfAdmin(String id, String uid, String name, String category, String author, String description, String price, String url, long timestamp, long viewsCount) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.category = category;
        this.author = author;
        this.description = description;
        this.price = price;
        this.url = url;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }
}
