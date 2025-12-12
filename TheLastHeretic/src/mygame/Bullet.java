package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 *
 * @author Luca Crivelli (modificato)
 */
public class Bullet {

    private Geometry geom;
    private float speed = 600f;
    private boolean active = true;
    private int direction = 1;

    // Caching statico delle texture
    private static Texture texRight = null;
    private static Texture texLeft  = null;

    private static Texture bossTexRight = null;
    private static Texture bossTexLeft  = null;


    // Dimensione del proiettile
    private float width = 18f;
    private float height = 16f;

    private BulletType type;

    //preload delle texture
    public static void preload(AssetManager assetManager) {
        if (texRight == null) {
            texRight = assetManager.loadTexture("Textures/bullet.png");
            texRight.setMagFilter(Texture.MagFilter.Nearest);
            texRight.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        }
        if (texLeft == null) {
            texLeft = assetManager.loadTexture("Textures/bullet_left.png");
            texLeft.setMagFilter(Texture.MagFilter.Nearest);
            texLeft.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        }
    
        if (bossTexRight == null) {
            bossTexRight = assetManager.loadTexture("Textures/daga_right.png");
            bossTexRight.setMagFilter(Texture.MagFilter.Nearest);
            bossTexRight.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        }
        if (bossTexLeft == null) {
            bossTexLeft = assetManager.loadTexture("Textures/daga_left.png");
            bossTexLeft.setMagFilter(Texture.MagFilter.Nearest);
            bossTexLeft.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        }
    }    

    /**
     * Costruttore Bullet; se le texture non sono ancora preloadate,
     * le carica al volo la prima volta.
     */
    public Bullet(AssetManager assetManager, Vector3f startPos, int direction, float scaleX, float scaleY, BulletType type, float width, float height) {
        this.direction = direction;
        this.speed *= scaleX;
        this.width = width;
        this.height = height;
        this.type = type;
    
        if (texRight == null || texLeft == null || bossTexRight == null || bossTexLeft == null) {
            preload(assetManager);
        }
    
        Quad quad = new Quad(width * scaleY, height * scaleY, false);
        geom = new Geometry("Bullet", quad);
    
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    
        // SCEGLIAMO LA TEXTURE IN BASE AL TIPO DI PROIETTILE
        Texture chosenTex;
        if (type == BulletType.BOSS) {
            chosenTex = (direction == -1 ? bossTexLeft : bossTexRight);
        } else {
            chosenTex = (direction == -1 ? texLeft : texRight);
        }
    
        mat.setTexture("ColorMap", chosenTex);
        geom.setMaterial(mat);
    
        geom.setLocalTranslation(startPos);
    }
    

    public Geometry getGeometry() {
        return geom;
    }

    public boolean isActive() {
        return active;
    }

    public void update(float tpf) {
        Vector3f pos = geom.getLocalTranslation();
        pos.x += direction * speed * tpf;
        geom.setLocalTranslation(pos);
    }

    public BulletType getType() {
        return type;
    }    

    public enum BulletType {
        PLAYER,
        BOSS
    }
}
