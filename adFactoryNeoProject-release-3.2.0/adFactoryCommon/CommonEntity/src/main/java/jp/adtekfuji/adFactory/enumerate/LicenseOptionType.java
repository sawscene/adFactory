/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * オプションタイプ
 *
 * @author ke.yokoi
 */
public enum LicenseOptionType {
    Unknown,          // 不明なライセンス
    NotRequireLicense,      // ライセンス不要
    LineTimer,              // ラインタイマー制御 (標準オプション)
    LineManaged,            // ライン一斉スタート機能
    Warehouse,              // 倉庫管理 (標準オプション)
    KanbanEditor,           // カンバン編集 (標準オプション)
    WorkflowEditor,         // 工程順編集 (標準オプション)
    OrganizationEditor,     // 組織編集 (標準オプション)
    EquipmentEditor,        // 設備編集 (標準オプション)
    CsvReportOut,           // 実績出力 (標準オプション)
    LedgerOut,              // 帳票出力 (標準オプション)
    SystemSettingEditor,    // システム設定 (標準オプション)
    MonitorSettingEditor,   // アンドンモニター設定 (標準オプション)
    Scheduling,             // スケジューリング機能
    ProductionNavi,         // 生産管理ナビ機能
    ApprovalOption,         // 承認機能
    LanguageOption,         // 言語切替機能
    SummaryReport,          // サマリーレポート
    LiteOption,             // Lite
    ReporterOption,         // Reporter
    LedgerManager;          // 帳票管理

    /**
     * オプション名を取得する
     *
     * @return
     */
    public String getName() {
        return "@" + this.name();
    }
}
