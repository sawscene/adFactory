package jp.adtekfuji.adFactory.enumerate;

import jakarta.xml.bind.annotation.XmlEnum;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * サーバーエラー種別
 *
 * @author ke.yokoi
 */
@XmlEnum(String.class)
public enum ServerErrorTypeEnum {

    SUCCESS(0x00),                          // 成功
    SERVER_FETAL(0x01),                     // サーバー処理エラー
    NAME_OVERLAP(0x02),                     // 名前重複で追加/更新不可
    IDENTNAME_OVERLAP(0x03),                // 識別子重複で追加/更新不可
    NOTFOUND_UPDATE(0x04),                  // 指定IDなしで更新不可
    NOTFOUND_DELETE(0x05),                  // 指定IDなしで削除不可
    NOTFOUND_PARENT(0x06),                  // 指定親階層IDなしで追加/更新不可
    EXIST_HIERARCHY_DELETE(0x07),           // 子階層ありで削除不可
    EXIST_CHILD_DELETE(0x08),               // 子要素ありで削除不可
    PROTCTED_DATA(0x09),                    // 保護された情報で削除不可
    NOT_PERMIT_EQUIPMENT(0x0A),             // 設備の接続不許可
    NOT_LOGINID_ORGANIZATION(0x0B),         // 組織のログイン不許可（ログインID未登録）
    NOT_AUTH_ORGANIZATION(0x0C),            // 組織のログイン不許可（認証情報不一致）
    NOT_PERMIT_ORGANIZATION(0x0D),          // 組織のログイン不許可（権限なし）
    THERE_START_NON_EDITABLE(0x0E),         // 開始している工程があるので、編集不可
    THERE_START_NON_DELETABLE(0x0F),        // 開始している工程があるので、削除不可
    UPDATE_ONLY_STATUS(0x10),               // ステータスのみ更新
    LICENSE_ERROR(0x11),                    // ライセンスエラー
    NOT_DELETE_SYSTEM_ADMIN(0x12),          // SYSTEM_ADMIN削除不可
    NOTFOUND_WORKFLOW(0x13),                // 工程順が見つからない
    NOTFOUND_ORGANIZATION(0x14),            // 組織が見つからない
    INVALID_ARGUMENT(0x15),                 // 無効な引数です
    THERE_WORKING_NON_START(0x16),          // 作業中のため作業不可
    THERE_COMPLETED_NON_START(0x17),        // 作業完了済みのため作業不可
    THERE_INTERRUPT_NON_START(0x2a),        // カンバンステータスが計画中、または中断のため作業不可
    THERE_SUPPORT_WORKING(0x2b),            // 応援者が作業中のため、作業を中断又は完了不可
    THERE_NOT_WORKING_NON_START(0x2c),      // 主作業者が作業中でないため、応援者の作業開始不可
    NOTFOUND_KANBAN(0x18),                  // カンバンが見つからない
    NOTFOUND_WORKKANBAN(0x19),              // 工程カンバンが見つからない
    OVER_MAX_VALUE(0x1A),                   // 最大値をオーバーしているデータがある
    FILE_NOT_EXIST(0x1B),                   // ファイルが存在しない
    MAIL_AUTHENTICATION_FAILED(0x1C),       // メール送信で認証失敗
    MAIL_MESSAGING_EXCEPTION(0x1D),         // メール送信でメッセージ異常
    ALREADY_WORKING_ORGANIZATION(0x1E),     // 他端末で作業中の組織
    NOTFOUND_WORK_CATEGORY(0x1F),           // 作業区分が見つからない
    EXIST_RELATION_DELETE(0x20),            // 関連付けありで削除不可
    UNMOVABLE_HIERARCHY(0x21),              // 移動不可能な階層
    DIFFERENT_VER_INFO(0x22),               // 排他用バージョンが異なる
    PARTS_ADD_ERROR(0x23),                  // 完成品情報登録エラー
    PARTS_DEL_ERROR(0x24),                  // 完成品情報削除エラー
    SERAL_ACTUAL_ADD_ERROR(0x25),           // ロット流し生産のシリアル番号毎の実績登録エラー
    DEFECT_ACTUAL_ADD_ERROR(0x26),          // 不良品の実績登録エラー
    REPLENISHMENT_KANBAN_ADD_ERROR(0x27),   // 補充カンバン登録エラー
    DEFECT_ACTUAL_FAILED(0x28),             // 不良品登録エラー
    ACTUAL_ADITION_ADD_ERROR(0x29),         // 工程実績付加情報登録エラー
    WAREHOUSE_DB_CONNECTION_FAILED(0x2A),   // 倉庫システムDBへの接続に失敗
    WAREHOUSE_DB_SELECT_FAILED(0x2B),       // 倉庫システムDBから情報の取得に失敗
    WAREHOUSE_DB_NOTFOUND_PARTS(0x2C),      // 倉庫システムDBに親PIDに紐づく使用部品情報が1件も存在しない。
    NOT_SET_PARENTPID(0x2D),                // カンバンに親PIDが設定されていない
    LOGIN_LDAP_EXCEPTION(0x30),             // LDAP認証処理で不明なエラー
    REQUEST_LIMIT_EXCEEDED(0x31),           // リクエスト上限を超えた
    APPROVAL_APPLY_NON_DELETABLE(0x32),     // 申請中のため削除不可
    NOTFOUND_SERIAL_NO(0x33),               // シリア番号が見つからない
    NOT_ASSIGNED_WORK(0x34),                // 割り当てられた作業はありません
    THERE_SUPPORT_WORKING_LOTOUT(0x35),     // 応援者が作業中のため、ロットアウト不可
    RELATED_WORKFLOW_APPLYING(0x36),        // 対象の工程を使用している工程順が申請中のため申請不可
    RELATED_WORK_APPLYING(0x37),            // 対象の工程順で使用している工程が申請中のため申請不可
    NOT_SOME_UPDATED(0x38),                 // 一部のデータが更新されなかった
    NOTFOUND_WORK(0x39),                    // 工程が見つからない
    NOTFOUND_ACTUAL_RESULT(0x40),           // 工程実績が見つからない
    EQUIPMENT_TYPE_INCORRECT(0x41),         // 設備ログイン不可 (設備種別が不一致)
    NOTFOUND_WORKINGWORK(0x42),             // 作業中の作業が見つかりません。
    NOTFOUND_ITEM(0x043),                   // 項目が見つかりません。
    NOT_PERMITTED_EDIT_RESOURCE(0x44),      // リソースの編集が許可されていません。
    NOT_ACCESS_RESOURCE(0x45),              // アクセス権がないため、リソースにアクセスできない。
    LOCKED_RESOURCE(0x46),                  // リソースはロックされている(または、使用中)ため編集不可
    
    // 倉庫案内 (1000番台～)
    NOTFOUND_LOCATION(0x3E8),               // 棚マスタが存在しない
    NOTFOUND_PRODUCT(0x3E8+1),              // 部品マスタが存在しない
    NOTFOUND_MATERIAL(0x3E8+2),             // 資材情報が存在しない
    NOTFOUND_DELIVERY(0x3E8+3),             // 出庫指示情報が存在しない
    WRONG_LOCATION(0x3E8 + 4),              // 棚が間違っている
    PARTS_NO_OVERLAP(0x3E8 + 5),            // 部品番号の重複エラー
    MATERIAL_NON_EDITABLE(0x3E8 + 6),       // 既に出庫されているため、編集不可
    RESULT_UNCONFIRMED(0x3E8 + 7),          // 結果未確認
    NOTFOUND_LOGSTOCK(0x3E8 + 8),           // 入出庫実績情報が存在しない
    NOT_START_INVENTORY(0x3E8 + 9),         // 棚卸開始していない
    EXIST_INVENTORY_RESULT(0x3E8 + 10),     // 棚卸結果が存在する

    UNKNOWN_ERROR(0xFFFF);                  // 不明なエラー

    private final int code;

    /**
     * コンストラクタ
     *
     * @param code エラーコード
     */
    private ServerErrorTypeEnum(int code) {
        this.code = code;
    }

    /**
     * エラーコードを取得する。
     *
     * @return エラーコード
     */
    public int getCode() {
        return this.code;
    }
}
