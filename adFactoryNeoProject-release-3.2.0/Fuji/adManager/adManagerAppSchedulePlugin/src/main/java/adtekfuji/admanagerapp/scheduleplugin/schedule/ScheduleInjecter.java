/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.scheduleplugin.schedule;

import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleConstants;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleShowConfig;
import adtekfuji.admanagerapp.scheduleplugin.common.ScheduleTypeEnum;
import adtekfuji.admanagerapp.scheduleplugin.schedule.cell.ScheduleCell;
import adtekfuji.admanagerapp.scheduleplugin.schedule.cell.ScheduleDateTimeCell;
import adtekfuji.admanagerapp.scheduleplugin.schedule.cell.ScheduleRecordTypeEnum;
import adtekfuji.admanagerapp.scheduleplugin.schedule.cell.SerialCell;
import adtekfuji.utility.DateUtils;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaConcurrentEntity;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaEntity;
import jp.adtekfuji.forfujiapp.common.agenda.CustomAgendaItemEntity;
import jp.adtekfuji.forfujiapp.utils.StyleInjecter;

/**
 * スケジュールの注入
 *
 * @author e-mori
 * @version Fver
 * @since 2016.07.06.thr
 */
public class ScheduleInjecter {

    // スケールの設定値
    private static double DATE_NUM = ScheduleConstants.DEFAULT_DATE_NUM;
    private static Date DATE_START = new Date();
    private static Date DATE_END = new Date();
    private static double DATE_WIDTH = ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_WEEKLY;
    private static final double DATE_HIGHT = ScheduleConstants.DEFAULT_DATE_HIGHT;
    private static final double SERIAL_HIGHT = ScheduleConstants.DEFAULT_SERIAL_HIGHT;

    private static final String DATE_SEPARATE = "/";

    public static void loadConfig(ScheduleShowConfig config) {
        DATE_NUM = (double) config.getBaseMonthlyDate();
        DATE_START = config.getBaseStartDate();
        DATE_END = config.getBaseEndDate();

        // TODO:DATE_WIDTHが定数以外で使用するとサイズが変わってしまうのであとで画面サイズに合わせてできる様にする
        if (Objects.isNull(config.getScheduleSize())) {
            DATE_WIDTH = ScheduleConstants.DEFAULT_DATE_WIDTH * config.getDailyWidthMagnification();
        } else {
            switch (config.getScheduleSize()) {
                case DAILY:
                    DATE_WIDTH = ScheduleConstants.DEFAULT_DATE_WIDTH * ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_DAILY;
                    break;
                case WEEKLY:
                    DATE_WIDTH = ScheduleConstants.DEFAULT_DATE_WIDTH * ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_WEEKLY;
                    break;
                case MONTHLY:
                    DATE_WIDTH = ScheduleConstants.DEFAULT_DATE_WIDTH * ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_MONTHLY;
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
    public static void injectDate(HBox dateField, ScrollPane scrollPane) {
        //保持している情報を初期化
        dateField.getChildren().clear();
        dateField.setPrefSize(DATE_WIDTH * DATE_NUM, DATE_HIGHT);
        boolean isScale = DATE_WIDTH >= ScheduleConstants.DEFAULT_DATE_WIDTH * ScheduleConstants.DEFAULT_DATE_MAGNIFICATION_DAILY;

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
    private static Node createDateLabel(String DateLabel, boolean isScale) {
        if (isScale) {
            ScheduleDateTimeCell timeLabel = new ScheduleDateTimeCell(DateLabel, DATE_WIDTH, DATE_HIGHT);
            return timeLabel;
        } else {
            Label timeLabel = new Label(DateLabel);
            timeLabel.setPrefSize(DATE_WIDTH, DATE_HIGHT);
            timeLabel.setAlignment(Pos.CENTER);
            StyleInjecter.setBorderColorStyle(timeLabel, "lightgray");
            return timeLabel;
        }
    }

    /**
     * 指定されたフィールドにカンバンのラベルを注入
     *
     * @param startDate
     * @param endDate
     * @param serialField
     * @param scheduleFiled
     * @param agendas
     * @param scheduleType
     */
    public static void injectSerial(Date startDate, Date endDate, VBox serialField, VBox scheduleFiled, List<CustomAgendaEntity> agendas, ScheduleTypeEnum scheduleType) {

        //保持している情報を初期化
        serialField.getChildren().clear();
        serialField.setPrefHeight(SERIAL_HIGHT * agendas.size());
        scheduleFiled.getChildren().clear();
        scheduleFiled.setPrefSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT * agendas.size());
        for (CustomAgendaEntity agenda : agendas) {
            if (Objects.nonNull(agenda.getTitle1())) {
                serialField.getChildren().add(new SerialCell(agenda.getTitle1(), SERIAL_HIGHT));
                injectSchedule(startDate, endDate, scheduleFiled, agenda, scheduleType);
            }
        }

    }

    /**
     * 指定されたフィールドにスケジュールを注入
     *
     * @param startDate
     * @param endDate
     * @param scheduleFiled
     * @param agenda 実際はカンバンと実績データ
     * @return
     */
    private static VBox injectSchedule(Date startDate, Date endDate, VBox scheduleFiled, CustomAgendaEntity agenda, ScheduleTypeEnum scheduleType) {
        VBox scheduleBase = new VBox();
        try {
            scheduleBase.setPrefSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT);
            scheduleBase.setMaxSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT);
            scheduleBase.setMinSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT);
            StyleInjecter.setBorderColorStyle(scheduleBase, "lightgray");
            scheduleBase.getChildren().add(createScheduleCellData(startDate, endDate, agenda.getPlanCollection(), ScheduleRecordTypeEnum.PLAN_RECORD, scheduleType));
            scheduleBase.getChildren().add(createScheduleCellData(startDate, endDate, agenda.getActualCollection(), ScheduleRecordTypeEnum.ACTUAL_RECORD, scheduleType));
            scheduleFiled.getChildren().add(scheduleBase);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return scheduleBase;
    }

    /**
     * 計画の情報を作成する
     *
     * @param startDate
     * @param endDate
     * @param concurrents　実際はカンバンデータ
     * @param typeEnum
     */
    private static AnchorPane createScheduleCellData(Date startDate, Date endDate, List<CustomAgendaConcurrentEntity> concurrents, ScheduleRecordTypeEnum typeEnum, ScheduleTypeEnum scheduleType) {
        AnchorPane schedulePane = new AnchorPane();
        schedulePane.setPrefSize(DATE_WIDTH * DATE_NUM, SERIAL_HIGHT / 2);
        Date timeStart = DateUtils.getBeginningOfDate(startDate);
        Date timeEnd = DateUtils.getEndOfDate(endDate);
        // 時間が重複しているカンバンごとにスケジュールを表示する
        for (CustomAgendaConcurrentEntity agendaConcu : concurrents) {
            int count = 0;
            agendaConcu.getItemCollection().sort(Comparator.comparing((item) -> item.getStartTime()));
            // カンバン情報の表示位置計算、及び生成
            for (CustomAgendaItemEntity agendaItemEntity : agendaConcu.getItemCollection()) {
                if (agendaItemEntity != null) {
                    Date timeStartProcess = agendaItemEntity.getStartTime();
                    Date timeEndProcess = agendaItemEntity.getEndTIme();
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
                        double cycleTimeStarEnd = (timeEnd.getTime() - timeStart.getTime()) / 1000.0 / 60.0;
                        double cycleTimeStarEndProcess = (timeEndProcess.getTime() - timeStartProcess.getTime()) / 1000.0 / 60.0;
                        double transRate = (timeStartProcess.getTime() - timeStart.getTime()) / 1000.0 / 60.0;
                        // 予実用表示ノードを作成
                        // 横幅：日数に対しての実作業時間、縦幅：シリアルの高さを並列時間の工程で割る
                        // 縦幅: 並列表示の際にScheduleCellが膨張して計画表全体がシリアル表示部分とずれるため
                        // 余白として縦幅を-1.5している
                        ScheduleCell plan = new ScheduleCell(agendaItemEntity,
                                (DATE_WIDTH * DATE_NUM / cycleTimeStarEnd) * cycleTimeStarEndProcess,
                                ((SERIAL_HIGHT / 2) / agendaConcu.getItemCollection().size()) - 1.5,
                                typeEnum, scheduleType);
                        // 表示位置を指定する
                        // 表示高さ：並列作業の場合は高さを表示順番に合わせて変更、表示位置：全体の日数に対して自分がどの位置にいるか
                        AnchorPane.setTopAnchor(plan, ((SERIAL_HIGHT / 2) / agendaConcu.getItemCollection().size()) * count);
                        AnchorPane.setLeftAnchor(plan, (DATE_WIDTH * DATE_NUM / cycleTimeStarEnd) * transRate);

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
     * @param startDate
     * @param endDate
     */
    public static void setTimeLine(Label timeLine, Date startDate, Date endDate) {
        Date today = new Date();
        timeLine.setVisible(true);
        // 表示範囲よりも今日が外だった場合非表示
        if (startDate.after(today) && endDate.before(today)) {
            timeLine.setTranslateX(0.0d);
            timeLine.setVisible(false);
            return;
        }
        double datePercent
                = (double) (today.getTime() - startDate.getTime()) / (double) (endDate.getTime() - startDate.getTime());
        timeLine.setTranslateX((double) ((DATE_WIDTH * DATE_NUM) * datePercent));
        timeLine.toFront();
    }

    /**
     * スケジュールの表示位置を今日の日付に設定
     *
     * @param scrollPane
     * @param startDate
     * @param endDate
     */
    public static void setScheduleDateNowPoint(ScrollPane scrollPane, Date startDate, Date endDate) {
        Date today = new Date();
        if (startDate.after(today) && endDate.before(today)) {
            scrollPane.setHvalue(0.0d);
            return;
        }
        double datePercent
                = (double) (today.getTime() - startDate.getTime()) / (double) (endDate.getTime() - startDate.getTime());
        double todayPoint = (double) ((DATE_WIDTH * DATE_NUM) * datePercent);
        scrollPane.setHvalue(todayPoint);
    }
//
//    /**
//     * スケジュールの表示位置を指定した日付に設定
//     *
//     * @param scrollPane
//     * @param nowDate
//     * @param lastDate
//     */
//    public static void setScheduleDatePoint(ScrollPane scrollPane, int nowDate, int lastDate) {
//        if (((double) nowDate / (double) lastDate) < 0.5) {
//            scrollPane.setHvalue(((double) nowDate - 1.0) / (double) lastDate);
//        } else {
//            scrollPane.setHvalue((double) nowDate / (double) lastDate);
//        }
//    }
}
