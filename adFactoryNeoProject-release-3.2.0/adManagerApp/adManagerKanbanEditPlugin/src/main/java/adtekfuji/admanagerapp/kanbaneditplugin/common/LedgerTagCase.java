package adtekfuji.admanagerapp.kanbaneditplugin.common;

import java.util.Map;
import java.util.function.Function;


/**
 * タグ識別設定
 */
public interface LedgerTagCase {

    String name = "ledgerTagCase";

    /**
     * マップ取得
     * @return タグ識別設定に従いマップの種類を切り替える
     */
    Map<String, Object> getMap();

    /**
     * タグの変換
     * @return タグ識別設定に従いタグを変換する
     */
    Function<String, String> getTagConverter();
}

