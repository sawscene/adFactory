/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorblankspaceplugin;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import jp.adtekfuji.adFactory.plugin.AdAndonComponentInterface;
import jp.adtekfuji.andon.common.AndonComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 余白フレームのコントローラー
 *
 * @author s-heya
 */
@AndonComponent(title = "余白フレーム")
@FxComponent(id = "BlankSpaceCompo", fxmlPath = "/fxml/admonitorblankspaceplugin/BlankSpaceCompo.fxml")
public class BlankSpaceCompoController  implements Initializable, ArgumentDelivery, AdAndonComponentInterface {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    private AnchorPane rootPane;

    /**
     * 余白フレームを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * パラメータを設定する。
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof String) {
            logger.info("adMonitorBlankSpacePlugin loaded: " + argument);
        }
    }

   /**
     * 表示を更新する。
     *
     * @param msg
     */
    @Override
    public void updateDisplay(Object msg) {
    }

    /**
     * フレームを終了する。
     */
    @Override
    public void exitComponent() {
        logger.info("exitComponent");
    }
}
