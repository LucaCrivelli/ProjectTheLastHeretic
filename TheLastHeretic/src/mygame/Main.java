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
    private HealthBar healthBar;

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

        rooms.add(new Room(assetManager, "Textures/sfondo0.png", sw, sh, "Sala1"));
        rooms.add(new Room(assetManager, "Textures/sfondo1.png", sw, sh, "Sala2"));
        rooms.add(new Room(assetManager, "Textures/sfondo2.png", sw, sh, "Sala3"));
        rooms.add(new Room(assetManager, "Textures/sfondo3.png", sw, sh, "Sala4"));

        // PLAYER
        player = new Player(assetManager, sw, sh);
        guiNode.attachChild(player.getNode());

        // Barra della vita
        float heartWidth = 92f;
        float heartHeight = 92f;
        float startX = 20f;
        float startY = settings.getHeight() - heartHeight - 20f;

        healthBar = new HealthBar(assetManager, 5, startX, startY, heartWidth, heartHeight);
        healthBar.attachToNode(guiNode, 0.5f); // attacca sopra lo sfondo

        // Attacca il background della prima stanza
        loadRoom(0);

        // ENEMY
        Enemy e1 = new Enemy(assetManager, 100f, 120f, settings.getWidth(), "Textures/enemy_right.png", "Textures/enemy_left.png", 40);
        e1.setPatrolBounds(20f, settings.getWidth() - 20f);
        rooms.get(1).addEnemy(e1);

        Enemy e2 = new Enemy(assetManager, 400f, 120f, settings.getWidth(), "Textures/enemy_right.png", "Textures/enemy_left.png", 60);
        e2.setPatrolBounds(20f, settings.getWidth() - 20f);
        rooms.get(2).addEnemy(e2);

        Enemy e3 = new Enemy(assetManager, 200f, 120f, settings.getWidth(), "Textures/enemy_right.png", "Textures/enemy_left.png", 90);
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

    private void loadRoom(int idx) {
        if (idx < 0 || idx >= rooms.size()) return;

        // --- Rimuovi tutti i proiettili attivi ---
        for (Bullet b : bullets) {
            if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
        }
        bullets.clear();

        // --- Rimuovi background precedente ---
        if (currentBackground != null && currentBackground.getParent() != null) guiNode.detachChild(currentBackground);

        // --- Rimuovi geometrie nemici ---
        for (Room r : rooms) {
            for (Enemy en : r.getEnemies()) {
                if (en.getGeometry().getParent() != null) guiNode.detachChild(en.getGeometry());
            }
        }

        currentRoomIndex = idx;
        currentBackground = rooms.get(idx).getBackground();
        currentBackground.setLocalTranslation(0, 0, 0f);
        guiNode.attachChild(currentBackground);

        // Ri-attacca la barra della vita
        if (healthBar != null) healthBar.refresh(player);

        // Ri-attacca il player
        if (player != null && player.getNode().getParent() == null) guiNode.attachChild(player.getNode());
        if (player != null) {
            Vector3f ppos = player.getNode().getLocalTranslation();
            player.getNode().setLocalTranslation(ppos.x, ppos.y, 0.1f);
        }

        // Attacca nemici della stanza corrente
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
        // Aggiorna il player (gestisce movimento, gravity, invincibilità, ecc.)
        player.update(tpf);

        // Parametri finestra / player
        float screenWidth = settings.getWidth();
        Vector3f pPos = player.getPosition();
        float pHalfW = player.getHalfWidth();
        float pHalfH = player.getHalfHeight();

        // --- Blocchi ai bordi prima e ultima stanza ---
        if (currentRoomIndex == 0 && pPos.x - pHalfW < 0) {
            player.setPosition(pHalfW, pPos.y);
            pPos = player.getPosition();
        }
        if (currentRoomIndex == rooms.size() - 1 && pPos.x + pHalfW > screenWidth) {
            player.setPosition(screenWidth - pHalfW, pPos.y);
            pPos = player.getPosition();
        }

        float offset = 5f; // distanza dal bordo per riposizionare il player nelle nuove stanze

        // --- Cambio stanza verso destra ---
        if (pPos.x - pHalfW > screenWidth && currentRoomIndex < rooms.size() - 1) {
            loadRoom(currentRoomIndex + 1);
            player.setPosition(0 + offset, pPos.y);
            pPos = player.getPosition();
        }

        // --- Cambio stanza verso sinistra ---
        if (pPos.x + pHalfW < 0 && currentRoomIndex > 0) {
            loadRoom(currentRoomIndex - 1);
            player.setPosition(screenWidth - offset, pPos.y);
            pPos = player.getPosition();
        }

        // --- PROIETTILI (bullet -> enemy)
        // -----------------------
        Iterator<Bullet> bit = bullets.iterator();
        List<Enemy> deadEnemies = new ArrayList<>(); // nemici da rimuovere alla fine

        while (bit.hasNext()) {
            Bullet b = bit.next();
            b.update(tpf);

            // posizione del proiettile (la localTranslation come punto di riferimento)
            Vector3f bPos = b.getGeometry().getLocalTranslation().clone();

            boolean removedByCollision = false;

            // controlla collisione con ciascun nemico nella stanza corrente
            for (Enemy en : new ArrayList<>(rooms.get(currentRoomIndex).getEnemies())) {
                // centro e half-size reali del nemico
                Vector3f ePosRaw = en.getGeometry().getLocalTranslation().clone();
                float eHalfW_real = en.getHalfWidth();
                float eHalfH_real = en.getHalfHeight();
                Vector3f eCenter = ePosRaw.add(eHalfW_real, eHalfH_real, 0f);

                // calcola AABB del nemico 
                float eLeft = eCenter.x - eHalfW_real;
                float eRight = eCenter.x + eHalfW_real;
                float eBottom = eCenter.y - eHalfH_real;
                float eTop = eCenter.y + eHalfH_real;

                //il punto del proiettile è dentro l'AABB del nemico?
                if (bPos.x >= eLeft && bPos.x <= eRight && bPos.y >= eBottom && bPos.y <= eTop) {
                    // collisione: rimuove il proiettile
                    if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
                    bit.remove();
                    removedByCollision = true;

                    // applica danno al nemico
                    boolean died = en.takeDamageFromBullet();
                    if (died) {
                        // rimuove graficamente il nemico e segnala per rimozione logica
                        if (en.getGeometry().getParent() != null) guiNode.detachChild(en.getGeometry());
                        deadEnemies.add(en);
                    } else {
                        // opzionale: effetto hit (flash, suono) — puoi aggiungerlo qui
                    }

                    break; // proiettile consumato -> esce dal loop dei nemici
                }
            }

            // se il proiettile non è stato rimosso da collisione, controlla limiti schermo / stato
            if (!removedByCollision) {
                float bx = b.getGeometry().getLocalTranslation().x;
                if (!b.isActive() || bx < 0 || bx > screenWidth) {
                    if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
                    bit.remove();
                } else if (b.getGeometry().getParent() == null) {
                    guiNode.attachChild(b.getGeometry());
                }
            }
        }

        // Rimuovi dalla lista i nemici morti (pulizia logica)
        if (!deadEnemies.isEmpty()) {
            List<Enemy> roomEnemies = rooms.get(currentRoomIndex).getEnemies();
            roomEnemies.removeAll(deadEnemies);
            // qui puoi aggiungere ulteriori azioni alla morte (drop, punti, ecc.)
        }

        // -----------------------
        // --- NEMICI (update + enemy -> player collision)
        // ricalcola posizione player/half per sicurezza (potrebbe essere cambiata)
        pPos = player.getPosition();
        pHalfW = player.getHitHalfWidth();   // usa hitbox ridotta per collisione con nemici
        pHalfH = player.getHitHalfHeight();

        for (Enemy en : new ArrayList<>(rooms.get(currentRoomIndex).getEnemies())) {
            // aggiorna comportamento nemico
            en.update(tpf, player.getPosition());

            // Assicuriamoci che la geometry sia attaccata al guiNode (se non lo è già)
            if (en.getGeometry().getParent() == null) {
                guiNode.attachChild(en.getGeometry());
                Vector3f ep = en.getGeometry().getLocalTranslation();
                en.getGeometry().setLocalTranslation(ep.x, ep.y, 0.15f);
            }

            // ---- Collisione AABB tra enemy e player (usando hitbox scalate) ----
            // centro del nemico (robusto sia che geometry sia bottom-left o centrata)
            Vector3f ePosRaw = en.getGeometry().getLocalTranslation().clone();
            float eHalfW_hit = en.getHitHalfWidth();
            float eHalfH_hit = en.getHitHalfHeight();
            float eHalfW_center = en.getHalfWidth();
            float eHalfH_center = en.getHalfHeight();
            Vector3f eCenter = ePosRaw.add(eHalfW_center, eHalfH_center, 0f);

            // distanza centro-centro
            float dx = Math.abs(eCenter.x - pPos.x);
            float dy = Math.abs(eCenter.y - pPos.y);

            boolean collisionX = dx <= (eHalfW_hit + pHalfW);
            boolean collisionY = dy <= (eHalfH_hit + pHalfH);

            if (collisionX && collisionY) {
                // il player subisce danno solo se il timer di invincibilità è scaduto
                if (player.takeDamage()) {
                    // aggiorna la barra della vita solo se il danno è effettivo
                    if (healthBar != null) healthBar.loseLife();
                    // opzionale: effetto invincibilità visivo (flash) qui
                }
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
            if (name.equals("Shoot") && isPressed) player.shoot(bullets, assetManager);
        }
    };
}