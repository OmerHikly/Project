package com.example.coronacheckcustomer;

import java.util.ArrayList;


import org.parceler.Parcel;

@Parcel
public class Child {
    String Name;
    String ParentPhone;
    String School;
    String cls;
    ArrayList<Groups> groups = new ArrayList<>();//רשימה שתדע להכיל בהתאם את מי שאושר ואת מי שלא בהתאם לכפתור שיילחץ



    public Child() {
    }

    public Child(String name,String school,String clas,ArrayList<Groups> groups,String PP) {
        Name = name;
        School=school;
        cls=clas;
        this.groups = groups;
        ParentPhone=PP;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<Groups> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Groups> groups) {
        this.groups = groups;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getParentPhone() {
        return ParentPhone;
    }

    public void setParentPhone(String parentPhone) {
        ParentPhone = parentPhone;
    }


}



