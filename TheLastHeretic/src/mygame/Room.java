/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.ui.Picture;
/**
 *
 * @author Luca Crivelli
 */
// import necessari
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String name;
    private final Picture background;
    private final List<Enemy> enemies = new ArrayList<>();
    private TrashCan trashCan;
    private List<Wall> walls = new ArrayList<>();

    public Room(com.jme3.asset.AssetManager assetManager, String texturePath, float screenWidth, float screenHeight, String name) {
        this.name = name;
        background = new Picture("BG_" + name);
        background.setImage(assetManager, texturePath, true);
        background.setWidth(screenWidth);
        background.setHeight(screenHeight);
        background.setPosition(0, 0);
    }

    public Picture getBackground() {
        return background;
    }

    public String getName() {
        return name;
    }
    
    public void addEnemy(Enemy e) {
        enemies.add(e);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void addTrashCan(TrashCan t) {
        this.trashCan = t;
    }
    
    public TrashCan getTrashCan() {
        return trashCan;
    }
    
    
    public void addWall(Wall w) {
        walls.add(w);
    }

    public List<Wall> getWalls() {
        return walls;
    }
}
