package lk.pesala_x.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.pesala_x.ClientUtil.ClientHandler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class ServerFormController implements Initializable {

    @FXML
    private ProgressBar progressLoad;

    @FXML
    private Button sendbtn;

    @FXML
    private TextField typetxt;

    Socket socket;
    DataInputStream dataInputStream;
    BufferedReader reader;
    DataOutputStream dataOutputStream;
    ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    public static ArrayList<String> Allname = new ArrayList<>();
    static String name;

    @FXML
    void sendbtnonAction(ActionEvent actionEvent) {
        name = typetxt.getText();
        Allname.add(name);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/clientForm.fxml"));
        Parent root1 = null;
        ClientFormController controller = new ClientFormController();
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fxmlLoader.setController(controller);
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.setTitle("Client");

        root1.setOnMouseClicked(event -> {
            System.out.println("Window clicked!");
        });

        stage.show();
        typetxt.clear();
    }

    @FXML
    void typetxtOnActon(ActionEvent event) {
        sendbtnonAction(event);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                if (serverSocket == null) {
                    serverSocket = new ServerSocket(3001);
                }
                while (!serverSocket.isClosed()) {
                    socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket, clients);
                    clients.add(clientHandler);
                    System.out.println("Client connected");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
