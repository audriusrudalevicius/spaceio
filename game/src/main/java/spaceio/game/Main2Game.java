package spaceio.game;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import de.lessvoid.nifty.Nifty;
import spaceio.core.data.DataLoader;
import spaceio.game.gui.common.DialogPanelControlDefinition;
import spaceio.game.gui.common.MenuButtonControlDefinition;
import spaceio.game.gui.console.ConsolePopupDefinition;
import spaceio.game.gui.menu.MainMenuScreenDefinition;
import spaceio.game.gui.style.HintStyleDefinition;
import spaceio.game.gui.style.TextStyleDefinition;

import javax.swing.*;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main2Game extends SimpleApplication {
    private static PrintStream console;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Node mapNode;
    private Node uniNode;
    private Spatial skyGeo;
    private boolean start = true;
    private ScreenshotAppState ss;

    public Main2Game() {
        super();
        this.showSettings = false;
    }

    public static void main(String[] args) {
        Main2Game app = new Main2Game();
        app.start();
    }

    public void log(String message) {
        if (console != null) {
            String[] s = getClass().getName().split("\\.");
            message = s[(s.length - 1)] + ": " + message.replace('_', ' ');
            console.println(message);
            if (!console.equals(System.err)) {
                System.err.println(message);
            }
        }
    }

    @Override
    public void simpleInitApp() {

        console = System.err;
        if (Integer.parseInt(org.lwjgl.opengl.GL11.glGetString(7938).split("\\.")[0]) < 2) {
            log("OpenGL 2.0 needed");
            JOptionPane.showMessageDialog(null, "OpenGL 2.0 needed", "Error", 0);
            throw new RuntimeException("OpenGL 2.0");
        }
        log("Starting game...");

        this.cam.setFrustumFar(200000.0F);
        this.flyCam.setEnabled(false);

        this.assetManager.registerLoader(DataLoader.class, "dat");

        this.niftyDisplay = new NiftyJmeDisplay(this.assetManager, this.inputManager, this.audioRenderer, this.guiViewPort);
        this.nifty = this.niftyDisplay.getNifty();

        this.initGUI();

        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.ALL);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.ALL);

        this.mapNode = new Node("MapNode");
        this.uniNode = new Node("UniverseNodeNode");

        this.rootNode.attachChild(this.uniNode);

        this.viewPort.detachScene(this.rootNode);
        this.viewPort.attachScene(this.uniNode);

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        log("Class paths: " + Arrays.stream(((URLClassLoader) cl).getURLs()).map(URL::toString).collect(Collectors.joining(", ")));
    }

    @Override
    public void simpleUpdate(float tpf) {

        if (this.start) {
            this.ss = new ScreenshotAppState();
            this.stateManager.attach(this.ss);

            this.attachSky();

            this.nifty.gotoScreen(MainMenuScreenDefinition.NAME);
            this.guiViewPort.addProcessor(this.niftyDisplay);

            this.start = false;
        }
    }

    private void attachSky() {
        String skyName = "Textures/Skies/sky.dds";
        this.skyGeo = SkyFactory.createSky(this.assetManager, skyName, SkyFactory.EnvMapType.CubeMap);
        this.uniNode.attachChild(this.skyGeo);
    }

    private void initGUI() {

        this.nifty.loadStyleFile("nifty-default-styles.xml");
        this.nifty.loadControlFile("nifty-default-controls.xml");

        ConsolePopupDefinition.register(nifty);
        HintStyleDefinition.register(nifty);
        TextStyleDefinition.register(nifty);
        MenuButtonControlDefinition.register(nifty);
        DialogPanelControlDefinition.register(nifty);
        MainMenuScreenDefinition.register(nifty);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void update() {
        try {
            super.update();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
