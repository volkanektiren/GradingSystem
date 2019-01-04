package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CourseGrade {
    private final SimpleIntegerProperty ID = new SimpleIntegerProperty();
    private final SimpleStringProperty studentName = new SimpleStringProperty("");
    private final SimpleStringProperty studentSurname = new SimpleStringProperty("");
    private final SimpleIntegerProperty studentGrade = new SimpleIntegerProperty();

    public CourseGrade() {
        this(0,"","",0);
    }

    public CourseGrade(int ID,String studentName,String studentSurname,int studentGrade){
        setID(ID);
        setStudentName(studentName);
        setStudentSurname(studentSurname);
        setStudentGrade(studentGrade);
    }

    public String getStudentSurname() {
        return studentSurname.get();
    }

    public SimpleStringProperty studentSurnameProperty() {
        return studentSurname;
    }

    public void setStudentSurname(String studentSurname) {
        this.studentSurname.set(studentSurname);
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

    public String getStudentName() {
        return studentName.get();
    }

    public SimpleStringProperty studentNameProperty() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName.set(studentName);
    }

    public int getStudentGrade() {
        return studentGrade.get();
    }

    public SimpleIntegerProperty studentGradeProperty() {
        return studentGrade;
    }

    public void setStudentGrade(int studentGrade) {
        this.studentGrade.set(studentGrade);
    }
}
