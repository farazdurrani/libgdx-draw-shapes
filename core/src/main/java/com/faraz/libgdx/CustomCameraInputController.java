package com.faraz.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CustomCameraInputController extends CameraInputController {

/*    private int scrolledAtX;
    private int scrolledAtY;
    private boolean scrolled;*/

    public CustomCameraInputController(Camera camera) {
        super(camera);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0) {
            zoom(-50f);
        } else {
            zoom(50f);
        }
        Matrix4 projection = camera.projection;
        Matrix4 view = camera.view;
        Vector3 position = camera.position;
        Vector3 up = camera.up;
//        Vector3 worldCoordsBeforeZoom = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
//        update();
        // Find the new world coordinates and adjust the camera position
//        Vector3 worldCoordsAfterZoom = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
//        camera.position.add(worldCoordsBeforeZoom.sub(worldCoordsAfterZoom));
//        camera.lookAt(worldCoordsBeforeZoom.sub(worldCoordsAfterZoom));
//        update();
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        camera.lookAt(-x * 2, -y * 2, 0);
        return true;
    }

    /*@Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (scrolled) {
            System.out.println("x|y==" + screenX + "|" + screenY);
            System.out.println("x|y==" + Gdx.input.getX() + "|" + Gdx.input.getY());
            scrolled = false;
        }
        return super.mouseMoved(screenX, screenY);
    }*/
}
