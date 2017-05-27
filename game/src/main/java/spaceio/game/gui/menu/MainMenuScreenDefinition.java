package spaceio.game.gui.menu;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.*;
import spaceio.game.gui.common.CommonBuilders;
import spaceio.game.gui.common.MenuButtonControlDefinition;
import spaceio.game.gui.menu.component.GameOptionsControlDefinition;
import spaceio.game.gui.menu.component.NewGameControlDefinition;

import javax.annotation.Nonnull;

public class MainMenuScreenDefinition {
    public static String NAME = "main-menu";

    private static String BTN_NEW_GAME_ID = "menuButton.newGame";
    private static String BTN_GAME_OPTIONS_ID = "menuButton.gameOptions";

    private static String MENU_NEW_GAME_ID = "menu.newGame";
    private static String MENU_GAME_OPTIONS_ID = "menu.gameOptions";

    private static CommonBuilders builders = new CommonBuilders();

    public static void register(@Nonnull final Nifty niftyGUI) {
        registerMenuScreenComponents(niftyGUI);

        new ScreenBuilder(NAME) {{
            controller(new MainMenuScreenController(
                    BTN_NEW_GAME_ID, MENU_NEW_GAME_ID,
                    BTN_GAME_OPTIONS_ID, MENU_GAME_OPTIONS_ID
            ));
            inputMapping("de.lessvoid.nifty.input.mapping.DefaultInputMapping");
            layer(new LayerBuilder("layer") {{
//                backgroundImage("menu-background.png");
                childLayoutVertical();
                panel(new PanelBuilder("navigation") {{
                    width("100%");
                    height("63px");
                    backgroundColor("#34495e");
                    childLayoutHorizontal();
                    padding("20px");
                    control(MenuButtonControlDefinition.getControlBuilder(BTN_NEW_GAME_ID, "Game", "Game options"));
                    panel(builders.hspacer("10px"));
                    control(MenuButtonControlDefinition.getControlBuilder(BTN_GAME_OPTIONS_ID, "Options", "Display and controls"));
                }});
                panel(new PanelBuilder("dialogParent") {{
                    childLayoutOverlay();
                    width("100%");
                    alignCenter();
                    valignCenter();
                    control(new ControlBuilder(MENU_NEW_GAME_ID, NewGameControlDefinition.NAME));
                    control(new ControlBuilder(MENU_GAME_OPTIONS_ID, GameOptionsControlDefinition.NAME));
                }});
            }});
            layer(new LayerBuilder() {{
                childLayoutVertical();
                panel(new PanelBuilder() {{
                    height("*");
                }});
                panel(new PanelBuilder() {{
                    childLayoutCenter();
                    height("50px");
                    width("100%");
                    backgroundColor("#2c3e50");
                    panel(new PanelBuilder() {{
                        paddingLeft("25px");
                        paddingRight("25px");
                        height("50%");
                        width("100%");
                        alignCenter();
                        valignCenter();
                        childLayoutHorizontal();
                    }});
                }});
            }});
            layer(new LayerBuilder("whiteOverlay") {{
                onCustomEffect(new EffectBuilder("renderQuad") {{
                    customKey("onResolutionStart");
                    length(350);
                    neverStopRendering(false);
                }});
                onStartScreenEffect(new EffectBuilder("renderQuad") {{
                    length(300);
                    effectParameter("startColor", "#ddff");
                    effectParameter("endColor", "#0000");
                }});
                onEndScreenEffect(new EffectBuilder("renderQuad") {{
                    length(300);
                    effectParameter("startColor", "#0000");
                    effectParameter("endColor", "#ddff");
                }});
            }});
        }}.build(niftyGUI);
    }

    private static void registerMenuScreenComponents(Nifty niftyGUI) {
        NewGameControlDefinition.register(niftyGUI);
        GameOptionsControlDefinition.register(niftyGUI);
    }
}
