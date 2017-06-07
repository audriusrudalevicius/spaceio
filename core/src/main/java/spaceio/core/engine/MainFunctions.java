package spaceio.core.engine;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;

public class MainFunctions {
    public static final String EDITOR_GROUP = "Editor";
    public static final String GROUP = "Main";

    public static final FunctionId F_TOGGLE_MOVEMENT = new FunctionId(GROUP, "Toggle Movement");
    public static final FunctionId F_TOGGLE_EDITOR = new FunctionId(GROUP, "Toggle Editor");
    public static final FunctionId F_TOGGLE_EDITOR_TOOLS = new FunctionId(EDITOR_GROUP, "Toggle Editor tools");
    public static final FunctionId F_HUD = new FunctionId(GROUP, "HUD Toggle");

    public static void initializeDefaultMappings( InputMapper inputMapper )
    {
        inputMapper.map( F_TOGGLE_MOVEMENT, KeyInput.KEY_LMENU );
        inputMapper.map( F_HUD, KeyInput.KEY_F3 );
        inputMapper.map( F_TOGGLE_EDITOR, KeyInput.KEY_F2 );
    }

    public static void initializeEditorMappings( InputMapper inputMapper )
    {
        inputMapper.map( F_TOGGLE_EDITOR_TOOLS, KeyInput.KEY_F11 );
    }

    public static void initializeEditorActionsMappings(InputManager inputManager)
    {
        inputManager.addMapping("MouseAxisX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseAxisY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("MouseAxisX-", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MouseAxisY-", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouseWheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("MouseWheel-", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("MouseButtonLeft", new MouseButtonTrigger(0));
        inputManager.addMapping("MouseButtonMiddle", new MouseButtonTrigger(2));
        inputManager.addMapping("MouseButtonRight", new MouseButtonTrigger(1));
    }
}
