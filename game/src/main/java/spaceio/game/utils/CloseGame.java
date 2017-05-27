package spaceio.game.utils;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import spaceio.game.engine.GameEngine;
import spaceio.game.gui.popup.QuitPopupDefinition;

public class CloseGame {
    private final GameEngine gameEngine;
    private final Nifty nifty;
    private final Screen screen;
    private final Element quitPopup;

    public CloseGame(Nifty niftyGUI, GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.nifty = niftyGUI;
        screen = niftyGUI.getCurrentScreen();
        quitPopup = niftyGUI.createPopup(QuitPopupDefinition.NAME);
    }

    public void show() {
        this.nifty.showPopup(screen, quitPopup.getId(), null);
        screen.processAddAndRemoveLayerElements();
    }

    public void continueGame() {
        nifty.closePopup(quitPopup.getId());
    }

    public void comfirmQuit() {
        this.gameEngine.jmonkeyApp.stop();
        System.exit(0);
    }
}
