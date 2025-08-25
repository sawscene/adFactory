/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.jdbc.adfactoryforfujidb;

import adtekfuji.admanagerapp.productionnaviplugin.entity.fuji.UnitTemplateInfo;
import adtekfuji.admanagerapp.productionnaviplugin.jdbc.JdbcConnector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * adFactoryForFujiDB アクセス
 *
 * @author nar-nakamura
 */
public class AdFactoryForFujiDbAccessor {
    private final Logger logger = LogManager.getLogger();

    private final JdbcConnector connector = AdFactoryForFujiDbConnector.getInstance();

    // ユニットテンプレート名を指定して、ユニットテンプレートのワークフローとカンバン階層IDを取得する。
    private final String SELECT_UNIT_TEMPLATE_DIAGLAM = "SELECT workflow_diaglam, fk_output_kanban_hierarchy_id FROM mst_unit_template WHERE unit_template_name = ?  AND (remove_flag IS NULL OR remove_flag = FALSE)";
  
    /**
     * コンストラクタ
     */
    public AdFactoryForFujiDbAccessor() {
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
     * ユニットテンプレート名を指定して、ユニットテンプレートのワークフローを取得する。
     *
     * @param templateName ユニットテンプレート名
     * @return ユニットテンプレート情報
     * @throws Exception 
     */
    public UnitTemplateInfo getUnitTemplate(String templateName) throws Exception {
        logger.info("getUnitTemplate: templateName={}", templateName);
        UnitTemplateInfo unitTemplate = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (Objects.isNull(connector.getConnection())) {
                connector.openDB();
            }

            ps = connector.getConnection().prepareStatement(SELECT_UNIT_TEMPLATE_DIAGLAM);
            ps.clearParameters();

            ps.setString(1, templateName);

            resultSet = ps.executeQuery();
            if (Objects.nonNull(resultSet)) {
                resultSet.next();
                try {
                    unitTemplate = new UnitTemplateInfo();
                    unitTemplate.setUnitTemplateName(templateName);
                    unitTemplate.setWorkflowDiaglam(resultSet.getString("workflow_diaglam"));
                    unitTemplate.setOutputKanbanHierarchyId(resultSet.getLong("fk_output_kanban_hierarchy_id"));
                } catch (SQLException ex) {
                    unitTemplate = null;
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
        return unitTemplate;
    }
}
