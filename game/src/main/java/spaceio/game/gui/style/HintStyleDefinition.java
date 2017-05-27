package spaceio.game.gui.style;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.StyleBuilder;
import de.lessvoid.nifty.tools.Color;
import javax.annotation.Nonnull;

public class HintStyleDefinition {
    public static String SPECIAL_HINT_NAME = "special-hint";
    public static String SPECIAL_HINT_TEXT_NAME = "special-hint#hint-text";
    public static void register(@Nonnull final Nifty niftyGUI) {
        new StyleBuilder() {{
            id(SPECIAL_HINT_NAME);
            base("nifty-panel-bright");
            childLayoutCenter();
            onShowEffect(new EffectBuilder("fade") {{
                length(150);
                effectParameter("start", "#0");
                effectParameter("end", "#d");
                inherit();
                neverStopRendering(true);
            }});
            onShowEffect(new EffectBuilder("move") {{
                length(150);
                inherit();
                neverStopRendering(true);
                effectParameter("mode", "fromOffset");
                effectParameter("offsetY", "-15");
            }});
            onCustomEffect(new EffectBuilder("fade") {{
                length(150);
                effectParameter("start", "#d");
                effectParameter("end", "#0");
                inherit();
                neverStopRendering(true);
            }});
            onCustomEffect(new EffectBuilder("move") {{
                length(150);
                inherit();
                neverStopRendering(true);
                effectParameter("mode", "toOffset");
                effectParameter("offsetY", "-15");
            }});
        }}.build(niftyGUI);

        new StyleBuilder() {{
            id(SPECIAL_HINT_TEXT_NAME);
            base("base-font");
            alignLeft();
            valignCenter();
            textHAlignLeft();
            color(new Color("#000f"));
        }}.build(niftyGUI);
    }
}
