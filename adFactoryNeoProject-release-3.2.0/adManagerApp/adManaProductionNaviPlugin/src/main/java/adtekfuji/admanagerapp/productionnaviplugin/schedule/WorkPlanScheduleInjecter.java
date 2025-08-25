/*
 *
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.schedule;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleShowConfig;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanSelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.cell.WorkPlanScheduleCell;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.cell.WorkPlanScheduleDateTimeCell;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.cell.WorkPlanScheduleRecordTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.schedule.cell.WorkPlanSerialCell;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.DateUtils;

import java.util.*;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaConcurrentEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaEntity;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaItemEntity;
import adtekfuji.admanagerapp.productionnaviplugin.utils.WorkPlanStyleInjecter;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * スケジュールの注入
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.06.thr
 */
public class WorkPlanScheduleInjecter {

    // スケールの設定値
    public double DATE_NUM = WorkPlanScheduleConstants.DEFAULT_DATE_NUM;
    private final long DAY_MILLIS = 86400000L;
    private Date DATE_START = new Date();
    private Date DATE_END = new Date();
    public double DATE_WIDTH = WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_WEEKLY;
    private final double DATE_HIGHT = WorkPlanScheduleConstants.DEFAULT_DATE_HIGHT;
    private final double SERIAL_HIGHT = WorkPlanScheduleConstants.DEFAULT_SERIAL_HIGHT;

    private final SceneContiner sc = SceneContiner.getInstance();
    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();

    private final String DATE_SEPARATE = "/";

    private final Map<Long, WorkPlanSerialCell> serialItems = new HashMap();
    private final Map<Long, VBox> scheduleItems = new HashMap();

    /**
     * 
     * @param config
     * @param startDate
     * @param endDate 
     */
    public WorkPlanScheduleInjecter(WorkPlanScheduleShowConfig config) {
        loadConfig(config);
    }

    /**
     * 
     * @param config 
     */
    public void loadConfig(WorkPlanScheduleShowConfig config) {
        DATE_NUM = (double) config.getBaseMonthlyDate();
        DATE_START = DateUtils.getBeginningOfDate(config.getBaseStartDate());
        DATE_END = DateUtils.getEndOfDate(config.getBaseEndDate());

        // TODO:DATE_WIDTHが定数以外で使用するとサイズが変わってしまうのであとで画面サイズに合わせてできる様にする
        if (Objects.isNull(config.getScheduleSize())) {
            DATE_WIDTH = WorkPlanScheduleConstants.DEFAULT_DATE_WIDTH * config.getDailyWidthMagnification();
        } else {
            switch (config.getScheduleSize()) {
                case DAILY:
                    DATE_WIDTH = WorkPlanScheduleConstants.DEFAULT_DATE_WIDTH * WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_DAILY;
                    break;
                case WEEKLY:
                    DATE_WIDTH = WorkPlanScheduleConstants.DEFAULT_DATE_WIDTH * WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_WEEKLY;
                    break;
                case MONTHLY:
                    DATE_WIDTH = WorkPlanScheduleConstants.DEFAULT_DATE_WIDTH * WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_MONTHLY;
                    break;
            }
        }
    }

    /**
     * 指定されたフィールドに日付のラベルを注入
     *
     * @param dateField
     * @param scrollPane
     */
    public void injectDate(HBox dateField, ScrollPane scrollPane) {
        //保持している情報を初期化
        dateField.getChildren().clear();
        dateField.setPrefSize(DATE_WIDTH * DATE_NUM, DATE_HIGHT);
        boolean isScale = DATE_WIDTH >= WorkPlanScheduleConstants.DEFAULT_DATE_WIDTH * WorkPlanScheduleConstants.DEFAULT_DATE_MAGNIFICATION_DAILY;

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(DATE_START);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(DATE_END);
        int diffMonth = endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);
        int diffYear = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
        // 月の差分がマイナス値だった場合別の年を挟んでいる。そのため年の差分の月を算出しマイナス値に足すことで2つの日付の月の差分を洗い出す。
        if (0 != diffMonth || 0 != diffYear) {
            diffMonth += ((endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)) * 12);
        }

        if (0 != diffMonth) {
            // 表示開始月の作成
            for (double i = (double) startDate.get(Calendar.DATE); i <= startDate.getActualMaximum(Calendar.DATE); i++) {
                dateField.getChildren().add(createDateLabel(String.valueOf((startDate.get(Calendar.MONTH) + 1) + DATE_SEPARATE + (int) i), isScale));
            }
            // 表示開始月と終了月の差分の月を作成
            for (int i = 1; i < diffMonth; i++) {
                Calendar diffMonthDate = Calendar.getInstance();
                diffMonthDate.clear();
                diffMonthDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH) + i, startDate.getActualMinimum(Calendar.DATE));
                for (double j = 0; j <= diffMonthDate.getActualMaximum(Calendar.DATE); j++) {
                    dateField.getChildren().add(createDateLabel(String.valueOf((diffMonthDate.get(Calendar.MONTH) + 1) + DATE_SEPARATE + ((int) j + 1)), isScale));
                }
            }
            // 表示終了月を作成
            for (double i = (double) endDate.getActualMinimum(Calendar.DATE); i <= (double) endDate.get(Calendar.DATE); i++) {
                dateField.getChildren().add(createDateLabel(String.valueOf((endDate.get(Calendar.MONTH) + 1) + DATE_SEPARATE + (int) i), isScale));
            }
        } else {
            for (double i = (double) startDate.get(Calendar.DATE) - 1; i < (double) endDate.get(Calendar.DATE); i++) {
                dateField.getChildren().add(createDateLabel(String.valueOf((startDate.get(Calendar.MONTH) + 1) + DATE_SEPARATE + ((int) i + 1)), isScale));
            }
        }
    }

    /**
     * 日付のラベル作成
     *
     * @param DateLabel
     * @param isScale
     * @return
     */
    private Node createDateLabel(String DateLabel, boolean isScale) {
        if (isScale) {
            WorkPlanScheduleDateTimeCell timeLabel = new WorkPlanScheduleDateTimeCell(DateLabel, DATE_WIDTH, DATE_HIGHT);
            return timeLabel;
        } else {
            Label timeLabel = new Label(DateLabel);
            timeLabel.setPrefSize(DATE_WIDTH, DATE_HIGHT);
            timeLabel.setAlignment(Pos.CENTER);
            WorkPlanStyleInjecter.setBorderColorStyle(timeLabel, "lightgray");
            return timeLabel;
        }
    }

    /**
     * 
     * @param serialField
     * @param scheduleFiled
     * @param agendas
     * @param workPlanScheduleType 
     */
    public void injectSerial(VBox serialField, VBox scheduleFiled
                    , List<WorkPlanCustomAgendaEntity> agendas, WorkPlanScheduleTypeEnum workPlanScheduleType) {
        injectSerial(serialField, scheduleFiled, agendas, workPlanScheduleType, null);
    }

    /**
     * 指定されたフィールドにカンバンのラベルを注入
     *
     * @param serialField
     * @param scheduleFiled
     * @param agendas
     * @param workPlanScheduleType
     * @param prop 
     */
    public void injectSerial(VBox serialField, VBox scheduleFiled, List<WorkPlanCustomAgendaEntity> agendas, WorkPlanScheduleTypeEnum workPlanScheduleType, Properties prop) {
        //保持している情報を初期化
        this.serialItems.clear();
        this.scheduleItems.clear();
        serialField.getChildren().clear();
        serialField.setPrefHeight(SERIAL_HIGHT * agendas.size());
        scheduleFiled.getChildren().clear();
        scheduleFiled.setPrefSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT * agendas.size());


        List<String> initTitleList =
                ProductionNaviPropertyConstants.InitTitleKeyList
                .stream()
                .map(LocaleUtils::getString)
                .collect(Collectors.toList());

        final String initSettingTitle = String.join(ProductionNaviPropertyConstants.TITLE_SEPARATOR, initTitleList);

        boolean modelFlg = false;
        boolean workFlg = false;
        boolean orderNoFlg = false;
        boolean cerialFlg = false;
        if (Objects.nonNull(prop)) {
            String title = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_TITLE, initSettingTitle);
            if (title.isEmpty()) {
                modelFlg = true;
                workFlg = true;
                orderNoFlg = true;
                cerialFlg = true;
            } else {
                Set<String> titleSet = Stream.of(title.split(ProductionNaviPropertyConstants.TITLE_SEPARATOR))
                        .collect(Collectors.toSet());

                if (titleSet.contains(LocaleUtils.getString(ProductionNaviPropertyConstants.WORKFLOW_NAME_KEY))) {
                    workFlg = true;
                }
                if (titleSet.contains(LocaleUtils.getString(ProductionNaviPropertyConstants.MODEL_NAME_KEY))) {
                    modelFlg = true;
                }
                if (titleSet.contains(LocaleUtils.getString(ProductionNaviPropertyConstants.ORDER_NUMBER_KEY))) {
                    orderNoFlg = true;
                }
                if (titleSet.contains(LocaleUtils.getString(ProductionNaviPropertyConstants.SERIAL_NUMBER_KEY))) {
                    cerialFlg = true;
                }
            }
        }

        for (WorkPlanCustomAgendaEntity agenda : agendas) {
            if (Objects.nonNull(agenda.getKanbanNameTitle())) {
                WorkPlanSerialCell serialCell = new WorkPlanSerialCell(agenda.getKanbanNameTitle(), modelFlg && Objects.nonNull(agenda.getModelNameTitle()) ? agenda.getModelNameTitle() : "", workFlg && Objects.nonNull(agenda.getWorkNoTitle()) ? agenda.getWorkNoTitle() : "", orderNoFlg && Objects.nonNull(agenda.getOrderNoTitle()) ? agenda.getOrderNoTitle() : "", cerialFlg && Objects.nonNull(agenda.getCerialTitle()) ? agenda.getCerialTitle() : "", SERIAL_HIGHT);
                serialCell.setUserData(agenda.getKanbanId());

                serialField.getChildren().add(serialCell);

                serialItems.put(agenda.getKanbanId(), serialCell);

                injectSchedule(scheduleFiled, agenda, workPlanScheduleType);

                // 作業計画の場合のみ、クリックイベントを追加する。(選択状態表示とカンバン編集)
                if (workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE)) {
                    serialCell.setOnMouseClicked((MouseEvent event) -> {
                        onRowClicked(event);
                    });
                }
            }
        }
    }

    /**
     * 指定されたフィールドにスケジュールを注入
     *
     * @param scheduleField
     * @param agenda 実際はカンバンと実績データ
     * @return
     */
    private VBox injectSchedule(VBox scheduleField, WorkPlanCustomAgendaEntity agenda, WorkPlanScheduleTypeEnum workPlanScheduleType) {
        StackPane scheduleStackPane = new StackPane();
        VBox scheduleBase = new VBox();
        try {
            //予定注入
            if (agenda.getScheduleCollection().size() > 0) {
                scheduleStackPane.getChildren().add(createLeavesData(agenda.getScheduleCollection()));
            }

            scheduleBase.setUserData(agenda.getKanbanId());

            Pane pane = new Pane();
            pane.setId("bgPane");
            pane.setStyle("-fx-background-color: transparent; -fx-border-width: 0.3; -fx-border-color: lightgray;");

            //日程注入
            scheduleBase.setPrefSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT);
            scheduleBase.setMaxSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT);
            scheduleBase.setMinSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT);
            WorkPlanStyleInjecter.setBorderColorStyle(scheduleBase, "lightgray");
            scheduleBase.getChildren().add(createScheduleCellData(agenda.getPlanCollection(), WorkPlanScheduleRecordTypeEnum.PLAN_RECORD, workPlanScheduleType));
            scheduleBase.getChildren().add(createScheduleCellData(agenda.getActualCollection(), WorkPlanScheduleRecordTypeEnum.ACTUAL_RECORD, workPlanScheduleType));

            scheduleStackPane.getChildren().add(pane);
            scheduleStackPane.getChildren().add(scheduleBase);

            scheduleField.getChildren().add(scheduleStackPane);

            scheduleItems.put(agenda.getKanbanId(), scheduleBase);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        // 作業計画の場合のみ、クリックイベントを追加する。(選択状態表示とカンバン編集)
        if (workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE)) {
            scheduleBase.setOnMouseClicked((MouseEvent event) -> {
                onRowClicked(event);
            });
        }

        return scheduleBase;
    }

    /**
     * 行クリックのアクション
     *
     * @param event 
     */
    private void onRowClicked(MouseEvent event) {
        try {
            long kanbanId;

            WorkPlanSerialCell serialCell;
            VBox scheduleBase;

            if (event.getSource() instanceof VBox) {
                scheduleBase = (VBox) event.getSource();
                kanbanId = (long) scheduleBase.getUserData();
            } else if (event.getSource() instanceof WorkPlanSerialCell) {
                serialCell = (WorkPlanSerialCell) event.getSource();
                kanbanId = (long) serialCell.getUserData();
            } else {
                return;
            }

            switch(event.getClickCount()) {
                case 1: // 選択状態変更
                    properties.setProperty(ProductionNaviPropertyConstants.SELECTED_KANBAN, String.valueOf(kanbanId));
                    break;
                case 2: //カンバン編集
                    callKanbanEdit(kanbanId, WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE);
                    break;
                default : break;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバン編集画面を表示する。
     *
     * @param kanbanId
     * @param workPlanScheduleType 
     */
    private void callKanbanEdit(long kanbanId, WorkPlanScheduleTypeEnum workPlanScheduleType) {
        try {
            WorkPlanSelectedKanbanAndHierarchy selected = new WorkPlanSelectedKanbanAndHierarchy(kanbanId, workPlanScheduleType);
            sc.setComponent("ContentNaviPane", "WorkPlanDetailCompo", selected);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } 
    }

    /**
     * 計画の情報を作成する
     *
     * @param concurrents　実際はカンバンデータ
     * @param typeEnum
     */
    private AnchorPane createScheduleCellData(List<WorkPlanCustomAgendaConcurrentEntity> concurrents
                                            , WorkPlanScheduleRecordTypeEnum typeEnum, WorkPlanScheduleTypeEnum workPlanScheduleType) {
        AnchorPane schedulePane = new AnchorPane();
        schedulePane.setPrefSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT / 2);
        Date timeStart = DATE_START;
        Date timeEnd = DATE_END;
        // 時間が重複しているカンバンごとにスケジュールを表示する
        for (WorkPlanCustomAgendaConcurrentEntity agendaConcu : concurrents) {
            int count = 0;
            agendaConcu.getItemCollection().sort(Comparator.comparing((item) -> item.getStartTime()));
            // カンバン情報の表示位置計算、及び生成
            for (WorkPlanCustomAgendaItemEntity agendaItemEntity : agendaConcu.getItemCollection()) {
                if (agendaItemEntity != null) {
                    Date timeStartProcess = agendaItemEntity.getStartTime();
                    Date timeEndProcess = agendaItemEntity.getEndTime();
                    if (Objects.isNull(timeStartProcess) || Objects.isNull(timeEndProcess)) {
                        continue;
                    }

                    if (timeEndProcess.compareTo(timeStart) > 0 || timeStartProcess.compareTo(timeEnd) > 0) {
                        if (timeStartProcess.compareTo(timeStart) < 0 && timeEndProcess.compareTo(timeStart) > 0) {
                            timeStartProcess = timeStart;
                        }
                        if (timeEndProcess.compareTo(timeEnd) > 0) {
                            timeEndProcess = timeEnd;
                        }
                        double cycleTimeStartEnd = (timeEnd.getTime() - timeStart.getTime()) / 1000.0 / 60.0;
                        double cycleTimeStartEndProcess = (timeEndProcess.getTime() - timeStartProcess.getTime()) / 1000.0 / 60.0;
                        double transRate = (timeStartProcess.getTime() - timeStart.getTime()) / 1000.0 / 60.0;
                        // 予実用表示ノードを作成
                        // 横幅：日数に対しての実作業時間、縦幅：シリアルの高さを並列時間の工程で割る
                        // 縦幅: 並列表示の際にScheduleCellが膨張して計画表全体がシリアル表示部分とずれるため
                        // 余白として縦幅を-1.5している
                        WorkPlanScheduleCell plan = new WorkPlanScheduleCell(agendaItemEntity,
                                (DATE_WIDTH * DATE_NUM / cycleTimeStartEnd) * cycleTimeStartEndProcess,
                                ((SERIAL_HIGHT / 2) / agendaConcu.getItemCollection().size()) - 1.5,
                                typeEnum, workPlanScheduleType);
                        // 表示位置を指定する
                        // 表示高さ：並列作業の場合は高さを表示順番に合わせて変更、表示位置：全体の日数に対して自分がどの位置にいるか
                        AnchorPane.setTopAnchor(plan, ((SERIAL_HIGHT / 2) / agendaConcu.getItemCollection().size()) * count);
                        AnchorPane.setLeftAnchor(plan, (DATE_WIDTH * DATE_NUM / cycleTimeStartEnd) * transRate);
                        logger.debug(" setTopAnchor:" + ((SERIAL_HIGHT / 2) / agendaConcu.getItemCollection().size()) * count);
                        logger.debug(" setLeftAnchor:" + (DATE_WIDTH * DATE_NUM / cycleTimeStartEnd) * transRate);

                        schedulePane.getChildren().addAll(plan);
                        count = count + 1;
                    }
                }
            }
        }
        
        return schedulePane;
    }

    /**
     * 時間軸の設定
     *
     * @param timeLine 時間軸のノード
     */
    public void setTimeLine(Label timeLine) {
        logger.debug("★ 時間軸の設定");
        Date today = new Date();
        timeLine.setVisible(true);
        // 表示範囲よりも今日が外だった場合非表示
        if (DATE_START.after(today) && DATE_END.before(today)) {
            timeLine.setTranslateX(0.0d);
            timeLine.setVisible(false);
            return;
        }
        double datePercent
                = (double) (today.getTime() - DATE_START.getTime()) / (double) (DATE_END.getTime() - DATE_START.getTime());
        timeLine.setTranslateX((double) ((DATE_WIDTH * DATE_NUM) * datePercent));
        timeLine.toFront();
    }

    /**
     * スケジュールの表示位置を今日の日付に設定
     *
     * @param scrollPane
     */
    public void setScheduleDateNowPoint(ScrollPane scrollPane) {
        logger.debug("★ スケジュールの表示位置を今日の日付に設定");
        Date today = new Date();
        if (DATE_START.after(today) && DATE_END.before(today)) {
            scrollPane.setHvalue(0.0d);
            return;
        }
        double datePercent = (double) (today.getTime() - DATE_START.getTime()) / (double) (DATE_END.getTime() - DATE_START.getTime());
        double todayPoint = (double) ((DATE_WIDTH * DATE_NUM) * datePercent);
        
        scrollPane.setHvalue(todayPoint);
    }
    
    /**
     * スケジュールの表示位置を今日の日付に設定
     *
     * @param scrollPane
     * @param scalPosition
     */
    public void setScheduleDateNowPoint(ScrollPane scrollPane, double scalPosition) {
        logger.debug("★ スケジュールの表示位置を今日の日付に設定");
        scrollPane.setHvalue(scalPosition);
    }

    /**
     * 指定されたフィールドに休日を注入
     *
     * @param holidayField
     * @param holidays
     */
    public void injectHolidays(AnchorPane holidayField, List<HolidayInfoEntity> holidays) {
        try{
            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            holidayField.getChildren().clear();
            for(HolidayInfoEntity h : holidays) {
                Label holiday = new Label();
                holiday.setPrefWidth(DATE_WIDTH);
                holiday.setText(h.getHolidayName());
                holiday.setAlignment(Pos.CENTER);

                //表示設定で設定した休日文字色と背景色注入
                String holidayColorChar = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_CHAR);
                String holidayColorBack = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_HOLIDAY_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_HOLIDAY_COLOR_BACK);
                if(holidayColorChar.contains("0x")) {
                    holidayColorChar = "#".concat(holidayColorChar.substring(2, 8));
                }
                if(holidayColorBack.contains("0x")) {
                    holidayColorBack = "#".concat(holidayColorBack.substring(2, 8));
                }
                holiday.setStyle("-fx-text-fill: " + holidayColorChar + "; -fx-background-color:" + holidayColorBack +";");

                //height
                AnchorPane.setTopAnchor(holiday, 0.0);
                AnchorPane.setBottomAnchor(holiday, 0.0);
                
                //注入日
                double datePercent
                        = (double) (h.getHolidayDate().getTime() - DATE_START.getTime()) / (double) (DATE_END.getTime() - DATE_START.getTime());
                AnchorPane.setLeftAnchor(holiday, (double)((DATE_WIDTH * DATE_NUM) * datePercent));
                
                //inject
                holidayField.getChildren().add(holiday);
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 
     * @param leaves
     * @return 
     */
    public AnchorPane createLeavesData(List<ScheduleInfoEntity> leaves) {
        AnchorPane leaveField = new AnchorPane();
        try{
            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            for(ScheduleInfoEntity l : leaves) {
                
                Label leave = new Label();
                
                //表示設定で設定した休日文字色と背景色注入
                String leaveColorChar = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_CHAR, ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_CHAR);
                String leaveColorBack = prop.getProperty(ProductionNaviPropertyConstants.KEY_SETTING_PLANS_COLOR_BACK, ProductionNaviPropertyConstants.INIT_SETTING_PLANS_COLOR_BACK);
                if(leaveColorChar.contains("0x")) {
                    leaveColorChar = "#".concat(leaveColorChar.substring(2, 8));
                }
                if(leaveColorBack.contains("0x")) {
                    leaveColorBack = "#".concat(leaveColorBack.substring(2, 8));
                }
                leave.setStyle("-fx-text-fill: " + leaveColorChar + "; -fx-background-color:" + leaveColorBack +";");

                //text
                leave.setText(l.getScheduleName());
                leave.setAlignment(Pos.CENTER);
           
                //width
                double leaveTime = l.getScheduleToDate().getTime() - l.getScheduleFromDate().getTime();
                double width = DATE_WIDTH * leaveTime / DAY_MILLIS ;
                leave.setPrefWidth(width);
                
                //height
                AnchorPane.setTopAnchor(leave, 0.0);
                AnchorPane.setBottomAnchor(leave, 0.0);
                
                //start位置
                double datePercent
                        = (double) (l.getScheduleFromDate().getTime() - DATE_START.getTime()) / (double) (DATE_END.getTime() - DATE_START.getTime());
                AnchorPane.setLeftAnchor(leave, (double)((DATE_WIDTH * DATE_NUM) * datePercent));
                
                //inject
                leaveField.getChildren().add(leave);
            }
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
        return leaveField;
    }

    public Map<Long, WorkPlanSerialCell> getSerialItems() {
        return serialItems;
    }

    public Map<Long, VBox> getScheduleItems() {
        return scheduleItems;
    }
}
