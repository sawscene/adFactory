/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.entity.search.ReportOutSearchCondition;
import jp.adtekfuji.adFactory.entity.view.ReportOutInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 実績情報取得用RESTクラス
 *
 * @author ke.yokoi
 */
public class ActualResultInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    private static final String ACTUAL_PATH = "/actual";
    private static final String COUNT_PATH = "/count";
    private static final String RANGE_PATH = "/range";
    private static final String SEARCH_PATH = "/search";
    private static final String FROM_TO_PATH = "?from=%s&to=%s";
    private static final String FARST_PATH = "/farst";
    private static final String LAST_PATH = "/last";
    private static final String EXPORTED_PATH = "/exported";
    private static final String ID_PATH = "?id=%s";
    private static final String REPORTOUT_PATH = "/reportout";
    private static final String ADITION_PATH = "/adition";
    private static final String RAW_PATH = "/raw";
    private static final String KANBAN_ID_PARAM = "kanbanId=%s";

    private static final String ID_TARGET_PATH = "/%s";
    private static final String TAG_PARAM = "tag=%s";

    /**
     * コンストラクタ
     */
    public ActualResultInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public ActualResultInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 実績情報を検索する
     *
     * @param condition
     * @return
     */
    public List<ActualResultEntity> search(ActualSearchCondition condition) {
        logger.info("searchRange:{}", condition);
        try {
            return this.searchRange(condition, null, null);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 指定された範囲の実績情報を検索
     *
     * @param condition 条件
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の実績一覧
     */
    public List<ActualResultEntity> searchRange(ActualSearchCondition condition, Long from, Long to) {
        logger.info("searchRange:{},{},{}", condition, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<ActualResultEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ArrayList<>();
    }

    /**
     * 検索数取得
     *
     * @param condition 条件
     * @return 検索数
     */
    public Long searchCount(ActualSearchCondition condition) {
        logger.info("searchCount:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return 0L;
        }
    }

    /**
     * 実績情報を検索する
     *
     * @param condition
     * @return
     */
    public ActualResultEntity getFarst(ActualSearchCondition condition) {
        logger.info("getFarst: {}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(FARST_PATH);

            return (ActualResultEntity) restClient.put(sb.toString(), condition, ActualResultEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ActualResultEntity();
    }

    /**
     * 最新の実績情報を取得する。
     *
     * @param condition
     * @return
     */
    public List<ActualResultEntity> findLastActualResulList(ActualSearchCondition condition) {
        logger.info("findLastActualResulList: {}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append("/last/list?");

            return restClient.put(sb.toString(), condition, new GenericType<List<ActualResultEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * エクスポート済フラグにtrueを設定する
     *
     * @param actualId 実績ID
     * @return サーバーからの応答
     */
    public ResponseEntity updateExportedFlag(Long actualId) {
        logger.debug("updateExportedFlag:{}", actualId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(EXPORTED_PATH);
            sb.append(String.format(ID_PATH, actualId.toString()));
            return (ResponseEntity) restClient.put(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * カンバンID一覧を指定して、工程実績情報一覧を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return 工程実績情報一覧
     */
    public List<ActualResultEntity> find(List<Long> kanbanIds) {
        return this.find(kanbanIds, false);
    }

    /**
     * カンバンID一覧を指定して、工程実績情報一覧を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @param isDetail 詳細情報を取得する？ (true: する, false: しない)
     * @return 工程実績情報一覧
     */
    public List<ActualResultEntity> find(List<Long> kanbanIds, Boolean isDetail) {
        logger.info("find: isDetail={}, kanbanIds={}", isDetail, kanbanIds);
        try {
            if (kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);

            // パラメータ
            sb.append("?detail=").append(isDetail);

            for (Long id : kanbanIds) {
                sb.append("&id=").append(id);
            }

            return restClient.find(sb.toString(), new GenericType<List<ActualResultEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public ActualResultEntity find(Long id) {
        logger.info("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append("/");
            sb.append(id);

            return (ActualResultEntity) restClient.find(sb.toString(), null, ActualResultEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //return new ArrayList<>();
            return new ActualResultEntity();
        }
    }

    /**
     * 条件を指定して、実績出力情報一覧を取得する。
     *
     * @param condition 検索条件
     * @return 実績出力情報一覧
     */
    public List<ReportOutInfoEntity> reportOutSearch(ReportOutSearchCondition condition) {
        return reportOutSearch(condition, null, null);
    }

    /**
     * 条件を指定して、指定された範囲の実績出力情報一覧を取得する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の実績出力情報一覧
     */
    public List<ReportOutInfoEntity> reportOutSearch(ReportOutSearchCondition condition, Long from, Long to) {
        logger.info("reportOutSearch:{}, from={}, to={}", condition, from, to);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(REPORTOUT_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            return restClient.put(sb.toString(), condition, new GenericType<List<ReportOutInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 条件を指定して、実績出力情報一覧の件数を取得する。
     *
     * @param condition 検索条件
     * @return 件数
     */
    public Long reportOutSearchCount(ReportOutSearchCondition condition) {
        logger.info("reportOutSearchCount:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(REPORTOUT_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 実績付加情報のファイルデータを取得する。
     *
     * @param kanbanId カンバンID
     * @param tag タグ
     * @return byte配列のファイルデータ
     */
    public byte[] downloadFileData(long kanbanId, String tag) {
        logger.info("downloadFileData:{}{}", kanbanId, tag);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append(ADITION_PATH);
            sb.append(RAW_PATH);

            // パラメータ
            sb.append("?");
            sb.append(String.format(KANBAN_ID_PARAM, kanbanId));
            sb.append("&");
            sb.append(String.format(TAG_PARAM, encode(tag)));

            byte[] response = (byte[]) restClient.download(sb.toString(), byte[].class);

            return response;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * ファイルダウンロード
     * @param actualAddtionId 対象とする実績追加ID
     * @return 画像データ
     */
    public byte[] downloadFileData(long actualAddtionId) {

        logger.info("downloadFileData:{}", actualAddtionId);
        StringBuilder sb = new StringBuilder();
        sb.append(ACTUAL_PATH);
        sb.append(ADITION_PATH);
        sb.append(RAW_PATH);
        sb.append(String.format(ID_TARGET_PATH, actualAddtionId));

        return (byte[]) restClient.download(sb.toString(), byte[].class);
    }


    /**
     * HTML形式にエンコードする
     *
     * @param str エンコード対象の文字列
     * @return エンコードされた文字列
     */
    public String encode(String str) {
        try {
            String encodeStr = URLEncoder.encode(str, "UTF-8");
            encodeStr = encodeStr.replace("*", "%2a");
            encodeStr = encodeStr.replace("-", "%2d");
            return encodeStr;
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 工程実績IDを指定して、品質データを更新する。
     *
     * @param id 工程実績ID
     * @param addInfo 品質データ
     * @return 処理結果
     */
    public ResponseEntity updateAddInfo(Long id, String addInfo) {
        logger.info("updateAddInfo: id={}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append("/add-info/");
            sb.append(id);

            return (ResponseEntity) restClient.put(sb.toString(), addInfo, MediaType.TEXT_PLAIN_TYPE, MediaType.APPLICATION_XML_TYPE, ResponseEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }

    /**
     * 工程実績の実績時間を更新する。
     * 
     * @param id 工程実績ID
     * @param time 実績時間
     * @return 処理結果
     */
    public ResponseEntity updateTime(Long id, String time) {
        logger.info("updateAddInfo: id={}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ACTUAL_PATH);
            sb.append("/time/");
            sb.append(id);
            sb.append("?authId=");
            sb.append(LoginUserInfoEntity.getInstance().getId());

            return (ResponseEntity) restClient.put(sb.toString(), time, MediaType.TEXT_PLAIN_TYPE, MediaType.APPLICATION_XML_TYPE, ResponseEntity.class);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            throw ex;
        }
    }
}
