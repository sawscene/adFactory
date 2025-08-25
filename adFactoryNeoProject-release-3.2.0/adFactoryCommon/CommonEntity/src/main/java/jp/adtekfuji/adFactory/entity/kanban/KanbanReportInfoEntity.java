/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.kanban;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.enumerate.ReportTypeEnum;

/**
 * カンバン帳票情報
 *
 * @author nar-nakamura
 */
@XmlRootElement(name = "kanbanReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class KanbanReportInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private Long kanbanReportId;// カンバン帳票ID

    @XmlElement()
    private Long kanbanId;// カンバンID

    @XmlElement()
    private String templateName;// テンプレートファイル名

    @XmlElement()
    private Date outputDate;// 出力日時

    @XmlElement()
    private String filePath;// ファイルパス

    @XmlElement()
    private ReportTypeEnum reportType;// 帳票種別

    @XmlTransient
    private String kanbanName;// カンバン名
    
    @XmlTransient
    private String fileName;// ファイル名

    /**
     * コンストラクタ
     */
    public KanbanReportInfoEntity() {
    }

    /**
     * カンバン帳票IDを取得する。
     *
     * @return カンバン帳票ID
     */
    public Long getKanbanReportId() {
        return this.kanbanReportId;
    }

    /**
     * カンバン帳票IDを設定する。
     *
     * @param kanbanReportId カンバン帳票ID
     */
    public void setKanbanReportId(Long kanbanReportId) {
        this.kanbanReportId = kanbanReportId;
    }

    /**
     * カンバンIDを取得する。
     *
     * @return カンバンID
     */
    public Long getKanbanId() {
        return this.kanbanId;
    }

    /**
     * カンバンIDを設定する。
     *
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }

    /**
     * テンプレートファイル名を取得する。
     *
     * @return テンプレートファイル名
     */
    public String getTemplateName() {
        return this.templateName;
    }

    /**
     * テンプレートファイル名を設定する。
     *
     * @param templateName テンプレートファイル名
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * 出力日時を取得する。
     *
     * @return 出力日時
     */
    public Date getOutputDate() {
        return this.outputDate;
    }

    /**
     * 出力日時を設定する。
     *
     * @param outputDate 出力日時
     */
    public void setOutputDate(Date outputDate) {
        this.outputDate = outputDate;
    }

    /**
     * ファイルパスを取得する。
     *
     * @return ファイルパス
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * ファイルパスを設定する。
     *
     * @param filePath ファイルパス
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 帳票種別を取得する。
     *
     * @return 帳票種別
     */
    public ReportTypeEnum getReportType() {
        return this.reportType;
    }

    /**
     * 帳票種別を設定する。
     *
     * @param reportType 帳票種別
     */
    public void setReportType(ReportTypeEnum reportType) {
        this.reportType = reportType;
    }

    /**
     * カンバン名を取得する。
     *
     * @return カンバン名
     */
    public String getKanbanName() {
        return this.kanbanName;
    }

    /**
     * カンバン名を設定する。
     *
     * @param kanbanName カンバン名
     */
    public void setKanbanName(String kanbanName) {
        this.kanbanName = kanbanName;
    }
    
    /**
     * ファイル名を取得する。
     *
     * @return ファイル名
     */
    public String getFileName() {
        if (Objects.isNull(fileName) && Objects.nonNull(filePath)) {
            fileName = new File(filePath).getName();
        }
        return this.fileName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.kanbanReportId);
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
        final KanbanReportInfoEntity other = (KanbanReportInfoEntity) obj;
        if (!Objects.equals(this.kanbanReportId, other.kanbanReportId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanReportInfoEntity{")
                .append("kanbanReportId=").append(this.kanbanReportId)
                .append(", kanbanId=").append(this.kanbanId)
                .append(", templateName=").append(this.templateName)
                .append(", outputDate=").append(this.outputDate)
                .append(", filePath=").append(this.filePath)
                .append(", reportType=").append(this.reportType)
                .append("}")
                .toString();
    }
}
