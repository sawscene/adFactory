/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.schedule.cell;

import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleSearcher;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleTypeEnum;
import java.text.SimpleDateFormat;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import jp.adtekfuji.forfujiapp.common.UIControlInterface;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaItemEntity;
import jp.adtekfuji.forfujiapp.dialog.DialogController;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;

/**
 * サンプルデータの注入
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.06.thr
 */
public class ScheduleCell extends Label {

    private static final SimpleDateFormat dateDataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private CustomAgendaItemEntity item;

    public ScheduleCell(CustomAgendaItemEntity item, double width, double hight, ScheduleRecordTypeEnum typeEnum, ScheduleTypeEnum scheduleType) {
        setText(item.getTitle2());
        setStyle("-fx-font-size:" + 10 + ";" + "-fx-text-fill: " + item.getFontColor()
                + ";" + "-fx-background-color:" + item.getBackgraundColor()+ "; -fx-border-width: 0.3; -fx-border-color: black;");
        setAlignment(Pos.CENTER);
        setPrefSize(width, hight);
        setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        // ツールチップ
        Tooltip toolTip = TooltipBuilder.build();
        toolTip.setText(item.getTitle1() + "\n" + item.getTitle2() + "\n" + dateDataFormat.format(item.getStartTime()) + " - " + dateDataFormat.format(item.getEndTIme()));
        this.setTooltip(toolTip);

        //setOnMouseMoved((MouseEvent event) -> {
        //    Tooltip t = new Tooltip();
        //    t.setText(item.getTitle1() + "\n" + item.getTitle2() + "\n"
        //            + dateDataFormat.format(item.getStartTime()) + " - " + dateDataFormat.format(item.getEndTIme()));
        //    t.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        //    t.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14; -fx-border-width: 1; -fx-border-color: black;");
        //    t.setTextAlignment(TextAlignment.CENTER);
        //    t.setContentDisplay(ContentDisplay.CENTER);
        //    Tooltip.install(this, t);
        //    event.consume();
        //});

        // 計画編集処理
        if (typeEnum.equals(ScheduleRecordTypeEnum.PLAN_RECORD) && scheduleType.equals(ScheduleTypeEnum.ORGANIZATION_SCHEDULE)) {
            setOnMouseClicked((MouseEvent event) -> {
                if (Objects.nonNull(item.getKanbanId())) {
                    DialogController.showEditKanban(ScheduleSearcher.getKanban(item.getKanbanId()), new UIControlInterface() {
                        @Override
                        public void updateUI() {
                        }

                        @Override
                        public void blockUI(boolean isBlock) {
                        }
                    });
                }
            });
        }
    }

    public CustomAgendaItemEntity getItem() {
        return item;
    }

    public void setItem(CustomAgendaItemEntity item) {
        this.item = item;
    }

    private void buildTooltip(Control control, String text) {
        Tooltip toolTip = TooltipBuilder.build();
        toolTip.setText(text);
        control.setTooltip(toolTip);
    }
}
