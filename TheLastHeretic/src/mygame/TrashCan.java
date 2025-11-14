package mygame;


import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;
import com.jme3.math.Vector3f;

/**
 *
 * @author guido.montalbetti
 */
public class TrashCan {
    private Picture pic;
    private float x, y;
    private float width, height;

    public TrashCan(AssetManager assetManager, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        pic = new Picture("TrashCan");
        pic.setImage(assetManager, "Textures/rubbish_bin.png", true);
        pic.setWidth(width);
        pic.setHeight(height);
        pic.setLocalTranslation(x, y, 0.12f);
    }

    public Picture getPicture() {
        return pic;
    }

    public float getX() { 
        return x; 
    }
    
    public float getY() { 
        return y; 
    }

    public float getWidth() { 
        return width; 
    }

    public float getHeight() { 
        return height; 
    }

    public boolean intersects(float px, float py, float pW, float pH) {
        boolean overlapX = (px + pW) >= x && (px <= x + width);
        boolean overlapY = (py + pH) >= y && (py <= y + height);
        return overlapX && overlapY;
    }
}
