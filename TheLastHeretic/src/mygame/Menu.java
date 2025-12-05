package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.ui.Picture;
import com.jme3.scene.Node;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;

/**
 *
 * @author luca crivelli
 */
public class Menu {

    private Picture playBtn, controlsBtn, creditsBtn, quitBtn, background, title;
    private boolean visible = true;

    public interface MenuListener {
        void onPlay();
        void onControls();
        void onCredits(); 
        void onQuit();
    }

    public Menu(AssetManager assetManager, Node guiNode, InputManager input, float sw, float sh, MenuListener listener) {

        // sfondo
        background = new Picture("MenuBG");
        background.setImage(assetManager, "Textures/bg_black.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 5);

        // TITOLO (immagine centrata in alto)
        title = new Picture("Title");
        title.setImage(assetManager, "Textures/title.png", true);

        // dimensioni del titolo (regolabili)
        float titleWidth = sw * 0.70f;
        float titleHeight = sh * 0.45f;
        title.setWidth(titleWidth);
        title.setHeight(titleHeight);

        // centratura
        float titleX = (sw - titleWidth) / 2f;
        float titleY = sh - titleHeight - (sh * 0.12f);  

        title.setLocalTranslation(titleX, titleY, 6);
    
        // DIMENSIONI PULSANTI (più alti)
        float btnWidth = sw * 0.50f;
        float btnHeight = sh * 0.56f;
    
        // centro orizzontale
        float centerX = (sw - btnWidth) / 2f;

        // distanza verticale per pulsanti così grandi
        float gap = btnHeight * 0.25f;

        // primo pulsante (Play) leggermente sopra il centro
        float centerY = (sh / 2f) + (gap * 1.1f);
    
        // PLAY
        playBtn = new Picture("Play");
        playBtn.setImage(assetManager, "Textures/play.png", true);
        playBtn.setWidth(btnWidth);
        playBtn.setHeight(btnHeight);
        playBtn.setLocalTranslation(centerX, centerY, 6);
    
        // CONTROLS
        controlsBtn = new Picture("Controls");
        controlsBtn.setImage(assetManager, "Textures/controls.png", true);
        controlsBtn.setWidth(btnWidth);
        controlsBtn.setHeight(btnHeight);
        controlsBtn.setLocalTranslation(centerX, centerY - gap, 7);
    
        // CREDITS
        creditsBtn = new Picture("Credits");
        creditsBtn.setImage(assetManager, "Textures/credits.png", true);
        creditsBtn.setWidth(btnWidth);
        creditsBtn.setHeight(btnHeight);
        creditsBtn.setLocalTranslation(centerX, centerY - gap * 2, 7);
    
        // QUIT
        quitBtn = new Picture("Quit");
        quitBtn.setImage(assetManager, "Textures/quit.png", true);
        quitBtn.setWidth(btnWidth);
        quitBtn.setHeight(btnHeight);
        quitBtn.setLocalTranslation(centerX, centerY - gap * 3, 7);
    
        // Aggiungi elementi alla GUI
        guiNode.attachChild(background);
        guiNode.attachChild(title);
        guiNode.attachChild(playBtn);
        guiNode.attachChild(controlsBtn);
        guiNode.attachChild(creditsBtn);
        guiNode.attachChild(quitBtn);
    
        // Input
        input.addMapping("MenuClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;
    
                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;
    
                if (isInside(playBtn, mx, my)) {
                    listener.onPlay();
                } else if (isInside(controlsBtn, mx, my)) {
                    listener.onControls();
                } else if (isInside(creditsBtn, mx, my)) {
                    listener.onCredits();
                } else if (isInside(quitBtn, mx, my)) {
                    listener.onQuit();
                }                
            }
        }, "MenuClick");
    }    

    private boolean isInside(Picture btn, float mx, float my) {
        float x = btn.getLocalTranslation().x;
        float y = btn.getLocalTranslation().y;
        float w = btn.getWidth();
        float h = btn.getHeight();
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    public void hide(Node guiNode) {
        if (!visible) return;
        guiNode.detachChild(background);
        guiNode.detachChild(title); 
        guiNode.detachChild(playBtn);
        guiNode.detachChild(controlsBtn);
        guiNode.detachChild(creditsBtn);
        guiNode.detachChild(quitBtn);
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }
}

