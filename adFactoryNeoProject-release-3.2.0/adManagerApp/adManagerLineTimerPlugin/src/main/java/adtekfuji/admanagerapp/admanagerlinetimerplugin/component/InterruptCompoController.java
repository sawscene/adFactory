/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.admanagerlinetimerplugin.component;

import adtekfuji.clientservice.ReasonInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 中断理由を選択するダイアログのコントローラー
 *
 * @author fu-kato
 */
@FxComponent(id = "InterruptionReasonCompo", fxmlPath = "/fxml/compo/interrupt_compo.fxml")
public class InterruptCompoController implements ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final ReasonInfoFacade reasonFacade = new ReasonInfoFacade();

    @FXML
    ComboBox<ReasonInfoEntity> interrunptReasonCombo;
    @FXML
    private Pane progressPane;

    /**
     * 中断理由をシステム設定から取得しコンボボックスを作成。項目変更時に引数に中断理由を設定する。
     *
     * @param argument 中断理由をObjectPropertyの形で呼び出し元とやり取りする
     */
    @Override
    public void setArgument(Object argument) {
        try {
            blockUI(true);

            // 表示するのは中断理由の文字列のみ
            interrunptReasonCombo.setConverter(new StringConverter<ReasonInfoEntity>() {
                @Override
                public String toString(ReasonInfoEntity object) {
                    return Objects.nonNull(object) ? object.getReason() : null;
                }
                @Override
                public ReasonInfoEntity fromString(String string) {
                    return null;
                }
            });

            // 変更時に中断理由を記録
            interrunptReasonCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                ObjectProperty<ReasonInfoEntity> reason = (ObjectProperty<ReasonInfoEntity>) argument;
                reason.setValue(newValue);
            });

            new Thread(() -> {
                try {
                    List<ReasonInfoEntity> reasons = reasonFacade.findType(ReasonTypeEnum.TYPE_INTERRUPT);
                    Platform.runLater(() -> {
                        interrunptReasonCombo.getItems().addAll(FXCollections.observableArrayList(reasons));
                        interrunptReasonCombo.getSelectionModel().selectFirst();
                    });
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     * 画面をブロックして操作不可にする
     *
     * @param flg trueの時ブロック実施
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            progressPane.setVisible(flg);
        });
    }
}
