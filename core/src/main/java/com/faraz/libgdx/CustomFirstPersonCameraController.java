package com.faraz.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;

public class CustomFirstPersonCameraController extends FirstPersonCameraController {

    private static final float scrollFactor = -0.1f;
    private static final float translateUnits = 10f; // FIXME auto calculate this based on the target
    private final Vector3 tmpV1 = new Vector3();

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
}
