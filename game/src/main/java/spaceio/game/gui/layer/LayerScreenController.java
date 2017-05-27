package spaceio.game.gui.layer;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;

import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import spaceio.game.engine.GameEngine;

import javax.annotation.Nonnull;

public class LayerScreenController implements ScreenController, KeyInputHandler {
    private GameEngine gameEngine;
    private Screen screen;
    private Nifty nifty;
    private Element quitPopup;
    private Window winControls;



    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        quitPopup = nifty.createPopup("quitPopup");
        this.winControls = screen.findNiftyControl("winGameLogControl", Window.class);
    }

    public void showGameLog()
    {
        winControls.getElement().setVisible(true);
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @NiftyEventSubscriber(pattern = "quitPopup.*")
    public void onAnswerPopupButtonClicked(final String id, final ButtonClickedEvent event) {
        if (id.equals("quitPopupYes")) {
            gameEngine.comfirmQuitGame();
        } else {
            gameEngine.cancelQuitGame();
        }
    }

    @NiftyEventSubscriber(id = "console")
    public void onConsoleEvent(final String id, final ConsoleExecuteCommandEvent executeCommandEvent) {
        System.out.println(executeCommandEvent.getCommandLine());
    }
}
