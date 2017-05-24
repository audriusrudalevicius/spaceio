package spaceio.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.brainless.alchemist.view.ViewPlatform;
import spaceio.game.controller.LoginController;
import spaceio.game.controller.StartGameController;
import spaceio.game.model.User;

import java.io.InputStream;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainGame extends Application {
    static final Logger logger = Logger.getLogger(MainGame.class.getName());
    protected final double MINIMUM_WINDOW_WIDTH = 390.0;
    protected final double MINIMUM_WINDOW_HEIGHT = 500.0;
    protected Stage stage;
    private User loggedUser;

    public static void main(String[] args) throws ParseException {
        Application.launch(MainGame.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            stage.setTitle("Coolest game ever ;-)");
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

            try {
                LoginController login = (LoginController) this.loadScene("/ui/Login.fxml");
                login.setApp(this);
            } catch (Exception ex) {
                Logger.getLogger(MainGame.class.getName()).log(Level.SEVERE, null, ex);
            }

            stage.show();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (Exception ex) {
            Logger.getLogger(MainGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public boolean userLogging(String userId, String password) {
        this.loggedUser = User.of(userId);
        this.startGame();
        return true;
    }

    private void startGame() {
        try {
            StartGameController ctl = (StartGameController) this.loadScene("/ui/StartGame.fxml");
            ctl.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(MainGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected Initializable loadScene(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = MainGame.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(MainGame.class.getResource(fxml));
        Pane page;
        try {
            page = (Pane) loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, 800, 600);
        ViewPlatform.JavaFXScene.setValue(scene);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }
}
