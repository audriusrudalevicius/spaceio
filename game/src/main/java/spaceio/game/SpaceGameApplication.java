package spaceio.game;

import com.jme3.app.BasicProfilerState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.OptionPanelState;
import com.simsilica.lemur.anim.AnimationState;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.state.DebugHudState;
import spaceio.game.engine.GameEngine;
import spaceio.game.utils.GameConstants;
import spaceio.game.utils.ScreenSettings;

public class SpaceGameApplication extends SimpleApplication {

    public SpaceGameApplication() {
        super(
                new AnimationState(),
                new OptionPanelState(),
                new DebugHudState(),
                new BasicProfilerState(),
                new DebugKeysAppState(),
                new ScreenshotAppState("", System.currentTimeMillis())
        );
    }

    public static void main(String[] args) {
        SpaceGameApplication application = new SpaceGameApplication();
        application.setSettings(ScreenSettings.generate());
        application.setShowSettings(false);

        application.setPauseOnLostFocus(false);
        application.start();
    }

    @Override
    public void simpleInitApp() {
        disableJmonkeyHUD();
        GuiGlobals.initialize(this);
        GuiGlobals globals = GuiGlobals.getInstance();
        BaseStyles.loadGlassStyle();
        globals.getStyles().setDefaultStyle(GameConstants.DEFAULT_GUI_STYLE);

        stateManager.attach(new GameEngine());
    }

    @Override
    public void update() {
        super.update();
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
