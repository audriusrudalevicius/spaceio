package spaceio.game.gui.popup;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import spaceio.game.gui.common.CommonBuilders;

import javax.annotation.Nonnull;

public class QuitPopupDefinition {

    @Nonnull
    private static final CommonBuilders builders = new CommonBuilders();
    public static String NAME = "quitPopup";

    public static void register(@Nonnull final Nifty niftyGUI) {
        new PopupBuilder("quitPopup") {
            {
                childLayoutCenter();
                panel(new PanelBuilder() {
                    {
//                        backgroundImage(popupBackgroundImage);
                        backgroundColor("#34495e");
                        width("240px");
                        height("200px");
                        alignCenter();
                        valignCenter();
                        onStartScreenEffect(new EffectBuilder("move") {
                            {
                                length(400);
                                inherit();
                                effectParameter("mode", "in");
                                effectParameter("direction", "top");
                            }
                        });
                        onEndScreenEffect(new EffectBuilder("move") {
                            {
                                length(400);
                                inherit();
                                neverStopRendering(true);
                                effectParameter("mode", "out");
                                effectParameter("direction", "top");
                            }
                        });

                        padding("10px");
                        childLayoutVertical();
                        panel(new PanelBuilder() {
                            {
                                paddingTop("40px");
                                childLayoutHorizontal();
                                control(new LabelBuilder("login", "Are you sure you want to quit game?") {
                                    {
                                        alignCenter();
                                        width("*");
                                    }
                                });
                            }
                        });
                        panel(new PanelBuilder() {
                            {
                                width("*");
                                paddingTop("60px");
                                alignCenter();
                                childLayoutHorizontal();
                                control(new ButtonBuilder("quitPopupYes") {
                                    {
                                        label("Quit");
                                        alignCenter();
                                        valignCenter();
                                    }
                                });
                                panel(builders.hspacer("20px"));
                                control(new ButtonBuilder("quitPopupNo") {
                                    {
                                        label("Cancel");
                                        alignCenter();
                                        valignCenter();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }.registerPopup(niftyGUI);
    }
}
