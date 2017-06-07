package spaceio.core.editor;

import com.simsilica.lemur.input.Axis;
import com.simsilica.lemur.input.Button;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;

public class EditorFunctions {
    public static final String GROUP_EDITOR_CAMERA = "EditorCamera";
    public static final String GROUP_EDITOR_SELECTION = "EditorSelect";

    public static final FunctionId F_Y_LOOK = new FunctionId(GROUP_EDITOR_CAMERA, "Y Look");
    public static final FunctionId F_X_LOOK = new FunctionId(GROUP_EDITOR_CAMERA, "X Look");

    public static final FunctionId F_PRIMARY = new FunctionId(GROUP_EDITOR_SELECTION, "Primary");
    public static final FunctionId F_MIDDLE = new FunctionId(GROUP_EDITOR_SELECTION, "Middle");
    public static final FunctionId F_RIGHT = new FunctionId(GROUP_EDITOR_SELECTION, "Right");

//    public static final FunctionId F_LEFT_MOVE = new FunctionId(GROUP_EDITOR_CAMERA, "Move left");
//    public static final FunctionId F_RIGHT_MOVE = new FunctionId(GROUP_EDITOR_CAMERA, "Move right");
//    public static final FunctionId F_UP_MOVE = new FunctionId(GROUP_EDITOR_CAMERA, "Move up");
//    public static final FunctionId F_DOWN_MOVE = new FunctionId(GROUP_EDITOR_CAMERA, "Move down");

    public static void initializeDefaultMappings(InputMapper inputMapper) {
        inputMapper.map(F_Y_LOOK, Axis.MOUSE_Y);
        inputMapper.map(F_X_LOOK, Axis.MOUSE_X);

        inputMapper.map(F_PRIMARY, Button.MOUSE_BUTTON1);
        inputMapper.map(F_MIDDLE, Button.MOUSE_BUTTON2);
        inputMapper.map(F_RIGHT, Button.MOUSE_BUTTON3);

//        inputMapper.map( F_LEFT_MOVE, KeyInput.KEY_LEFT );
//        inputMapper.map( F_RIGHT_MOVE, KeyInput.KEY_RIGHT );
//        inputMapper.map( F_DOWN_MOVE, KeyInput.KEY_DOWN );
//        inputMapper.map( F_UP_MOVE, KeyInput.KEY_UP );
    }
}
