package spaceio.game.scene;

import com.jme3.app.Application;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;
import com.simsilica.event.EventBus;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.state.CompositeAppState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.core.engine.GeometryGenerators;
import spaceio.core.engine.MainFunctions;
import spaceio.core.engine.Materials;
import spaceio.core.engine.movement.MovementFunctions;
import spaceio.core.event.GameSessionEvent;
import spaceio.game.engine.GameSessionStateInterface;
import spaceio.game.scene.debugScene.DebugSceneRenderState;
import spaceio.game.view.common.*;
import spaceio.game.view.editor.DebugHud;
import spaceio.game.view.editor.WorldEditorState;
import spaceio.game.view.render.OctantSelectionManager;
import spaceio.game.view.render.Renderer;

import java.util.ArrayList;
import java.util.List;

import static spaceio.game.SpaceGameApplication.INPUT_MAPPING_HIDE_STATS;

public class DebugGameSceneState extends CompositeAppState implements GameSessionStateInterface {

    private static final Logger log = LoggerFactory.getLogger(DebugGameSceneState.class);

    private int clientId;

    private EntityId playerId;
    private EntityId shipId;

    private Application app;
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager stateManager;

    private List<AppState> states;
    private List<Spatial> nodes;

    private AppActionListener actionListener = new AppActionListener();

    public DebugGameSceneState() {
        super();
    }

    @Override
    protected void initialize(Application app) {
        super.initialize(app);
        log.info("initialize");

        EventBus.publish(GameSessionEvent.sessionStarted, new GameSessionEvent());

        this.app = app;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.stateManager = this.app.getStateManager();

        GeometryGenerators.initialize(this.assetManager);

        states = new ArrayList<>();

        states.add(new Lighting());
        states.add(new Hud());
        states.add(new Materials());
        states.add(new Renderer());
        states.add(new DebugHud());
        states.add(new OctantSelectionManager());
        states.add(new SkyState());
        states.add(new PlayerMovementState());
        states.add(new DebugSceneRenderState());
        states.add(new WorldEditorState());

        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        MainFunctions.initializeDefaultMappings(inputMapper);
        inputMapper.activateGroup(MainFunctions.GROUP);
        MovementFunctions.initializeDefaultMappings(inputMapper);

        inputMapper.addDelegate(MainFunctions.F_TOGGLE_EDITOR, this, "toggleEditor");
    }

    @Override
    protected void cleanup(Application app) {
        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.deactivateGroup(MainFunctions.GROUP);
        EventBus.publish(GameSessionEvent.sessionEnded, new GameSessionEvent());
        inputMapper.removeDelegate(MainFunctions.F_TOGGLE_EDITOR_TOOLS, this, "toggleEditor");
        super.cleanup(app);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        this.states.forEach(abstractAppState -> this.stateManager.attach(abstractAppState));
        this.inputManager.addListener(actionListener);
    }

    @Override
    protected void onDisable() {
        this.states.forEach(abstractAppState -> this.stateManager.detach(abstractAppState));
        this.nodes.forEach(Spatial::removeFromParent);
        this.inputManager.removeListener(actionListener);
        super.onDisable();
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }


    @Override
    public void disconnect() {
        getStateManager().detach(this);
    }

    public void toggleEditor() {
        if (getStateManager().getState(WorldEditorState.class) != null) {
            getStateManager().getState(WorldEditorState.class).toggleEnabled();
        }
    }

    private class AppActionListener implements ActionListener {
        public void onAction(String name, boolean value, float tpf) {
            if (name.equals(INPUT_MAPPING_HIDE_STATS)) {
                if (getStateManager().getState(StatsAppState.class) != null) {
                    getStateManager().getState(StatsAppState.class).toggleStats();
                }
            }
        }
    }
}
