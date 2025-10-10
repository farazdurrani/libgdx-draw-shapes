package com.faraz.libgdx;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class CustomCameraInputController extends CameraInputController {

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
        return super.scrolled(amountX, amountY);
    }
}
