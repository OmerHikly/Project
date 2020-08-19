import com.example.coronacheckcustomer.Child;
import com.example.coronacheckcustomer.Customer;

public class Decleration {
    Customer Declarant;
    Child TheDeclaredChild;
    String Organiztion;
    String Group;
    String DateAndHour;

    public Decleration() {
    }

    public Decleration(Customer declarant, Child thedeclaredchild, String organiztion, String group, String dateandhour) {
        Declarant = declarant;
        TheDeclaredChild = thedeclaredchild;
        Organiztion = organiztion;
        Group = group;
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

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public String getDateAndHour() {
        return DateAndHour;
    }

    public void setDateAndHour(String dateAndHour) {
        DateAndHour = dateAndHour;
    }
}
