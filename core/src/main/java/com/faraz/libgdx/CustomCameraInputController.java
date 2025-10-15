package com.faraz.libgdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class CustomCameraInputController extends CameraInputController {
    public CustomCameraInputController(FreeFlowingCamera camera) {
        super(camera);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        ((FreeFlowingCamera) camera).processMouseScroll(amountY);
        zoom(((FreeFlowingCamera) camera).getZoom());
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        ((FreeFlowingCamera) camera).processMouseMovement(screenX, screenY); //todo undo
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == forwardKey) {
            ((FreeFlowingCamera) camera).processKeyboard(CameraMovement.FORWARD);
        }
        if (keycode == backwardKey) {
            ((FreeFlowingCamera) camera).processKeyboard(CameraMovement.BACKWARD);
        }
        if (keycode == Input.Keys.A) { // strafe left
            ((FreeFlowingCamera) camera).processKeyboard(CameraMovement.LEFT);
        }
        if (keycode == Input.Keys.A) { // strafe right
            ((FreeFlowingCamera) camera).processKeyboard(CameraMovement.RIGHT);
        }
        return true;
    }

    @Override
    public void update() {
        camera.update();
    }
}
