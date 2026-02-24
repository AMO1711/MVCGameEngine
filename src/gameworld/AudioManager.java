package gameworld;

import engine.assets.core.AssetCatalog;
import engine.assets.ports.AssetInfoDTO;
import engine.assets.ports.AssetType;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {

    private final AssetCatalog musicCatalog;
    private Clip currentMusic;

    public AudioManager(AssetCatalog musicCatalog) {
        if (musicCatalog == null) {
            throw new IllegalArgumentException("musicCatalog cannot be null");
        }
        this.musicCatalog = musicCatalog;
    }

    public void playMusic(String assetId) {
        if (assetId == null) {
            throw new IllegalArgumentException("music assetId cannot be null");
        }

        stopMusic();

        try {
            AssetInfoDTO assetInfo = musicCatalog.get(assetId);

            if (assetInfo == null) {
                throw new IllegalArgumentException("Music asset not found: " + assetId);
            }

            String filePath = musicCatalog.getPath() + assetInfo.fileName;

            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(new File(filePath));

            currentMusic = AudioSystem.getClip();
            currentMusic.open(audioStream);

            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
            currentMusic.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException("Error playing music: " + assetId, e);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.close();
            currentMusic = null;
        }
    }

    public void switchMusic(String assetId) {
        stopMusic();
        playMusic(assetId);
    }

    public void setVolume(float volume) {
        if (currentMusic == null) return;

        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }

        FloatControl gainControl = (FloatControl) currentMusic.getControl(FloatControl.Type.MASTER_GAIN);

        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float dB = min + (max - min) * volume;

        gainControl.setValue(dB);
    }
}

