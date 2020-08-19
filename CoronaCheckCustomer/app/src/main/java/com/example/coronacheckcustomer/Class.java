package com.example.coronacheckcustomer;

import com.example.coronacheckcustomer.Child;

import java.util.ArrayList;

public class Class {
    String Name;
    String EducatorPhone;
    ArrayList<Child>list=new ArrayList<>();

    public Class(String name, String educatorPhone, ArrayList<Child> list) {
        Name = name;
        EducatorPhone = educatorPhone;
        this.list = list;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEducatorPhone() {
        return EducatorPhone;
    }

    public void setEducatorPhone(String educatorPhone) {
        EducatorPhone = educatorPhone;
    }

    public ArrayList<Child> getList() {
        return list;
    }

    public void setList(ArrayList<Child> list) {
        this.list = list;
    }
}
