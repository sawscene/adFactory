/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model;

import adtekfuji.utility.DateUtils;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jp.adtekfuji.adfactoryserver.entity.actual.ProductionPerformanceEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * パフォーマンスDBアクセス
 * 
 * @author s-heya
 */
@Singleton
public class PerformanceModel {

    private final Logger logger = LogManager.getLogger();

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    /**
     * 出来高を集計する。
     * 
     * @param fromDate
     * @param toDate
     * @param isProdctionNumber
     * @return 
     */
    @Lock(LockType.READ)
    @ExecutionTimeLogging
    public List<ProductionPerformanceEntity> sumPerformance(Date fromDate, Date toDate, boolean isProdctionNumber) {
        if (Objects.isNull(fromDate)) {
            fromDate = new Date();
        }
        
        if (Objects.isNull(toDate)) {
            toDate = new Date();
        }
        
        Date fromDateTime = DateUtils.getBeginningOfDate(fromDate);
        Date toDateTime = DateUtils.getEndOfDate(toDate);

        Query query;
        if (isProdctionNumber) {
            query = em.createNamedQuery("ProductionPerformanceEntity.sumByModelNameAndProductionNumber");
        } else {
            query = em.createNamedQuery("ProductionPerformanceEntity.sumByModelName");
        }
        query.setParameter(1, fromDateTime, TemporalType.TIMESTAMP);
        query.setParameter(2, toDateTime, TemporalType.TIMESTAMP);

        return query.getResultList();
    }
}
