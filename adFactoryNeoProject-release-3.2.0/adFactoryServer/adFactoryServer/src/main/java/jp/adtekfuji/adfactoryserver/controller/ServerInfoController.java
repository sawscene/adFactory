/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.controller;

import adtekfuji.property.AdProperty;
import com.sun.management.OperatingSystemMXBean;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jp.adtekfuji.adfactoryserver.model.LicenseManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * サーバー情報
 * 
 * @author ke.yokoi
 */
@Named(value = "serverInfo")
@RequestScoped
public class ServerInfoController {

    @PersistenceContext(unitName = "adtekfuji_adFactoryServer_war_1.0PU")
    private EntityManager em;

    private final Logger logger = LogManager.getLogger();

    private static final String OUTERNAL_VER = "Ver";
    private static final String INTERNAL_VER = "InternalVer";
    private static final String VERSION_INI = "versionini";
    private String outernalVersion = "unknown";
    private String internalVersion = "unknown";

    public ServerInfoController() {
        try {
            AdProperty.rebasePath(System.getenv("ADFACTORY_HOME"));
            AdProperty.load(VERSION_INI, "version.ini");
            Properties properties = AdProperty.getProperties(VERSION_INI);
            this.outernalVersion = properties.getProperty(OUTERNAL_VER);
            this.internalVersion = properties.getProperty(INTERNAL_VER);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }

    public String getOuternalVersion() {
        return outernalVersion;
    }

    public String getInternalVersion() {
        return internalVersion;
    }

    public String getLicenseDate() {
        Date date = LicenseManager.getInstance().getLicenseDate();
        if (Objects.isNull(date)) {
            return "license file is nothing.";
        }
        return date.toString();
    }

    public String getSysteminfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("system info ------------------ \r");
        getSystemInfo(sb);
        sb.append("database info ---------------- \r");
        getDatabaseInfo(sb);
        return sb.toString();
    }

    private void getSystemInfo(StringBuilder sb) {
        //Properties properties = System.getProperties();
        //for (Map.Entry<Object, Object> e : properties.entrySet()) {
        //    sb.append(e.getKey()).append(" : ").append(e.getValue()).append("\r");
        //}
        String[] propKeys = {"os.name", "os.version", "sun.os.patch.level", "java.runtime.version", "java.class.version", "openejb.version", "tomcat.version", "user.name"};
        for (String key : propKeys) {
            sb.append(key).append(" : ").append(System.getProperty(key)).append("\r");
        }
        long logSize = FileUtils.sizeOfDirectory(new File(System.getenv("ADFACTORY_HOME") + File.separator + "logs"));
        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long maximumMemory = Runtime.getRuntime().maxMemory();
        OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long virtualMemory = bean.getCommittedVirtualMemorySize();
        long freePhysicalMemory = bean.getFreePhysicalMemorySize();
        long freeSwapSpace = bean.getFreeSwapSpaceSize();
        long totalPhysicalMemory = bean.getTotalPhysicalMemorySize();
        long totalSwapSpace = bean.getTotalSwapSpaceSize();
        double systemCpuLoad = bean.getSystemCpuLoad();
        double processCpuLoad = bean.getProcessCpuLoad();
        sb.append("log folder size : ").append(FileUtils.byteCountToDisplaySize(logSize)).append("\r");
        sb.append("total memory : ").append(FileUtils.byteCountToDisplaySize(totalMemory)).append("\r");
        sb.append("used memory : ").append(FileUtils.byteCountToDisplaySize(usedMemory)).append("\r");
        sb.append("maximum memory : ").append(FileUtils.byteCountToDisplaySize(maximumMemory)).append("\r");
        sb.append("committed virtual memory : ").append(FileUtils.byteCountToDisplaySize(virtualMemory)).append("\r");
        sb.append("free physical memory : ").append(FileUtils.byteCountToDisplaySize(freePhysicalMemory)).append("\r");
        sb.append("free swap space size : ").append(FileUtils.byteCountToDisplaySize(freeSwapSpace)).append("\r");
        sb.append("total physical memory : ").append(FileUtils.byteCountToDisplaySize(totalPhysicalMemory)).append("\r");
        sb.append("total swap space size : ").append(FileUtils.byteCountToDisplaySize(totalSwapSpace)).append("\r");
        if (systemCpuLoad > 0) {
            sb.append("system cpu load : ").append(systemCpuLoad * 100.0).append("%\r");
        } else {
            sb.append("system cpu load : -\r");
        }
        if (processCpuLoad > 0) {
            sb.append("process cpu load : ").append(processCpuLoad * 100.0).append("%\r");
        } else {
            sb.append("process cpu load : -\r");
        }
        sb.append("\r");
    }

    private void getDatabaseInfo(StringBuilder sb) {
        List<String> tables = Arrays.asList(
                "trn_kanban", "trn_work_kanban", "trn_actual_result",
                "mst_workflow", "mst_work", "mst_organization", "mst_equipment");
        String result;
        try {
            Query query1 = em.createNativeQuery("SELECT pg_size_pretty(pg_database_size('adFactoryDB2'));");
            result = query1.getSingleResult().toString();
            sb.append("database size : ").append(result).append("\r");
            for (String table : tables) {
                Query query2 = em.createNativeQuery("SELECT pg_size_pretty(pg_relation_size('" + table + "'));");
                result = query2.getSingleResult().toString();
                sb.append("table size(").append(table).append(") : ").append(result).append("\r");
            }
            result = em.createNativeQuery("select count(*) from pg_stat_activity;").getSingleResult().toString();
            sb.append("connect user count : ").append(result).append("\r");

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        sb.append("\r");
    }

}
