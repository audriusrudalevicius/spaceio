package spaceio.core.engine.movement;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public interface MovementHandler {
    public void setLocation( Vector3f loc );
    public Vector3f getLocation();

    public void setFacing( Quaternion facing );
    public Quaternion getFacing();
}
