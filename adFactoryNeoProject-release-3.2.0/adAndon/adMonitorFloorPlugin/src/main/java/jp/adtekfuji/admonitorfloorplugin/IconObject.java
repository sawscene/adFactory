/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.admonitorfloorplugin;

import java.util.Arrays;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author ke.yokoi
 */
public class IconObject {

    /**
     * ツールチップのフォント
     */
    private static final Font font = new Font("Meiryo UI", 20);

    /**
     * 点滅時間
     */
    private static final long blinkTime = Setting.GetBlinkTime();

    private StatusPatternEnum statusPattern = StatusPatternEnum.PLAN_NORMAL;
    private final Timeline timeline = new Timeline();
    private final Integer idWidth = 20;
    private final HBox hbox = new HBox();
    private final Label label2 = new Label();
    private final Rectangle backRect = new Rectangle();
    private final WorkEquipmentSetting workEquipSetting;
    private final long equId;
    private final String equName;
    private final double width;
    private final double height;
    private String reason;

    /**
     * RectObject
     *
     * @param id
     * @param text
     * @param posX
     * @param posY
     * @param width
     * @param height
     * @param setting
     * @param equId
     */
    public IconObject(final Integer id, final String text, double posX, double posY, double width, double height, WorkEquipmentSetting setting, Long equId) {
        this.workEquipSetting = setting;

        this.equId = equId;
        this.equName = text;
        this.width = width;
        this.height = height;

        //形状作成.
        Label label1 = new Label(id.toString());
        label1.setTextFill(Color.BLACK);
        //label1.setFont(Setting.getFont(idWidth.doubleValue(), label1.getText()));
        label1.setFont(Setting.getFont(this.idWidth.doubleValue(), "00"));
        label1.setContentDisplay(ContentDisplay.CENTER);
        label1.setAlignment(Pos.CENTER);
        label1.prefWidthProperty().set(this.idWidth);
        label1.prefHeightProperty().set(height);
        label1.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.rgb(0xFD, 0xEA, 0xDA).toString()), CornerRadii.EMPTY, Insets.EMPTY)));
        label1.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        label1.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        StackPane stack2 = new StackPane();
        stack2.prefWidthProperty().set(width - this.idWidth);
        stack2.prefHeightProperty().set(height);
        this.backRect.widthProperty().bind(stack2.widthProperty());
        this.backRect.heightProperty().bind(stack2.heightProperty());
        this.backRect.setFill(Color.WHITE);
        this.label2.setText(text);
        this.label2.setTextFill(Color.BLACK);
        this.label2.setFont(Setting.getFont(width - this.idWidth, height, this.label2.getText()));
        this.label2.setContentDisplay(ContentDisplay.CENTER);
        this.label2.setAlignment(Pos.CENTER);
        this.label2.prefWidthProperty().bind(stack2.widthProperty());
        this.label2.prefHeightProperty().bind(stack2.heightProperty());
        this.label2.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        this.label2.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        stack2.getChildren().addAll(this.backRect, this.label2);

        buildTooltip(this.label2, text);

        this.hbox.getChildren().addAll(label1, stack2);
        this.hbox.translateXProperty().set(posX);
        this.hbox.translateYProperty().set(posY);
        this.hbox.borderProperty().set(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
    }

    /**
     *
     * @return
     */
    public Node getNode() {
        return this.hbox;
    }

    /**
     *
     * @return
     */
    public WorkEquipmentSetting getSetting() {
        return this.workEquipSetting;
    }

    /**
     * ステータス反映
     *
     * @param status
     */
    public void Update(DisplayedStatusInfoEntity status) {
        try {
            LogManager.getLogger().info("Update start: {}", status);
            /*
             ActualNoticeCommand または CallingNoticeCommand のみしか呼び出されないので注意
             TimerCommand(定期更新) は上位でスルーするようにしている
             「休憩」状態は TimerCommand で通知されるためスルーされる
             よって、ここでは、「呼び出し」「中断」「開始」(変化が上位より通知される場合)のみの対応となる
             仮に、(現システムで)「休憩」を考慮すると、定期更新のタイミング分の遅延が発生することになる
             */
            Color foreColor = Color.web(status.getFontColor());
            Color backColor = Color.web(status.getBackColor());

            this.timeline.getKeyFrames().clear();

            double _width = (this.label2.getWidth() > 0) ? this.label2.getWidth() : this.width - this.idWidth;
            double _height = (this.label2.getHeight() > 0) ? this.label2.getHeight() : this.height;

            if (Arrays.asList(StatusPatternEnum.CALLING, StatusPatternEnum.SUSPEND_NORMAL, StatusPatternEnum.WORK_NORMAL).contains(status.getStatusName())) {
                if (LightPatternEnum.BLINK.equals(status.getLightPattern())) {
                    // 点滅
                    this.timeline.getKeyFrames().addAll(new KeyFrame(new Duration(0),
                            new KeyValue(this.backRect.fillProperty(), backColor),
                            new KeyValue(this.label2.fontProperty(), Setting.getFont(_width, _height, this.reason)),
                            new KeyValue(this.label2.textFillProperty(), foreColor),
                            new KeyValue(this.label2.textProperty(), this.reason)
                    ),
                            new KeyFrame(new Duration(blinkTime),
                                    new KeyValue(this.backRect.fillProperty(), Color.WHITE),
                                    new KeyValue(this.label2.fontProperty(), Setting.getFont(_width, _height, this.equName)),
                                    new KeyValue(this.label2.textFillProperty(), Color.BLACK),
                                    new KeyValue(this.label2.textProperty(), this.equName)
                            ),
                            new KeyFrame(new Duration(blinkTime * 2),
                                    new KeyValue(this.backRect.fillProperty(), Color.WHITE),
                                    new KeyValue(this.label2.fontProperty(), Setting.getFont(_width, _height, this.reason)),
                                    new KeyValue(this.label2.textFillProperty(), Color.BLACK),
                                    new KeyValue(this.label2.textProperty(), this.reason)
                            )
                    );

                } else {
                    // 点灯
                    if (!this.equName.equals(this.reason)) {
                        // 呼び出し理由が選択されている場合、設備名と呼び出し理由を交互に表示
                        this.timeline.getKeyFrames().addAll(new KeyFrame(new Duration(0),
                                new KeyValue(this.backRect.fillProperty(), backColor),
                                new KeyValue(this.label2.fontProperty(), Setting.getFont(_width, _height, this.reason)),
                                new KeyValue(this.label2.textFillProperty(), foreColor),
                                new KeyValue(this.label2.textProperty(), this.reason)),
                                new KeyFrame(new Duration(blinkTime),
                                        new KeyValue(this.backRect.fillProperty(), backColor),
                                        new KeyValue(this.label2.fontProperty(), Setting.getFont(_width, _height, this.equName)),
                                        new KeyValue(this.label2.textFillProperty(), foreColor),
                                        new KeyValue(this.label2.textProperty(), this.equName)),
                                new KeyFrame(new Duration(blinkTime * 2),
                                        new KeyValue(this.backRect.fillProperty(), backColor),
                                        new KeyValue(this.label2.fontProperty(), Setting.getFont(_width, _height, this.reason)),
                                        new KeyValue(this.label2.textFillProperty(), foreColor),
                                        new KeyValue(this.label2.textProperty(), this.reason))
                        );

                    } else {
                        this.backRect.setFill(backColor);
                        this.label2.setText(this.equName);
                        if (_width > 0) {
                            this.label2.setFont(Setting.getFont(_width, _height, this.equName));
                        }
                        this.label2.setTextFill(foreColor);
                    }
                }

            } else {
                this.backRect.setFill(backColor);
                this.label2.setText(this.equName);
                if (_width > 0) {
                    this.label2.setFont(Setting.getFont(_width, _height, this.equName));
                }
                this.label2.setTextFill(foreColor);
            }

        } finally {
            this.statusPattern = status.getStatusName();
            LogManager.getLogger().info("Update end: {}", status);
        }
    }

    /**
     * アニメーションを開始する。
     */
    public void playTimeline() {
        if (Objects.isNull(this.timeline) || this.timeline.getKeyFrames().isEmpty()) {
            return;
        }

        this.timeline.setAutoReverse(false);
        this.timeline.setCycleCount(Timeline.INDEFINITE);// 無限に繰り返す
        this.timeline.playFrom(Duration.ZERO);
    }

    /**
     * アニメーションを停止する。
     */
    public void stopTimeline() {
        if (Objects.isNull(this.timeline)) {
            return;
        }

        this.timeline.stop();
    }

    /**
     * ツールチップを構築する。
     *
     * @param control
     * @param text
     */
    private void buildTooltip(Control control, String text) {
        try{
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setText(text);
            toolTip.setFont(font);
        } catch(Exception ex) {
        }
    }

    /**
     * ステータスを取得する。
     *
     * @return
     */
    public StatusPatternEnum getStatusPattern() {
        return this.statusPattern;
    }

    /**
     * 呼出理由を取得する。
     * 
     * @return 呼出理由
     */
    public String getReason() {
        return reason;
    }

    /**
     * 呼出理由を設定する。
     *
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 設備IDを取得する。
     *
     * @return 設備ID
     */
    public Long getEquId() {
        return this.equId;
    }

    /**
     * 呼び出し中かどうかを取得する。
     *
     * @return 呼び出し中か (true:呼び出し中, false:呼び出し以外)
     */
    public boolean isCalling() {
        return StatusPatternEnum.CALLING.equals(this.statusPattern);
    }

    /**
     * 休憩中かどうかを取得する。
     *
     * @return 休憩中か (true:休憩中, false:休憩以外)
     */
    public boolean isBreaktime() {
        return StatusPatternEnum.BREAK_TIME.equals(this.statusPattern);
    }

    @Override
    public String toString() {
        return new StringBuilder("IconObject{")
                .append("statusPattern=").append(this.statusPattern)
                .append(", equId=").append(this.equId)
                .append(", equName=").append(this.equName)
                .append(", reason=").append(this.reason)
                .append("}")
                .toString();
    }
}
