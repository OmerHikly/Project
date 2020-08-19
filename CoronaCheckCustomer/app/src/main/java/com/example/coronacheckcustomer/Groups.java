package com.example.coronacheckcustomer;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;

import java.util.ArrayList;

@Parcel(Parcel.Serialization.BEAN)
public class Groups {
    String Name;
    String GroupCode;
    String OrganizationName;
    String leaderPhone;
    ArrayList<Object> list = new ArrayList <>();     //רשימה שתכיל את כל המשתמשים


    public Groups() {
    }

    @ParcelConstructor
    public Groups(String name, String organizationName, String leaderPhone,String groupCode) {
        Name = name;
        OrganizationName = organizationName;
        this.leaderPhone = leaderPhone;
        this.GroupCode=groupCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLeaderPhone() {
        return leaderPhone;
    }

    public void setLeaderPhone(String leaderPhone) {
        this.leaderPhone = leaderPhone;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }



    public String getGroupCode() {
        return GroupCode;
    }


    public void setGroupCode(String groupCode) {
        GroupCode = groupCode;
    }

    public ArrayList<Object> getList() {
        return list;
    }

    public void setList(ArrayList<Object> list) {
        this.list = list;
    }


}
