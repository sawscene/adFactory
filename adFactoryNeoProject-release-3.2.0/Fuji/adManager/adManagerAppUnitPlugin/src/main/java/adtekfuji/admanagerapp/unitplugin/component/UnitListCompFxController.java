/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this  file, choose Tools | s
 * and open the  in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component;

import adtekfuji.admanagerapp.unitplugin.component.table.UnitTableController;
import adtekfuji.admanagerapp.unitplugin.component.tree.UnitTreeController;
import adtekfuji.cash.CashManager;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitHierarchyInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import jp.adtekfuji.javafxcommon.utils.SplitPaneUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット一覧画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
@FxComponent(id = "UnitListComp", fxmlPath = "/fxml/compo/unitListCompo.fxml")
public class UnitListCompFxController implements Initializable, UnitListCompoInterface, ArgumentDelivery, ComponentHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final UnitTreeController tree = new UnitTreeController(this);
    private final UnitTableController table = new UnitTableController(this);

    private boolean isInitView = false;// 初期化終了フラグ

    @FXML
    private SplitPane unitPane;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private Pane workProgress;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SplitPaneUtils.loadDividerPosition(unitPane, getClass().getSimpleName());
        this.createView();
    }

    /**
     * 前の画面からの引継ぎ処理
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        logger.info(UnitListCompFxController.class.getName() + ":setArgument start");
        logger.info(UnitListCompFxController.class.getName() + ":setArgument end");
    }

    /**
     * 画面生成処理
     *
     */
    private void createView() {
        forceBlockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // ツリー作成
                    FXMLLoader fXMLLoader1 = new FXMLLoader(getClass().getResource("/fxml/compo/tree/unitTree.fxml"), rb);
                    fXMLLoader1.setController(tree);
                    AnchorPane treePane = fXMLLoader1.load();
                    setAnchor(treePane);
                    Platform.runLater(() -> {
                        leftPane.getChildren().add(treePane);
                    });

                    // テーブル作成
                    FXMLLoader fXMLLoader2 = new FXMLLoader(getClass().getResource("/fxml/compo/table/unitTable.fxml"), rb);
                    fXMLLoader2.setController(table);
                    AnchorPane tablePane = fXMLLoader2.load();
                    setAnchor(tablePane);
                    Platform.runLater(() -> {
                        rightPane.getChildren().add(tablePane);
                    });

                    createCashData();

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        forceBlockUI(false);
                        isInitView = true;
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 画面固定処理
     *
     * @param node 固定したい情報
     */
    public void setAnchor(Node node) {
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    /**
     * 選択されているユニットの取得
     *
     * @return 選択されているユニット
     */
    @Override
    public UnitInfoEntity getSelectRecord() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | s.
    }

    /**
     * 選択されているユニット階層の取得
     *
     * @return 選択されているユニット階層
     */
    @Override
    public UnitHierarchyInfoEntity getSelectTree() {
        return this.tree.getSelectHierarchy();
    }

    /**
     * ユニットテーブル画面の更新
     *
     * @param hierarchy 階層データ
     */
    @Override
    public void updateTable(UnitHierarchyInfoEntity hierarchy) {
        table.updateTable(hierarchy.getUnitHierarchyId());
    }

    /**
     * ユニットツリー画面の更新
     *
     */
    @Override
    public void updateTree() {
        this.tree.updateTree();
    }

    /**
     * ユニットテーブル画面の削除
     *
     */
    @Override
    public void clearTable() {
        this.table.clearTableList();
    }

    /**
     * 画面に使用制限をかける (初期化中はキャンセルされる)
     *
     * @param isBlock
     */
    @Override
    public void blockUI(boolean isBlock) {
        if (!isInitView) {
            return;
        }
        this.forceBlockUI(isBlock);
    }

    /**
     * 画面に使用制限をかける
     *
     * @param isBlock 
     */
    private void forceBlockUI(boolean isBlock) {
        sc.blockUI("ContentNaviPane", isBlock);
        workProgress.setVisible(isBlock);
    }

    /**
     * キャッシュデータ読み込み
     *
     */
    private void createCashData() {
        try {
            CashManager cache = CashManager.getInstance();

            CacheUtils.createCacheOrganization(true);
            CacheUtils.createCacheEquipment(true);

            cache.setNewCashList(KanbanInfoEntity.class);
            cache.setNewCashList(UnitInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public boolean destoryComponent() {
        SplitPaneUtils.saveDividerPosition(unitPane, getClass().getSimpleName());
        return true;
    }
}
