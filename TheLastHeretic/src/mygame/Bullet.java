package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class Bullet {

    private Geometry geom;
    private float speed = 600f; // velocità orizzontale
    private boolean active = true;
    private int direction = 1;

    public Bullet(AssetManager assetManager, Vector3f startPos, int direction) {
        this.direction = direction;

        Quad quad = new Quad(12, 10, false); // dimensioni visibili più piccole
        geom = new Geometry("Bullet", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if (direction == -1) {
            mat.setTexture("ColorMap", assetManager.loadTexture("Textures/bullet_left.png"));
        }
        else{
            mat.setTexture("ColorMap", assetManager.loadTexture("Textures/bullet.png"));
        }
        //mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        //geom.setQueueBucket(RenderQueue.Bucket.Transparent);
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

        // Risoluzione finestra 1024x768
        if (pos.x < 0 || pos.x > 1024) {
            active = false;
        }
    }
}
