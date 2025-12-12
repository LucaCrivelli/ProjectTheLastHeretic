package mygame;


import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;

/**
 *
 * @author Luca Crivelli
 */
public class TrashCan {
    private Picture pic;
    private float x, y;
    private float width, height;

    public TrashCan(AssetManager assetManager, float x, float y, float width, float height, float scaleX, float scaleY) {
        this.x = x * scaleX;
        this.y = y * scaleY;
        this.width = width*scaleY;
        this.height = height * scaleY;
    
        pic = new Picture("TrashCan");
        pic.setImage(assetManager, "Textures/rubbish_bin.png", true);
        pic.setWidth(this.width);
        pic.setHeight(this.height);
    
        pic.setLocalTranslation(this.x, this.y, 0.9f);
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
