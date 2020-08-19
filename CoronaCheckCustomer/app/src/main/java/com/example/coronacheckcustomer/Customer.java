package com.example.coronacheckcustomer;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel

public class Customer {
        String Phone;
        String Id;
        String Name;
        String Password;
        String FirstName;


    ArrayList<Groups>Groups= new ArrayList<>();
    ArrayList<Child>children= new ArrayList<>();


    public Customer() {
        }


    public Customer(String phone, String id, String name, String password,String firstname, ArrayList<Groups> groups, ArrayList<Child> children){
        Phone = phone;
        Id = id;
        Name = name;
        Password = password;
        FirstName=firstname;
        Groups = groups;
        this.children = children;
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

        public String getPassword() {
            return Password;
        }


    public ArrayList<com.example.coronacheckcustomer.Groups> getGroups() {
        return Groups;
    }

    public void setGroups(ArrayList<com.example.coronacheckcustomer.Groups> groups) {
        Groups = groups;
    }

    public ArrayList<Child> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
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


        public void setPassword(String password) {
            Password = password;
        }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }





}



