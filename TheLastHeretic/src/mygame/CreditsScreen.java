package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.ui.Picture;
import com.jme3.scene.Node;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author ingrid.cereda
 */
public class CreditsScreen {

    private Picture background;
    private Picture menuButton;
    private boolean visible = true;

    public interface CreditsListener {
        void onMenu();
    }

    public CreditsScreen(AssetManager assetManager, Node guiNode, InputManager input, float sw, float sh, CreditsListener listener) {
        // sfondo dei crediti
        background = new Picture("CreditsBG");
        background.setImage(assetManager, "Textures/creditsPage.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 10);
        guiNode.attachChild(background);


        float btnWidth = sw * 0.50f;
        float btnHeight = sh * 0.56f;
        float btnX = (sw - btnWidth) / 2f;
        float btnY = sh * 0.05f;

        // pulsante Menu
        menuButton = new Picture("MenuBtn");
        menuButton.setImage(assetManager, "Textures/menu.png", true);
        menuButton.setWidth(btnWidth);
        menuButton.setHeight(btnHeight);
        menuButton.setLocalTranslation(btnX, btnY, 11);
        guiNode.attachChild(menuButton);

        // Input
        input.addMapping("CreditsClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;

                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;

                if (mx >= menuButton.getLocalTranslation().x &&
                    mx <= menuButton.getLocalTranslation().x + menuButton.getWidth() &&
                    my >= menuButton.getLocalTranslation().y &&
                    my <= menuButton.getLocalTranslation().y + menuButton.getHeight()) {

                    hide(guiNode);
                    listener.onMenu();
                }
            }
        }, "CreditsClick");
    }

    public void hide(Node guiNode) {
        if (!visible) return;
        guiNode.detachChild(background);
        guiNode.detachChild(menuButton);
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }
}