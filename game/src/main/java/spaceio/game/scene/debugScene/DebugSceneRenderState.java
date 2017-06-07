package spaceio.game.scene.debugScene;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;
import com.simsilica.lemur.GuiGlobals;
import spaceio.game.view.render.SceneNodeProperties;

public class DebugSceneRenderState extends BaseAppState {
    private Geometry ground;
    private Node sceneObjects;
    private SceneNodeProperties sceneObjectParams = SceneNodeProperties.createNewBox();

    @Override
    protected void initialize(Application app) {
        Material groundMaterial = GuiGlobals.getInstance().createMaterial(ColorRGBA.Yellow, true).getMaterial();
        sceneObjects = new Node("Scene objects");

        Box b = new Box(
                10,
                10,
                10
        );
        TangentBinormalGenerator.generate(b);
        ground = new Geometry("Box", b);
        ground.setMaterial(groundMaterial);
        sceneObjects.attachChild(ground);

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        sceneObjects.setLocalTranslation(sceneObjectParams.getCoordinateX(), sceneObjectParams.getCoordinateY(), sceneObjectParams.getCoordinateZ());
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        Node rootNode = ((SimpleApplication) getApplication()).getRootNode();
        rootNode.attachChild(sceneObjects);
    }

    @Override
    protected void onDisable() {
        sceneObjects.removeFromParent();
    }

    public SceneNodeProperties getSceneObjectParams() {
        return sceneObjectParams;
    }
}
