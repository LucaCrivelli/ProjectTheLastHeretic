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

    private Picture playBtn, controlsBtn, quitBtn, background;
    private boolean visible = true;

    public interface MenuListener {
        void onPlay();
        void onControls();
        void onQuit();
    }

    public Menu(AssetManager assetManager, Node guiNode, InputManager input, float sw, float sh, MenuListener listener) {

        // sfondo
        background = new Picture("MenuBG");
        background.setImage(assetManager, "Textures/bg_black.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 5);

        // bottone play
        playBtn = new Picture("Play");
        playBtn.setImage(assetManager, "Textures/play.png", true);
        playBtn.setWidth(sw * 0.3f);
        playBtn.setHeight(sh * 0.1f);
        playBtn.setLocalTranslation(sw * 0.35f, sh * 0.55f, 6);

        // bottone controls
        controlsBtn = new Picture("Controls");
        controlsBtn.setImage(assetManager, "Textures/controls.png", true);
        controlsBtn.setWidth(sw * 0.3f);
        controlsBtn.setHeight(sh * 0.1f);
        controlsBtn.setLocalTranslation(sw * 0.35f, sh * 0.40f, 6);

        // bottone quit
        quitBtn = new Picture("Quit");
        quitBtn.setImage(assetManager, "Textures/quit.png", true);
        quitBtn.setWidth(sw * 0.3f);
        quitBtn.setHeight(sh * 0.1f);
        quitBtn.setLocalTranslation(sw * 0.35f, sh * 0.25f, 6);

        // aggiungi a gui
        guiNode.attachChild(background);
        guiNode.attachChild(playBtn);
        guiNode.attachChild(controlsBtn);
        guiNode.attachChild(quitBtn);

        // input
        input.addMapping("MenuClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;

                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;

                if (isInside(playBtn, mx, my)) listener.onPlay();
                if (isInside(controlsBtn, mx, my)) listener.onControls();
                if (isInside(quitBtn, mx, my)) listener.onQuit();
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
        guiNode.detachChild(playBtn);
        guiNode.detachChild(controlsBtn);
        guiNode.detachChild(quitBtn);
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }
}

