package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class InstructorController {

    PreparedStatement pst = null;
    ResultSet resultSet = null;
    String query = "";
    Stage stage=null;
    Scene scene=null;
    static Course course = null;

    @FXML
    private TextArea txtResult;
    @FXML
    private TextArea txtResult2;
    @FXML
    private Button btnShowCourses;
    @FXML
    private Button btnAddCourse;
    @FXML
    private Button btnDeleteCourse;
    @FXML
    private Button btnShowGrades;
    @FXML
    private Button btnChangeGrade;
    @FXML
    private Button btnShowStudentGrades;
    @FXML
    private TableColumn<Course , Integer> columnCourseID;
    @FXML
    private TableColumn<Course , String> columnCourseName;
    @FXML
    private TableColumn<Course , Integer> columnCourseQuota;
    @FXML
    private TableView<Course> tableCourses;
    @FXML
    private TableView<CourseGrade> tableGrades;
    @FXML
    private TableColumn<CourseGrade , Integer> columnStudentID;
    @FXML
    private TableColumn<CourseGrade , String> columnStudentName;
    @FXML
    private TableColumn<CourseGrade , String> columnStudentSurname;
    @FXML
    private TableColumn<CourseGrade , Integer> columnStudentGrade;

    public void getCourses(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            query = "SELECT * FROM courses";
            pst = conn.prepareStatement(query);
            resultSet = pst.executeQuery();
            ObservableList<Course> data = FXCollections.observableArrayList();
            columnCourseID.setCellValueFactory(new PropertyValueFactory<Course, Integer>("ID"));
            columnCourseName.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
            columnCourseQuota.setCellValueFactory(new PropertyValueFactory<Course, Integer>("quota"));

            while (resultSet.next()) {
                int ID = resultSet.getInt("CourseID");
                String name = resultSet.getString("Name");
                int quota = resultSet.getInt("Quota");
                data.add(new Course(ID, name, quota));
            }
            tableCourses.setItems(data);
            DatabaseConnection.dbDisconnect(conn);
            txtResult.setText("All Courses are on screen.");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void addCourse(ActionEvent actionEvent){
        stage = new Stage();
        stage.setTitle("Add Course");
        stage.getIcons().add(new Image("sample/estülogo.png"));
        TextField txtCourseName = new TextField();
        txtCourseName.setPromptText("Course Name");
        TextField txtCourseQuota = new TextField();
        txtCourseQuota.setPromptText("Course Quota");
        Button add = new Button("Add");
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    Connection conn = DatabaseConnection.dbConnect();
                    query = "INSERT INTO courses (Name,Quota,InstructorID) VALUES (?,?,?)";
                    pst = conn.prepareStatement(query);
                    pst.setString(1,txtCourseName.getText());
                    pst.setInt(2,Integer.valueOf(txtCourseQuota.getText()));
                    pst.setInt(3, LoginController.user.getID());
                    pst.executeUpdate();
                    pst.close();
                    DatabaseConnection.dbDisconnect(conn);
                    stage.close();
                    txtResult.setText("New Course("+txtCourseName.getText()+") is added. Please click 'Show All Grades' button to refresh the table.");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        VBox addBox = new VBox();
        addBox.getChildren().addAll(txtCourseName,txtCourseQuota,add);
        Scene scene = new Scene(addBox, 300,90);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
    public void deleteCourse(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            TablePosition pos = tableCourses.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            Course selected = tableCourses.getItems().get(index);
            int ID = selected.getID();
            query = "DELETE FROM courses WHERE CourseID=" + ID;
            pst = conn.prepareStatement(query);
            pst.executeUpdate();
            DatabaseConnection.dbDisconnect(conn);
            tableCourses.getItems().removeAll(tableCourses.getSelectionModel().getSelectedItem());
            txtResult.setText("A course("+selected.getName()+") is deleted.");
        }catch(IndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(" Please Select a Course!");
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showGrades(){
        try {
            TablePosition pos = tableCourses.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            course = tableCourses.getItems().get(index);
            Pane myPane = FXMLLoader.load(getClass().getResource("showgrades.fxml"));
            scene = new Scene(myPane);
            stage = new Stage();
            stage.setTitle(course.getName());
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

    //Grade Screen Method
    public void showStudentGrades(ActionEvent actionEvent){
        try {
            Connection conn = DatabaseConnection.dbConnect();
            query = "Select S.StudentID , S.Name , S.Surname , G.Grade From students S,grades G where G.CourseID=? and S.StudentID = G.StudentID";
            pst = conn.prepareStatement(query);
            pst.setInt(1,course.getID());
            resultSet = pst.executeQuery();
            ObservableList<CourseGrade> data = FXCollections.observableArrayList();
            columnStudentID.setCellValueFactory(new PropertyValueFactory<CourseGrade,Integer>("ID"));
            columnStudentName.setCellValueFactory(new PropertyValueFactory<CourseGrade,String>("studentName"));
            columnStudentSurname.setCellValueFactory(new PropertyValueFactory<CourseGrade,String>("studentSurname"));
            columnStudentGrade.setCellValueFactory(new PropertyValueFactory<CourseGrade,Integer>("studentGrade"));

            while (resultSet.next()) {
                int ID = resultSet.getInt("StudentID");
                String name = resultSet.getString("Name");
                String surname = resultSet.getString("Surname");
                int grade = resultSet.getInt("Grade");
                data.add(new CourseGrade(ID,name,surname,grade));
            }
            tableGrades.setItems(data);
            DatabaseConnection.dbDisconnect(conn);
            txtResult2.setText("Grades of "+course.getName()+" are on screen.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void changeGrade(ActionEvent actionEvent){
        try{
            TablePosition pos = tableGrades.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            CourseGrade updateGrade = tableGrades.getItems().get(index);
            Stage changeGrade = new Stage();
            changeGrade.getIcons().add(new Image("sample/estülogo.png"));
            TextField textGrade = new TextField(String.valueOf(updateGrade.getStudentGrade()));
            Button change = new Button("Change");
            change.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{
                        Connection conn = DatabaseConnection.dbConnect();
                        query = "Update grades Set Grade=? where StudentID =? and CourseID=?";
                        pst = conn.prepareStatement(query);
                        pst.setInt(1,Integer.valueOf(textGrade.getText()));
                        pst.setInt(2,updateGrade.getID());
                        pst.setInt(3,course.getID());
                        pst.executeUpdate();
                        DatabaseConnection.dbDisconnect(conn);
                        changeGrade.close();
                        txtResult2.setText("A student's("+updateGrade.getStudentName()+" grade is changed. Please click 'Show Grades' button to refresh the table.");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            VBox updateBox = new VBox();
            updateBox.getChildren().addAll(textGrade,change);
            Scene scene = new Scene(updateBox, 100,60);
            changeGrade.setScene(scene);
            changeGrade.initModality(Modality.APPLICATION_MODAL);
            changeGrade.show();
        }catch(IndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Select a Record!");
            alert.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void showMyGrades(ActionEvent actionEvent){
        try{
            Connection conn = DatabaseConnection.dbConnect();
            query = "SELECT * FROM courses where InstructorID=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1,LoginController.user.getID());
            resultSet = pst.executeQuery();
            ObservableList<Course> data = FXCollections.observableArrayList();
            columnCourseID.setCellValueFactory(new PropertyValueFactory<Course, Integer>("ID"));
            columnCourseName.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
            columnCourseQuota.setCellValueFactory(new PropertyValueFactory<Course, Integer>("quota"));

            while (resultSet.next()) {
                int ID = resultSet.getInt("CourseID");
                String name = resultSet.getString("Name");
                int quota = resultSet.getInt("Quota");
                data.add(new Course(ID, name, quota));
            }
            tableCourses.setItems(data);
            DatabaseConnection.dbDisconnect(conn);
            txtResult.setText("All Courses are on screen.");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
