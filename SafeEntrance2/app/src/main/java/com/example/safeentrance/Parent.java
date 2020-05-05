package com.example.safeentrance;

import org.parceler.Parcel;

@Parcel
public class Parent {
    String Id;
    String Name;
    boolean Parr;

    public Parent() {
    }

    public Parent(String id, String name, boolean parr) {
        Id = id;
        Name = name;
        Parr = parr;
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

    public boolean isParr() {
        return Parr;
    }

    public void setParr(boolean parr) {
        Parr = parr;
    }
}
