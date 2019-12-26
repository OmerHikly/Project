package com.example.alpha_test;

public class Guard {
    private String Uid;
    private  String Phone;
    private String Id;
    private String Name;
    private String SecondName;
    private String SchoolCode;
    private String Description;
    private String SchoolName;
    private String Password;
    private String GuardCode;


    public Guard( String name, String secondName, String schoolCode, String id, String schoolName, String phone, String password, String guardCode) {
        Phone = phone;
        Id = id;
        Name = name;
        SecondName = secondName;
        SchoolCode = schoolCode;
        SchoolName = schoolName;
        Password = password;
        GuardCode = guardCode;
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

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getGuardCode() {
        return GuardCode;
    }

    public void setGuardCode(String guardCode) {
        GuardCode = guardCode;
    }
}
