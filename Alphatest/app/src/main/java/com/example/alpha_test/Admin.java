package com.example.alpha_test;

import org.parceler.Parcel;

@Parcel
public class Admin {
    private String Uid;
    private  String Phone;
    private String Id;
    private String Name;
    private String SecondName;
    private String School;
    private String Description;

    private String Password;







    private Boolean Activated=false;

    public Admin() {
    }

    public Admin(String name, String secondName, String ID, String schoolCode, String phone, String password ) {
        Password=password;
        Phone = phone;
        this.Id = ID;
        Name = name;
        SecondName = secondName;
        School = schoolCode;


    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getID() {
        return Id;
    }

    public void setID(String ID) {
        this.Id = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSecondName() {
        return SecondName;
    }

    public void setSecondName(String secondName) {
        SecondName = secondName;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String schoolCode) {
        School = schoolCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Boolean getActivated() {
        return Activated;
    }

    public void setActivated(Boolean activated) {
        Activated = activated;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }



}
