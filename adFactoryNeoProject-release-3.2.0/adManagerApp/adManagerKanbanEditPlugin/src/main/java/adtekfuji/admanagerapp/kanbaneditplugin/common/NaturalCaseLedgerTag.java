package adtekfuji.admanagerapp.kanbaneditplugin.common;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;


/**
 * タグの大文字・小文字を区別する
 */
public class NaturalCaseLedgerTag implements LedgerTagCase {

    public static final String name = "NaturalCase";

    static public NaturalCaseLedgerTag instance = new NaturalCaseLedgerTag();

    /**
     * マップ取得
     * @return 大文字小文字を区別するマップを返す
     */
    @Override
    public Map<String, Object> getMap()
    {
        return new TreeMap<>();
    }

    /**
     * タグを変換する
     * @return 大文字、小文字を変換しない
     */
    @Override
    public Function<String, String> getTagConverter()
    {
        return string -> string;
    }
}
