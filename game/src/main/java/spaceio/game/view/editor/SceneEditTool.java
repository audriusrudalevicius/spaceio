package spaceio.game.view.editor;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.scene.*;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Torus;
import com.jme3.util.BufferUtils;
import com.jme3.util.TempVars;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

public abstract class SceneEditTool {

    protected static Vector3f ARROW_X = new Vector3f(1, 0, 0);
    protected static Vector3f ARROW_Y = new Vector3f(0, 1, 0);
    protected static Vector3f ARROW_Z = new Vector3f(0, 0, 1);
    protected static Vector3f QUAD_XY = new Vector3f(1, 1, 0);
    protected static Vector3f QUAD_XZ = new Vector3f(1, 0, 1);

    protected static Vector3f QUAD_YZ = new Vector3f(0, 1, 1);
    protected AssetManager manager;
    protected Camera camera;
    protected ToolController toolController;
    protected Node toolNode;
    protected Node onTopToolNode;
    protected Node axisMarker;
    protected Material redMat, blueMat, greenMat, yellowMat, cyanMat, magentaMat, orangeMat;
    protected Geometry quadXY, quadXZ, quadYZ;
    protected Geometry arrowX, arrowY, arrowZ;
    protected Geometry coneX, coneY, coneZ;
    protected Geometry boxX, boxY, boxZ;
    protected Geometry circleXY, circleXZ, circleYZ;
    protected ToolController.TransformationType transformType;
    protected Application app;
    protected AxisMarkerPickType axisPickType;
    private boolean overrideCameraControl;

    protected void displayPlanes() {
        axisMarker.attachChild(quadXY);
        axisMarker.attachChild(quadXZ);
        axisMarker.attachChild(quadYZ);
    }

    protected void hidePlanes() {
        quadXY.removeFromParent();
        quadXZ.removeFromParent();
        quadYZ.removeFromParent();
    }

    protected void displayArrows() {
        axisMarker.attachChild(arrowX);
        axisMarker.attachChild(arrowY);
        axisMarker.attachChild(arrowZ);
    }

    protected void hideArrows() {
        arrowX.removeFromParent();
        arrowY.removeFromParent();
        arrowZ.removeFromParent();
    }

    protected void displayCones() {
        axisMarker.attachChild(coneX);
        axisMarker.attachChild(coneY);
        axisMarker.attachChild(coneZ);
    }

    protected void hideCones() {
        coneX.removeFromParent();
        coneY.removeFromParent();
        coneZ.removeFromParent();
    }

    protected void displayBoxes() {
        axisMarker.attachChild(boxX);
        axisMarker.attachChild(boxY);
        axisMarker.attachChild(boxZ);
    }

    protected void hideBoxes() {
        boxX.removeFromParent();
        boxY.removeFromParent();
        boxZ.removeFromParent();
    }

    protected void displayCircles() {
        axisMarker.attachChild(circleXY);
        axisMarker.attachChild(circleXZ);
        axisMarker.attachChild(circleYZ);
    }

    protected void hideCircles() {
        circleXY.removeFromParent();
        circleXZ.removeFromParent();
        circleYZ.removeFromParent();
    }

    public static Vector3f pickWorldLocation(Camera cam, Vector2f mouseLoc, Node rootNode) {
        return pickWorldLocation(cam, mouseLoc, rootNode, null);
    }

    public static Spatial pickWorldSpatial(Camera cam, Vector2f mouseLoc, Node rootNode) {
        CollisionResult cr = pick(cam, mouseLoc, rootNode);
        if (cr != null) {
            return cr.getGeometry();
        } else {
            return null;
        }
    }


    public static Vector3f pickWorldLocation(Camera cam, Vector2f mouseLoc, Node rootNode, Spatial exclude) {
        CollisionResult cr = doPick(cam, mouseLoc, rootNode, exclude);
        if (cr != null) {
            return cr.getContactPoint();
        } else {
            return null;
        }
    }

    private static CollisionResult doPick(Camera cam, Vector2f mouseLoc, Node node, Spatial exclude) {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray();
        Vector3f pos = cam.getWorldCoordinates(mouseLoc, 0).clone();
        Vector3f dir = cam.getWorldCoordinates(mouseLoc, 0.3f).clone();
        dir.subtractLocal(pos).normalizeLocal();
        ray.setOrigin(pos);
        ray.setDirection(dir);
        node.collideWith(ray, results);
        CollisionResult result = null;
        if (exclude == null) {
            result = results.getClosestCollision();
        } else {
            Iterator<CollisionResult> it = results.iterator();
            while (it.hasNext()) {
                CollisionResult cr = it.next();
                if (isExcluded(cr.getGeometry(), exclude)) {
                    continue;
                } else {
                    return cr;
                }
            }

        }
        return result;
    }

    private static boolean isExcluded(Spatial s, Spatial exclude) {
        if (s.equals(exclude)) {
            return true;
        }

        if (s.getParent() != null) {
            return isExcluded(s.getParent(), exclude);
        }
        return false;
    }

    private static CollisionResult pick(Camera cam, Vector2f mouseLoc, Node node) {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray();
        Vector3f pos = cam.getWorldCoordinates(mouseLoc, 0).clone();
        Vector3f dir = cam.getWorldCoordinates(mouseLoc, 0.125f).clone();
        dir.subtractLocal(pos).normalizeLocal();
        ray.setOrigin(pos);
        ray.setDirection(dir);
        node.collideWith(ray, results);
        CollisionResult result = results.getClosestCollision();
        return result;
    }

    private static Mesh createCone(int radialSamples, float radius, float height) {
        Mesh cone = new Mesh();

        float fInvRS = 1.0f / radialSamples;

        // Generate points on the unit circle to be used in computing the mesh
        // points on a dome slice.
        float[] afSin = new float[radialSamples];
        float[] afCos = new float[radialSamples];
        for (int i = 0; i < radialSamples; i++) {
            float fAngle = FastMath.TWO_PI * fInvRS * i;
            afCos[i] = FastMath.cos(fAngle);
            afSin[i] = FastMath.sin(fAngle);
        }

        FloatBuffer vb = BufferUtils.createVector3Buffer(radialSamples + 2);
        cone.setBuffer(VertexBuffer.Type.Position, 3, vb);

        TempVars vars = TempVars.get();
        Vector3f tempVa = vars.vect1;

        for (int i = 0; i < radialSamples; i++) {
            Vector3f kRadial = tempVa.set(afCos[i], 0, afSin[i]);
            kRadial.mult(radius, tempVa);
            vb.put(tempVa.x).put(tempVa.y).put(tempVa.z);

            BufferUtils.populateFromBuffer(tempVa, vb, i);

        }
        vars.release();

        // top of the cone
        vb.put(0).put(height).put(0);
        // base of the cone
        vb.put(0).put(0).put(0);

        ShortBuffer ib = BufferUtils.createShortBuffer(3 * (radialSamples) * 2);
        cone.setBuffer(VertexBuffer.Type.Index, 3, ib);

        short top = (short) radialSamples;
        short bot = (short) (radialSamples + 1);

        for (int i = 0; i < radialSamples; i++) {
            short a = (short) i;
            short b = (short) ((i + 1) % radialSamples);
            ib.put(top);
            ib.put(b);
            ib.put(a);

            ib.put(a);
            ib.put(b);
            ib.put(bot);
        }

        cone.updateBound();

        return cone;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void hideMarker() {
        if (axisMarker != null) {
            axisMarker.removeFromParent();
        }
    }

    public boolean isOverrideCameraControl() {
        return overrideCameraControl;
    }

    public void setOverrideCameraControl(boolean overrideCameraControl) {
        this.overrideCameraControl = overrideCameraControl;
    }

    public abstract void actionPrimary(Vector2f screenCoord, boolean pressed, final Node rootNode);

    /**
     * The secondary action for the tool gets activated
     *
     * @param screenCoord the position of the mouse
     * @param pressed     true if the secondary button is pressed, false if released
     */
    public abstract void actionSecondary(Vector2f screenCoord, boolean pressed, final Node rootNode);

    /**
     * Called when the mouse is moved but not dragged (ie no buttons are
     * pressed)
     *
     * @param screenCoord the position of the mouse
     */
    public abstract void mouseMoved(Vector2f screenCoord, final Node rootNode);

    /**
     * Called when the mouse is moved while the primary button is down
     *
     * @param screenCoord the position of the mouse
     * @param pressed     true if the primary button is pressed, false if released
     */
    public abstract void draggedPrimary(Vector2f screenCoord, boolean pressed, final Node rootNode);

    /**
     * Called when the mouse is moved while the secondary button is down
     *
     * @param screenCoord the position of the mouse
     * @param pressed     true if the secondary button is pressed, false if released
     */
    public abstract void draggedSecondary(Vector2f screenCoord, boolean pressed, final Node rootNode);

    public void keyPressed(KeyInputEvent kie) {
    }

    public void activate(Application app, AssetManager manager, Node toolNode, Node onTopToolNode, Spatial selectedSpatial, ToolController toolController) {
        this.app = app;
        this.manager = manager;
        this.toolNode = toolNode;

        this.toolController = toolController;
        this.setTransformType(toolController.getTransformationType());

        addMarker(toolNode, onTopToolNode);
    }

    protected void addMarker(Node toolNode, Node onTopToolNode) {
        this.toolNode = toolNode;
        this.onTopToolNode = onTopToolNode;

        if (axisMarker == null) {
            axisMarker = createAxisMarker();
        }
        axisMarker.removeFromParent();
        this.onTopToolNode.attachChild(axisMarker);
        setDefaultAxisMarkerColors();

        doUpdateToolsTransformation();

    }

    public ToolController.TransformationType getTransformType() {
        return transformType;
    }

    public void setTransformType(ToolController.TransformationType transformType) {
        this.transformType = transformType;
    }

    public void updateToolsTransformation() {

        app.enqueue(() -> {
            doUpdateToolsTransformation();
            return null;
        });
    }

    protected void actionPerformed(AbstractUndoableSceneEdit undoer) {
        EditorStateManager.getInstance().getState(SceneUndoRedoManager.class).addEdit(this, undoer);
        toolController.setNeedsSave();
    }

    public void doUpdateToolsTransformation() {
        if (toolController.getSelectedSpatial() != null) {
            axisMarker.setLocalTranslation(toolController.getSelectedSpatial().getWorldTranslation());
            switch (transformType) {
                case local:
                    axisMarker.setLocalRotation(toolController.getSelectedSpatial().getWorldRotation());
                    break;
                case global:
                    axisMarker.setLocalRotation(Quaternion.IDENTITY);
                    break;
                case camera:
                    if (camera != null) {
                        axisMarker.setLocalRotation(camera.getRotation());
                    }
                    break;
            }
            setAxisMarkerScale(toolController.getSelectedSpatial());
        } else {
            axisMarker.setLocalTranslation(Vector3f.ZERO);
            axisMarker.setLocalRotation(Quaternion.IDENTITY);
        }
    }

    private void setAxisMarkerScale(Spatial selected) {
        if (selected != null) {
            if (selected.getWorldBound() instanceof BoundingBox) {
                BoundingBox bbox = (BoundingBox) selected.getWorldBound();
                float smallest = Math.min(Math.min(bbox.getXExtent(), bbox.getYExtent()), bbox.getZExtent());
                float scale = Math.max(1, smallest / 2f);
                axisMarker.setLocalScale(scale);
            }
        } else {
            axisMarker.setLocalScale(1);
        }
    }

    protected Vector3f pickAxisMarker(Camera cam, Vector2f mouseLoc, AxisMarkerPickType pickType) {
        if (axisMarker == null) {
            return null;
        }

        CollisionResult cr = pick(cam, mouseLoc, axisMarker);
        if (cr == null || cr.getGeometry() == null) {
            return null;
        }

        String collisionName = cr.getGeometry().getName();

        if (pickType != null) {
            if (pickType == AxisMarkerPickType.planeOnly || pickType == AxisMarkerPickType.axisAndPlane) {
                if ("quadXY".equals(collisionName) || "circleXY".equals(collisionName)) {
                    return QUAD_XY;
                } else if ("quadXZ".equals(collisionName) || "circleXZ".equals(collisionName)) {
                    return QUAD_XZ;
                } else if ("quadYZ".equals(collisionName) || "circleYZ".equals(collisionName)) {
                    return QUAD_YZ;
                }
            }
            if (pickType == AxisMarkerPickType.axisOnly || pickType == AxisMarkerPickType.axisAndPlane) {
                if ("arrowX".equals(collisionName) || "coneX".equals(collisionName) || "boxX".equals(collisionName)) {
                    return ARROW_X;
                } else if ("arrowY".equals(collisionName) || "coneY".equals(collisionName) || "boxY".equals(collisionName)) {
                    return ARROW_Y;
                } else if ("arrowZ".equals(collisionName) || "coneZ".equals(collisionName) || "boxZ".equals(collisionName)) {
                    return ARROW_Z;
                }
            }
        }
        return null;
    }

    protected void highlightAxisMarker(Camera camera, Vector2f screenCoord, AxisMarkerPickType axisMarkerPickType) {
        highlightAxisMarker(camera, screenCoord, axisMarkerPickType, false);
    }

    protected void highlightAxisMarker(Camera camera, Vector2f screenCoord, AxisMarkerPickType axisMarkerPickType, boolean colorAll) {
        setDefaultAxisMarkerColors();
        Vector3f picked = pickAxisMarker(camera, screenCoord, axisMarkerPickType);
        if (picked == null) {
            return;
        }

        if (picked == ARROW_X) {
            arrowX.setMaterial(orangeMat);
            coneX.setMaterial(orangeMat);
            boxX.setMaterial(orangeMat);
        } else if (picked == ARROW_Y) {
            arrowY.setMaterial(orangeMat);
            coneY.setMaterial(orangeMat);
            boxY.setMaterial(orangeMat);
        } else if (picked == ARROW_Z) {
            arrowZ.setMaterial(orangeMat);
            coneZ.setMaterial(orangeMat);
            boxZ.setMaterial(orangeMat);
        } else {
            if (picked == QUAD_XY || colorAll) {
                quadXY.setMaterial(orangeMat);
                circleXY.setMaterial(orangeMat);
            }
            if (picked == QUAD_XZ || colorAll) {
                quadXZ.setMaterial(orangeMat);
                circleXZ.setMaterial(orangeMat);
            }
            if (picked == QUAD_YZ || colorAll) {
                quadYZ.setMaterial(orangeMat);
                circleYZ.setMaterial(orangeMat);
            }
        }
    }

    protected Node createAxisMarker() {
        float size = 2;
        float arrowSize = size;
        float planeSize = size * 0.5f;

        Quaternion ROLL090 = new Quaternion().fromAngleAxis(-FastMath.PI / 2, new Vector3f(0, 0, 1));
        Quaternion YAW090 = new Quaternion().fromAngleAxis(-FastMath.PI / 2, new Vector3f(0, 1, 0));
        Quaternion PITCH090 = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0));

        redMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.getAdditionalRenderState().setWireframe(false);
        redMat.setColor("Color", ColorRGBA.Red);
        redMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        redMat.getAdditionalRenderState().setLineWidth(2f);

        greenMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        greenMat.getAdditionalRenderState().setWireframe(false);
        greenMat.setColor("Color", ColorRGBA.Green);
        greenMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        greenMat.getAdditionalRenderState().setLineWidth(2f);

        blueMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMat.getAdditionalRenderState().setWireframe(false);
        blueMat.setColor("Color", ColorRGBA.Blue);
        blueMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        blueMat.getAdditionalRenderState().setLineWidth(2f);

        yellowMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        yellowMat.getAdditionalRenderState().setWireframe(false);
        yellowMat.setColor("Color", new ColorRGBA(1f, 1f, 0f, 0.25f));
        yellowMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        yellowMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        yellowMat.getAdditionalRenderState().setLineWidth(2f);

        cyanMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        cyanMat.getAdditionalRenderState().setWireframe(false);
        cyanMat.setColor("Color", new ColorRGBA(0f, 1f, 1f, 0.25f));
        cyanMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        cyanMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        cyanMat.getAdditionalRenderState().setLineWidth(2f);

        magentaMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        magentaMat.getAdditionalRenderState().setWireframe(false);
        magentaMat.setColor("Color", new ColorRGBA(1f, 0f, 1f, 0.25f));
        magentaMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        magentaMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        magentaMat.getAdditionalRenderState().setLineWidth(2f);

        orangeMat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        orangeMat.getAdditionalRenderState().setWireframe(false);
        orangeMat.setColor("Color", new ColorRGBA(251f / 255f, 130f / 255f, 0f, 0.4f));
        orangeMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        orangeMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        orangeMat.getAdditionalRenderState().setLineWidth(2f);

        Node axis = new Node();

        // create arrows
        arrowX = new Geometry("arrowX", new Arrow(new Vector3f(arrowSize, 0, 0)));
        arrowY = new Geometry("arrowY", new Arrow(new Vector3f(0, arrowSize, 0)));
        arrowZ = new Geometry("arrowZ", new Arrow(new Vector3f(0, 0, arrowSize)));
        axis.attachChild(arrowX);
        axis.attachChild(arrowY);
        axis.attachChild(arrowZ);

        // create planes
        quadXY = new Geometry("quadXY", new Quad(planeSize, planeSize));
        quadXZ = new Geometry("quadXZ", new Quad(planeSize, planeSize));
        quadXZ.setLocalRotation(PITCH090);
        quadYZ = new Geometry("quadYZ", new Quad(planeSize, planeSize));
        quadYZ.setLocalRotation(YAW090);

        // create circles
        float s = arrowSize / 80f; // s = 2/80 = 0.025
        Mesh circle = new Torus(64, 4, s, arrowSize);
        circleXY = new Geometry("circleXY", circle);
        circleXZ = new Geometry("circleXZ", circle);
        circleXZ.setLocalRotation(PITCH090);
        circleYZ = new Geometry("circleYZ", circle);
        circleYZ.setLocalRotation(YAW090);

        // create cones
        float h = arrowSize / 8f;
        float r = arrowSize / 16f;
        Mesh cone = createCone(16, r, h);

        coneX = new Geometry("coneX", cone);
        coneX.move(-h, 0, 0);
        coneX.rotate(ROLL090);

        coneY = new Geometry("coneY", cone);
        coneY.move(0, -h, 0);
        coneY.setLocalRotation(YAW090);

        coneZ = new Geometry("coneZ", cone);
        coneZ.move(0, 0, -h);
        coneZ.setLocalRotation(PITCH090);

        coneX.move(ARROW_X.mult(arrowSize));
        coneY.move(ARROW_Y.mult(arrowSize));
        coneZ.move(ARROW_Z.mult(arrowSize));

        //create boxes
        float ext = arrowSize / 16f;
        Mesh box = new Box(ext, ext, ext);
        boxX = new Geometry("boxX", box);
        boxX.move(-ext, 0, 0);
        boxY = new Geometry("boxY", box);
        boxY.move(0, -ext, 0);
        boxZ = new Geometry("boxZ", box);
        boxZ.move(0, 0, -ext);

        boxX.move(ARROW_X.mult(arrowSize));
        boxY.move(ARROW_Y.mult(arrowSize));
        boxZ.move(ARROW_Z.mult(arrowSize));

        axis.setModelBound(new BoundingBox());
        axis.updateModelBound();
        return axis;
    }

    protected void setDefaultAxisMarkerColors() {
        arrowX.setMaterial(redMat);
        arrowY.setMaterial(greenMat);
        arrowZ.setMaterial(blueMat);

        coneX.setMaterial(redMat);
        coneY.setMaterial(greenMat);
        coneZ.setMaterial(blueMat);

        boxX.setMaterial(redMat);
        boxY.setMaterial(greenMat);
        boxZ.setMaterial(blueMat);

        quadXY.setMaterial(yellowMat);
        quadXZ.setMaterial(magentaMat);
        quadYZ.setMaterial(cyanMat);

        circleXY.setMaterial(blueMat);
        circleXZ.setMaterial(greenMat);
        circleYZ.setMaterial(redMat);
    }

    protected enum AxisMarkerPickType {
        axisOnly, planeOnly, axisAndPlane
    }
}
