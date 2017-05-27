package spaceio.game.gui.menu.component;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import spaceio.game.engine.GameEngine;
import spaceio.game.gui.layer.LayerScreenDefinition;
import spaceio.game.gui.menu.MainMenuScreenController;

import javax.annotation.Nonnull;

public class NewGameControlController implements Controller {
    private Screen screen;
    private Nifty nifty;
    private GameEngine gameEngine;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        this.screen = screen;
        this.nifty = nifty;
        this.gameEngine = ((MainMenuScreenController) this.screen.getScreenController()).getGameEngine();
    }

    @Override
    public void init(@Nonnull Parameters parameter) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onFocus(boolean getFocus) {

    }

    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }

    @Override
    public void onEndScreen() {

    }

    @NiftyEventSubscriber(pattern = "newGameButton.*")
    public void onMenuButtonListBoxClick(@Nonnull final String id, final NiftyMousePrimaryClickedEvent clickedEvent) {
        screen.endScreen(null);
        nifty.gotoScreen(LayerScreenDefinition.NAME);

        this.gameEngine.playGame(true);
    }
}
