package spaceio.game.view;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.*;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.props.PropertyPanel;
import com.simsilica.lemur.style.ElementId;
import spaceio.core.engine.MainFunctions;
import spaceio.game.SpaceGameApplication;
import spaceio.game.gui.common.DialogHelpers;
import spaceio.game.scene.debugScene.DebugSceneRenderState;
import spaceio.game.utils.GameConstants;
import spaceio.game.view.common.PlayerMovementState;
import spaceio.game.view.render.SceneNodeProperties;

import java.util.ArrayList;
import java.util.List;

public class EditorPanelState extends BaseAppState {
    private Container editor;

    @Override
    protected void initialize(Application app) {
        editor = new Container();

        Label title = editor.addChild(new Label("Space.io Editor"));
        title.setFontSize(32);
        title.setInsets(new Insets3f(10, 10, 0, 10));
        title.addMouseListener(DialogHelpers.createDnDMouseListener(title, editor));

        ActionButton toggleMovementState = editor.addChild(new ActionButton(new CallMethodAction("Toggle movement", this, "toggleMovementState")));
        toggleMovementState.setInsets(new Insets3f(10, 10, 10, 10));

        List<VersionedReference> versionsList = new ArrayList<VersionedReference>();

        TabbedPanel tabs = new TabbedPanel(GameConstants.DEFAULT_GUI_STYLE);
        editor.addChild(new RollupPanel("Parameters", tabs, new ElementId("root.rollup"), GameConstants.DEFAULT_GUI_STYLE));

        PropertyPanel properties = new PropertyPanel(new ElementId("nestedProperties"), GameConstants.DEFAULT_GUI_STYLE);

        SceneNodeProperties sceneParams = getState(DebugSceneRenderState.class).getSceneObjectParams();
        properties.addFloatProperty("X", sceneParams, "coordinateX", -100, 100, 0.5f);
        properties.addFloatProperty("Y", sceneParams, "coordinateY", -100, 100, 0.5f);
        properties.addFloatProperty("Z", sceneParams, "coordinateZ", -100, 100, 0.5f);

        versionsList.add(properties.createReference());
        tabs.addTab("Object properties", properties);

        DialogHelpers.scaleDialog(app, editor);

        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.addDelegate(MainFunctions.F_TOGGLE_EDITOR, this, "toggleHud");
    }


    protected void toggleMovementState() {
        getState(PlayerMovementState.class).toggleEnabled();
    }

    @Override
    protected void cleanup(Application app) {
        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.removeDelegate(MainFunctions.F_TOGGLE_EDITOR, this, "toggleHud");

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
