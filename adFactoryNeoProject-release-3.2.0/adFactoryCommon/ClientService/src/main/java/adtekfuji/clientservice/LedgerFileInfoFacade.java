package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.GenericType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerFileInfoEntity;
import jp.adtekfuji.adFactory.entity.ledger.LedgerFileSearchEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LedgerFileInfoFacade {

    private final Logger logger = LogManager.getLogger();

    private final RestClient restClient;

    private static final SimpleDateFormat SDFORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String LEDGER_FILE_PATH = "/ledger/file";
    private static final String CHILDREN_PATH = "/children";
    private static final String RAW_PATH = "/raw";
    private static final String ID_PATH = "id=%s";

    /**
     * コンストラクタ
     */
    public LedgerFileInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase
     */
    public LedgerFileInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 階層IDをしてそこに属する帳票情報を取得する
     * @param parentIds 親ID
     * @return 帳票情報
     */
    public List<LedgerFileInfoEntity> findChildren(LedgerFileSearchEntity entity) {
        logger.info("findChildren: ids={}", entity);
        if (Objects.isNull(entity) || entity.getLedgerIds().isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(LEDGER_FILE_PATH);
        sb.append(CHILDREN_PATH);
        return restClient.post(sb.toString(), entity, new GenericType<List<LedgerFileInfoEntity>>(){});
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
            sb.append(LEDGER_FILE_PATH);
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
    public LedgerFileInfoEntity findURI(String uri) {
        logger.info("find:{}", uri);
        try {
            return (LedgerFileInfoEntity) restClient.find("/" + uri, LedgerFileInfoEntity.class);
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
    public File downloadFileData(Long id) {

        logger.info("downloadFileData:{}", id);
        StringBuilder sb = new StringBuilder();
        sb.append(LEDGER_FILE_PATH);
        sb.append(RAW_PATH);
        sb.append("/"+id);

        return (File) restClient.download(sb.toString(), File.class);
    }

}
