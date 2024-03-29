package spaceio.game.scene.debugScene;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.simsilica.lemur.GuiGlobals;
import spaceio.game.SpaceGameApplication;
import spaceio.game.view.render.SceneNodeProperties;

public class DebugSceneRenderState extends BaseAppState {
    private Geometry ground;
    private Node sceneObjects = new Node("Scene objects");
    private SceneNodeProperties sceneObjectParams = SceneNodeProperties.createNewBox();

    @Override
    protected void initialize(Application app) {
        Material groundMaterial = GuiGlobals.getInstance().createMaterial(ColorRGBA.Yellow, true).getMaterial();

        Box b = new Box(
                2,
                2,
                2
        );
        ground = new Geometry("Box", b);
        ground.setMaterial(groundMaterial);
        sceneObjects.attachChild(ground);

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
//        sceneObjects.setLocalTranslation(sceneObjectParams.getCoordinateX(), sceneObjectParams.getCoordinateY(), sceneObjectParams.getCoordinateZ());
    }

    @Override
    protected void cleanup(Application app) {

    }

    public Node getNode() {
        return sceneObjects;
    }

    @Override
    protected void onEnable() {
        Node rootNode = ((SpaceGameApplication) getApplication()).getRootNode();
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
