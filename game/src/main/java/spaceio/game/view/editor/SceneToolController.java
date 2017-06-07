package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.*;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import spaceio.game.view.editor.camera.AbstractCameraController;

public class SceneToolController  extends BaseAppState {
    protected boolean showSelection = false;
    protected boolean showGrid = false;
    protected Spatial selected;
    protected Spatial selectionShape;
    protected AbstractCameraController camController;

    private Node cursor;

    protected Node toolsNode;
    protected AssetManager assetManager;
    private Material blueMat;
    private Geometry grid;

    public SceneToolController(AssetManager assetManager) {
        this.toolsNode = new Node("ToolsNode");
        this.assetManager = assetManager;

        initTools();
    }

    public void setCamController(AbstractCameraController camController) {
        this.camController = camController;
    }

    public Spatial getSelectedSpatial() {
        return selected;
    }

    public void setNeedsSave() {

    }

    public void doSetCursorLocation(Vector3f location) {
        cursor.setLocalTranslation(location);
        if (camController != null)
            camController.doSetCamFocus(location);
    }

    public void updateSelection(Spatial spat) {
        if (showSelection && spat != null) {
            if (selected != spat) {
                if (selectionShape != null) {
                    detachSelectionShape();
                }
                attachSelectionShape(spat);
            } else {
                if (selectionShape == null) {
                    attachSelectionShape(spat);
                }
            }
        } else {
            if (selectionShape != null) {
                detachSelectionShape();
            }
        }
        selected = spat;
    }

    protected void attachSelectionShape(Spatial spat) {
        if (selectionShape != null) {
            selectionShape.removeFromParent();
            selectionShape = null;
        }
        if (spat instanceof ParticleEmitter) {
            attachBoxSelection(spat);

        } else if (spat instanceof Geometry) {
            attachGeometrySelection((Geometry) spat);
        } else if (spat.getControl(PhysicsControl.class) != null) {
            attachPhysicsSelection(spat);
        } else {
            attachBoxSelection(spat);
        }
    }

    public void rebuildSelectionBox() {
        if (selected != null) {
            attachSelectionShape(selected);
        }
    }

    protected void attachGeometrySelection(Geometry geom) {
        Mesh mesh = geom.getMesh();
        if (mesh == null) {
            return;
        }
        final Geometry selectionGeometry = new Geometry("selection_geometry_sceneviewer", mesh);
        selectionGeometry.setMaterial(blueMat);
        selectionGeometry.setLocalTransform(geom.getWorldTransform());
        selectionShape = selectionGeometry;
        toolsNode.attachChild(selectionGeometry);
    }

    protected void attachPhysicsSelection(Spatial geom) {
        PhysicsCollisionObject control = geom.getControl(RigidBodyControl.class);
        if (control == null) {
            control = geom.getControl(VehicleControl.class);
        }
        if (control == null) {
            control = geom.getControl(GhostControl.class);
        }
        if (control == null) {
            control = geom.getControl(CharacterControl.class);
        }
        if (control == null) {
            return;
        }
        final Spatial selectionGeometry = DebugShapeFactory.getDebugShape(control.getCollisionShape());
        if (selectionGeometry != null) {
            selectionGeometry.setMaterial(blueMat);
            selectionGeometry.setLocalTransform(geom.getWorldTransform());
            selectionShape = selectionGeometry;
            toolsNode.attachChild(selectionGeometry);
        }
    }

    protected void attachBoxSelection(Spatial geom) {
        BoundingVolume bound = geom.getWorldBound();
        if (bound instanceof BoundingBox) {
            BoundingBox bbox = (BoundingBox) bound;
            Vector3f extent = new Vector3f();
            bbox.getExtent(extent);
            WireBox wireBox = new WireBox();
            wireBox.setBound(bbox);
            final Geometry selectionGeometry = new Geometry("selection_geometry_sceneviewer", wireBox);
            selectionGeometry.setMaterial(blueMat);
            selectionGeometry.setLocalTranslation(bbox.getCenter().subtract(geom.getWorldTranslation()));

            selectionShape = new Node("SelectionParent");
            ((Node) selectionShape).attachChild(selectionGeometry);

            toolsNode.attachChild(selectionShape);
        }
    }

    public Node getToolsNode() {
        return toolsNode;
    }

    public void snapCursorToSelection() {
        if (selected != null) {
            cursor.setLocalTranslation(selected.getWorldTranslation());
        }
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        if (showGrid) {
            toolsNode.attachChild(grid);
        } else {
            toolsNode.detachChild(grid);
        }
    }

    public void setShowSelection(boolean showSelection) {
        this.showSelection = showSelection;
        if (showSelection && selected != null && selectionShape == null) {
            attachSelectionShape(selected);
        } else if (!showSelection && selectionShape != null) {
            detachSelectionShape();
        }
    }

    protected void detachSelectionShape() {
        if (selectionShape != null) {
            final Spatial shape = selectionShape;
            selectionShape = null;
            shape.removeFromParent();
        }
    }

    @Override
    protected void initialize(Application app) {
        toolsNode.attachChild(cursor);
        toolsNode.attachChild(grid);
    }

    @Override
    protected void cleanup(Application app) {
        detachSelectionShape();
        grid.removeFromParent();
        cursor.removeFromParent();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (selected == null || selectionShape == null) {
            return;
        }

        selectionShape.setLocalTranslation(selected.getWorldTranslation());
        selectionShape.setLocalRotation(selected.getWorldRotation());
    }

    public Vector3f getCursorLocation() {
        return cursor.getLocalTranslation();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    private Material createBlueMat() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Blue);
        return mat;
    }

    private void initTools() {
        blueMat = createBlueMat();
        Material greenMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        greenMat.getAdditionalRenderState().setWireframe(true);
        greenMat.setColor("Color", ColorRGBA.Green);
        Material grayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        grayMat.getAdditionalRenderState().setWireframe(true);
        grayMat.setColor("Color", ColorRGBA.Gray);


        if (cursor == null) {
            cursor = new Node();
        }
        cursor.detachAllChildren();
        Geometry cursorArrowY = new Geometry("cursorArrowY", new Arrow(new Vector3f(0, -1, 0)));
        cursorArrowY.setLocalTranslation(0, 1, 0);
        cursorArrowY.setMaterial(greenMat);
        cursor.attachChild(cursorArrowY);

        grid = new Geometry("grid", new Grid(20, 20, 1.0f));
        grid.setMaterial(grayMat);
        grid.setLocalTranslation(-10, 0, -10);
    }
}
