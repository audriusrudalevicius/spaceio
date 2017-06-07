package spaceio.game.view.render;

public class SceneNodeProperties {
    private float coordinateX = 0;
    private float coordinateY = 0;
    private float coordinateZ = 0;

    public static SceneNodeProperties createNewBox()
    {
        SceneNodeProperties s = new SceneNodeProperties();
        s.setCoordinateY(-20);
        return s;
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(float x) {
        this.coordinateX = x;
    }

    public float getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(float y) {
        this.coordinateY = y;
    }

    public float getCoordinateZ() {
        return coordinateZ;
    }

    public void setCoordinateZ(float z) {
        this.coordinateZ = z;
    }
}
