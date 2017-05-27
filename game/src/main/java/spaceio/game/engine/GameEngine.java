package spaceio.game.engine;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleCommands;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import spaceio.game.data.GameData;
import spaceio.game.gui.common.DialogPanelControlDefinition;
import spaceio.game.gui.common.MenuButtonControlDefinition;
import spaceio.game.gui.console.ConsolePopupDefinition;
import spaceio.game.gui.intro.IntroScreenDefinition;
import spaceio.game.gui.layer.LayerScreenController;
import spaceio.game.gui.layer.LayerScreenDefinition;
import spaceio.game.gui.menu.MainMenuScreenController;
import spaceio.game.gui.menu.MainMenuScreenDefinition;
import spaceio.game.gui.popup.QuitPopupDefinition;
import spaceio.game.gui.style.HintStyleDefinition;
import spaceio.game.gui.style.TextStyleDefinition;
import spaceio.game.utils.CloseGame;
import spaceio.game.utils.Params;
import spaceio.game.utils.ScreenSettings;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

public class GameEngine extends AbstractAppState {
    private static Logger logger = Logger.getLogger(GameEngine.class.getCanonicalName());
    public SimpleApplication jmonkeyApp;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private Nifty niftyGUI;
    private Node guiNode;
    private Node rootNode;
    private FlyByCamera flyCam;
    private Camera cam;
    private InputManager inputManager;
    private ViewPort guiViewPort;

    private MainMenuScreenController mainMenuScreenController;
    private LayerScreenController layerScreenController;

    private GameData gameData;
    private GameState gameState;
    private CloseGame closeGame;

    private Console console;
    private ConsoleCommands consoleCommands;
    private Element consolePopup;
    private boolean isConsoleBound = false;


    private ActionListener actionListener = (name, keyPressed, tpf) -> {
        if (name.equals("Toggle Full Screen") && !keyPressed)
            changeScreenSize();
        if (name.equals("Toggle console") && !keyPressed && isConsoleBound) {
            this.toggleConsole();
        }
    };

    public void toggleConsole() {
        Screen screen = this.niftyGUI.getCurrentScreen();
        if (screen == null) {
            return;
        }
        if (screen.isActivePopup(this.consolePopup)) {
            screen.closePopup(this.consolePopup, () -> {
            });
        } else {
            this.niftyGUI.showPopup(screen, this.consolePopup.getId(), null);
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        Params.screenHeight = app.getContext().getSettings().getHeight();
        super.initialize(stateManager, app);
        this.jmonkeyApp = (SimpleApplication) app;

        this.loadResources();

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, jmonkeyApp.getAudioRenderer(), guiViewPort);
        guiViewPort.addProcessor(niftyDisplay);
        niftyGUI = niftyDisplay.getNifty();

        this.loadGameData();
        this.loadGameControls();
        this.buildScreens();

        this.niftyGUI.gotoScreen(IntroScreenDefinition.NAME);

        inputManager.setCursorVisible(true);
    }

    private void loadGameControls() {
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        inputManager.deleteMapping("FLYCAM_ZoomIn");
        inputManager.deleteMapping("FLYCAM_ZoomOut");
        inputManager.deleteMapping("FLYCAM_Up");
        inputManager.deleteMapping("FLYCAM_Down");
        inputManager.deleteMapping("FLYCAM_Left");
        inputManager.deleteMapping("FLYCAM_Right");

        inputManager.deleteTrigger("FLYCAM_RotateDrag", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        String[] mappings = {"Toggle Full Screen", "Toggle console", "Quit"};

        Trigger[] triggers = {new KeyTrigger(KeyInput.KEY_F12), new KeyTrigger(KeyInput.KEY_INSERT), new KeyTrigger(KeyInput.KEY_ESCAPE)};

        for (int i = 0; i < mappings.length; i++) {
            inputManager.addMapping(mappings[i], triggers[i]);
            inputManager.addListener(actionListener, mappings[i]);
        }
    }

    @Override
    public void update(float tpf) {
        if (!this.isConsoleBound) {
            this.bindConsole(this.niftyGUI);
            this.isConsoleBound = true;
        }
        if (gameState != null)
            gameState.update(tpf);
    }

    public void playGame(boolean newGame) {
        if (newGame) {
            if (this.gameState == null) {
                logger.info("Starting new game");

                this.gameState = new GameState();
                this.gameState.initialize(stateManager, jmonkeyApp);
            }
        }
    }

    public void changeScreenSize() {
        AppSettings settings = ScreenSettings.generate();

        if (jmonkeyApp.getContext().getSettings().isFullscreen()) {
            settings.setFullscreen(false);
            settings.setResolution(1280, 720);
            Params.screenHeight = 720;
        } else {
            Params.screenHeight = settings.getHeight();
        }

        jmonkeyApp.setSettings(settings);
        jmonkeyApp.restart();
    }

    private void bindConsole(Nifty nifty) {
        this.consolePopup = nifty.createPopup(ConsolePopupDefinition.NAME);
        assert this.consolePopup != null;
        this.console = this.consolePopup.findNiftyControl("console", Console.class);
        assert this.console != null;
        this.console.output("Toggle the console on/off with the F1 key\nEnter 'help' to show all available commands");
        // this is not required when you only want to use the simple console
        // but when you want some support for commands this is how
        consoleCommands = new ConsoleCommands(nifty, console);
        consoleCommands.enableCommandCompletion(true);
        consoleCommands.registerCommand("quit", new QuitCommand());
    }

    private void loadGameData() {
        gameData = GameData.getInstance();
        gameData.setGameEngine(this);
    }

    private void loadResources() {
        stateManager = jmonkeyApp.getStateManager();
        assetManager = jmonkeyApp.getAssetManager();
        inputManager = jmonkeyApp.getInputManager();
        flyCam = jmonkeyApp.getFlyByCamera();
        cam = jmonkeyApp.getCamera();
        guiViewPort = jmonkeyApp.getGuiViewPort();
        guiNode = jmonkeyApp.getGuiNode();
        rootNode = jmonkeyApp.getRootNode();
    }

    private void buildScreens() {
        this.niftyGUI.loadStyleFile("nifty-default-styles.xml");
        this.niftyGUI.loadControlFile("nifty-default-controls.xml");

        IntroScreenDefinition.register(niftyGUI);
        ConsolePopupDefinition.register(niftyGUI);
        QuitPopupDefinition.register(niftyGUI);
        HintStyleDefinition.register(niftyGUI);
        TextStyleDefinition.register(niftyGUI);
        MenuButtonControlDefinition.register(niftyGUI);
        DialogPanelControlDefinition.register(niftyGUI);

        MainMenuScreenDefinition.register(niftyGUI);
        LayerScreenDefinition.register(niftyGUI);

        mainMenuScreenController = (MainMenuScreenController) niftyGUI.getScreen(MainMenuScreenDefinition.NAME).getScreenController();
        mainMenuScreenController.setGameEngine(this);

        layerScreenController = (LayerScreenController) niftyGUI.getScreen(LayerScreenDefinition.NAME).getScreenController();
        layerScreenController.setGameEngine(this);
    }

    public GameData getGameData() {
        return gameData;
    }

    public void showQuitPopup() {
        Screen currentScreen = niftyGUI.getCurrentScreen();

        if (currentScreen == null || this.closeGame != null) {
            return;
        }

        closeGame = new CloseGame(niftyGUI, this);
        closeGame.show();
    }

    public void cancelQuitGame() {
        if (this.closeGame != null) {
            this.closeGame.continueGame();
            this.closeGame = null;
        }
    }

    public void comfirmQuitGame() {
        if (this.closeGame != null) {
            this.closeGame.comfirmQuit();
        }
    }

    private class QuitCommand implements ConsoleCommands.ConsoleCommand {
        @Override
        public void execute(@Nonnull String... args) {
            showQuitPopup();
        }
    }
}
