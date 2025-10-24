package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.math.Vector3f;

/**
 *Enemy semplice: pattuglia tra minX e maxX, insegue il player se entro aggroRange.
 * Usa due texture (right/left).
 * 
 * @author ingrid.cereda
 */
public class Enemy {

    private Geometry geom;
    private Quad quad;
    private Material material;
    private Texture texRight;
    private Texture texLeft;

    private float speed;        // velocità di pattugliamento
    private int direction = 1;         // 1 = destra, -1 = sinistra
    private boolean active = true;

    private float minX = 0f;
    private float maxX = 800f;         // valore di default, sovrascrivibile
    private float y = 180f;

    private float aggroRange = 200f;   // se il player è entro questo range in x, insegue

    public Enemy(AssetManager assetManager, float startX, float startY, float screenWidth, String texRightPath, String texLeftPath, float speed) {
        
        this.speed = speed;
        
        // carico texture
        texRight = assetManager.loadTexture(texRightPath);
        texRight.setMagFilter(Texture.MagFilter.Nearest);
        texRight.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        texLeft = assetManager.loadTexture(texLeftPath);
        texLeft.setMagFilter(Texture.MagFilter.Nearest);
        texLeft.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

        // quad e geometry
        quad = new Quad(212, 212, false);
        geom = new Geometry("Enemy", quad);

        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setTexture("ColorMap", texRight); // partire verso destra per default
        geom.setMaterial(material);

        // posizione iniziale (z sopra lo sfondo)
        this.y = startY;
        geom.setLocalTranslation(startX, startY, 0.15f);

        // imposta i limiti di pattuglia alla larghezza della stanza per default
        this.minX = 0f;
        this.maxX = screenWidth;
    }

    // Imposta i limiti di pattuglia (es. quando aggiungi il nemico alla stanza)
    public void setPatrolBounds(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    public Geometry getGeometry() {
        return geom;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean v) {
        active = v;
    }

    // aggiornamento: chase se player dentro aggroRange (solo X considerato),
    // altrimenti pattuglia tra minX e maxX
    public void update(float tpf, Vector3f playerPos) {
        if (!active) return;

        Vector3f pos = geom.getLocalTranslation();
        float px = playerPos.x;
        float ex = pos.x;

        boolean chasing = Math.abs(px - ex) <= aggroRange;

        if (chasing) {
            // insegui il player (solo orizzontalmente)
            if (px > ex) {
                direction = 1;
                ex += speed * 1.2f * tpf; // magari più veloce quando insegue
            } else if (px < ex) {
                direction = -1;
                ex -= speed * 1.2f * tpf;
            }
        } else {
            // pattuglia: avanti e indietro tra minX e maxX
            ex += direction * speed * tpf;
            if (ex < minX) {
                ex = minX;
                direction = 1;
            } else if (ex > maxX) {
                ex = maxX;
                direction = -1;
            }
        }

        // applica la texture in base alla direzione
        if (direction == -1){
            material.setTexture("ColorMap", texLeft);
        }
        else {
            material.setTexture("ColorMap", texRight);
        }

        // imposta la posizione (mantenendo z)
        geom.setLocalTranslation(ex, y, geom.getLocalTranslation().z);
    }
}

