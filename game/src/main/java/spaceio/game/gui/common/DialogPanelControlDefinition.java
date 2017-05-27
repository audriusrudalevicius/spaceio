package spaceio.game.gui.common;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;

import javax.annotation.Nonnull;

public class DialogPanelControlDefinition {
    public static String NAME = "dialogPanel";
    private static CommonBuilders builders = new CommonBuilders();

    public static void register(@Nonnull final Nifty nifty) {
        new ControlDefinitionBuilder(NAME) {{
            set("childRootId", "#effectPanel");
            panel(new PanelBuilder() {{
                visible(false);
                childLayoutCenter();
                panel(new PanelBuilder("#effectPanel") {{
                    style("nifty-panel");
                    childLayoutVertical();
                    alignCenter();
                    valignCenter();
                    width("50%");
                    height("60%");
                    padding("14px,20px,26px,19px");
                    onShowEffect(builders.createMoveEffect("in", "left", 500));
                    onHideEffect(builders.createMoveEffect("out", "right", 500));
                    onHideEffect(builders.createFadeEffect());
                }});
            }});
        }}.registerControlDefintion(nifty);
    }
}
