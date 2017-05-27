package spaceio.game.utils;

import com.jme3.system.AppSettings;

public class ScreenSettings {

    private AppSettings gameScreen = new AppSettings(true);

    public ScreenSettings() {
        gameScreen = new AppSettings(true);
    }

    public static AppSettings generate()
    {
        return new ScreenSettings().gameScreen;
    }
}
