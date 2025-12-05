package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author luca crivelli
 */
public class Sound {

    private static AssetManager assetManager;

    // AudioNode dei nemici: key = nemico
    private static Map<Enemy, AudioNode> enemySounds = new HashMap<>();

    // AudioNode del boss (uno solo)
    private static AudioNode bossAmbient;

    public static void init(AssetManager am){
        assetManager = am;
    }

    // Aggiungi un nemico normale con suono
    public static void addEnemySound(Enemy enemy, boolean boss){
        if (enemySounds.containsKey(enemy)) return;
        
        if(boss){
            AudioNode sound = new AudioNode(assetManager, "Sounds/witchLaughGood.wav", false);
            sound.setLooping(true);
            sound.setPositional(true);
            sound.setVolume(1);
            sound.play();
            
            enemySounds.put(enemy, sound);
        }else{
            AudioNode sound = new AudioNode(assetManager, "Sounds/zombie.wav", false);
            sound.setLooping(true);
            sound.setPositional(true);
            sound.setVolume(1);
            sound.play();
            
            enemySounds.put(enemy, sound);
        }
    }

    // Rimuovi nemico (ferma suono)
    public static void removeEnemySound(Enemy enemy){
        AudioNode sound = enemySounds.remove(enemy);
        if (sound != null) {
            sound.stop();
        }
    }


    public static void shoot() {
        AudioNode shot = new AudioNode(assetManager, "Sounds/gunShot.wav", false);
        shot.setPositional(false);
        shot.setLooping(false);
        shot.play();
    }

    public static void hurtPlayer() {
        AudioNode hurt = new AudioNode(assetManager, "Sounds/hurtPlayer.wav", false);
        hurt.setPositional(false);
        hurt.setLooping(false);
        hurt.setVolume(2);
        hurt.play();
    }
    
    public static void throwKnife() {
        AudioNode knife = new AudioNode(assetManager, "Sounds/throwKnife.wav", false);
        knife.setPositional(false);
        knife.setLooping(false);
        knife.play();
    }
    
    public static void openDoor() {
        AudioNode open = new AudioNode(assetManager, "Sounds/popOpen.wav", false);
        open.setPositional(false);
        open.setLooping(false);
        open.play();
    }
    
    public static void closeDoor() {
        AudioNode close = new AudioNode(assetManager, "Sounds/doorClose.wav", false);
        close.setPositional(false);
        close.setLooping(false);
        close.play();
    }
    
    public static void died() {
        AudioNode died = new AudioNode(assetManager, "Sounds/youDied.wav", false);
        died.setPositional(false);
        died.setLooping(false);
        died.play();
    }
    
}