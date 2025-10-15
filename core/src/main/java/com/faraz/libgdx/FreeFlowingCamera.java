package com.faraz.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import static com.faraz.libgdx.CameraMovement.*;

public class FreeFlowingCamera extends Camera {

    //from PerspectiveCamera
    public final float fieldOfView;
    final Vector3 tmp = new Vector3();
    // camera Attributes
    private final Vector3 Position;
    private final Vector3 Front;
    private final Vector3 Up = new Vector3(0.0f, 1.0f, 0.0f);
    private final Vector3 WorldUp;
    // camera options
    private final float MovementSpeed;
    private final float MouseSensitivity;
    private final Vector3 Right = new Vector3();
    // euler Angles
    private float Yaw;
    private float Pitch;
    private float Zoom;

    public FreeFlowingCamera(float fieldOfViewY, float viewportWidth, float viewportHeight, Vector3 position) {
        this(fieldOfViewY, viewportWidth, viewportHeight, position, new Vector3(0.0f, 1.0f, 0.0f), -90.0f, 0.0f, new Vector3(0.0f, 0.0f, -1.0f), 2.5f, 0.1f, 45.0f);
    }

    // constructor with vectors
    private FreeFlowingCamera(float fieldOfViewY, float viewportWidth, float viewportHeight, Vector3 position, Vector3 up, float yaw, float pitch, Vector3 front, float movementSpeed, float mouseSensitivity, float zoom) {
        this.fieldOfView = fieldOfViewY;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.Position = position;
        this.WorldUp = up;
        this.Yaw = yaw;
        this.Pitch = pitch;
        this.Front = front;
        this.MovementSpeed = movementSpeed;
        this.MouseSensitivity = mouseSensitivity;
        this.Zoom = zoom;
        update();
    }

    // processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    public void processKeyboard(CameraMovement direction) {
        float velocity = MovementSpeed * Gdx.input.getDeltaX();
        //todo read
        //Problem 1:
//        from FirstPersonCameraController
//        tmp.set(camera.direction).nor().scl(deltaTime * velocity); <-- see the difference between this line and 57 where you're not normalizing?
//        camera.position.add(tmp);
        if (direction == FORWARD) Position.add(Front.scl(velocity));
        if (direction == BACKWARD) Position.sub(Front.scl(velocity));
        if (direction == LEFT) Position.sub(Right.scl(velocity));
        if (direction == RIGHT) Position.add(Right.scl(velocity));
//Problem 2: you're not calling update here. But no worries update is getting called from another flow. But whereever
// it's getting called from, it's update the Position/Direction/Up again and again. No Good.
    }

    // processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    public void processMouseMovement(float xoffset, float yoffset) {
        processMouseMovement(xoffset, yoffset, true);
    }

    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch) {
        xoffset *= MouseSensitivity;
        yoffset *= MouseSensitivity;

        Yaw += xoffset;
        Pitch += yoffset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch) {
            if (Pitch > 89.0f) Pitch = 89.0f;
            if (Pitch < -89.0f) Pitch = -89.0f;
        }

        // update Front, Right and Up Vectors using the updated Euler angles
        update();
    }

    // processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    public void processMouseScroll(float yoffset) {
        Zoom -= yoffset;
        if (Zoom < 1.0f) Zoom = 1.0f;
        if (Zoom > 89.0f) Zoom = 89.0f;
    }

    @Override
    public void update() {
        update(true);
    }

    @Override
    public void update(boolean updateFrustum) {

        // calculates the front vector from the Camera's (updated) Euler Angles
        Vector3 front = new Vector3();
        front.x = MathUtils.cos(MathUtils.degreesToRadians * Yaw) * MathUtils.cos(MathUtils.degreesToRadians * Pitch);
        front.y = MathUtils.sin(MathUtils.degreesToRadians * Pitch);
        front.z = MathUtils.sin(MathUtils.degreesToRadians * Yaw) * MathUtils.cos(MathUtils.degreesToRadians * Pitch);
        Front.set(front.nor());
        // also re-calculate the Right and Up vector
        Right.set(tmp.set(Front).crs(WorldUp).nor());  // normalize the vectors, because their length gets
        // closer to 0 the more you look up or down which results in slower movement.
        Up.set(tmp.set(Right).crs(Front).nor());

        position.set(Position);
        direction.set(Front);
        up.set(Up);

        //from PerspectiveCamera
        float aspect = viewportWidth / viewportHeight;
        projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
        view.setToLookAt(position, tmp.set(position).add(direction), up);
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);

        if (updateFrustum) {
            invProjectionView.set(combined);
            Matrix4.inv(invProjectionView.val);
            frustum.update(invProjectionView);
        }

        //from PerspectiveCamera by my own Position, Front, etc
//        float aspect = viewportWidth / viewportHeight;
//        projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
//        view.setToLookAt(Position, tmp.set(Position).add(Front), Up);
//        combined.set(projection);
//        Matrix4.mul(combined.val, view.val);
//
//        if (updateFrustum) {
//            invProjectionView.set(combined);
//            Matrix4.inv(invProjectionView.val);
//            frustum.update(invProjectionView);
//        }
    }

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4 getViewMatrix() {
        return view.setToLookAt(Position, tmp.set(Position).add(Front), Up);
    }

    public float getZoom() {
        return this.Zoom;
    }
}
