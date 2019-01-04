package sample;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdministratorController {

    PreparedStatement pst = null;
    ResultSet resultSet = null;
    String query = "";
    ObservableList<User> data = FXCollections.observableArrayList();
    static Stage stage = null;
    Scene scene = null;

    @FXML
    private TableColumn<User, Integer> columnID;
    @FXML
    private TableColumn<User, String> columnName;
    @FXML
    private TableColumn<User, String> columnSurname;
    @FXML
    private TableColumn<User, String> columnEmail;
    @FXML
    private TableColumn<User, String> columnTel;
    @FXML
    private TableColumn<User, String> columnUsername;
    @FXML
    private TableColumn<User, String> columnPassword;
    @FXML
    private TableView<User> table;
    @FXML
    private Label txtLabel;

    public void getStudents() {
        getinfo("Student");
        txtLabel.setText("Students");
    }

    public void getInstructors() {
        getinfo("Instructor");
        txtLabel.setText("Instructors");
    }

    public void getinfo(String infotype) {
        try {
            Connection conn = DatabaseConnection.dbConnect();
            query = "Select * From " + infotype + "s";
            pst = conn.prepareStatement(query);
            resultSet = pst.executeQuery();
            data = FXCollections.observableArrayList();
            columnID.setCellValueFactory(new PropertyValueFactory<User, Integer>("ID"));
            columnName.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
            columnSurname.setCellValueFactory(new PropertyValueFactory<User, String>("surname"));
            columnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
            columnTel.setCellValueFactory(new PropertyValueFactory<User, String>("telephone"));
            columnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
            columnPassword.setCellValueFactory(new PropertyValueFactory<User, String>("password"));

            while (resultSet.next()) {
                int ID = resultSet.getInt(infotype + "ID");
                String name = resultSet.getString("Name");
                String surname = resultSet.getString("Surname");
                String email = resultSet.getString("Email");
                String tel = resultSet.getString("Telephone");
                String username = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                data.add(new User(ID, name, surname, email, tel, username, password));
            }
            table.setItems(data);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addButton() {
        try {
            Pane myPane = FXMLLoader.load(getClass().getResource("adduser.fxml"));
            scene = new Scene(myPane);
            stage = new Stage();
            stage.setTitle("Add User");
            stage.getIcons().add(new Image("sample/estülogo.png"));
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateButton(){
        try{
            TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            User updateUser = table.getItems().get(index);
            Stage updateStage = new Stage();
            updateStage.setTitle("Update User");
            updateStage.getIcons().add(new Image("sample/estülogo.png"));
            TextField textName = new TextField(updateUser.getName());
            TextField textSurname = new TextField(updateUser.getSurname());
            TextField textEmail = new TextField(updateUser.getEmail());
            TextField textTel = new TextField(updateUser.getTelephone());
            TextField textUsername = new TextField(updateUser.getUsername());
            TextField textPassword = new TextField(updateUser.getPassword());
            Button update = new Button("Update");
            update.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String name = textName.getText();
                    String surname = textSurname.getText();
                    String email = textEmail.getText();
                    String tel = textTel.getText();
                    String username = textUsername.getText();
                    String password = textPassword.getText();
                    if (name.trim().isEmpty() || surname.trim().isEmpty() || email.trim().isEmpty() || tel.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Form Error!");
                        alert.setHeaderText(null);
                        alert.setContentText("Please fill in empty fields!");
                        alert.show();
                    }else {
                        try {
                            Connection conn = DatabaseConnection.dbConnect();
                            if (txtLabel.getText() == "Students") {
                                query = "Update students Set Name = ? , Surname=? , Email=? , Telephone=? , Username=? , Password=? " +
                                        "where StudentID =" + updateUser.getID();
                            } else if (txtLabel.getText() == "Instructors") {
                                query = "Update instructors Set Name = ?  , Surname=? , Email=? , Telephone=? , Username=? , Password=? " +
                                        "where InstructorID =" + updateUser.getID();
                            }
                            pst = conn.prepareStatement(query);
                            pst.setString(1, textName.getText());
                            pst.setString(2, textSurname.getText());
                            pst.setString(3, textEmail.getText());
                            pst.setString(4, textTel.getText());
                            pst.setString(5, textUsername.getText());
                            pst.setString(6, textPassword.getText());
                            pst.executeUpdate();
                            DatabaseConnection.dbDisconnect(conn);
                            updateStage.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            VBox updateBox = new VBox();
            updateBox.getChildren().addAll(textName,textSurname,textEmail,textTel,textUsername,textPassword,update);
            Scene scene = new Scene(updateBox, 300,300);
            updateStage.setScene(scene);
            updateStage.initModality(Modality.APPLICATION_MODAL);
            updateStage.show();
        } catch(IndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(" Please Select a User!");
            alert.show();
        }

    }
    public void deleteButton(){
        try {
            Connection conn = DatabaseConnection.dbConnect();
            TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
            int index = pos.getRow();
            User selected = table.getItems().get(index);
            int ID = selected.getID();
            if(txtLabel.getText() == "Students"){
                query = "DELETE FROM students WHERE StudentID=" + ID;
            }
            else if(txtLabel.getText() == "Instructors"){
                query = "DELETE FROM instructors WHERE InstructorID=" + ID;
            }
            pst = conn.prepareStatement(query);
            pst.executeUpdate();
            DatabaseConnection.dbDisconnect(conn);
            table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
        } catch (IndexOutOfBoundsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Select a User!");
            alert.show();
        } catch(MySQLIntegrityConstraintViolationException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("You cannot delete this user until not delete this user's connected course or grade!");
            alert.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}