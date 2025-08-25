package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.form.FormInfoEntity;
import jp.adtekfuji.adFactory.entity.form.FormTagEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.ws.rs.core.Response;

/**
 * 帳票用ファサード
 */
public class FormFacade {
    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;

    /**
     * コンストラクタ
     */
    public FormFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public FormFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    private static final String FORM_PATH = "/form";

    private static final String TAG_PATH = "/tag";

    public static final String UPLOAD_PATH = "/upload";

    /**
     * タグ一覧取得
     * @param formInfoEntity テンプレート情報
     * @return
     */
    public FormTagEntity getFormTag(FormInfoEntity formInfoEntity)
    {
        logger.info("getFormTag:{}", formInfoEntity);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(FORM_PATH);
            sb.append(TAG_PATH);

            return (FormTagEntity) restClient.put(sb.toString(), formInfoEntity, FormTagEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 帳票アップロード
     * @param filePath ファイルパス
     * @return 結果
     */
    public ResponseEntity uploadForm(String filePath) {
        try {
            logger.info("importPlanInfo");

            StringBuilder sb = new StringBuilder();
            sb.append(FORM_PATH);
            sb.append(UPLOAD_PATH);

            return (ResponseEntity) restClient.upload(sb.toString(), filePath, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

}
