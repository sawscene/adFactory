/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.kanban;

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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.enumerate.ReportTypeEnum;

/**
 * カンバン帳票情報
 *
 * @author nar-nakamura
 */
@Entity
@Table(name = "trn_kanban_report")
@XmlRootElement(name = "kanbanReport")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    // カンバンID一覧を指定して、件数を取得する。
    @NamedQuery(name = "KanbanReportEntity.countByKanbanIds", query = "SELECT COUNT(r.kanbanReportId) FROM KanbanReportEntity r WHERE r.kanbanId IN :kanbanIds"),
    // カンバンID一覧を指定して、カンバン帳票情報一覧を取得する。
    @NamedQuery(name = "KanbanReportEntity.findByKanbanIds", query = "SELECT NEW jp.adtekfuji.adfactoryserver.entity.kanban.KanbanReportEntity(r, k.kanbanName) FROM KanbanReportEntity r LEFT JOIN KanbanEntity k ON k.kanbanId = r.kanbanId WHERE r.kanbanId IN :kanbanIds ORDER BY r.kanbanReportId"),
    // カンバンID一覧を指定して、カンバン帳票情報一覧を削除する。
    @NamedQuery(name = "KanbanReportEntity.removeByKanbanIds", query = "DELETE FROM KanbanReportEntity r WHERE r.kanbanId IN :kanbanIds"),
    // ファイルパスを指定して、カンバン帳票情報を削除する。
    @NamedQuery(name = "KanbanReportEntity.removeByFilePath", query = "DELETE FROM KanbanReportEntity r WHERE r.filePath = :filePath"),
})
public class KanbanReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kanban_report_id")
    private Long kanbanReportId;// カンバン帳票ID

    //@NotNull
    @Column(name = "kanban_id")
    private Long kanbanId;// カンバンID

    @Size(max = 256)
    @Column(name = "template_name")
    private String templateName;// テンプレートファイル名

    @Column(name = "output_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date outputDate;// 出力日時

    @Column(name = "file_path")
    private String filePath;// ファイルパス

    @Column(name = "report_type")
    private ReportTypeEnum reportType;// 帳票種別

    @Transient
    private String kanbanName;// カンバン名

    /**
     * コンストラクタ
     */
    public KanbanReportEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param kanbanReport カンバン帳票情報
     * @param kanbanName カンバン名
     */
    public KanbanReportEntity(KanbanReportEntity kanbanReport, String kanbanName) {
        this.kanbanReportId = kanbanReport.getKanbanReportId();
        this.kanbanId = kanbanReport.getKanbanId();
        this.templateName = kanbanReport.getTemplateName();
        this.outputDate = kanbanReport.getOutputDate();
        this.filePath = kanbanReport.getFilePath();
        this.reportType = kanbanReport.getReportType();
        this.kanbanName = kanbanName;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.kanbanReportId);
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
        final KanbanReportEntity other = (KanbanReportEntity) obj;
        if (!Objects.equals(this.kanbanReportId, other.kanbanReportId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("KanbanReportEntity{")
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
