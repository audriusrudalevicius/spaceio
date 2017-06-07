package spaceio.game.view.editor;

import com.jme3.app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public abstract class AbstractUndoableSceneEdit extends AbstractUndoableEdit {

    public static final Logger log = LoggerFactory.getLogger(AbstractUndoableSceneEdit.class);
    private Application app;

    public AbstractUndoableSceneEdit(Application app) {
        this.app = app;
    }

    public abstract void sceneUndo();

    public abstract void sceneRedo();

    public void awtUndo() {

    }

    public void awtRedo() {

    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        try {
            app.enqueue((Callable<Void>) () -> {
                sceneUndo();
                return null;
            }).get();
        } catch (InterruptedException ex) {
            log.error("Cannot undo - interrupted", ex);
        } catch (ExecutionException ex) {
            log.error("Cannot undo", ex);
        }
        awtUndo();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            app.enqueue((Callable<Void>) () -> {
                sceneRedo();
                return null;
            }).get();
        } catch (InterruptedException ex) {
            log.error("Cannot redo - interrupted", ex);
        } catch (ExecutionException ex) {
            log.error("Cannot redo", ex);
        }
        awtRedo();
    }
}
