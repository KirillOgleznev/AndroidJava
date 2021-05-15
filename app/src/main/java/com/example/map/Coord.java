package com.example.map;

public class Coord {
    int id;
    double lat;
    double lon;

    public Coord() {
        super();
    }

    public Coord(int i, double lat, double lon) {
        super();
        this.id = i;
        this.lat = lat;
        this.lon = lon;
    }

    // constructor
    public Coord(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
