package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;


public class ResourceInfoFacade {
    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String RESOURCE_PATH = "/resource";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String TYPEKEY_PATH = "/typekey";
    private final static String TYPE_PATH = "type=%s";
    private final static String KEY_PATH = "key=%s";
    private final static String ADD_PATH = "/add";


    public ResourceInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public ResourceInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 設備IDを指定して、設備情報を取得する。
     *
     * @param id 設備ID
     * @return 設備情報
     */
    public ResourceInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(RESOURCE_PATH);
            sb.append(String.format(ID_TARGET_PATH, id.toString()));

            return (ResourceInfoEntity) restClient.find(sb.toString(), EquipmentInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResourceInfoEntity();
        }
    }

    /**
     * Typeとkeyでリソースを検索
     * @param resourceType リソースタイプ
     * @param resourceKey キータイプ
     * @return リソース情報
     */
    public ResourceInfoEntity findByTypeKey(ResourceTypeEnum resourceType, String resourceKey) {
        try {
            logger.info("findByTypeKey: {} {}", resourceType, resourceKey);
            StringBuilder sb = new StringBuilder();
            sb.append(RESOURCE_PATH);
            sb.append(TYPEKEY_PATH);
            sb.append("?");
            sb.append(String.format(TYPE_PATH, resourceType));
            sb.append("&");
            sb.append(String.format(KEY_PATH, resourceKey));

            return (ResourceInfoEntity) restClient.find(sb.toString(), ResourceInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 更新
     * @param entity 更新するエンティティ
     * @return 結果
     */
    public ResponseEntity update(ResourceInfoEntity entity) {
        logger.info("update");

        return (ResponseEntity) restClient.put(RESOURCE_PATH, entity, ResponseEntity.class);
    }

    public ResponseEntity add(ResourceInfoEntity entity) {
        logger.info("add");
        StringBuilder sb = new StringBuilder();
        sb.append(RESOURCE_PATH);
        sb.append(ADD_PATH);

        return (ResponseEntity) restClient.put(sb.toString(), entity, ResponseEntity.class);
    }

}
