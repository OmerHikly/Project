package com.example.alpha_test;

import org.parceler.Parcel;

@Parcel
public class Student {
    private String Uid;
    private  String Phone;
    private String Id;
    private String Name;
    private String SecondName;
    private String School;
    private String Description;

    private String Cls;
    private String Password;





    private Boolean Activated=false;

    public Student() {
    }

    public Student(String name, String secondName, String id, String school, String cls, String phone, String password ) {

        Phone = phone;
        Id = id;
        Name = name;
        SecondName = secondName;
        School = school;
        Cls = cls;
        Password = password;

    }
    public Boolean getActivated() {
        return Activated;
    }

    public void setActivated(Boolean activated) {
        Activated = activated;
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


}
