package adtekfuji.admanagerapp.kanbaneditplugin.common;

import jp.adtekfuji.excelreplacer.ExcelReplacer;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * 大文字小文字を区別しない場合のタグ設定
 */
public class InsensitiveCaseLedgerTag implements LedgerTagCase {

    public static final String name = "InsensitiveCase";

    public static InsensitiveCaseLedgerTag instance = new InsensitiveCaseLedgerTag();

    /**
     * マップ取得
     * @return 大文字小文字を区別しないマップを返す。
     */
    @Override
    public Map<String, Object> getMap()
    {
        return new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * タグを変換
     * @return 全て大文字にする
     */
    @Override
    public Function<String, String> getTagConverter()
    {
        return ExcelReplacer::toUpperString;
    }
}
