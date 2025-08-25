package adtekfuji.admanagerapp.chartplugin.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import adtekfuji.admanagerapp.chartplugin.common.Constants;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import adtekfuji.locale.LocaleUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import jp.adtekfuji.adFactory.entity.workflow.ConWorkflowWorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.javafxcommon.utils.Selection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程ペイン
 *
 * @author fu-kato
 */
public class WorkPane implements Initializable {

    private final Logger logger = LogManager.getLogger();

    @FXML
    private TableView<Selection<ConWorkflowWorkInfoEntity>> workView;

    @FXML
    private TableColumn<Selection<ConWorkflowWorkInfoEntity>, Boolean> selectedColumn;
    @FXML
    private TableColumn<Selection<ConWorkflowWorkInfoEntity>, Number> indexColumn;
    @FXML
    private TableColumn<Selection<ConWorkflowWorkInfoEntity>, String> workNameColumn;

    //UPまたはDOWN時に呼ばれるイベント
    private EventHandler<ActionEvent> moveListener = (ActionEvent e) -> {};
    
    //チェックボックスが切り替わったときに呼ばれるイベント
    private ChangeListener checkBoxChangeListener = (observable, oldValue, newValue) -> {};

    /**
     * チェックボックスが切り替わったときに呼ばれるイベントを追加する
     * 
     * @param checkBoxChangeListener 
     */
    public void addCheckBoxChangeListener(ChangeListener checkBoxChangeListener) {
        this.checkBoxChangeListener = checkBoxChangeListener;
        this.workView.getItems().stream()
                .forEach(selection -> {
                    selection.selectedProperty().addListener(this.checkBoxChangeListener);
                });
    }

    /**
     * 工程名を指定しそれが存在するならエンティティを取得する
     *
     * @param workName
     * @return 
     */
    public Optional<Selection<ConWorkflowWorkInfoEntity>> getWorkSelection(String workName) {
        for(Selection<ConWorkflowWorkInfoEntity> entity : workView.getItems()) {
            if(entity.getName().equals(workName)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }
    
    /**
     * 工程リストのリストを取得
     * 
     * @return 
     */
    public ObservableList<Selection<ConWorkflowWorkInfoEntity>> getWorkSelections() {
        return workView.getItems();
    }

    /**
     * 項目を上下に移動したときに呼ばれるイベントを設定する
     * 
     * @param moveListener 
     */
    public void setMoveListener(EventHandler<ActionEvent> moveListener) {
        this.moveListener = moveListener;
    }

    /**
     * 選択された工程を取得する。
     *
     * @return
     */
    public Map<Integer, ConWorkflowWorkInfoEntity> getSelectedWork() {
        Map<Integer, ConWorkflowWorkInfoEntity> works = new HashMap<>();
        for (Selection<ConWorkflowWorkInfoEntity> row : this.workView.getItems()) {
            if (row.isSelected()) {
                int index = this.workView.getItems().indexOf(row) + 1;
                works.put(index, row.getValue());
            }
        }
        return works;
    }

    /**
     * 工程ペインの初期化
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        workView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        CheckBox checkBox = new CheckBox();
        checkBox.setOnAction((ActionEvent event) -> {
            if (checkBox.isSelected()) {
                for (Selection<ConWorkflowWorkInfoEntity> row : workView.getItems()) {
                    row.setSelected(Boolean.TRUE);
                }
            } else {
                for (Selection<ConWorkflowWorkInfoEntity> row : workView.getItems()) {
                    row.setSelected(Boolean.FALSE);
                }
            }
        });
        this.selectedColumn.setGraphic(checkBox);
        this.selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        this.workNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.indexColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(this.workView.getItems().indexOf(column.getValue()) + 1));
        this.selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(this.selectedColumn));
        this.selectedColumn.setEditable(true);
        this.workView.setEditable(true);
    }

    /**
     * 上へ移動
     *
     * @param event
     */
    @FXML
    public void onUp(ActionEvent event) {
        try {
            logger.info("onUp start.");

            if (this.workView.getItems().isEmpty()) {
                return;
            }

            int index = this.workView.getSelectionModel().getSelectedIndex();
            if ((index - 1) >= 0) {
                Selection<ConWorkflowWorkInfoEntity> element = this.workView.getItems().get(index);
                this.workView.getItems().remove(index);
                this.workView.getItems().add(index - 1, element);
                this.workView.getSelectionModel().select(element);
            }

            moveListener.handle(event);
        } finally {
            logger.info("onUp end.");
        }
    }

    /**
     * 下へ移動
     *
     * @param event
     */
    @FXML
    public void onDown(ActionEvent event) {
        try {
            logger.info("onDown start.");

            if (this.workView.getItems().isEmpty()) {
                return;
            }

            int index = this.workView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && (index + 1) < this.workView.getItems().size()) {
                Selection<ConWorkflowWorkInfoEntity> element = this.workView.getItems().get(index);
                this.workView.getItems().remove(index);
                this.workView.getItems().add(index + 1, element);
                this.workView.getSelectionModel().select(element);
            }

            moveListener.handle(event);
        } finally {
            logger.info("onDown end.");
        }
    }

    /**
     * 対象工程リストの生成
     *
     * @param workflow
     */
    public void create(WorkflowInfoEntity workflow) {
        try {
            logger.info("createWorkList start.");

            LinkedList<Selection<ConWorkflowWorkInfoEntity>> list = new LinkedList<>();
            for (ConWorkflowWorkInfoEntity entity : workflow.getConWorkflowWorkInfoCollection()) {
                String displayWorkName = this.createDisplayWorkName(entity.getWorkName(), entity.getWorkRev());

                list.add(new Selection<>(true, displayWorkName, entity));
            }

            Collections.sort(list, new Comparator<Selection<ConWorkflowWorkInfoEntity>>(){
                @Override
                public int compare(Selection<ConWorkflowWorkInfoEntity> left, Selection<ConWorkflowWorkInfoEntity> right){
                    try {
                        long leftOrder = left.getValue().getStandardStartTime().getTime();
                        long rightOrder = right.getValue().getStandardStartTime().getTime();
                        if (leftOrder > rightOrder) {
                            return 1;
                        } else if (leftOrder == rightOrder) {
                            return 0;
                        }
                    } catch (Exception ex) {
                        logger.fatal(ex);
                    }
                    return -1;
                }
            });

            this.createWorkViewItems(list);
        } finally {
            logger.info("createWorkList end.");
        }
    }

    /**
     * プロパティを読み込む。
     *
     * @param properties
     * @param selectedWorkflow
     */
    public void loadProperties(Properties properties, WorkflowInfoEntity selectedWorkflow) {
        if(Objects.isNull(selectedWorkflow)) {
            return;
        }
        
        String value;

        // 対象工程
        Set<Long> selectedWorkIds = new HashSet<>();
        value = properties.getProperty(Constants.TIMELINE_WORK_ID);
        if (!StringUtils.isEmpty(value)) {
            String[] values = value.split(",");
            for (String str : values) {
                selectedWorkIds.add(Long.parseLong(str));
            }
        }

        value = properties.getProperty(Constants.TIMELINE_WORK_LIST);
        if (!StringUtils.isEmpty(value)) {
            LinkedList<Selection<ConWorkflowWorkInfoEntity>> list = new LinkedList<>();
            Map<Long, ConWorkflowWorkInfoEntity> workflowWorks = selectedWorkflow.getConWorkflowWorkInfoCollection().stream().collect(Collectors.toMap(ConWorkflowWorkInfoEntity::getFkWorkId, d -> d));

            String[] values = value.split(",");
            for (String str : values) {
                Long workId = Long.parseLong(str);
                if (workflowWorks.containsKey(workId)) {
                    ConWorkflowWorkInfoEntity entity = workflowWorks.get(workId);

                    String displayWorkName = this.createDisplayWorkName(entity.getWorkName(), entity.getWorkRev());

                    list.add(new Selection<>(selectedWorkIds.contains(workId), displayWorkName, entity));
                }
            }

            this.createWorkViewItems(list);
        } else {
            this.create(selectedWorkflow);
        }
    }
    
    /**
     * リストの項目を構築する
     * 
     * @param list 
     */
    private void createWorkViewItems(List list) {
        ObservableList<Selection<ConWorkflowWorkInfoEntity>> observableList = FXCollections.observableArrayList(list);
        this.workView.setItems(observableList);
        this.workView.getItems().stream()
                .forEach(selection -> {
                    selection.selectedProperty().addListener(this.checkBoxChangeListener);
                });
    }

    /**
     * プロパティを保存する。
     *
     * @param properties
     */
    public void saveProperties(Properties properties) {
        List<String> workIds = new ArrayList<>();
        List<String> selectedWorkIds = new ArrayList<>();

        for (Selection<ConWorkflowWorkInfoEntity> row : this.workView.getItems()) {
            workIds.add(String.valueOf(row.getValue().getFkWorkId()));
            if (row.isSelected()) {
                selectedWorkIds.add(String.valueOf(row.getValue().getFkWorkId()));
            }
        }

        properties.setProperty(Constants.TIMELINE_WORK_LIST, String.join(",", workIds));
        properties.setProperty(Constants.TIMELINE_WORK_ID, String.join(",", selectedWorkIds));
    }

    /**
     * 工程の表示名を作成する。
     *
     * @param workName 工程名
     * @param workRev 版数
     * @return 表示名(工程名 : 版数)
     */
    private String createDisplayWorkName(String workName, Integer workRev) {
        StringBuilder name = new StringBuilder(workName);
        if (Objects.nonNull(workRev)) {
            name.append(" : ").append(workRev);
        }
        return name.toString();
    }
}
