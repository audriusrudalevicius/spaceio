package spaceio.game.view.editor.tools;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import spaceio.game.view.editor.SceneEditTool;

public class SelectTool extends SceneEditTool {
    private boolean wasDraggingR, wasDraggingL = false;
    private boolean wasDownR = false;

    @Override
    public void actionPrimary(Vector2f screenCoord, boolean pressed, final Node rootNode) {
        if (!pressed) {
            if (!wasDraggingL) {
                Vector3f result = pickWorldLocation(getCamera(), screenCoord, rootNode);
                if (result != null) {
                    if (toolController.isSnapToGrid()) {
                        result.set(Math.round(result.x), result.y, Math.round(result.z));
                    }
                    toolController.doSetCursorLocation(result);
                }
            }
            wasDraggingL = false;
        }
    }

    @Override
    public void actionSecondary(final Vector2f screenCoord, boolean pressed, final Node rootNode) {
        if (pressed) {
            Spatial selected;// = toolController.getSelectedSpatial();
            // mouse down

            if (!wasDraggingR && !wasDownR) { // wasn't dragging and was not down already
                // pick on the spot
                Spatial s = pickWorldSpatial(camera, screenCoord, rootNode);
                if (!toolController.isSelectTerrain() && isTerrain(s)) {
                    // only select non-terrain
                    selected = null;
                    return;
                } else {

                    // climb up and find the Model Node (parent) and select that, don't select the geom
                    if (!toolController.isSelectGeometries()) {
                        Spatial linkNodeParent = findModelNodeParent(s);
                        if (linkNodeParent != null) {
                            s = linkNodeParent;
                        } else {
                            return;
                        }
                    }
                    // if the selected spatial is a sub spatial of a linked node
                    // select the linked node
                    s = findAssetLinkNode(s);

                    final Spatial selec = s;
                    selected = selec;
                    java.awt.EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (selec != null) {
                                doSelect();
                            }
                        }

                        private void doSelect() {
                            // in case of linked assets the selected node in the viewer is not necessarily in the explorer.
//                            Node n = rootNode.getChild(selec);
//                            if (n != null) {
                                //set selected node in nodes container view
//                            }
                        }
                    });
                }
                // toolController will be updated by the SceneComposerTopComponent
                toolController.updateSelection(selected);
            }
            wasDownR = true;
        } else {
            // mouse up, stop everything
            wasDownR = false;
            wasDraggingR = false;
        }
    }

    /**
     * Climb up the spatial until we find the first node parent. TODO: use
     * userData to determine the actual model's parent.
     */
    private Spatial findModelNodeParent(Spatial child) {
        if (child == null) {
            return null;
        }

        if (child instanceof Node) {
            return child;
        }

        if (child.getParent() != null) {
            return findModelNodeParent(child.getParent());
        }

        return null;
    }

    /**
     * Search for a AssetLinkNode in the hierarchy of the Spatial spat. If found
     * return the last AssetLinkNode encounter (closest to scene root). Else
     * return the given Spatial spat.
     *
     * @param spat the Spatial to search from.
     * @return a AssetLinkNode if encounter else return spat
     */
    private Spatial findAssetLinkNode(final Spatial spat) {
        Spatial s = spat;
        Spatial linked = null;
        while (s != null) {
            if (s instanceof AssetLinkNode) {
                linked = s;
            }
            s = s.getParent();
        }
        if (linked != null) {
            return linked;
        }
        return spat;
    }

    @Override
    public void mouseMoved(Vector2f screenCoord, Node rootNode) {
    }

    @Override
    public void draggedPrimary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        wasDraggingL = pressed;
    }

    @Override
    public void draggedSecondary(Vector2f screenCoord, boolean pressed, Node rootNode) {
        wasDraggingR = pressed;
    }

    /**
     * Check if the selected item is a Terrain It will climb up the parent tree
     * to see if a parent is terrain too. Recursive call.
     */
    protected boolean isTerrain(Spatial s) {
        if (s == null) {
            return false;
        }
        if (s instanceof Terrain) {
            return true;
        }

        if (s.getParent() != null) {
            return isTerrain(s.getParent());
        }
        return false;
    }
}
