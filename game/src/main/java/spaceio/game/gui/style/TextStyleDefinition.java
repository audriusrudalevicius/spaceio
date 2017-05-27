package spaceio.game.gui.style;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.StyleBuilder;
import de.lessvoid.nifty.tools.Color;

import javax.annotation.Nonnull;

public class TextStyleDefinition {
    public static final String TITLE_LABEL_NAME = "title-text";

    public static void register(@Nonnull final Nifty niftyGUI) {
        new StyleBuilder() {{
            id(TITLE_LABEL_NAME);
            base("base-font");
            alignLeft();
            valignCenter();
            textHAlignLeft();
            color(new Color("#000f"));
        }}.build(niftyGUI);
    }
}
