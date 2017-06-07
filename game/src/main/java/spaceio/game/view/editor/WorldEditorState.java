package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.state.CompositeAppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.core.engine.MainFunctions;
import spaceio.game.engine.GameEngine;
import spaceio.game.scene.debugScene.DebugSceneRenderState;
import spaceio.game.view.common.PlayerMovementState;
import spaceio.game.view.editor.tools.PickManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WorldEditorState extends CompositeAppState {

    private static final Logger log = LoggerFactory.getLogger(WorldEditorState.class);
    private final PickManager pickManager;
    private final EditorStateManager editorStateManager;
    private final SceneUndoRedoManager sceneUndoRedoManager;
    private InputManager inputManager;
    private InputListener actionListener;
    private EditorChangeListener editorChangeListener;
    private CameraController cameraController;
    private ToolController toolController;
    private EditorPanelState editorPanelState;
    private Node overlayNode;

    public WorldEditorState() {
        super();
        this.editorStateManager = EditorStateManager.getInstance();
        this.sceneUndoRedoManager = new SceneUndoRedoManager();
        this.pickManager = new PickManager();
        this.editorStateManager.attach(this.sceneUndoRedoManager);
        this.editorStateManager.attach(this.sceneUndoRedoManager);
        this.editorStateManager.attach(this.pickManager);
    }

    @Override
    protected void cleanup(Application app) {
        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.deactivateGroup(MainFunctions.EDITOR_GROUP);
        this.editorStateManager.cleanup();
        super.cleanup(app);
    }

    @Override
    public void update(float tpf) {
        this.editorStateManager.initializePending();
        this.editorStateManager.terminatePending();

        super.update(tpf);
    }

    @Override
    protected void initialize(Application app) {
        super.initialize(app);

        this.editorStateManager.initializePending();
        inputManager = app.getInputManager();
        actionListener = new EditorActionListener();
        overlayNode = app.getStateManager().getState(GameEngine.class).getToolsRoot();
        log.info("initialize");

        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.activateGroup(MainFunctions.EDITOR_GROUP);
        MainFunctions.initializeEditorMappings(inputMapper);
        MainFunctions.initializeEditorActionsMappings(app.getInputManager());

        this.editorChangeListener = new EditorChangeListener();
        Node rootNode = app.getStateManager().getState(DebugSceneRenderState.class).getNode();
        cameraController = new CameraController(app.getCamera(), app.getInputManager(), app);
        editorPanelState = new EditorPanelState();
        toolController = new ToolController(app, rootNode);

        toolController.setShowSelection(true);
        toolController.setShowGrid(true);

        toolController.setCamController(cameraController);
        cameraController.setToolController(toolController);
        editorPanelState.setToolController(toolController);


        app.getStateManager().attach(cameraController);
        app.getStateManager().attach(toolController);
        app.getStateManager().attach(editorPanelState);

        this.cameraController.enable();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.inputManager.addListener(actionListener);
        this.sceneUndoRedoManager.addChangeListener(editorChangeListener);

        overlayNode.attachChild(toolController.getToolsNode());
        overlayNode.attachChild(toolController.getOnTopToolsNode());
        getApplication().getStateManager().getState(PlayerMovementState.class).setEnabled(false);
    }

    @Override
    protected void onDisable() {
        this.inputManager.removeListener(actionListener);
        this.sceneUndoRedoManager.removeChangeListener(editorChangeListener);
        toolController.getToolsNode().removeFromParent();
        toolController.getOnTopToolsNode().removeFromParent();
        this.cameraController.disable();
        getApplication().getStateManager().getState(PlayerMovementState.class).setEnabled(true);
        super.onDisable();
    }

    @Override
    public void render(RenderManager rm) {
        overlayNode.updateGeometricState();
        super.render(rm);
    }

    public void toggleEnabled() {
        setEnabled(!isEnabled());
    }

    private class EditorActionListener implements ActionListener {

        public void onAction(String name, boolean value, float tpf) {
            if (!value) {
                return;
            }
        }
    }

    private class EditorChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            log.info(String.format("Change made on %s ", e.toString()));
        }
    }
}
