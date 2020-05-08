package com.example.safeentrance;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;


@Parcel(Parcel.Serialization.BEAN)
public class Student {
  String Phone;
  String Id;
  String Name;
  String School;
  String Cls;
  String Password;
  Parent parent1;
  Parent parent2;

 boolean activated=false;

    public Student() {
    }

    @ParcelConstructor
    public Student(String name, String id, String school, String cls, String phone, String password, Parent parent1, Parent parent2) {
        Phone = phone;
        Id = id;
        Name = name;
        School = school;
        Cls = cls;
        Password = password;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }



    public String getPhone() {
        return Phone;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getSchool() {
        return School;
    }

    public String getCls() {
        return Cls;
    }

    public String getPassword() {
        return Password;
    }

    public boolean isActivated() {
        return activated;
    }
    public Parent getParent1() {
        return parent1;
    }

    public Parent getParent2() {
        return parent2;
    }



    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setSchool(String school) {
        School = school;
    }

    public void setCls(String cls) {
        Cls = cls;
    }

    public void setPassword(String password) {
        Password = password;
    }


    public void setParent1(Parent parent1) {
        this.parent1 = parent1;
    }

    public void setParent2(Parent parent2) {
        this.parent2 = parent2;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }


}
