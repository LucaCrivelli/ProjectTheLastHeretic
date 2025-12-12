package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

public class Menu {

    public enum ScreenType { MAIN, CONTROLS, CREDITS }

    private Picture playBtn, controlsBtn, creditsBtn, quitBtn, background, title;
    private Picture menuBtn; // pulsante usato in Controls e Credits
    private boolean visible = true;
    private ActionListener menuClickListener;
    private ScreenType currentScreen;

    public interface MenuListener {
        void onPlay();
        void onControls();
        void onCredits();
        void onQuit();
    }

    private float sw, sh;
    private Node guiNode;
    private InputManager input;
    private AssetManager assetManager;
    private MenuListener listener;

    public Menu(AssetManager assetManager, Node guiNode, InputManager input, float sw, float sh, MenuListener listener) {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.input = input;
        this.sw = sw;
        this.sh = sh;
        this.listener = listener;

        showMainMenu();
    }

    // ----- SCHERMO PRINCIPALE -----
    public void showMainMenu() {
        removeAllListeners();
        currentScreen = ScreenType.MAIN;
        visible = true;

        // Background
        background = new Picture("MenuBG");
        background.setImage(assetManager, "Textures/bg_black.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 5);

        // Titolo
        title = new Picture("Title");
        title.setImage(assetManager, "Textures/title.png", true);
        float titleWidth = sw * 0.70f;
        float titleHeight = sh * 0.45f;
        title.setWidth(titleWidth);
        title.setHeight(titleHeight);
        float titleX = (sw - titleWidth) / 2f;
        float titleY = sh - titleHeight - (sh * 0.12f);
        title.setLocalTranslation(titleX, titleY, 6);

        // Pulsanti
        float btnWidth = sw * 0.50f;
        float btnHeight = sh * 0.56f;
        float centerX = (sw - btnWidth) / 2f;
        float gap = btnHeight * 0.25f;
        float centerY = (sh / 2f) + (gap * 1.1f);

        playBtn = new Picture("Play");
        playBtn.setImage(assetManager, "Textures/play.png", true);
        playBtn.setWidth(btnWidth);
        playBtn.setHeight(btnHeight);
        playBtn.setLocalTranslation(centerX, centerY, 6);

        controlsBtn = new Picture("Controls");
        controlsBtn.setImage(assetManager, "Textures/controls.png", true);
        controlsBtn.setWidth(btnWidth);
        controlsBtn.setHeight(btnHeight);
        controlsBtn.setLocalTranslation(centerX, centerY - gap, 7);

        creditsBtn = new Picture("Credits");
        creditsBtn.setImage(assetManager, "Textures/credits.png", true);
        creditsBtn.setWidth(btnWidth);
        creditsBtn.setHeight(btnHeight);
        creditsBtn.setLocalTranslation(centerX, centerY - gap * 2, 7);

        quitBtn = new Picture("Quit");
        quitBtn.setImage(assetManager, "Textures/quit.png", true);
        quitBtn.setWidth(btnWidth);
        quitBtn.setHeight(btnHeight);
        quitBtn.setLocalTranslation(centerX, centerY - gap * 3, 7);

        guiNode.attachChild(background);
        guiNode.attachChild(title);
        guiNode.attachChild(playBtn);
        guiNode.attachChild(controlsBtn);
        guiNode.attachChild(creditsBtn);
        guiNode.attachChild(quitBtn);

        input.addMapping("MenuClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(menuClickListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;
                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;

                if (isInside(playBtn, mx, my)) {
                    hideCurrentScreen();
                    listener.onPlay();
                } else if (isInside(controlsBtn, mx, my)) {
                    hideCurrentScreen();
                    showControlsScreen();
                } else if (isInside(creditsBtn, mx, my)) {
                    hideCurrentScreen();
                    showCreditsScreen();
                } else if (isInside(quitBtn, mx, my)) {
                    listener.onQuit();
                }
            }
        }, "MenuClick");
    }

    // ----- SCHERMO CONTROLS -----
    public void showControlsScreen() {
        removeAllListeners();
        currentScreen = ScreenType.CONTROLS;
        visible = true;

        background = new Picture("ControlsBG");
        background.setImage(assetManager, "Textures/controlsPage.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 5);
        guiNode.attachChild(background);

        float btnWidth = sw * 0.50f;
        float btnHeight = sh * 0.56f;
        float btnX = (sw - btnWidth) / 2f;
        float btnY = sh * 0.05f;

        menuBtn = new Picture("MenuBtn");
        menuBtn.setImage(assetManager, "Textures/menu.png", true);
        menuBtn.setWidth(btnWidth);
        menuBtn.setHeight(btnHeight);
        menuBtn.setLocalTranslation(btnX, btnY, 6);
        guiNode.attachChild(menuBtn);

        input.addMapping("ControlsClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(menuClickListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;
                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;
                if (mx >= btnX && mx <= btnX + btnWidth &&
                    my >= btnY && my <= btnY + btnHeight) {
                    hideCurrentScreen();
                    showMainMenu();
                }
            }
        }, "ControlsClick");
    }

    // ----- SCHERMO CREDITS -----
    public void showCreditsScreen() {
        removeAllListeners();
        currentScreen = ScreenType.CREDITS;
        visible = true;

        background = new Picture("CreditsBG");
        background.setImage(assetManager, "Textures/creditsPage.png", true);
        background.setWidth(sw);
        background.setHeight(sh);
        background.setLocalTranslation(0, 0, 5);
        guiNode.attachChild(background);

        float btnWidth = sw * 0.50f;
        float btnHeight = sh * 0.56f;
        float btnX = (sw - btnWidth) / 2f;
        float btnY = sh * 0.05f;

        menuBtn = new Picture("MenuBtn");
        menuBtn.setImage(assetManager, "Textures/menu.png", true);
        menuBtn.setWidth(btnWidth);
        menuBtn.setHeight(btnHeight);
        menuBtn.setLocalTranslation(btnX, btnY, 6);
        guiNode.attachChild(menuBtn);

        input.addMapping("CreditsClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(menuClickListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (!visible || !isPressed) return;
                float mx = input.getCursorPosition().x;
                float my = input.getCursorPosition().y;
                if (mx >= btnX && mx <= btnX + btnWidth &&
                    my >= btnY && my <= btnY + btnHeight) {
                    hideCurrentScreen();
                    showMainMenu();
                }
            }
        }, "CreditsClick");
    }

    public void hideCurrentScreen() {
        if (!visible) return;

        guiNode.detachChild(background);
        if (menuBtn != null) guiNode.detachChild(menuBtn);
        if (title != null) guiNode.detachChild(title);
        if (playBtn != null) guiNode.detachChild(playBtn);
        if (controlsBtn != null) guiNode.detachChild(controlsBtn);
        if (creditsBtn != null) guiNode.detachChild(creditsBtn);
        if (quitBtn != null) guiNode.detachChild(quitBtn);

        // rimuove mapping input
        if (currentScreen == ScreenType.MAIN && input.hasMapping("MenuClick"))
            input.deleteMapping("MenuClick");
        else if (currentScreen == ScreenType.CONTROLS && input.hasMapping("ControlsClick"))
            input.deleteMapping("ControlsClick");
        else if (currentScreen == ScreenType.CREDITS && input.hasMapping("CreditsClick"))
            input.deleteMapping("CreditsClick");

        visible = false;
    }

    private boolean isInside(Picture btn, float mx, float my) {
        float x = btn.getLocalTranslation().x;
        float y = btn.getLocalTranslation().y;
        float w = btn.getWidth();
        float h = btn.getHeight();
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private void removeAllListeners() {
        if (input.hasMapping("MenuClick")) input.deleteMapping("MenuClick");
        if (input.hasMapping("ControlsClick")) input.deleteMapping("ControlsClick");
        if (input.hasMapping("CreditsClick")) input.deleteMapping("CreditsClick");
        if (menuClickListener != null) input.removeListener(menuClickListener);
    }

    public boolean isVisible() {
        return visible;
    }
}
