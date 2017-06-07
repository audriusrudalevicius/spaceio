package spaceio.game.view.common;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import spaceio.game.SpaceGameApplication;

import javax.annotation.Nonnull;

public class SkyState extends BaseAppState {

    Spatial sky;

    @Override
    protected void initialize(Application app) {
        this.sky = createSkybox(app.getAssetManager());
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        ((SpaceGameApplication) getApplication()).getRootNode().attachChild(sky);
    }

    @Override
    protected void onDisable() {
        this.sky.removeFromParent();
    }

    @Nonnull
    private Spatial createSkybox(AssetManager assetManager) {
        String skyName = "Textures/Skies/sky.dds";
        return SkyFactory.createSky(assetManager, skyName, SkyFactory.EnvMapType.CubeMap);
    }
}
