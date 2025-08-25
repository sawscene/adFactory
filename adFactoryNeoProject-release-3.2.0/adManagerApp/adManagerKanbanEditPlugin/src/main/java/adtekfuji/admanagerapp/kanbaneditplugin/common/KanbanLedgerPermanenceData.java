/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.common;

import java.util.List;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.assemblyparts.AssemblyPartsInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.TraceabilityEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;

/**
 * カンバン帳票データ
 *
 * @author e-mori
 */
public class KanbanLedgerPermanenceData {

    /**
     * 帳票テンプレートパス
     */
    private String ledgerFilePass;

    /**
     * カンバン
     */
    private KanbanInfoEntity kanbanInfoEntity;

    /**
     * 工程カンバン一覧
     */
    private List<WorkKanbanInfoEntity> workKanbanInfoEntities;

    /**
     * 追加工程カンバン一覧
     */
    private List<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntities;

    /**
     * 工程実績一覧
     */
    private List<ActualResultEntity> actualResultInfoEntities;

    /**
     * トレーサビリティ一覧
     */
    private List<TraceabilityEntity> traceabilityEntities;

    /**
     * 使用部品一覧
     */
    private List<AssemblyPartsInfoEntity> assemblyPartsInfos;
    
    /**
     * 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     */
    private boolean useExtensionTag;

    /**
     * 部品トレースを使用するか(true:使用する, false:使用しない)
     */
    private boolean enablePartsTrace;

    /**
     * QRコードを使用するか(true:使用する, false:使用しない)
     */
    private boolean useQRCodeTag;

    /**
     * コンストラクタ
     */
    public KanbanLedgerPermanenceData() {
    }

    /**
     * 帳票テンプレートパスを取得する。
     *
     * @return 帳票テンプレートパス
     */
    public String getLedgerFilePass() {
        return this.ledgerFilePass;
    }

    /**
     * 帳票テンプレートパスを設定する。
     *
     * @param ledgerFilePass 帳票テンプレートパス
     */
    public void setLedgerFilePass(String ledgerFilePass) {
        this.ledgerFilePass = ledgerFilePass;
    }

    /**
     * カンバンを取得する。
     *
     * @return カンバン
     */
    public KanbanInfoEntity getKanbanInfoEntity() {
        return this.kanbanInfoEntity;
    }

    /**
     * カンバンを設定する。
     *
     * @param kanbanInfoEntity カンバン
     */
    public void setKanbanInfoEntity(KanbanInfoEntity kanbanInfoEntity) {
        this.kanbanInfoEntity = kanbanInfoEntity;
    }

    /**
     * 工程カンバンを取得する。
     *
     * @return 工程カンバン
     */
    public List<WorkKanbanInfoEntity> getWorkKanbanInfoEntities() {
        return this.workKanbanInfoEntities;
    }

    /**
     * 工程カンバンを設定する。
     *
     * @param workKanbanInfoEntities 工程カンバン一覧
     */
    public void setWorkKanbanInfoEntities(List<WorkKanbanInfoEntity> workKanbanInfoEntities) {
        this.workKanbanInfoEntities = workKanbanInfoEntities;
    }

    /**
     * 追加工程カンバン一覧を取得する。
     *
     * @return 追加工程カンバン一覧
     */
    public List<WorkKanbanInfoEntity> getSeparateworkWorkKanbanInfoEntities() {
        return this.separateworkWorkKanbanInfoEntities;
    }

    /**
     * 追加工程カンバン一覧を設定する。
     *
     * @param separateworkWorkKanbanInfoEntities 追加工程カンバン一覧
     */
    public void setSeparateworkWorkKanbanInfoEntities(List<WorkKanbanInfoEntity> separateworkWorkKanbanInfoEntities) {
        this.separateworkWorkKanbanInfoEntities = separateworkWorkKanbanInfoEntities;
    }

    /**
     * 工程実績一覧を取得する。
     *
     * @return 工程実績一覧
     */
    public List<ActualResultEntity> getActualResultInfoEntities() {
        return this.actualResultInfoEntities;
    }

    /**
     * 工程実績一覧を設定する。
     *
     * @param actualResultInfoEntities 工程実績一覧
     */
    public void setActualResultInfoEntities(List<ActualResultEntity> actualResultInfoEntities) {
        this.actualResultInfoEntities = actualResultInfoEntities;
    }

    /**
     * トレーサビリティ一覧を取得する。
     *
     * @return トレーサビリティ一覧
     */
    public List<TraceabilityEntity> getTraceabilityEntities() {
        return this.traceabilityEntities;
    }

    /**
     * トレーサビリティ一覧を設定する。
     *
     * @param traceabilityEntities トレーサビリティ一覧
     */
    public void setTraceabilityEntities(List<TraceabilityEntity> traceabilityEntities) {
        this.traceabilityEntities = traceabilityEntities;
    }

    /**
     * 使用部品一覧を取得する。
     *
     * @return 使用部品一覧
     */
    public List<AssemblyPartsInfoEntity> getAssemblyPartsInfos() {
        return this.assemblyPartsInfos;
    }

    /**
     * 使用部品一覧を設定する。
     *
     * @param assemblyPartsInfos 使用部品一覧
     */
    public void setAssemblyPartsInfos(List<AssemblyPartsInfoEntity> assemblyPartsInfos) {
        this.assemblyPartsInfos = assemblyPartsInfos;
    }

    /**
     * 「拡張フラグを使用するか」を取得する。
     * 
     * @return 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     */
    public boolean getUseExtensionTag() {
        return this.useExtensionTag;
    }

    /**
     * 「拡張フラグを使用するか」を設定する。
     * 
     * @param useExtensionTag 「拡張フラグを使用するか」（true：使用する/false：使用しない）
     */
    public void setUseExtensionTag(boolean useExtensionTag) {
        this.useExtensionTag = useExtensionTag;
    }

    /**
     * 部品トレースを使用するかを取得する。
     *
     * @return 部品トレースを使用するか(true:使用する, false:使用しない)
     */
    public boolean isEnablePartsTrace() {
        return this.enablePartsTrace;
    }

    /**
     * 部品トレースを使用するかを設定する。
     *
     * @param enablePartsTrace 部品トレースを使用するか(true:使用する, false:使用しない)
     */
    public void setEnablePartsTrace(boolean enablePartsTrace) {
        this.enablePartsTrace = enablePartsTrace;
    }

    /**
     * 「QRコードを使用するか」を取得する。
     *
     * @return 「QRコードを使用するか」（true：使用する/false：使用しない）
     */
    public boolean getUseQRCodeTag() {
        return this.useQRCodeTag;
    }

    /**
     * 「QRコードを使用するか」を設定する。
     * 
     * @param useQRCodeTag 「QRコードを使用するか」（true：使用する/false：使用しない）
     */
    public void setUseQRCodeTag(boolean useQRCodeTag) {
        this.useQRCodeTag = useQRCodeTag;
    }
}
