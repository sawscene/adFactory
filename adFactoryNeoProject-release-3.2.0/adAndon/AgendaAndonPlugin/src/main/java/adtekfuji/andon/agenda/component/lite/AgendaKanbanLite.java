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
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.LightPatternEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.andon.enumerate.ContentTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 計画実績ペインのコントローラー
 *
 * @author ke.yokoi
 */
@FxComponent(id = "AgendaKanbanLite", fxmlPath = "/fxml/andon/agenda/lite/agenda_kanban_lite.fxml")
public class AgendaKanbanLite implements Initializable, ArgumentDelivery, ComponentHandler, AgendaCompoInterface {

    private final Logger logger = LogManager.getLogger();
    private final Properties properties = AdProperty.getProperties();
    private Timeline pageToggleTimeline;
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ConfigData config = ConfigData.getInstance();
    private ResourceBundle rb;
    private final CurrentData currentData = CurrentData.getInstance();
    private final AgendaModel model = AgendaModel.getInstance();
    private final Blinking blinking = Blinking.getInstance();
    private final CallingPool callingPool = CallingPool.getInstance();
    private Optional<DisplayedStatusInfoEntity> callerStatusInfo;

    private Map<Long, Agenda> agendas = new LinkedHashMap<>();
    
    private final String PICK_LITE_WORK_NAME_REGEX_KEY = "PickLiteWorkNameRegex";
    private String pickLiteWorkNameRegex;
 
    private final double WORKFLOW_WIDTH = 200.0;
    private final double KANBAN_WIDTH = 200.0;
    private final double DELIVERY_TIME_WIDTH = 150.0;
    private final double PROGRESS_WIDTH = 100.0;
    private final double HEADDER_HEIGHT = 80.0;
    private final double PLANWORK_WIDTH = 100.0;
    private final double ACTUALWORK_WIDTH = 100.0;

    @FXML
    private AnchorPane contentPane;
    @FXML
    private TableView tableView;
    @FXML
    private TableColumn workflowColumn;
    @FXML
    private TableColumn kanbanColumn;
    @FXML
    private TableColumn deliveryTimeColumn;
    @FXML
    private TableColumn progressColumn;
    @FXML
    private TableColumn processStatusColumn;

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
            SceneContiner sc = SceneContiner.getInstance();
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
     * 呼出し
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
            int num = this.config.getProcessDisplayRows();
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

            // 工程表示のサイズを計算
            int colums = this.config.getProcessDisplayColumns();
            int rows = this.config.getProcessDisplayRows();
            double itemWidth = (contentPane.getPrefWidth() - (WORKFLOW_WIDTH + KANBAN_WIDTH + DELIVERY_TIME_WIDTH + PROGRESS_WIDTH + PLANWORK_WIDTH + ACTUALWORK_WIDTH)) / colums;
            double itemHeight = (contentPane.getPrefHeight() - HEADDER_HEIGHT) / rows;

            GridPane gridPane = new GridPane();
            gridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
            
            // ヘッダー部を組み立てる
            String foreColor = "black";
            String backColor = "lightgray";
            String title1 = rb.getString("key.OrderProcesses");
            if (config.getContentType().equals(ContentTypeEnum.MODEL)) {
                title1 = rb.getString("key.ModelName");
            }
            Label workflowTitle = this.mekeLabel(title1, WORKFLOW_WIDTH, HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);
            Label kanbanTitle = this.mekeLabel(rb.getString("key.Kanban"), KANBAN_WIDTH, HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);
            Label deliveryTimeTitle = mekeLabel(rb.getString("key.DeliveryTime"), DELIVERY_TIME_WIDTH, HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);
            Label progressTitle = this.mekeLabel(rb.getString("key.Progress") + "[%]", PROGRESS_WIDTH, HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);
            Label processStatusTitle = this.mekeLabel(rb.getString("key.ProcessStatus"), (itemWidth * colums), HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);
            Label planWorkTitle = this.mekeLabel(rb.getString("planWork"), 100.0, HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);
            Label actualWorkTitle = this.mekeLabel(rb.getString("actualWork"), 100.0, HEADDER_HEIGHT, config.getColumnFontSize(), foreColor, backColor, Pos.CENTER);

            gridPane.add(workflowTitle, 0, 0);
            gridPane.add(kanbanTitle, 1, 0);
            gridPane.add(deliveryTimeTitle, 2, 0);
            gridPane.add(planWorkTitle, 3, 0);
            gridPane.add(actualWorkTitle, 4, 0);
            gridPane.add(progressTitle, 5, 0);
            gridPane.add(processStatusTitle, 6, 0);

            int count = 1;
            for (Entry<Long, Agenda> entry : partial) {
                Agenda agenda = entry.getValue();

                List<AgendaTopic> planTopics = agenda.getPlans().stream()
                    .map(AgendaPlan::getTopics)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
                List<AgendaGroup> planGroups = AgendaGroup.groupBy(planTopics);

                // 計画の進捗を計算
                int topicNum = 0;
                int compNum = 0;
                for (AgendaGroup group : planGroups) {
                    topicNum = group.getTopics().size();
                    for (AgendaTopic topic : group.getTopics()) {
                        if (topic.getWorkKanbanStatus() == KanbanStatusEnum.COMPLETION) {
                            compNum ++;
                        }
                    }
                }
                String progress = topicNum == 0 ? "-" : String.format("%d", 100 - (int)((double)(topicNum - compNum) / topicNum * 100.0));

                // 納期を取り出す
                String deliveryTime = "";
                List<AddInfoEntity> addInfo = agenda.getKanbanAddInfos();
                if (addInfo != null) {
                    Optional<AddInfoEntity> info = addInfo.stream().filter(s -> s.getKey().equals(rb.getString("key.DeliveryTime"))).findFirst();
                    if (info.isPresent()) {
                        deliveryTime = info.get().getVal();
                    }
                }
                
                // 工程の表示切り替え間隔で、表示する工程を切り替える
                AnchorPane workPane = new AnchorPane();
                Timeline timeline = new Timeline();
                int frameNum = (int)Math.ceil((double)topicNum / colums);
                List<KeyFrame> keyframes = IntStream.range(0, frameNum)
                    .boxed()
                    .map(i -> new KeyFrame(Duration.seconds(i * this.config.getProcessPageSwitchingInterval()), (event) -> {
                        this.drawWork(planGroups, workPane, colums, i, itemWidth, itemHeight);
                    }))
                    .collect(Collectors.toList());
                keyframes.add(new KeyFrame(Duration.seconds(frameNum * this.config.getProcessPageSwitchingInterval()), event -> {
                    this.drawWork(planGroups, workPane, colums, frameNum, itemWidth, itemHeight);
                }));
                timeline.getKeyFrames().addAll(keyframes);
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();

                // 工程順～進捗の表示を組み立てる
                foreColor = agenda.getFontColor();
                backColor = agenda.getBackColor();
                Label workflowLabel = this.mekeLabel(agenda.getTitle2(), WORKFLOW_WIDTH, itemHeight, config.getTitleFontSize(), foreColor, backColor, Pos.CENTER);
                Label kanbanLabel = this.mekeLabel(agenda.getTitle1(), KANBAN_WIDTH, itemHeight, config.getTitleFontSize(), foreColor, backColor, Pos.CENTER);
                Label deliveryTimeLabel = this.mekeLabel(deliveryTime, DELIVERY_TIME_WIDTH, itemHeight, config.getTitleFontSize(), foreColor, backColor, Pos.CENTER);
                Label progressLabel = this.mekeLabel(progress, PROGRESS_WIDTH, itemHeight, config.getTitleFontSize(), foreColor, backColor, Pos.CENTER);

                Label planWorkLabel = this.mekeLabel(StringTime.convertMillisToStringTime(agenda.getPlanWork()), 100.0, itemHeight, config.getTitleFontSize(), foreColor, backColor, Pos.CENTER);
                Label actualWorkLabel = this.mekeLabel(StringTime.convertMillisToStringTime(agenda.getActualWork()), 100.0, itemHeight, config.getTitleFontSize(), foreColor, backColor, Pos.CENTER);

                gridPane.add(workflowLabel, 0, count);
                gridPane.add(kanbanLabel, 1, count);
                gridPane.add(deliveryTimeLabel, 2, count);
                gridPane.add(planWorkLabel, 3, count);
                gridPane.add(actualWorkLabel, 4, count);
                gridPane.add(progressLabel, 5, count);
                gridPane.add(workPane, 6, count);

                count ++;
            }

            contentPane.getChildren().add(gridPane);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("drawGrid end.");
        }
    }
    
    /**
     * ラベル表示の生成
     * @return 
     */
    private Label mekeLabel(String text, double width, double height, double fontSize, String foreColor, String backColor, Pos textAlign) {
        Label lable = new Label();
        lable.setPrefWidth(width);
        lable.setPrefHeight(height);
        AnchorPane.setTopAnchor(lable, 0.0);
        AnchorPane.setBottomAnchor(lable, 0.0);
        AnchorPane.setLeftAnchor(lable, 0.0);
        AnchorPane.setRightAnchor(lable, 0.0);
        lable.setStyle("-fx-padding:2px; -fx-font-size:" + fontSize+ ";" + "-fx-background-color: " + backColor + ";" + "-fx-text-fill: " + foreColor + ";" + "-fx-border-width: 1;-fx-border-color: black;");
        lable.setAlignment(textAlign);
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
     * @param translateX X座標
     * @return Label テキストラベル
     */
    private Label mekeTextLabel(String text, double width, double height, double fontSize, String fontColor, double translateX) {
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
        AnchorPane.setLeftAnchor(textLabel, translateX);
        AnchorPane.setBottomAnchor(textLabel, 0.0);
        return textLabel;
    }

    /**
     * 工程背景ラベル表示の生成
     * 
     * @param width 幅
     * @param height 高さ
     * @param backColor 背景色
     * @param translateX X座標
     * @return Label工程背景ラベル
     */
    private Label mekeWorkLabel(double width, double height, String backColor, double translateX) {
        Label workLable = new Label();
        workLable.setPrefWidth(width);
        workLable.setPrefHeight(height);
        workLable.setStyle("-fx-padding:2px; -fx-background-color: " + backColor + ";" + "-fx-border-width: 1;-fx-border-color: black;");
        workLable.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        workLable.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        AnchorPane.setTopAnchor(workLable, 0.0);
        AnchorPane.setLeftAnchor(workLable, translateX);
        AnchorPane.setBottomAnchor(workLable, 0.0);
        return workLable;
    }

    /**
     * 工程進捗ラベル表示の生成
     * 
     * @param label ラベル
     * @param prosess 進捗
     * @param width 幅
     * @param height 高さ
     * @param translateX X座標
     * @return 工程進捗ラベル
     */
    private Label mekeWorkProgressLabel(Label label, double process, double width, double height, double translateX) {
        label.setPrefSize(width * process, height);
        label.setStyle("-fx-background-color:#00000066;-fx-border-width:1 0 1 1;-fx-border-color:black;");
        label.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        label.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, translateX);
        AnchorPane.setBottomAnchor(label, 0.0);
        return label;
    }
 
    /**
     * 工程部分の表示
     * @param planGroups
     * @return 
     */
    private void drawWork(List<AgendaGroup> planGroups, AnchorPane pane, int colums, int pos, double itemWidth, double itemHeight) {
        List workLabels = new LinkedList<Label>();
        pane.getChildren().clear();
        Double translateX = 0.0;
        for (AgendaGroup group : planGroups) {
            for (int loop = pos * colums; loop < group.getTopics().size() && loop < (pos * colums + colums); loop++ ) {
                AgendaTopic topic = group.getTopics().get(loop);
                //工程名だけ抽出
                String workName = topic.getTitle1();
                String fontColor = topic.getFontColor();
                String backColor = topic.getBackColor();
                try
                {
                    Matcher m = Pattern.compile(this.pickLiteWorkNameRegex).matcher(workName);
                    if (m.find()) {
                        workName = m.group(1);
                    }
                } catch(Exception ex) {
                }
                
                if (!StringUtils.isEmpty(topic.getTitle2())) {
                    workName += "\n" + topic.getTitle2();
                }
                
                // 呼出し
                boolean isBlinking = false;
                if (callingPool.isOrganizationCall(topic.getOrganizationId())) {
                    if (callerStatusInfo.isPresent()) {
                        DisplayedStatusInfoEntity statusInfo = callerStatusInfo.get();
                        isBlinking = statusInfo.getLightPattern() == LightPatternEnum.BLINK ? true : false;
                        fontColor = statusInfo.getFontColor();
                        backColor = statusInfo.getBackColor();
                    } else {
                        isBlinking = true;
                        fontColor = "black";
                        backColor = "red";
                    }
                }

                if (Boolean.valueOf(properties.getProperty(Constants.ENABLE_LITE_TAKT_TIME, Constants.ENABLE_LITE_TAKT_TIME_DEFAULT))) {
                    // テキストラベル
                    Label textLabel = this.mekeTextLabel(workName, itemWidth, itemHeight, config.getItemFontSize(), fontColor, translateX);
                    // 工程背景ラベル
                    Label worklabel = this.mekeWorkLabel(itemWidth, itemHeight, backColor, translateX);
                    // 工程進捗ラベル
                    Label workProgressLabel = new Label();
                    if (Objects.nonNull(topic.getProgress())) {
                        workProgressLabel = this.mekeWorkProgressLabel(workProgressLabel, topic.getProgress(), itemWidth, itemHeight, translateX);
                    }
                    // 点滅
                    if (topic.isBlink() || isBlinking) {
                        blinking.play(textLabel);
                        blinking.play(worklabel);
                        blinking.play(workProgressLabel);
                    }
                    pane.getChildren().add(worklabel);
                    pane.getChildren().add(workProgressLabel);
                    pane.getChildren().add(textLabel);
                    // 次工程の出力位置（X座標）を加算
                    translateX = translateX + itemWidth;
                } else {
                    // ラベル
                    Label label = this.mekeLabel(workName, itemWidth, itemHeight, config.getItemFontSize(), fontColor, backColor, Pos.CENTER);
                    // 点滅
                    if (topic.isBlink() || isBlinking) {
                        blinking.play(label);
                    }
                    workLabels.add(label);
                }
            }
        }
        if (0 < workLabels.size()) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(workLabels);
            hBox.setPrefWidth(Control.USE_COMPUTED_SIZE);
            hBox.setPrefHeight(Control.USE_COMPUTED_SIZE);
            pane.getChildren().add(hBox);
        }
    }
}
