package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    PreparedStatement pst = null;
    ResultSet resultSet = null;
    String query = "";
    Stage prevStage;
    static User user=null;

    @FXML
    private TextField txtUserName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private MenuButton txtLoginType;
    @FXML
    private MenuItem txtAdmin;
    @FXML
    private MenuItem txtInstructor;
    @FXML
    private MenuItem txtStudent;

    public void login() {

        String username = txtUserName.getText();
        String password = txtPassword.getText();


        try {
            Connection conn = DatabaseConnection.dbConnect();
            boolean studentFlag = txtLoginType.getText() == txtStudent.getText();
            if (txtLoginType.getText() == txtAdmin.getText()){
                query = "SELECT * FROM admins WHERE Username=? AND Password=?";
            }
            else if (txtLoginType.getText() == txtStudent.getText()){
                query = "SELECT * FROM students WHERE Username=? AND Password=?";
            }
            else if (txtLoginType.getText() == txtInstructor.getText()) {
                query = "SELECT * FROM instructors WHERE Username=? AND Password=?";
            }

            pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            resultSet = pst.executeQuery();

            if(resultSet.next()) {
                if(txtLoginType.getText() == txtStudent.getText()){
                    int ID = resultSet.getInt("StudentID");
                    String name = resultSet.getString("Name");
                    String surname = resultSet.getString("Surname");
                    String email = resultSet.getString("Email");
                    String tel = resultSet.getString("Telephone");
                    user = new User(ID,name,surname,email,tel,username,password);
                }else if(txtLoginType.getText() == txtInstructor.getText()){
                    int ID = resultSet.getInt("InstructorID");
                    String name = resultSet.getString("Name");
                    String surname = resultSet.getString("Surname");
                    String email = resultSet.getString("Email");
                    String tel = resultSet.getString("Telephone");
                    user = new User(ID,name,surname,email,tel,username,password);
                }
                showmainview();
            } else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Username or password wrong!");
                alert.show();
            }
            pst.close();
            resultSet.close();
            DatabaseConnection.dbDisconnect(conn);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Select Login Type!");
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loginadmin(){ txtLoginType.setText(txtAdmin.getText()); }
    public void logininstructor(){ txtLoginType.setText(txtInstructor.getText()); }
    public void loginstudent(){ txtLoginType.setText(txtStudent.getText()); }

    public void setPrevStage(Stage stage){ this.prevStage = stage; }

    public void showmainview(){
        try {
            Pane myPane = FXMLLoader.load(getClass().getResource((txtLoginType.getText() + ".fxml")));
            Scene scene = new Scene(myPane);
            Stage stage = new Stage();
            stage.setTitle(txtLoginType.getText() + " Page");
            stage.getIcons().add(new Image("sample/estülogo.png"));
            stage.setScene(scene);
            prevStage.close();
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
