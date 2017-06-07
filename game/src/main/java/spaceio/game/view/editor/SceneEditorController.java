package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class SceneEditorController {
    private Application application;

    public SceneEditorController(Application application) {
        this.application = application;
    }

    public void doRotateSpatial(Spatial selected, Quaternion rotation) {
        Quaternion before = new Quaternion(selected.getLocalRotation());
        selected.rotate(rotation);
        Quaternion after = new Quaternion(selected.getLocalRotation());
        rotateUndo(selected, before, after);
    }

    private void rotateUndo(final Spatial spatial, final Quaternion before, final Quaternion after) {
        if (spatial != null && before != null) {
            EditorStateManager.getInstance().getState(SceneUndoRedoManager.class).addEdit(this, new AbstractUndoableSceneEdit(application) {

                @Override
                public void sceneUndo() throws CannotUndoException {
                    //undo stuff here
                    spatial.setLocalRotation(before);
                }

                @Override
                public void sceneRedo() throws CannotRedoException {
                    //redo stuff here
                    spatial.setLocalRotation(after);
                }
            });
        }
    }

    public void doMoveSpatial(Spatial selected, Vector3f translation) {
        Vector3f localTranslation = selected.getLocalTranslation();
        Vector3f before = new Vector3f(localTranslation);
        Node parent = selected.getParent();
        if (parent != null) {
            localTranslation.set(translation).subtractLocal(parent.getWorldTranslation());
            localTranslation.divideLocal(parent.getWorldScale());

            new Quaternion().set(parent.getWorldRotation()).inverseLocal().multLocal(localTranslation);
        } else {
            localTranslation.set(translation);
        }
        Vector3f after = new Vector3f(localTranslation);
        selected.setLocalTranslation(localTranslation);
        moveUndo(selected, before, after);
    }

    private void moveUndo(final Spatial spatial, final Vector3f before, final Vector3f after) {
        if (spatial != null && before != null) {
            EditorStateManager.getInstance().getState(SceneUndoRedoManager.class).addEdit(this, new AbstractUndoableSceneEdit(application) {

                @Override
                public void sceneUndo() throws CannotUndoException {
                    //undo stuff here
                    spatial.setLocalTranslation(before);
                }

                @Override
                public void sceneRedo() throws CannotRedoException {
                    //redo stuff here
                    spatial.setLocalTranslation(after);
                }
            });
        }
    }
}
