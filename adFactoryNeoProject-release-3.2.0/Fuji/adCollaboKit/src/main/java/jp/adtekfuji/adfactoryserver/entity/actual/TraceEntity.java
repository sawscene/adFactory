/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.actual;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author s-heya
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "trace")
@Entity
@Table(name = "view_trace")
@NamedQueries({
    @NamedQuery(name = "TraceEntity.countByTagName", query = "SELECT COUNT(t.id) FROM TraceEntity t WHERE (t.dateTime >= :fromDate AND t.dateTime <= :toDate) AND t.tagName IN :tagNames"),
    @NamedQuery(name = "TraceEntity.findByTagName", query = "SELECT t FROM TraceEntity t WHERE (t.dateTime >= :fromDate AND t.dateTime <= :toDate) AND t.tagName IN :tagNames ORDER BY t.id"),
})
public class TraceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;
    @Size(max = 256)
    @Column(name = "work_name")
    private String workName;
    @Size(max = 256)
    @Column(name = "tag_name")
    private String tagName;
    @Column(name = "date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;
    @Column(name = "trace_value")
    private String traceValue;

    public TraceEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTraceValue() {
        return traceValue;
    }

    public void setTraceValue(String traceValue) {
        this.traceValue = traceValue;
    }

}
