/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.schedule.cell;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanScheduleTypeEnum;
import adtekfuji.admanagerapp.productionnaviplugin.common.WorkPlanSelectedKanbanAndHierarchy;
import adtekfuji.admanagerapp.productionnaviplugin.common.agenda.WorkPlanCustomAgendaItemEntity;
import adtekfuji.admanagerapp.productionnaviplugin.utils.scheduling.AutomaticScheduling;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Properties;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程の計画と実績の注入
 *
 * @author (TST)min
 * @version 1.8.3
 * @since 2018/09/28
 */
public class WorkPlanScheduleCell extends Label {

    private final SceneContiner sc = SceneContiner.getInstance();
    private final Properties properties = AdProperty.getProperties();
    
    private final Logger logger = LogManager.getLogger();
    
    private final SimpleDateFormat dateDataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final String TAG_NEW_LINE = "\r\n";
//    private final String TAG_NEW_LINE = "<br />";
    
    private WorkPlanCustomAgendaItemEntity item;
    
    private double movedBefore = -1;
    
    private double startPointX = -1;
    private double nowPointX = -1;

    private boolean isDragged = false;
    
    private AutomaticScheduling automaticScheduling;
    /**
     * コンストラクタ
     * 
     * @param item
     * @param width
     * @param hight
     * @param typeEnum
     * @param scheduleType 
     */
    public WorkPlanScheduleCell(WorkPlanCustomAgendaItemEntity item, double width, double hight, WorkPlanScheduleRecordTypeEnum typeEnum, WorkPlanScheduleTypeEnum scheduleType) {
        logger.debug("◆工程の作成");
        logger.trace("　　ラベルサイズ:" + width + " x " + hight);
        logger.trace("　　タイトル:" + item.getTitle1());
        logger.trace("　　タイトル:" + item.getTitle2());
        logger.trace("　　タイトル:" + item.getTitle3());
        logger.trace("　　タイトル:" + item.getTitle4());
        logger.trace("　　タイトル:" + item.getTitle5());
        
        //ラベル作成
        this.setText(item.getTitle3());
        if((Objects.nonNull(item.getWorkName()) && !item.getWorkName().isEmpty())){
            this.setText(item.getWorkName());
        }
        this.setStyle("-fx-font-size:" + 10 + ";" + "-fx-text-fill: " + item.getFontColor()
                + ";" + "-fx-background-color:" + item.getBackgraundColor()+ "; -fx-border-width: 0.3; -fx-border-color: black;");
        this.setAlignment(Pos.CENTER);
        this.setPrefSize(width, hight);
        this.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        this.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        
        // ツールチップ
        StringBuilder text = new StringBuilder();
//        text.append("<h3>");
        text.append(item.getTitle1());
//        text.append("<h5>");
        if((Objects.nonNull(item.getTitle2()) && !item.getTitle2().isEmpty())){
            text.append(TAG_NEW_LINE).append(item.getTitle2());
//            text.append(item.getTitle2());
        }
        if((Objects.nonNull(item.getTitle3()) && !item.getTitle3().isEmpty())){
            text.append(TAG_NEW_LINE).append(item.getTitle3());
        }
        text.append(TAG_NEW_LINE).append(dateDataFormat.format(item.getStartTime()))
                .append("-").append(dateDataFormat.format(item.getEndTime()));
        if((Objects.nonNull(item.getTitle4()) && !item.getTitle4().isEmpty())){
            text.append(TAG_NEW_LINE).append(item.getTitle4());
        }
        
        if((Objects.nonNull(item.getTitle5()) && !item.getTitle5().isEmpty())){
            text.append(TAG_NEW_LINE).append(item.getTitle5());
        }
//        text.append("</h5>");
//        text.append("</h3>");
//        
//        WebView webView = new WebView();
//        WebEngine webEngine = webView.getEngine();
//        webEngine.loadContent(text.toString());
        
        try {
            Tooltip toolTip = TooltipBuilder.build(this);
            toolTip.setText(text.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
//        toolTip.setPrefSize(300, 180);
//        toolTip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//        toolTip.setGraphic(webView);
       
        
        // 計画編集処理
//        if (typeEnum.equals(ScheduleRecordTypeEnum.PLAN_RECORD) && scheduleType.equals(ScheduleTypeEnum.ORGANIZATION_SCHEDULE)) {
        if (typeEnum.equals(WorkPlanScheduleRecordTypeEnum.PLAN_RECORD)) {
            // ダブルクリック（編集画面）, 作業者計画画面
            if(scheduleType.equals(WorkPlanScheduleTypeEnum.ORGANIZATION_WP_SCHEDULE)) {
                setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(Objects.nonNull(item.getKanbanId())) {
                            switch(event.getClickCount()) {
                                case 2: //カンバン編集
                                    WorkPlanSelectedKanbanAndHierarchy selected = new WorkPlanSelectedKanbanAndHierarchy(item.getKanbanId(), scheduleType);
                                    sc.setComponent("ContentNaviPane", "WorkPlanDetailCompo", selected);
                                    break;
                                default : break;
                            }
                        }
                    }
                });
            }
            
            // 工程の移動
            if(!item.getWorkKanbanStatus().equals(KanbanStatusEnum.COMPLETION)) {
                // 移動準備
                setOnMouseEntered((MouseEvent entered) -> {
                    // CURSOR TYPE
                    sc.getStage().getScene().setCursor(Cursor.OPEN_HAND);
                });

                // 移動開始
                setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent moveStart) {
                        // CURSOR TYPE
                        sc.getStage().getScene().setCursor(Cursor.CLOSED_HAND);

                        // 開始点保存
                        startPointX = getTranslateX();

//                         this.setMouseTransparent(true);
                        moveStart.consume();
                    }
                });

                // マウスを押しながら(annimation event)
                setOnMouseDragged((MouseEvent dragged) -> {
                    if(movedBefore != -1) {
                        nowPointX = nowPointX + (dragged.getSceneX() - movedBefore);
                    } else {
                        nowPointX = startPointX;
                        
                        this.toFront();
                        isDragged = true;
                        properties.setProperty(ProductionNaviPropertyConstants.IS_DRAGGED, "true");
                    }
                    setTranslateX(nowPointX);
                    movedBefore = dragged.getSceneX();
                });
                
                // 移動終了
                setOnMouseReleased((MouseEvent moveStop) -> {
                    if(!isDragged) {
                        return;
                    }

                    // CURSOR TYPE
                    sc.getStage().getScene().setCursor(Cursor.OPEN_HAND);

                    // 工程の時間変更処理
                    double movedLength = getTranslateX() - startPointX;
                    automaticScheduling = new AutomaticScheduling(item.getKanbanId());
                    if(scheduleType.equals(WorkPlanScheduleTypeEnum.KANBAN_WP_SCHEDULE)) {
                        properties.setProperty(ProductionNaviPropertyConstants.DRAGGED_ID, String.valueOf(item.getKanbanId()));
                    } else {
                        properties.setProperty(ProductionNaviPropertyConstants.DRAGGED_ID, String.valueOf(item.getOrganizationId()));
                    }
                    
                    try {
                        automaticScheduling.dragTime(item.getWorkKanbanId(), width, movedLength, getSelectWpFollow(scheduleType));
                    } catch (Exception ex) {
                        logger.fatal(ex);
                    }

                    // 初期化
                    isDragged = false;
                    startPointX = -1;
                    movedBefore = -1;
                    sc.getStage().getScene().setCursor(Cursor.DEFAULT);

//                      this.setMouseTransparent(false);
//                      moveStop.consume();
                });

                // 準備終了
                setOnMouseExited((MouseEvent exited)->{
                    // CURSOR TYPE
                    sc.getStage().getScene().setCursor(Cursor.DEFAULT);
                });
            }
        }
    }
    
    
    public WorkPlanCustomAgendaItemEntity getItem() {
        return item;
    }

    public void setItem(WorkPlanCustomAgendaItemEntity item) {
        this.item = item;
    }

    private boolean getSelectWpFollow(WorkPlanScheduleTypeEnum workPlanScheduleType) {
        try{
            // 条件をプロパティファイルに保存
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties prop = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            
            // 工程の追従
            String propName = ProductionNaviPropertyConstants.SELECT_WP_FOLLOWING;
            if(workPlanScheduleType.equals(WorkPlanScheduleTypeEnum.ORGANIZATION_WP_SCHEDULE)) {
                propName = ProductionNaviPropertyConstants.SELECT_WORKER_FOLLOWING;
            }
            String mode = prop.getProperty(propName, "trackingOFF");
            if(Objects.nonNull(mode) ){
                if(mode.equals("trackingON")){
                    return true;
                }else{
                    return false;
                }
            }
        } catch(IOException ex) {
            logger.fatal(ex);
        }
        return false;
    }
}