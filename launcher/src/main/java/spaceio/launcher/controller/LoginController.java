package spaceio.launcher.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import spaceio.launcher.Launcher;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends AnchorPane implements Initializable {
    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    Button login;
    @FXML
    Label errorMessage;
    @FXML
    VBox vbox;

    private Launcher application;

    private static double xOffset = 0;
    private static double yOffset = 0;

    public void setApp(Launcher application){
        this.application = application;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setText("");
        userId.setPromptText("demo");
        password.setPromptText("demo");

        this.vbox.setOnMousePressed(event -> {
            xOffset = application.getStage().getX() - event.getScreenX();
            yOffset = application.getStage().getY() - event.getScreenY();
        });

        this.vbox.setOnMouseDragged(event -> {
            application.getStage().setX(event.getScreenX() + xOffset);
            application.getStage().setY(event.getScreenY() + yOffset);
        });
    }

    public void processLogin(ActionEvent event) {
        if (application == null){
            errorMessage.setText("Hello " + userId.getText());
        } else {
            if (!application.userLogging(userId.getText(), password.getText())){
                errorMessage.setText("Username/Password is incorrect");
            }
        }
    }
}
