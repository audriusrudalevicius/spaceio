package spaceio.game;

import com.jme3.app.SimpleApplication;
import spaceio.game.engine.GameEngine;
import spaceio.game.utils.ScreenSettings;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SpaceGameApplication extends SimpleApplication {

    public static void main(String[] args) {
        SpaceGameApplication application = new SpaceGameApplication();
        application.setSettings(ScreenSettings.generate());
        application.setShowSettings(false);
        java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
        application.setPauseOnLostFocus(false);
        application.start();
    }

    @Override
    public void simpleInitApp() {
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.ALL);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.ALL);

        disableJmonkeyHUD();
        stateManager.attach(new GameEngine());
    }

    private void disableJmonkeyHUD() {
        setDisplayFps(false);
        setDisplayStatView(false);
    }

    @Override
    public void destroy() {
        stop();
        System.exit(0);
    }
}
