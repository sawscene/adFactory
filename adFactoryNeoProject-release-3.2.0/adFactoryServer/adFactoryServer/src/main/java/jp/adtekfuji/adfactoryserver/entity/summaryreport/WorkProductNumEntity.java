package jp.adtekfuji.adfactoryserver.entity.summaryreport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class WorkProductNumEntity {
    @Id
    @Column(name="work_name")
    public String workName;

    @Column(name="number")
    public Long number;

    public WorkProductNumEntity() {}
    public WorkProductNumEntity(String workName, Long number) {
        this.workName = workName;
        this.number = number;
    }
}
