/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.workflowmodel;

import static java.lang.Math.atan2;
import static java.lang.Math.tan;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.utility.DataFormatUtil;
import jp.adtekfuji.bpmn.model.entity.BpmnNode;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import jp.adtekfuji.javafxcommon.enumeration.MarkerStatus;

/**
 * FXML Controller class
 *
 * @author ta.ito
 */
public class WorkCell extends CellBase {

    private final ConWorkflowWorkInfoEntity workflowWork;
    private final VBox vbox = new VBox();
    private final CheckBox checkBox = new CheckBox();
    private final ToggleButton toggleButton = new ToggleButton();
    private final Label workNameLabel = new Label();
    private final Label equipmentLabel = new Label();
    private final Label organizationLabel = new Label();
    private final String workName;
    private BooleanProperty skipFlagProperty;
    private boolean skipFlag;

    private CellBase prevCell = null;
    private CellBase nextCell = null;

    public WorkCell(ConWorkflowWorkInfoEntity workflowWork, String workName) {
        this(workflowWork, workName, true);
    }

    public WorkCell(ConWorkflowWorkInfoEntity workflowWork, String workName, boolean editable) {
        this(workflowWork, workName, editable, editable);
    }

    /**
     * 工程セルを作成する
     *
     * @param workflowWork
     * @param workName
     * @param movable trueの場合、工程の追加・移動を許可する
     * @param editable trueの場合、組織設備時間の編集を許可する
     */
    public WorkCell(ConWorkflowWorkInfoEntity workflowWork, String workName, boolean movable, boolean editable) {
        this(workflowWork, workName, movable, editable, 1.0);
    }

    /**
     * 拡大率を指定して工程セルを作成する
     *
     * @param workflowWork
     * @param workName
     * @param movable
     * @param editable
     * @param scale 1.0を基準とする拡大率
     */
    public WorkCell(ConWorkflowWorkInfoEntity workflowWork, String workName, boolean movable, boolean editable, double scale) {
        super(movable);

        this.workflowWork = workflowWork;
        this.workName = workName;

        try {
            this.checkBox.setVisible(editable);
            this.toggleButton.setFocusTraversable(movable);

            this.vbox.getChildren().add(checkBox);
            this.vbox.getChildren().add(workNameLabel);
            this.vbox.getChildren().add(equipmentLabel);
            this.vbox.getChildren().add(organizationLabel);
            this.toggleButton.setGraphic(vbox);
            this.toggleButton.setPrefSize(72 * scale, 72 * scale);
            this.cellPane.getChildren().add(0, toggleButton);
            this.workNameLabel.setText(workName);

            this.skipFlagProperty = new SimpleBooleanProperty(skipFlag);
            this.skipFlagProperty.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    this.toggleButton.getStyleClass().add("cellSkipFalse");
                } else {
                    this.toggleButton.getStyleClass().remove("cellSkipFalse");
                }
            });
            this.skipFlagProperty.bind(this.workflowWork.skipFlagProperty());

            Tooltip toolTip = TooltipBuilder.build(toggleButton);
            toolTip.showingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("D");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

                    StringBuilder sb = new StringBuilder();
                    sb.append(workNameLabel.getText());
                    sb.append(System.lineSeparator());
                    sb.append(equipmentLabel.getText());
                    sb.append(System.lineSeparator());
                    sb.append(organizationLabel.getText());
                    sb.append(System.lineSeparator());
                    sb.append(sdf1.format(workflowWork.getStandardStartTime()));
                    sb.append(" Day");
                    sb.append(System.lineSeparator());
                    sb.append(sdf2.format(workflowWork.getStandardStartTime()));
                    sb.append(" - ");
                    sb.append(sdf2.format(workflowWork.getStandardEndTime()));
                    toolTip.setText(sb.toString());
                }
            });

            toggleButton.setOnDragDetected((MouseEvent event) -> {
                Optional.ofNullable(this.getOnDragDetected()).ifPresent(handler -> handler.handle(event));
            });

            toggleButton.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    WorkflowCellEventNotifier.raiseLeftMouseButtonDoubleClicked(this);
                }
            });

            toggleButton.widthProperty().addListener((observable, oldValue, newValue) -> {
                workNameLabel.setWrapText(newValue.doubleValue() >= 82.0);
            });
        } catch (Exception ex) {
        }
    }

    /**
     * Paneを取得する。
     *
     * @return
     */
    public Pane getNode() {
        return this.vbox;
    }

    public ConWorkflowWorkInfoEntity getWorkflowWork() {
        return workflowWork;
    }

    public String getWorkName() {
        return workName;
    }

    public String getWorkNameLabelText() {
        return workNameLabel.getText();
    }

    public void setWorkNameLabelText(String text) {
        workNameLabel.setText(text);
    }

    public String getEquipmentLabelText() {
        return equipmentLabel.getText();
    }

    public void setEquipmentLabelText(String text) {
        equipmentLabel.setText(text);
    }

    public String getOrganizationLabelText() {
        return organizationLabel.getText();
    }

    public void setOrganizationLabelText(String text) {
        organizationLabel.setText(text);
    }

    @Override
    public ToggleGroup getToggleGroup() {
        return toggleButton.getToggleGroup();
    }

    @Override
    public void setToggleGroup(ToggleGroup group) {
        toggleButton.setToggleGroup(group);
    }

    @Override
    public boolean isChecked() {
        return checkBox.isSelected();
    }

    @Override
    public void setChecked(boolean value) {
        checkBox.setSelected(value);
    }

    @Override
    public boolean isSelected() {
        return toggleButton.isSelected();
    }

    @Override
    public void setSelected(boolean value) {
        toggleButton.setSelected(value);
    }

    @Override
    protected void showMarker(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        boolean isSame = false;
        boolean isNext = false;
        boolean isRight = false;
        boolean isAncestor = false;
        if (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class))) {
            WorkflowFlowCellEntity entity = (WorkflowFlowCellEntity) dragboard.getContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class));
            // 同じセルでないか確認
            isSame = entity.getCellId().equals(getBpmnNode().getId());
            if (!isSame) {
                // 祖先関係有無の検索
                isAncestor = this.isAncestor(entity.getCellId());
                // 下のセルと同一でないか確認
                isNext = this.nextCell.getBpmnNode().getId().equals(entity.getCellId());
                // 右のセルと同一でないか確認
                if (this.prevCell instanceof ParallelStartCell) {
                    ParallelStartCell parent = (ParallelStartCell) this.prevCell;
                    int myIndex = parent.getFirstRow().indexOf(this);
                    isRight = (myIndex < (parent.getFirstRow().size() - 1)) ? parent.getFirstRow().get(myIndex + 1).getBpmnNode().getId().equals(entity.getCellId()) : false;
                }
            }
        }

        if (!isSame && !isAncestor && (dragboard.hasContent(DataFormatUtil.getDataFormat(WorkInfoEntity.class))
                || dragboard.hasContent(DataFormatUtil.getDataFormat(WorkflowFlowCellEntity.class)))) {

            // セル対角線の係数計算
            double coefficient = tan(atan2(cellPane.getHeight(), cellPane.getWidth()));

            // Styleの変更(座標位置に合わせて色替え)
            if ((!isNext && (event.getY() >= (cellPane.getHeight() / 2)) && (isRight || (event.getY() >= (event.getX() * coefficient))))) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                this.marker.setStyle("-fx-border-color: black black red black; -fx-border-width: 0 0 10 0;");
                this.markerStatus = MarkerStatus.BOTTOM;
            } else if (!isRight && (event.getX() >= (cellPane.getWidth() / 2))) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                this.marker.setStyle("-fx-border-color: black red black black; -fx-border-width: 0 10 0 0;");
                this.markerStatus = MarkerStatus.RIGHT;
            } else {
                this.marker.setStyle("-fx-border-color: black black black black; -fx-border-width: 0 0 0 0;");
                this.markerStatus = MarkerStatus.NONE;
            }
        }
    }

    /**
     * 上位にこのIDのセルが存在するか
     *
     * @param cellId
     * @return
     */
    private boolean isAncestor(String cellId) {
        CellBase tmp = this;
        while (Objects.nonNull(tmp) && !(tmp instanceof EndCell)) {
            if (tmp instanceof ParallelEndCell) {
                BpmnNode startNode = ((ParallelEndCell) tmp).getParallelStartCell().getBpmnNode();
                if (startNode.getId().equals(cellId)) {
                    return true;
                }
            }

            if (tmp instanceof ParallelStartCell) {
                tmp = ((ParallelStartCell) tmp).getParallelEndCell().getNextCell();
            } else {
                tmp = tmp.getNextCell();
            }
        }
        return false;
    }

    @Override
    public CellBase getNextCell() {
        return nextCell;
    }

    @Override
    public void setNextCell(CellBase nextCell) {
        this.nextCell = nextCell;
    }

    @Override
    public CellBase getPrevCell() {
        return prevCell;
    }

    @Override
    public void setPrevCell(CellBase prevCell) {
        this.prevCell = prevCell;
    }

    @Override
    public void setScale(double value) {
        this.toggleButton.setPrefSize(72.0 * value, 72.0 * value);
    }

    @Override
    public double getScale() {
        return this.toggleButton.getPrefHeight() / 72.0;
    }

    public void setVisibleOrganization(boolean value) {
        this.organizationLabel.setVisible(value);
    }

    public void setVisibleEquipment(boolean value) {
        this.equipmentLabel.setVisible(value);
    }
}
