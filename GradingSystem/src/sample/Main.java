package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.getIcons().add(new Image("sample/estülogo.png"));

        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("login.fxml"));

        Pane myPane = myLoader.load();

        LoginController controller = myLoader.getController();
        controller.setPrevStage(primaryStage);

        primaryStage.setTitle("Eskişehir Technical University Grading System");
        primaryStage.setScene(new Scene(myPane));

        primaryStage.show();

    }

    public static void main(String[] args) { launch(args); }
}
