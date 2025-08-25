/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.common;

import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import jp.adtekfuji.javafxcommon.workflowmodel.ParallelStartCell;
import jp.adtekfuji.javafxcommon.workflowmodel.StartCell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレートセルのドラッグアンドドロップ時の処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.1.Tus
 */
public class UnitTemplateCellDragAndDropEvents {

    private final static Logger logger = LogManager.getLogger();
    private final UnitTemplateModel unittemplateModel;

    // 並列工程の作業時間をシフトする
    private final boolean isShift;

    public UnitTemplateCellDragAndDropEvents(UnitTemplateModel unittemplateModel , boolean isShift) {
        this.unittemplateModel = unittemplateModel;
        this.isShift = isShift;
    }

    /**
     * スタートセルにドロップ時のイベントを設定する
     *
     * @param cell
     */
    public void configureStartCellDrop(StartCell cell) {
        // ドロップ対象の上にマウスカーソルがある場合
        cell.setOnDragOver((DragEvent event) -> {
            // Styleの変更(座標位置に合わせて色替え)
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(getUnitTemplateDataFormat()) || dragboard.hasContent(getWorkflowDataFormat())) {
                // ドラッグされているのがイメージである場合
                // コピーもしくは移動で受け入れ可能であることを示す
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });

        cell.setOnDragEntered((DragEvent event) -> {
            String style = cell.getToggleButton().getStyle();
            cell.getToggleButton().setStyle(style + "-fx-border-color: black black cornflowerblue black; -fx-border-width: 0 0 10 0;");
        });

        cell.setOnDragExited((DragEvent event) -> {
            String style = cell.getToggleButton().getStyle();
            cell.getToggleButton().setStyle("-fx-border-color: black black black black; -fx-border-width: 0 0 0 0;");
        });

        // ドロップ時
        cell.setOnDragDropped((DragEvent event) -> {
            // 座標位置から挿入場所を確定して挿入 cellサイズ80*80
            // 指定範囲外の場合挿入イベント無
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasContent(getUnitTemplateDataFormat())) {
                UnitTemplateInfoEntity entity = (UnitTemplateInfoEntity) dragboard.getContent(getUnitTemplateDataFormat());
                unittemplateModel.getUnitTemplatePane().setSelectedCellBase(cell);
                if (event.getY() >= cell.getHeight() / 2) {
                    // 直列
                    logger.info("セルの下に挿入");
                    UnitTemplatePaneEditor.addSerial(unittemplateModel, entity);
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);
            } else if (dragboard.hasContent(getWorkflowDataFormat())) {
                WorkflowInfoEntity entity = (WorkflowInfoEntity) dragboard.getContent(getWorkflowDataFormat());
                unittemplateModel.getUnitTemplatePane().setSelectedCellBase(cell);
                if (event.getY() >= cell.getHeight() / 2) {
                    // 直列
                    logger.info("セルの下に挿入");
                    UnitTemplatePaneEditor.addSerial(unittemplateModel, entity);
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);
            } else {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
            }
        });
    }

    /**
     * スタートセルにドロップ時のイベントを設定する
     *
     * @param cell
     */
    public void configurePararellCellDrop(ParallelStartCell cell) {
        // ドロップ対象の上にマウスカーソルがある場合
        cell.setOnDragOver((DragEvent event) -> {
            // Styleの変更(座標位置に合わせて色替え)
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(getUnitTemplateDataFormat()) || dragboard.hasContent(getWorkflowDataFormat())) {
                // ドラッグされているのがイメージである場合
                // コピーもしくは移動で受け入れ可能であることを示す
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });

        cell.setOnDragEntered((DragEvent event) -> {
            String style = cell.getToggleButton().getStyle();
            cell.getToggleButton().setStyle("-fx-border-color: black black cornflowerblue black; -fx-border-width: 0 0 2 0;");
        });

        cell.setOnDragExited((DragEvent event) -> {
            String style = cell.getToggleButton().getStyle();
            cell.getToggleButton().setStyle("-fx-border-color: black black black black; -fx-border-width: 0 0 0 0;");
        });

        // ドロップ時
        cell.setOnDragDropped((DragEvent event) -> {
            // 座標位置から挿入場所を確定して挿入 cellサイズ80*80
            // 指定範囲外の場合挿入イベント無
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasContent(getUnitTemplateDataFormat())) {
                UnitTemplateInfoEntity entity = (UnitTemplateInfoEntity) dragboard.getContent(getUnitTemplateDataFormat());
                unittemplateModel.getUnitTemplatePane().setSelectedCellBase(cell);
                // 直列
                logger.info("セルの下に挿入");
                UnitTemplatePaneEditor.addSerial(unittemplateModel, entity);
                // ドラッグ成功を返す
                event.setDropCompleted(true);
            } else if (dragboard.hasContent(getWorkflowDataFormat())) {
                WorkflowInfoEntity entity = (WorkflowInfoEntity) dragboard.getContent(getWorkflowDataFormat());
                unittemplateModel.getUnitTemplatePane().setSelectedCellBase(cell);
                // 直列
                logger.info("セルの下に挿入");
                UnitTemplatePaneEditor.addSerial(unittemplateModel, entity);
                // ドラッグ成功を返す
                event.setDropCompleted(true);
            } else {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
            }
        });
    }

    /**
     * セルにドロップ時のイベントを設定する
     *
     * @param cell
     */
    public void configureCellDrop(final UnitTemplateCell cell) {
        // ドロップ対象の上にマウスカーソルがある場合
        cell.setOnDragOver((DragEvent event) -> {
            // Styleの変更(座標位置に合わせて色替え)
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(getUnitTemplateDataFormat()) || dragboard.hasContent(getWorkflowDataFormat())) {
                // ドラッグされているのがイメージである場合
                // コピーもしくは移動で受け入れ可能であることを示す
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });

        cell.setOnDragEntered((DragEvent event) -> {
            String style = cell.getToggleButton().getStyle();
            cell.getToggleButton().setStyle(style + "; -fx-border-color: black cornflowerblue cornflowerblue black; -fx-border-width: 0 10 10 0;");
        });

        cell.setOnDragExited((DragEvent event) -> {
            String style = cell.getToggleButton().getStyle();
            cell.getToggleButton().setStyle(style + "; -fx-border-color: black black black black; -fx-border-width: 0 0 0 0;");
        });

        // ドロップ時
        cell.setOnDragDropped((DragEvent event) -> {
            // 座標位置から挿入場所を確定して挿入 cellサイズ80*80
            // 指定範囲外の場合挿入イベント無
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasContent(getUnitTemplateDataFormat())) {
                UnitTemplateInfoEntity entity = (UnitTemplateInfoEntity) dragboard.getContent(getUnitTemplateDataFormat());
                unittemplateModel.getUnitTemplatePane().setSelectedCellBase(cell);
                if ((event.getY() >= cell.getHeight() / 2) && (event.getY() / event.getX() >= 1.0)) {
                    // 直列
                    logger.info("セルの下に挿入");
                    UnitTemplatePaneEditor.addSerial(unittemplateModel, entity);
                } else if (event.getX() >= cell.getHeight() / 2) {
                    // getWidthを使って比較したいがwidthの値は並列個数分だけ大きくなるためheightと比較
                    // 並列
                    logger.info("セルの右に挿入");
                    UnitTemplatePaneEditor.addParallel(unittemplateModel, entity, isShift);
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);
            } else if (dragboard.hasContent(getWorkflowDataFormat())) {
                WorkflowInfoEntity entity = (WorkflowInfoEntity) dragboard.getContent(getWorkflowDataFormat());
                unittemplateModel.getUnitTemplatePane().setSelectedCellBase(cell);
                if ((event.getY() >= cell.getHeight() / 2) && (event.getY() / event.getX() >= 1.0)) {
                    // 直列
                    logger.info("セルの下に挿入");
                    UnitTemplatePaneEditor.addSerial(unittemplateModel, entity);
                } else if (event.getX() >= cell.getHeight() / 2) {
                    // getWidthを使って比較したいがwidthの値は並列個数分だけ大きくなるためheightと比較
                    // 並列
                    logger.info("セルの右に挿入");
                    UnitTemplatePaneEditor.addParallel(unittemplateModel, entity, isShift);
                }
                // ドラッグ成功を返す
                event.setDropCompleted(true);
            } else {
                // ドラッグ失敗を返す
                event.setDropCompleted(false);
            }
        });
    }

    private DataFormat getUnitTemplateDataFormat() {
        DataFormat dataFormat = DataFormat.lookupMimeType(UnitTemplateInfoEntity.class.getName());
        if (dataFormat == null) {
            dataFormat = new DataFormat(UnitTemplateInfoEntity.class.getName());
        }
        return dataFormat;
    }

    private DataFormat getWorkflowDataFormat() {
        DataFormat dataFormat = DataFormat.lookupMimeType(WorkflowInfoEntity.class.getName());
        if (dataFormat == null) {
            dataFormat = new DataFormat(WorkflowInfoEntity.class.getName());
        }
        return dataFormat;
    }
}
