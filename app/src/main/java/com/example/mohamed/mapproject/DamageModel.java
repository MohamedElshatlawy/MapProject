package com.example.mohamed.mapproject;

import java.io.Serializable;

public class DamageModel implements Serializable {
    private String id;
    private String locName;
    private String damage;
    private double lat;
    private double lng;
    private String date;
    private String company;

    public DamageModel() {
    }

    public DamageModel(String locName, String damage, double lat, double lng,String date,String company) {

        this.locName = locName;
        this.damage = damage;
        this.lat = lat;
        this.lng = lng;
        this.date=date;
        this.company=company;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
