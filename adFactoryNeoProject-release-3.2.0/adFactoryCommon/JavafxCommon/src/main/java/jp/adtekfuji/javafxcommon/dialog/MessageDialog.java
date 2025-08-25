/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.dialog;

import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogButtons;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogResult;
import jp.adtekfuji.javafxcommon.dialog.MessageDialogEnum.MessageDialogType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * メッセージダイアログ
 *
 * @author nar-nakamura
 */
public class MessageDialog {

    private static final Logger logger = LogManager.getLogger();
    private static final SceneContiner sc = SceneContiner.getInstance();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale");

    /**
     * メッセージダイアログを表示する。
     *
     * @param owner 親ウィンドウ
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     * @param buttons 表示ボタン種別
     * @return 
     */
    public static MessageDialogResult show(Window owner, String title, String message, MessageDialogType type, MessageDialogButtons buttons) {
        return show(owner, title, message, type, buttons, null, null, null);
    }

    /**
     * メッセージダイアログを表示する。
     *
     * @param owner 親ウィンドウ
     * @param title タイトル
     * @param message メッセージ
     * @param type ダイアログ種別
     * @param buttons 表示ボタン種別
     * @param borderWidth 枠線の太さ
     * @param borderColor 枠線の色
     * @param backgroundColor 背景の色
     * @return 
     */
    public static MessageDialogResult show(Window owner, String title, String message, MessageDialogType type, MessageDialogButtons buttons,
            Double borderWidth, String borderColor, String backgroundColor) {
        Dialog<MessageDialogResult> dlg = new Dialog();

        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.initOwner(owner);
        dlg.initStyle(StageStyle.UTILITY);// タイトルバーには×ボタンのみ表示
        dlg.setResizable(false);// サイズ変更不可
        dlg.setTitle(title);

        MessageDialogArgument argument = new MessageDialogArgument(message, type, buttons);

        // 枠線の太さ
        if (Objects.nonNull(borderWidth)) {
            argument.setBorderWidth(borderWidth);
        }
        // 枠線の色
        if (Objects.nonNull(borderColor)) {
            argument.setBorderColor(borderColor);
        }
        // 背景の色
        if (Objects.nonNull(backgroundColor)) {
            argument.setBackgroundColor(backgroundColor);
        }

        try {
            String path = sc.getFxComponents().get("MessageDialog");
            FXMLLoader loader = new FXMLLoader(MessageDialog.class.getResource(path), rb);
            Pane fxml = (Pane) loader.load();
            Object controller = loader.getController();

            if (controller instanceof ArgumentDelivery) {
                ((ArgumentDelivery) controller).setArgument(argument);
            }

            if (controller instanceof DialogHandler) {
                ((DialogHandler) controller).setDialog(dlg);
            }

            dlg.getDialogPane().setContent(fxml);
        } catch (NullPointerException | IOException ex) {
            logger.warn(ex, ex);
            dlg.setContentText("404 not found.");
        }

        Optional<MessageDialogResult> result = dlg.showAndWait();
        return result.get();
    }
}
