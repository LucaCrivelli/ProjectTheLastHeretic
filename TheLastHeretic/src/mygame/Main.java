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

public class Main extends SimpleApplication {

    public static boolean insideTrash = false;
    private TrashCan currentTrash = null;

    private Player player;
    private List<Bullet> bullets = new ArrayList<>();

    private List<Room> rooms = new ArrayList<>();
    private int currentRoomIndex = 0;
    private Picture currentBackground = null;
    private HealthBar healthBar;

    // Death screen
    private Picture deathScreenGif;
    private Picture deathScreenStatic;
    private Picture continueButton;
    private boolean deathScreenActive = false;
    private float deathScreenTimer = 0f;
    private float deathScreenDelay = 3.0f;
    private boolean showContinueButton = false;


    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);
        flyCam.setEnabled(false);

        Bullet.preload(assetManager);

        float sw = settings.getWidth();
        float sh = settings.getHeight();
        float scaleY = sh / 768f;
        float scaleX = sw / 1024f;

        // === ROOM CREATION ===
        rooms.add(new Room(assetManager, "Textures/sfondo0.png", sw, sh, "Sala1"));
        rooms.add(new Room(assetManager, "Textures/sfondo1.png", sw, sh, "Sala2"));
        rooms.add(new Room(assetManager, "Textures/sfondo2.png", sw, sh, "Sala3"));
        rooms.add(new Room(assetManager, "Textures/sfondo3.png", sw, sh, "Sala4"));
        rooms.add(new Room(assetManager, "Textures/parcheggio.png", sw, sh, "Sala5"));

        // === Aggiunta bidone stanza 2 ===
        rooms.get(3).addTrashCan(new TrashCan(assetManager, 600f, 110f, 178f, 178f, scaleX, scaleY));

        player = new Player(assetManager, sw, sh, scaleX, scaleY);
        guiNode.attachChild(player.getNode());

        healthBar = new HealthBar(assetManager, 5, 20f, sh - 112f, 92f, 92f);
        healthBar.attachToNode(guiNode, 0.5f);

        loadRoom(0);

        initEnemies(sw, scaleX, scaleY);

        // INPUT
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Shoot", new com.jme3.input.controls.MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // NUOVO — entrare nel bidone
        inputManager.addMapping("EnterTrash", new KeyTrigger(KeyInput.KEY_W));

        inputManager.addListener(actionListener, "Left", "Right", "Jump", "Shoot", "EnterTrash");

        // CLICK per pulsante continua
        inputManager.addMapping("Click", new com.jme3.input.controls.MouseButtonTrigger(MouseInput.BUTTON_LEFT)); 
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("Click") && isPressed && showContinueButton) {
                    guiNode.detachChild(deathScreenStatic);
                    guiNode.detachChild(continueButton);
                    deathScreenActive = false;
                    showContinueButton = false;
                    resetGame();
                }
            }
        }, "Click");
    }

    private void initEnemies(float sw, float scaleX,float scaleY) {
        for (Room r : rooms) r.getEnemies().clear();

        Enemy e1 = new Enemy(assetManager, 100f, 120f * scaleY, sw, "Textures/enemy_right.png", "Textures/enemy_left.png", 40, scaleX ,scaleY);
        e1.setPatrolBounds(20f, sw - 20f);
        rooms.get(1).addEnemy(e1);

        Enemy e2 = new Enemy(assetManager, 400f, 120f * scaleY, sw, "Textures/enemy_right.png", "Textures/enemy_left.png", 60, scaleX, scaleY);
        e2.setPatrolBounds(20f, sw - 20f);
        rooms.get(2).addEnemy(e2);

        Enemy e3 = new Enemy(assetManager, 200f, 120f * scaleY, sw, "Textures/enemy_right.png", "Textures/enemy_left.png", 90, scaleX, scaleY);
        e3.setPatrolBounds(20f, sw - 20f);
        rooms.get(2).addEnemy(e3);
    }

    private void loadRoom(int idx) {
        if (idx < 0 || idx >= rooms.size()) return;

        // rimuovi oggetti stanza precedente
        for (Bullet b : bullets)
            if (b.getGeometry().getParent() != null) guiNode.detachChild(b.getGeometry());
        bullets.clear();

        if (currentBackground != null)
            guiNode.detachChild(currentBackground);

        // rimuovi trash della stanza precedente
        TrashCan old = rooms.get(currentRoomIndex).getTrashCan();
        if (old != null && old.getPicture().getParent() != null)
            guiNode.detachChild(old.getPicture());

        // rimuovi nemici
        for (Room r : rooms)
            for (Enemy en : r.getEnemies())
                if (en.getGeometry().getParent() != null)
                    guiNode.detachChild(en.getGeometry());

        currentRoomIndex = idx;
        currentBackground = rooms.get(idx).getBackground();
        guiNode.attachChild(currentBackground);

        // Riattacca player
        if (!insideTrash) {
            guiNode.detachChild(player.getNode());
            guiNode.attachChild(player.getNode());
        }

        // Attacca nemici nuova stanza
        for (Enemy en : rooms.get(idx).getEnemies()) {
            guiNode.attachChild(en.getGeometry());
        }

        // Attacca il trash can se esiste
        if (rooms.get(idx).getTrashCan() != null){
            guiNode.attachChild(rooms.get(idx).getTrashCan().getPicture());
        }

        player.getNode().setLocalTranslation(
            player.getPosition().x,
            player.getPosition().y,
            0.2f
        );
    }


    @Override
    public void simpleUpdate(float tpf) {
        float scaleX = settings.getWidth() / 1024;

        // Schermata morte
        if (player.getCurrentLives() <= 0) {
            handleDeathScreen(tpf);
            return;
        }

        // PLAYER BLOCCATO NEL BIDONE
        if (!insideTrash) {
            player.update(tpf);
        }

        float screenWidth = settings.getWidth();
        Vector3f pPos = player.getPosition();
        float pHalfW = player.getHalfWidth();

        // Cambio stanza
        if (!insideTrash) {
            if (currentRoomIndex == 0 && pPos.x - pHalfW < 0)
                player.setPosition(pHalfW, pPos.y);

            if (currentRoomIndex == rooms.size() - 1 && pPos.x + pHalfW > screenWidth)
                player.setPosition(screenWidth - pHalfW, pPos.y);

            if (pPos.x - pHalfW > screenWidth && currentRoomIndex < rooms.size() - 1) {
                loadRoom(currentRoomIndex + 1);
                player.setPosition(5f, pPos.y);
            }

            if (pPos.x + pHalfW < 0 && currentRoomIndex > 0) {
                loadRoom(currentRoomIndex - 1);
                player.setPosition(screenWidth - 5f, pPos.y);
            }
        }

        // Proiettili
        updateBullets(tpf);

        // Nemici
        if (!insideTrash) updateEnemies(tpf, scaleX);
    }

    // ENTRARE NEL BIDONE
    private void tryEnterTrash() {
    Room r = rooms.get(currentRoomIndex);
    TrashCan t = r.getTrashCan();
    if (t == null) return;

    Vector3f p = player.getPosition();
    float pW = player.getHalfWidth();
    float pH = player.getHalfHeight();

    // Prendiamo il centro del player
    float playerCenterX = p.x;
    float playerCenterY = p.y;

    // Margine di tolleranza per l'entrata
    float marginX = 20f; // solo ±20px circa orizzontalmente
    float marginY = 0f;  // verticale già a posto

    boolean overlapX = (playerCenterX + marginX) >= t.getX() && (playerCenterX - marginX) <= (t.getX() + t.getWidth());
    boolean overlapY = (playerCenterY + marginY) >= t.getY() && (playerCenterY - marginY) <= (t.getY() + t.getHeight());

    if (overlapX && overlapY) {
        insideTrash = true;
        currentTrash = t;

        guiNode.detachChild(player.getNode());

        // <<< EFFETTI SONORI QUI >>>

        player.reset();
        healthBar.refresh(player);
    }
}

    // USCIRE DAL BIDONE CON SPACE
    private void exitTrash() {
        if (!insideTrash) return;
    
        insideTrash = false;
    
        // riaggiungi player alla scena
        if (player.getNode().getParent() == null)
            guiNode.attachChild(player.getNode());
    
        // posiziona il player SOPRA il bidone
        float newX = currentTrash.getX() + currentTrash.getWidth() / 2f;
        float newY = currentTrash.getY() + currentTrash.getHeight();
    
        player.setPosition(newX, newY);
        player.getNode().setLocalTranslation(newX, newY, 0.2f);
    
        // <<< QUI PUOI METTERE IL SUONO DI USCITA >>>
    
        currentTrash = null;
    }
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {

            if (insideTrash) {
                if (name.equals("Jump") && isPressed) exitTrash();
                return;
            }

            if (name.equals("Left")) player.setLeft(isPressed);
            if (name.equals("Right")) player.setRight(isPressed);

            if (name.equals("Jump") && isPressed) player.jump();

            if (name.equals("Shoot") && isPressed) player.shoot(bullets, assetManager);

            if (name.equals("EnterTrash") && isPressed) tryEnterTrash();
        }
    };

    // BULLETS, ENEMIES, RESET, ecc. (RESTO UGUALE)

    private void updateBullets(float tpf) {
        float screenWidth = settings.getWidth();
        Iterator<Bullet> bit = bullets.iterator();
        List<Enemy> deadEnemies = new ArrayList<>();

        while (bit.hasNext()) {
            Bullet b = bit.next();
            b.update(tpf);
            Vector3f bPos = b.getGeometry().getLocalTranslation().clone();

            boolean removed = false;

            for (Enemy en : new ArrayList<>(rooms.get(currentRoomIndex).getEnemies())) {
                Vector3f ePosRaw = en.getGeometry().getLocalTranslation().clone();
                float hw = en.getHalfWidth();
                float hh = en.getHalfHeight();
                Vector3f c = ePosRaw.add(hw, hh, 0f);

                if (bPos.x >= c.x - hw && bPos.x <= c.x + hw &&
                    bPos.y >= c.y - hh && bPos.y <= c.y + hh) {

                    guiNode.detachChild(b.getGeometry());
                    bit.remove();
                    removed = true;

                    if (en.takeDamageFromBullet()) {
                        guiNode.detachChild(en.getGeometry());
                        deadEnemies.add(en);
                    }
                    break;
                }
            }

            if (!removed) {
                float bx = b.getGeometry().getLocalTranslation().x;
                if (bx < 0 || bx > screenWidth) {
                    guiNode.detachChild(b.getGeometry());
                    bit.remove();
                } else if (b.getGeometry().getParent() == null) {
                    guiNode.attachChild(b.getGeometry());
                }
            }
        }

        rooms.get(currentRoomIndex).getEnemies().removeAll(deadEnemies);
    }

    private void updateEnemies(float tpf, float scaleX) {
        Vector3f pPos = player.getPosition();
        float pHW = player.getHitHalfWidth();
        float pHH = player.getHitHalfHeight();

        for (Enemy en : new ArrayList<>(rooms.get(currentRoomIndex).getEnemies())) {
            en.update(tpf, pPos, scaleX);

            if (en.getGeometry().getParent() == null)
                guiNode.attachChild(en.getGeometry());

            Vector3f ePos = en.getGeometry().getLocalTranslation();
            float ehw = en.getHitHalfWidth();
            float ehh = en.getHitHalfHeight();
            float ecx = ePos.x + en.getHalfWidth();
            float ecy = ePos.y + en.getHalfHeight();

            if (Math.abs(ecx - pPos.x) <= (ehw + pHW) &&
                Math.abs(ecy - pPos.y) <= (ehh + pHH)) {

                if (player.takeDamage()) healthBar.loseLife();
            }
        }
    }

    private void handleDeathScreen(float tpf) {
        if (!deathScreenActive) {
            deathScreenActive = true;
            deathScreenTimer = 0;
            showContinueButton = false;

            deathScreenStatic = new Picture("Dead");
            deathScreenStatic.setImage(assetManager, "Textures/you-died.png", true);
            deathScreenStatic.setWidth(settings.getWidth());
            deathScreenStatic.setHeight(settings.getHeight());
            deathScreenStatic.setLocalTranslation(0, 0, 10f);
            guiNode.attachChild(deathScreenStatic);
        }

        deathScreenTimer += tpf;

        if (deathScreenTimer >= deathScreenDelay && !showContinueButton) {
            continueButton = new Picture("Continue");
            continueButton.setImage(assetManager, "Textures/continue.png", true);
            continueButton.setWidth(settings.getWidth() * 0.75f);
            continueButton.setHeight(settings.getHeight() * 0.6f);
            continueButton.setLocalTranslation(settings.getWidth() * 0.17f, settings.getHeight() * 0.01f, 11f);
            guiNode.attachChild(continueButton);
            showContinueButton = true;
        }
    }

    private void resetRooms() {
        for (Room r : rooms) {
            for (Enemy e : r.getEnemies())
                if (e.getGeometry().getParent() != null)
                    guiNode.detachChild(e.getGeometry());
            r.getEnemies().clear();
        }

        float sw = settings.getWidth();
        float sy = settings.getHeight() / 768f;
        float sx = settings.getWidth() / 1024f;
        initEnemies(sw, sx, sy);
    }

    private void resetGame() {
        for (Bullet b : bullets)
            if (b.getGeometry().getParent() != null)
                guiNode.detachChild(b.getGeometry());
        bullets.clear();

        player.reset();
        insideTrash = false;

        resetRooms();
        healthBar.refresh(player);
        loadRoom(0);
    }


    @Override
    public void simpleRender(RenderManager rm) {}

}
