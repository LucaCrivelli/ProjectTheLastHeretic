package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.math.Vector3f;
import mygame.Bullet.BulletType;

public class Player {

    private final Node node;
    private final Geometry geometry;
    private final Quad quad;

    private Texture textureRight;
    private Texture textureLeft;
    private Material material;

    private float speed = 300f;
    private float jumpForce = 700f;
    private float gravity = -1000f;
    private float velocityY = 0f;
    private boolean left, right, jumping;
    private boolean facingLeft = false;

    private float groundY;
    private float screenWidth;
    private float screenHeight;
    private float playerSize;

    private float shootTimer = 0f;

    private int maxLives = 5;
    private int currentLives = 5;

    private float invincibilityTime = 2.0f;
    private float invincibilityTimer = 0f;

    private float hitboxScale = 0.5f;

    private float scaleY;
    private float scaleX;

    public Player(AssetManager assetManager, float screenWidth, float screenHeight, float scaleX, float scaleY) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.groundY = 190f * scaleY;
        this.playerSize = 242f * scaleY;
        this.scaleY = scaleY;
        this.scaleX = scaleX;
        
        this.speed *= scaleX;
        this.jumpForce *= scaleX;
        this.gravity *= scaleX;
        this.velocityY *= scaleX;

        textureRight = assetManager.loadTexture("Textures/character_right.png");
        textureRight.setMagFilter(Texture.MagFilter.Nearest);
        textureRight.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        textureLeft = assetManager.loadTexture("Textures/character_left.png");
        textureLeft.setMagFilter(Texture.MagFilter.Nearest);
        textureLeft.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", textureRight);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        quad = new Quad(360 * scaleY, 340 * scaleY, false);
        geometry = new Geometry("Player", quad);
        geometry.setMaterial(material);

        node = new Node("PlayerNode");
        geometry.setLocalTranslation(-quad.getWidth() / 2f, -quad.getHeight() / 2f, 0);
        node.attachChild(geometry);

        node.setLocalTranslation(screenWidth / 2f, groundY, 0);
    }

    public Node getNode() {
        return node;
    }

    public void setLeft(boolean left) {
        this.left = left; 
    }
    public void setRight(boolean right) {
        this.right = right; 
    }

    public void jump() {
        if (!jumping) {
            velocityY = jumpForce;
            jumping = true;
        }
    }

    public void update(float tpf) {

        // PLAYER BLOCCATO NEL BIDONE
        if (Main.insideTrash) return;

        Vector3f pos = node.getLocalTranslation();

        if (left && !right) {
            pos.x -= speed * tpf;
            if (!facingLeft) {
                facingLeft = true;
                material.setTexture("ColorMap", textureLeft);
            }
        } else if (right && !left) {
            pos.x += speed * tpf;
            if (facingLeft) {
                facingLeft = false;
                material.setTexture("ColorMap", textureRight);
            }
        }

        if (invincibilityTimer > 0f) invincibilityTimer -= tpf;

        velocityY += gravity * tpf;
        pos.y += velocityY * tpf;

        node.setLocalTranslation(pos);

        if (pos.y <= groundY) {
            pos.y = groundY;
            velocityY = 0;
            jumping = false;
        }

        if (shootTimer > 0f) shootTimer -= tpf;
    }

    public void shoot(java.util.List<Bullet> bullets, AssetManager assetManager) {
        if (Main.insideTrash) return;  // non sparare nel bidone

        if (shootTimer <= 0f) {
            Vector3f bulletPos = node.getLocalTranslation().clone();
            bulletPos.y += playerSize / 4f;
            bulletPos.z = 0.2f;
            int direction = facingLeft ? -1 : 1;

            Bullet bullet = new Bullet(assetManager, bulletPos, direction, scaleX, scaleY, BulletType.PLAYER, 18f, 16f);

            bullets.add(bullet);
            shootTimer = 0.4f;
        }
    }

    public void setPosition(float x, float y) {
        node.setLocalTranslation(x, y, node.getLocalTranslation().z);
    }

    public Vector3f getPosition() {
        return node.getLocalTranslation().clone();
    }

    public float getHalfWidth() {
        return quad.getWidth() / 2f;
    }

    public float getHalfHeight() {
        return quad.getHeight() / 2f;
    }

    public float getHitHalfWidth() {
        return getHalfWidth() * hitboxScale;
    }

    public float getHitHalfHeight() {
        return getHalfHeight() * hitboxScale;
    }

    public boolean takeDamage() {
        if (invincibilityTimer <= 0f && currentLives > 0) {
            currentLives--;
            invincibilityTimer = invincibilityTime;
            return true;
        }
        return false;
    }

    public int getCurrentLives() {
        return currentLives;
    }
    public int getMaxLives() {
        return maxLives; 
    }

    public void reset() {
        currentLives = maxLives;
        invincibilityTimer = 0f;
        node.setLocalTranslation(screenWidth / 2f, groundY, 0);
    }
}
