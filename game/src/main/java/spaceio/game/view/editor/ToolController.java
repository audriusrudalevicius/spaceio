package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ToolController extends SceneToolController {

    private SceneEditTool editTool;
    private TransformationType transformationType = ToolController.TransformationType.local;

    private Node onTopToolsNode = new Node("OverlayNode");

    private boolean snapToGrid = false;
    private boolean selectTerrain = false;
    private boolean selectGeometries = false;
    private Application application;
    private Node rootNode;

    public ToolController(Application application, Node rootNode) {
        super(application.getAssetManager());
        this.application = application;
        this.rootNode = rootNode;
    }

    public void setSelected(Spatial selected) {
        this.selected = selected;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public TransformationType getTransformationType() {
        return transformationType;
    }

    public boolean isSelectTerrain() {
        return selectTerrain;
    }

    public boolean isSelectGeometries() {
        return selectGeometries;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public boolean isOverrideCameraControl() {
        if (editTool != null) {
            return editTool.isOverrideCameraControl();
        } else {
            return false;
        }
    }

    public boolean isEditToolEnabled() {
        return editTool != null;
    }

    public void doEditToolActivatedPrimary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        if (editTool != null) {
            editTool.setCamera(camera);
            editTool.actionPrimary(mouseLoc, pressed, rootNode);
        }
    }

    public void doEditToolActivatedSecondary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        if (editTool != null) {
            editTool.setCamera(camera);
            editTool.actionSecondary(mouseLoc, pressed, rootNode);
        }
    }

    public void doEditToolMoved(Vector2f mouseLoc, Camera camera) {
        if (editTool != null) {
            editTool.setCamera(camera);
            editTool.mouseMoved(mouseLoc, rootNode);
        }
    }

    public void doEditToolDraggedPrimary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        if (editTool != null) {
            editTool.setCamera(camera);
            editTool.draggedPrimary(mouseLoc, pressed, rootNode);
        }
    }

    public void doEditToolDraggedSecondary(Vector2f mouseLoc, boolean pressed, Camera camera) {
        if (editTool != null) {
            editTool.setCamera(camera);
            editTool.draggedSecondary(mouseLoc, pressed, rootNode);
        }
    }

    public void doKeyPressed(KeyInputEvent kie) {
        if (editTool != null) {
            editTool.keyPressed(kie);
        }
    }

    @Override
    protected void initialize(Application app) {
        super.initialize(app);
    }

    @Override
    public void update(float tpf) {
        if (editTool != null) {
            editTool.doUpdateToolsTransformation();
        }
        onTopToolsNode.updateLogicalState(tpf);
        super.update(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        onTopToolsNode.updateGeometricState();
        super.render(rm);
    }

    @Override
    protected void cleanup(Application app) {
        super.cleanup(app);
        onTopToolsNode.detachAllChildren();
    }

    public Node getOnTopToolsNode() {
        return onTopToolsNode;
    }

    public void enableEditTool(SceneEditTool sceneEditTool)
    {
        doEnableEditTool(application, sceneEditTool);
    }

    private void doEnableEditTool(Application application, SceneEditTool sceneEditTool) {
        if (editTool != null) {
            editTool.hideMarker();
        }
        editTool = sceneEditTool;
        editTool.activate(application, assetManager, toolsNode, onTopToolsNode, selected, this);
    }

    public void selectedSpatialTransformed() {
        if (editTool != null) {
            editTool.updateToolsTransformation();
        }
    }

    public enum TransformationType {
        local, global, camera
    }
}
