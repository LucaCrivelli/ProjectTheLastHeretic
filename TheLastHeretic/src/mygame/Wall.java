package mygame;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.asset.AssetManager;

/**
 *
 * @author luca crivelli
 */
public class Wall {
    private Geometry geom;
    private float width, height;
    private float x, y;

    public Wall(AssetManager assetManager, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        Quad quad = new Quad(width, height);
        geom = new Geometry("Wall", quad);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // muro invisibile
        mat.setColor("Color", new com.jme3.math.ColorRGBA(0, 0, 0, 0));
        mat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);

        geom.setLocalTranslation(x, y, 0.1f);
    }

    public Geometry getGeometry() {
        return geom;
    }

    // Controllo collisione semplice col player
    public boolean collides(float px, float py, float halfW, float halfH) {
        float left = x;
        float right = x + width;
        float bottom = y;
        float top = y + height;

        float pLeft = px - halfW;
        float pRight = px + halfW;
        float pBottom = py - halfH;
        float pTop = py + halfH;

        return !(pRight < left || pLeft > right || pTop < bottom || pBottom > top);
    }
}
