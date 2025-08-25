/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.view;

import java.util.ArrayList;
import java.util.List;
import jp.adtekfuji.andon.property.WorkSetting;

/**
 * パフォーマンスデータ
 * 
 * @author s-heya
 */
public class PerformanceRow {
    private final Integer index;
    private final String modelName;
    private final String productionNumber;
    private final List<PerformanceCell> cells = new ArrayList<>();
    
    /**
     * コンストラクタ
     * 
     * @param index
     * @param modelName 
     * @param cellCount
     */
    public PerformanceRow(Integer index, String modelName, String productionNumber, int cellCount) {
        this.index = index;
        this.modelName = modelName;
        this.productionNumber = productionNumber;
        for (int i = 0; i < cellCount + 1; i++) {
            this.cells.add(new PerformanceCell());
        }
    }

    /**
     * コンストラクタ
     * 
     * @param index
     * @param modelName 
     * @param cellCount
     * @param workSetting
     */
    public PerformanceRow(Integer index, String modelName, String productionNumber, int cellCount, WorkSetting workSetting) {
        this.index = index;
        this.modelName = modelName;
        this.productionNumber = productionNumber;
        for (int i = 0; i < cellCount + 1; i++) {
            this.cells.add(new PerformanceCell());
        }
    }

    /**
     * 行インデックスを取得する。
     * 
     * @return 行インデックス
     */
    public Integer getIndex() {
        return this.index;
    }

    /**
     * モデル名を取得する。
     * 
     * @return 行タイトル
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 製造番号を取得する。
     * 
     * @return 製造番号
     */
    public String getProductionNumber() {
        return productionNumber;
    }
    
    /**
     * セル一覧を取得する。
     * 
     * @return セル一覧
     */
    public List<PerformanceCell> getCells() {
        return this.cells;
    }
}
