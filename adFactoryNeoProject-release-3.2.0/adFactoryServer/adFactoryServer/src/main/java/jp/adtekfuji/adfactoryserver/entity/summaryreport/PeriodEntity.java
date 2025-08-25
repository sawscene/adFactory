package jp.adtekfuji.adfactoryserver.entity.summaryreport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public
class PeriodEntity {

    @Id
    @Column(name = "id")
    public Long id;

    @Column(name = "startDate")
    public Date startDate;

    @Column(name = "endDate")
    public Date endDate;
}
