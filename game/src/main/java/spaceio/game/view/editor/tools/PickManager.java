package spaceio.game.view.editor.tools;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.game.view.editor.EditorStateInterface;
import spaceio.game.view.editor.EditorStateManager;
import spaceio.game.view.editor.ToolController;
import spaceio.game.view.editor.SceneEditTool;

public class PickManager implements EditorStateInterface {
    public static final Quaternion PLANE_XY = new Quaternion().fromAngleAxis(0, new Vector3f(1, 0, 0));
    public static final Quaternion PLANE_YZ = new Quaternion().fromAngleAxis(-FastMath.PI / 2, new Vector3f(0, 1, 0));//YAW090
    public static final Quaternion PLANE_XZ = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0)); //PITCH090
    private static final Logger log = LoggerFactory.getLogger(PickManager.class);
    private final Node plane;
    private Vector3f startPickLoc;
    private Vector3f finalPickLoc;
    private Vector3f startSpatialLocation;
    private Quaternion origineRotation;
    private Spatial spatial;
    private ToolController.TransformationType transformationType;

    public PickManager() {
        float size = 1000;
        Geometry g = new Geometry("plane", new Quad(size, size));
        g.setLocalTranslation(-size / 2, -size / 2, 0);
        plane = new Node();
        plane.attachChild(g);
    }

    public void initiatePick(Spatial selectedSpatial, Quaternion planeRotation, ToolController.TransformationType type, Camera camera, Vector2f screenCoord) {
        log.info("initiated pick");
        spatial = selectedSpatial;
        startSpatialLocation = selectedSpatial.getWorldTranslation().clone();

        setTransformation(planeRotation, type, camera);
        plane.setLocalTranslation(startSpatialLocation);

        startPickLoc = SceneEditTool.pickWorldLocation(camera, screenCoord, plane, null);
    }

    public void setTransformation(Quaternion planeRotation, ToolController.TransformationType type, Camera camera) {
        Quaternion rot = new Quaternion();
        transformationType = type;
        if (null != transformationType) {
            switch (transformationType) {
                case local:
                    rot.set(spatial.getWorldRotation());
                    rot.multLocal(planeRotation);
                    origineRotation = spatial.getWorldRotation().clone();
                    break;
                case global:
                    rot.set(planeRotation);
                    origineRotation = new Quaternion(Quaternion.IDENTITY);
                    break;
                case camera:
                    rot.set(camera.getRotation());
                    origineRotation = camera.getRotation();
                    break;
                default:
                    break;
            }
        }
        plane.setLocalRotation(rot);
    }

    public void reset() {
        startPickLoc = null;
        finalPickLoc = null;
        startSpatialLocation = null;
        spatial = null;
    }

    public boolean updatePick(Camera camera, Vector2f screenCoord) {
        finalPickLoc = SceneEditTool.pickWorldLocation(camera, screenCoord, plane, null);
        return finalPickLoc != null;
    }

    public Vector3f getTranslation(Vector3f axisConstrainte) {
        Vector3f localConstrainte = (origineRotation.mult(axisConstrainte)).normalize(); // according to the "plane" rotation
        Vector3f constrainedTranslation = localConstrainte.mult(getTranslation().dot(localConstrainte));
        return constrainedTranslation;
    }

    public Vector3f getTranslation() {
        return finalPickLoc.subtract(startPickLoc);
    }

    @Override
    public void stateAttached(EditorStateManager editorStateManager) {

    }

    @Override
    public void cleanup() {

    }

    @Override
    public void stateDetached(EditorStateManager editorStateManager) {

    }

    @Override
    public void initialize(EditorStateManager editorStateManager) {
        log.info("initialize");
    }


    public float getRotation(Quaternion quaternion) {
        // tODO implement
        return 0;
    }
}
