package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.props.PropertyPanel;
import com.simsilica.lemur.style.ElementId;
import spaceio.core.engine.MainFunctions;
import spaceio.game.SpaceGameApplication;
import spaceio.game.gui.common.DialogHelpers;
import spaceio.game.scene.debugScene.DebugSceneRenderState;
import spaceio.game.utils.GameConstants;
import spaceio.game.view.common.PlayerMovementState;
import spaceio.game.view.editor.tools.MoveTool;
import spaceio.game.view.editor.tools.RotateTool;
import spaceio.game.view.editor.tools.SelectTool;
import spaceio.game.view.render.SceneNodeProperties;

public class EditorPanelState extends BaseAppState {
    private Container editor;
    private ToolController toolController;
    private SelectTool selectTool = new SelectTool();
    private MoveTool moveTool = new MoveTool();
    private RotateTool rotateTool = new RotateTool();

    public void setToolController(ToolController toolController) {
        this.toolController = toolController;
    }

    @Override
    protected void initialize(Application app) {
        editor = new Container();

        Label title = editor.addChild(new Label("Space.io Editor"));
        title.setFontSize(32);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        title.addMouseListener(DialogHelpers.createDnDMouseListener(title, editor));

        ActionButton toggleMovementState = editor.addChild(new ActionButton(new CallMethodAction("Toggle movement", this, "toggleMovementState")));
        toggleMovementState.setInsets(new Insets3f(10, 10, 10, 10));

        TabbedPanel tabs = new TabbedPanel(GameConstants.DEFAULT_GUI_STYLE);

        PropertyPanel objectProperties = new PropertyPanel(new ElementId("nestedProperties"), GameConstants.DEFAULT_GUI_STYLE);

        SceneNodeProperties sceneParams = getState(DebugSceneRenderState.class).getSceneObjectParams();
        objectProperties.addFloatProperty("X", sceneParams, "coordinateX", -100, 100, 0.5f);
        objectProperties.addFloatProperty("Y", sceneParams, "coordinateY", -100, 100, 0.5f);
        objectProperties.addFloatProperty("Z", sceneParams, "coordinateZ", -100, 100, 0.5f);

        tabs.addTab("Object properties", objectProperties);

        Container toolsPanel = new Container(GameConstants.DEFAULT_GUI_STYLE);

        Button selectButton = new ActionButton(new CallMethodAction("Select", this, "useSelectTool"));
        toolsPanel.addChild(selectButton);

        Button moveButton = new ActionButton(new CallMethodAction("Move", this, "useMoveTool"));
        toolsPanel.addChild(moveButton);

        Button rotateButton = new ActionButton(new CallMethodAction("Rotate", this, "useRotateTool"));
        toolsPanel.addChild(rotateButton);

        editor.addChild(new RollupPanel("Tools", toolsPanel, new ElementId("root.rollup.tools"), GameConstants.DEFAULT_GUI_STYLE));
        editor.addChild(new RollupPanel("Parameters", tabs, new ElementId("root.rollup.parameters"), GameConstants.DEFAULT_GUI_STYLE));

        DialogHelpers.scaleDialog(app, editor);

        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.addDelegate(MainFunctions.F_TOGGLE_EDITOR_TOOLS, this, "toggleHud");
    }

    protected void useRotateTool() {
        if (toolController != null) {
            toolController.enableEditTool(rotateTool);
        }
    }

    protected void useMoveTool() {
        if (toolController != null) {
            toolController.enableEditTool(moveTool);
        }
    }

    protected void useSelectTool() {
        if (toolController != null) {
            toolController.enableEditTool(selectTool);
        }
    }

    protected void toggleMovementState() {
        getState(PlayerMovementState.class).toggleEnabled();
    }

    @Override
    protected void cleanup(Application app) {
        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.removeDelegate(MainFunctions.F_TOGGLE_EDITOR_TOOLS, this, "toggleHud");

    }

    @Override
    protected void onEnable() {
        Node gui = ((SpaceGameApplication) getApplication()).getGuiNode();
        gui.attachChild(editor);
    }

    @Override
    protected void onDisable() {
        editor.removeFromParent();
    }

    public void toggleHud() {
        setEnabled(!isEnabled());
    }
}
