package spaceio.core.engine.movement;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import spaceio.core.octree.PagedGridInterface;

public class PagedGridMovementHandler implements MovementHandler {

    private Camera camera;

    private Vector3f location = new Vector3f();
    private Vector3f camLoc = new Vector3f();
    private PagedGridInterface pagedGrid;

    public PagedGridMovementHandler( PagedGridInterface pagedGrid, Camera camera ) {
        this.camera = camera;
        this.pagedGrid = pagedGrid;
        setLocation(camera.getLocation());
    }

    protected void setLandLocation( float x, float z ) {
        pagedGrid.setCenterWorldLocation(x, z);
    }

    @Override
    public final void setLocation( Vector3f loc ) {
        // If the camera has not moved then don't bother passing the
        // information on.  It's an easy check for us to make and in
        // JME, sometimes moving a node with lots of children can
        // be expensive if unnnecessary.
        if( loc.x == location.x && loc.y == location.y && loc.z == location.z ) {
            return;
        }

        // Keep the world location.
        location.set(loc);

        // Set just the elevation to the camera
        camLoc.set(0, loc.y, 0);

        // Pass the land location onto the setLandLocation() method for
        // applying to the paged grid.
        setLandLocation(loc.x, loc.z);

        // Give the camera it's new location.
        camera.setLocation(camLoc);
    }

    @Override
    public final Vector3f getLocation() {
        return location;
    }

    @Override
    public void setFacing( Quaternion facing ) {
        camera.setRotation(facing);
    }

    @Override
    public Quaternion getFacing() {
        return camera.getRotation();
    }
}