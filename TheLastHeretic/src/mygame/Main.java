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

    private Geometry player;
    private float speed = 1f;
    private float jumpForce = 3f;
    private float gravity = -13f;
    private float velocityY = 0;
    private boolean left, right, jumping;

    private float groundY = -0.415f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // Disattiva FPS e statistiche
        setDisplayStatView(false);

        // Blocca la camera
        flyCam.setEnabled(false);

        // Camera ortografica
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        // Sfondo giallo
        Quad background = new Quad(10, 10);
        Geometry bgGeom = new Geometry("Background", background);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", ColorRGBA.Yellow);
        bgGeom.setMaterial(bgMat);
        bgGeom.setLocalTranslation(-5, -5, 0); // Z=0
        rootNode.attachChild(bgGeom);

        // Pavimento blu
        Quad floor = new Quad(1, 1);
        Geometry floorGeom = new Geometry("Floor", floor);
        Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.Blue);
        floorGeom.setMaterial(floorMat);
        floorGeom.setLocalTranslation(-5, groundY - 0.5f, 0.1f); // Z davanti allo sfondo
        rootNode.attachChild(floorGeom);

        // Player rosso (quadrato 1x1 centrato)
        Quad playerQuad = new Quad(0.1f, 0.1f);
        player = new Geometry("Player", playerQuad);
        Material redMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMat.setColor("Color", ColorRGBA.Red);
        player.setMaterial(redMat);
        player.setLocalTranslation(-0.5f, groundY, 0.2f); // Z davanti pavimento
        rootNode.attachChild(player);

        // Input
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Left", "Right", "Jump");
    }

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f pos = player.getLocalTranslation();
        System.out.println("Player pos: " + pos);  // Debug

        // Movimento orizzontale
        if (left) pos.x -= speed * tpf;
        if (right) pos.x += speed * tpf;

        // Gravità
        velocityY += gravity * tpf;
        pos.y += velocityY * tpf;

        // Collisione con il pavimento
        if (pos.y <= groundY) {
            pos.y = groundY;
            velocityY = 0;
            jumping = false;
        }

        player.setLocalTranslation(pos);
    }

    @Override
    public void simpleRender(RenderManager rm) {}

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Left")) left = isPressed;
            if (name.equals("Right")) right = isPressed;

            if (name.equals("Jump") && isPressed && !jumping) {
                velocityY = jumpForce;
                jumping = true;
            }
        }
    };
}
