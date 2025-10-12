package com.faraz.libgdx;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

public class CustomFirstPersonCameraController extends FirstPersonCameraController {
    public CustomFirstPersonCameraController(Camera camera) {
        super(camera);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
