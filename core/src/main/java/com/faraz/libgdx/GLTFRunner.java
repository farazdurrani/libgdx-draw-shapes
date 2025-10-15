package com.faraz.libgdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class GLTFRunner extends ApplicationAdapter {
    public static final String filename = "Grinnell_Lake.glb";
//        public static final String filename = "BoomBox.gltf";
    public static final String data = "/home/faraz/Android/code-workspace/libgdx-drawtriangles/assets/data/tutorial/";
    private final String data_file = "/home/faraz/Android/code-workspace/libgdx-drawtriangles/assets/data/" + filename;
    protected PerspectiveCamera cam;
    protected FirstPersonCameraController camController;
    protected ModelBatch modelBatch;
    protected AssetManager assets;
    protected Array<ModelInstance> instances = new Array<>();
    protected Environment environment;
    protected boolean loading;
    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;
    private int visibleCount;
    private DirectionalLightEx light;
    private static final Vector3 position = new Vector3();


    @Override
    public void create() {
        stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
        stage.addActor(label);
        stringBuilder = new StringBuilder();

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        environment.add(light);

        cam = new PerspectiveCamera(67f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 5100f, 0f);
        cam.lookAt(Vector3.Zero);
        cam.near = 10f;
        cam.far = 30000f;
        cam.update();

        camController = new CustomFirstPersonCameraController(cam);
        Gdx.input.setInputProcessor(camController);

        loadAsset();
        assets.load(data_file, SceneAsset.class);
        loading = true;
    }

    private void doneLoading() {
        Model model = assets.get(data_file, SceneAsset.class).scene.model;
        for (int i = 0; i < model.nodes.size; i++) {
            String id = model.nodes.get(i).id;
            instances.add(new ModelInstance(model, id));
        }
        System.out.println("How many instances " + instances.size);
        loading = false;
    }

    @Override
    public void render() {
        if (loading && assets.update()) {
            doneLoading();
        }
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        visibleCount = 0;
        for (int i = 0; i < instances.size; i++) {
            if (isVisible(cam, instances.get(i))) {
                ModelInstance instance = instances.get(i);
                modelBatch.render(instance, environment);
                visibleCount++;
            }
        }

        modelBatch.end();

        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        label.setText(stringBuilder);
        stage.draw();
    }

    private boolean isVisible(final Camera cam, final ModelInstance instance) {
//        instance.transform.getTranslation(position);
//        return cam.frustum.pointInFrustum(position);
        //todo undo above
        return true;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    private void loadAsset() {
        assets = new AssetManager();
        if (data_file.endsWith(".gltf")) {
            assets.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
        } else {
            assets.setLoader(SceneAsset.class, data_file, new GLBAssetLoader());
        }
    }
}
