package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerHierarchyInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LedgerHierarchyInfoFacade {

    private final Logger logger = LogManager.getLogger();

    private final RestClient restClient;

    private static final String LEDGER_TREE_PATH = "/ledger/tree";
    private static final String CHILDREN_PATH = "/children";

    /**
     * コンストラクタ
     */
    public LedgerHierarchyInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public LedgerHierarchyInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    public List<LedgerHierarchyInfoEntity> findChildren(Long parentId) {
        return findChildren(Collections.singletonList(parentId));
    }

    /**
     * 階層IDをしてそこに属する帳票階層を取得する
     * @param parentIds 親ID　
     * @return 帳票階層
     */
    public List<LedgerHierarchyInfoEntity> findChildren(List<Long> parentIds) {
        logger.info("find: ids={}", parentIds);
        if (parentIds.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LEDGER_TREE_PATH);
        sb.append(CHILDREN_PATH);
        sb.append("?");
        sb.append(parentIds.stream().map(id -> "id="+id).collect(Collectors.joining("&")));
        return restClient.find(sb.toString(), new GenericType<List<LedgerHierarchyInfoEntity>>(){});
    }

    /**
     * 帳票階層を登録する
     * @param ledgerHierarchyInfo 帳票階層情報
     * @return 結果
     */
    public ResponseEntity register(LedgerHierarchyInfoEntity ledgerHierarchyInfo) {
        logger.info("register:{}", ledgerHierarchyInfo);
        try {
            return (ResponseEntity) restClient.post(LEDGER_TREE_PATH, ledgerHierarchyInfo, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 帳票階層の削除
     * @param ids 帳票階層ID
     * @return レスポンス
     */
    public ResponseEntity remove(Long id) {
        return remove(Collections.singletonList(id));
    }

    /**
     * 工程順階層の削除
     *
     * @param ids 帳票階層ID
     * @return レスポンス
     */
    public ResponseEntity remove(List<Long> ids) {
        logger.info("remove:{}", ids);
        if (ids.isEmpty()) {
            return ResponseEntity.success();
        }

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_TREE_PATH);
            sb.append("?");
            sb.append(ids.stream().map(id -> "id="+id).collect(Collectors.joining("&")));

            return (ResponseEntity) restClient.delete(sb.toString(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }



    /**
     * 帳票階層を取得する。
     * @param uri
     * @return
     */
    public LedgerHierarchyInfoEntity findURI(String uri) {
        logger.info("find:{}", uri);
        try {
            return (LedgerHierarchyInfoEntity) restClient.find("/" + uri, LedgerHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 帳票階層の更新
     * @param ledgerHierarchyInfoEntity 帳票階層情報
     * @return レスポンス
     */
    public ResponseEntity update(LedgerHierarchyInfoEntity ledgerHierarchyInfoEntity) {
        logger.info("update:{}", ledgerHierarchyInfoEntity);
        try {
            return (ResponseEntity) restClient.put(LEDGER_TREE_PATH, ledgerHierarchyInfoEntity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return new ResponseEntity();
    }

    /**
     * 帳票階層を取得する。
     * @param id 帳票階層ID
     * @return レスポンス
     */
    public LedgerHierarchyInfoEntity find(Long id) {
        logger.info("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(LEDGER_TREE_PATH);
            sb.append("/");
            sb.append(id);

            return (LedgerHierarchyInfoEntity) restClient.find(sb.toString(), LedgerHierarchyInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }


}
