package spaceio.game.controller;

import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import spaceio.game.MainGame;

import java.net.URL;
import java.util.ResourceBundle;

public class StartGameController extends StackPane implements Initializable {

    private MainGame application;

    public void setApp(MainGame application) {
        this.application = application;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
