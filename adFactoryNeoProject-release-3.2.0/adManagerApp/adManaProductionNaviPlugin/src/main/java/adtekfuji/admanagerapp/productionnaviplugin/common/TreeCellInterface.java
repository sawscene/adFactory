/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.common;

/**
 *
 * @author ta.ito
 */
public interface TreeCellInterface {

    long getHierarchyId();
    String getName();
    long getChildCount();
    void setChildCount(long count);
    Object getEntity();
}
