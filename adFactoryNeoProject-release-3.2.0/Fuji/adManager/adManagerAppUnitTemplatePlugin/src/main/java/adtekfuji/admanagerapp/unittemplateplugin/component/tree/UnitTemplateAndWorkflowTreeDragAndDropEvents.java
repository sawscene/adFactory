/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component.tree;

import adtekfuji.admanagerapp.unittemplateplugin.common.UnitTemplateCell;
import jp.adtekfuji.forfujiapp.javafx.tree.cell.TreeCellInterface;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;

/**
 * ユニットテンプレート・工程順ツリーのドラッグアンドドロップ時の処理
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.1.Tus
 */
public class UnitTemplateAndWorkflowTreeDragAndDropEvents {

    /**
     * ユニットテンプレートツリーの設定項目を注入
     *
     * @param treeView 注入対象のNode
     */
    public static void configureUnitTemplateTreeView(TreeView<TreeCellInterface> treeView) {
        // ドラッグ開始
        treeView.setOnDragDetected((MouseEvent event) -> {
            // コピーと移動が使用できるドラッグボードを生成
            System.out.println("DragDetected");
            Dragboard dragboard = treeView.startDragAndDrop(TransferMode.COPY_OR_MOVE);

            // ドラッグボードに保持させるコンテントを生成し、
            // ドラッグボードに保持させる
            ClipboardContent content = new ClipboardContent();
            DataFormat dataFormat = getDataFormat(treeView.getSelectionModel().getSelectedItem().getValue());
            if (Objects.isNull(dataFormat)) {
                return;
            }
            content.put(dataFormat, (UnitTemplateInfoEntity) treeView.getSelectionModel().getSelectedItem().getValue().getEntity());
            dragboard.setContent(content);

            // ドラッグ中は半透明にし、ドラッグをしていることを分かるようにするy
            UnitTemplateCell cell = new UnitTemplateCell(null, treeView.getSelectionModel().getSelectedItem().getValue().getName());
            cell.setOpacity(0.6);
            cell.setPrefSize(80.0, 80.0);
            new Scene(cell);
            dragboard.setDragView(cell.snapshot(null, null));
        });

        // ドラッグ終了
        treeView.setOnDragDone((DragEvent event) -> {
            System.out.println("DragDone");
        });
    }

    /**
     * 工程順ツリーの設定項目を注入
     *
     * @param treeView 注入対象のNode
     */
    public static void configureWorkflowTreeView(TreeView<TreeCellInterface> treeView) {
        // ドラッグ開始
        treeView.setOnDragDetected((MouseEvent event) -> {
            // コピーと移動が使用できるドラッグボードを生成
            System.out.println("DragDetected");
            Dragboard dragboard = treeView.startDragAndDrop(TransferMode.COPY_OR_MOVE);

            // ドラッグボードに保持させるコンテントを生成し、
            // ドラッグボードに保持させる
            ClipboardContent content = new ClipboardContent();
            DataFormat dataFormat = getDataFormat(treeView.getSelectionModel().getSelectedItem().getValue());
            if (Objects.isNull(dataFormat)) {
                return;
            }
            content.put(dataFormat, (WorkflowInfoEntity) treeView.getSelectionModel().getSelectedItem().getValue().getEntity());
            dragboard.setContent(content);

            // ドラッグ中は半透明にし、ドラッグをしていることを分かるようにするy
            WorkflowInfoEntity workflow = (WorkflowInfoEntity) treeView.getSelectionModel().getSelectedItem().getValue().getEntity();
            UnitTemplateCell cell = new UnitTemplateCell(null, workflow.getWorkflowName(), workflow.getWorkflowRev());
            cell.setOpacity(0.6);
            cell.setPrefSize(80.0, 80.0);
            new Scene(cell);
            dragboard.setDragView(cell.snapshot(null, null));
        });

        // ドラッグ終了
        treeView.setOnDragDone((DragEvent event) -> {
            System.out.println("DragDone");
        });
    }

    public static DataFormat getDataFormat(TreeCellInterface cellInterface) {
        if (cellInterface.getEntity() instanceof UnitTemplateInfoEntity) {
            DataFormat dataFormat = DataFormat.lookupMimeType(UnitTemplateInfoEntity.class.getName());
            if (dataFormat == null) {
                dataFormat = new DataFormat(UnitTemplateInfoEntity.class.getName());
            }
            return dataFormat;
        } else if (cellInterface.getEntity() instanceof WorkflowInfoEntity) {
            DataFormat dataFormat = DataFormat.lookupMimeType(WorkflowInfoEntity.class.getName());
            if (dataFormat == null) {
                dataFormat = new DataFormat(WorkflowInfoEntity.class.getName());
            }
            return dataFormat;
        }
        return null;
    }
}
