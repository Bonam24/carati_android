package com.example.carati;

public class modelJustToGetExpiryDate {

    String expirydate;
    //model to get just the expiry date from the database
    public modelJustToGetExpiryDate() {
    }

    public modelJustToGetExpiryDate(String expirydate) {
        this.expirydate = expirydate;
    }

    public String getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(String expirydate) {
        this.expirydate = expirydate;
    }
}
