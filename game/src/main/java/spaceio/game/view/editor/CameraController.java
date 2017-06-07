package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import spaceio.game.view.editor.camera.AbstractCameraController;

public class CameraController extends AbstractCameraController {
    private ToolController toolController;
    private boolean forceCameraControls = false; // when user holds shift, this is true

    public CameraController(Camera cam, InputManager inputManager, Application application) {
        super(cam, inputManager, application);
    }

    private boolean isEditButtonEnabled() {
        return toolController.isEditToolEnabled();
    }

    public void setToolController(ToolController toolController) {
        this.toolController = toolController;
    }

    public boolean isToolUsesCameraControls() {
        return !toolController.isOverrideCameraControl();
    }

    public Camera getCamera() {
        return cam;
    }

    @Override
    public void onKeyEvent(KeyInputEvent kie) {
        //don't forget the super call
        super.onKeyEvent(kie);
        if (kie.isPressed()) {
            if (KeyInput.KEY_LSHIFT == kie.getKeyCode()) {
                forceCameraControls = true;
            }
        } else if (kie.isReleased()) {
            if (KeyInput.KEY_LSHIFT == kie.getKeyCode()) {
                forceCameraControls = false;
            }
        }
        toolController.doKeyPressed(kie);
    }

    @Override
    public void checkClick(int button, boolean pressed) {
        if (!forceCameraControls || !pressed) { // dont call toolController while forceCam but on button release (for UndoRedo)
            if (button == 0) {
                toolController.doEditToolActivatedPrimary(new Vector2f(mouseX, mouseY), pressed, cam);
            }
            if (button == 1) {
                toolController.doEditToolActivatedSecondary(new Vector2f(mouseX, mouseY), pressed, cam);
            }
        }
    }

    @Override
    protected void checkDragged(int button, boolean pressed) {
        if (!forceCameraControls || !pressed) {
            if (button == 0) {
                toolController.doEditToolDraggedPrimary(new Vector2f(mouseX, mouseY), pressed, cam);
            } else if (button == 1) {
                toolController.doEditToolDraggedSecondary(new Vector2f(mouseX, mouseY), pressed, cam);
            }
        }
    }

    @Override
    protected void checkMoved() {
        toolController.doEditToolMoved(new Vector2f(mouseX, mouseY), cam);
    }

    @Override
    public boolean useCameraControls() {
        return isToolUsesCameraControls() || forceCameraControls;
    }
}
