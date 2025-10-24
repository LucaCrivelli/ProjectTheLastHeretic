package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.MouseInput;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Luca
 */
public class Main extends SimpleApplication {

    private Player player;
    private java.util.List<Bullet> bullets = new java.util.ArrayList<>();

    // --- Room system ---
    private List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = 0;
    private Picture currentBackground = null;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Disattiva HUD di debug e movimento della camera
        setDisplayStatView(false);
        flyCam.setEnabled(false);

        // Carica/Preload risorse (esempio per Bullet)
        Bullet.preload(assetManager);

        // === INIZIALIZZA STANZE ===
        float sw = settings.getWidth();
        float sh = settings.getHeight();

        // Crea stanze: aggiungi quante vuoi con sfondi diversi
        rooms.add(new Room(assetManager, "Textures/sfondo0.png", sw, sh, "Sala1"));
        rooms.add(new Room(assetManager, "Textures/sfondo1.png", sw, sh, "Sala2"));
        rooms.add(new Room(assetManager, "Textures/sfondo2.png", sw, sh, "Sala3"));
        rooms.add(new Room(assetManager, "Textures/sfondo3.png", sw, sh, "Sala4"));
        

        // PLAYER
        player = new Player(assetManager, sw, sh);
        guiNode.attachChild(player.getNode());

        // Attacca il background della prima stanza
        loadRoom(0);
        
        // ENEMY
        Enemy e1 = new Enemy(assetManager, 100f, 120f, settings.getWidth(), "Textures/enemy_right.png", "Textures/enemy_left.png",40);
        e1.setPatrolBounds(20f, settings.getWidth() - 20f);
        rooms.get(1).addEnemy(e1);

        Enemy e2 = new Enemy(assetManager, 400f, 120f, settings.getWidth(), "Textures/enemy_right.png", "Textures/enemy_left.png",60);
        e2.setPatrolBounds(20f, settings.getWidth() - 20f);
        rooms.get(2).addEnemy(e2);

        // stanza 2 (index 1) non ha nemici — ok se vuoi stanze senza nemici

        Enemy e3 = new Enemy(assetManager, 200f, 120f, settings.getWidth(), "Textures/enemy_right.png", "Textures/enemy_left.png",90);
        e3.setPatrolBounds(20f, settings.getWidth() - 20f);
        rooms.get(2).addEnemy(e3);


        // INPUT
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Left", "Right", "Jump");

        inputManager.addMapping("Shoot", new com.jme3.input.controls.MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Shoot");
    }

    /**
     * Attacca il background della stanza con indice idx.
     * Rimuove il background precedente dal guiNode prima di attaccare il nuovo.
     */
    private void loadRoom(int idx) {
        if (idx < 0 || idx >= rooms.size()) return;
    
        if (currentBackground != null && currentBackground.getParent() != null) {
            guiNode.detachChild(currentBackground);
        }
        // inoltre stacca eventuali nemici del vecchio room (pulizia)
        if (player != null) {
            // non stacchiamo il player
        }
        // stacca tutte le geometrie dei nemici correnti (se attaccate)
        for (Room r : rooms) {
            for (Enemy en : r.getEnemies()) {
                if (en.getGeometry().getParent() != null) {
                    guiNode.detachChild(en.getGeometry());
                }
            }
        }
    
        currentRoomIndex = idx;
        currentBackground = rooms.get(idx).getBackground();
        currentBackground.setLocalTranslation(0, 0, 0f);
        guiNode.attachChild(currentBackground);
    
        // ri-attacca il player (se non già attaccato) e poni z
        if (player != null && player.getNode().getParent() == null) {
            guiNode.attachChild(player.getNode());
        }
        if (player != null) {
            Vector3f ppos = player.getNode().getLocalTranslation();
            player.getNode().setLocalTranslation(ppos.x, ppos.y, 0.1f);
        }
    
        // attacca nemici della stanza corrente
        for (Enemy en : rooms.get(idx).getEnemies()) {
            if (en.getGeometry().getParent() == null) {
                guiNode.attachChild(en.getGeometry());
                Vector3f ep = en.getGeometry().getLocalTranslation();
                en.getGeometry().setLocalTranslation(ep.x, ep.y, 0.15f);
            }
        }
    }    

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);

        float screenWidth = settings.getWidth();
        float px = player.getPosition().x;
        float py = player.getPosition().y;
        float halfW = player.getHalfWidth();

        // --- Blocchi ai bordi prima e ultima stanza ---
        if (currentRoomIndex == 0 && px - halfW < 0) {
            player.setPosition(halfW, py);
            px = halfW;
        }
        if (currentRoomIndex == rooms.size() - 1 && px + halfW > screenWidth) {
            player.setPosition(screenWidth - halfW, py);
            px = screenWidth - halfW;
        }

        float offset = 5f; // distanza dal bordo, puoi cambiare il valore

        // --- Cambio stanza verso destra ---
        if (px - halfW > screenWidth && currentRoomIndex < rooms.size() - 1) {
            loadRoom(currentRoomIndex + 1);
            player.setPosition(0 + offset, py); // bordo sinistro nuova stanza
        }

        // --- Cambio stanza verso sinistra ---
        if (px + halfW < 0 && currentRoomIndex > 0) {
            loadRoom(currentRoomIndex - 1);
            player.setPosition(screenWidth - offset, py); // bordo destro nuova stanza
        }

        // --- Aggiornamento proiettili ---
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(tpf);
            float bx = b.getGeometry().getLocalTranslation().x;
            if (!b.isActive() || bx < 0 || bx > screenWidth) {
                if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
                it.remove();
            } else if (b.getGeometry().getParent() == null) {
                guiNode.attachChild(b.getGeometry());
            }
        }

        // aggiorna nemici solo della stanza corrente
        for (Enemy en : rooms.get(currentRoomIndex).getEnemies()) {
            en.update(tpf, player.getPosition());
            // la geometria è già attaccata in loadRoom; controllo parent per sicurezza
            if (en.getGeometry().getParent() == null) {
                guiNode.attachChild(en.getGeometry());
                Vector3f ep = en.getGeometry().getLocalTranslation();
                en.getGeometry().setLocalTranslation(ep.x, ep.y, 0.15f);
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
