package spaceio.game.view.editor.tools;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import spaceio.game.view.editor.AbstractUndoableSceneEdit;
import spaceio.game.view.editor.EditorStateManager;
import spaceio.game.view.editor.SceneEditTool;
import spaceio.game.view.editor.ToolController;

public class RotateTool extends SceneEditTool {
    private Vector3f pickedMarker;
    private Quaternion startRotate;
    private Quaternion startWorldRotate;
    private Quaternion lastRotate;
    private boolean wasDragging = false;
    private PickManager pickManager;
    private Application application;

    public RotateTool() {
        axisPickType = SceneEditTool.AxisMarkerPickType.planeOnly;
        setOverrideCameraControl(true);
    }

    @Override
    public void activate(Application application, AssetManager manager, Node toolNode, Node onTopToolNode, Spatial selectedSpatial, ToolController toolController) {
        this.application = application;
        super.activate(application, manager, toolNode, onTopToolNode, selectedSpatial, toolController);
        this.pickManager = EditorStateManager.getInstance().getState(PickManager.class);
        displayCircles();
    }

    @Override
    public void actionPrimary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        if (!pressed) {
            setDefaultAxisMarkerColors();
            pickedMarker = null; // mouse released, reset selection
            if (wasDragging) {
                actionPerformed(new RotateUndo(application, toolController.getSelectedSpatial(), startRotate, lastRotate));
                wasDragging = false;
            }
            pickManager.reset();
        } else {
            if (toolController.getSelectedSpatial() == null) {
                return;
            }

            if (pickedMarker == null) {
                pickedMarker = pickAxisMarker(camera, screenCoord, axisPickType);
                if (pickedMarker == null) {
                    return;
                }

                if (pickedMarker.equals(QUAD_XY)) {
                    pickManager.initiatePick(toolController.getSelectedSpatial(), PickManager.PLANE_XY, getTransformType(), camera, screenCoord);
                } else if (pickedMarker.equals(QUAD_XZ)) {
                    pickManager.initiatePick(toolController.getSelectedSpatial(), PickManager.PLANE_XZ, getTransformType(), camera, screenCoord);
                } else if (pickedMarker.equals(QUAD_YZ)) {
                    pickManager.initiatePick(toolController.getSelectedSpatial(), PickManager.PLANE_YZ, getTransformType(), camera, screenCoord);
                }
                startRotate = toolController.getSelectedSpatial().getLocalRotation().clone();
                startWorldRotate = toolController.getSelectedSpatial().getWorldRotation().clone();
                wasDragging = true;
            }
        }
    }

    @Override
    public void actionSecondary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        if (pressed) {
            cancel();
        }
    }

    @Override
    public void mouseMoved(Vector2f screenCoord, Node rootNode) {
        if (pickedMarker == null) {
            highlightAxisMarker(camera, screenCoord, axisPickType);
        } else {
            pickedMarker = null;
            pickManager.reset();
        }
    }

    @Override
    public void draggedPrimary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        if (!pressed) {
            setDefaultAxisMarkerColors();
            pickedMarker = null; // mouse released, reset selection

            if (wasDragging) {
                actionPerformed(new RotateUndo(application, toolController.getSelectedSpatial(), startRotate, lastRotate));
                wasDragging = false;
            }
            pickManager.reset();
        } else if (wasDragging) {
            if (!pickManager.updatePick(camera, screenCoord)) {
                return;
            }

            if (pickedMarker.equals(QUAD_XY) || pickedMarker.equals(QUAD_XZ) || pickedMarker.equals(QUAD_YZ)) {
                Quaternion rotation = startRotate.mult(pickManager.getRotation(startWorldRotate.inverse()));
                toolController.getSelectedSpatial().setLocalRotation(rotation);
                lastRotate = rotation;
            }
            updateToolsTransformation();
        }
    }

    @Override
    public void draggedSecondary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        if (pressed) {
            cancel();
        }
    }

    private void cancel() {
        if (wasDragging) {
            wasDragging = false;
            toolController.getSelectedSpatial().setLocalRotation(startRotate);
            setDefaultAxisMarkerColors();
            pickedMarker = null; // mouse released, reset selection
            pickManager.reset();
        }
    }

    private class RotateUndo extends AbstractUndoableSceneEdit {

        private Spatial spatial;
        private Quaternion before, after;

        RotateUndo(Application application, Spatial spatial, Quaternion before, Quaternion after) {
            super(application);
            this.spatial = spatial;
            this.before = before;
            this.after = after;
        }

        @Override
        public void sceneUndo() {
            spatial.setLocalRotation(before);
            toolController.selectedSpatialTransformed();
        }

        @Override
        public void sceneRedo() {
            spatial.setLocalRotation(after);
            toolController.selectedSpatialTransformed();
        }
    }
}
