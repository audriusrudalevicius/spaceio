package spaceio.game.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.event.EventBus;
import de.lessvoid.nifty.Nifty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.core.event.GameSessionEvent;
import spaceio.game.SpaceGameApplication;
import spaceio.game.gui.intro.IntroScreenDefinition;
import spaceio.game.scene.MainMenuSceneState;
import spaceio.game.utils.Params;
import spaceio.game.utils.ScreenSettings;

public class GameEngine extends AbstractAppState {
    private static Logger logger = LoggerFactory.getLogger(GameEngine.class);
    public SpaceGameApplication jmonkeyApp;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private Nifty niftyGUI;
    private Node guiNode;
    private Node rootNode;
    private FlyByCamera flyCam;
    private Camera cam;
    private InputManager inputManager;
    private ViewPort guiViewPort;
    private Node overlayRoot = new Node("Overlay Root");
    private Node toolsRoot = new Node("Tools Root");

    private ActionListener actionListener = (name, keyPressed, tpf) -> {
        if (name.equals("Toggle Full Screen") && !keyPressed)
            changeScreenSize();
    };

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        logger.info("initialize");
        EventBus.addListener(new GameListener(), GameSessionEvent.sessionStarted, GameSessionEvent.sessionEnded);

        Params.screenHeight = app.getContext().getSettings().getHeight();
        super.initialize(stateManager, app);
        this.jmonkeyApp = (SpaceGameApplication) app;

        loadResources();
        loadRoots();

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, jmonkeyApp.getAudioRenderer(), guiViewPort);
        guiViewPort.addProcessor(niftyDisplay);
        niftyGUI = niftyDisplay.getNifty();

        loadGameControls();

        inputManager.setCursorVisible(false);

        if (Params.showIntro) {
            IntroScreenDefinition.register(() -> {
                niftyGUI.gotoScreen(null);
                this.loadMainMenu();
                return null;
            }, niftyGUI);

            niftyGUI.gotoScreen(IntroScreenDefinition.NAME);
        } else {
            this.loadMainMenu();
        }
    }

    private void loadRoots() {
        ViewPort view = jmonkeyApp.getRenderManager().createPostView("Overlay", cam);
        view.attachScene(overlayRoot);
        jmonkeyApp.getViewPort().attachScene(toolsRoot);
    }

    private void loadMainMenu() {
        stateManager.attach(new MainMenuSceneState());
        inputManager.setCursorVisible(true);
    }

    private void loadGameControls() {
        inputManager.deleteMapping(SpaceGameApplication.INPUT_MAPPING_EXIT);

        String[] mappings = {"Toggle Full Screen"};

        Trigger[] triggers = {new KeyTrigger(KeyInput.KEY_F12), new KeyTrigger(KeyInput.KEY_INSERT), new KeyTrigger(KeyInput.KEY_ESCAPE)};

        for (int i = 0; i < mappings.length; i++) {
            inputManager.addMapping(mappings[i], triggers[i]);
            inputManager.addListener(actionListener, mappings[i]);
        }
    }

    public void changeScreenSize() {
        AppSettings settings = ScreenSettings.generate();

        settings.setFullscreen(!settings.isFullscreen());

        if (jmonkeyApp.getContext().getSettings().isFullscreen()) {
            settings.setResolution(Params.defaultScreenWidth, Params.defaultScreenHeight);
            Params.screenHeight = Params.defaultScreenHeight;
        } else {
            Params.screenHeight = settings.getHeight();
        }

        jmonkeyApp.setSettings(settings);
        jmonkeyApp.restart();
        jmonkeyApp.reshape(settings.getWidth(), settings.getHeight());
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

    @Override
    public void update(float tpf) {
        overlayRoot.updateLogicalState(tpf);
        toolsRoot.updateLogicalState(tpf);
        super.update(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        overlayRoot.updateGeometricState();
        toolsRoot.updateGeometricState();
        super.render(rm);
    }

    public Node getOverlayRoot() {
        return overlayRoot;
    }

    public Node getToolsRoot() {
        return toolsRoot;
    }

    private class GameListener {
        public void sessionStarted(GameSessionEvent event) {
            logger.info("Game started");
        }

        public void sessionEnded(GameSessionEvent event) {
            logger.info("Game ended");
        }
    }
}
