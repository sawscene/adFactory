/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.cell;

/**
 * ユニットテンプレート工程順階層画面
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public interface TreeCellInterface {

    public String getName();

    public Long getHierarchyId();

    public Object getEntity();
    
    public Boolean isHierarchy();
}
