package spaceio.launcher.controller;

import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import spaceio.launcher.Launcher;

import java.net.URL;
import java.util.ResourceBundle;

public class StartGameController extends StackPane implements Initializable {

    private Launcher application;

    public void setApp(Launcher application) {
        this.application = application;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
