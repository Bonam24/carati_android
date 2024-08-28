package com.example.carati;

public class Model_Category {
    String id,category,uid;
    long timestamp;

    public Model_Category() {
    }
    //constructor
    public Model_Category(String id, String category, String uid) {
        this.id = id;
        this.category = category;
        this.uid = uid;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
