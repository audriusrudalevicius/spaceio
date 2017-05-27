package spaceio.game.gui.menu.component;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import spaceio.game.gui.common.CommonBuilders;
import spaceio.game.gui.common.DialogPanelControlDefinition;

import javax.annotation.Nonnull;

public class GameOptionsControlDefinition {
    public static final String NAME = "game-options-panel";

    @Nonnull
    private static final CommonBuilders builders = new CommonBuilders();

    public static void register(@Nonnull final Nifty nifty) {
        new ControlDefinitionBuilder(NAME) {{
            controller(new GameOptionsControlController());
            control(new ControlBuilder(DialogPanelControlDefinition.NAME) {{
                panel(new PanelBuilder() {{
                    width("100%");
                    height("100%");
                    alignCenter();
                    valignCenter();
                    childLayoutVertical();
                    backgroundColor("#95a5a6");
                    panel(builders.vspacer("15px"));
                    control(builders.createLabel("Options is Under construction", "100%"));
                }});
            }});
        }}.registerControlDefintion(nifty);
    }
}
