package spaceio.game.scene;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.game.SpaceGameApplication;
import spaceio.game.engine.ConnectionState;
import spaceio.game.engine.HostState;
import spaceio.game.gui.common.DialogHelpers;
import spaceio.game.utils.GameConstants;

public class MainMenuSceneState extends BaseAppState {
    static Logger log = LoggerFactory.getLogger(MainMenuSceneState.class);

    private Container mainWindow;

    private TextField connectHost;
    private TextField connectPort;

    private TextField hostPort;
    private TextField hostDescription;

    protected void showError( String title, String error ) {
        getState(OptionPanelState.class).show(title, error);
    }

    protected int parseInt( String title, String sInt ) {
        sInt = sInt.trim();
        if( sInt.length() == 0 ) {
            showError(title + " Error", "Please specify a port.\nDefault is " + GameConstants.DEFAULT_PORT);
            return -1;
        }
        try {
            return Integer.parseInt(sInt);
        } catch( Exception e ) {
            log.error("Error parsing port:" + sInt, e);
            showError(title + " Error", "Invalid port:" + sInt + "\n"
                    + e.getClass().getSimpleName() + ":" + e.getMessage());
            return -1;
        }
    }

    protected void debugScene() {
        log.info("Debug scene selected");
        getStateManager().attach(new DebugGameSceneState());
        setEnabled(false);
    }

    protected void connect() {
        log.info("Connect... host:" + connectHost.getText() + "  port:" + connectPort.getText());

        // Validate the parameters
        String host = connectHost.getText().trim();
        if( host.length() == 0 ) {
            showError("Connect Error", "Please specify a host.");
            return;
        }
        int port = parseInt("Connect", connectPort.getText());

        // Add the state that will manage our remote connection and create
        // the game states and so on.
        getStateManager().attach(new ConnectionState(this, host, port));

        // Disable ourselves
        setEnabled(false);
    }

    protected void host() {
        log.info("Host a game on port:" + hostPort.getText());
        log.info("Description:");
        log.info(hostDescription.getText());

        // Validate the parameters
        int port = parseInt("Hosting", hostPort.getText());

        try {
            // Add the state to manage the hosting environment.  It will launch
            // a self-connecting ConnectionState on its own.
            getStateManager().attach(new HostState(port, hostDescription.getText()));

            // Disable ourselves
            setEnabled(false);
        } catch( RuntimeException e ) {
            log.error("Error attaching host state", e);
            String message = "Error hosting game on port:" + port;
            Throwable cause = e.getCause();
            if( cause != null ) {
                message += "\n" + cause.getClass().getSimpleName() + ":" + cause.getMessage();
            }
            showError("Hosting", message);
        }
    }

    @Override
    protected void initialize(Application app) {
        mainWindow = new Container();
        Label title = mainWindow.addChild(new Label("Space.io"));
        title.setFontSize(32);
        title.setInsets(new Insets3f(10, 10, 0, 10));

        ActionButton debugScene = mainWindow.addChild(new ActionButton(new CallMethodAction("Debug Game", this, "debugScene")));
        debugScene.setInsets(new Insets3f(10, 10, 10, 10));

        ActionButton exit = mainWindow.addChild(new ActionButton(new CallMethodAction("Exit Game", app, "stop")));
        exit.setInsets(new Insets3f(10, 10, 10, 10));


        Container props;

        Container joinPanel = mainWindow.addChild(new Container());
        joinPanel.setInsets(new Insets3f(10, 10, 10, 10));
        joinPanel.addChild(new Label("Join a Network Server", new ElementId("title")));

        props = joinPanel.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Last)));
        props.setBackground(null);
        props.addChild(new Label("Connect to host:"));
        connectHost = props.addChild(new TextField("localhost"), 1);
        props.addChild(new Label("On port:"));
        connectPort = props.addChild(new TextField(String.valueOf(GameConstants.DEFAULT_PORT)), 1);
        joinPanel.addChild(new ActionButton(new CallMethodAction("Connect", this, "connect")));

        Container hostPanel = mainWindow.addChild(new Container());
        hostPanel.setInsets(new Insets3f(10, 10, 10, 10));
        hostPanel.addChild(new Label("Host a Game Server", new ElementId("title")));

        props = hostPanel.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Last)));
        props.setBackground(null);
        props.addChild(new Label("Host on port:"));
        hostPort = props.addChild(new TextField(String.valueOf(GameConstants.DEFAULT_PORT)), 1);

        hostPanel.addChild(new Label("Server Description"));
        hostDescription = hostPanel.addChild(new TextField("This a game server.\nThere are many like it\nbut this one is mine."));
        hostDescription.setSingleLine(false);
        hostPanel.addChild(new ActionButton(new CallMethodAction("Begin Hosting", this, "host")));
        DialogHelpers.scaleDialog(app, mainWindow);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        Node gui = ((SpaceGameApplication)getApplication()).getGuiNode();
        gui.attachChild(mainWindow);
        GuiGlobals.getInstance().requestFocus(mainWindow);
    }

    @Override
    protected void onDisable() {
        mainWindow.removeFromParent();
    }
}
