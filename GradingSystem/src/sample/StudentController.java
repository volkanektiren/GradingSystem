package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentController {

    PreparedStatement pst = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    ResultSet resultSet = null;
    ResultSet resultSet2 = null;
    ResultSet resultSet3 = null;
    String query = "";
    String query2 = "";
    String query3 = "";
    Stage stage=null;
    Scene scene=null;
    static Course selectedCourse = null;


    @FXML
    private TableView<Course> tableStudentCourses;
    @FXML
    private TableColumn<Course,Integer> columnCourseID;
    @FXML
    private TableColumn<Course,String> columnCourseName;
    @FXML
    private TableColumn<Course,Integer> columnGrade;
    @FXML
    private Button btnregistercourse;
    @FXML
    private Button btnshowstudentcourses;
    @FXML
    private Button btnshowgrades;

    public void registerCourse(ActionEvent actionEvent){
        try {
            Pane myPane = FXMLLoader.load(getClass().getResource("registercourse.fxml"));
            scene = new Scene(myPane);
            stage = new Stage();
            stage.setTitle("Register a Course");
            stage.getIcons().add(new Image("sample/estülogo.png"));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getStudentCourses(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            query = "SELECT G.CourseID , C.Name , G.Grade FROM grades G , courses C where G.StudentID=? and C.CourseID=G.CourseID";
            pst = conn.prepareStatement(query);
            pst.setInt(1,LoginController.user.getID());
            resultSet = pst.executeQuery();
            ObservableList<Course> data = FXCollections.observableArrayList();
            columnCourseID.setCellValueFactory(new PropertyValueFactory<Course,Integer>("ID"));
            columnCourseName.setCellValueFactory(new PropertyValueFactory<Course,String>("name"));
            columnGrade.setCellValueFactory(new PropertyValueFactory<Course,Integer>("quota"));
            while(resultSet.next()){
                int ID = resultSet.getInt("CourseID");
                String coursename = resultSet.getString("Name");
                int grade = resultSet.getInt("Grade");
                data.add(new Course(ID,coursename,grade));
            }
            tableStudentCourses.setItems(data);
            DatabaseConnection.dbDisconnect(conn);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCourseGrades(ActionEvent actionEvent){
        try{
            TablePosition pos = tableStudentCourses.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            selectedCourse = tableStudentCourses.getItems().get(index);
            Pane myPane = FXMLLoader.load(getClass().getResource("coursegrades.fxml"));
            scene = new Scene(myPane);
            stage = new Stage();
            stage.setTitle(selectedCourse.getName());
            stage.getIcons().add(new Image("sample/estülogo.png"));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }catch (IndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Select a Course!");
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Register course page
    @FXML
    private TableView<Course> tableCourses;
    @FXML
    private TableColumn<Course,Integer> colCourseID;
    @FXML
    private TableColumn<Course,String> colCourseName;
    @FXML
    private TableColumn<Course,Integer> colCourseQuota;
    @FXML
    private Button btnshowcourses;
    @FXML
    private Button btnregister;

    public void getCourses(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            query = "SELECT * FROM courses";
            pst = conn.prepareStatement(query);
            resultSet = pst.executeQuery();
            ObservableList<Course> data = FXCollections.observableArrayList();
            colCourseID.setCellValueFactory(new PropertyValueFactory<Course, Integer>("ID"));
            colCourseName.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
            colCourseQuota.setCellValueFactory(new PropertyValueFactory<Course, Integer>("quota"));

            while (resultSet.next()) {
                int ID = resultSet.getInt("CourseID");
                String name = resultSet.getString("Name");
                int quota = resultSet.getInt("Quota");
                data.add(new Course(ID, name, quota));
            }
            tableCourses.setItems(data);
            DatabaseConnection.dbDisconnect(conn);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void register(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            TablePosition pos = tableCourses.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            Course selected = tableCourses.getItems().get(index);
            query="Select * From grades where StudentID=? and CourseID=?";
            pst= conn.prepareStatement(query);
            pst.setInt(1,LoginController.user.getID());
            pst.setInt(2,selected.getID());
            resultSet = pst.executeQuery();
            if (resultSet.next()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("You had registered that course!");
                alert.show();
            }else{
                query2="Select * From grades where CourseID=?";
                pst2 = conn.prepareStatement(query2);
                pst2.setInt(1,selected.getID());
                resultSet2 = pst2.executeQuery();
                int quotaiterator = 0;
                while (resultSet2.next()) quotaiterator++;
                if (quotaiterator>=selected.getQuota()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("That courses quota is already fulled");
                    alert.show();
                }else{
                    query3 = "INSERT INTO grades (StudentID , Grade, CourseID) VALUES (?,NULL,?)";
                    pst3 = conn.prepareStatement(query3);
                    pst3.setInt(1,LoginController.user.getID());
                    pst3.setInt(2,selected.getID());
                    pst3.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("You successfully registered selected course...");
                    alert.show();
                }
            }
            DatabaseConnection.dbDisconnect(conn);
        }catch(IndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Select a Course!");
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Course Grades Screen
    @FXML
    private TableView<CourseGrade> tableCourseGrades;
    @FXML
    private TableColumn<CourseGrade,Integer> cStudentID;
    @FXML
    private TableColumn<CourseGrade,String> cName;
    @FXML
    private TableColumn<CourseGrade,String> cSurname;
    @FXML
    private TableColumn<CourseGrade,Integer> cGrade;
    @FXML
    private Button btnshowcoursegrades;
    @FXML
    private PieChart chart;
    @FXML TextArea chartResult;

    public void showCourseGrades(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            query = "SELECT S.StudentID , S.Name , S.Surname , G.Grade FROM students S , grades G WHERE CourseID=? and S.StudentID = G.StudentID";
            pst = conn.prepareStatement(query);
            pst.setInt(1,selectedCourse.getID());
            resultSet = pst.executeQuery();
            ObservableList<CourseGrade> data = FXCollections.observableArrayList();
            cStudentID.setCellValueFactory(new PropertyValueFactory<CourseGrade, Integer>("ID"));
            cName.setCellValueFactory(new PropertyValueFactory<CourseGrade, String>("studentName"));
            cSurname.setCellValueFactory(new PropertyValueFactory<CourseGrade, String>("studentSurname"));
            cGrade.setCellValueFactory(new PropertyValueFactory<CourseGrade, Integer>("studentGrade"));

            while (resultSet.next()) {
                int ID = resultSet.getInt("StudentID");
                String name = resultSet.getString("Name");
                String surname = resultSet.getString("Surname");
                int Grade = resultSet.getInt("Grade");
                data.add(new CourseGrade(ID, name, surname , Grade));
            }
            tableCourseGrades.setItems(data);
            //Pie Chart
            int pie1 = 0;
            int pie2 = 0;
            int pie3 = 0;
            int pie4 = 0;
            int pie5 = 0;
            resultSet = pst.executeQuery();
            while(resultSet.next()){
                int Grade = resultSet.getInt("Grade");
                if (Grade >= 81) ++pie5;
                else if (Grade >= 61) ++pie4;
                else if (Grade >= 41) ++pie3;
                else if (Grade >= 21) ++pie2;
                else ++pie1;
            }
            ObservableList<PieChart.Data> chartData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("0-20", pie1),
                            new PieChart.Data("21-40", pie2),
                            new PieChart.Data("41-60", pie3),
                            new PieChart.Data("61-80", pie4),
                            new PieChart.Data("81-100", pie5));
            chart.setData(chartData);
            chartResult.setText("0-20     = " + pie1 +
                    " students\n21-40   = " + pie2 +
                    " students\n41-60   = " + pie3 +
                    " students\n61-80   = " + pie4 +
                    " students\n81-100 = " + pie5 + " students");
            DatabaseConnection.dbDisconnect(conn);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
