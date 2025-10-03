package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class Main extends SimpleApplication {

    private Player player;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Disattiva FPS e statistiche
        setDisplayStatView(false);
        flyCam.setEnabled(false);

        // Camera ortografica
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Sfondo giallo
        Quad background = new Quad(10, 10);
        Geometry bgGeom = new Geometry("Background", background);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setTexture("ColorMap", assetManager.loadTexture("Textures/sfondo1.png"));
        bgMat.setColor("Color", ColorRGBA.Yellow);
        bgGeom.setMaterial(bgMat);
        bgGeom.setLocalTranslation(-5, -5, 0); 
        rootNode.attachChild(bgGeom);
        /*
        // Pavimento blu
        Quad floor = new Quad(1, 1);
        Geometry floorGeom = new Geometry("Floor", floor);
        Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.Blue);
        floorGeom.setMaterial(floorMat);
        floorGeom.setLocalTranslation(-5, -0.415f - 0.5f, 0.1f);
        rootNode.attachChild(floorGeom);
        */
        // Crea il player
        player = new Player(assetManager);
        rootNode.attachChild(player.getGeometry());

        // Input
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Left", "Right", "Jump");
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {}

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Left")) player.setLeft(isPressed);
            if (name.equals("Right")) player.setRight(isPressed);
            if (name.equals("Jump") && isPressed) player.jump();
        }
    };
}
