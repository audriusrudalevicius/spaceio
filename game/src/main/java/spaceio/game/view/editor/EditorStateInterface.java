package spaceio.game.view.editor;

public interface EditorStateInterface {
    void stateAttached(EditorStateManager editorStateManager);

    void cleanup();

    void stateDetached(EditorStateManager editorStateManager);

    void initialize(EditorStateManager editorStateManager);
}
