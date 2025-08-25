/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.system;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportPropertyEntity;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adfactoryserver.entity.kanban.KanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.kanban.WorkKanbanEntity;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;

/**
 * 障害レポート情報
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "troubleReport")
public class TroubleReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private String reportId;
    @XmlElement()
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportDatetime;
    @XmlElement()
    private EquipmentLoginResult equipmentLogin;
    @XmlElement()
    private OrganizationLoginResult organizationLogin;
    @XmlElement()
    private KanbanEntity kanban;
    @XmlElement()
    private WorkKanbanEntity workKanban;
    @XmlElement()
    private WorkEntity work;
    @XmlElement()
    private String status;
    @XmlElement()
    private String javaSpecVersion;
    @XmlElement()
    private String javaBootClassPath;
    @XmlElementWrapper(name = "traceData")
    @XmlElement(name = "property")
    private List<ActualProductReportPropertyEntity> traceData;
    @XmlElement()
    private String tlogFile;

    /**
     * コンストラクタ
     */
    void TroubleReportEntity() {
    }

    /**
     * レポートIDを取得する。
     *
     * @return レポートID
     */
    public String getReportId() {
        return this.reportId;
    }

    /**
     * レポートIDを設定する。
     *
     * @param reportId レポートID
     */
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    /**
     * レポート日時を取得する。
     *
     * @return レポート日時
     */
    public Date getReportDatetime() {
        return this.reportDatetime;
    }

    /**
     * レポート日時を設定する。
     *
     * @param reportDatetime レポート日時
     */
    public void setReportDatetime(Date reportDatetime) {
        this.reportDatetime = reportDatetime;
    }

    /**
     * 設備ログイン情報を取得する。
     *
     * @return 設備ログイン情報
     */
    public EquipmentLoginResult getEquipmentLogin() {
        return this.equipmentLogin;
    }

    /**
     * 設備ログイン情報を設定する。
     *
     * @param equipmentLogin 設備ログイン情報
     */
    public void setEquipmentLogin(EquipmentLoginResult equipmentLogin) {
        this.equipmentLogin = equipmentLogin;
    }

    /**
     * 組織ログイン情報を取得する。
     *
     * @return 組織ログイン情報
     */
    public OrganizationLoginResult getOrganizationLogin() {
        return this.organizationLogin;
    }

    /**
     * 組織ログイン情報を設定する。
     *
     * @param organizationLogin 組織ログイン情報
     */
    public void setOrganizationLogin(OrganizationLoginResult organizationLogin) {
        this.organizationLogin = organizationLogin;
    }

    /**
     * カンバン情報を取得する。
     *
     * @return カンバン情報
     */
    public KanbanEntity getKanban() {
        return this.kanban;
    }

    /**
     * カンバン情報を設定する。
     *
     * @param kanban カンバン情報
     */
    public void setKanban(KanbanEntity kanban) {
        this.kanban = kanban;
    }

    /**
     * 工程カンバン情報を取得する。
     *
     * @return 工程カンバン情報
     */
    public WorkKanbanEntity getWorkKanban() {
        return this.workKanban;
    }

    /**
     * 工程カンバン情報を設定する。
     *
     * @param workKanban 工程カンバン情報
     */
    public void setWorkKanban(WorkKanbanEntity workKanban) {
        this.workKanban = workKanban;
    }

    /**
     * 工程情報を取得する。
     *
     * @return 工程情報
     */
    public WorkEntity getWork() {
        return this.work;
    }

    /**
     * 工程情報を設定する。
     *
     * @param work 工程情報
     */
    public void setWork(WorkEntity work) {
        this.work = work;
    }

    /**
     * ステータスを取得する。
     *
     * @return ステータス
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * ステータスを設定する。
     *
     * @param status ステータス
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * JVM仕様のバージョンを取得する。
     *
     * @return JVM仕様のバージョン
     */
    public String getJavaSpecVersion() {
        return this.javaSpecVersion;
    }

    /**
     * JVM仕様のバージョンを設定する。
     *
     * @param javaSpecVersion JVM仕様のバージョン
     */
    public void setJavaSpecVersion(String javaSpecVersion) {
        this.javaSpecVersion = javaSpecVersion;
    }

    /**
     * JVMのブートクラスパスを取得する。
     *
     * @return JVMのブートクラスパス
     */
    public String getJavaBootClassPath() {
        return this.javaBootClassPath;
    }

    /**
     * JVMのブートクラスパスを設定する。
     *
     * @param javaBootClassPath JVMのブートクラスパス
     */
    public void setJavaBootClassPath(String javaBootClassPath) {
        this.javaBootClassPath = javaBootClassPath;
    }

    /**
     * トレーサビリティ情報を取得する。
     *
     * @return トレーサビリティ情報
     */
    public List<ActualProductReportPropertyEntity> getTraceData() {
        return this.traceData;
    }

    /**
     * トレーサビリティ情報を設定する。
     *
     * @param traceData トレーサビリティ情報
     */
    public void setTraceData(List<ActualProductReportPropertyEntity> traceData) {
        this.traceData = traceData;
    }

    /**
     * TLogファイル名を取得する。
     *
     * @return TLogファイル名
     */
    public String getTlogFile() {
        return this.tlogFile;
    }

    /**
     * TLogファイル名を設定する。
     *
     * @param tlogFile TLogファイル名
     */
    public void setTlogFile(String tlogFile) {
        this.tlogFile = tlogFile;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.reportId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TroubleReportEntity other = (TroubleReportEntity) obj;
        if (!Objects.equals(this.reportId, other.reportId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TroubleReportEntity{"
                + "reportId=" + this.reportId
                + ", reportDatetime=" + this.reportDatetime
                + '}';
    }
}
