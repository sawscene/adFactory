/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component;

import adtekfuji.admanagerapp.unitplugin.component.tabletree.UniTreeTableController;
import adtekfuji.fxscene.ArgumentDelivery;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニット詳細画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
@FxComponent(id = "UnitDetailComp", fxmlPath = "/fxml/compo/unitDetailCompo.fxml")
public class UnitDetailCompoFxController implements Initializable, UnitDetailCompoInterface, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private final UniTreeTableController tableTree = new UniTreeTableController(this);
    private UnitInfoEntity unitInfoEntity = null;

    @FXML
    private AnchorPane topPane;
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
        // 初期化処理は setArgument で引数を受け取ってから別スレッドで行なう。(処理中表示のため)
    }

    @Override
    public void setArgument(Object argument) {
        logger.info(UnitDetailCompoFxController.class.getName() + ":setArgument start");
        try {
            if (argument instanceof UnitInfoEntity) {
                unitInfoEntity = (UnitInfoEntity) argument;
            }

            this.createView();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitDetailCompoFxController.class.getName() + ":setArgument end");
    }

    /**
     * 画面生成処理
     *
     */
    private void createView() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected synchronized Void call() throws Exception {
                try {
                    // テーブル作成
                    FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource("/fxml/compo/treetable/unitTreeTable.fxml"), rb);
                    fXMLLoader.setController(tableTree);
                    AnchorPane tablePane = fXMLLoader.load();
                    setAnchor(tablePane);
                    Platform.runLater(() -> {
                        topPane.getChildren().add(tablePane);
                    });

                    tableTree.createTreeTable(unitInfoEntity);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
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
     * 画面に使用制限をかける
     *
     * @param isBlock
     */
    @Override
    public void blockUI(boolean isBlock) {
        sc.blockUI("ContentNaviPane", isBlock);
        workProgress.setVisible(isBlock);
    }
}
