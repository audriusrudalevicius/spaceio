package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.util.MemoryUtils;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.input.InputMapper;
import jme3tools.optimize.GeometryBatchFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaceio.core.engine.MainFunctions;
import spaceio.game.SpaceGameApplication;
import spaceio.game.view.common.PlayerMovementState;

public class DebugHud extends BaseAppState {

    private static final Logger logger = LoggerFactory.getLogger(DebugHud.class);
    private SpaceGameApplication app;
    private AssetManager assetManager;
    private Node debugNode;
    private VersionedReference<Vector3f> worldLoc;
    private Runtime runtime = Runtime.getRuntime();

    private Label location;
    private Label memory;
    private Label directMem;

    private long lastUsedMem;
    private long lastMeg100;
    private long lastDirectMem;
    private long lastDirectMeg100;
    private long nextUpdate = System.currentTimeMillis() + 16; // 60 FPS max
    private long nextMemTime = System.currentTimeMillis() + 1000;

    private long frameCounter;
    private long lastFrameCheck;
    private double lastFps;

    private Container debugHud;

    private Node coordinatesNode;

    @Override
    public void initialize(Application app) {
        logger.info("Initialize");
        this.app = (SpaceGameApplication) app;
        assetManager = app.getAssetManager();
        debugNode = new Node("Debug render node");

        attachCoordinateAxes(this.debugNode);

        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.addDelegate(MainFunctions.F_HUD, this, "toggleHud");

        worldLoc = getState(PlayerMovementState.class).getWorldPosition().createReference();

        debugHud = new Container();

        location = debugHud.addChild(new Label("000.00 000.00 00.00"));
        location.setTextHAlignment(HAlignment.Right);
        resetLocation();

        memory = debugHud.addChild(new Label("Mem: 0.0 meg (0.0 %)"));
        memory.setTextHAlignment(HAlignment.Right);

        directMem = debugHud.addChild(new Label("DMem: 0.0 meg / 0"));
        directMem.setTextHAlignment(HAlignment.Right);

        coordinatesNode = createCoordinateGrid();
        GeometryBatchFactory.optimize(coordinatesNode);
    }

    private Node createCoordinateGrid() {
        Node coordinateSystem = new Node("coordinate_system");
        Line xAxisLine = new Line(new Vector3f(-10000, 0, 0), new Vector3f(+10000, 0, 0));
        Line yAxisLine = new Line(new Vector3f(0, -10000, 0), new Vector3f(0, +10000, 0));
        Line zAxisLine = new Line(new Vector3f(0, 0, -10000), new Vector3f(0, 0, 10000));

        Geometry gX = new Geometry("x_axis", xAxisLine);
        Geometry gY = new Geometry("y_axis", yAxisLine);
        Geometry gZ = new Geometry("z_axis", zAxisLine);
        Material mX = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        mX.setColor("Color", ColorRGBA.Red);
        Material mY = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mY.setColor("Color", ColorRGBA.Green);
        Material mZ = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mZ.setColor("Color", ColorRGBA.Blue);
        gX.setMaterial(mX);
        gY.setMaterial(mY);
        gZ.setMaterial(mZ);

        gX.getMaterial().getAdditionalRenderState().setLineWidth(4);
        gY.getMaterial().getAdditionalRenderState().setLineWidth(4);
        gZ.getMaterial().getAdditionalRenderState().setLineWidth(4);

        coordinateSystem.attachChild(gX);
        coordinateSystem.attachChild(gY);
        coordinateSystem.attachChild(gZ);

        // create xz-grid
        int size = 1000;
        Grid xzPlane = new Grid(size, size, 5);
        float offset = size / 2;
        Material gridMat = GuiGlobals.getInstance().createMaterial(ColorRGBA.LightGray, false).getMaterial();
        Geometry xzGrid = new Geometry("XZ-Plane", xzPlane);
        xzGrid.setMaterial(gridMat);
        xzGrid.rotateUpTo(new Vector3f(0.0f, 1.0f, 0.0f));
        xzGrid.setLocalTranslation(new Vector3f(-offset, 0.0f, -offset));
        coordinateSystem.attachChild(xzGrid);

        return coordinateSystem;
    }

    @Override
    protected void cleanup(Application app) {
        InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
        inputMapper.removeDelegate(MainFunctions.F_HUD, this, "toggleHud");
    }

    private void resetLocation() {
        Vector3f v = worldLoc.get();
        String loc = String.format("%.2f, %.2f, %.2f", v.x, v.y, v.z);
        location.setText(loc);
    }

    @Override
    public void update(float tpf) {

        frameCounter++;
        long time = System.currentTimeMillis();
        if (time < nextUpdate)
            return;
        nextUpdate = time + 16; // 60 FPS max

        if (worldLoc.update()) {
            resetLocation();
        }

        if (time < nextMemTime)
            return;
        nextMemTime = time + 1000;

        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        if (lastUsedMem != usedMemory) {
            lastUsedMem = usedMemory;

            long maxMemory = runtime.maxMemory();
            long meg100 = (usedMemory * 100) / (1024 * 1024);
            if (lastMeg100 != meg100) {
                lastMeg100 = meg100;
                double meg = meg100 / 100.0;
                double percent = (usedMemory * 100.0 / maxMemory);
                String mem = String.format("Mem: %.2f meg  (%.1f %%)", meg, percent);
                memory.setText(mem);
            }
        }

        long directUsage = MemoryUtils.getDirectMemoryUsage();
        if (directUsage != lastDirectMem) {
            lastDirectMem = directUsage;

            long meg100 = (directUsage * 100) / (1024 * 1024);
            if (lastDirectMeg100 != meg100) {
                long directCount = MemoryUtils.getDirectMemoryCount();
                double meg = meg100 / 100.0;
                String mem = String.format("DMem: %.2f meg / %d", meg, directCount);
                directMem.setText(mem);
            }
        }

        Camera cam = getApplication().getCamera();
        Vector3f pref = debugHud.getPreferredSize();
        debugHud.setLocalTranslation(cam.getWidth() - pref.x - 10, cam.getHeight() - 10, 0);
    }

    public void toggleHud() {
        setEnabled(!isEnabled());
    }

    private void attachCoordinateAxes(Node n) {
        Arrow arrow = new Arrow(Vector3f.UNIT_X);
        putShape(n, arrow, ColorRGBA.Red);

        arrow = new Arrow(Vector3f.UNIT_Y);
        putShape(n, arrow, ColorRGBA.Green);

        arrow = new Arrow(Vector3f.UNIT_Z);
        putShape(n, arrow, ColorRGBA.Blue);
    }

    private void putShape(Node n, Mesh shape, ColorRGBA color) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(4);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        n.attachChild(g);
    }

    @Override
    protected void onEnable() {
        app.getGuiNode().attachChild(debugHud);
        app.getGuiNode().attachChild(debugNode);
//        app.getRootNode().attachChild(coordinatesNode);
    }

    @Override
    protected void onDisable() {
        debugHud.removeFromParent();
        debugNode.removeFromParent();
//        coordinatesNode.removeFromParent();
    }
}
