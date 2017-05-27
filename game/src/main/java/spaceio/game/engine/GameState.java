package spaceio.game.engine;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FadeFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import spaceio.game.utils.Params;

public class GameState {
    private SimpleApplication app;
    private AssetManager assetManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private Node rootNode;
    private FlyByCamera flyCam;
    private Camera cam;
    private FilterPostProcessor fpp;

    public void update(float tpf) {

    }

    public void initialize(AppStateManager stateManager, SimpleApplication app) {
        this.app = app;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.rootNode = this.app.getRootNode();
        this.flyCam = this.app.getFlyByCamera();
        this.cam = this.app.getCamera();

        initView();
        createSkybox();
    }

    private void initView() {
        fpp = new FilterPostProcessor(assetManager);
        Params.fadeFilter = new FadeFilter(1.5f);
        fpp.addFilter(Params.fadeFilter);
        viewPort.addProcessor(fpp);
    }

    private void createSkybox()
    {
        String skyName = "Textures/Skies/sky.dds";
        Spatial skyGeo = SkyFactory.createSky(this.assetManager, skyName, SkyFactory.EnvMapType.CubeMap);
        rootNode.attachChild(skyGeo);
    }
}
