package spaceio.game.view.editor.tools;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import spaceio.game.view.editor.AbstractUndoableSceneEdit;
import spaceio.game.view.editor.EditorStateManager;
import spaceio.game.view.editor.ToolController;
import spaceio.game.view.editor.SceneEditTool;

public class MoveTool extends SceneEditTool {
    private final AxisMarkerPickType axisPickType;
    private Vector3f pickedMarker;
    private Vector3f constraintAxis; //used for one axis move
    private boolean wasDragging = false;
    private Vector3f startPosition;
    private Vector3f lastPosition;
    private PickManager pickManager;

    public MoveTool() {
        axisPickType = SceneEditTool.AxisMarkerPickType.axisAndPlane;
        setOverrideCameraControl(true);
    }

    @Override
    public void activate(Application app, AssetManager manager, Node toolNode, Node onTopToolNode, Spatial selectedSpatial, ToolController toolController) {
        super.activate(app, manager, toolNode, onTopToolNode, selectedSpatial, toolController);
        this.pickManager = EditorStateManager.getInstance().getState(PickManager.class);
        displayPlanes();
        displayCones();
    }

    @Override
    public void actionPrimary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        if (!pressed) {
            setDefaultAxisMarkerColors();
            pickedMarker = null; // mouse released, reset selection
            constraintAxis = Vector3f.UNIT_XYZ; // no constraint
            if (wasDragging) {
                actionPerformed(new MoveUndo(app, toolController.getSelectedSpatial(), startPosition, lastPosition));
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
                } else if (pickedMarker.equals(ARROW_X)) {
                    pickManager.initiatePick(toolController.getSelectedSpatial(), PickManager.PLANE_XY, getTransformType(), camera, screenCoord);
                    constraintAxis = Vector3f.UNIT_X; // move only X
                } else if (pickedMarker.equals(ARROW_Y)) {
                    pickManager.initiatePick(toolController.getSelectedSpatial(), PickManager.PLANE_YZ, getTransformType(), camera, screenCoord);
                    constraintAxis = Vector3f.UNIT_Y; // move only Y
                } else if (pickedMarker.equals(ARROW_Z)) {
                    pickManager.initiatePick(toolController.getSelectedSpatial(), PickManager.PLANE_XZ, getTransformType(), camera, screenCoord);
                    constraintAxis = Vector3f.UNIT_Z; // move only Z
                }
                startPosition = toolController.getSelectedSpatial().getLocalTranslation().clone();
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
            constraintAxis = Vector3f.UNIT_XYZ; // no constraint
            if (wasDragging) {
                actionPerformed(new MoveUndo(app, toolController.getSelectedSpatial(), startPosition, lastPosition));
                wasDragging = false;
            }
            pickManager.reset();
        } else if (wasDragging == true) {
            if (!pickManager.updatePick(camera, screenCoord)) {
                return;
            }
            Vector3f diff = Vector3f.ZERO;
            if (pickedMarker.equals(QUAD_XY) || pickedMarker.equals(QUAD_XZ) || pickedMarker.equals(QUAD_YZ)) {
                diff = pickManager.getTranslation();

            } else if (pickedMarker.equals(ARROW_X) || pickedMarker.equals(ARROW_Y) || pickedMarker.equals(ARROW_Z)) {
                diff = pickManager.getTranslation(constraintAxis);
            }
            Vector3f position;
            Spatial parent = toolController.getSelectedSpatial().getParent();
            if (parent != null) {
                position = startPosition.add(parent.getWorldRotation().inverse().mult(diff));
            } else {
                position = startPosition.add(diff);
            }
            lastPosition = position;
            toolController.getSelectedSpatial().setLocalTranslation(position);
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
            toolController.getSelectedSpatial().setLocalTranslation(startPosition);
            setDefaultAxisMarkerColors();
            pickedMarker = null; // mouse released, reset selection
            constraintAxis = Vector3f.UNIT_XYZ; // no constraint
        }
    }

    protected class MoveUndo extends AbstractUndoableSceneEdit {

        private Spatial spatial;
        private Vector3f before = new Vector3f(), after = new Vector3f();

        MoveUndo(Application app, Spatial spatial, Vector3f before, Vector3f after) {
            super(app);
            this.spatial = spatial;
            this.before.set(before);
            if (after != null) {
                this.after.set(after);
            }
        }

        @Override
        public void sceneUndo() {
            spatial.setLocalTranslation(before);
            RigidBodyControl control = spatial.getControl(RigidBodyControl.class);
            if (control != null) {
                control.setPhysicsLocation(spatial.getWorldTranslation());
            }
            CharacterControl character = spatial.getControl(CharacterControl.class);
            if (character != null) {
                character.setPhysicsLocation(spatial.getWorldTranslation());
            }
            //     toolController.selectedSpatialTransformed();
        }

        @Override
        public void sceneRedo() {
            spatial.setLocalTranslation(after);
            RigidBodyControl control = spatial.getControl(RigidBodyControl.class);
            if (control != null) {
                control.setPhysicsLocation(spatial.getWorldTranslation());
            }
            CharacterControl character = spatial.getControl(CharacterControl.class);
            if (character != null) {
                character.setPhysicsLocation(spatial.getWorldTranslation());
            }
            //toolController.selectedSpatialTransformed();
        }

        public void setAfter(Vector3f after) {
            this.after.set(after);
        }
    }
}
