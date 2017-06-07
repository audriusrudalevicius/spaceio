package spaceio.game.view.render;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.core.engine.GeometryGenerators;
import spaceio.core.engine.Materials;
import spaceio.core.octree.Octinfo;
import spaceio.game.SpaceGameApplication;

import java.util.ArrayList;
import java.util.List;

public class OctantSelectionManager extends AbstractAppState {
    public static final int MAX_DEPTH = 14; //maximum allowed octree and drawing step depth
    private static final Logger log = LoggerFactory.getLogger(OctantSelectionManager.class);
    private byte step = 1; // equals to depth, but with a slightly different meaning

    private SpaceGameApplication app;
    private AppStateManager sm;

    private Geometry geometryUnderCursor;

    private Node selectionNode;
    private Node selectionBoxes; //the selection boxes to attach to the scenegraph
    private SelectionControl selectionControl;

    private CollisionResult lastCollisionResult;
    private Octinfo lastSelectionOctinfo;

    //private List<Octinfo> selectionBoxesOi; // a list of Octinfo representing the selection boxes


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        log.info("Initialize");

        this.app = (SpaceGameApplication) app;
        this.sm = stateManager;

        geometryUnderCursor = new Geometry("dummy geometry");
        lastSelectionOctinfo = new Octinfo();

        //init the selection node
        this.selectionNode = new Node("Selection cursor");
        this.selectionNode.attachChild(GeometryGenerators.wireFrameQuad());
        this.selectionControl = new SelectionControl();
        this.selectionNode.addControl(selectionControl);
        this.app.getRootNode().attachChild(selectionNode);

        //init the selection boxes node
        this.selectionBoxes = new Node("Selection boxes");
        this.app.getRootNode().attachChild(selectionBoxes);
    }

    /**
     * Tracks the current selection of the user. Is called by the renderer.
     *
     * @param collisionResult
     * @param oi              the fictitious octant where the selection should be
     */
    public void updateSelection(CollisionResult collisionResult, Octinfo oi) {
        if (collisionResult != null) {
            this.lastCollisionResult = collisionResult;
            this.geometryUnderCursor = collisionResult.getGeometry();

            selectionControl.updateData(collisionResult, oi);
            Octinfo prevOctInfo = lastSelectionOctinfo;
            lastSelectionOctinfo = oi;

            if (prevOctInfo != oi) {
                log.info(String.format("New octree selected - %s", oi.toString()));
            }
        }
    }

    public CollisionResult getLatestCollisionResult() {
        return this.lastCollisionResult;
    }

    public Geometry getObjectUnderCursor() {
        return geometryUnderCursor;
    }

    public Octinfo getLastSelectionOctinfo() {
        return lastSelectionOctinfo;
    }

    public void selectionBoxesClear() {
        selectionBoxes.detachAllChildren();
    }

    public void selectionBoxesAdd(Octinfo oi) {
        if (selectionBoxes.getChild(oi.toString()) == null)
            selectionBoxes.attachChild(GeometryGenerators.getCubeByOctinfo(oi, sm.getState(Materials.class).getSelectionBoxMaterial()));
    }

    /**
     * Returns a list of Octinfos from all the currently selection boxes
     *
     * @return
     */
    public List<Octinfo> selectionBoxesOctinfos() {
        List<Octinfo> ois = new ArrayList<Octinfo>();
        for (Spatial s : selectionBoxes.getChildren()) {
            ois.add((Octinfo) s.getUserData("Octinfo"));
        }
        return ois;
    }

    public byte getStep() {
        return step;
    }

    public void setStep(byte step) {
        if (step > (byte) 0 && step <= MAX_DEPTH) {
            this.step = step;
        }
        log.info("New step size: " + step);
    }

    public void stepIncrease() {
        if (step < MAX_DEPTH) {
            app.getFlyByCamera().setMoveSpeed(app.getFlyByCamera().getMoveSpeed() / 2f);
            this.step++;
        }
        log.info("New step: " + step);
    }

    public void stepDecrease() {
        if (step > (byte) 0) {
            app.getFlyByCamera().setMoveSpeed(app.getFlyByCamera().getMoveSpeed() * 2f);
            this.step--;
        }
        log.info("New step: " + step);
    }
}