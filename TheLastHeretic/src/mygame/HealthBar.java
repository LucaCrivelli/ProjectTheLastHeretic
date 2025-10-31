
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luca Crivelli
 */
public class HealthBar {
    private List<Picture> hearts = new ArrayList<>();
    private int maxLives;
    private int currentLives;
    private float heartWidth;
    private float heartHeight;
    private final AssetManager assetManager;
    private final String fullHeartPath = "Textures/heart_full.png";
    private final String emptyHeartPath = "Textures/heart_empty.png";


    public HealthBar(AssetManager assetManager, int maxLives, float startX, float startY, float heartWidth, float heartHeight) {
        this.assetManager = assetManager;
        this.maxLives = maxLives;
        this.currentLives = maxLives;
        this.heartWidth = heartWidth;
        this.heartHeight = heartHeight;

        for (int i = 0; i < maxLives; i++) {
            Picture heart = new Picture("Heart" + i);
            heart.setImage(assetManager, fullHeartPath, true); // la texture del cuore pieno
            heart.setWidth(heartWidth);
            heart.setHeight(heartHeight);
            heart.setPosition(startX + i * 50, startY);
            hearts.add(heart);
        }
    }

    public void attachToNode(Node parent, float z) {
        for (Picture heart : hearts) {
            heart.setLocalTranslation(heart.getLocalTranslation().x, heart.getLocalTranslation().y, z);
            if (heart.getParent() == null) parent.attachChild(heart);
        }
    }

    public void loseLife() {
        if (currentLives > 0) {
            currentLives--;
            hearts.get(currentLives).setImage(assetManager, emptyHeartPath, true);
        }
    }

    /** Quando il player recupera una vita */
    public void gainLife() {
        if (currentLives < maxLives) {
            hearts.get(currentLives).setImage(assetManager, fullHeartPath, true);
            currentLives++;
        }
    }
    
    /** Sincronizza la barra con la vita effettiva del player */
    public void refresh(Player player) {
        int playerLives = player.getCurrentLives();
        for (int i = 0; i < maxLives; i++) {
            String tex = (i < playerLives) ? fullHeartPath : emptyHeartPath;
            hearts.get(i).setImage(assetManager, tex, true);
        }
        currentLives = playerLives;
    }       

    public int getCurrentLives() {
        return currentLives;
    }
}
