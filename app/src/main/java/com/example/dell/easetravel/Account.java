package com.example.dell.easetravel;

public class Account {

    String username;
    String email;
    String pass;

    public Account()
    {
        username = email = pass = "";
    }

    public void setAccount(String n_user, String n_name, String n_pass)
    {
        username = n_user;
        email = n_name;
        pass = n_pass;
    }
    public String getUsername()
    {
        return username;
    }
}
