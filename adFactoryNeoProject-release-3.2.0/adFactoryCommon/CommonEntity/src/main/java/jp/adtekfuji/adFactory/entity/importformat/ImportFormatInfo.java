/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.importformat;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * インポートフォーマット設定
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "importFormatInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportFormatInfo {

    private KanbanFormatInfo kanbanFormatInfo;
    private KanbanPropFormatInfo kanbanPropFormatInfo;
    private WorkKanbanFormatInfo workKanbanFormatInfo;
    private WorkKanbanPropFormatInfo workKanbanPropFormatInfo;
    private KanbanStatusFormatInfo kanbanStatusFormatInfo;
    private ProductFormatInfo productFormatInfo;
    private HolidayFormatInfo holidayFormatInfo;
    private WorkKanbanPropFormatInfo updateWorkKanbanPropFormatInfo;

    /**
     * 複数のファイルから工程カンバンプロパティを読み込む時に使用する情報
     */
    @XmlElementWrapper(name = "workKanbanPropFormats")
    @XmlElement(name = "workKanbanPropFormatInfo")
    private List<WorkKanbanPropFormatInfo> workKanbanPropFormats = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public ImportFormatInfo() {
    }

    /**
     * カンバンのフォーマット情報を取得する。
     *
     * @return カンバンのフォーマット情報
     */
    public KanbanFormatInfo getKanbanFormatInfo() {
        return this.kanbanFormatInfo;
    }

    /**
     * カンバンのフォーマット情報を設定する。
     *
     * @param kanbanFormatInfo カンバンのフォーマット情報
     */
    public void setKanbanFormatInfo(KanbanFormatInfo kanbanFormatInfo) {
        this.kanbanFormatInfo = kanbanFormatInfo;
    }

    /**
     * カンバンプロパティのフォーマット情報を取得する。
     *
     * @return カンバンプロパティのフォーマット情報
     */
    public KanbanPropFormatInfo getKanbanPropFormatInfo() {
        return this.kanbanPropFormatInfo;
    }

    /**
     * カンバンプロパティのフォーマット情報を設定する。
     *
     * @param kanbanPropFormatInfo カンバンプロパティのフォーマット情報
     */
    public void setKanbanPropFormatInfo(KanbanPropFormatInfo kanbanPropFormatInfo) {
        this.kanbanPropFormatInfo = kanbanPropFormatInfo;
    }

    /**
     * 工程カンバンのフォーマット情報を取得する。
     *
     * @return 工程カンバンのフォーマット情報
     */
    public WorkKanbanFormatInfo getWorkKanbanFormatInfo() {
        return this.workKanbanFormatInfo;
    }

    /**
     * 工程カンバンのフォーマット情報を設定する。
     *
     * @param workKanbanFormatInfo 工程カンバンのフォーマット情報
     */
    public void setWorkKanbanFormatInfo(WorkKanbanFormatInfo workKanbanFormatInfo) {
        this.workKanbanFormatInfo = workKanbanFormatInfo;
    }

    /**
     * 工程カンバンプロパティのフォーマット情報を取得する。
     *
     * @return 工程カンバンプロパティのフォーマット情報
     */
    public WorkKanbanPropFormatInfo getWorkKanbanPropFormatInfo() {
        return this.workKanbanPropFormatInfo;
    }

    /**
     * 工程カンバンプロパティのフォーマット情報を設定する。
     *
     * @param workKanbanPropFormatInfo 工程カンバンプロパティのフォーマット情報
     */
    public void setWorkKanbanPropFormatInfo(WorkKanbanPropFormatInfo workKanbanPropFormatInfo) {
        this.workKanbanPropFormatInfo = workKanbanPropFormatInfo;
    }

    /**
     * 工程カンバンプロパティのフォーマット情報を取得する。
     *
     * @return
     */
    public List<WorkKanbanPropFormatInfo> getWorkKanbanPropFormats() {
        return this.workKanbanPropFormats;
    }

    /**
     * カンバンステータスのフォーマット情報を取得する。
     *
     * @return カンバンステータスのフォーマット情報
     */
    public KanbanStatusFormatInfo getKanbanStatusFormatInfo() {
        return this.kanbanStatusFormatInfo;
    }

    /**
     * カンバンステータスのフォーマット情報を設定する。
     *
     * @param kanbanStatusFormatInfo カンバンステータスのフォーマット情報
     */
    public void setKanbanStatusFormatInfo(KanbanStatusFormatInfo kanbanStatusFormatInfo) {
        this.kanbanStatusFormatInfo = kanbanStatusFormatInfo;
    }

    /**
     * 製品のフォーマット情報を取得する。
     *
     * @return 製品のフォーマット情報
     */
    public ProductFormatInfo getProductFormatInfo() {
        return this.productFormatInfo;
    }

    /**
     * 製品のフォーマット情報を設定する。
     *
     * @param productFormatInfo 製品のフォーマット情報
     */
    public void setProductFormatInfo(ProductFormatInfo productFormatInfo) {
        this.productFormatInfo = productFormatInfo;
    }

    /**
     * 休日のフォーマット情報を取得する。
     *
     * @return 休日のフォーマット情報
     */
    public HolidayFormatInfo getHolidayFormatInfo() {
        return this.holidayFormatInfo;
    }

    /**
     * 休日のフォーマット情報を設定する。
     *
     * @param holidayFormatInfo 休日のフォーマット情報
     */
    public void setHolidayFormatInfo(HolidayFormatInfo holidayFormatInfo) {
        this.holidayFormatInfo = holidayFormatInfo;
    }

    /**
     *
     * @return
     */
    public WorkKanbanPropFormatInfo getUpdateWorkKanbanPropFormatInfo() {
        return this.updateWorkKanbanPropFormatInfo;
    }

    /**
     *
     * @param updateWorkKanbanPropFormatInfo
     */
    public void setUpdateWorkKanbanPropFormatInfo(WorkKanbanPropFormatInfo updateWorkKanbanPropFormatInfo) {
        this.updateWorkKanbanPropFormatInfo = updateWorkKanbanPropFormatInfo;
    }

    @Override
    public String toString() {
        return new StringBuilder("ImportFormatInfo{")
                .append("kanbanFormatInfo=").append(this.kanbanFormatInfo)
                .append(", kanbanPropFormatInfo=").append(this.kanbanPropFormatInfo)
                .append(", workKanbanFormatInfo=").append(this.workKanbanFormatInfo)
                .append(", workKanbanPropFormatInfo=").append(this.workKanbanPropFormatInfo)
                .append(", kanbanStatusFormatInfo=").append(this.kanbanStatusFormatInfo)
                .append(", productFormatInfo=").append(this.productFormatInfo)
                .append(", holidayFormatInfo=").append(this.holidayFormatInfo)
                .append("}")
                .toString();
    }
}
