package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;

public class Player {

    private Geometry geometry;
    private Quad quad;
    private Texture spriteTex;

    private int currentFrame = 0;
    private float frameTimer = 0;
    private final float frameDuration = 0.15f;

    private final int numFramesX = 4; // colonne
    private final int numFramesY = 2; // righe

    private float speed = 1f;
    private float jumpForce = 3.5f;
    private float gravity = -13f;
    private float velocityY = 0;
    private boolean left, right, jumping;

    private float groundY = -0.415f;

    private boolean facingLeft = false; // per flip orizzontale

    public Player(AssetManager assetManager) {
        // Carica texture
        spriteTex = assetManager.loadTexture("Textures/characters_assets.png");
        spriteTex.setMagFilter(Texture.MagFilter.Nearest);
        spriteTex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        // Crea Quad
        quad = new Quad(0.2f, 0.2f, true);
        geometry = new Geometry("Player", quad);

        // Materiale e trasparenza
        Material spriteMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        spriteMat.setTexture("ColorMap", spriteTex);
        spriteMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        geometry.setMaterial(spriteMat);

        // Frame iniziale
        setFrame(0, 0, false);
        geometry.setLocalTranslation(-0.5f, groundY, 0.2f);
    }

    public Geometry getGeometry() {
        return geometry;
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
        Vector3f pos = geometry.getLocalTranslation();

        // Movimento orizzontale
        if (left) pos.x -= speed * tpf;
        if (right) pos.x += speed * tpf;

        // Flip orizzontale
        if (left) facingLeft = true;
        else if (right) facingLeft = false;

        // Gravità
        velocityY += gravity * tpf;
        pos.y += velocityY * tpf;

        // Collisione con il pavimento
        if (pos.y <= groundY) {
            pos.y = groundY;
            velocityY = 0;
            jumping = false;
        }

        geometry.setLocalTranslation(pos);

        // Animazione
        if (left || right) {
            frameTimer += tpf;
            if (frameTimer >= frameDuration) {
                frameTimer = 0;
                currentFrame = (currentFrame + 1) % numFramesX;
                setFrame(currentFrame, 0, facingLeft);
            }
        } else {
            // fermo, frame iniziale
            currentFrame = 0;
            setFrame(0, 0, facingLeft);
        }
    }

    private void setFrame(int frameX, int frameY, boolean flipX) {
        float frameWidth = 1.0f / numFramesX;
        float frameHeight = 1.0f / numFramesY;

        float u1 = frameX * frameWidth;
        float v1 = 1f - (frameY * frameHeight);
        float u2 = u1 + frameWidth;
        float v2 = v1 - frameHeight;

        if (flipX) {
            float temp = u1; 
            u1 = u2; 
            u2 = temp;
        }

        quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
                u1, v2,
                u2, v2,
                u1, v1,
                u2, v1
        });
        quad.updateBound();
    }
}
