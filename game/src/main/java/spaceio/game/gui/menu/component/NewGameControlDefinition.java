package spaceio.game.gui.menu.component;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import spaceio.game.gui.common.CommonBuilders;
import spaceio.game.gui.common.DialogPanelControlDefinition;
import spaceio.game.gui.common.MenuButtonControlDefinition;

import javax.annotation.Nonnull;

public class NewGameControlDefinition {
    public static final String NAME = "new-game-panel";

    public static final String BTN_START_NEW_GAME = "newGameButton.start-new-game";

    @Nonnull
    private static final CommonBuilders builders = new CommonBuilders();

    public static void register(@Nonnull final Nifty nifty) {
        new ControlDefinitionBuilder(NAME) {{
            controller(new NewGameControlController());
            control(new ControlBuilder(DialogPanelControlDefinition.NAME) {{
                panel(new PanelBuilder() {{
                    width("100%");
                    height("100%");
                    alignCenter();
                    valignCenter();
                    childLayoutVertical();
                    backgroundColor("#95a5a6");
                    panel(builders.vspacer("15px"));
                    control(MenuButtonControlDefinition.getControlBuilder(BTN_START_NEW_GAME, "Start new game", "New game"));
                }});
            }});

        }}.registerControlDefintion(nifty);
    }
}
