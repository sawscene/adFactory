/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.master;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 休憩マスタ
 *
 * @author ke.yokoi
 */
@Entity
@Table(name = "mst_breaktime")
@XmlRootElement(name = "breaktime")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // 追加時の名前重複チェック
    @NamedQuery(name = "BreaktimeEntity.checkAddByName", query = "SELECT COUNT(b.breaktimeId) FROM BreaktimeEntity b WHERE b.breaktimeName = :breaktimeName"),
    // 更新時の名前重複チェック
    @NamedQuery(name = "BreaktimeEntity.checkUpdateByName", query = "SELECT COUNT(b.breaktimeId) FROM BreaktimeEntity b WHERE b.breaktimeName = :breaktimeName AND b.breaktimeId != :breaktimeId"),
    // 休憩IDを指定して、休憩情報を取得する。
    @NamedQuery(name = "BreaktimeEntity.findByBreaktimeId", query = "SELECT b FROM BreaktimeEntity b WHERE b.breaktimeId IN :breaktimeIds ORDER BY b.starttime"),
    // 組織IDにて休憩情報を取得
    @NamedQuery(name ="BreaktimeEntity.findByOrganizationId", query = "SELECT b FROM BreaktimeEntity b, ConOrganizationBreaktimeEntity cob WHERE cob.organizationId = :organizationId AND cob.breaktimeId = b.breaktimeId")
})
public class BreaktimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "breaktime_id")
    private Long breaktimeId;// 休憩ID

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 256)
    @Column(name = "breaktime_name")
    private String breaktimeName;// 休憩名称

    @Basic(optional = false)
    //@NotNull
    @Column(name = "starttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date starttime;// 開始時間

    @Basic(optional = false)
    //@NotNull
    @Column(name = "endtime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endtime;// 終了時間

    //@NotNull
    @Column(name = "ver_info")
    @Version
    private Integer verInfo = 1;// 排他用バーション

    /**
     * コンストラクタ
     */
    public BreaktimeEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param breaktimeName 休憩名称
     * @param starttime 開始時間
     * @param endtime 終了時間
     */
    public BreaktimeEntity(String breaktimeName, Date starttime, Date endtime) {
        this.breaktimeName = breaktimeName;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    /**
     * 休憩IDを取得する。
     *
     * @return 休憩ID
     */
    public Long getBreaktimeId() {
        return this.breaktimeId;
    }

    /**
     * 休憩IDを設定する。
     *
     * @param breaktimeId 休憩ID
     */
    public void setBreaktimeId(Long breaktimeId) {
        this.breaktimeId = breaktimeId;
    }

    /**
     * 休憩名称を取得する。
     *
     * @return 休憩名称
     */
    public String getBreaktimeName() {
        return this.breaktimeName;
    }

    /**
     * 休憩名称を設定する。
     *
     * @param breaktimeName 休憩名称
     */
    public void setBreaktimeName(String breaktimeName) {
        this.breaktimeName = breaktimeName;
    }

    /**
     * 開始時間を取得する。
     *
     * @return 開始時間
     */
    public Date getStarttime() {
        return this.starttime;
    }

    /**
     * 開始時間を設定する。
     *
     * @param starttime 開始時間
     */
    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    /**
     * 終了時間を取得する。
     *
     * @return 終了時間
     */
    public Date getEndtime() {
        return this.endtime;
    }

    /**
     * 終了時間を設定する。
     *
     * @param endtime 終了時間
     */
    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.breaktimeId);
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
        final BreaktimeEntity other = (BreaktimeEntity) obj;
        if (!Objects.equals(this.breaktimeId, other.breaktimeId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("BreaktimeEntity{")
                .append("breaktimeId=").append(this.breaktimeId)
                .append(", ")
                .append("breaktimeName=").append(this.breaktimeName)
                .append(", ")
                .append("starttime=").append(this.starttime)
                .append(", ")
                .append("endtime=").append(this.endtime)
                .append(", ")
                .append("verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }
}
