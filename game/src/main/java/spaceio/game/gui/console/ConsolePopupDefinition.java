package spaceio.game.gui.console;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;

import javax.annotation.Nonnull;

public class ConsolePopupDefinition {

    public static String NAME = "console-popup";

    public static void register(@Nonnull final Nifty nifty) {
        new PopupBuilder(NAME) {{
            childLayoutAbsolute();
            panel(new PanelBuilder() {{
                childLayoutCenter();
                width("100%");
                height("100%");
                alignCenter();
                valignCenter();
                control(new ConsoleBuilder("console") {{
                    width("80%");
                    lines(25);
                    alignCenter();
                    valignCenter();
                    onStartScreenEffect(new EffectBuilder("move") {{
                        length(150);
                        inherit();
                        neverStopRendering(true);
                        effectParameter("mode", "in");
                        effectParameter("direction", "top");
                    }});
                    onEndScreenEffect(new EffectBuilder("move") {{
                        length(150);
                        inherit();
                        neverStopRendering(true);
                        effectParameter("mode", "out");
                        effectParameter("direction", "top");
                    }});
                }});
            }});
        }}.registerPopup(nifty);
    }
}
