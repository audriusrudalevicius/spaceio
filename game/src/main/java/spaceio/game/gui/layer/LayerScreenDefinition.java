package spaceio.game.gui.layer;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.*;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.listbox.builder.ListBoxBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.controls.window.builder.WindowBuilder;
import spaceio.game.gui.common.CommonBuilders;
import spaceio.game.gui.layer.component.GameLogScreenController;
import spaceio.game.utils.Params;

import javax.annotation.Nonnull;

public class LayerScreenDefinition {
    public static String NAME = "game-layer";
    private static CommonBuilders builders = new CommonBuilders();

    public static void register(@Nonnull final Nifty niftyGUI) {
        registerMenuScreenComponents(niftyGUI);

        new ScreenBuilder(NAME) {{
            controller(new LayerScreenController());
            layer(new LayerBuilder("layer") {{

                childLayoutVertical();
                panel(new PanelBuilder() {{
                    width("100%");
                    alignCenter();
                    childLayoutHorizontal();
                    control(new LabelBuilder("labelCurrentGameTime") {
                        {
                            width("130px");
                            textHAlignLeft();
                        }
                    });
                    panel(builders.hspacer("10px"));

                    panel(new PanelBuilder("winGLC_Element") {
                        {
                            childLayoutAbsolute();
                            controller(new GameLogScreenController());

                            control(new LabelBuilder("LogLabel", "Game Log") {
                                {
                                    onShowEffect(builders.createMoveEffect("in", "bottom", 600));
                                    onHideEffect(builders.createMoveEffect("out", "bottom", 600));
                                    backgroundColor("#95a5a6");
                                    color("#34495e");
//                                    backgroundImage("Interface/panelBack3.png");
                                    x("334px");
                                    int h = Params.screenHeight - (720 - 700);
                                    y(Integer.toString(h) + "px");//"700px");
                                    width("612px");
//                                    interactOnClick("HideWindow()");

                                }
                            });


                            control(new WindowBuilder("winGameLogControl", "Game Log") {
                                {
                                    onShowEffect(builders.createMoveEffect("in", "bottom", 600));
                                    onHideEffect(builders.createMoveEffect("out", "bottom", 600));
                                    interactOnClickRepeat("showHide()");
                                    closeable(false);
                                    backgroundColor("#95a5a6");
                                    panel(new PanelBuilder() {
                                        {
                                            childLayoutVertical();
                                            panel(new PanelBuilder() {
                                                {
                                                    childLayoutHorizontal();
                                                    control(new TextFieldBuilder("time_WGLC", " Date Time") {
                                                        {
                                                            width("130px");
                                                        }
                                                    });
                                                    control(new TextFieldBuilder("message_WGLC", " Message") {
                                                        {
                                                            width("467px");
                                                        }
                                                    });
                                                }
                                            });
                                            control(new ListBoxBuilder("gameLog_WGLC") {
                                                {
                                                    displayItems(7);
                                                    selectionModeSingle();
                                                    optionalVerticalScrollbar();
                                                    hideHorizontalScrollbar();
                                                    width("*");
                                                    control(new ControlBuilder("customListBox_GameLog") {
                                                        {
                                                            controller("de.lessvoid.nifty.controls.listbox.ListBoxItemController");
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                    x("334px");
                                    int h = Params.screenHeight - (720 - 488);
                                    y(Integer.toString(h) + "px");//"488px");
                                    visible(false);
                                    width("612px");
                                    height("230px");
                                }
                            });
                        }
                    });
                }});
            }});
        }}.build(niftyGUI);
    }

    private static void registerMenuScreenComponents(@Nonnull final Nifty niftyGUI) {
        new ControlDefinitionBuilder("customListBox_GameLog") {
            {
                panel(new PanelBuilder() {
                    {
                        childLayoutHorizontal();
                        width("100%");
                        control(new LabelBuilder("#time") {
                            {
                                visibleToMouse();
                                textHAlignLeft();
                                height("25px");
                                width("130px");
                            }
                        });
                        image(new ImageBuilder("#icon") {
                            {
                                width("20px");
                                height("20px");
                            }
                        });
                        control(new LabelBuilder("#message") {
                            {
                                visibleToMouse();
                                textHAlignLeft();
                                height("25px");
                                width("480");

                            }
                        });
                    }
                });
            }
        }.registerControlDefintion(niftyGUI);

        new ControlDefinitionBuilder("customListBox_Line") {
            {
                panel(new PanelBuilder() {
                    {
                        childLayoutHorizontal();
                        width("100%");
                        image(new ImageBuilder("#icon") {
                            {
                                width("25px");
                                height("25px");
                            }
                        });
                        control(new LabelBuilder("#message") {
                            {
                                visibleToMouse();
                                alignLeft();
                                textHAlignLeft();
                                height("30px");
                                width("*");
                            }
                        });
                    }
                });
            }
        }.registerControlDefintion(niftyGUI);
    }
}
