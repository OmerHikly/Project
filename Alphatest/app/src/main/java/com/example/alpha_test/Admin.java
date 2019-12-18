package com.example.alpha_test;

public class Admin {
    private String Uid;
    private  String Phone;
    private String Id;
    private String Name;
    private String SecondName;
    private String SchoolCode;
    private String Description;
    private String SchoolName;

    private Boolean Activated=false;
    private Integer level=3;


    public Admin( String phone, String ID, String name, String secondName, String schoolCode,String schoolname ) {
        Phone = phone;
        this.Id = ID;
        Name = name;
        SecondName = secondName;
        SchoolCode = schoolCode;
        SchoolName=schoolname;

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

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
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


}
