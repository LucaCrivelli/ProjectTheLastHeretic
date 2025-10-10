package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.MouseInput;
import com.jme3.renderer.RenderManager;
import com.jme3.ui.Picture;

public class Main extends SimpleApplication {

    private Player player;
    private java.util.List<Bullet> bullets = new java.util.ArrayList<>();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Disattiva HUD di debug e movimento della camera
        setDisplayStatView(false);
        flyCam.setEnabled(false);

        // === SFONDO ===
        Picture bg = new Picture("Background");
        bg.setImage(assetManager, "Textures/sfondo1.png", true);
        bg.setWidth(settings.getWidth());
        bg.setHeight(settings.getHeight());
        bg.setPosition(0, 0);
        guiNode.attachChild(bg);

        // === PLAYER ===
        player = new Player(assetManager, settings.getWidth(), settings.getHeight());
        guiNode.attachChild(player.getGeometry());

        // === INPUT ===
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Left", "Right", "Jump");

        inputManager.addMapping("Shoot", new com.jme3.input.controls.MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Shoot");
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);

        // Aggiorna e rimuove proiettili disattivati
        java.util.Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(tpf);
            if (!b.isActive()) {
                guiNode.detachChild(b.getGeometry());
                it.remove();
            }
        }

        // Attacca solo i proiettili ancora attivi e non già attaccati (in guiNode)
        for (Bullet b : bullets) {
            if (b.getGeometry().getParent() == null) {
                guiNode.attachChild(b.getGeometry());
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {}

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Left")) player.setLeft(isPressed);
            if (name.equals("Right")) player.setRight(isPressed);
            if (name.equals("Jump") && isPressed) player.jump();

            if (name.equals("Shoot") && isPressed) {
                player.shoot(bullets, assetManager);
            }
        }
    };
}
