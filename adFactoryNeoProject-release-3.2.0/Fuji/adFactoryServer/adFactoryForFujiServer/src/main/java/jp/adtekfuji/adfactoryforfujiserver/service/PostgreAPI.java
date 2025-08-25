/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryforfujiserver.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitActualResultEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitKanbanEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitKanbanHierarchyEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitWorkKanbanEntity;
import jp.adtekfuji.adfactoryforfujiserver.entity.unit.UnitWorkflowEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * PostgreSQL用クエリAPI
 *
 * @author s-heya
 */
public class PostgreAPI {
    private static final Logger logger = LogManager.getLogger();

    /**
     * カンバンを取得する。
     *
     * @param em
     * @param kanbanIds
     * @return
     */
    public static List<UnitKanbanEntity> getKanbans(EntityManager em, List<Long> kanbanIds) {
        try {
            logger.info("getKanbans: {}", kanbanIds);

            if (kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_kanban(ARRAY[");
            for (int ii = 0; ii < kanbanIds.size(); ii++) {
                sb.append(kanbanIds.get(ii));
                if (ii < kanbanIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString(), UnitKanbanEntity.class);
            List<UnitKanbanEntity> entities = query.getResultList();
            return entities;
        } finally {
            logger.info("getKanbans end.");
        }
    }

    /**
     * 工程カンバンを取得する。
     *
     * @param em
     * @param kanbanIds
     * @return
     */
    public static List<UnitWorkKanbanEntity> getWorkKanbans(EntityManager em, List<Long> kanbanIds) {
        try {
            logger.info("getWorkKanbans: {}", kanbanIds);

            if (kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_work_kanban(ARRAY[");
            for (int ii = 0; ii < kanbanIds.size(); ii++) {
                sb.append(kanbanIds.get(ii));
                if (ii < kanbanIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString(), UnitWorkKanbanEntity.class);
            List<UnitWorkKanbanEntity> entities = query.getResultList();
            return entities;
        } finally {
            logger.info("getWorkKanbans end.");
        }
    }

    /**
     * 作業中・完了状態の工程カンバンを取得する。
     *
     * @param em
     * @param kanbanIds
     * @return
     */
    public static List<UnitWorkKanbanEntity> getWorkKanbansInWork(EntityManager em, List<Long> kanbanIds) {
        try {
            logger.info("getWorkKanbans: {}", kanbanIds);

            if (kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_work_kanban_in_work(ARRAY[");
            for (int ii = 0; ii < kanbanIds.size(); ii++) {
                sb.append(kanbanIds.get(ii));
                if (ii < kanbanIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString(), UnitWorkKanbanEntity.class);
            List<UnitWorkKanbanEntity> entities = query.getResultList();
            return entities;
        } finally {
            logger.info("getWorkKanbans end.");
        }
    }

    /**
     * 生産ユニットの進捗率を取得する。
     *
     * @param em
     * @param kanbanIds
     * @return
     */
    public static Double getProgressRate(EntityManager em, List<Long> kanbanIds) {
        try {
            logger.info("getProgressRate: {}", kanbanIds);

            if (kanbanIds.isEmpty()) {
                return 0D;
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_unit_progress_rate (ARRAY[");
            for (int ii = 0; ii < kanbanIds.size(); ii++) {
                sb.append(kanbanIds.get(ii));
                if (ii < kanbanIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString());
            return (Double) query.getSingleResult();
        } finally {
            logger.info("getProgressRate end.");
        }
    }

    /**
     * 工程順を取得する。
     *
     * @param em
     * @param workflowIds
     * @return
     */
    public static List<UnitWorkflowEntity> getWorkflows(EntityManager em, List<Long> workflowIds) {
        try {
            logger.info("getWorkflows: {}", workflowIds);

            if (workflowIds.isEmpty()) {
                return new ArrayList<>();
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_workflow(ARRAY[");
            for (int ii = 0; ii < workflowIds.size(); ii++) {
                sb.append(workflowIds.get(ii));
                if (ii < workflowIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString(), UnitWorkflowEntity.class);
            List<UnitWorkflowEntity> entities = query.getResultList();
            return entities;
        } finally {
            logger.info("getWorkflows end.");
        }
    }

    /**
     * カンバン階層を取得する。
     *
     * @param em
     * @param kanbanHierarchyIds
     * @return
     */
    public static List<UnitKanbanHierarchyEntity> getKanbanHierarchies(EntityManager em, List<Long> kanbanHierarchyIds) {
        try {
            logger.info("getKanbanHierarchies: {}", kanbanHierarchyIds);

            if (kanbanHierarchyIds.isEmpty()) {
                return new ArrayList<>();
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_kanbanHierarchy(ARRAY[");
            for (int ii = 0; ii < kanbanHierarchyIds.size(); ii++) {
                sb.append(kanbanHierarchyIds.get(ii));
                if (ii < kanbanHierarchyIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString(), UnitKanbanHierarchyEntity.class);
            List<UnitKanbanHierarchyEntity> entities = query.getResultList();
            return entities;
        } finally {
            logger.info("getKanbanHierarchies end.");
        }
    }

    /**
     * 実績を取得する
     *
     * @param em
     * @param kanbanIds
     * @return
     */
    public static List<UnitActualResultEntity> getActualResults(EntityManager em, List<Long> kanbanIds) {
        try {
            logger.info("getActualResults: {}", kanbanIds);

            if (kanbanIds.isEmpty()) {
                return new ArrayList<>();
            }

            // クエリ文字列
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM get_actual_result_by_kanbanId(ARRAY[");
            for (int ii = 0; ii < kanbanIds.size(); ii++) {
                sb.append(kanbanIds.get(ii));
                if (ii < kanbanIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]);");

            Query query = em.createNativeQuery(sb.toString(), UnitActualResultEntity.class);
            List<UnitActualResultEntity> entities = query.getResultList();
            return entities;
        } finally {
            logger.info("getActualResults end.");
        }
    }
}
