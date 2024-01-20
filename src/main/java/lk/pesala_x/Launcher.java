package lk.pesala_x;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/serverForm.fxml"))));
        stage.getIcons().add(new Image("/assets/CHAT-X 1.png"));
        stage.setResizable(false);
        stage.setTitle("Server");
        stage.centerOnScreen();
        stage.show();
        System.out.println("Programme started");
    }
}
