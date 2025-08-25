/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import adtekfuji.admanagerapp.systemsettingplugin.entity.ApprovalTableData;
import javafx.scene.control.TableRow;

/**
 * 承認ルート組織情報テーブル行クラス
 * 
 * @author shizuka.hirano
 */
public class ApprovalTableRow extends TableRow<ApprovalTableData> {

    /**
     * コンストラクタ
     */
    public ApprovalTableRow() {
    }

    /**
     * 行データ更新
     * @param item　組織情報テーブル情報
     * @param empty empty 空白確認
     */
    @Override
    protected void updateItem(ApprovalTableData item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            this.setStyle("-fx-font-style: normal; -fx-font-weight: normal;");
            this.requestLayout();
        }
    }
}
