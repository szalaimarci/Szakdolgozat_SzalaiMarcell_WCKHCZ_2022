package com.example.szakdoga;

import android.util.Log;

import java.util.Date;

public class Items {
    private String barcode;
    private String city;
    private String street;
    private Date date;
    private String date2;
    private String price;
    private boolean reported;

    public Items() {}

    public Items(String price, String city, String street, String date2, boolean reported) {
        this.price = price;
        this.city = city;
        this.street = street;
        this.date2 = date2;
        this.reported = reported;
    }

    public Items(String barcode, String city, String street, Date date, String price, boolean reported ) {
        this.barcode = barcode;
        this.city = city;
        this.street = street;
        this.date = date;
        this.price = price;
        this.reported = reported;
    }


    public String getCity() {
        return city;
    }
    public String getStreet(){
        return street;
    }
    public Date getDate() {
        return date;
    }
    public String getDate2() {
        return date2;
    }
    public String getPrice() {
        return price;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public boolean isReported() {
        return reported;
    }
    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
