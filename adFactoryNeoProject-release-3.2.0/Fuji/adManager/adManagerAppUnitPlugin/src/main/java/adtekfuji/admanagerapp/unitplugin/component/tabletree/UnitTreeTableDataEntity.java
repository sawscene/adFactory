/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.unitplugin.component.tabletree;

import adtekfuji.locale.LocaleUtils;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitInfoEntity;
import jp.adtekfuji.forfujiapp.entity.unit.UnitKanbanInfoEntity;

/**
 * ユニットツリーテーブル用エンティティ
 *
 * @author ek.mori
 * @version 1.4.3
 * @since 2016.11.11.Fri
 */
class UnitTreeTableDataEntity {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private SimpleDateFormat formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));

    private Long id = 0l;
    private String name = "";
    private String tamplateName = "";
    private String status = "";
    private String startDate = "2000/01/01 00:00:00";
    private String endDate = "2000/12/31 23:59:59";
    private Object entity = null;

    public UnitTreeTableDataEntity(UnitInfoEntity entity) {
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        this.id = entity.getUnitId();
        this.name = entity.getUnitName();
        if (Objects.nonNull(entity.getUnitTemplateName())) {
            this.tamplateName = entity.getUnitTemplateName();
        }
        this.status = "";
        if (Objects.nonNull(entity.getStartDatetime())) {
            this.startDate = formatter.format(entity.getStartDatetime());
        }
        if (Objects.nonNull(entity.getCompDatetime())) {
            this.endDate = formatter.format(entity.getCompDatetime());
        }
        this.entity = entity;
    }

    public UnitTreeTableDataEntity(KanbanInfoEntity entity) {
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        this.id = entity.getKanbanId();
        this.name = entity.getKanbanName();
        if (Objects.nonNull(entity.getWorkflowName())) {
            this.tamplateName = entity.getWorkflowName();
        }
        this.status = LocaleUtils.getString(entity.getKanbanStatus().getResourceKey());;
        if (Objects.nonNull(entity.getStartDatetime())) {
            this.startDate = formatter.format(entity.getStartDatetime());
        }
        if (Objects.nonNull(entity.getCompDatetime())) {
            this.endDate = formatter.format(entity.getCompDatetime());
        }
        this.entity = entity;
    }

    public UnitTreeTableDataEntity(UnitKanbanInfoEntity entity) {
        formatter = new SimpleDateFormat(LocaleUtils.getString("key.DateTimeFormat"));
        this.id = entity.getKanbanId();
        this.name = entity.getKanbanName();
        if (Objects.nonNull(entity.getWorkflowName())) {
            this.tamplateName = entity.getWorkflowName();
        }
        this.status = LocaleUtils.getString(entity.getKanbanStatus().getResourceKey());;
        if (Objects.nonNull(entity.getStartDatetime())) {
            this.startDate = formatter.format(entity.getStartDatetime());
        }
        if (Objects.nonNull(entity.getCompDatetime())) {
            this.endDate = formatter.format(entity.getCompDatetime());
        }
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public String getTamplateName() {
        return tamplateName;
    }

    public String getStatus() {
        return status;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Object getEntity() {
        return entity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTamplateName(String tamplateName) {
        this.tamplateName = tamplateName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

}
