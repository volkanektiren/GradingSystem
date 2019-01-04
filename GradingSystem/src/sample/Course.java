package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Course {
    private final SimpleIntegerProperty ID = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleIntegerProperty quota = new SimpleIntegerProperty();

    public Course() {
        this(0,"",0);
    }
    public Course(int ID , String name , int quota){
        setID(ID);
        setName(name);
        setQuota(quota);
    }

    public int getID() {
        return ID.get();
    }

    public SimpleIntegerProperty IDProperty() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getQuota() {
        return quota.get();
    }

    public SimpleIntegerProperty quotaProperty() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota.set(quota);
    }
}
