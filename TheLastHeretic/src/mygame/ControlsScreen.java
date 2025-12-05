package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

/**
 *
 * @author luca crivelli
 */
public class ControlsScreen {

    private Picture background, menuBtn;
    private boolean visible = true;

    public interface ControlsListener {
        void onMenu();
    }

    public ControlsScreen(AssetManager assetManager, Node guiNode, InputManager input, float sw, float sh, ControlsListener listener) {

        // Sfondo controlli
        background = new Picture("ControlsBG");
        background.setImage(assetManager, "Textures/controlsPage.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 5);

        // Pulsante MENU in basso centrato
        menuBtn = new Picture("MenuBtn");
        menuBtn.setImage(assetManager, "Textures/menu.png", true);

        float btnWidth = sw * 0.50f;
        float btnHeight = sh * 0.56f;

        menuBtn.setWidth(btnWidth);
        menuBtn.setHeight(btnHeight);

        float btnX = (sw - btnWidth) / 2f;
        float btnY = sh * 0.05f;
        menuBtn.setLocalTranslation(btnX, btnY, 6);

        guiNode.attachChild(background);
        guiNode.attachChild(menuBtn);

        // Input click
        input.addMapping("ControlsClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;

                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;

                if (mx >= btnX && mx <= btnX + btnWidth &&
                    my >= btnY && my <= btnY + btnHeight) {
                    listener.onMenu();
                }
            }
        }, "ControlsClick");
    }

    public void hide(Node guiNode) {
        if (!visible) return;
        guiNode.detachChild(background);
        guiNode.detachChild(menuBtn);
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }
}
