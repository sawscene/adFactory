/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.javafx.tree.entity;

import jp.adtekfuji.forfujiapp.javafx.tree.cell.TreeCellInterface;
import jp.adtekfuji.forfujiapp.entity.unittemplate.UnitTemplateInfoEntity;

/**
 * ユニットテンプレートツリー用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.10.31.Mon
 */
public class UnitTemplateTreeEntity implements TreeCellInterface {
    
    private UnitTemplateInfoEntity entity = new UnitTemplateInfoEntity();

    public UnitTemplateTreeEntity(UnitTemplateInfoEntity unitTemplateInfoEntity) {
        this.entity = unitTemplateInfoEntity;
    }
    
    @Override
    public String getName() {
        return this.entity.getUnitTemplateName();
    }

    @Override
    public Long getHierarchyId() {
        return this.entity.getParentId();
    }

    @Override
    public Object getEntity() {
        return this.entity;
    }

    @Override
    public Boolean isHierarchy() {
        return false;
    }
    
}
