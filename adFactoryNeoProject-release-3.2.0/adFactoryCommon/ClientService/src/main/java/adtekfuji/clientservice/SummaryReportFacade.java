/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.List;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.model.SummaryReport;
import jp.adtekfuji.adFactory.entity.model.SummaryReportCondition;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 統計情報取得用RESTクラス
 *
 * @author kentarou.suzuki
 */
public class SummaryReportFacade {

    /**
     * ログ出力クラス
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * RESTクライアント
     */
    private final RestClient restClient;

    /**
     * パス(リソースクラス)
     */
    private static final String SUMMARY_REPORT_PATH = "/summary-report";

    private static final String SEND_MAIL_PATH = "/send-mail/%d";

    private static final String RELOAD_CONFIG_PATH = "/reload-config";

    private static final String CALCULATE_PATH = "/calculate";
    /**
     * パス(リソースクラスのメソッド)
     */
    private static final String MODEL_PATH = "/model";

    /**
     * コンストラクタ
     */
    public SummaryReportFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 生産活動の統計情報を取得する。
     * 
     * @param condition 集計条件
     * @return 統計情報一覧
     * @throws Exception 
     */
    public List<SummaryReport> getStatistics(SummaryReportCondition condition)  {
        logger.debug("getStatistics:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(SUMMARY_REPORT_PATH);
            sb.append(MODEL_PATH);

            GenericType<List<SummaryReport>> summaryReports = new GenericType<List<SummaryReport>>(){};

            return restClient.post(sb.toString(), condition, summaryReports);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }
    
    public ResponseEntity sendMail(long index) {
        logger.info("sendMail :{}", index);
        StringBuilder sb = new StringBuilder();
        sb.append(SUMMARY_REPORT_PATH);
        sb.append(String.format(SEND_MAIL_PATH, index));

        return (ResponseEntity) restClient.post(sb.toString(), null, ResponseEntity.class);
    }

    public void loadSummaryReportConfig()
    {
        logger.info("loadSummaryReportConfig");
        StringBuilder sb = new StringBuilder();
        sb.append(SUMMARY_REPORT_PATH);
        sb.append(RELOAD_CONFIG_PATH);
        restClient.put(sb.toString(), null);
    }

    public SummaryReportInfoEntity calculate(SummaryReportConfigInfoEntity entity)
    {
        logger.info("calculate");
        StringBuilder sb = new StringBuilder();
        sb.append(SUMMARY_REPORT_PATH);
        sb.append(CALCULATE_PATH);

        return (SummaryReportInfoEntity) restClient.post(sb.toString(), entity, SummaryReportInfoEntity.class);
    }

    public void setReadTimeout(Integer time) {
        restClient.setReadTimeout(time);
    }

    public Integer getReadTimeout(){
        return restClient.getReadTimeout();
    }

}
