/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import jp.adtekfuji.adFactory.entity.kanban.WorkGroup;
import org.apache.commons.lang.time.DateUtils;

/**
 * カンバン生成設定情報
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class WorkPlanKanbanDefaultOffsetData {

    private ObjectProperty<Date> startOffsetTimeProperty;
    private BooleanProperty checkOffsetWorkingHoursProperty;
    private ObjectProperty<Date> openingTimeProperty;
    private ObjectProperty<Date> closingTimeProperty;
    private BooleanProperty checkLotProductionProperty;
    private BooleanProperty checkOnePieceFlowProperty;
    private IntegerProperty lotQuantityProperty;
    private IntegerProperty sumProperty;

    private Date startOffsetTime;
    private Boolean checkOffsetWorkingHours;
    private Date openingTime;
    private Date closingTime;
    private Boolean checkLotProduction = false;
    private Boolean checkOnePieceFlow = true;
    private Integer lotQuantity = 1;
    private List<WorkGroup> workGroups = new ArrayList();
    private LinkedList<WorkPlanWorkGroupPropertyData> workGroupProps = new LinkedList<>();

    public WorkPlanKanbanDefaultOffsetData() {
    }

    public WorkPlanKanbanDefaultOffsetData(Date startOffsetTime, Boolean checkOffsetWorkingHours, Date openingTime, Date closingTime) {
        this.startOffsetTime = startOffsetTime;
        this.checkOffsetWorkingHours = checkOffsetWorkingHours;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public ObjectProperty<Date> startOffsetTimeProperty() {
        if (Objects.isNull(startOffsetTimeProperty)) {
            startOffsetTimeProperty = new SimpleObjectProperty<>(startOffsetTime);
        }
        return startOffsetTimeProperty;
    }

    public BooleanProperty checkOffsetWorkingHoursProperty() {
        if (Objects.isNull(checkOffsetWorkingHoursProperty)) {
            checkOffsetWorkingHoursProperty = new SimpleBooleanProperty(checkOffsetWorkingHours);
        }
        return checkOffsetWorkingHoursProperty;
    }

    public ObjectProperty<Date> openingTimeProperty() {
        if (Objects.isNull(openingTimeProperty)) {
            openingTimeProperty = new SimpleObjectProperty<>(openingTime);
        }
        return openingTimeProperty;
    }

    public ObjectProperty<Date> closingTimeProperty() {
        if (Objects.isNull(closingTimeProperty)) {
            closingTimeProperty = new SimpleObjectProperty<>(closingTime);
        }
        return closingTimeProperty;
    }

    public BooleanProperty checkLotProductionProperty() {
        if (Objects.isNull(checkLotProductionProperty)) {
            checkLotProductionProperty = new SimpleBooleanProperty(checkLotProduction);
        }
        return checkLotProductionProperty;
    }

    public BooleanProperty checkOnePieceFlowProperty() {
        if (Objects.isNull(checkOnePieceFlowProperty)) {
            checkOnePieceFlowProperty = new SimpleBooleanProperty(checkOnePieceFlow);
        }
        return checkOnePieceFlowProperty;
    }

    public IntegerProperty lotQuantityProperty() {
        if (Objects.isNull(lotQuantityProperty)) {
            lotQuantityProperty = new SimpleIntegerProperty(lotQuantity);
        }
        return lotQuantityProperty;
    }

    public IntegerProperty sumProperty() {
        if (Objects.isNull(this.sumProperty)) {
            this.sumProperty = new SimpleIntegerProperty(0);
        }
        return sumProperty;
    }

    public Date getStartOffsetTime() {
        if (Objects.nonNull(startOffsetTimeProperty)) {
            return DateUtils.setMilliseconds(startOffsetTimeProperty.get(), 0);
        }
        return Objects.nonNull(startOffsetTime) ? DateUtils.setMilliseconds(startOffsetTime, 0) : null;
    }

    public void setStartOffsetTime(Date startOffsetTime) {
        if (Objects.nonNull(startOffsetTimeProperty)) {
            startOffsetTimeProperty.set(startOffsetTime);
        } else {
            this.startOffsetTime = startOffsetTime;
        }
    }

    public Boolean getCheckOffsetWorkingHours() {
        if (Objects.nonNull(checkOffsetWorkingHoursProperty)) {
            return checkOffsetWorkingHoursProperty.get();
        }
        return checkOffsetWorkingHours;
    }

    public void setCheckOffsetWorkingHours(Boolean checkOffsetWorkingHours) {
        if (Objects.nonNull(checkOffsetWorkingHoursProperty)) {
            checkOffsetWorkingHoursProperty.set(checkOffsetWorkingHours);
        } else {
            this.checkOffsetWorkingHours = checkOffsetWorkingHours;
        }
    }

    public Date getOpeningTime() {
        if (Objects.nonNull(openingTimeProperty)) {
            return openingTimeProperty.get();
        }
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        if (Objects.nonNull(openingTimeProperty)) {
            openingTimeProperty.set(openingTime);
        } else {
            this.openingTime = openingTime;
        }
    }

    public Date getClosingTime() {
        if (Objects.nonNull(closingTimeProperty)) {
            return closingTimeProperty.get();
        }
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        if (Objects.nonNull(closingTimeProperty)) {
            closingTimeProperty.set(closingTime);
        } else {
            this.closingTime = closingTime;
        }
    }

    public Boolean getCheckLotProduction() {
        if (Objects.nonNull(checkLotProductionProperty)) {
            return checkLotProductionProperty.get();
        }
        return checkLotProduction;
    }

    public void setCheckLotProduction(Boolean checkLotProduction) {
        if (Objects.nonNull(checkLotProductionProperty)) {
            checkLotProductionProperty.set(checkLotProduction);
        } else {
            this.checkLotProduction = checkLotProduction;
        }
    }

    public Boolean getCheckOnePieceFlow() {
        if (Objects.nonNull(checkOnePieceFlowProperty)) {
            return checkOnePieceFlowProperty.get();
        }
        return checkOnePieceFlow;
    }

    public void setCheckOnePieceFlow(Boolean checkOnePieceFlow) {
        if (Objects.nonNull(checkOnePieceFlowProperty)) {
            checkOnePieceFlowProperty.set(checkOnePieceFlow);
        } else {
            this.checkOnePieceFlow = checkOnePieceFlow;
        }
    }

    public Integer getLotQuantity() {
        if (Objects.nonNull(lotQuantityProperty)) {
            return lotQuantityProperty.get();
        }
        return lotQuantity;
    }

    public void setLotQuantity(Integer lotQuantity) {
        if (Objects.nonNull(lotQuantityProperty)) {
            lotQuantityProperty.set(lotQuantity);
        } else {
            this.lotQuantity = lotQuantity;
        }
    }

    public List<WorkGroup> getWorkGroups() {
        return workGroups;
    }

    public void setWorkGroups(List<WorkGroup> workGroups) {
        this.workGroups = workGroups;
    }

    public LinkedList<WorkPlanWorkGroupPropertyData> getWorkGroupProps() {
        return workGroupProps;
    }

    public void setWorkGroupProps(LinkedList<WorkPlanWorkGroupPropertyData> workGroups) {
        this.workGroupProps = workGroups;
    }
}
