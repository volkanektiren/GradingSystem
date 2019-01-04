package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddUserController {
    PreparedStatement pst = null;
    ResultSet resultSet = null;
    String query = "";

    @FXML
    private MenuItem txtInstructor;
    @FXML
    private MenuItem txtStudent;
    @FXML
    private MenuButton txtUserType;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTel;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtPassword;

    public void add(ActionEvent actionEvent) {

        String name = txtName.getText();
        String surname = txtSurname.getText();
        String email = txtEmail.getText();
        String tel = txtTel.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        if (name.trim().isEmpty() || surname.trim().isEmpty() || email.trim().isEmpty() || tel.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Error!");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in empty fields!");
            alert.show();
        } else {
            try {

                Connection conn = DatabaseConnection.dbConnect();

                if (txtUserType.getText() == txtStudent.getText()) {
                    query = "INSERT INTO students (Name,Surname,Email,Telephone,Username,Password) VALUES (?,?,?,?,?,?)";

                } else if (txtUserType.getText() == txtInstructor.getText()) {
                    query = "INSERT INTO instructors (Name,Surname,Email,Telephone,Username,Password) VALUES (?,?,?,?,?,?)";
                }
                pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, surname);
                pst.setString(3, email);
                pst.setString(4, tel);
                pst.setString(5, username);
                pst.setString(6, password);
                pst.executeUpdate();

                pst.close();
                DatabaseConnection.dbDisconnect(conn);
                AdministratorController.stage.close();

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Form Error!");
                alert.setHeaderText(null);
                alert.setContentText("Please Select User Type!");
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addinstructor(){
        txtUserType.setText(txtInstructor.getText());
    }
    public void addstudent(){ txtUserType.setText(txtStudent.getText()); }
}
