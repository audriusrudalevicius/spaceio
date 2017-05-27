package spaceio.game.gui.menu;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import spaceio.game.engine.GameEngine;
import spaceio.game.gui.popup.QuitPopupDefinition;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MainMenuScreenController implements ScreenController, KeyInputHandler {
    private static Logger logger = Logger.getLogger(MainMenuScreenController.class.getName());

    private Nifty nifty;
    private Screen screen;

    private Map<String, String> buttonToElementMap = new Hashtable<String, String>();
    private List<String> buttonIdList = new ArrayList<String>();
    private String currentMenuButtonId;
    private GameEngine gameEngine;
    private String[] mappings;


    public MainMenuScreenController(final String... mapping) {
        this.mappings = mapping;
    }

    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.ConsoleToggle) {
            this.gameEngine.toggleConsole();
        }
        return false;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        this.bindMappings();
    }

    private void bindMappings() {
        if (this.mappings == null || this.mappings.length == 0 || this.mappings.length % 2 != 0) {
            logger.warning("expecting pairs of values that map menuButton IDs to dialog IDs");
        } else {
            for (int i = 0; i < this.mappings.length / 2; i++) {
                String menuButtonId = this.mappings[i * 2 + 0];
                String elementId = this.mappings[i * 2 + 1];

                Element element = null;
                try {
                    element = screen.findElementById(elementId);
                } catch (NullPointerException e) {
                    // do nothing.
                }
                if (element == null) {
                    logger.warning(String.format("Element with id \"%s\" not exists!", elementId));
                }

                buttonToElementMap.put(menuButtonId, elementId);
                buttonIdList.add(menuButtonId);

                if (i == 0) {
                    currentMenuButtonId = menuButtonId;
                }
            }
        }
    }

    @Override
    public void onStartScreen() {
        screen.findElementById(buttonToElementMap.get(currentMenuButtonId)).show();
        screen.findElementById(currentMenuButtonId).startEffect(EffectEventId.onCustom, null, "selected");
    }

    @Override
    public void onEndScreen() {

    }

    private void changeDialogTo(final String id) {
        if (!id.equals(currentMenuButtonId)) {

            Element nextElement = screen.findElementById(buttonToElementMap.get(id));
            nextElement.show();
            Element currentElement = screen.findElementById(buttonToElementMap.get(currentMenuButtonId));
            currentElement.hide();

            currentMenuButtonId = id;
        }
    }

    @NiftyEventSubscriber(pattern = "menuButton.*")
    public void onMenuButtonListBoxClick(@Nonnull final String id, final NiftyMousePrimaryClickedEvent clickedEvent) {
        changeDialogTo(id);
    }

    @NiftyEventSubscriber(pattern = "quitPopup.*")
    public void onAnswerPopupButtonClicked(final String id, final ButtonClickedEvent event) {
        if (id.equals("quitPopupYes")) {
            gameEngine.comfirmQuitGame();
        } else {
            gameEngine.cancelQuitGame();
        }
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
}
