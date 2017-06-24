package com.planezy.planezyuserapp;

/**
 * Created by Gavine Joyce on 23/06/2017.
 */

public class User {
    private String name="";
    private String email="";
    private String phone="";
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public User(){

    }
}
