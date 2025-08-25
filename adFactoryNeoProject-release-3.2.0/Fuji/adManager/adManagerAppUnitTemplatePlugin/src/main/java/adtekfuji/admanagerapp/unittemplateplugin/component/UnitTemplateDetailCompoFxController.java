/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unittemplateplugin.component;

import adtekfuji.admanagerapp.unittemplateplugin.component.bpmn.UnitTemplateBPMNController;
import adtekfuji.admanagerapp.unittemplateplugin.component.tree.UnitTemplateAndWorkflowTreeController;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ユニットテンプレート詳細画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.26.Wen
 */
@FxComponent(id = "UnittemplateDetailComp", fxmlPath = "/fxml/compo/unittemplateDetailCompo.fxml")
public class UnitTemplateDetailCompoFxController implements Initializable, UnitTemplateDetailCompoInterface, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private Pane workProgress;

    private final UnitTemplateAndWorkflowTreeController tree = new UnitTemplateAndWorkflowTreeController(this);
    private final UnitTemplateBPMNController bpmnPane = new UnitTemplateBPMNController(this);
    private UnitTemplateInfoEntity editUnitTemplate = null;

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
        logger.info(UnitTemplateDetailCompoFxController.class.getName() + ":setArgument start");
        try {
            if (argument instanceof UnitTemplateInfoEntity) {
                editUnitTemplate = (UnitTemplateInfoEntity) argument;

                if (Objects.isNull(editUnitTemplate.getConUnitTemplateAssociateCollection())) {
                    editUnitTemplate.setConUnitTemplateAssociateCollection(new ArrayList<>());
                }
            }

            this.createView();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        logger.info(UnitTemplateDetailCompoFxController.class.getName() + ":setArgument end");
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
                    // ツリー作成
                    FXMLLoader fXMLLoader1 = new FXMLLoader(getClass().getResource("/fxml/compo/tree/unittemplateAndWorkflowTree.fxml"), rb);
                    fXMLLoader1.setController(tree);
                    AnchorPane treePane = fXMLLoader1.load();
                    setAnchor(treePane);
                    Platform.runLater(() -> {
                        leftPane.getChildren().add(treePane);
                    });

                    // テーブル作成
                    FXMLLoader fXMLLoader2 = new FXMLLoader(getClass().getResource("/fxml/compo/bpmn/unittemplateBPMN.fxml"), rb);
                    fXMLLoader2.setController(bpmnPane);
                    AnchorPane tablePane = fXMLLoader2.load();
                    setAnchor(tablePane);
                    Platform.runLater(() -> {
                        rightPane.getChildren().add(tablePane);
                    });

                    Platform.runLater(() -> {
                        bpmnPane.createBPMN(editUnitTemplate);
                    });

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
                    });
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                blockUI(false);
            }

            @Override
            protected void failed() {
                super.failed();
                blockUI(false);
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

    @Override
    public void addSerial() {
        logger.info(UnitTemplateDetailCompoFxController.class.getName() + ":addSerial start");
        this.bpmnPane.onAddSerial(this.tree.getSelectTreeItem());
        logger.info(UnitTemplateDetailCompoFxController.class.getName() + ":addSerial end");
    }

    @Override
    public void addParallel() {
        logger.info(UnitTemplateDetailCompoFxController.class.getName() + ":addParallel start");
        this.bpmnPane.onAddParallel(this.tree.getSelectTreeItem());
        logger.info(UnitTemplateDetailCompoFxController.class.getName() + ":addParallel end");
    }

    /**
     * 編集中のユニットテンプレートを返す
     *
     * @return
     */
    @Override
    public UnitTemplateInfoEntity getUnitTemplateInfoEntity() {
        return this.editUnitTemplate;
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
