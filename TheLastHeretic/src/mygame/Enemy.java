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
    
    private float stopDistance = 24f;

    private float aggroRange = 200f;   // se il player è entro questo range in x, insegue
    
    // hitbox scale per il nemico (puoi tararlo per tipi diversi)
    private float hitboxScale = 0.4f;
    
    private int hp = 4;

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

    // ritorna metà larghezza (assicurati che quad sia accessibile)
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

    public void setHitboxScale(float s) {
        this.hitboxScale = s;
    }

    /** Restituisce gli HP attuali (opzionale, utile per debug) */
    public int getHp() {
        return hp;
    }

    /**
     * Il nemico subisce un colpo. Ritorna true se il colpo ha ucciso il nemico.
     */
    public boolean takeDamageFromBullet() {
        hp--;
        if (hp <= 0) {
            // eventuale logica di "morte" (suono/animazione) qui
            return true;
        }
        return false;
    }


    // aggiornamento: chase se player dentro aggroRange (solo X considerato),
    // altrimenti pattuglia tra minX e maxX
    public void update(float tpf, Vector3f playerPos) {
        if (!active) return;
    
        Vector3f pos = geom.getLocalTranslation();
        float ex = pos.x;
        float enemyHalf = quad.getWidth() / 2f;
        float exCenter = ex + enemyHalf;
        float pxCenter = playerPos.x;
    
        float dx = pxCenter - exCenter;
        float absDx = Math.abs(dx);
    
        float aggroRangeEffective = aggroRange;
        float speedChase = speed * 1.2f;
        float stopDistance = 114f;
    
        // se siamo già dentro la zona di stop, fermati e non inseguire
        boolean inStopZone = absDx <= stopDistance;
    
        if (absDx <= aggroRangeEffective && !inStopZone) {
            float targetCenter;
            if (dx > 0) {
                targetCenter = pxCenter - stopDistance;
                direction = 1;
            } else {
                targetCenter = pxCenter + stopDistance;
                direction = -1;
            }
    
            float move = speedChase * tpf;
    
            if (dx > 0) exCenter = Math.min(exCenter + move, targetCenter);
            else exCenter = Math.max(exCenter - move, targetCenter);
    
            ex = exCenter - enemyHalf;
        } 
        else if (absDx > aggroRangeEffective) {
            // pattuglia normale
            ex += direction * speed * tpf;
            if (ex < minX) {
                ex = minX;
                direction = 1;
            } else if (ex > maxX) {
                ex = maxX;
                direction = -1;
            }
        }
    
        // aggiorna texture
        if (direction == -1) material.setTexture("ColorMap", texLeft);
        else material.setTexture("ColorMap", texRight);
    
        geom.setLocalTranslation(ex, y, pos.z);
    }
}

