package spaceio.game.gui.intro;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;
import spaceio.game.gui.menu.MainMenuScreenDefinition;

import javax.annotation.Nonnull;

public class IntroScreenDefinition {

    public static String NAME = "intro-screen";

    public static void register(@Nonnull final Nifty nifty) {
        new ScreenBuilder(NAME) {{
            controller(new DefaultScreenController() {
                @Override
                public void onStartScreen() {
                    nifty.gotoScreen(MainMenuScreenDefinition.NAME);
                }
            });

            layer(new LayerBuilder() {
                {
                    backgroundColor("#c6c6c6");
                    onStartScreenEffect(new EffectBuilder("fade") {
                        {
                            length(800);
                            startDelay(1500);
                            effectParameter("start", "#323232");
                            effectParameter("end", "#c6c6c6");
                        }
                    });
                }
            });

            layer(new LayerBuilder("layer") {{
                childLayoutCenter();
            }});

            layer(new LayerBuilder() {
                {
                    onStartScreenEffect(new EffectBuilder("fade") {
                        {
                            length(800);
                            startDelay(1500);
                            effectParameter("start", "#f");
                            effectParameter("end", "#c6c6c6");
                        }
                    });
                }
            });
        }}.build(nifty);
    }
}
