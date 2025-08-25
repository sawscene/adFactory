package adtekfuji.admanagerapp.workfloweditplugin.component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.admanagerapp.workfloweditplugin.common.SelectedWorkflowAndHierarchy;
import adtekfuji.admanagerapp.workfloweditplugin.model.WorkflowEditModel;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.javafxcommon.event.ActionEventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程・工程順編集メニュー
 *
 * @author ta.ito
 */
@FxComponent(id = "WorkflowNaviCompo", fxmlPath = "/fxml/compo/workflow_navi_compo.fxml")
public class FunctionNaviCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private final Long CLICKABLE_INTERVAL = 1000L;

    private long actionMillisWork = 0L;
    private long actionMillisWorkflow = 0L;
    private long actionMillisLiteWorkflow = 0L;

    @FXML
    private VBox menuPane;
    @FXML
    private Button registProcessButton;
    @FXML
    private Button registOrderButton;
    @FXML
    private Button liteRegistOrderButton;
    @FXML
    private Button backButton;

    /**
     * 工程・工程順編集メニューを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // メニューの表示を切り替える
        final boolean isWorkflowEditor = ClientServiceProperty.isLicensed(LicenseOptionType.WorkflowEditor.getName());
        final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        if (!isWorkflowEditor) {
            this.menuPane.getChildren().remove(this.registProcessButton);
            this.menuPane.getChildren().remove(this.registOrderButton);
        }
        if (!isLiteOption) {
            this.menuPane.getChildren().remove(this.liteRegistOrderButton);
        }
        
        WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();
        if (!workflowEditModel.isInnerMode()) {
            this.menuPane.getChildren().remove(this.backButton);
        }
    }

    /**
     * パラメータを設定する。
     * 
     * @param argument パラメータ 
     */
    @Override
    public void setArgument(Object argument) {
        WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();
        if (workflowEditModel.isInnerMode()) {
            Properties arguments = new Properties();
            arguments.put("hideMenu", Boolean.TRUE.toString());
            sc.setComponent("AppBarPane", "AppBarCompo", arguments);

            if (argument instanceof WorkflowInfoEntity workflow && Objects.nonNull(workflow.getWorkflowId())) {
                // 工程編集画面
                SelectedWorkflowAndHierarchy selected = new SelectedWorkflowAndHierarchy(workflow, "", null);
                sc.setComponent("ContentNaviPane", "WorkflowDetailCompo", selected);
                return;
            }

            // 工程順編集画面
            sc.setComponent("ContentNaviPane", "WorkflowEditCompo");
        }
    }

    /**
     * 工程の登録
     * 
     * @param event 
     */
    @FXML
    private void onRegistProcessAction(ActionEvent event) {
        // 連打を防止
        long nowTime = System.currentTimeMillis();
        if ((nowTime - this.actionMillisWork) <= CLICKABLE_INTERVAL) {
            return;
        }
        this.actionMillisWork = nowTime;

        sc.setComponent("ContentNaviPane", "WorkEditCompo");
    }

    /**
     * 工程順の登録
     * 
     * @param event 
     */
    @FXML
    private void onRegistOrderProcessesAction(ActionEvent event) {
        // 連打を防止
        long nowTime = System.currentTimeMillis();
        if ((nowTime - this.actionMillisWorkflow) <= CLICKABLE_INTERVAL) {
            return;
        }
        this.actionMillisWorkflow = nowTime;

        sc.setComponent("ContentNaviPane", "WorkflowEditCompo");
    }

    /**
     * [Lite]工程順編集
     * 
     * @param event 
     */
    @FXML
    private void onLiteRegistProcessAction(ActionEvent event) {
        // 連打を防止
        long nowTime = System.currentTimeMillis();
        if ((nowTime - this.actionMillisLiteWorkflow) <= CLICKABLE_INTERVAL) {
            return;
        }
        this.actionMillisLiteWorkflow = nowTime;

        sc.setComponent("ContentNaviPane", "WorkflowEditLite");
    }
    
    /**
     * 前画面に戻る
     * 
     * @param event 
     */
    @FXML
    private void onBack(ActionEvent event) {
        WorkflowEditModel workflowEditModel = WorkflowEditModel.getInstance();
        if (workflowEditModel.isInnerMode()) {
            workflowEditModel.raiseEvent(ActionEventListener.SceneEvent.Close, null);
        }        
    }
}
