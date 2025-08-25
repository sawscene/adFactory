/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.component;

import adtekfuji.andon.agenda.common.AgendaCompoInterface;
import adtekfuji.andon.agenda.common.KanbanStatusConfig;
import adtekfuji.andon.agenda.enumerate.PayoutOrderDisplayGroupEnum;
import adtekfuji.andon.agenda.model.AgendaPayoutModel;
import adtekfuji.andon.agenda.model.data.*;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.util.Duration;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryInfo;
import jp.adtekfuji.adFactory.entity.warehouse.TrnDeliveryItemInfo;
import jp.adtekfuji.adFactory.enumerate.DeliveryStatusEnum;
import jp.adtekfuji.adFactory.enumerate.StatusPatternEnum;
import jp.adtekfuji.javafxcommon.controls.TooltipBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 払出モニターのコントローラー
 * 
 * @author okada
 */
@FxComponent(id = "AgendaPayoutStatusCompo", fxmlPath = "/fxml/compo/agenda_payout_status_compo.fxml")
public class AgendaPayoutStatusCompoController implements Initializable, ArgumentDelivery, ComponentHandler, AgendaCompoInterface {

    private final Logger logger = LogManager.getLogger();

    /** 払出指示ラベルの通常時のスタイル */
    private final String PAYOUT_ORDER_LINE_NORMAL    = "-fx-font-size: %1$s; -fx-text-fill: BLACK; -fx-background-color: gray;   -fx-border-width: 0.0; -fx-border-color: gray;";
    /** 払出指示ラベルの注意時のスタイル */
    private final String PAYOUT_ORDER_LINE_ATTENTION = "-fx-font-size: %1$s; -fx-text-fill: BLACK; -fx-background-color: orange; -fx-border-width: 0.0; -fx-border-color: orange;";
    /** 払出指示ラベルの進捗遅れ時のスタイル */
    private final String PAYOUT_ORDER_LINE_DELAY     = "-fx-font-size: %1$s; -fx-text-fill: %2$s;  -fx-background-color: %3$s;   -fx-border-width: 0.0; -fx-border-color: %3$s;";

    private final ConfigData config = ConfigData.getInstance();
    private ResourceBundle rb;
    private final CurrentData currentData = CurrentData.getInstance();
    /** アジェンダモデル(払出状況) */
    private final AgendaPayoutModel model = AgendaPayoutModel.getInstance();
    /** 払出指示情報 */
    private Map<PayoutOrderDisplayGroupEnum, List<TrnDeliveryInfo>> agendas = new LinkedHashMap<>();

    /** タイマ処理(ページ切り替え間隔(秒)毎の処理)のコントローラー */
    private Timeline pageToggleTimeline;
    /** スクロールペイン */
    @FXML
    private ScrollPane contentScrollPane;
    /** 画面全体ペイン(縦軸ペイン、横軸ペインを含む) */
    @FXML
    private VBox contentPane;
    /** 払出完了ラベル */
    @FXML
    private Label payoutCompleteLabel;
    /** 払出完了の払出指示ペイン */
    @FXML
    private TilePane payoutCompletePane;
    /** 払出待ちラベル */
    @FXML
    private Label payoutWaitingLabel;
    /** 払出待ちの払出指示ペイン */
    @FXML
    private TilePane payoutWaitingPane;
    /** ピッキング中ラベル */
    @FXML
    private Label pickingLabel;
    /** ピッキング中の払出指示ペイン */
    @FXML
    private TilePane pickingPane;
    /** 受付ラベル */
    @FXML
    private Label receptionLabel;
    /** 受付の払出指示ペイン */
    @FXML
    private TilePane receptionPane;

    /** 開始時間 */
    private Date startTime;
    /** 終了時間 */
    private Date endTime;
    
    /** 払出指示一覧の横幅 */
    private double payoutOrderListWidth;
    /** 払出指示一覧の一行の縦幅 */
    private double payoutOrderListHeight;
    /** 払出指示一覧の列数 */
    private int payoutOrderColNum;
    /** 払出指示の横幅 */
    private double payoutOrderWidth;
               
    /** 払出指示のベース横幅 */
    private final double PAYOUT_ORDER_WIDTH_BASE = 285;
    /** 払出指示のラインの縦幅 */
    private final double PAYOUT_ORDER_HEIGHT = 30;
    /** 払出指示一覧の水平・垂直方向のスペース幅 */
    private final double PAYOUT_ORDER_LIST_SPACE_WIDTH = 15;
    /** 画面の左側のスペース幅(fxmlで修正した場合はこちらも修正する事) */
    private final double PANE_LEFT_ANCHOR = 40;

    int maxPage;
    int completedPage;
    int waitPage;
    int pickingPage;
    int receptionPage;
                
    /**
     * コントローラクラスを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            logger.info("initialize start.");

            this.rb = rb;

            // ウィンドウをディスプレイの解像度に合わせる
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            logger.debug(String.format("Screen: Width=%1$f Height=%2$f", visualBounds.getWidth(), visualBounds.getHeight()));

            // 払出指示一覧の横幅を取得(小数第2位で切り捨て)
            this.payoutOrderListWidth = visualBounds.getWidth() - (this.PANE_LEFT_ANCHOR * 2);
            this.payoutOrderListWidth = new BigDecimal(this.payoutOrderListWidth).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
            
            // 払出指示一覧の列数を取得(小数第1位で切り捨て)
            this.payoutOrderColNum = new BigDecimal(payoutOrderListWidth / this.PAYOUT_ORDER_WIDTH_BASE).setScale(0, BigDecimal.ROUND_DOWN).intValue();
            
            // 払出指示一覧の一行の縦幅を取得
            this.payoutOrderListHeight = (this.PAYOUT_ORDER_HEIGHT * 2) + this.PAYOUT_ORDER_LIST_SPACE_WIDTH;
            
            // 払出指示の横幅を取得
            this.payoutOrderWidth = (this.payoutOrderListWidth / this.payoutOrderColNum) - this.PAYOUT_ORDER_LIST_SPACE_WIDTH;
            
            logger.debug(String.format("contentPane: 払出指示一覧の横幅=%1$f 払出指示一覧の列数=%2$d 払出指示の横幅=%3$f", 
                                       this.payoutOrderListWidth, this.payoutOrderColNum, this.payoutOrderWidth));
            
            // スクロールペイの設定
            this.contentPane.setPrefWidth(this.payoutOrderListWidth);
            this.contentPane.setFillWidth(true);
            this.contentScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
            this.contentScrollPane.setPrefWidth(this.payoutOrderListWidth + 30);
            
            // 払出完了の払出指示一覧
            setPayoutOrderListProperty(this.payoutCompletePane, this.config.getPayoutCompleteLineCount());
            // 払出待ちの払出指示一覧
            setPayoutOrderListProperty(this.payoutWaitingPane, this.config.getPayoutWaitingLineCount());
            // ピッキング中の払出指示一覧
            setPayoutOrderListProperty(this.pickingPane, this.config.getPickingLineCount());
            // 受付の払出指示一覧
            setPayoutOrderListProperty(this.receptionPane, this.config.getReceptionLineCount());

            this.startTime = this.config.getStartTime();
            this.endTime = this.config.getEndTime();

            // 開始日付を設定？
            this.currentData.setFromDate(this.currentData.getKeepTargetDay());
            // タイマ処理の初期化
            if (Objects.nonNull(this.pageToggleTimeline)) {
                this.pageToggleTimeline.stop();
                this.pageToggleTimeline.getKeyFrames().clear();
            }
            // コンテンツを描画する。
            draw(false);

            // アジェンダモデルに自身のコントローラーを渡す。
            model.setController(this);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("initialize end.");
        }
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
        logger.info("destroyComponent end.");
        return true;
    }

    /**
     * 更新間隔時間による画面表示の更新
     * 
     */
    @Override
    public void updateDisplay() {
        logger.info("updateDisplay start.");
        updateDisplay(false);
        logger.info("updateDisplay end.");
    }

    /**
     * 更新間隔時間による画面表示の更新
     * 
     * @param isUpdateTimeDrawing 更新時間での描画フラグ
     */
    @Override
    public void updateDisplay(boolean isUpdateTimeDrawing) {
        logger.info(String.format("updateDisplay start.  isUpdateTimeDrawing=%1$s", isUpdateTimeDrawing));
        
        try {
            agendas = currentData.getDeliveryInfos();
            currentData.setFromDate(currentData.getKeepTargetDay());            
            draw(isUpdateTimeDrawing);

        } catch (Exception ex) {
            logger.fatal(ex, ex);

        } finally {
            logger.info("updateDisplay end.");
        }
    }
    
    /**
     * 払出指示一覧コントロールのプロパティ設定。
     * 
     * @param pane 各グループの払出指示一覧コントロール
     * @param payoutOrderRowNum 各グループの行数
     */
    private void setPayoutOrderListProperty(TilePane pane, int payoutOrderRowNum) {
        pane.setPrefWidth(this.payoutOrderListWidth);
        pane.setPrefHeight(this.payoutOrderListHeight * payoutOrderRowNum);
        pane.setHgap(this.PAYOUT_ORDER_LIST_SPACE_WIDTH);       // 行内の各タイル間の水平方向のスペース幅
        pane.setVgap(this.PAYOUT_ORDER_LIST_SPACE_WIDTH);       // 列内の各タイル間の垂直方向のスペース幅
        pane.setPrefColumns(this.payoutOrderColNum);            // 列数を設定
        pane.setPrefRows(payoutOrderRowNum);                    // 行数を設定
    }

    private boolean isDateUpdate = false; 

    /**
     * コンテンツを描画する。
     *
     * @param isUpdateTimeDrawing 更新時間での描画フラグ
     */
    private void draw(boolean isUpdateTimeDrawing) {
        try {
            logger.debug(String.format("draw start.  isUpdateTimeDrawing=%1$s", isUpdateTimeDrawing));
            
            if (Objects.isNull(this.agendas) || this.agendas.size() <= 0) {
                return;
            }
            
            /**
             * 本メソッドが処理される場合、新しい払出指示情報がメンバー変数の保存済みとなるので、
             * タイマ処理で本メソッドを処理すると、新しい払出指示情報で画面描画か行えます。
             */

            // タイマ処理(ページ切り替え間隔(秒)毎の処理)が実行中状態の場合、画面描画はタイマ処理で行う。
            this.isDateUpdate = false;
            if (isUpdateTimeDrawing && 
                    Objects.nonNull(this.pageToggleTimeline) && this.pageToggleTimeline.getStatus() == Animation.Status.RUNNING) {
                this.isDateUpdate = true;
                logger.debug("draw end. 再描画なし");
                return;
            }

            // 払出完了
            this.payoutCompleteLabel.setVisible(this.config.getPayoutCompleteLineCount() > 0);
            this.payoutCompleteLabel.setManaged(this.payoutCompleteLabel.isVisible());
            List<List<TrnDeliveryInfo>> completedPages = this.splitPage(this.agendas.get(PayoutOrderDisplayGroupEnum.PAYOUT_COMPLETE), this.payoutOrderColNum * this.config.getPayoutCompleteLineCount());
            this.maxPage = completedPages.size();

            // 払出待ち
            this.payoutWaitingLabel.setVisible(this.config.getPayoutWaitingLineCount() > 0);
            this.payoutWaitingLabel.setManaged(this.payoutWaitingLabel.isVisible());
            List<List<TrnDeliveryInfo>> waitPages = this.splitPage(this.agendas.get(PayoutOrderDisplayGroupEnum.PAYOUT_WAITING), this.payoutOrderColNum * this.config.getPayoutWaitingLineCount());
            this.maxPage = Math.max(this.maxPage, waitPages.size());
            
            // ピッキング中
            this.pickingLabel.setVisible(this.config.getPickingLineCount() > 0);
            this.pickingLabel.setManaged(this.pickingLabel.isVisible());
            List<List<TrnDeliveryInfo>> pickingPages = this.splitPage(this.agendas.get(PayoutOrderDisplayGroupEnum.PICKING), this.payoutOrderColNum * this.config.getPickingLineCount());
            this.maxPage = Math.max(this.maxPage, pickingPages.size());

            // 受付
            this.receptionLabel.setVisible(this.config.getReceptionLineCount() > 0);
            this.receptionLabel.setManaged(this.receptionLabel.isVisible());
            List<List<TrnDeliveryInfo>> receptionPages = this.splitPage(this.agendas.get(PayoutOrderDisplayGroupEnum.RECEPTION), this.payoutOrderColNum * this.config.getReceptionLineCount());
            this.maxPage = Math.max(this.maxPage, receptionPages.size());
            
            if (this.maxPage > 1) {
                /**
                 * ページ切り替え
                 * 下記のタイマ処理では、1回目の処理が設定している経過時間となる為、
                 * その期間画面に何も表示されない状態が続いてしまいます。
                 * 対応として、1回目の処理で2頁目を表示する様にし、
                 * タイマ処理設定時に、1頁目を表示する様にしています。
                 */
                
                this.completedPage = 1;
                this.waitPage = 1;
                this.pickingPage = 1;
                this.receptionPage = 1;
                
                KeyFrame keyframes = new KeyFrame(Duration.seconds(config.getPagingIntervalSeconds()), e -> {

                    this.completedPage = completedPages.size() <= this.completedPage ?  1 : this.completedPage + 1;
                    this.waitPage = waitPages.size() <= this.waitPage ?  1 : this.waitPage + 1;
                    this.pickingPage = pickingPages.size() <= this.pickingPage ?  1 : this.pickingPage + 1;
                    this.receptionPage = receptionPages.size() <= this.receptionPage ?  1 : this.receptionPage + 1;

                    if (this.maxPage == this.completedPage || this.maxPage == this.waitPage
                            || this.maxPage == this.pickingPage || this.maxPage == this.receptionPage) {
                        if (this.isDateUpdate) {
                            this.isDateUpdate = false;
                            if (Objects.nonNull(pageToggleTimeline)) {
                                pageToggleTimeline.stop();
                                pageToggleTimeline.getKeyFrames().clear();
                            }
                            draw(false);
                            return;
                        }
                    }
                   
                    drawPane(completedPages.isEmpty() ? null : completedPages.get(this.completedPage - 1), 
                            waitPages.isEmpty() ? null : waitPages.get(this.waitPage - 1), 
                            pickingPages.isEmpty() ? null : pickingPages.get(this.pickingPage - 1), 
                            receptionPages.isEmpty() ? null : receptionPages.get(this.receptionPage - 1));
                });

                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }

                pageToggleTimeline = new Timeline();
                pageToggleTimeline.getKeyFrames().addAll(keyframes);
                pageToggleTimeline.setCycleCount(Timeline.INDEFINITE);
                pageToggleTimeline.play();

                this.drawPane(completedPages.isEmpty() ? null : completedPages.get(0), 
                        waitPages.isEmpty() ? null : waitPages.get(0), 
                        pickingPages.isEmpty() ? null : pickingPages.get(0), 
                        receptionPages.isEmpty() ? null : receptionPages.get(0));
                
            } else if (maxPage == 1) {
                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }

                this.drawPane(completedPages.isEmpty() ? null : completedPages.get(0), 
                        waitPages.isEmpty() ? null : waitPages.get(0), 
                        pickingPages.isEmpty() ? null : pickingPages.get(0), 
                        receptionPages.isEmpty() ? null : receptionPages.get(0));

            } else {
                if (Objects.nonNull(pageToggleTimeline)) {
                    pageToggleTimeline.stop();
                    pageToggleTimeline.getKeyFrames().clear();
                }
                
                this.drawPane(null, null, null, null);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.debug("draw end.");
        }
    }
    
    /**
     * グループ別指示情報をページ別に分割。
     *   １ページに表示する指示情報数が 0 件の場合、0件を
     * 
     * @param deliveryInfos グループ別指示情報
     * @param pageItemCount １ページに表示する指示情報数
     * @return ページ別の指示情報(１ページに表示する指示情報数が０件の場合空を返す)
     */
    private List<List<TrnDeliveryInfo>> splitPage(List<TrnDeliveryInfo> deliveryList, int pageItemCount) {

        if (pageItemCount == 0) {
            return new ArrayList<>(); 
        }
        
        // 払出ステータス、払出予定日、ユニット番号、製番で並び替え
        List<TrnDeliveryInfo> _deliveryList = deliveryList.stream()
            .sorted(TrnDeliveryInfo.statusComparator
                .thenComparing(TrnDeliveryInfo.dueDateComparator)
                .thenComparing(TrnDeliveryInfo.unitNoComparator)
                .thenComparing(TrnDeliveryInfo.orderNoComparator))
            .collect(Collectors.toList());

        IntStream indices = _deliveryList.size() % pageItemCount == 0
                ? IntStream.range(0, _deliveryList.size() / pageItemCount)
                : IntStream.rangeClosed(0, _deliveryList.size() / pageItemCount);

        List<List<TrnDeliveryInfo>> list = indices
                .mapToObj(i -> _deliveryList.stream()
                        .limit(i * pageItemCount + pageItemCount)
                        .skip(i * pageItemCount)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        
        return list;
    }
    
    /**
     * 払出指示を描写する。
     *
     * @param payoutOrderInfos 払出指示情報
     */
    private void drawPane(List<TrnDeliveryInfo> completedPage, List<TrnDeliveryInfo> waitPage, List<TrnDeliveryInfo> pickingPage, List<TrnDeliveryInfo> receptionPage) {
        try {
            // 画面表示なしの場合、描画更新は行なわない。
            if (!KanbanStatusConfig.getEnableView()) {
                return;
            }

            // 描画停止状態の時は描画処理を行なわない。
            if (!model.isUpdate()) {
                return;
            }
            
            // 払出完了
            this.payoutCompletePane.getChildren().clear();
            if (Objects.nonNull(completedPage)) {
                this.payoutCompletePane.getChildren().addAll(createPayoutOrderList(completedPage));
            }
            
            // 払出待ち
            this.payoutWaitingPane.getChildren().clear();
            if (Objects.nonNull(waitPage)) {
                this.payoutWaitingPane.getChildren().addAll(createPayoutOrderList(waitPage)); 
            }

            // ピッキング中
            this.pickingPane.getChildren().clear();
            if (Objects.nonNull(pickingPage)) {
                this.pickingPane.getChildren().addAll(createPayoutOrderList(pickingPage));    
            }
            
            // 受付
            this.receptionPane.getChildren().clear();
            if (Objects.nonNull(receptionPage)) {
                this.receptionPane.getChildren().addAll(createPayoutOrderList(receptionPage));
            }
            
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * 払出指示一覧を作成する。
     * 
     * @param deliveryInfos 払出指示情報
     * @return 払出指示ペイン
     */
    private List<VBox> createPayoutOrderList(List<TrnDeliveryInfo> deliveryInfos) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        List<VBox> payoutOrderListPane = new ArrayList<>();
        StringBuilder topMsg;
        StringBuilder topTooltipMsg;
        StringBuilder bottomMsg;
        StringBuilder bottomTooltipMsg;
        StringBuilder stockoutMsg;
        StringBuilder stockoutTooltipMsg;
        String tmp;
        String style;
        boolean stockFlag;
        
        //List<TrnDeliveryInfo> trnDeliveryInfos = deliveryInfos;
        
        // 製番の絞り込み
        //if (!StringUtils.isEmpty(currentData.getOrderNo())) {
        //    trnDeliveryInfos = deliveryInfos.stream()
        //            .filter(o -> !StringUtils.isEmpty(o.getOrderNo()) && o.getOrderNo().contains(currentData.getOrderNo()))
        //            .collect(Collectors.toList());        
        //}

        for (TrnDeliveryInfo receptionInfo : deliveryInfos) {
            // ----- 上段部分を作成 -----
            topMsg = new StringBuilder();
            topTooltipMsg = new StringBuilder();

            // ユニットNo
            tmp = Objects.nonNull(receptionInfo.getUnitNo()) ? receptionInfo.getUnitNo(): "";
            topMsg.append("  ").append(tmp);
            topTooltipMsg.append(tmp);

            // モデル名
            //tmp = Objects.nonNull(receptionInfo.getModelName()) ? receptionInfo.getModelName(): "";
            //topMsg.append(tmp);
            //topTooltipMsg.append(tmp);
            
            // 製番
            //if (StringUtils.isEmpty(receptionInfo.getSerialStart())) {
            //    // 開始製番=NULL：非表示
            //    topTooltipMsg.append("\n");
            //
            //} else if (StringUtils.isEmpty(receptionInfo.getSerialEnd()) || receptionInfo.getSerialStart().equals(receptionInfo.getSerialEnd())) {
            //    // 製番が単一：開始製番=終了製番 or 終了製番=NULL：開始製番を表示
            //    topMsg.append("  ").append(receptionInfo.getSerialStart());
            //    topTooltipMsg.append("\n").append(receptionInfo.getSerialStart());
            //
            //} else {
            //    // 製番が複数：開始製番<>終了製番：開始製番-終了製番を表示
            //    topMsg.append("  ").append(receptionInfo.getSerialStart()).append("-").append(receptionInfo.getSerialEnd());
            //    topTooltipMsg.append("\n").append(receptionInfo.getSerialStart()).append("-").append(receptionInfo.getSerialEnd());
            //}
            String orderNo = receptionInfo.getOrderNo();
            topMsg.append("  ").append(orderNo);
            topTooltipMsg.append("\n").append(orderNo);

            // ラベルを作成
            Label topLabel = createPayoutOrderLabel(1);
            topLabel.setText(topMsg.toString());

            // 納期
            if (Objects.nonNull(receptionInfo.getDueDate())) {
                topTooltipMsg.append("\n").append(dateFormat.format(receptionInfo.getDueDate()));
            }

            // ツールチップを追加
            this.buildTooltip(topLabel, topTooltipMsg.toString());

            // ----- 下段部分を作成 -----
            bottomMsg = new StringBuilder();
            bottomTooltipMsg = new StringBuilder();
            long totalPickingNum = 0;
            long totalPartsNum = 0;
            
            if (Objects.nonNull(receptionInfo.getDeliveryList())) {
                List<TrnDeliveryItemInfo> deliveryItemInfos = receptionInfo.getDeliveryList().stream()
                        .filter(info -> Objects.isNull(info.getArrange()) || info.getArrange() != 2)
                        .collect(Collectors.toList());

                // ピッキング数
                //totalPickingNum = deliveryItemInfos.stream().mapToInt(val -> val.getDeliveryNum()).sum(); // 部品数で算出
                totalPickingNum = deliveryItemInfos.stream()
                        .filter(o -> o.getRequiredNum() > 0
                                && o.getRequiredNum() <= o.getDeliveryNum())
                        .count(); // 品種数で算出
                // 総部品数 (欠品数を除く)
                //totalPartsNum = deliveryItemInfos.stream().mapToInt(val -> val.getRequiredNum()).sum() - receptionInfo.getStockOutNum();  // 部品数で算出
                totalPartsNum = deliveryItemInfos.stream()
                        .filter(o -> o.getRequiredNum() > 0)
                        .count(); // 品種数で算出
            }

            // ピッキング数
            tmp = Objects.nonNull(totalPickingNum) ? String.format("%,d", totalPickingNum): "";
            bottomMsg.append(tmp);
            bottomTooltipMsg.append(tmp);
            // 総部品数
            tmp = Objects.nonNull(totalPartsNum) ? String.format("%,d", totalPartsNum): "";
            bottomMsg.append(" / ").append(tmp);
            bottomTooltipMsg.append(" / ").append(tmp);

            // ラベルを作成
            Label bottomLabel = createPayoutOrderLabel(2);
            bottomLabel.setText(bottomMsg.toString());

            // ツールチップを追加
            this.buildTooltip(bottomLabel, bottomTooltipMsg.toString());

            // ----- 欠品部分を作成 -----
            stockoutMsg = new StringBuilder();
            stockoutTooltipMsg = new StringBuilder();
            Label stockoutLabel;

            // 欠品の有無確認
            if (Objects.nonNull(receptionInfo.getStockOutNum()) && 0 < receptionInfo.getStockOutNum()) {
                // 欠品有
                stockFlag = true;
                
                tmp = String.format("%,d", receptionInfo.getStockOutNum());
                stockoutMsg.append(this.rb.getString("key.MissingParts")).append("  ").append(tmp);
                stockoutTooltipMsg.append(tmp);

                // ラベルを作成
                stockoutLabel = createPayoutOrderLabel(3);
                stockoutLabel.setText(stockoutMsg.toString());

                // ツールチップを追加
                this.buildTooltip(stockoutLabel, stockoutTooltipMsg.toString());
            } else {
                // 欠品なし
                stockFlag = false;
                
                // ラベルを作成
                stockoutLabel = createPayoutOrderLabel(3);
                stockoutLabel.setText("");
            }

            // ----- 払出指示を作成 -----
            // 下段部分を編集
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().add(bottomLabel);
            hBox.getChildren().add(stockoutLabel);

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().add(topLabel);
            vBox.getChildren().add(hBox);
            
            // 処理日の00:00:00を取得
            Date date = Date.from(convertToLocalDateViaSqlDate(new Date()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            // 払出予定日の00:00:00を取得
            Date deliveryDate = null;
            if (Objects.nonNull(receptionInfo.getDeliveryDate())) {
                deliveryDate = Date.from(convertToLocalDateViaSqlDate(receptionInfo.getDeliveryDate()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            }

            // 指示情報の状態確認
            if(Objects.equals(DeliveryStatusEnum.CONFIRM, receptionInfo.getStatus())) {
                // 確認待ち(払出不可)の場合
                style = String.format(PAYOUT_ORDER_LINE_ATTENTION, config.getFontSizePs());
                topLabel.setStyle(style);
                bottomLabel.setStyle(style);
                stockoutLabel.setStyle(style);
            } else if (!Objects.equals(DeliveryStatusEnum.COMPLETED, receptionInfo.getStatus())
                    && Objects.nonNull(receptionInfo.getDueDate())
                    && (Objects.nonNull(receptionInfo.getDeliveryDate()) && receptionInfo.getDueDate().compareTo(deliveryDate) < 0
                        || Objects.isNull(receptionInfo.getDeliveryDate()) && date.after(receptionInfo.getDueDate()))) {
                // 完了予定遅れの場合
                
                // 完了予定遅れの情報を取得
                Optional<DisplayedStatusInfoEntity> dsie = model.getDisplayedStatuses().stream()
                        .filter(info -> Objects.equals(StatusPatternEnum.WORK_DELAYCOMP, info.getStatusName()))
                        .findFirst();

                // ラベルのスタイルを変更
                if( dsie.isPresent() ) {
                    DisplayedStatusInfoEntity entity = dsie.get();
                    style = String.format(PAYOUT_ORDER_LINE_DELAY, config.getFontSizePs(), entity.getFontColor(), entity.getBackColor());
                    topLabel.setStyle(style);
                    bottomLabel.setStyle(style);
                    stockoutLabel.setStyle(style);
                }
            }

            // 欠品有時の欠品部分は指示情報の状態に関係なくスタイルは同じ
            if (stockFlag) {
                style = String.format(PAYOUT_ORDER_LINE_ATTENTION, config.getFontSizePs());
                stockoutLabel.setStyle(style);
            }

            payoutOrderListPane.add(vBox);
        }
        
        return payoutOrderListPane;
    }

    /**
     * DateをLocalDateに変換
     *
     * @param dateToConvert 変換対象
     * @return
     */
    private LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    /**
     * 払出指示のラベルを作成する。
     * 
     * @param makeKbn 作成区分(1:上段部分,2:下段部分,3:欠品部分,左記以外:欠品部分)
     * @return 払出指示のラベル
     */
    private Label createPayoutOrderLabel(int makeKbn) {

        double topPlanWidth = this.payoutOrderWidth;
        double bottomPlanWidth = new BigDecimal(topPlanWidth * 0.6).setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
        double stockoutPlanWidth = topPlanWidth - bottomPlanWidth;

        Label makeLabel = new Label();
        
        // スタイルを設定
        makeLabel.setStyle(String.format(PAYOUT_ORDER_LINE_NORMAL, config.getFontSizePs()));
        makeLabel.setAlignment(Pos.CENTER);                                     // ラベル内のテキストやグラフィックの構成を指定
        makeLabel.setTextAlignment(TextAlignment.LEFT);                         // ラベル内のテキストの水平方向の配置を設定
        makeLabel.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);     // ラベルの最小サイズを設定
        makeLabel.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);     // ラベルの最大サイズを設定
        
        // ラベルの推奨サイズを設定
        switch (makeKbn) {
            case 1:         // 上段部分
                makeLabel.setPrefSize(topPlanWidth, this.PAYOUT_ORDER_HEIGHT);                        
                break;
            case 2:         // 下段部分
                makeLabel.setPrefSize(bottomPlanWidth, this.PAYOUT_ORDER_HEIGHT);
                break;
            case 3:         // 欠品部分
            default:
                makeLabel.setPrefSize(stockoutPlanWidth, this.PAYOUT_ORDER_HEIGHT);
                break;
        }
        
        return makeLabel;
    }

    /**
     * ツールチップを構築する。
     *
     * @param control
     * @param text
     */
    private void buildTooltip(Control control, String text) {
        try {
            Tooltip toolTip = TooltipBuilder.build(control);
            toolTip.setText(text);
        } catch (Exception ex) {
        }
    }
    
}
