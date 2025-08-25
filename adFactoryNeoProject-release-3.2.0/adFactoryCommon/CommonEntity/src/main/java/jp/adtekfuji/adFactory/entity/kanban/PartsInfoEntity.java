/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 完成品情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "parts")
@JsonIgnoreProperties(ignoreUnknown=true)
public class PartsInfoEntity implements Serializable {

    @XmlElement()
    private String partsId;// パーツID

    @XmlElement()
    private String serialNoInfo;// シリアル番号情報(JSON)

    @XmlElement()
    private Long workKanbanId;// 製造元工程カンバンID

    @XmlElement()
    private Date compDatetime;// 製造日

    @XmlElement()
    private Long destWorkKanbanId;// 供給先工程カンバンID

    @XmlElement()
    private Boolean removeFlag = false;// 論理削除フラグ

    @XmlTransient
    @JsonIgnore
    private List<SerialNoInfo> serialNoInfos = null;// シリアル番号情報一覧

    /**
     * コンストラクタ
     */
    public PartsInfoEntity() {
    }

    /**
     * パーツIDを取得する。
     *
     * @return パーツID
     */
    public String getPartsId() {
        return this.partsId;
    }

    /**
     * パーツIDを設定する。
     *
     * @param partsId パーツID
     */
    public void setPartsId(String partsId) {
        this.partsId = partsId;
    }

    /**
     * シリアル番号情報(JSON)を取得する。
     *
     * @return シリアル番号情報(JSON)
     */
    public String getSerialNoInfo() {
        return this.serialNoInfo;
    }

    /**
     * シリアル番号情報(JSON)を設定する。
     *
     * @param serialNoInfo シリアル番号情報(JSON)
     */
    public void setSerialNoInfo(String serialNoInfo) {
        this.serialNoInfo = serialNoInfo;
    }

    /**
     * シリアル番号情報一覧を取得する。
     *
     * @return シリアル番号情報一覧
     */
    public List<SerialNoInfo> getSerialNoInfos() {
        // リストがnullの場合、シリアル番号情報のJSON文字列をシリアル番号情報一覧に変換してセットする。
        if (Objects.isNull(this.serialNoInfos)){
            this.serialNoInfos = JsonUtils.jsonToObjects(this.getSerialNoInfo(), SerialNoInfo[].class);
        }
        return this.serialNoInfos;
    }

    /**
     * 製造元工程カンバンIDを取得する。
     *
     * @return 製造元工程カンバンID
     */
    public Long getWorkKanbanId() {
        return this.workKanbanId;
    }

    /**
     * 製造元工程カンバンIDを設定する。
     *
     * @param workKanbanId 製造元工程カンバンID
     */
    public void setWorkKanbanId(Long workKanbanId) {
        this.workKanbanId = workKanbanId;
    }

    /**
     * 製造日を取得する。
     *
     * @return 製造日
     */
    public Date getCompDatetime() {
        return this.compDatetime;
    }

    /**
     * 製造日を設定する。
     *
     * @param compDatetime 製造日
     */
    public void setCompDatetime(Date compDatetime) {
        this.compDatetime = compDatetime;
    }

    /**
     * 供給先工程カンバンIDを取得する。
     *
     * @return 供給先工程カンバンID
     */
    public Long getDestWorkKanbanId() {
        return this.destWorkKanbanId;
    }

    /**
     * 供給先工程カンバンIDを設定する。
     *
     * @param destWorkKanbanId 供給先工程カンバンID
     */
    public void setDestWorkKanbanId(Long destWorkKanbanId) {
        this.destWorkKanbanId = destWorkKanbanId;
    }

    /**
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.partsId);
        hash = 53 * hash + Objects.hashCode(this.workKanbanId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PartsInfoEntity other = (PartsInfoEntity) obj;
        if (!Objects.equals(this.partsId, other.partsId)) {
            return false;
        }
        if (!Objects.equals(this.workKanbanId, other.workKanbanId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("PartsInfoEntity{")
                .append("partsId=").append(this.partsId)
                .append(", workKanbanId=").append(this.workKanbanId)
                .append(", compDatetime=").append(this.compDatetime)
                .append(", destWorkKanbanId=").append(this.destWorkKanbanId)
                .append(", removeFlag=").append(this.removeFlag)
                .append("}")
                .toString();
    }
}
