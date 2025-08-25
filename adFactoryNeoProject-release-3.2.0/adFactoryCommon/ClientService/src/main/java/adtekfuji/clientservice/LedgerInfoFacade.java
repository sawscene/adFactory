package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.ListWrapper;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerHierarchyInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LedgerInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final RestClient restClient;

    private static final SimpleDateFormat SDFORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String LEDGER_PATH = "/ledger";
    private static final String CHILDREN_PATH = "/children";
    private static final String UPLOAD_PATH = "/upload";
    private static final String TEMPLATE_PATH = "/template";
    private static final String REPORT_PATH = "/report";
    private static final String SEARCH_PATH = "/search";
    private static final String OUT_PATH = "/out";
    private static final String LEDGER_ID_PATH = "ledgerId=";
    private static final String EQUIPMENT_ID_PATH = "eId=";
    private static final String ORGANIZATION_ID_PATH = "oId=";
    private static final String FROM_DATE_PATH = "fromDate=";
    private static final String TO_DATE_PATH = "toDate=";
    private final static String AUTHID_PATH = "authId=%s";
    private final static String AND_PATH = "&";

    /**
     * コンストラクタ
     */
    public LedgerInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public LedgerInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    public List<LedgerInfoEntity> findChildren(Long parentId) {
        return findChildren(Collections.singletonList(parentId));
    }

    /**
     * 階層IDをしてそこに属する帳票情報を取得する
     * @param parentIds 親ID
     * @return 帳票情報
     */
    public List<LedgerInfoEntity> findChildren(List<Long> parentIds) {
        logger.info("find: ids={}", parentIds);
        if (parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LEDGER_PATH);
        sb.append(CHILDREN_PATH);
        sb.append("?");
        sb.append(parentIds.stream().map(id -> "id="+id).collect(Collectors.joining("&")));
        return restClient.find(sb.toString(), new GenericType<List<LedgerHierarchyInfoEntity>>(){});
    }

    /**
     * 帳票を登録する
     * @param ledgerInfo 帳票情報
     * @return 結果
     */
    public ResponseEntity register(LedgerInfoEntity ledgerInfo) {
        logger.info("register:{}", ledgerInfo);
        try {
            return (ResponseEntity) restClient.post(LEDGER_PATH, ledgerInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            if (ex instanceof ConstraintViolationException) {
                ConstraintViolationException cve = (ConstraintViolationException) ex;
                for (ConstraintViolation cv : cve.getConstraintViolations()) {
                    logger.info("CONSTRAINT VIOLOATION : {}", cv.toString());
                }
            }
        }
        return new ResponseEntity();
    }

    /**
     * 帳票の削除
     * @param ids 帳票ID
     * @return レスポンス
     */
    public ResponseEntity remove(Long id) {
        return remove(Collections.singletonList(id));
    }

    /**
     * 帳票の削除
     * @param ids 帳票ID
     * @return レスポンス
     */
    public ResponseEntity remove(List<Long> ids) {
        logger.info("remove:{}", ids);
        if (ids.isEmpty()) {
            return ResponseEntity.success();
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_PATH);
            sb.append("?");
            sb.append(ids.stream().map(id -> "id="+id).collect(Collectors.joining("&")));

            return (ResponseEntity) restClient.delete(sb.toString(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 帳票を取得する。
     * @param uri
     * @return
     */
    public LedgerInfoEntity findURI(String uri) {
        logger.info("find:{}", uri);
        try {
            return (LedgerInfoEntity) restClient.find("/" + uri, LedgerInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 帳票の更新
     * @param ledgerInfoEntity 帳票情報
     * @return レスポンス
     */
    public ResponseEntity update(LedgerInfoEntity ledgerInfoEntity) {
        logger.info("update:{}", ledgerInfoEntity);
        try {
            return (ResponseEntity) restClient.put(LEDGER_PATH, ledgerInfoEntity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 帳票を取得する。
     * @param id 帳票階層ID
     * @return レスポンス
     */
    public LedgerInfoEntity find(Long id) {
        logger.info("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_PATH);
            sb.append("/");
            sb.append(id);

            return (LedgerInfoEntity) restClient.find(sb.toString(), LedgerInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 帳票アップロード
     * @param filePath ファイルパス
     * @return 結果
     */
    public ResponseEntity uploadTemplate(String filePath) {
        try {
            logger.info("uploadForm {}", filePath);

            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_PATH);
            sb.append(UPLOAD_PATH);
            sb.append(TEMPLATE_PATH);

            return (ResponseEntity) restClient.upload(sb.toString(), filePath, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 帳票、設備、組織、日時から工程実績を取得する
     * @param ledgerId 帳票ID
     * @param equipmentIds 設備ID
     * @param organizationIds 組織ID
     * @param fromDate 開始時間
     * @param toDate 終了時間
     * @return
     */
    public List<ActualResultEntity> getHistory(Long ledgerId, List<Long> equipmentIds, List<Long> organizationIds, Date fromDate, Date toDate) {
        try {
            logger.info("getHistory ledgerId={} equipmentIds={} oganizationIds={}, fromDate={}, toDate={}", ledgerId, equipmentIds, organizationIds, fromDate, toDate);

            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_PATH);
            sb.append(REPORT_PATH);
            sb.append(SEARCH_PATH);
            sb.append("?");
            sb.append(LEDGER_ID_PATH).append(ledgerId);


            if (!Objects.isNull(equipmentIds) && !equipmentIds.isEmpty()) {
                equipmentIds.forEach(id-> sb.append("&" + EQUIPMENT_ID_PATH).append(id));
            }

            if (!Objects.isNull(organizationIds) && !organizationIds.isEmpty()) {
                organizationIds.forEach(id-> sb.append("&" + ORGANIZATION_ID_PATH).append(id));
            }

            if (Objects.nonNull(fromDate)) {
                sb.append("&" + FROM_DATE_PATH).append(SDFORMAT.format(fromDate));
            }

            if (Objects.nonNull(toDate)) {
                sb.append("&" + TO_DATE_PATH).append(SDFORMAT.format(toDate));
            }

            return (List<ActualResultEntity>) restClient.find(sb.toString(), new GenericType<List<ActualResultEntity>>(){});
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 帳票出力する
     * @param ledgerId 帳票ID
     * @param actualResultIds 工程実績ID
     * @param fromDate 開始日
     * @param toDate 終了日
     * @return 帳票出力結果
     */
    public ResponseEntity reportOut(Long ledgerId, List<Long> equipmentIds, List<Long> organizationIds, Date fromDate, Date toDate, List<Long> actualResultIds) {
        try {
            logger.info("getHistory ledgerId={}", ledgerId);

            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_PATH);
            sb.append(REPORT_PATH);
            sb.append(OUT_PATH);
            sb.append("?");
            sb.append(LEDGER_ID_PATH).append(ledgerId);

            if (!Objects.isNull(equipmentIds) && !equipmentIds.isEmpty()) {
                equipmentIds.forEach(id-> sb.append("&" + EQUIPMENT_ID_PATH).append(id));
            }

            if (!Objects.isNull(organizationIds) && !organizationIds.isEmpty()) {
                organizationIds.forEach(id-> sb.append("&" + ORGANIZATION_ID_PATH).append(id));
            }

            if (Objects.nonNull(fromDate)) {
                sb.append("&" + FROM_DATE_PATH).append(SDFORMAT.format(fromDate));
            }

            if (Objects.nonNull(toDate)) {
                sb.append("&" + TO_DATE_PATH).append(SDFORMAT.format(toDate));
            }

            if (Objects.nonNull(loginUserInfoEntity.getId())) {
                sb.append(String.format(AND_PATH + AUTHID_PATH, loginUserInfoEntity.getId()));
            }

            return (ResponseEntity) restClient.post(sb.toString(), new ListWrapper<>(actualResultIds), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

}
