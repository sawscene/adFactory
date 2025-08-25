/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.jdbc.adfactorydb;

import adtekfuji.admanagerapp.productionnaviplugin.jdbc.JdbcConnector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adFactoryDB アクセス
 *
 * @author nar-nakamura
 */
public class AdFactoryDbAccessor {
    private final Logger logger = LogManager.getLogger();

    private final JdbcConnector connector = AdFactoryDbConnector.getInstance();

    // カンバン名・工程順IDを指定して、カンバンIDを取得する。
    private final String SELECT_KANBAN_ID_BY_KANBAN_NAME = "SELECT kanban_id FROM trn_kanban WHERE kanban_name = ? AND workflow_id = ?";
    // 組織識別名を指定して、組織IDを取得する。
    private final String SELECT_ORGANIZATION_ID_BY_ORGANIZATION_IDENTIFY = "SELECT organization_id FROM mst_organization WHERE organization_identify = ? AND remove_flag = FALSE";

    /**
     * コンストラクタ
     */
    public AdFactoryDbAccessor() {
    }

    /**
     * データベースを閉じる。
     */
    public void closeDb() {
        try {
            if (Objects.nonNull(connector.getConnection())) {
                this.connector.closeDB();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンが存在するかどうかを返す。
     * 
     * @param kanbanName カンバン名
     * @param workflowId 工程順ID
     * @return カンバンが存在する？
     * @throws Exception 
     */
    public Boolean exsitKanban(String kanbanName, Long workflowId) throws Exception {
        logger.info("exsitKanban: kanbanName={}, workflowId={}", kanbanName, workflowId);
        Boolean isExist = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.connector.getConnection())) {
                this.connector.openDB();
            }

            ps = this.connector.getConnection().prepareStatement(SELECT_KANBAN_ID_BY_KANBAN_NAME);
            ps.clearParameters();

            ps.setString(1, kanbanName);
            ps.setLong(2, workflowId);

            resultSet = ps.executeQuery();
            if (Objects.nonNull(resultSet)) {
                isExist = resultSet.next();
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally{
            try {
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                    resultSet = null;
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
        }
        return isExist;
    }

    /**
     * 組織識別名を指定して、組織IDを取得する。
     *
     * @param organizationIdentify 組織識別名
     * @return 組織ID (存在しない場合はnull)
     * @throws Exception 
     */
    public Long getOrganizationId(String organizationIdentify) throws Exception {
        logger.info("getOrganizationId: organizationIdentify={}", organizationIdentify);
        Long organizationId = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(this.connector.getConnection())) {
                this.connector.openDB();
            }

            ps = this.connector.getConnection().prepareStatement(SELECT_ORGANIZATION_ID_BY_ORGANIZATION_IDENTIFY);
            ps.clearParameters();

            ps.setString(1, organizationIdentify);

            resultSet = ps.executeQuery();
            if (Objects.nonNull(resultSet)) {
                resultSet.next();
                try {
                    organizationId = resultSet.getLong("organization_id");
                } catch (SQLException ex) {
                    organizationId = null;
                }
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally{
            try {
                if (Objects.nonNull(resultSet)) {
                    resultSet.close();
                    resultSet = null;
                }
                if (Objects.nonNull(ps)) {
                    ps.close();
                    ps = null;
                }
            } catch (SQLException ex) {
                logger.fatal(ex, ex);
            }
        }
        return organizationId;
    }
}
