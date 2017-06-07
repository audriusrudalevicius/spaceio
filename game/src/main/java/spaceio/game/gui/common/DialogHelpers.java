package spaceio.game.gui.common;

import com.jme3.app.Application;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.event.MouseListener;

public class DialogHelpers {

    public static MouseListener createDnDMouseListener(Spatial object, Spatial moveObject) {
        return new MouseListener() {
            boolean mousePressed = false;

            @Override
            public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
                if (target != null && target.equals(object) && event.isPressed()) {
                    mousePressed = true;
                } else {
                    mousePressed = false;
                }
            }

            @Override
            public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {

            }

            @Override
            public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {

            }

            @Override
            public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {
                if (mousePressed) {
                    moveObject.setLocalTranslation(event.getX(), event.getY(), 0);
                }
            }
        };
    }

    public static void scaleDialog(Application application, Container container)
    {
        int height = application.getCamera().getHeight();
        float standardScale = height / application.getContext().getSettings().getHeight();

        Vector3f pref = container.getPreferredSize().clone();

        pref.multLocal(1.5f * standardScale);

        // With a slight bias toward the top
        float y = height * 0.6f + pref.y * 0.5f;

        container.setLocalTranslation(100 * standardScale, y, 0);
        container.setLocalScale(1.5f * standardScale);
    }
}
