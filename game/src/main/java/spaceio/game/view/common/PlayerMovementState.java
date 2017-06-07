package spaceio.game.view.common;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.core.VersionedHolder;
import com.simsilica.lemur.core.VersionedObject;
import com.simsilica.lemur.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.core.engine.MainFunctions;
import spaceio.core.engine.movement.MovementFunctions;
import spaceio.core.engine.movement.MovementHandler;

public class PlayerMovementState  extends BaseAppState
        implements AnalogFunctionListener, StateFunctionListener {

    static Logger log = LoggerFactory.getLogger(PlayerMovementState.class);

    private InputMapper inputMapper;
    private MovementHandler mover;
    private double turnSpeed = 2.5;  // one half complete revolution in 2.5 seconds
    private double yaw = FastMath.PI;
    private double pitch;
    private double maxPitch = FastMath.HALF_PI;
    private double minPitch = -FastMath.HALF_PI;
    private Quaternion facing = new Quaternion().fromAngles((float)pitch, (float)yaw, 0);
    private double forward;
    private double side;
    private double elevation;
    private double speed = 3.0;
    private VersionedHolder<Vector3f> worldPos = new VersionedHolder<Vector3f>(new Vector3f());
    private boolean mouseReleased = true;

    public void toggleEnabled() {
        setEnabled(!isEnabled());
    }

    public VersionedObject<Vector3f> getWorldPosition() {
        return worldPos;
    }

    public void toggleMouseRelease() {
        mouseReleased = !mouseReleased;
        updateMouseState();
    }

    public boolean isMouseReleased() {
        return mouseReleased;
    }

    public void grabMouse() {
        mouseReleased = false;
        updateMouseState();
    }

    public void releaseMouse() {
        mouseReleased = true;
        updateMouseState();
    }

    private void updateMouseState() {
        GuiGlobals.getInstance().setCursorEventsEnabled(mouseReleased);
        getApplication().getInputManager().setCursorVisible(mouseReleased);
    }

    public void setMovementHandler( MovementHandler mover ) {
        this.mover = mover;
        updateFacing();
    }

    public MovementHandler getMovementHandler() {
        return mover;
    }

    public void setPitch( double pitch ) {
        this.pitch = pitch;
        updateFacing();
    }

    public double getPitch() {
        return pitch;
    }

    public void setYaw( double yaw ) {
        this.yaw = yaw;
        updateFacing();
    }

    public double getYaw() {
        return yaw;
    }

    public void setRotation( Quaternion rotation ) {
        // Do our best
        float[] angle = rotation.toAngles(null);
        this.pitch = angle[0];
        this.yaw = angle[1];
        updateFacing();
    }

    public Quaternion getRotation() {
        return mover.getFacing();
    }

    @Override
    protected void initialize( Application app ) {
        if( this.mover == null ) {
            this.mover = new CameraMovementHandler(app.getCamera());
        }

        if( inputMapper == null )
            inputMapper = GuiGlobals.getInstance().getInputMapper();

        inputMapper.addDelegate( MainFunctions.F_TOGGLE_MOVEMENT, this, "toggleEnabled" );

        // Most of the movement functions are treated as analog.
        inputMapper.addAnalogListener(this,
                MovementFunctions.F_Y_LOOK,
                MovementFunctions.F_X_LOOK,
                MovementFunctions.F_MOVE,
                MovementFunctions.F_ELEVATE,
                MovementFunctions.F_STRAFE);

        // Only run mode is treated as a 'state' or a trinary value.
        // (Positive, Off, Negative) and in this case we only care about
        // Positive and Off.  See MovementFunctions for a description
        // of alternate ways this could have been done.
        inputMapper.addStateListener(this,
                MovementFunctions.F_RUN,
                MovementFunctions.F_SUPER_RUN);
    }

    @Override
    protected void cleanup(Application app) {

        inputMapper.removeDelegate( MainFunctions.F_TOGGLE_MOVEMENT, this, "toggleEnabled" );

        inputMapper.removeAnalogListener( this,
                MovementFunctions.F_Y_LOOK,
                MovementFunctions.F_X_LOOK,
                MovementFunctions.F_MOVE,
                MovementFunctions.F_ELEVATE,
                MovementFunctions.F_STRAFE);
        inputMapper.removeStateListener( this,
                MovementFunctions.F_RUN,
                MovementFunctions.F_SUPER_RUN);
    }

    @Override
    protected void onEnable() {
        // Make sure our input group is enabled
        inputMapper.activateGroup( MovementFunctions.GROUP_MOVEMENT );
        grabMouse();
    }

    @Override
    protected void onDisable() {
        inputMapper.deactivateGroup( MovementFunctions.GROUP_MOVEMENT );
        releaseMouse();
    }

    @Override
    public void update( float tpf ) {

        // 'integrate' camera position based on the current move, strafe,
        // and elevation speeds.
        if( forward != 0 || side != 0 || elevation != 0 ) {
            Vector3f loc = mover.getLocation();

            Quaternion rot = mover.getFacing();
            Vector3f move = rot.mult(Vector3f.UNIT_Z).multLocal((float)(forward * speed * tpf));
            Vector3f strafe = rot.mult(Vector3f.UNIT_X).multLocal((float)(side * speed * tpf));

            // Note: this camera moves 'elevation' along the camera's current up
            // vector because I find it more intuitive in free flight.
            Vector3f elev = rot.mult(Vector3f.UNIT_Y).multLocal((float)(elevation * speed * tpf));

            loc = loc.add(move).add(strafe).add(elev);
            mover.setLocation(loc);
            worldPos.setObject(loc);
        }
    }

    /**
     *  Implementation of the StateFunctionListener interface.
     */
    @Override
    public void valueChanged( FunctionId func, InputState value, double tpf ) {

        // Change the speed based on the current run mode
        // Another option would have been to use the value
        // directly:
        //    speed = 3 + value.asNumber() * 5
        //...but I felt it was slightly less clear here.
        boolean b = value == InputState.Positive;
        if( func == MovementFunctions.F_RUN ) {
            if( b ) {
                speed = 10;
            } else {
                speed = 3;
            }
        } else if( func == MovementFunctions.F_SUPER_RUN ) {
            if( b ) {
                speed = 20;
            } else {
                speed = 3;
            }
        }
    }

    /**
     *  Implementation of the AnalogFunctionListener interface.
     */
    @Override
    public void valueActive( FunctionId func, double value, double tpf ) {

        // Setup rotations and movements speeds based on current
        // axes states.
        if( func == MovementFunctions.F_Y_LOOK ) {
            pitch += -value * tpf * turnSpeed;
            if( pitch < minPitch )
                pitch = minPitch;
            if( pitch > maxPitch )
                pitch = maxPitch;
        } else if( func == MovementFunctions.F_X_LOOK ) {
            double yawDelta = -value * tpf * turnSpeed;
            yaw += yawDelta;
            if( yaw < 0 )
                yaw += Math.PI * 2;
            if( yaw > Math.PI * 2 )
                yaw -= Math.PI * 2;
        } else if( func == MovementFunctions.F_MOVE ) {
            this.forward = value;
            return;
        } else if( func == MovementFunctions.F_STRAFE ) {
            this.side = -value;
            return;
        } else if( func == MovementFunctions.F_ELEVATE ) {
            this.elevation = value;
            return;
        } else {
            return;
        }
        updateFacing();
    }

    protected void updateFacing() {
        facing.fromAngles( (float)pitch, (float)yaw, 0 );
        mover.setFacing(facing);
    }

    public static class CameraMovementHandler implements MovementHandler {
        private Camera camera;

        public CameraMovementHandler(Camera camera) {
            this.camera = camera;
        }

        public Vector3f getLocation() {
            return camera.getLocation();
        }

        public void setLocation(Vector3f loc) {
            camera.setLocation(loc);
        }

        public Quaternion getFacing() {
            return camera.getRotation();
        }

        public void setFacing(Quaternion facing) {
            camera.setRotation(facing);
        }
    }
}
