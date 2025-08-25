package adtekfuji.admanagerapp.workfloweditplugin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.javafxcommon.event.ActionEventListener;

/**
 * 工程・工程順編集モデル
 * 
 * @author s-heya
 */
public class WorkflowEditModel {


    private static WorkflowEditModel instance;
    private final List<ActionEventListener> listeners = new ArrayList<>();
    private boolean innerMode;
    private String param;
    private WorkflowInfoEntity workflow;
    
    /**
     * コンストラクタ
     */
    private WorkflowEditModel() {
    }
    
    /**
     * 工程・工程順編集モデルを取得する。
     * 
     * @return 工程・工程順編集モデル
     */
    public static WorkflowEditModel getInstance() {
        if (Objects.isNull(instance)) {
            instance = new WorkflowEditModel();
        }
        return instance;
    }

    /**
     * リスナーを追加する。
     * 
     * @param listner リスナー
     */
    public void addListener(ActionEventListener listner) {
        if (!this.listeners.contains(listner)) {
            this.listeners.add(listner);
        }
    }
    
    /**
     * リスナーを削除する。
     * 
     * @param listner リスナー
     */
    public void removeListener(ActionEventListener listner) {
        this.listeners.remove(listner);
    }
    
    /**
     * イベントを発行する
     *
     * @param event
     * @param param
     */
    public void raiseEvent(ActionEventListener.SceneEvent event, Object param) {
        for (ActionEventListener listner : listeners) {
            listner.onNotification(event, param);
        }
    }

    /**
     * インナーモードかどうかを返す。
     * 
     * @return 
     */
    public boolean isInnerMode() {
        return innerMode;
    }

    /**
     * インナーモードを設定する。
     * 
     * @param innerMode 
     */
    public void setInnerMode(boolean innerMode) {
        this.innerMode = innerMode;
    }

    /**
     * パラメーターを取得する。
     * 
     * @return 
     */
    public String getParam() {
        return param;
    }

    /**
     * パラメーターを設定する。
     * 
     * @param param 
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * 工程順マスタ情報を取得する。
     * 
     * @return 工程順マスタ情報
     */
    public WorkflowInfoEntity getWorkflow() {
        return workflow;
    }

    /**
     * 工程順マスタ情報を設定する。
     * 
     * @param workflow 工程順マスタ情報
     */
    public void setWorkflow(WorkflowInfoEntity workflow) {
        this.workflow = workflow;
    }


}
