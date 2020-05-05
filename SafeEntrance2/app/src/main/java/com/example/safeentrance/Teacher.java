package com.example.safeentrance;

import org.parceler.Parcel;

@Parcel
public class Teacher {
    private  String Phone;
    private String Id;
    private String Name;
    private String School;
    private String Cls;
    private String Password;

    private boolean activated=false;

    public Teacher() {
    }
    public Teacher(String name, String id, String school, String cls, String phone, String password) {
        Phone = phone;
        Id = id;
        Name = name;
        School = school;
        Cls = cls;
        Password = password;
    }


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
    }

    public String getCls() {
        return Cls;
    }

    public void setCls(String cls) {
        Cls = cls;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }


}
