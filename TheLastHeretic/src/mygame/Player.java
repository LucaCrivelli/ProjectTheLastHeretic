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

public class Player {

    private final Node node;         // Nodo padre per pivot e flip
    private final Geometry geometry;  // Sprite
    private final Quad quad;
    private final Texture spriteTex;

    // Sprite sheet
    private final int numFramesX = 4;
    private final int numFramesY = 2;
    private int currentFrame = 0;
    private float frameTimer = 0f;
    private final float frameDuration = 0.15f;

    // Movimento
    private float speed = 300f;
    private float jumpForce = 650f;
    private float gravity = -900f;
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
        this.groundY = 200f;
        this.playerSize = 218f; // Dimensione singolo frame in pixel

        // === Texture ===
        spriteTex = assetManager.loadTexture("Textures/characters_assets.png");
        spriteTex.setMagFilter(Texture.MagFilter.Nearest);
        spriteTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        // === Quad ===
        quad = new Quad(120, 300, false);
        geometry = new Geometry("Player", quad);

        // === Materiale ===
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", spriteTex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geometry.setMaterial(mat);

        // === Nodo padre ===
        node = new Node("PlayerNode");
        geometry.setLocalTranslation(-quad.getWidth() / 2f, -quad.getHeight() / 2f, 0);
        node.attachChild(geometry);

        // Posizione iniziale
        node.setLocalTranslation(screenWidth / 2f, groundY, 0);

        // Frame iniziale
        setFrame(0, 0);
    }

    public Node getGeometry() {
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

        // Flip orizzontale con scala negativa
        if (left && !facingLeft) {
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
        }

        // Gravità
        velocityY += gravity * tpf;
        pos.y += velocityY * tpf;

        // Pavimento
        if (pos.y <= groundY) {
            pos.y = groundY;
            velocityY = 0;
            jumping = false;
        }

        // Animazione
        if (left || right) {
            frameTimer += tpf;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame = (currentFrame + 1) % numFramesX;
                setFrame(currentFrame, 0);
            }
        } else {
            currentFrame = 0;
            setFrame(0, 0);
        }

        if (shootTimer > 0f) {
            shootTimer -= tpf;
        }
    }

    private void setFrame(int frameX, int frameY) {
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
    }

    public void shoot(java.util.List<Bullet> bullets, AssetManager assetManager) {
        if (shootTimer <= 0f) {
            Vector3f bulletPos = node.getLocalTranslation().clone();
            bulletPos.y += playerSize / 4f;
            bulletPos.z = 0.2f;  // un po’ più in alto del player nel guiNode per layering

            int direction = facingLeft ? -1 : 1;

            Bullet bullet = new Bullet(assetManager, bulletPos, direction);
            bullets.add(bullet);
            shootTimer = 0.5f; // mezzo secondo cooldown
        }
    }
}
