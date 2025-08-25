/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * タグ置換結果
 *
 * @author nar-nakamura
 */
public class ReplaceTagResult {

    private boolean success = false;
    private final Set<String> faildReplaceTags = new LinkedHashSet();

    /**
     * コンストラクタ
     */
    public ReplaceTagResult() {
    }

    /**
     * 成功したかどうかを取得する。
     *
     * @return true:成功, false:失敗
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * 成功したかどうかを設定する。
     *
     * @param success true:成功, false:失敗
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 未置換タグ一覧を取得する。
     *
     * @return 未置換タグ一覧
     */
    public Set<String> getFaildReplaceTags() {
        return this.faildReplaceTags;
    }

    @Override
    public String toString() {
        return new StringBuilder("ReplaceTagResult{")
                .append("success=").append(this.success)
                .append("}")
                .toString();
    }
}
