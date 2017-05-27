package spaceio.game.gui.common;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.FocusHandler;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.Screen;

import javax.annotation.Nonnull;

public class MenuButtonController implements Controller {

    private Element element;
    private FocusHandler focusHandler;

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        this.element = element;
        this.focusHandler = screen.getFocusHandler();
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
        if (inputEvent == NiftyStandardInputEvent.NextInputElement) {
            focusHandler.getNext(element).setFocus();
            return true;
        } else if (inputEvent == NiftyStandardInputEvent.PrevInputElement) {
            focusHandler.getPrev(element).setFocus();
            return true;
        } else if (inputEvent == NiftyStandardInputEvent.Activate) {
            element.onClickAndReleasePrimaryMouseButton();
            return true;
        }
        return false;
    }

    @Override
    public void onEndScreen() {

    }
}
