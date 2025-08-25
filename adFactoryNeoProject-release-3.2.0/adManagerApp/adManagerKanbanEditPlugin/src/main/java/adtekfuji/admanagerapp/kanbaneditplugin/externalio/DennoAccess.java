/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.externalio;

import adtekfuji.admanagerapp.kanbaneditplugin.entity.KanbanBaseInfoEntity;
import adtekfuji.admanagerapp.kanbaneditplugin.entity.KanbanBaseInfoPropertyEntity;
import adtekfuji.dao.DbConnectorOracle;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import jp.adtekfuji.adFactory.entity.job.OrderInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * エデックリンセイシステム様 電脳工場(Oracle 19c)にアクセス
 * 
 * @author s-heya
 */
public class DennoAccess implements DbAccess {
    
    private final Logger logger = LogManager.getLogger();
    
    private final Properties properties = AdProperty.getProperties();
    private final String hostNmae;
    private final String port;
    private final String serviceName;
    private final String userID;
    private final String userPass;
    
    /**
     * コンストラクタ
     */
    public DennoAccess() {
        this.hostNmae = properties.getProperty("DENNO_HOST_NAME", "192.168.214.200");
        this.port = properties.getProperty("DENNO_PORT", "1521");
        this.serviceName = properties.getProperty("DENNO_SERVICE_NAME", "ORCL");
        this.userID = properties.getProperty("DENNO_USER_ID", "DFW_USER");
        this.userPass = properties.getProperty("DENNO_PASSWORD", "EX");
    }
        
    /**
     * カンバン基本情報を取得する。
     * 
     * @param id 作業指示書コード 
     * @return カンバン基本情報
     */
    @Override
    public KanbanBaseInfoEntity GetKanbanBaseInfo(String id) {
        final String SELECT_SQL = "SELECT DFW_T030F.ITEM_NAME1 AS HINMEI, DFW_T030F.ITEM_NAME2 AS ZUBAN, DFW_M040M.MODEL_NUMBER AS KIKAKU, DFW_T030F.ARRANGE_QTY AS QTY, KOUTEI_M040M.ITEM_NAME1 AS KOUTEIMEI, DFW_J010F.REMARKS2 AS COMM FROM DFW_H010F LEFT JOIN DFW_T030F ON DFW_H010F.PROD_NO = DFW_T030F.PROD_NO AND DFW_H010F.ARRANGE_ID = DFW_T030F.ARRANGE_ID AND DFW_H010F.ROUTE = DFW_T030F.ROUTE LEFT JOIN DFW_J010F ON DFW_J010F.PROD_NO = DFW_T030F.PROD_NO LEFT JOIN DFW_M040M ON DFW_M040M.ITEM_CD = DFW_J010F.ITEM_CD AND DFW_M040M.ITEM_DIV = DFW_J010F.ITEM_DIV LEFT JOIN (SELECT M040M.ITEM_DIV, M040M.ITEM_CD, M040M.ITEM_NAME1 FROM DFW_USER.DFW_M040M M040M WHERE M040M.ITEM_DIV = 'K') KOUTEI_M040M ON DFW_T030F.PROC_CD = KOUTEI_M040M.ITEM_CD WHERE DFW_H010F.PROD_NO = :PROD_NO AND DFW_T030F.PROC_NO = :KBAN AND DFW_T030F.PROCURE_DIV = '1'";

        DbConnectorOracle connector = DbConnectorOracle.getInstance();
        KanbanBaseInfoEntity kanbanBase = null;
   
        try {
            int index = id.lastIndexOf("-");
            if (index < 0) {
                return kanbanBase;
            }

            String porder = id.substring(0, index);    // 製番
            String kban = id.substring(index + 1);     // 工程番号

            String sql = SELECT_SQL.replaceAll(":PROD_NO", "'" + porder + "'").replaceAll(":KBAN", kban);
            connector.openDB(this.hostNmae, this.port, this.userID, this.userPass, this.serviceName);

            try (ResultSet resultSet = connector.execQuery(sql)) {
                if (resultSet.next()) {
                    String hinmei = resultSet.getString("HINMEI");                              // 品名
                    String kikakuKatasiki = resultSet.getString("KIKAKU");                      // 規格・型式
                    String syanaiZuban = resultSet.getString("ZUBAN");                          // 図番
                    String syanaiComment = resultSet.getString("COMM");                         // 社内コメント
                    int kvol = StringUtils.parseInteger(resultSet.getString("QTY"));            // 数量
                    String kbumonName = resultSet.getString("KOUTEIMEI");                       // 工程名

                    // カンバン基本情報
                    kanbanBase = new KanbanBaseInfoEntity();
                    // 工程順名 (規格・型式 - 部門名)
                    kanbanBase.setWorkflowName(kikakuKatasiki + "-" + kbumonName);
                    // ロット数量 (生産数)
                    kanbanBase.setLotQuantity(kvol);

                    // プロパティ
                    List<KanbanBaseInfoPropertyEntity> props = new ArrayList<>();
                    // プロパティ - 注番
                    props.add(new KanbanBaseInfoPropertyEntity(LocaleUtils.getString("key.Porder"), CustomPropertyTypeEnum.TYPE_STRING, porder, 1));
                    // プロパティ - 品名
                    props.add(new KanbanBaseInfoPropertyEntity(LocaleUtils.getString("key.ProductName"), CustomPropertyTypeEnum.TYPE_STRING, hinmei, 2));
                    // プロパティ - 規格・型式
                    props.add(new KanbanBaseInfoPropertyEntity(LocaleUtils.getString("key.KikakuKatasiki"), CustomPropertyTypeEnum.TYPE_STRING, kikakuKatasiki, 3));
                    // プロパティ - 図番
                    props.add(new KanbanBaseInfoPropertyEntity(LocaleUtils.getString("key.FigureNumber"), CustomPropertyTypeEnum.TYPE_STRING, syanaiZuban, 4));
                    // プロパティ - 社内コメント
                    props.add(new KanbanBaseInfoPropertyEntity(LocaleUtils.getString("key.InternalComment"), CustomPropertyTypeEnum.TYPE_STRING, syanaiComment, 5));
                    // プロパティ - 工程番号
                    props.add(new KanbanBaseInfoPropertyEntity(LocaleUtils.getString("key.worknumber"), CustomPropertyTypeEnum.TYPE_STRING, kban, 6));

                    kanbanBase.setPropertyCollection(props);

                    // 注番情報
                    OrderInfoEntity orderInfo = new OrderInfoEntity();
                    orderInfo.setPorder(porder);// 注番
                    orderInfo.setHinmei(hinmei);// 品名
                    orderInfo.setKikakuKatasiki(kikakuKatasiki);// 規格・型式
                    orderInfo.setKbumoName(kbumonName);// 部門名
                    orderInfo.setSyanaiZuban(syanaiZuban);// 図番
                    orderInfo.setSyanaiComment(syanaiComment);// 社内コメント
                    orderInfo.setKban(kban);// 工程番号
                    orderInfo.setKvol(kvol);// 計画指示数

                    kanbanBase.setOrderInfo(orderInfo);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            connector.closeDB();
        }
        
        return kanbanBase;
    }

    /**
     * データベースにアクセスできるかどうかを返す。
     * 
     * @return true: アクセス可, false: アクセス不可 
     */
    @Override
    public boolean IsAvailable() {
        return true;
    }
    
}
