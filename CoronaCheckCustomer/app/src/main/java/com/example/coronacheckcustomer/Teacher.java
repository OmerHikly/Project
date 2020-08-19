package com.example.coronacheckcustomer;

import org.parceler.Parcel;

@Parcel
public class Teacher {
    String Uid;
    String Phone;
    String Id;
    String Name;
    String SecondName;
    String School;


    String Cls;
    String Password;

    Boolean Activated=false;


    public Teacher() {
    }

    public Teacher(String name, String secondName, String id, String school, String cls, String phone, String password) {
        Phone = phone;
        Id = id;
        Name = name;
        SecondName = secondName;
        School = school;
        Cls = cls;
        Password = password;
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

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
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

    public String getSecondName() {
        return SecondName;
    }

    public void setSecondName(String secondName) {
        SecondName = secondName;
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

    public Boolean getActivated() {
        return Activated;
    }

    public void setActivated(Boolean activated) {
        Activated = activated;
    }
}
