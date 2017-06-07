package spaceio.game.view.editor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

public class SceneUndoRedoManager extends UndoRedo.Manager implements EditorStateInterface {

    public void addEdit(final Object source, final UndoableEdit edit) {
        undoableEditHappened(new UndoableEditEvent(source, edit));
    }

    public void discardAllEdits() {
        super.discardAllEdits();
    }

    @Override
    public void stateAttached(EditorStateManager editorStateManager) {

    }

    @Override
    public void cleanup() {
        this.discardAllEdits();
    }

    @Override
    public void stateDetached(EditorStateManager editorStateManager) {

    }

    @Override
    public void initialize(EditorStateManager editorStateManager) {

    }
}
