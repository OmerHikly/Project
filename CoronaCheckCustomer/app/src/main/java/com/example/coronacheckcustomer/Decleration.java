package com.example.coronacheckcustomer;


    public class Decleration {
        Customer Declarant;
        Child TheDeclaredChild;
        String Organiztion;
        String GroupCode;
        String DateAndHour;


        public Decleration() {
        }

        public Decleration(Customer declarant, Child thedeclaredchild, String organiztion, String groupCode, String dateandhour) {
            Declarant = declarant;
            TheDeclaredChild = thedeclaredchild;
            Organiztion = organiztion;
            GroupCode = groupCode;
            DateAndHour = dateandhour;

        }


        public Customer getDeclarant() {
            return Declarant;
        }

        public void setDeclarant(Customer declarant) {
            Declarant = declarant;
        }

        public Child getTheDeclaredChild() {
            return TheDeclaredChild;
        }

        public void setTheDeclaredChild(Child theDeclaredChild) {
            TheDeclaredChild = theDeclaredChild;
        }

        public String getOrganiztion() {
            return Organiztion;
        }

        public void setOrganiztion(String organiztion) {
            Organiztion = organiztion;
        }

        public String getGroupCode() {
            return GroupCode;
        }

        public void setGroupCode(String groupCode) {
            GroupCode = groupCode;
        }

        public String getDateAndHour() {
            return DateAndHour;
        }

        public void setDateAndHour(String dateAndHour) {
            DateAndHour = dateAndHour;
        }

    }


