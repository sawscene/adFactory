/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.enumerate.EquipmentTypeEnum;
import jp.adtekfuji.adFactory.enumerate.PropertyEnum;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentSettingTemplateEntity;
import jp.adtekfuji.adfactoryserver.entity.equipment.EquipmentTypeEntity;
import jp.adtekfuji.adfactoryserver.utility.ExecutionTimeLogging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 設備種別マスタ REST
 *
 * @author ke.yokoi
 */
@Stateless
@Path("equipment-type")
public class EquipmentTypeEntityFacadeREST extends AbstractFacade<EquipmentTypeEntity> {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    private static boolean isInitialize = false;

    private static final List<EquipmentSettingTemplateEntity> equipmentSettingTemplates = new ArrayList();// 設備マスタ設定テンプレート一覧

    /**
     * コンストラクタ
     */
    public EquipmentTypeEntityFacadeREST() {
        super(EquipmentTypeEntity.class);
    }

    /**
     * 設備種別マスタを初期化する。
     */
    private void initializeTable() {
        if (EquipmentTypeEntityFacadeREST.isInitialize) {
            return;
        }

        logger.info("createEquipmentTypeTable start.");

        // 作業者端末の設備種別情報を登録する。
        this.createTerminal();
        // 進捗モニタの設備種別情報を登録する。
        this.createMonitor();
        // 製造設備の設備種別情報を登録する。
        this.createManufacture();
        // 測定機器の設備種別情報を登録する。
        this.createMeasure();
        // 作業者端末(Lite)の設備種別情報を登録する。
        this.createLiteTerminal();
        // 作業者端末(Reporter)の設備種別情報を登録する。
        this.createReporterTerminal();

        this.em.flush();

        EquipmentTypeEntityFacadeREST.isInitialize = true;

        logger.info("createEquipmentTypeTable end.");
    }

    /**
     * 作業者端末の設備種別情報を登録する。
     */
    private void createTerminal() {
        EquipmentSettingTemplateEntity settingTemplate1 = new EquipmentSettingTemplateEntity(
                EquipmentTypeEnum.TERMINAL, CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name(), CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name(), "", 1);
        equipmentSettingTemplates.add(settingTemplate1);
        EquipmentSettingTemplateEntity settingTemplate2 = new EquipmentSettingTemplateEntity(
                EquipmentTypeEnum.TERMINAL, PropertyEnum.WORK_PROGRESS.name(), CustomPropertyTypeEnum.TYPE_BOOLEAN.name(), "", 2);
        equipmentSettingTemplates.add(settingTemplate2);

        this.getEquipmentType(EquipmentTypeEnum.TERMINAL);
    }

    /**
     * 進捗モニタの設備種別情報を登録する。
     */
    private void createMonitor() {
        EquipmentSettingTemplateEntity settingTemplate1 = new EquipmentSettingTemplateEntity(
                EquipmentTypeEnum.MONITOR, CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name(), CustomPropertyTypeEnum.TYPE_IP4_ADDRESS.name(), "", 1);
        equipmentSettingTemplates.add(settingTemplate1);

        this.getEquipmentType(EquipmentTypeEnum.MONITOR);
    }

    /**
     * 製造設備の設備種別情報を登録する。
     */
    private void createManufacture() {
        EquipmentSettingTemplateEntity settingTemplate1 = new EquipmentSettingTemplateEntity(
                EquipmentTypeEnum.MANUFACTURE, CustomPropertyTypeEnum.TYPE_PLUGIN.name(), CustomPropertyTypeEnum.TYPE_PLUGIN.name(), "", 1);
        equipmentSettingTemplates.add(settingTemplate1);

        this.getEquipmentType(EquipmentTypeEnum.MANUFACTURE);
    }

    /**
     * 測定機器の設備種別情報を登録する。
     */
    private void createMeasure() {
        EquipmentSettingTemplateEntity settingTemplate1 = new EquipmentSettingTemplateEntity(
                EquipmentTypeEnum.MEASURE, CustomPropertyTypeEnum.TYPE_PLUGIN.name(), CustomPropertyTypeEnum.TYPE_PLUGIN.name(), "", 1);
        equipmentSettingTemplates.add(settingTemplate1);

        this.getEquipmentType(EquipmentTypeEnum.MEASURE);
    }

    /**
     * 作業者端末(Lite)の設備種別情報を登録する。
     */
    private void createLiteTerminal() {
        this.getEquipmentType(EquipmentTypeEnum.LITE);
    }
    
    /**
     * 作業者端末(Reporter)の設備種別情報を登録する。
     */
    private void createReporterTerminal() {
        this.getEquipmentType(EquipmentTypeEnum.REPORTER);
    }

    /**
     * 設備種別情報を取得する。
     *
     * @param name 設備種別
     */
        @Lock(LockType.READ)
    private EquipmentTypeEntity getEquipmentType(EquipmentTypeEnum name) {
        TypedQuery<EquipmentTypeEntity> query = this.em.createNamedQuery("EquipmentTypeEntity.findByName", EquipmentTypeEntity.class);
        query.setParameter("name", name);

        List<EquipmentTypeEntity> equipmentTypes = query.getResultList();
        EquipmentTypeEntity equipmentType;
        if (equipmentTypes.isEmpty()) {
            // 設備種別情報を新規登録する。
            equipmentType = new EquipmentTypeEntity(name);
            this.create(equipmentType);
        } else {
            equipmentType = equipmentTypes.get(0);
        }

        List<EquipmentSettingTemplateEntity> settingTemplates = this.getSettingTemplate(equipmentType.getName());
        equipmentType.setSettingTemplateCollection(settingTemplates);

        return equipmentType;
    }

    @Override
    public void create(EquipmentTypeEntity entity) {
        super.create(entity);
        this.em.flush();
    }

    /**
     * 設備種別情報を取得する。
     *
     * @param type 設備種別
     * @return 設備種別情報
     */
    @Lock(LockType.READ)
    public EquipmentTypeEntity findType(EquipmentTypeEnum type) {
        this.initializeTable();
        return this.getEquipmentType(type);
    }

    /**
     * 設備種別一覧を取得する。
     *
     * @param authId 認証ID
     * @return 設備種別一覧
     */
    @Lock(LockType.READ)
    @GET
    @Produces({"application/xml", "application/json"})
    @ExecutionTimeLogging
    public List<EquipmentTypeEntity> findAll(@QueryParam("authId") Long authId) {
        this.initializeTable();

        List<EquipmentTypeEntity> entities = super.findAll();
        for (EquipmentTypeEntity entity : entities) {
            entity.setSettingTemplateCollection(this.getSettingTemplate(entity.getName()));
        }
        return entities;
    }

    /**
     * 設備種別名を指定して、設備マスタ設定テンプレート一覧を取得する。
     *
     * @param name 設備種別名
     * @return 設備マスタ設定テンプレート一覧
     */
    @Lock(LockType.READ)
    private List<EquipmentSettingTemplateEntity> getSettingTemplate(EquipmentTypeEnum name) {
        return equipmentSettingTemplates.stream()
                .filter(p -> p.getEquipmentType().equals(name)).collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
