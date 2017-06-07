package spaceio.game.view.common;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.lwjgl.opengl.Display;
import spaceio.game.SpaceGameApplication;
import spaceio.game.gui.common.CustomPicture;

import javax.annotation.Nonnull;

public class Hud extends AbstractAppState implements ScreenController {

    private SpaceGameApplication app;
    private AppStateManager sm;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SpaceGameApplication) app;
        this.sm = stateManager;

        loadCrosshairs();
    }

    private void loadCrosshairs() {
        CustomPicture pic = new CustomPicture("Crosshair");
        pic.setImage(app.getAssetManager(), "Textures/Crosshair/cross_normal.png", true);
        pic.setWidth(32);
        pic.setHeight(32);
        pic.setPosition((Display.getWidth() / 2) - 16, (Display.getHeight() / 2) - 16);
        app.getGuiNode().attachChild(pic);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
