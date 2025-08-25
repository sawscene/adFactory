/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.common;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Nodeを点滅する
 *
 * @author phamvanthanh
 */
public class Blinking {

    private final Logger logger = LogManager.getLogger();
    private static Blinking instance = null;
    private static List<FadeTransition> list;

    private Blinking() {
        list = new ArrayList<>();
    }

    public static Blinking getInstance() {
        if (instance == null) {
            instance = new Blinking();
        }
        return instance;
    }

    public void play(Node node) {
        //logger.info("blinking::play start.");
        FadeTransition fade = new FadeTransition();
        fade.setDelay(Duration.seconds(0.7));
        fade.setNode(node);
        fade.setFromValue(1);
        fade.setToValue(0.2);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
        list.add(fade);
    }

    public void stop() {
        logger.info("blinking::stop start.");
        try {
            for (FadeTransition fade : list) {
                fade.stop();
            }
            list.clear();
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }
}
