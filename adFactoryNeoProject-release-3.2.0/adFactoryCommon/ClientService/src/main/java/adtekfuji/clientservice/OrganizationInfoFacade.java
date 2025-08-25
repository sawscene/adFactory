/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import adtekfuji.utility.PasswordEncoder;
import jakarta.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.MediaType;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.SampleResponse;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginRequest;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.search.OrganizationSearchCondition;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 組織情報取得用RESTクラス
 *
 * @author e-mori
 */
public class OrganizationInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();

    private final static long RANGE = 100L;

    private final static String ORGANIZATION_PATH = "/organization";
    private final static String TREE_PATH = "/tree";
    private final static String COUNT_PATH = "/count";
    private final static String RANGE_PATH = "/range";
    private final static String COPY_PATH = "/copy";
    private final static String ID_TARGET_PATH = "/%s";
    private final static String LOGIN_PATH = "/login";
    private final static String NAME_PATH = "/name";
    private final static String ANCESTORS_PATH = "/ancestors";
    private final static String MAILS_PATH = "/mails";
    private final static String SEARCH_PATH = "/search";

    private final static String QUERY_PATH = "?";
    private final static String AND_PATH = "&";
    private final static String ID_PATH = "id=%s";
    private final static String USER_PATH = "user=%s";
    private final static String AUTHID_PATH = "authId=%s";
    private final static String FROM_TO_PATH = "from=%s&to=%s";
    private final static String NAME_QUERY_PATH = "name=%s";

    // キャッシュ
    private Map<Long, OrganizationInfoEntity> cache;

    public OrganizationInfoFacade() {
        restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    public OrganizationInfoFacade(String uriBase) {
        restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * 最上位階層に含まれる組織個数取得
     *
     * @return 組織個数取得
     */
    public Long getTopHierarchyCount() {
        logger.debug("getTopHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
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
     * 最上位階層に含まれる組織一覧を取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 組織一覧
     */
    public List<OrganizationInfoEntity> getTopHierarchyRange(Long from, Long to) {
        logger.debug("getTopHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
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

            return restClient.findAll(sb.toString(), new GenericType<List<OrganizationInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * IDで指定された階層の組織個数取得
     *
     * @param hierarchyId 親階層ID
     * @return 組織個数
     */
    public Long getAffilationHierarchyCount(Long hierarchyId) {
        logger.debug("getAffilationHierarchyCount:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
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
     * IDで指定された階層の組織一覧を取得
     *
     * @param hierarchyId 親階層ID
     * @param from 頭数
     * @param to 尾数
     * @return 組織一覧
     */
    public List<OrganizationInfoEntity> getAffilationHierarchyRange(Long hierarchyId, Long from, Long to) {
        logger.debug("getAffilationHierarchyRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
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

            return restClient.findAll(sb.toString(), new GenericType<List<OrganizationInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 新しい組織の登録
     *
     * @param entity 登録する組織情報
     * @return 登録の成否
     */
    public ResponseEntity regist(OrganizationInfoEntity entity) {
        logger.debug("regist:{}", entity);

        // 追加情報をJSONにする
        String jsonStr = JsonUtils.objectsToJson(entity.getPropertyInfoCollection());
        entity.setOrganizationAddInfo(jsonStr);

        try {
            return (ResponseEntity) restClient.post(ORGANIZATION_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 組織の更新
     *
     * @param entity
     * @return
     */
    public ResponseEntity update(OrganizationInfoEntity entity) {

        // 追加情報をJSONにする
        String jsonStr = JsonUtils.objectsToJson(entity.getPropertyInfoCollection());
        entity.setOrganizationAddInfo(jsonStr);

        try {
            return (ResponseEntity) restClient.put(ORGANIZATION_PATH, entity, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返しますOrganization
            return new ResponseEntity();
        }
    }

    /**
     * 組織のコピー
     *
     * @param entity
     * @return
     */
    public ResponseEntity copy(OrganizationInfoEntity entity) {

        // 追加情報をJSONにする
        String jsonStr = JsonUtils.objectsToJson(entity.getPropertyInfoCollection());
        entity.setOrganizationAddInfo(jsonStr);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(COPY_PATH);
            sb.append(String.format(ID_TARGET_PATH, entity.getOrganizationId()));

            return (ResponseEntity) restClient.post(sb.toString(), null, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 組織の削除
     *
     * @param entity
     * @return
     */
    public ResponseEntity delete(OrganizationInfoEntity entity) {

        try {
            return (ResponseEntity) restClient.delete(ORGANIZATION_PATH, entity.getOrganizationId(), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);

        }
        return null;
    }

    /**
     * 組織の移動
     *
     * @param entity
     * @return
     */
    public ResponseEntity move(OrganizationInfoEntity entity) {

        // 追加情報をJSONにする
        String jsonStr = JsonUtils.objectsToJson(entity.getPropertyInfoCollection());
        entity.setOrganizationAddInfo(jsonStr);

        try {
            return this.update(entity);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 組織情報取得
     *
     * @param id 組織ID
     * @return IDに該当する組織
     */
    public OrganizationInfoEntity find(Long id) {
        logger.debug("find:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(String.format(ID_TARGET_PATH, id.toString()));

            return (OrganizationInfoEntity) restClient.find(sb.toString(), OrganizationInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new OrganizationInfoEntity();
        }
    }

    /**
     * 指定した組織IDの組織情報を取得する。
     *
     * @param ids 組織ID一覧
     * @return 組織情報一覧
     */
    public List<OrganizationInfoEntity> find(List<Long> ids) {
        logger.info("find:{}", ids);
        try {
            if (ids.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(ID_PATH, ids.get(0)));

            for (int i = 1; i < ids.size(); i++) {
                sb.append(AND_PATH);
                sb.append(String.format(ID_PATH, ids.get(i)));
            }

            return restClient.find(sb.toString(), new GenericType<List<OrganizationInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 組織情報一覧取得
     *
     * @return 組織情報一覧
     */
    public List<OrganizationInfoEntity> findAll() {
        logger.debug("findAll:start");
        try {
            // ※.findAllのRESTは使用禁止。全件取得する場合は必ずrangeで分割取得すること。
            List<OrganizationInfoEntity> organizations = new ArrayList();
            Long infoCount = this.count();
            for (long count = 0; count < infoCount; count += RANGE) {
                List<OrganizationInfoEntity> entities = this.findRange(count, count + RANGE - 1);
                organizations.addAll(entities);
            }

            return organizations;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * 全設備数取得
     *
     * @return
     */
    public Long count() {
        logger.debug("count:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
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
     * 指定された範囲の設備情報を取得
     *
     * @param from 頭数
     * @param to 尾数
     * @return 指定された範囲の設備一覧
     */
    public List<OrganizationInfoEntity> findRange(Long from, Long to) {
        logger.debug("findRange:start");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
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

            return restClient.findAll(sb.toString(), new GenericType<List<OrganizationInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * ログイン処理
     *
     * @param id 組織識別情報
     * @param password 組織パスワード
     * @param retMessage エラー時のメッセージログ
     * @return ログイン結果情報
     */
    public OrganizationLoginResult login(String id, String password, StringBuilder retMessage) {
        logger.info("login:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(LOGIN_PATH);

            PasswordEncoder encoder = new PasswordEncoder();
            OrganizationLoginRequest loginRequest = OrganizationLoginRequest.passwordType(id, encoder.encode(password));
            OrganizationLoginResult loginResult = (OrganizationLoginResult) restClient.put(sb.toString(), loginRequest, OrganizationLoginResult.class);
            if (loginResult.getIsSuccess()) {
                return loginResult;
            }
            switch (loginResult.getErrorType()) {
                case NOT_AUTH_ORGANIZATION:
                    retMessage.append("key.LoginErrNotmatchPassword");
                    break;
                case NOT_LOGINID_ORGANIZATION:
                    retMessage.append("key.LoginErrThereIsNoUserID");
                    break;
                case NOT_PERMIT_ORGANIZATION:
                    retMessage.append("key.NotPermitAuthority");
                    break;
                case LOGIN_LDAP_EXCEPTION:
                    retMessage.append("key.LoginLdapException");
                    break;
                default:
                    break;
            }
            return loginResult;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            retMessage.append("key.ServerReconnectMessage");
        }
        return null;
    }
    
    /**
     * ログイン処理 (LDAP認証)
     *
     * @param id ユーザーID
     * @param password パスワード
     * @param retMessage エラー時のメッセージログ
     * @return ログイン状態の成否
     */
    public OrganizationLoginResult loginLdap(String id, String password, StringBuilder retMessage) {
        logger.info("loginLdap:{}", id);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(LOGIN_PATH);

            PasswordEncoder encoder = new PasswordEncoder();
            OrganizationLoginRequest loginRequest = OrganizationLoginRequest.ldapType(id, encoder.encodeAES(password));
            OrganizationLoginResult loginResult = (OrganizationLoginResult) restClient.put(sb.toString(), loginRequest, OrganizationLoginResult.class);
            if (loginResult.getIsSuccess()) {
                return loginResult;
            }
            switch (loginResult.getErrorType()) {
                case NOT_AUTH_ORGANIZATION:
                    retMessage.append("key.LoginErrNotmatchPassword");
                    break;
                case NOT_LOGINID_ORGANIZATION:
                    retMessage.append("key.LoginErrThereIsNoUserID");
                    break;
                case NOT_PERMIT_ORGANIZATION:
                    retMessage.append("key.NotPermitAuthority");
                    break;
                case LOGIN_LDAP_EXCEPTION:
                    retMessage.append("key.LoginLdapException");
                    break;
                default:
                    break;
            }
            return loginResult;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            retMessage.append("key.ServerReconnectMessage");
        }
        return null;
    }

    public OrganizationInfoEntity findName(String name) {
        logger.debug("find name:{}", name);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(NAME_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(NAME_QUERY_PATH, name));

            if (Objects.nonNull(loginUserInfoEntity.getId()) && !LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {
                sb.append(AND_PATH);
                sb.append(String.format(USER_PATH, loginUserInfoEntity.getId()));
            }

            return (OrganizationInfoEntity) restClient.find(sb.toString(), OrganizationInfoEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new OrganizationInfoEntity();
        }
    }

    /**
     * 組織情報を取得する
     *
     * @param organizationId
     * @return
     */
    public OrganizationInfoEntity get(long organizationId) {
        OrganizationInfoEntity organization = null;

        if (Objects.isNull(this.cache)) {
            this.cache = new HashMap<>();
        }

        if (this.cache.containsKey(organizationId)) {
            organization = this.cache.get(organizationId);
        } else {
            organization = this.find(organizationId);
            if (Objects.nonNull(organization.getOrganizationId())) {
                this.cache.put(organizationId, organization);
            }
        }
        return organization;
    }

    /**
     * TSVファイルから組織をインポートする。
     *
     * @param filePath
     * @return
     */
    public ResponseEntity importFile(String filePath) {
        try {
            logger.info("importFile: " + filePath);

            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append("/import/file");

            return (ResponseEntity) restClient.upload(sb.toString(), filePath, ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ResponseEntity();
        }
    }

    /**
     * 指定されたユーザーIDを子組織IDとする組織を文字列で取得する。
     *
     * @param userId ユーザーID (組織ID)
     * @return 文字列
     */
    public String findAncestorsString(Long userId) {
        logger.debug("findAncestorsString: userId={}", userId);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(TREE_PATH);
            sb.append(ANCESTORS_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(String.format(USER_PATH, userId));

            return (String) restClient.find(sb.toString(), MediaType.TEXT_PLAIN_TYPE, String.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    public List<String> mailingList(List<Long> ids)
    {
        logger.info("mailingList:{}", ids);
        try {
            if (ids.isEmpty()) {
                return new ArrayList<>();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(MAILS_PATH);

            // パラメータ
            sb.append(QUERY_PATH);
            sb.append(ids.stream().map(id->String.format(ID_PATH, id)).collect(Collectors.joining(AND_PATH)));

            SampleResponse response = (SampleResponse) restClient.find(sb.toString(), SampleResponse.class);
            if (ServerErrorTypeEnum.SUCCESS != ServerErrorTypeEnum.valueOf(response.getStatus())) {
                return new ArrayList<>();
            }

            return response.getDataList();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }

    /**
     * キャッシュをクリアする
     */
    public void clearCache() {
        if (Objects.nonNull(this.cache)) {
            this.cache.clear();
        }
    }

    /**
     * 組織情報を検索する。
     *
     * @param condition 検索条件
     * @return 組織情報
     */
    public List<OrganizationInfoEntity> search(OrganizationSearchCondition condition) {
        logger.info("search:{}", condition);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(ORGANIZATION_PATH);
            sb.append(SEARCH_PATH);
            sb.append(QUERY_PATH);

            if (Objects.nonNull(loginUserInfoEntity.getId())) {
                sb.append(String.format(AUTHID_PATH, loginUserInfoEntity.getId()));
            }
            
            return restClient.put(sb.toString(), condition, new GenericType<List<OrganizationInfoEntity>>() {
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
}
