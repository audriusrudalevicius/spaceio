package spaceio.core.engine;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;

public class MainFunctions {
    public static final String GROUP = "Main";

    public static final FunctionId F_TOGGLE_MOVEMENT = new FunctionId(GROUP, "Toggle Movement");
    public static final FunctionId F_TOGGLE_EDITOR = new FunctionId(GROUP, "Toggle Editor");
    public static final FunctionId F_HUD = new FunctionId(GROUP, "HUD Toggle");

    public static void initializeDefaultMappings( InputMapper inputMapper )
    {
        inputMapper.map( F_TOGGLE_MOVEMENT, KeyInput.KEY_LMENU );
        inputMapper.map( F_HUD, KeyInput.KEY_F3 );
        inputMapper.map( F_TOGGLE_EDITOR, KeyInput.KEY_F2 );
    }
}
