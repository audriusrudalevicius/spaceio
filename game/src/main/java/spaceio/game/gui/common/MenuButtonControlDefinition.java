package spaceio.game.gui.common;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import spaceio.game.gui.style.HintStyleDefinition;

import javax.annotation.Nonnull;

public class MenuButtonControlDefinition {
    public static final String NAME = "menuButtonControl";
    private static final String PARAMETER_LABEL = "menuButtonLabel";
    private static final String PARAMETER_HINT = "menuButtonHint";

    public static void register(@Nonnull final Nifty nifty) {
        new ControlDefinitionBuilder(NAME) {{
            controller(new MenuButtonController());
            panel(new PanelBuilder() {{
                backgroundColor("#95a5a6");
                width("123px");
                alignCenter();
                valignCenter();
                childLayoutCenter();
                focusable(true);
                visibleToMouse();
                onActiveEffect(new EffectBuilder("border") {{
                    effectParameter("color", "#2c3e50");
                }});
                onHoverEffect(new HoverEffectBuilder("changeMouseCursor") {{
                    effectParameter("id", "hand");
                }});
                onHoverEffect(new HoverEffectBuilder("border") {{
                    effectParameter("color", "#2980b9");
                }});
                onFocusEffect(new EffectBuilder("border") {{
                    effectParameter("color", "#e74c3c");
                }});
                onHoverEffect(new HoverEffectBuilder("hint") {{
                    effectParameter("hintText", controlParameter(PARAMETER_HINT));
                    effectParameter("hintStyle", HintStyleDefinition.SPECIAL_HINT_NAME);
                    effectParameter("hintDelay", "750");
                    effectParameter("offsetX", "center");
                    effectParameter("offsetY", "50");
                }});
                control(new LabelBuilder() {{
                    color("#34495e");
                    text(controlParameter(PARAMETER_LABEL));
                    alignCenter();
                    valignCenter();
                    onCustomEffect(new EffectBuilder("textColor") {{
                        effectParameter("color", "#34495e");
                        effectParameter("customKey", "selected");
                        effectParameter("timeType", "infinite");
                        effectParameter("neverStopRendering", "true");
                    }});
                }});
            }});
        }}.registerControlDefintion(nifty);
    }

    public static ControlBuilder getControlBuilder(final String id, final String text, final String hintText) {
        return new ControlBuilder(id, NAME) {{
            parameter(PARAMETER_LABEL, text);
            parameter(PARAMETER_HINT, hintText);
        }};
    }

    public static ControlBuilder getControlBuilder(final String id, final String text, final String hintText, final String width) {
        return new ControlBuilder(id, NAME) {{
            parameter(PARAMETER_LABEL, text);
            parameter(PARAMETER_HINT, hintText);
            width(width);
        }};
    }
}