/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.utils;

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
import jp.adtekfuji.adFactory.enumerate.ProductionTypeEnum;
import org.apache.commons.lang3.time.DateUtils;

/**
 * カンバン生成設定情報
 *
 * @author e.mori
 * @version 1.6.1
 * @since 2017.1.11.Wen
 */
public class KanbanDefaultOffsetData {

    private ObjectProperty<Date> startOffsetTimeProperty;
    private BooleanProperty checkOffsetWorkingHoursProperty;
    private ObjectProperty<Date> openingTimeProperty;
    private ObjectProperty<Date> closingTimeProperty;
    private ObjectProperty<ProductionTypeEnum> productionTypeProperty;

    private IntegerProperty lotQuantityProperty;
    private IntegerProperty sumProperty;

    private Date startOffsetTime;
    private Boolean checkOffsetWorkingHours;
    private Date openingTime;
    private Date closingTime;
    private Boolean checkLotProduction = false;
    private Boolean checkOnePieceFlow = true;
    private ProductionTypeEnum productionType = ProductionTypeEnum.ONE_PIECE;
    private Integer lotQuantity = 1;
    private List<WorkGroup> workGroups = new ArrayList();
    private LinkedList<WorkGroupPropertyData> workGroupProps = new LinkedList<>();

    /**
     *
     */
    public KanbanDefaultOffsetData() {
    }

    /**
     *
     * @param startOffsetTime
     * @param checkOffsetWorkingHours
     * @param openingTime
     * @param closingTime
     */
    public KanbanDefaultOffsetData(Date startOffsetTime, Boolean checkOffsetWorkingHours, Date openingTime, Date closingTime) {
        this.startOffsetTime = startOffsetTime;
        this.checkOffsetWorkingHours = checkOffsetWorkingHours;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<Date> startOffsetTimeProperty() {
        if (Objects.isNull(this.startOffsetTimeProperty)) {
            this.startOffsetTimeProperty = new SimpleObjectProperty<>(this.startOffsetTime);
        }
        return this.startOffsetTimeProperty;
    }

    /**
     *
     * @return
     */
    public BooleanProperty checkOffsetWorkingHoursProperty() {
        if (Objects.isNull(this.checkOffsetWorkingHoursProperty)) {
            this.checkOffsetWorkingHoursProperty = new SimpleBooleanProperty(this.checkOffsetWorkingHours);
        }
        return this.checkOffsetWorkingHoursProperty;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<Date> openingTimeProperty() {
        if (Objects.isNull(this.openingTimeProperty)) {
            this.openingTimeProperty = new SimpleObjectProperty<>(this.openingTime);
        }
        return this.openingTimeProperty;
    }

    /**
     *
     * @return
     */
    public ObjectProperty<Date> closingTimeProperty() {
        if (Objects.isNull(this.closingTimeProperty)) {
            this.closingTimeProperty = new SimpleObjectProperty<>(this.closingTime);
        }
        return this.closingTimeProperty;
    }

    /**
     *
     * @return
     */
    public boolean isOnePieceLot() {
        return Objects.equals(ProductionTypeEnum.LOT_ONE_PIECE, getProductionType())
                || Objects.equals(ProductionTypeEnum.LOT_ONE_PIECE2, getProductionType());
    }

    /**
     *
     * @return
     */
    public boolean isLot() {
        return Objects.equals(ProductionTypeEnum.LOT_ONE_PIECE, getProductionType())
                || Objects.equals(ProductionTypeEnum.LOT_ONE_PIECE2, getProductionType())
                || Objects.equals(ProductionTypeEnum.LOT, getProductionType());
    }

    /**
     *
     * @return
     */
    public ObjectProperty<ProductionTypeEnum> productionTypeProperty() {
        if (Objects.isNull(this.productionTypeProperty)) {
            this.productionTypeProperty = new SimpleObjectProperty<>(this.productionType);
        }
        return this.productionTypeProperty;
    }

    /**
     *
     * @return
     */
    public ProductionTypeEnum getProductionType() {
        if (Objects.nonNull(this.productionTypeProperty)) {
            return this.productionTypeProperty.get();
        }
        return this.productionType;
    }

    /**
     *
     * @return
     */
    public IntegerProperty lotQuantityProperty() {
        if (Objects.isNull(this.lotQuantityProperty)) {
            this.lotQuantityProperty = new SimpleIntegerProperty(this.lotQuantity);
        }
        return this.lotQuantityProperty;
    }

    /**
     *
     * @return
     */
    public IntegerProperty sumProperty() {
        if (Objects.isNull(this.sumProperty)) {
            this.sumProperty = new SimpleIntegerProperty(0);
        }
        return this.sumProperty;
    }

    /**
     *
     * @return
     */
    public Date getStartOffsetTime() {
        if (Objects.nonNull(this.startOffsetTimeProperty)) {
            return DateUtils.setMilliseconds(this.startOffsetTimeProperty.get(), 0);
        }
        return Objects.nonNull(this.startOffsetTime) ? DateUtils.setMilliseconds(this.startOffsetTime, 0) : null;
    }

    /**
     *
     * @param startOffsetTime
     */
    public void setStartOffsetTime(Date startOffsetTime) {
        if (Objects.nonNull(this.startOffsetTimeProperty)) {
            this.startOffsetTimeProperty.set(startOffsetTime);
        } else {
            this.startOffsetTime = startOffsetTime;
        }
    }

    /**
     *
     */
    public Boolean getCheckOffsetWorkingHours() {
        if (Objects.nonNull(this.checkOffsetWorkingHoursProperty)) {
            return this.checkOffsetWorkingHoursProperty.get();
        }
        return this.checkOffsetWorkingHours;
    }

    /**
     *
     * @param checkOffsetWorkingHours
     */
    public void setCheckOffsetWorkingHours(Boolean checkOffsetWorkingHours) {
        if (Objects.nonNull(this.checkOffsetWorkingHoursProperty)) {
            this.checkOffsetWorkingHoursProperty.set(checkOffsetWorkingHours);
        } else {
            this.checkOffsetWorkingHours = checkOffsetWorkingHours;
        }
    }

    /**
     *
     * @return
     */
    public Date getOpeningTime() {
        if (Objects.nonNull(this.openingTimeProperty)) {
            return this.openingTimeProperty.get();
        }
        return this.openingTime;
    }

    /**
     *
     * @param openingTime
     */
    public void setOpeningTime(Date openingTime) {
        if (Objects.nonNull(this.openingTimeProperty)) {
            this.openingTimeProperty.set(openingTime);
        } else {
            this.openingTime = openingTime;
        }
    }

    /**
     *
     * @return
     */
    public Date getClosingTime() {
        if (Objects.nonNull(this.closingTimeProperty)) {
            return this.closingTimeProperty.get();
        }
        return this.closingTime;
    }

    /**
     *
     * @param closingTime
     */
    public void setClosingTime(Date closingTime) {
        if (Objects.nonNull(this.closingTimeProperty)) {
            this.closingTimeProperty.set(closingTime);
        } else {
            this.closingTime = closingTime;
        }
    }

    /**
     *
     * @return
     */
    public Integer getLotQuantity() {
        if (Objects.nonNull(this.lotQuantityProperty)) {
            return this.lotQuantityProperty.get();
        }
        return this.lotQuantity;
    }

    /**
     *
     * @param lotQuantity
     */
    public void setLotQuantity(Integer lotQuantity) {
        if (Objects.nonNull(this.lotQuantityProperty)) {
            this.lotQuantityProperty.set(lotQuantity);
        } else {
            this.lotQuantity = lotQuantity;
        }
    }

    /**
     *
     * @return
     */
    public List<WorkGroup> getWorkGroups() {
        return this.workGroups;
    }

    /**
     *
     * @param workGroups
     */
    public void setWorkGroups(List<WorkGroup> workGroups) {
        this.workGroups = workGroups;
    }

    /**
     *
     * @return
     */
    public LinkedList<WorkGroupPropertyData> getWorkGroupProps() {
        return this.workGroupProps;
    }

    /**
     *
     * @param workGroups
     */
    public void setWorkGroupProps(LinkedList<WorkGroupPropertyData> workGroups) {
        this.workGroupProps = workGroups;
    }
}
