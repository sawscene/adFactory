/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.component.lite;

import adtekfuji.andon.agenda.common.AgendaCompoInterface;
import adtekfuji.andon.agenda.common.Blinking;
import adtekfuji.andon.agenda.common.CallingPool;
import adtekfuji.andon.agenda.common.Constants;
import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.model.AgendaModel;
import adtekfuji.andon.agenda.model.data.*;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringTime;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画実績ペインのコントローラー
 *
 * @author ke.yokoi
 */
@FxComponent(id = "AgendaWorkerStatusLite", fxmlPath = "/fxml/andon/agenda/lite/agenda_worker_status_lite.fxml")
public class AgendaWorkerStatusLite implements Initializable, ArgumentDelivery, ComponentHandler, AgendaCompoInterface {

    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();
    private final CallingPool callingPool = CallingPool.getInstance();
    private Optional<DisplayedStatusInfoEntity> callerStatusInfo;

    private final String crlf = System.getProperty("line.separator");
    private final String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";
    private String pickLiteWorkNameRegex;

    private Timeline pageToggleTimeline;
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private ResourceBundle rb;// = ResourceBundle.getBundle("locale.locale");
    private final CurrentData currentData = CurrentData.getInstance();
    private final AgendaModel model = AgendaModel.getInstance();
    private final Blinking blinking = Blinking.getInstance();

    private Map<Long, Agenda> agendas = new LinkedHashMap<>();

    @FXML
    private AnchorPane contentPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.rb = rb;

            // ウィンドウをディスプレイの解像度に合わせる
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            logger.info("Screen: Width={0} Height={1}", visualBounds.getWidth(), visualBounds.getHeight());

            contentPane.setPrefHeight(visualBounds.getHeight() - 160.0);
            contentPane.setPrefWidth(visualBounds.getWidth());

            blinking.stop();

            // プロパティから取得
            this.pickLiteWorkNameRegex = properties.getProperty(PICK_LITE_WORK_NAME_REGEX_KEY);
            // 呼出し
            this.callerStatusInfo = model.getDisplayedStatuses().stream().filter(s -> s.getStatusName() == StatusPatternEnum.CALLING ).findFirst();

            draw();

            model.setController(this);

            // 表示倍率を設定
            Scene scene = sc.getStage().getScene();

            this.transforms(visualBounds.getWidth(), visualBounds.getHeight(), scene.getWidth(), scene.getHeight());
            
            scene.widthProperty().addListener((observable, oldValue, newValue) -> {
                this.transforms(visualBounds.getWidth(), visualBounds.getHeight(), scene.getWidth(), scene.getHeight());
            });

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 表示倍率を設定する。
     *
     * @param oldWidth
     * @param oldHeight
     * @param newWidth
     * @param newHeight
     */
    private void transforms(double oldWidth, double oldHeight, double newWidth, double newHeight) {
        if (Objects.isNull(this.contentPane) && Objects.isNull(this.contentPane.getTransforms())) {
            return;
        }
        double ratio = newWidth / oldWidth;
        this.contentPane.getTransforms().clear();
        this.contentPane.getTransforms().add(new Scale(ratio, ratio, 0, 0));
    }

    @Override
    public void setArgument(Object argument) {
    }

    @Override
    public boolean destoryComponent() {
        logger.info("destroyComponent start.");
        if (Objects.nonNull(pageToggleTimeline)) {
            pageToggleTimeline.stop();
            pageToggleTimeline.getKeyFrames().clear();
        }
        return true;
    }

    /**
     * Update display by update interval time
     *
     */
    @Override
    public void updateDisplay() {
        logger.info("updateDisplay start.");
        try {
            blinking.stop();
            contentPane.getChildren().clear();
            agendas = currentData.getAgendas();
            draw();
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            logger.info("updateDisplay end.");
        }
    }

    /**
     * 呼出しを使うか
     */
    @Override
    public boolean isUseCall() {
        return true;
    };

    /**
     * コンテンツを描画する。
     *
     */
    private void draw() {
        try {
            if (this.agendas.size() <= 0) {
                return;
            }

            // 表示する項目を表示数で分割
            int num = this.config.getProcessDisplayColumns() * this.config.getProcessDisplayRows();
            IntStream indices = this.agendas.size() % num == 0
                    ? IntStream.range(0, this.agendas.size() / num)
                    : IntStream.rangeClosed(0, this.agendas.size() / num);

            List<List<Entry<Long, Agenda>>> separated = indices
                    .mapToObj(i -> this.agendas.entrySet().stream()
                            .limit(i * num + num)
                            .skip(i * num)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());

            if (this.config.isTogglePages() && separated.size() > 1) {
                // ページ切り替え
                List<KeyFrame> keyframes = IntStream.range(0, separated.size())
                        .boxed()
                        .map(i -> new KeyFrame(Duration.seconds(i * this.config.getToggleTime()), (event) -> {
                            drawGrid(separated.get(i));
                        }))
                        .collect(Collectors.toList());

                // ループの最後と先頭が同じ時間になってしまうため最後の要素を再度追加
                keyframes.add(new KeyFrame(Duration.seconds(separated.size() * this.config.getToggleTime()), event -> {
                    drawGrid(separated.get(separated.size() - 1));
                }));

                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }

                pageToggleTimeline = new Timeline();
                pageToggleTimeline.getKeyFrames().addAll(keyframes);
                pageToggleTimeline.setCycleCount(Timeline.INDEFINITE);
                pageToggleTimeline.play();

            } else {
                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }
                drawGrid(separated.stream().flatMap(elm -> elm.stream()).collect(Collectors.toList()));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 
     * @param partial 
     */
    private void drawGrid(List<Entry<Long, Agenda>> partial) {
        try {
            // 画面表示なしの場合、描画更新は行なわない。
            if (!KanbanStatusConfig.getEnableView()) {
                return;
            }
            // AgendaModelで描画停止状態の時は描画処理を行なわない。
            if (!model.isUpdate()) {
                return;
            }
            if (partial.size() <= 0) {
                return;
            }

            contentPane.getChildren().clear();

            // 実績列が設定のカラム数より大きいなら設定を優先する
            int colums = this.config.getProcessDisplayColumns();
            int rows = this.config.getProcessDisplayRows();
            double paneWidth = (contentPane.getPrefWidth() - 10*colums) / colums;
            double paneHeight = contentPane.getPrefHeight()/ rows;
            
            GridPane gridPane = new GridPane();
            gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

            int count = 0;
            for (Entry<Long, Agenda> entry : partial) {

                Agenda agenda = entry.getValue();

                // 作業者名のラベル
                String fontColor = "black";
                String backColor = "lightgray";
                Label lable1 = this.mekeLabel(agenda.getTitle2(), paneWidth/2, paneHeight, config.getTitleFontSize(), fontColor, backColor);

                // 最後の実績を取得する
                List<AgendaTopic> actualTopics = agenda.getActuals().stream()
                    .sorted(this.actualGroupListComparator)
                    .map(AgendaGroup::getTopics)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

                String labelText = rb.getString("key.LiteStatusNoWork");
                fontColor = "gray";
                backColor = "white";  
                boolean isBlinking = false;
                boolean isOverTaktTime = false;
                if (actualTopics.size() > 0) {
                    AgendaTopic lastActualTopic = actualTopics.get(actualTopics.size()-1);
                    String workName = lastActualTopic.getTitle3();
                    
                    long sumTime = lastActualTopic.getSumTimes();
                    long overTimeMillis = sumTime - lastActualTopic.getTaktTime();
                    
                    if (!lastActualTopic.getIsIndirectData()) {
                        //工程名だけ抽出
                        try
                        {
                            Matcher m = Pattern.compile(this.pickLiteWorkNameRegex).matcher(workName);
                            if (m.find()) {
                                workName = m.group(1);
                            }
                        } catch(Exception ex) {
                        }

                        // 工程順名(モデル名)/カンバン名/工程名 のラベル
                        labelText = lastActualTopic.getTitle2() + crlf + lastActualTopic.getTitle1() + crlf + workName;
                        
                        // 累計作業時間（HH:MM:SS）を表示
                        String workTime = StringTime.convertMillisToStringTime(sumTime);
                        
                        String overTime = "";
                        // 工程のタクトタイムが設定されている場合、超過時間を表示
                        if (Objects.nonNull(lastActualTopic.getTaktTime()) && lastActualTopic.getTaktTime() > 0) {
                            // 超過時間が1時間を超えない場合、時間の部分を表示しない（MM:SS）
                            if (1000 <= overTimeMillis && overTimeMillis < 3600000) {
                                isOverTaktTime = true;
                                overTime = "  (+" + StringTime.convertMillisToStringTime(overTimeMillis).substring(3) + ")";
                            // 超過時間が1時間を超える場合、時間の部分まで表示する（HH:MM:SS）
                            } else if (3600000 <= overTimeMillis) {
                                isOverTaktTime = true;
                                overTime = "  (+" + StringTime.convertMillisToStringTime(overTimeMillis) + ")";
                            }
                        }
                        
                        labelText = labelText + crlf + workTime + overTime;

                        if (!StringUtils.isEmpty(lastActualTopic.getReason())) {
                            labelText = labelText + crlf + crlf + lastActualTopic.getReason();
                        }
                    } else {
                        // 間接作業名
                        labelText = StringUtils.isEmpty(lastActualTopic.getReason())? workName : lastActualTopic.getReason();
                    }

                    // スタイル
                    fontColor = "black";
                    backColor = "lightgray";
                    isBlinking = false;
                    if (Boolean.valueOf(properties.getProperty(Constants.ENABLE_LITE_TAKT_TIME, Constants.ENABLE_LITE_TAKT_TIME_DEFAULT))) {
                        // 進捗アラート機能が有効の場合
                        fontColor = lastActualTopic.getFontColor();
                        backColor = lastActualTopic.getBackColor();
                        isBlinking = lastActualTopic.isBlink();
                    } else {
                        if (lastActualTopic.getWorkKanbanStatus() != KanbanStatusEnum.COMPLETION) {
                            Optional<DisplayedStatusInfoEntity> opt = model.getDisplayedStatuses().stream()
                                .filter(s -> s.getStatusName() == StatusPatternEnum.toStatusPattern(lastActualTopic.getActualStatus())).findFirst();
                            if (opt.isPresent()) {
                                fontColor = opt.get().getFontColor();
                                backColor = opt.get().getBackColor();
                                isBlinking = opt.get().getLightPattern() == LightPatternEnum.BLINK;
                            }
                        } else {
                            Optional<DisplayedStatusInfoEntity> opt = model.getDisplayedStatuses().stream()
                                .filter(s -> s.getStatusName() == StatusPatternEnum.COMP_NORMAL).findFirst();
                            if (opt.isPresent()) {
                                fontColor = opt.get().getFontColor();
                                backColor = opt.get().getBackColor();
                                isBlinking = opt.get().getLightPattern() == LightPatternEnum.BLINK;
                            }
                        }
                    }
                }

                // 呼出し
                if (callingPool.isOrganizationCall(entry.getKey())) {
                    if (callerStatusInfo.isPresent()) {
                        DisplayedStatusInfoEntity statusInfo = callerStatusInfo.get();
                        isBlinking = isBlinking || statusInfo.getLightPattern() == LightPatternEnum.BLINK;
                        fontColor = statusInfo.getFontColor();
                        backColor = statusInfo.getBackColor();
                    } else {
                        isBlinking = true;
                        fontColor = "black";
                        backColor = "red";
                    }
                    // /呼び出し理由
                    labelText = callingPool.getCallReason();
                }

                AnchorPane workPane = new AnchorPane();
                // テキストラベル
                Label textLabel = this.mekeTextLabel(labelText, paneWidth/2, paneHeight, config.getItemFontSize(), fontColor);

                // 作業背景ラベルの縦横のうち短い方の長さの2%の長さをボーダーラインの幅とする
                double borderWidth = paneWidth/2 / 50;
                if (paneWidth/2 >= paneHeight) {
                    borderWidth = paneHeight / 50;
                }
                // 作業背景ラベル
                Label workLabel = this.mekeWorkLabel(paneWidth/2, paneHeight, backColor, borderWidth, isOverTaktTime);
                // 作業進捗ラベル
                Label workProgressLabel = new Label();
                if (actualTopics.size() > 0) {
                    AgendaTopic topic = actualTopics.get(actualTopics.size() - 1);
                    if (Objects.nonNull(topic.getProgress())) {
                        workProgressLabel = this.mekeWorkProgressLabel(workProgressLabel, topic.getProgress(), paneWidth/2, paneHeight, borderWidth, isOverTaktTime);
                    }
                }
                
                workPane.getChildren().add(workLabel);
                workPane.getChildren().add(workProgressLabel);
                workPane.getChildren().add(textLabel);
               
                // 点滅
                if (isBlinking) {
                    blinking.play(textLabel);
                    blinking.play(workLabel);
                    blinking.play(workProgressLabel);
                }

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.setPadding(new Insets(5, 5, 5, 5));
                hBox.getChildren().addAll(lable1, workPane);
                hBox.setPrefWidth(Control.USE_COMPUTED_SIZE);
                hBox.setPrefHeight(Control.USE_COMPUTED_SIZE);
                gridPane.add(hBox, count % colums, count / colums);

                count ++;
            }

            contentPane.getChildren().add(gridPane);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("drawGrid end.");
        }
    }

    // Date比較用
    private final Comparator<Date> dateComparator = (a, b) -> {
        if (a.before(b)) {
            return -1;
        } else if (a.after(b)) {
            return 1;
        } else {
            return 0;
        }
    };
    // 実績時間順
    private final Comparator<AgendaGroup> actualGroupListComparator = (a, b) -> dateComparator.compare(
            Objects.isNull(a.getEndDate()) ? new Date(Long.MAX_VALUE) : a.getEndDate(),
            Objects.isNull(b.getEndDate()) ? new Date(Long.MAX_VALUE) : b.getEndDate()
    );

    /**
     * ラベル表示の生成
     * @return 
     */
    private Label mekeLabel(String text, double width, double height, double fontSize, String foreColor, String backColor) {
        Label lable = new Label();
        lable.setPrefWidth(width);
        lable.setPrefHeight(height);
        AnchorPane.setTopAnchor(lable, 0.0);
        AnchorPane.setBottomAnchor(lable, 0.0);
        AnchorPane.setLeftAnchor(lable, 0.0);
        AnchorPane.setRightAnchor(lable, 0.0);
        lable.setStyle("-fx-padding:2px; -fx-font-size:" + fontSize+ ";" + "-fx-background-color: " + backColor + ";" + "-fx-text-fill: " + foreColor + ";" + "-fx-border-width: 1;-fx-border-color: black;");
        lable.setAlignment(Pos.CENTER);
        lable.setText(text);
        lable.setWrapText(true); // テキストの折り返し
        return lable;
    }

    /**
     * テキストラベルの生成
     * 
     * @param text 工程名
     * @param width 幅
     * @param height 高さ
     * @param fontSize フォントサイズ
     * @param fontColor フォント色
     * @return Label テキストラベル
     */
    private Label mekeTextLabel(String text, double width, double height, double fontSize, String fontColor) {
        Label textLabel = new Label ();
        textLabel.setText(text);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setTextAlignment(TextAlignment.CENTER);
        textLabel.setPrefSize(width, height);
        textLabel.setStyle("-fx-font-size:" + fontSize + "; -fx-text-fill: " + fontColor + "; -fx-background-color:transparent;");
        textLabel.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        textLabel.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        textLabel.setWrapText(true);
        AnchorPane.setTopAnchor(textLabel, 0.0);
        AnchorPane.setLeftAnchor(textLabel, 0.0);
        AnchorPane.setBottomAnchor(textLabel, 0.0);
        return textLabel;
    }

    /**
     * 作業背景ラベル表示の生成
     * 
     * @param width 幅
     * @param height 高さ
     * @param backColor 背景色
     * @param borderWidth 作業時間がタクトタイムを超過した場合のボーダーラインの幅
     * @param isOverTaktTime 作業時間がタクトタイムを超過しているかの判定
     * @return Label工程背景ラベル
     */
    private Label mekeWorkLabel(double width, double height, String backColor, double borderWidth, boolean isOverTaktTime) {
        Label workLable = new Label();
        workLable.setPrefWidth(width);
        workLable.setPrefHeight(height);
        if (isOverTaktTime) {
            workLable.setStyle("-fx-padding:2px; -fx-background-color: " + backColor + ";" + "-fx-border-width: " + borderWidth + ", " + borderWidth + "; -fx-border-insets: 0, " + borderWidth + "; -fx-border-color: red, black;");
        }else {
            workLable.setStyle("-fx-padding:2px; -fx-background-color: " + backColor + ";" + "-fx-border-width:1; -fx-border-color: black");
        }
        workLable.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        workLable.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        AnchorPane.setTopAnchor(workLable, 0.0);
        AnchorPane.setLeftAnchor(workLable, 0.0);
        AnchorPane.setBottomAnchor(workLable, 0.0);
        return workLable;
    }

    /**
     * 作業進捗ラベル表示の生成
     * 
     * @param label ラベル
     * @param prosess 進捗
     * @param width 幅
     * @param height 高さ
     * @param borderWidth 作業時間がタクトタイムを超過した場合のボーダーラインの幅
     * @param isOverTaktTime 作業時間がタクトタイムを超過しているかの判定
     * @return 工程進捗ラベル
     */
    private Label mekeWorkProgressLabel(Label label, double process, double width, double height, double borderWidth, boolean isOverTaktTime) {
        label.setPrefSize(width * process, height);
        if (isOverTaktTime) {
            label.setStyle("-fx-background-color:#00000066;-fx-border-width: " + borderWidth + ", " + borderWidth + "; -fx-border-insets: 0, " + borderWidth + "; -fx-border-color: red, black;");
        }else {
            label.setStyle("-fx-background-color:#00000066;-fx-border-width:1 0 1 1;-fx-border-color:black;");
        }
        label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        return label;
    }
}
