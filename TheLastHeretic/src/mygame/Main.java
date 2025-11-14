package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.MouseInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main extends SimpleApplication {

    private Player player;
    private List<Bullet> bullets = new ArrayList<>();

    // Room system 
    private List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = 0;
    private Picture currentBackground = null;
    private HealthBar healthBar;

    // Dead screen
    private Picture deathScreenGif;
    private Picture deathScreenStatic;
    private Picture continueButton;
    private boolean deathScreenActive = false;
    private float deathScreenTimer = 0f;
    private float deathScreenDelay = 3.0f; // tempo prima di mostrare l'immagine statica
    private boolean showContinueButton = false;


    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Disattiva HUD e camera libera
        setDisplayStatView(false);
        flyCam.setEnabled(false);

        // Preload risorse
        Bullet.preload(assetManager);

        // Dimensioni finestra e scale
        float sw = settings.getWidth();
        float sh = settings.getHeight();
        float scaleY = sh / 768f;

        // === INIZIALIZZA STANZE ===
        rooms.add(new Room(assetManager, "Textures/sfondo0.png", sw, sh, "Sala1"));
        rooms.add(new Room(assetManager, "Textures/sfondo1.png", sw, sh, "Sala2"));
        rooms.add(new Room(assetManager, "Textures/sfondo2.png", sw, sh, "Sala3"));
        rooms.add(new Room(assetManager, "Textures/sfondo3.png", sw, sh, "Sala4"));

        // PLAYER
        player = new Player(assetManager, sw, sh, scaleY);
        guiNode.attachChild(player.getNode());

        // Barra della vita
        float heartWidth = 92f;
        float heartHeight = 92f;
        float startX = 20f;
        float startY = sh - heartHeight - 20f;
        healthBar = new HealthBar(assetManager, 5, startX, startY, heartWidth, heartHeight);
        healthBar.attachToNode(guiNode, 0.5f);

        // Carica la prima stanza
        loadRoom(0);

        // Inizializza nemici
        initEnemies(sw, scaleY);

        // INPUT
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Left", "Right", "Jump");

        inputManager.addMapping("Shoot", new com.jme3.input.controls.MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Shoot");

        // Gestione pulsante CONTINUA nella schermata di morte
        inputManager.addMapping("Click", new com.jme3.input.controls.MouseButtonTrigger(MouseInput.BUTTON_LEFT)); 
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Click") && isPressed && showContinueButton) {
                        // rimuovi schermata di morte
                        guiNode.detachChild(deathScreenStatic);
                        guiNode.detachChild(continueButton);
                        deathScreenActive = false;
                        showContinueButton = false;

                        // riavvia la partita
                        resetGame();
                }
            }
        }, "Click");

    }

    private void initEnemies(float sw, float scaleY) {
        // Svuota nemici vecchi
        for (Room r : rooms) r.getEnemies().clear();

        // Sala 2
        Enemy e1 = new Enemy(assetManager, 100f, 120f * scaleY, sw,
                "Textures/enemy_right.png", "Textures/enemy_left.png", 40, scaleY);
        e1.setPatrolBounds(20f, sw - 20f);
        rooms.get(1).addEnemy(e1);

        // Sala 3
        Enemy e2 = new Enemy(assetManager, 400f, 120f * scaleY, sw,
                "Textures/enemy_right.png", "Textures/enemy_left.png", 60, scaleY);
        e2.setPatrolBounds(20f, sw - 20f);
        rooms.get(2).addEnemy(e2);

        Enemy e3 = new Enemy(assetManager, 200f, 120f * scaleY, sw,
                "Textures/enemy_right.png", "Textures/enemy_left.png", 90, scaleY);
        e3.setPatrolBounds(20f, sw - 20f);
        rooms.get(2).addEnemy(e3);
    }

    private void loadRoom(int idx) {
        if (idx < 0 || idx >= rooms.size()) return;

        // Svuota proiettili
        for (Bullet b : bullets) {
            if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
        }
        bullets.clear();

        // Rimuovi background precedente
        if (currentBackground != null && currentBackground.getParent() != null) guiNode.detachChild(currentBackground);

        // Rimuovi geometrie nemici
        for (Room r : rooms) {
            for (Enemy en : r.getEnemies()) {
                if (en.getGeometry().getParent() != null) guiNode.detachChild(en.getGeometry());
            }
        }

        currentRoomIndex = idx;
        currentBackground = rooms.get(idx).getBackground();
        currentBackground.setLocalTranslation(0, 0, 0f);
        guiNode.attachChild(currentBackground);

        // Ri-attacca barra vita
        if (healthBar != null) healthBar.refresh(player);

        // Ri-attacca player
        if (player != null && player.getNode().getParent() == null) guiNode.attachChild(player.getNode());
        if (player != null) {
            Vector3f ppos = player.getNode().getLocalTranslation();
            player.getNode().setLocalTranslation(ppos.x, ppos.y, 0.1f);
        }

        // Attacca nemici stanza corrente
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

    // --- GESTIONE SCHERMATA DI MORTE ---
    if (player.getCurrentLives() <= 0) {
        if (!deathScreenActive) {
            deathScreenActive = true;
            deathScreenTimer = 0f;
            showContinueButton = false;
            
            // Mostra immagine statica
            deathScreenStatic = new Picture("DeathStatic");
            deathScreenStatic.setImage(assetManager, "Textures/you-died.png", true);
            deathScreenStatic.setWidth(settings.getWidth());
            deathScreenStatic.setHeight(settings.getHeight());
            deathScreenStatic.setPosition(0, 0);
            deathScreenStatic.setLocalTranslation(0, 0, 10f); // z alto sopra tutto
            guiNode.attachChild(deathScreenStatic);
        }
        
        deathScreenTimer += tpf;

        if (deathScreenTimer >= deathScreenDelay && !showContinueButton) {

            // Mostra pulsante Continua
            // Dimensioni proporzionali
            float btnWidth = settings.getWidth() * 0.75f;
            float btnHeight = settings.getHeight() * 0.6f;

            continueButton = new Picture("ContinueButton");
            continueButton.setImage(assetManager, "Textures/continue.png", true);
            continueButton.setWidth(btnWidth);
            continueButton.setHeight(btnHeight);

            // Centrato orizzontalmente e abbassato verticalmente
            float btnX = settings.getWidth() / 1.83f - btnWidth / 2f;
            float btnY = settings.getHeight() * 0.005f;           // 5% dal bordo inferior
            continueButton.setLocalTranslation(btnX, btnY, 11f);
            guiNode.attachChild(continueButton);

            showContinueButton = true;


        }

        // Non eseguire aggiornamenti di gioco mentre la schermata di morte è attiva
        return;
    }

    // --- AGGIORNA PLAYER ---
    player.update(tpf);

    // --- PARAMETRI FINESTRA / PLAYER ---
    float screenWidth = settings.getWidth();
    Vector3f pPos = player.getPosition();
    float pHalfW = player.getHalfWidth();
    float pHalfH = player.getHalfHeight();
    float offset = 5f;

    // --- CAMBIO STANZA ---
    if (currentRoomIndex == 0 && pPos.x - pHalfW < 0) {
        player.setPosition(pHalfW, pPos.y); pPos = player.getPosition();
    }
    if (currentRoomIndex == rooms.size() - 1 && pPos.x + pHalfW > screenWidth) {
        player.setPosition(screenWidth - pHalfW, pPos.y); pPos = player.getPosition();
    }
    if (pPos.x - pHalfW > screenWidth && currentRoomIndex < rooms.size() - 1) {
        loadRoom(currentRoomIndex + 1);
        player.setPosition(0 + offset, pPos.y); pPos = player.getPosition();
    }
    if (pPos.x + pHalfW < 0 && currentRoomIndex > 0) {
        loadRoom(currentRoomIndex - 1);
        player.setPosition(screenWidth - offset, pPos.y); pPos = player.getPosition();
    }

    // --- PROIETTILI ---
    Iterator<Bullet> bit = bullets.iterator();
    List<Enemy> deadEnemies = new ArrayList<>();

    while (bit.hasNext()) {
        Bullet b = bit.next();
        b.update(tpf);
        Vector3f bPos = b.getGeometry().getLocalTranslation().clone();
        boolean removedByCollision = false;

        for (Enemy en : new ArrayList<>(rooms.get(currentRoomIndex).getEnemies())) {
            Vector3f ePosRaw = en.getGeometry().getLocalTranslation().clone();
            float eHalfW_real = en.getHalfWidth();
            float eHalfH_real = en.getHalfHeight();
            Vector3f eCenter = ePosRaw.add(eHalfW_real, eHalfH_real, 0f);

            float eLeft = eCenter.x - eHalfW_real;
            float eRight = eCenter.x + eHalfW_real;
            float eBottom = eCenter.y - eHalfH_real;
            float eTop = eCenter.y + eHalfH_real;

            if (bPos.x >= eLeft && bPos.x <= eRight && bPos.y >= eBottom && bPos.y <= eTop) {
                if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
                bit.remove();
                removedByCollision = true;

                if (en.takeDamageFromBullet()) {
                    if (en.getGeometry().getParent() != null) guiNode.detachChild(en.getGeometry());
                    deadEnemies.add(en);
                }
                break;
            }
        }

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

    if (!deadEnemies.isEmpty()) {
        rooms.get(currentRoomIndex).getEnemies().removeAll(deadEnemies);
    }

    // --- NEMICI ---
    pPos = player.getPosition();
    pHalfW = player.getHitHalfWidth();
    pHalfH = player.getHitHalfHeight();

    for (Enemy en : new ArrayList<>(rooms.get(currentRoomIndex).getEnemies())) {
        en.update(tpf, player.getPosition());
        if (en.getGeometry().getParent() == null) {
            guiNode.attachChild(en.getGeometry());
            Vector3f ep = en.getGeometry().getLocalTranslation();
            en.getGeometry().setLocalTranslation(ep.x, ep.y, 0.15f);
        }

        Vector3f ePosRaw = en.getGeometry().getLocalTranslation().clone();
        float eHalfW_hit = en.getHitHalfWidth();
        float eHalfH_hit = en.getHitHalfHeight();
        float eHalfW_center = en.getHalfWidth();
        float eHalfH_center = en.getHalfHeight();
        Vector3f eCenter = ePosRaw.add(eHalfW_center, eHalfH_center, 0f);

        float dx = Math.abs(eCenter.x - pPos.x);
        float dy = Math.abs(eCenter.y - pPos.y);

        boolean collisionX = dx <= (eHalfW_hit + pHalfW);
        boolean collisionY = dy <= (eHalfH_hit + pHalfH);

        if (collisionX && collisionY) {
            if (player.takeDamage() && healthBar != null) healthBar.loseLife();
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

    private void resetRooms() {
        // Svuota geometrie nemici e liste nemici
        for (Room r : rooms) {
            for (Enemy e : r.getEnemies()) {
                if (e.getGeometry().getParent() != null) guiNode.detachChild(e.getGeometry());
            }
            r.getEnemies().clear();
        }
    
        // Ricrea nemici come all'inizio
        float sw = settings.getWidth();
        float scaleY = settings.getHeight() / 768f;
        initEnemies(sw, scaleY);
    }
    
    private void resetGame() {
        // 1. svuota proiettili
        for (Bullet b : bullets) {
            if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
        }
        bullets.clear();
    
        // 2. reset player
        player.reset();
    
        // 3. reset stanze + nemici
        resetRooms();
    
        // 4. reset barra della vita
        healthBar.refresh(player);
    
        // 5. carica prima stanza
        loadRoom(0);
    }    
}
