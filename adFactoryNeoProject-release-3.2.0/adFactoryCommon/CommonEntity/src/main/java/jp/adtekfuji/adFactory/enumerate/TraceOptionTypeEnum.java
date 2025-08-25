/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.enumerate;

/**
 * トレーサビリティ追加情報種別
 *
 * @author nar-nakamura
 */
public enum TraceOptionTypeEnum {
    PLUGIN("key.PluginName"),
    WORK("key.ProcessName"),
    PROPERTY("key.Tag"),
    COUNT("key.RowCount"),
    FIELDS("key.Fields"),
    TEN_KEYBOARD("key.tenkeyboard"),
    INTEGER_DIGITS("key.IntegerDigits"),
    DECIMAL_DIGITS("key.DecimalDigits"),
    ABSOLUTE_DISPLAY("key.AbsoluteDisplay"),
    VALUE_LIST("key.ValueList"),
    COLOR_VALUE_LIST("key.ColorValueList"),             // 色付入力値リスト
    REFERENCE_NUMBER("key.ProcessName"),
    FIELD_SIZE("key.FieldSize"),
    DELIMITER("key.InputDelimiter"),
    HOLD_PREV_DATA("key.HoldPreviousData"),
    CHECK_EMPTY("key.ForbitEmptyData"),
    CHECK_UNIQUE("key.ForbitRepeatedData"),
    INPUT_LIST_ONLY("key.InputListOnly"),
    ATTACH_FILE("key.AttachFile"),                      // ファイルを添付する
    BULK_INPUT("key.BulkInputOption"),                  // 製品単位に入力する
    BULK_TYPE_SEQUENTIAL("key.BulkType_sequential"),    // 同じ手順を連続して入力
    BULK_TYPE_GROUPING("key.BulkType_grouping"),        // 複数の手順をグループ化して入力
    CHECK_BARCODE("key.CheckBarcode"),
    INPUT_PRODUCT_NUM("key.InputProductNum"),
    DATETIME_TYPE(""),                      // 日付
    KEYBOARD_TYPE("key.InputKeyboard"),     // キーボードタイプ
    ALIGNMENT("key.Alignment"),             // 文字レイアウト
    ALIGNMENT_LEFT("key.AlignmentLeft"),    // 文字レイアウト 左
    ALIGNMENT_CENTER("key.AlignmentCenter"),// 文字レイアウト 中央
    ALIGNMENT_RIGHT("key.AlignmentRight"),  // 文字レイアウト 右
    INPUT_TEXT("inputText"),                // コメントを入力する
    DISPLAY_TEXT("displayText"),            // 後工程にコメントを表示する
    QR_READ("key.ReadQRByCamera");          // カメラでQRコードを読み取る

    private final String resourceKey;

    /**
     * コンストラクタ
     *
     * @param resourceKey
     */
    private TraceOptionTypeEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * リソースキーを取得する
     *
     * @return
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * 文字列と比較する
     *
     * @param value
     * @return
     */
    public boolean equals(String value) {
        return this.toString().equals(value);
    }
}
