package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * Bullet con texture caricate una sola volta (static caching)
 * @author Luca Crivelli (modificato)
 */
public class Bullet {

    private Geometry geom;
    private float speed = 600f;
    private boolean active = true;
    private int direction = 1;

    // Caching statico delle texture (caricate una sola volta)
    private static Texture texRight = null;
    private static Texture texLeft  = null;

    // Dimensione del proiettile
    private static final float WIDTH  = 18f;
    private static final float HEIGHT = 16f;

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
    }

    /**
     * Costruttore Bullet; se le texture non sono ancora preloadate,
     * le carica al volo la prima volta.
     */
    public Bullet(AssetManager assetManager, Vector3f startPos, int direction, float scaleX, float scaleY) {
        this.direction = direction;
        this.speed *= scaleX;
        //se necessario
        if (texRight == null || texLeft == null) {
            preload(assetManager);
        }
        Quad quad = new Quad(WIDTH * scaleY, HEIGHT * scaleY, false);
        geom = new Geometry("Bullet", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // assegna la texture corretta
        if (direction == -1) {
            mat.setTexture("ColorMap", texLeft);
        } else {
            mat.setTexture("ColorMap", texRight);
        }
        geom.setMaterial(mat);

        // Assicuriamoci che la geometria sia disegnata nella giusta queue per GUI 2D
        geom.setQueueBucket(RenderQueue.Bucket.Gui);

        // Posiziona il proiettile alla posizione iniziale
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
}
