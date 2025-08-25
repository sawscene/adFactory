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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentSettingInfoEntity;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginRequest;
import jp.adtekfuji.adFactory.entity.login.EquipmentLoginResult;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.search.EquipmentSearchCondition;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.PropertyEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設備情報取得用RESTクラス
 *
 * @author e-mori
 */
public class EquipmentInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static String EQUIPMENT_PATH = "/equipment";
    private final static String TREE_PATH = "/tree";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String SEARCH_PATH = "/search";
    private final static String COPY_PATH = "/copy";
    private final static String NAME_PATH = "/name";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String LOGIN_PATH = "/login";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%s";
    private final static String USER_PATH = "user=%s";
    private final static String AUTHID_PATH = "authId=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY = "name=%s";
    private final static String LICENSE_COUNT_FLG_QUERY = "isLicenseCount=%s";

    // キャッシュ
    private Map<Long, EquipmentInfoEntity> cacheById;
    private Map<String, EquipmentInfoEntity> cacheByName;

    public EquipmentInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public EquipmentInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * ルート階層に属する設備情報の件数を取得する。
     *
     * @return ルート階層に属する設備情報の件数
     */
    public Long getTopHierarchyCount() {
        logger.debug("getTopHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(TREE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(QUERY_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * ルート階層に属する設備情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return ルート階層に属する設備情報一覧
     */
    public List<EquipmentInfoEntity> getTopHierarchyRange(Long from, Long to) {
        return getTopHierarchyRange(from, to, Boolean.FALSE);
    }

    /**
     * ルート階層に属する設備情報一覧を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param isLicenseCount ライセンス数取得フラグ
     * @return ルート階層に属する設備情報一覧
     */
    public List<EquipmentInfoEntity> getTopHierarchyRange(Long from, Long to, Boolean isLicenseCount) {
        logger.debug("getTopHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(TREE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            int cnt = 0;

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
                cnt++;
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
                cnt++;
            }

            if (Objects.nonNull(isLicenseCount)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(LICENSE_COUNT_FLG_QUERY, isLicenseCount));
                cnt++;
            }

            return restClient.findAll(sb.toString(), new GenericType<List<EquipmentInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 階層IDを指定して、階層に属する設備情報の件数を取得する。
     *
     * @param hierarchyId 階層ID
     * @return 階層に属する設備情報の件数
     */
    public Long getAffilationHierarchyCount(Long hierarchyId) {
        logger.debug("getAffilationHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(TREE_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, hierarchyId.toString()));

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 階層IDを指定して、階層に属する設備情報一覧を取得する。
     *
     * @param hierarchyId 階層ID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 階層に属する設備情報一覧
     */
    public List<EquipmentInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to) {
        return getAffilationHierarchyRange(hierarchyId, from, to, Boolean.FALSE);
    }

    /**
     * 階層IDを指定して、階層に属する設備情報一覧を取得する。
     *
     * @param hierarchyId 階層ID
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @param isLicenseCount ライセンス数取得フラグ
     * @return 階層に属する設備情報一覧
     */
    public List<EquipmentInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to, Boolean isLicenseCount) {
        logger.debug("getAffilationHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(TREE_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, hierarchyId.toString()));

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            if (Objects.nonNull(isLicenseCount)) {
                sb.append(AND_PATH);
                sb.append(String.format(LICENSE_COUNT_FLG_QUERY, isLicenseCount));
            }

            return restClient.findAll(sb.toString(), new GenericType<List<EquipmentInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 新しい設備情報を登録する。
     *
     * @param entity 設備情報
     * @return 結果
     */
    public ResponseEntity regist(EquipmentInfoEntity entity) {
        logger.debug("regist:{}", entity);
        try {
            // 設備設定項目とJSONの追加情報を更新する。
            this.upateSetting(entity);

            return (ResponseEntity) restClient.post(EQUIPMENT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 設備情報を更新する。
     *
     * @param entity 設備情報
     * @return 結果
     */
    public ResponseEntity update(EquipmentInfoEntity entity) {
        try {
            // 設備設定項目とJSONの追加情報を更新する。
            this.upateSetting(entity);

            return (ResponseEntity) restClient.put(EQUIPMENT_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 設備情報をコピーする。
     *
     * @param entity 設備情報
     * @return 結果
     */
    public ResponseEntity copy(EquipmentInfoEntity entity) {
        try {
            // 設備設定項目とJSONの追加情報を更新する。
            this.upateSetting(entity);

            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(COPY_PATH);
            sb.append(String.format(ID_TARGET_PATH, entity.getEquipmentId()));

            return (ResponseEntity) restClient.post(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 設備情報を削除する。
     *
     * @param entity 設備情報
     * @return 結果
     */
    public ResponseEntity delete(EquipmentInfoEntity entity) {
        try {
            return (ResponseEntity) restClient.delete(EQUIPMENT_PATH, entity.getEquipmentId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return null;
    }

    /**
     * 設備情報の階層を移動する。
     *
     * @param entity 設備情報
     * @return 結果
     */
    public ResponseEntity move(EquipmentInfoEntity entity) {
        try {
            // 設備設定項目とJSONの追加情報を更新する。
            this.upateSetting(entity);

            return this.update(entity);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 設備情報を全て取得する。
     *
     * @return 組織情報一覧
     */
    public List<EquipmentInfoEntity> findAll() {
        logger.debug("findAll:start");
        try {

            return restClient.findAll(EQUIPMENT_PATH, new GenericType<List<EquipmentInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 設備IDを指定して、設備情報を取得する。
     *
     * @param id 設備ID
     * @return 設備情報
     */
    public EquipmentInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(String.format(ID_TARGET_PATH, id.toString()));

            return (EquipmentInfoEntity) restClient.find(sb.toString(), EquipmentInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new EquipmentInfoEntity();
        }
    }

    /**
     * 設備ID一覧を指定して、設備情報一覧を取得する。
     *
     * @param ids 設備ID一覧
     * @return 設備情報一覧
     */
    public List<EquipmentInfoEntity> find(List<Long> ids) {
        logger.info("find:{}", ids);
        try {
            if (ids.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, ids.get(0)));

            for (int i = 1; i < ids.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(ID_PATH, ids.get(i)));
            }

            return restClient.find(sb.toString(), new GenericType<List<EquipmentInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 設備識別名を指定して、設備情報を取得する。
     *
     * @param identName 設備識別名
     * @return 設備情報
     */
    public EquipmentInfoEntity findName(String identName) {
        logger.debug("findName:{}", identName);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY, RestClient.encode(identName)));

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return (EquipmentInfoEntity) restClient.find(sb.toString(), EquipmentInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new EquipmentInfoEntity();
        }
    }

    /**
     * 設備情報の件数を取得する。
     *
     * @return 件数
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(COUNT_PATH);

            // パラメータ
            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(QUERY_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return Long.parseLong((String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 指定された範囲の設備情報を取得する。
     *
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の設備情報一覧
     */
    public List<EquipmentInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(RANGE_PATH);

            // パラメータ
            int cnt = 0;

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
                cnt++;
            }

            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(cnt == 0 ? QUERY_PATH : AND_PATH);
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
                cnt++;
            }

            return restClient.findAll(sb.toString(), new GenericType<List<EquipmentInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 条件を指定して設備情報の件数を取得する。
     *
     * @param condition 検索条件
     * @return 件数
     */
    public Long countSearch(EquipmentSearchCondition condition) {
        logger.info("countSearch:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(SEARCH_PATH);
            sb.append(COUNT_PATH);

            return Long.parseLong((String) restClient.put(sb.toString(), condition, MediaType.TEXT_PLAIN_TYPE, String.class));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return (long) 0;
        }
    }

    /**
     * 条件を指定して、指定された範囲の設備情報を検索する。
     *
     * @param condition 検索条件
     * @param from 範囲の先頭
     * @param to 範囲の末尾
     * @return 指定された範囲の設備情報一覧
     */
    public List<EquipmentInfoEntity> findSearchRange(EquipmentSearchCondition condition, Long from, Long to) {
        logger.info("findSearchRange:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(SEARCH_PATH);
            sb.append(RANGE_PATH);
            sb.append(QUERY_PATH);

            // パラメータ
            if (Objects.nonNull(from) && Objects.nonNull(to)) {
                sb.append(String.format(FROM_TO_PATH, from.toString(), to.toString()));
            }

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(String.format(AND_PATH + AUTHID_PATH, loginUserInfoEntity.getId()));
            }
            
            return restClient.put(sb.toString(), condition, new GenericType<List<EquipmentInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 設備ログインする。
     *
     * @param request 設備ログイン要求情報
     * @return 設備ログイン結果
     */
    public EquipmentLoginResult login(EquipmentLoginRequest request) {
        try {
            logger.debug("equipment login:{}", request);
            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append(LOGIN_PATH);

            return (EquipmentLoginResult) restClient.put(sb.toString(), request, EquipmentLoginResult.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 設備IDを指定して、設備情報を取得する。
     *
     * ※.該当設備がキャッシュに存在する場合はキャッシュから取得する。
     *
     * @param equipmentId 設備ID
     * @return 設備情報
     */
    public EquipmentInfoEntity get(long equipmentId) {
        EquipmentInfoEntity equipment = null;

        if (Objects.isNull(this.cacheById)) {
            this.cacheById = new HashMap<>();
        }

        if (this.cacheById.containsKey(equipmentId)) {
            equipment = this.cacheById.get(equipmentId);
        } else {
            equipment = this.find(equipmentId);
            if (Objects.nonNull(equipment.getEquipmentId())) {
                this.cacheById.put(equipmentId, equipment);
            }
        }

        return equipment;
    }

    /**
     * 設備識別名を指定して、設備情報を取得する。
     *
     * ※.該当設備がキャッシュに存在する場合はキャッシュから取得する。
     *
     * @param name 設備識別名
     * @return 設備情報
     */
    public EquipmentInfoEntity get(String name) {
        EquipmentInfoEntity equipment = null;

        if (Objects.isNull(this.cacheByName)) {
            this.cacheByName = new HashMap<>();
        }

        if (this.cacheByName.containsKey(name)) {
            equipment = this.cacheByName.get(name);
        } else {
            try {
                equipment = this.findName(name);
                if (Objects.nonNull(equipment.getEquipmentId())) {
                    this.cacheByName.put(name, equipment);
                }
            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }

        return equipment;
    }

    /**
     * キャッシュをクリアする。
     */
    public void clearCache() {
        if (Objects.nonNull(this.cacheById)) {
            this.cacheById.clear();
        }
        if (Objects.nonNull(this.cacheByName)) {
            this.cacheByName.clear();
        }
    }

    /**
     * 設備設定項目とJSONの追加情報を更新する。
     *
     * @param entity 設備
     */
    private void upateSetting(EquipmentInfoEntity entity) {
        String ipv4Address = "";
        Boolean workProgressFlag = false;
        String pluginName = "";

        // IPv4アドレス
        Optional<EquipmentSettingInfoEntity> opt;
        opt = entity.getSettingInfoCollection().stream()
                .filter(e -> CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name().equals(e.getEquipmentSettingName()))
                .findFirst();
        if (opt.isPresent()) {
            ipv4Address = opt.get().getEquipmentSettingValue();
        }

        // 工程進捗フラグ
        opt = entity.getSettingInfoCollection().stream()
                .filter(e -> PropertyEnum.WORK_PROGRESS.name().equals(e.getEquipmentSettingName()))
                .findFirst();
        if (opt.isPresent()) {
            workProgressFlag = Boolean.valueOf(opt.get().getEquipmentSettingValue());
        }

        // プラグイン名
        opt = entity.getSettingInfoCollection().stream()
                .filter(e -> CustomPropertyTypeEnum.TYPE_PLUGIN.name().equals(e.getEquipmentSettingName()))
                .findFirst();
        if (opt.isPresent()) {
            pluginName = opt.get().getEquipmentSettingValue();
        }

        // 設備設定項目をセットする。
        entity.setIpv4Address(ipv4Address);
        entity.setWorkProgressFlag(workProgressFlag);
        entity.setPluginName(pluginName);

        // 追加情報一覧をJSON文字列に変換する。
        String jsonStr = JsonUtils.objectsToJson(entity.getPropertyInfoCollection());
        entity.setEquipmentAddInfo(jsonStr);
    }
    
    /**
     * JSONファイルから設備をインポートする。
     *
     * @param filePath JSONファイルのパス
     * @return 結果
     */
    public ResponseEntity importFile(String filePath) {
        try {
            logger.info("importFile: " + filePath);

            StringBuilder sb = new StringBuilder();
            sb.append(EQUIPMENT_PATH);
            sb.append("/import");

            return (ResponseEntity) restClient.upload(sb.toString(), filePath, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }
    
}
