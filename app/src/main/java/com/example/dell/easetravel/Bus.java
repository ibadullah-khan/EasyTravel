package com.example.dell.easetravel;

public class Bus {

    String number;
    String route;
    String capacity;

    public Bus()
    {
        number = route = capacity = "";
    }

    public void setBus(String n_num, String n_route, String n_cap)
    {
        number = n_num;
        route = n_route;
        capacity = n_cap;
    }
    public void updateBusInfo(String n_num, String n_route, String n_cap)
    {
        number = n_num;
        route = n_route;
        capacity = n_cap;
    }
}
