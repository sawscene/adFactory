/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.view.WorkReportInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業日報
 *
 * @author nar-nakamura
 */
public class WorkReportInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private final static String WORK_REPORT_PATH = "/workreport";// 作業日報ビュー
    private final static String DAILY_PATH = "/daily";// 作業日報
    private final static String KANBAN_PATH = "/kanban";
    private final static String PRODUCTION_PATH = "/production";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String ORGANIZATION_ID_PATH = "organizationId=%s";
    private final static String WORK_DATE_PATH = "workDate=%s";
    private final static String FROM_DATE_PATH = "fromDate=%s";
    private final static String TO_DATE_PATH = "toDate=%s";
    private final static String ID_PATH = "id=%s";

    /**
     * コンストラクタ
     */
    public WorkReportInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @return 作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDate(String fromDate, String toDate) {
        return findFromToDate(fromDate, toDate, null);
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @return 作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDate(String fromDate, String toDate, List<Long> organizationIds) {
        logger.debug("findFromToDate: fromDate={}, toDate={}, organizationIds={}", fromDate, toDate, organizationIds);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_REPORT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(FROM_DATE_PATH, fromDate));
            sb.append(AND_PATH);
            sb.append(String.format(TO_DATE_PATH, toDate));

            if (Objects.nonNull(organizationIds)) {
                for (Long id : organizationIds) {
                    sb.append(AND_PATH);
                    sb.append(String.format(ID_PATH, id));
                }
            }

            return restClient.findAll(sb.toString(), new GenericType<List<WorkReportInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された期間の作業日報情報一覧を範囲指定して取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * ※．一括取得でタイムアウトする場合のみ使用する。
     * 　取得範囲に関わらず実行毎にView全体の取得クエリが実行されるので、
     * 　1回で取得する件数は多く、分割回数は少なくなるようにして使用すること。
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDateRange(String fromDate, String toDate, Long from, Long to) {
        return findFromToDateRange(fromDate, toDate, null, from, to);
    }

    /**
     * 指定された期間の作業日報情報一覧を範囲指定して取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * ※．一括取得でタイムアウトする場合のみ使用する。
     * 　取得範囲に関わらず実行毎にView全体の取得クエリが実行されるので、
     * 　1回で取得する件数は多く、分割回数は少なくなるようにして使用すること。
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDateRange(String fromDate, String toDate, List<Long> organizationIds, Long from, Long to) {
        logger.debug("findFromToDateRange: fromDate={}, toDate={}, organizationIds={}, from={}, to={}", fromDate, toDate, organizationIds, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_REPORT_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(FROM_DATE_PATH, fromDate));
            sb.append(AND_PATH);
            sb.append(String.format(TO_DATE_PATH, toDate));

            if (Objects.nonNull(organizationIds)) {
                for (Long id : organizationIds) {
                    sb.append(AND_PATH);
                    sb.append(String.format(ID_PATH, id));
                }
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<WorkReportInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された期間の作業日報情報の件数を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @return 件数
     */
    public Long countFromToDate(String fromDate, String toDate) {
        return countFromToDate(fromDate, toDate, null);
    }

    /**
     * 指定された期間の作業日報情報の件数を取得する。(作業日・作業者・工程順・注文番号(サブカンバン名)・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @return 件数
     */
    public Long countFromToDate(String fromDate, String toDate, List<Long> organizationIds) {
        logger.debug("countFromToDate: fromDate={}, toDate={}, organizationIds={}", fromDate, toDate, organizationIds);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_REPORT_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(FROM_DATE_PATH, fromDate));
            sb.append(AND_PATH);
            sb.append(String.format(TO_DATE_PATH, toDate));

            if (Objects.nonNull(organizationIds)) {
                for (Long id : organizationIds) {
                    sb.append(AND_PATH);
                    sb.append(String.format(ID_PATH, id));
                }
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定された作業者・日付の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
     *
     * @param organizationId 組織ID
     * @param workDate 作業日 (yyyyMMdd)
     * @return 作業日報情報
     */
    public List<WorkReportInfoEntity> findOrganizationIdDailyKanban(Long organizationId, String workDate) {
        logger.debug("findOrganizationIdDaily:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_REPORT_PATH);
            sb.append(DAILY_PATH);
            sb.append(KANBAN_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ORGANIZATION_ID_PATH, organizationId.toString()));
            sb.append(AND_PATH);
            sb.append(String.format(WORK_DATE_PATH, workDate));

            return restClient.findAll(sb.toString(), new GenericType<List<WorkReportInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @return 作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDateKanban(String fromDate, String toDate) {
        return findFromToDateKanban(fromDate, toDate, null);
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・カンバン名・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @return 作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDateKanban(String fromDate, String toDate, List<Long> organizationIds) {
        logger.debug("findFromToDateKanban: fromDate={}, toDate={}, organizationIds={}", fromDate, toDate, organizationIds);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_REPORT_PATH);
            sb.append(KANBAN_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(FROM_DATE_PATH, fromDate));
            sb.append(AND_PATH);
            sb.append(String.format(TO_DATE_PATH, toDate));

            if (Objects.nonNull(organizationIds)) {
                for (Long id : organizationIds) {
                    sb.append(AND_PATH);
                    sb.append(String.format(ID_PATH, id));
                }
            }

            return restClient.findAll(sb.toString(), new GenericType<List<WorkReportInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 指定された期間の作業日報情報一覧を取得する。(作業日・作業者・工程順・製造番号・工程で集計)
     *
     * @param fromDate 先頭年月日 (yyyyMMdd)
     * @param toDate 末尾年月日 (yyyyMMdd)
     * @param organizationIds 組織IDリスト (指定した組織の子以降の組織も対象になる)
     * @return 作業日報情報
     */
    public List<WorkReportInfoEntity> findFromToDateProduction(String fromDate, String toDate, List<Long> organizationIds) {
        logger.debug("findFromToDateProduction: fromDate={}, toDate={}, organizationIds={}", fromDate, toDate, organizationIds);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(WORK_REPORT_PATH);
            sb.append(PRODUCTION_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(FROM_DATE_PATH, fromDate));
            sb.append(AND_PATH);
            sb.append(String.format(TO_DATE_PATH, toDate));

            if (Objects.nonNull(organizationIds)) {
                for (Long id : organizationIds) {
                    sb.append(AND_PATH);
                    sb.append(String.format(ID_PATH, id));
                }
            }

            return restClient.findAll(sb.toString(), new GenericType<List<WorkReportInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
