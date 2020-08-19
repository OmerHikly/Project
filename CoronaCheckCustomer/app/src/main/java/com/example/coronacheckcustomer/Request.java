package com.example.coronacheckcustomer;

public class Request {

    String ParentPhone;
    String ParentName;
    Child child;
    String GroupName;

    public Request() {
    }


    public Request(String parentPhone, String parentName, Child child,String groupName) {
        ParentPhone = parentPhone;
        ParentName = parentName;
        this.child=child;
        GroupName =groupName;
    }

    public String getParentPhone() {
        return ParentPhone;
    }

    public void setParentPhone(String parentPhone) {
        ParentPhone = parentPhone;
    }

    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child childName) {
        child = childName;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
}
