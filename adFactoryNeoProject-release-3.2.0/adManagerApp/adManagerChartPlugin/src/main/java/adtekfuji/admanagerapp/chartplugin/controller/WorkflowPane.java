package adtekfuji.admanagerapp.chartplugin.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import adtekfuji.admanagerapp.chartplugin.common.Constants;
import adtekfuji.clientservice.WorkflowHierarchyInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.javafxcommon.treecell.WorkflowHierarchyTreeCell;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程順ペイン
 *
 * @author fu-kato
 */
public class WorkflowPane implements Initializable {

    private final Logger logger = LogManager.getLogger();
    private final WorkflowInfoFacade workflowFacade = new WorkflowInfoFacade();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private TreeItem<WorkflowHierarchyInfoEntity> rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(0L, LocaleUtils.getString("key.OrderProcessesHierarch")));
    private final WorkflowHierarchyInfoFacade workflowHierarchyFacade = new WorkflowHierarchyInfoFacade();

    @FXML
    private Label workflowLabel;

    @FXML
    private TreeView<WorkflowHierarchyInfoEntity> workflowView;

    private WorkflowInfoEntity selectedWorkflow;

    private ChangeListener<? super TreeItem<WorkflowHierarchyInfoEntity>> listener = null;
    
    private boolean withLite = true;

    /**
     * 選択された工程順を取得する。
     *
     * @return
     */
    public WorkflowInfoEntity getSelectedWorkflow() {
        return this.selectedWorkflow;
    }

    /**
     * 項目が変更したときに発生するイベントを登録する
     *
     * @param listener
     */
    public void addListener(ChangeListener<? super TreeItem<WorkflowHierarchyInfoEntity>> listener) {
        this.listener = listener;
    }

    //ツリーが展開するときに呼び出されるイベント
    private final ChangeListener<Boolean> changeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                TreeItem treeItem = (TreeItem) ((BooleanProperty) observable).getBean();
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        expand(treeItem);
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    };


    /**
     * 工程順ペインの初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootItem = new TreeItem<>(new WorkflowHierarchyInfoEntity(0L, LocaleUtils.getString("key.OrderProcessesHierarch")));
      
        // 対象工程順
        this.workflowView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TreeItem<WorkflowHierarchyInfoEntity>> observable, TreeItem<WorkflowHierarchyInfoEntity> oldValue, TreeItem<WorkflowHierarchyInfoEntity> newValue) -> {
            if (Objects.nonNull(newValue) && newValue.getValue().getWorkflowHierarchyId() != 0L) {
                WorkflowHierarchyInfoEntity workflowHierarchy = newValue.getValue();

                if (Objects.isNull(workflowHierarchy.getParentId())) {
                    WorkflowInfoEntity entity  = workflowFacade.find(workflowHierarchy.getWorkflowHierarchyId());
                    if (Objects.nonNull(entity.getWorkflowId())) {
                        this.selectedWorkflow = entity;
                        if(Objects.nonNull(listener)) {
                            listener.changed(observable, oldValue, newValue);
                        }
                    } else {
                        this.selectedWorkflow = null;
                    }
                    this.workflowLabel.setText(workflowHierarchy.getHierarchyName());
                }

            }
        });

        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                create();
                return null;
            }
        };
        new Thread(task).start();
    }

    /**
     * 工程順階層を展開する。
     *
     * @param parentItem 対象階層
     */
    private synchronized void expand(TreeItem<WorkflowHierarchyInfoEntity> parentItem) {
        try {
            parentItem.getChildren().clear();

            WorkflowHierarchyInfoEntity workflowHierarchy = parentItem.getValue();

            long count = workflowHierarchy.getChildCount();
            for (long from = 0; from < count; from += Constants.REST_RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyFacade.getAffilationHierarchyRange(workflowHierarchy.getWorkflowHierarchyId(), from, from + Constants.REST_RANGE - 1, true);
                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0 || !entity.getWorkflowInfoCollection().isEmpty()) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    parentItem.getChildren().add(item);
                });
            }

            for (WorkflowInfoEntity workflow : workflowHierarchy.getWorkflowInfoCollection()) {
                WorkflowHierarchyInfoEntity entity = new WorkflowHierarchyInfoEntity();
                entity.setWorkflowHierarchyId(workflow.getWorkflowId());
                String rev = Objects.isNull(workflow.getWorkflowRev()) ? "" : " : " + workflow.getWorkflowRev().toString();
                entity.setHierarchyName(workflow.getWorkflowName() + rev);
                TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                parentItem.getChildren().add(item);
            }

            Platform.runLater(() -> this.workflowView.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ツリーの親階層生成
     *
     */
    private synchronized void create() {
        try {
            logger.info("create start.");

            long count = workflowHierarchyFacade.getTopHierarchyCount();
            this.rootItem.getChildren().clear();
            this.rootItem.getValue().setChildCount(count);

            for (long from = 0; from < count; from += Constants.REST_RANGE) {
                List<WorkflowHierarchyInfoEntity> entities = workflowHierarchyFacade.getTopHierarchyRange(from, from + Constants.REST_RANGE - 1, true, this.withLite);

                entities.stream().forEach((entity) -> {
                    TreeItem<WorkflowHierarchyInfoEntity> item = new TreeItem<>(entity);
                    if (entity.getChildCount() > 0 || !entity.getWorkflowInfoCollection().isEmpty()) {
                        item.getChildren().add(new TreeItem());
                    }
                    item.expandedProperty().addListener(this.changeListener);
                    this.rootItem.getChildren().add(item);
                });
            }

            Platform.runLater(() -> {
                this.workflowView.rootProperty().setValue(rootItem);
                this.workflowView.setCellFactory((TreeView<WorkflowHierarchyInfoEntity> o) -> new WorkflowHierarchyTreeCell());
            });

            this.rootItem.setExpanded(true);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("create end.");
        }
    }

    /**
     * プロパティを読み込む。
     *
     * @param properties
     */
    public void loadProperties(Properties properties) {
        // 対象工程順
        String value = properties.getProperty(Constants.TIMELINE_WORKFLOW_ID);
        value = Objects.isNull(value) || value.equals("null") ? "" : value;
        if (!StringUtils.isEmpty(value) && !StringUtils.equals(value, "0")) {
            WorkflowInfoEntity entity = this.workflowFacade.find(Long.parseLong(value));
            if (Objects.isNull(entity.getWorkflowId())) {
                return;
            }
            this.selectedWorkflow = entity;
            this.workflowLabel.setText(selectedWorkflow.getWorkflowName() + " : " + selectedWorkflow.getWorkflowRev().toString());
        }
    }

    /**
     * プロパティを保存する。
     *
     * @param properties
     */
    public void saveProperties(Properties properties) {
        Long workflowId = null;
        if (Objects.nonNull(this.selectedWorkflow)) {
            workflowId = this.selectedWorkflow.getWorkflowId();
        }

        properties.setProperty(Constants.TIMELINE_WORKFLOW_ID, String.valueOf(workflowId));
    }

    /**
     * Lite 工程順を含める。
     */
    public void withLite() {
        this.withLite = true;
    }
    
}
