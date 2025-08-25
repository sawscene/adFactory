/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.common;

import adtekfuji.fxscene.SceneContiner;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.javafxcommon.TreeCellInterface;
import jp.adtekfuji.javafxcommon.workflowmodel.WorkCell;

/**
 * ツリーセル表示用クラス
 *
 * @author ta.ito
 */
public class CustomTreeCell extends TreeCell<TreeCellInterface> {

    private final SceneContiner sc = SceneContiner.getInstance();

    public CustomTreeCell() {
    }

    @Override
    protected void updateItem(TreeCellInterface item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (empty) {
            setText(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
            if (item.getEntity() instanceof WorkInfoEntity) {
                this.setOnDragDetected((MouseEvent event) -> {
                    System.out.println("DragDetected");
                    startDragWork(event);
                });

                this.setOnDragDone((DragEvent event) -> {
                    System.out.println("DragDone");
                });
            }
        }
    }

    private String getString() {
        return Objects.isNull(getItem()) ? "" : getItem().getName();
    }

    public Object getEntity() {
        return getItem().getEntity();
    }

    public long getHierarchyId() {
        return getItem().getHierarchyId();
    }

    private void startDragWork(MouseEvent event) {

        if (!(event.getSource() instanceof CustomTreeCell)) {
            return;
        }
        CustomTreeCell treeCell = (CustomTreeCell) event.getSource();

        if (!(treeCell.getEntity() instanceof WorkInfoEntity)) {
            return;
        }
        WorkInfoEntity entity = (WorkInfoEntity) treeCell.getEntity();

        // コピーが使用できるドラッグボードを生成
        Dragboard dragboard = this.startDragAndDrop(TransferMode.COPY);

        // ドラッグボードに保持させるコンテントを生成し、
        // ドラッグボードに保持させる
        ClipboardContent content = new ClipboardContent();
        DataFormat dataFormat = DataFormatUtil.getDataFormat(WorkInfoEntity.class);
        content.put(dataFormat, entity);
        dragboard.setContent(content);

        // ドラッグ中は半透明にし、ドラッグをしていることを分かるようにする
        WorkCell cell = new WorkCell(new ConWorkflowWorkInfoEntity(), entity.getWorkName(), false);
        cell.setOpacity(0.6);
        cell.getStylesheets().addAll(sc.getSceneProperties().getCsspathes());
        Scene scene = new Scene(cell);
        dragboard.setDragView(cell.snapshot(null, null));
    }
}
