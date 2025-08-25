package jp.adtekfuji.adfactoryserver.entity.summaryreport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public
class CallRankingEntity {

    @Id
    @Column(name = "reason")
    public String reason;

    @Column(name = "count")
    public Long count;

    public CallRankingEntity() {

    }
}

