package com.example.dell.easetravel;

public class Locationn {

    double latitude;
    double longitude;

    public Locationn()
    {
        latitude = longitude = 0;
    }

    public void setLocation(double lat, double longi)
    {
        latitude = lat;
        longitude = longi;
    }

    public double getLat()
    {
        return latitude;
    }

    public double getLongi()
    {
        return longitude;
    }
}
