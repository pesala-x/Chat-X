package lk.pesala_x.controller;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {

    @FXML
    private Button Camerabtn;

    @FXML
    private Button emojibtn;

    @FXML
    private VBox messagContainer;

    @FXML
    private Text nametxt;

    @FXML
    private Text namset;

    @FXML
    private Text namtext;

    @FXML
    private ScrollPane pane;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button sendbtn;

    @FXML
    private Text showNameBar;

    @FXML
    private TextField typetxt;

    @FXML
    private VBox vbox;
    static boolean openWindow = false;
    private static final double paneHeight = 500;

    public static String name;

    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    BufferedReader reader;
    public String nam;
    public Text nameSet;
    public int count = 1;
    public String image;
    public ClientFormController(){
    }
    @FXML
    void typetxtOnActon(ActionEvent event) throws IOException {
        sendbtnonAction(event);
    }

    public void sendbtnonAction(ActionEvent actionEvent) throws IOException {
        printName();
        new Thread(() -> {
            String reply;
            try {
                reply = typetxt.getText();
                if(reply!=null) {
                    dataOutputStream.writeUTF(reply);
                    dataOutputStream.flush();
                    appendMessageMe("Me :"+ reply, "-fx-border-color: #11D2E5; -fx-background-color: #34E9FA; -fx-background-radius: 20px 0px 20px 20px; -fx-border-radius: 20px 0px 20px 20px;");
                }
                typetxt.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        namset.setText(ServerFormController.Allname.get(0));
        showNameBar.setText(ServerFormController.Allname.get(0));
        namtext.setText(ServerFormController.name);
        closePane();
        new Thread(() -> {
            try {
                if(socket==null) {
                    socket = new Socket("localhost", 3001);
                }
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                openWindow = true;
                System.out.println(ServerFormController.name);

            } catch (IOException e) {

                e.printStackTrace();
            }

            String message;
            System.out.println(ServerFormController.Allname.size());
            while (true) {
                if(ServerFormController.Allname.size()>=1){
                    for(int i = count; i<ServerFormController.Allname.size(); i++){
                        namset.setText(namset.getText()+","+ServerFormController.Allname.get(i));
                        showNameBar.setText(namset.getText());
                        count++;
                    }
                }
                try {
                    message = dataInputStream.readUTF();
                    if(message!= null) {
                        image = message;
                        System.out.println("Server: " + message);
                        appendMessage(name +" :"+ message, "-fx-border-color: #CF76FF; -fx-background-color: #CF76FF; -fx-background-radius: 0px 20px 20px 20px; -fx-border-radius: 0px 20px 20px 20px;");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        try {
            Thread.sleep(500);
            pane.setVisible(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String message, String style) {
        if (message.matches(".*\\.(png|jpe?g|gif)$")) {
            Platform.runLater(() -> {
                try {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(105);
                    imageView.setPreserveRatio(true);

                    VBox imageContainer = new VBox(imageView);
                    imageContainer.setAlignment(Pos.CENTER_LEFT);
                    imageContainer.setPadding(new Insets(10));
                    imageContainer.setSpacing(10);

                    Label textLabel = new Label(name+" : ");
                    textLabel.setWrapText(true);
                    textLabel.setAlignment(Pos.CENTER_LEFT);
                    VBox.setMargin(textLabel, new Insets(0, 10, 0, 0));

                    HBox imageBox = new HBox(textLabel, imageContainer);
                    imageBox.setAlignment(Pos.CENTER_LEFT);
                    imageBox.setSpacing(10);

                    messagContainer.getChildren().add(imageBox);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }else {
            Platform.runLater(() -> {
                Label messageLabel = new Label(message);
                messageLabel.setWrapText(true);
                messageLabel.setPadding(new Insets(10));
                messageLabel.setStyle(style);

                HBox messageContainer = new HBox(messageLabel);
                messageContainer.setAlignment(Pos.TOP_LEFT);
                messageContainer.setPadding(new Insets(10));
                messageContainer.setFillHeight(true);

                Label timeLabel = new Label(getCurrentTime());
                timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;");
                timeLabel.setPadding(new Insets(0, 0, 5, 0));
                HBox.setHgrow(timeLabel, Priority.ALWAYS);

                VBox chatBubble = new VBox(timeLabel, messageContainer);
                chatBubble.setAlignment(Pos.TOP_LEFT);
                chatBubble.setPadding(new Insets(5));

                messagContainer.getChildren().add(chatBubble);
            });
        }
    }
    private void appendMessageMe(String message, String style) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            //messageLabel.setPrefWidth(200);
            messageLabel.setPadding(new Insets(10));
            messageLabel.setStyle(style);

            messageLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

            HBox messageContainer = new HBox(messageLabel);
            messageContainer.setAlignment(Pos.TOP_RIGHT);
            messageContainer.setPadding(new Insets(10));
            messageContainer.setFillHeight(true);

            Label timeLabel = new Label(getCurrentTime());
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;");
            timeLabel.setPadding(new Insets(0, 0, 5, 0));
            HBox.setHgrow(timeLabel, Priority.ALWAYS);

            VBox chatBubble = new VBox(timeLabel, messageContainer);
            chatBubble.setAlignment(Pos.TOP_RIGHT);
            chatBubble.setPadding(new Insets(5));

            messagContainer.getChildren().add(chatBubble);
        });
    }

    public void emojibtnonAction(ActionEvent actionEvent) throws AWTException {
        if(openWindow==true){
            openPane();
            typetxt.requestFocus();
            openWindow = false;
        }else {
            closePane();
            openWindow = true;
        }
    }

    private String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    public void CamerabtnonAction(ActionEvent actionEvent) {
        printName();
        FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory()+dialog.getFile();
        dialog.dispose();
        System.out.println(file + " chosen.");
        if (file != null) {
            sendFileToServer(file);
            displayFileInScrollPane(file);
        }
    }

    public void gringbigeyesOnAction(ActionEvent actionEvent) {
        String emojiCode = "U+1f603";
        System.out.println("Button clicked. Emoji code: " + emojiCode);
        typetxt.appendText(convertEmojiCode(emojiCode));
    }
    public void gringfacesmileonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f604"));
    }

    public void smillyonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f642"));
    }

    public void upsidedownonaction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f643"));
    }

    public void facewithtearsjoyonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f602"));
    }

    public void rollingfacewithtearsjoyonAction(ActionEvent actionEvent) { typetxt.appendText(convertEmojiCode("U+1f923")); }

    public void vinkifaceonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f609"));
    }

    public void savoringfoodonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f60B"));
    }

    public void StarStruckOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F60D"));
    }

    public void KissingFaceOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F617"));
    }

    public void FaceBlowingaKissOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F618"));
    }

    public void SmilingFacewithHeartsOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F970"));
    }
    public void HeartEyesOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F970"));
    }

    public void FaceScreaminginFearOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F631"));
    }

    public void SmirkingFaceOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F60F"));
    }

    public void UnamusedFaceOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1F612"));
    }

/*
😀 - Grinning Face (U+1F600)
😃 - Grinning Face with Big Eyes (U+1F603)
😄 - Grinning Face with Smiling Eyes (U+1F604)
😁 - Beaming Face with Smiling Eyes (U+1F601)
😆 - Grinning Squinting Face (U+1F606)
😅 - Grinning Face with Sweat (U+1F605)
😂 - Face with Tears of Joy (U+1F602)
🤣 - Rolling on the Floor Laughing (U+1F923)
😊 - Smiling Face with Smiling Eyes (U+1F60A)
😇 - Smiling Face with Halo (U+1F607)
😉 - Winking Face (U+1F609)
😌 - Relieved Face (U+1F60C)
😍 - Heart Eyes (U+1F60D)
😘 - Face Blowing a Kiss (U+1F618)
😗 - Kissing Face (U+1F617)
😙 - Kissing Face with Smiling Eyes (U+1F619)
😚 - Kissing Face with Closed Eyes (U+1F61A)
☺️ - Smiling Face (U+263A)
🙂 - Slightly Smiling Face (U+1F642)
🙃 - Upside-Down Face (U+1F643)
😋 - Face Savoring Food (U+1F60B)
😛 - Face with Tongue (U+1F61B)
😜 - Winking Face with Tongue (U+1F61C)
🤪 - Zany Face (U+1F92A)
😝 - Squinting Face with Tongue (U+1F61D)
🤑 - Money-Mouth Face (U+1F911)
🤗 - Hugging Face (U+1F917)
🤔 - Thinking Face (U+1F914)
🤐 - Zipper-Mouth Face (U+1F910)
😐 - Neutral Face (U+1F610)
😑 - Expressionless Face (U+1F611)
😶 - Face Without Mouth (U+1F636)
😏 - Smirking Face (U+1F60F)
😒 - Unamused Face (U+1F612)
🙄 - Face with Rolling Eyes (U+1F644)
😬 - Grimacing Face (U+1F62C)
😮 - Face with Open Mouth (U+1F62E)
😯 - Hushed Face (U+1F62F)
😧 - Anguished Face (U+1F627)
😨 - Fearful Face (U+1F628)
😰 - Anxious Face with Sweat (U+1F630)
😱 - Face Screaming in Fear (U+1F631)
😳 - Flushed Face (U+1F633)
😵 - Dizzy Face (U+1F635)
😡 - Pouting Face (U+1F621)
😠 - Angry Face (U+1F620)
🤬 - Face with Symbols on Mouth (U+1F92C)
😷 - Face with Medical Mask (U+1F637)
🤒 - Face with Thermometer (U+1F912)
🤕 - Face with Head-Bandage (U+1F915)
*/

    private void openPane() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);
        transition.setToY(0);
        transition.play();
    }
    private void closePane() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);
        transition.setToY(paneHeight);
        transition.play();
    }

    private String convertEmojiCode(String emojiCode) {
        int codePoint = Integer.parseInt(emojiCode.substring(2), 16);
        return new String(Character.toChars(codePoint));
    }

    private void sendFileToServer(String file) {
        try {
            dataOutputStream.writeUTF(file);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//371*549

    private void displayFileInScrollPane(String file) {
        Platform.runLater(() -> {
            try {
                ImageView imageView = new ImageView(file);
                imageView.setFitWidth(105);
                imageView.setPreserveRatio(true);

                VBox imageContainer = new VBox(imageView);
                imageContainer.setAlignment(Pos.CENTER_RIGHT);
                imageContainer.setPadding(new Insets(10));
                imageContainer.setSpacing(10);

                Label textLabel = new Label("Me : ");
                textLabel.setWrapText(true);
                textLabel.setAlignment(Pos.CENTER_RIGHT);
                VBox.setMargin(textLabel, new Insets(0, 10, 0, 0));

                HBox imageBox = new HBox(textLabel, imageContainer);
                imageBox.setAlignment(Pos.CENTER_RIGHT);
                imageBox.setSpacing(10);

                messagContainer.getChildren().add(imageBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void printName(){
        name = namtext.getText();
    }
}
