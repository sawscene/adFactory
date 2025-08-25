/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorlinestatusplugin;

import java.io.File;
import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class MelodyPlayer {

    private static final Logger logger = LogManager.getLogger();
    private MediaPlayer mediaPlayer = null;

    public MelodyPlayer() {

    }

    public void play(String mediaPath, Boolean repeat) {
        this.stop();
        if (Objects.isNull(mediaPath) || mediaPath.equals("") || Objects.isNull(repeat)) {
            return;
        }
        try {
            File file = new File(mediaPath);
            if (!file.exists()) {
                logger.fatal("not found media file:{}", mediaPath);
                return;
            }
            logger.info("open media file:{} repeat:{}", mediaPath, repeat);
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> {
                if (repeat) {
                    mediaPlayer.seek(Duration.ZERO);
                } else {
                    mediaPlayer.stop();
                }
            });
            mediaPlayer.setAutoPlay(true);
        } catch (MediaException ex) {
            logger.fatal(ex, ex);
        }
    }

    public void stop() {
        if (Objects.nonNull(mediaPlayer)) {
            mediaPlayer.stop();
        }
    }

    public void switchMute() {
        if (Objects.nonNull(mediaPlayer)) {
            mediaPlayer.setMute(!mediaPlayer.isMute());
        }
    }
}
