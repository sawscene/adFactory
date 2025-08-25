/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.assemblyparts;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 使用部品更新要求
 *
 * @author y-harada
 */
@XmlRootElement(name = "assemblyPartsUpdateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssemblyPartsUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");

    @XmlElementWrapper(name = "pids")
    @XmlElement(name = "string")
    private String[] pids;

    @XmlElementWrapper(name = "assembleds")
    @XmlElement(name = "boolean")
    private Boolean[] assembleds;

    @XmlElementWrapper(name = "verInfos")
    @XmlElement(name = "integer")
    private Integer[] verInfos;

    @XmlElement()
    private String assembledDateStr;

    /**
     * コンストラクタ
     */
    public AssemblyPartsUpdateRequest() {
    }

    /**
     * PID一覧を取得する。
     *
     * @return PID一覧
     */
    public String[] getPids() {
        return this.pids;
    }

    /**
     * PID一覧を設定する。
     *
     * @param pids PID一覧
     */
    public void setPids(String[] pids) {
        this.pids = pids;
    }

    /**
     * 使用フラグ一覧を取得する。
     *
     * @return 使用フラグ一覧
     */
    public Boolean[] getAssembleds() {
        return this.assembleds;
    }

    /**
     * 使用フラグ一覧を設定する。
     *
     * @param assembleds 使用フラグ一覧
     */
    public void setAssembleds(Boolean[] assembleds) {
        this.assembleds = assembleds;
    }

    /**
     * バージョン情報一覧を取得する。 ;
     *
     * @return バージョン情報一覧
     */
    public Integer[] getVerInfos() {
        return this.verInfos;
    }

    /**
     * バージョン情報一覧を設定する。
     *
     * @param verInfos バージョン情報一覧
     */
    public void setVerInfos(Integer[] verInfos) {
        this.verInfos = verInfos;
    }

    /**
     * 使用確定日時を取得する。
     *
     * @return 使用確定日時
     * @throws java.lang.Exception
     */
    public Date getAssembledDate() throws Exception {
        return sdf.parse(assembledDateStr);
    }

    /**
     * 使用確定日時を設定する。
     *
     * @param assembledDate 使用確定日時
     */
    public void setAssembledDate(Date assembledDate) {
        this.assembledDateStr = sdf.format(assembledDate);
    }

    @Override
    public String toString() {
        return new StringBuilder("PlanChangeCondition{")
                .append("pids=").append(this.pids)
                .append(", assembleds=").append(this.assembleds)
                .append(", verInfos=").append(this.verInfos)
                .append("}")
                .toString();
    }
}
