package com.example.safeentrance;

public class ParentsPermit {
    String DateAndTime;
    String Url;
    String Qr_Info;
    boolean Parent;


    public ParentsPermit() {
    }

    public ParentsPermit(String dateAndTime, String url, String qr_Info, boolean parent) {
        DateAndTime = dateAndTime;
        Url = url;
        Qr_Info = qr_Info;
        Parent = parent;
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
        return Parent;
    }

    public void setParent(boolean parent) {
        Parent = parent;
    }
}
