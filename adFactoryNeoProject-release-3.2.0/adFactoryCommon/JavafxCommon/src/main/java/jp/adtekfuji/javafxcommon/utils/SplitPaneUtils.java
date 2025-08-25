/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.utils;

import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SplitPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * スプリットペインに関する拡張ユーティリティ
 *
 * @author fu-kato
 */
public class SplitPaneUtils {

    private final static Logger logger = LogManager.getLogger();

    private final static String DEFAULT_UI_PROPERTY_NAME = "adManagerUI";

    /**
     * 設定ファイル(adManagerUI.properties)の設定をもとに引数に指定したSplitPaneの最初のスプリットバーの座標を復元する。
     *
     * @param pane 座標を復元するSplitPane。このSplitPaneのスプリットバー座標が変更されるため注意
     * @param prefix 保存する際に識別子として使用する文字列
     */
    public static void loadDividerPosition(SplitPane pane, String prefix) {
            try {
                AdProperty.load(DEFAULT_UI_PROPERTY_NAME, DEFAULT_UI_PROPERTY_NAME + ".properties");
                Properties uiProp = AdProperty.getProperties(DEFAULT_UI_PROPERTY_NAME);
                pane.widthProperty().addListener(new ChangeListener<Number>(){
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue,Number newValue){
                        logger.info("loadDividerPosition start.");
                        Double pos;
                        if(uiProp.containsKey(prefix + "_divPos")){
                            pos = Double.valueOf(uiProp.getProperty(prefix + "_divPos")) / pane.getWidth();
                        }else{
                            pos = 0.25;
                        }
                        pane.setDividerPosition(0, pos);
                        pane.widthProperty().removeListener(this);
                    }
                });
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
    }
    
    /**
     * 設定ファイル(adManagerUI.properties)の設定をもとに引数に指定したSplitPaneの最初のスプリットバーの座標を復元する。
     *
     * @param pane 座標を復元するSplitPane。このSplitPaneのスプリットバー座標が変更されるため注意
     * @param prefix 保存する際に識別子として使用する文字列
     * @param dividerPosition SplitPaneのデフォルト値
     */
    public static void loadDividerPosition(SplitPane pane, String prefix, double dividerPosition) {
            try {
                AdProperty.load(DEFAULT_UI_PROPERTY_NAME, DEFAULT_UI_PROPERTY_NAME + ".properties");
                Properties uiProp = AdProperty.getProperties(DEFAULT_UI_PROPERTY_NAME);
                pane.widthProperty().addListener(new ChangeListener<Number>(){
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue,Number newValue){
                        logger.info("loadDividerPosition start.");
                        Double pos;
                        if(uiProp.containsKey(prefix + "_divPos")){
                            pos = Double.valueOf(uiProp.getProperty(prefix + "_divPos")) / pane.getWidth();
                        }else{
                            pos = dividerPosition;
                        }
                        pane.setDividerPosition(0, pos);
                        pane.widthProperty().removeListener(this);
                    }
                });
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
    }

    /**
     * 設定ファイル(adManagerUI.properties)へ現在のSplitPaneの最初のスプリットバー座標を保存する。
     * <pre>
     * 　保存形式: prefix + "_divPos" = value
     * </pre>
     *
     * @param pane 座標を復元するSplitPane
     * @param prefix 保存する際に識別子として使用する文字列
     */
    public static void saveDividerPosition(SplitPane pane, String prefix) {
        try {
            if (pane.getDividerPositions().length > 0) {
                double pos = pane.getDividerPositions()[0];
                double divPos = pane.getWidth() * pos;
                AdProperty.getProperties(DEFAULT_UI_PROPERTY_NAME).setProperty(prefix + "_divPos", String.valueOf(divPos));
                AdProperty.store(DEFAULT_UI_PROPERTY_NAME);
            }
        } catch (Exception ex) {
            logger.info(ex, ex);
        }
    }
}
