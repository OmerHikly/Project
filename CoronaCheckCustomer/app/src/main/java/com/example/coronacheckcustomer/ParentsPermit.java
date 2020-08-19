package com.example.coronacheckcustomer;


import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class ParentsPermit {
    String DateAndTime;
    String Url;
    String Qr_Info;
    boolean ParentType;


    public ParentsPermit() {
    }

    public ParentsPermit(String dateAndTime, String url, String qr_Info, boolean pare) {
        DateAndTime = dateAndTime;
        Url = url;
        Qr_Info = qr_Info;
        ParentType = pare;
    }



    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }


    public String getQr_Info() {
        return Qr_Info;
    }

    public void setQr_Info(String qr_Info) {
        Qr_Info = qr_Info;
    }

    public boolean whichParent() {
        return ParentType;
    }

    public void setParent(boolean parent) {
        ParentType = parent;
    }
}