package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.math.Vector3f;

/**
 *
 * @author Luca Crivelli
 */
public class Player {

    private final Node node;         // Nodo padre per pivot e flip
    private final Geometry geometry;  // Sprite
    private final Quad quad;
    //private final Texture spriteTex;

    // Sprite sheet
    //private final int numFramesX = 4;
    //private final int numFramesY = 2;
    //private int currentFrame = 0;
    //private float frameTimer = 0f;
    //private final float frameDuration = 0.15f;
    private Texture textureRight;
    private Texture textureLeft;
    private Material material;

    // Movimento
    private float speed = 300f;
    private float jumpForce = 700f;
    private float gravity = -1000f;
    private float velocityY = 0f;
    private boolean left, right, jumping;
    private boolean facingLeft = false;

    // Dimensioni
    private float groundY;
    private float screenWidth;
    private float screenHeight;
    private float playerSize;

    // cooldown sparo
    private float shootTimer = 0f;

    public Player(AssetManager assetManager, float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.groundY = 190f;
        this.playerSize = 242f;

        // === Carica le texture ===
        textureRight = assetManager.loadTexture("Textures/character_right.png");
        textureRight.setMagFilter(Texture.MagFilter.Nearest);
        textureRight.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        textureLeft = assetManager.loadTexture("Textures/character_left.png");
        textureLeft.setMagFilter(Texture.MagFilter.Nearest);
        textureLeft.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        // === Crea il materiale e imposta texture di default ===
        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", textureRight); // Inizia con texture a destra
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // === Crea la geometria ===
        quad = new Quad(360, 340, false);
        geometry = new Geometry("Player", quad);
        geometry.setMaterial(material);

        // === Crea nodo padre ===
        node = new Node("PlayerNode");
        geometry.setLocalTranslation(-quad.getWidth() / 2f, -quad.getHeight() / 2f, 0);
        node.attachChild(geometry);

        // === Posizione iniziale ===
        node.setLocalTranslation(screenWidth / 2f, groundY, 0);
    }

    public Node getNode() {
        return node;
    }

    public void setLeft(boolean left) { this.left = left; }
    public void setRight(boolean right) { this.right = right; }

    public void jump() {
        if (!jumping) {
            velocityY = jumpForce;
            jumping = true;
        }
    }

    public void update(float tpf) {
        Vector3f pos = node.getLocalTranslation();

        // Movimento orizzontale
        if (left) pos.x -= speed * tpf;
        if (right) pos.x += speed * tpf;

        if (left && !facingLeft) {
            facingLeft = true;
            material.setTexture("ColorMap", textureLeft);
        } else if (right && facingLeft) {
            facingLeft = false;
            material.setTexture("ColorMap", textureRight);
        }

        // Flip orizzontale con scala negativa
        /*if (left && !facingLeft) {
            facingLeft = true;
            node.setLocalScale(-1f, 1f, 1f);
            // Correggi posizione per compensare flip
            node.setLocalTranslation(pos.x + quad.getWidth(), pos.y, pos.z);
        } else if (right && facingLeft) {
            facingLeft = false;
            node.setLocalScale(1f, 1f, 1f);
            // Correggi posizione per compensare flip
            node.setLocalTranslation(pos.x - quad.getWidth(), pos.y, pos.z);
        } else {
            node.setLocalTranslation(pos);
        }*/

        // Gravità
        velocityY += gravity * tpf;
        pos.y += velocityY * tpf;

        node.setLocalTranslation(pos);
        
        // Pavimento
        if (pos.y <= groundY) {
            pos.y = groundY;
            velocityY = 0;
            jumping = false;
        }

        // Animazione
        /*if (left || right) {
            frameTimer += tpf;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame = (currentFrame + 1) % numFramesX;
                setFrame(currentFrame, 0);
            }
        } else {
            currentFrame = 0;
            setFrame(0, 0);
        }*/

        if (shootTimer > 0f) {
            shootTimer -= tpf;
        }
    }

    /*private void setFrame(int frameX, int frameY) {
        float frameWidth = 1f / numFramesX;
        float frameHeight = 1f / numFramesY;

        // UV del frame singolo
        float u1 = frameX * frameWidth;
        float vTop = 1f - frameY * frameHeight;    // alto del frame
        float u2 = u1 + frameWidth;
        float vBottom = vTop - frameHeight;        // basso del frame

        // Set delle UV sul Quad
        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
            u1, vBottom,
            u2, vBottom,
            u1, vTop,
            u2, vTop
        });
        quad.updateBound();
    }*/

    public void shoot(java.util.List<Bullet> bullets, AssetManager assetManager) {
        if (shootTimer <= 0f) {
            Vector3f bulletPos = node.getLocalTranslation().clone();
            bulletPos.y += playerSize / 4f;
            bulletPos.z = 0.2f;  // un po’ più in alto del player nel guiNode per layering

            int direction = facingLeft ? -1 : 1;

            Bullet bullet = new Bullet(assetManager, bulletPos, direction);
            bullets.add(bullet);
            shootTimer = 0.4f; // mezzo secondo cooldown
        }
    }

    // Imposta la posizione del nodo del player mantenendo lo z
    public void setPosition(float x, float y) {
        node.setLocalTranslation(x, y, node.getLocalTranslation().z);
    }

    // Restituisce la posizione attuale del nodo
    public Vector3f getPosition() {
        return node.getLocalTranslation().clone();
    }


    // restituisce metà larghezza del quad (utile per rilevare collisione con i bordi)
    public float getHalfWidth() {
        return quad.getWidth() / 4f;
    }
}
