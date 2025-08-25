/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.clientservice;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import java.util.List;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.view.WorkReportInfoEntity;
import jp.adtekfuji.adFactory.entity.view.WorkReportInfoListEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 直接工数実績情報用RESTクラス
 *
 * @author nar-nakamura
 */
public class DirectActualInfoFacade {

    private final Logger logger = LogManager.getLogger();
    private final LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
    private final RestClient restClient;

    private final static String DIRECT_ACTUAL_PATH = "/direct-actual";
    private final static String KANBAN_PATH = "/kanban";
    private final static String PRODUCTION_PATH = "/production";
    private final static String ORDER_PATH = "/order";
    private static final String AUTH_ID_PATH = "?authId=%d";

    /**
     * コンストラクタ
     */
    public DirectActualInfoFacade() {
        this.restClient = new RestClient(new RestClientProperty(ClientServiceProperty.getServerUri()));
    }

    /**
     * コンストラクタ
     *
     * @param uriBase 
     */
    public DirectActualInfoFacade(String uriBase) {
        this.restClient = new RestClient(new RestClientProperty(uriBase));
    }

    /**
     * カンバン単位の直接工数実績情報を更新する。
     *
     * @param workReports カンバン単位の直接工数実績情報
     * @return 更新の成否
     */
    public ResponseEntity updateKanbanActual(List<WorkReportInfoEntity> workReports) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DIRECT_ACTUAL_PATH);
            sb.append(KANBAN_PATH);
            sb.append(String.format(AUTH_ID_PATH, loginUser.getId()));

            return (ResponseEntity) restClient.put(sb.toString(), new WorkReportInfoListEntity(workReports), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 製造番号単位の直接工数実績情報を更新する。
     *
     * @param workReports 製造番号単位の直接工数実績情報
     * @return 更新の成否
     */
    public ResponseEntity updateProductionActual(List<WorkReportInfoEntity> workReports) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DIRECT_ACTUAL_PATH);
            sb.append(PRODUCTION_PATH);
            sb.append(String.format(AUTH_ID_PATH, loginUser.getId()));
            
            return (ResponseEntity) restClient.put(sb.toString(), new WorkReportInfoListEntity(workReports), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }

    /**
     * 注文番号単位の直接工数実績情報を更新する。
     *
     * @param workReports 注文番号単位の直接工数実績情報
     * @return 更新の成否
     */
    public ResponseEntity updateOrderActual(List<WorkReportInfoEntity> workReports) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(DIRECT_ACTUAL_PATH);
            sb.append(ORDER_PATH);
            sb.append(String.format(AUTH_ID_PATH, loginUser.getId()));
            
            return (ResponseEntity) restClient.put(sb.toString(), new WorkReportInfoListEntity(workReports), ResponseEntity.class);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            //TODO: 異常系はエラーコード参照の元データを入れて画面に返します
            return new ResponseEntity();
        }
    }
}
