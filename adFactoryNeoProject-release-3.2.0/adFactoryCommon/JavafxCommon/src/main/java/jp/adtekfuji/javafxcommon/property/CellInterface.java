/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.List;

/**
 *
 * @author e-mori
 */
public interface CellInterface {

    void addCell(AbstractCell cell);

    void removeCell();

    void clearCell();

    void checkCell();

    /**
     * 表示順を上げる
     */
    void increaseOrder();

    /**
     * 表示順を下げる
     */
    void decreaseOrder();

    /**
     * セルを取得する
     *
     * @return
     */
    List<AbstractCell> getCells();
}
