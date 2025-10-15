package com.faraz.libgdx;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;

public class CustomFirstPersonCameraController extends FirstPersonCameraController {

    private static final float scrollFactor = -0.1f;
    private static final float translateUnits = 10f; // FIXME auto calculate this based on the target
    private static final float rotSpeed = 0.2f;
    private final Vector3 tmpV1 = new Vector3();
    private int mouseX = 0;
    private int mouseY = 0;

    public CustomFirstPersonCameraController(Camera camera) {
        super(camera);
        velocity = 500f;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return zoom(amountY * scrollFactor * translateUnits);
    }

    private boolean zoom(float amount) {
        camera.translate(tmpV1.set(camera.direction).scl(amount * 50f));
        camera.update();
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int magX = Math.abs(mouseX - screenX);
        int magY = Math.abs(mouseY - screenY);

        if (mouseX > screenX) {
            camera.rotate(Vector3.Y, 1 * magX * rotSpeed);
            camera.update();
        }

        if (mouseX < screenX) {
            camera.rotate(Vector3.Y, -1 * magX * rotSpeed);
            camera.update();
        }

        if (mouseY < screenY) {
            if (camera.direction.y > -0.965) camera.rotate(camera.direction.cpy().crs(Vector3.Y), -1 * magY * rotSpeed);
            camera.update();
        }

        if (mouseY > screenY) {

            if (camera.direction.y < 0.965) camera.rotate(camera.direction.cpy().crs(Vector3.Y), 1 * magY * rotSpeed);
            camera.update();
        }

        mouseX = screenX;
        mouseY = screenY;

        return false;
    }
}
