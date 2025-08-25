
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice;

import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.HolidayInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkflowInfoFacade;
import adtekfuji.property.AdProperty;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Timer;
import jp.adtekfuji.adFactory.entity.holiday.HolidayInfoEntity;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import static jp.adtekfuji.adinterfaceservice.Constants.POLLING_TIME;
import jp.adtekfuji.adinterfaceservicecommon.plugin.util.ImportFormatFileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static jp.adtekfuji.adinterfaceservice.Constants.ENABLE_IMPORT;
import jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.SELECT_PROD_CSV_PATH;
import static jp.adtekfuji.adinterfaceservicecommon.plugin.common.ProductionNaviPropertyConstants.SELECT_PROD_IGNORE_EXISTING;

/**
 * サービスでカンバンインポートを行う
 *
 * @author fu-kato
 */
public class ImportServicePlugin extends Thread implements AdInterfaceServiceInterface {

    private static final String SERVICE_NAME = "ImportService";

    private static final Logger logger = LogManager.getLogger();

    private final Timer timer = new Timer();
    private ImportTask importTask;
    private ImportFormatInfo importFormatInfo = null;

    private final boolean enableImport;
    private final String importDir;
    private final long pollingTime;
    private final boolean ignoreSameKanban;

    private static final long REST_RANGE_NUM = 20;
    private static final long HOLIDAY_RANGE_NUM = 100;

    public ImportServicePlugin() throws IOException {
        // adInterface の設定を読み込む。
        AdProperty.load("adInterface.properties");
        final Properties properties = AdProperty.getProperties();

        if (!properties.containsKey(ENABLE_IMPORT)) {
            properties.setProperty(ENABLE_IMPORT, "false");
            store();
        }

        if (!properties.containsKey(POLLING_TIME)) {
            properties.setProperty(POLLING_TIME, "60");
            store();
        }

        // インポートサービス有効？
        enableImport = Boolean.valueOf(properties.getProperty(ENABLE_IMPORT));
        // インポートサービスのポーリング間隔(秒)
        pollingTime = 1000L * Long.valueOf(properties.getProperty(POLLING_TIME));

        // adManager 生産管理の設定を読み込む。
        AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
        final Properties productionNaviProps = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

        // 読み込みフォルダ
        final String defaultPath = System.getProperty("user.home") + File.separator + "Documents";
        importDir = productionNaviProps.getProperty(SELECT_PROD_CSV_PATH, defaultPath);
        // 同名のカンバンを無視する？
        ignoreSameKanban = Boolean.valueOf(productionNaviProps.getProperty(SELECT_PROD_IGNORE_EXISTING, Boolean.toString(false)));
    }

    @Override
    public void run() {
        importTask = new ImportTask(importDir, importFormatInfo, ignoreSameKanban);
        timer.schedule(importTask, pollingTime, pollingTime);
    }

    @Override
    public void startService() throws Exception {
        logger.info("started import service.");

        if (!enableAutoImport()) {
            return;
        }

        final CashManager cm = CashManager.getInstance();
        createCashOrganization(cm);
        createCashHoliday(cm);

        ImportFormatFileUtil.setWorkflowInfoFacade(new WorkflowInfoFacade());
        importFormatInfo = ImportFormatFileUtil.load();

        super.start();
    }

    private void store() {
        try {
            AdProperty.store();
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    @Override
    public void stopService() throws Exception {
        logger.info("stopped import service.");

        if (Objects.nonNull(timer)) {
            timer.cancel();
        }

        super.join();
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    private boolean enableAutoImport() {
        return enableImport;
    }

    /**
     * キャッシュに組織情報を読み込む。
     */
    private void createCashOrganization(CashManager cashManager) {
        logger.info("createCashOrganization");

        if (cashManager.isExist(OrganizationInfoEntity.class)) {
            logger.info("OrganizationInfoEntity exist.");
            return;
        }

        cashManager.setNewCashList(OrganizationInfoEntity.class);
        cashManager.clearList(OrganizationInfoEntity.class);

        OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
        Long organizationCount = organizationInfoFacade.count();
        for (long count = 0; count < organizationCount; count += REST_RANGE_NUM) {
            List<OrganizationInfoEntity> entitys = organizationInfoFacade.findRange(count, count + REST_RANGE_NUM - 1);
            entitys.stream().forEach((entity) -> {
                cashManager.setItem(OrganizationInfoEntity.class, entity.getOrganizationId(), entity);
            });
        }

        logger.info("createCashOrganization end.");
    }

    /**
     * キャッシュに休日情報を読み込む。
     */
    private void createCashHoliday(CashManager cashManager) {
        logger.info("createCashHoliday");

        if (cashManager.isExist(HolidayInfoEntity.class)) {
            logger.info("HolidayInfoEntity exist.");
            return;
        }

        cashManager.setNewCashList(HolidayInfoEntity.class);
        cashManager.clearList(HolidayInfoEntity.class);

        HolidayInfoFacade holidayInfoFacade = new HolidayInfoFacade();
        long holidayCount = holidayInfoFacade.count();
        for (long count = 0; count <= holidayCount; count += HOLIDAY_RANGE_NUM) {
            List<HolidayInfoEntity> entities = holidayInfoFacade.findRange(count, count + HOLIDAY_RANGE_NUM - 1);
            entities.stream().forEach((entity) -> {
                cashManager.setItem(HolidayInfoEntity.class, entity.getHolidayId(), entity);
            });
        }

        logger.info("createCashHoliday end.");
    }
}
