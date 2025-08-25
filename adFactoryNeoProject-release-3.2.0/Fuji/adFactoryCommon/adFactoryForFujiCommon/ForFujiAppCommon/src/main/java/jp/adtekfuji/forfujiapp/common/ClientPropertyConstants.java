package jp.adtekfuji.forfujiapp.common;

import java.io.File;

/**
 * クライアント設定ファイル用定数
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
public class ClientPropertyConstants {

    // AdProperties TAG
    public final static String ADPROPERTY_FOR_FUJI_TAG = "ForFuji";

    // PROPERTY
    public final static String ADFACTORY_FOR_FUJI_SERVER_PROPERTY_FILE = "adFactoryForFujiService.properties";
    public final static String ADFACTORY_FOR_FUJI_CLIENT_PROPERTY_FILE = "adFactoryForFujiClient.properties";
    public final static String ADFACTORY_FOR_FUJI_SERVICE_URI = "adFactoryForFujiServiceURI";
    public final static String ADFACTORY_SERVICE_URI = "adFactoryServiceURI";
    public static final String KEY_SERVICE_PATH = "adInterfaceServiceAddress";
    public static final String KEY_PORT_NUM = "adInterfaceServicePortNum";
    public static final String KEY_CRYPT = "adFactoryCrypt";
    public static final String KEY_RECONNECT_TIMER = "adInterfaceServiceReConnectTime";

    // UnitTemplateConfig
    public static final String KEY_SELECT_UNIT_TEMPLATE_HIERARCHY = "UnitTemplateConfig_selectHierarchy";

    // UnitTemplateConfi
    public static final String KEY_SELECT_UNIT_HIERARCHY = "UnitConfig_selectHierarchy";

    // UnitScheduleConf
    public final static String PROP_KEY_SCEDULE_CELL_SIZE = "ScheduleCellSize";
    public final static String PROP_KEY_AUTO_INTERVAL_TIME = "AutoIntervalTime";
    public final static String PROP_KEY_SELECT_UNITTEMPLATE = "SelectUnitTemplate";
    public final static String PROP_KEY_ITEM_TITLE = "ItemTitle";
    public final static String PROP_KEY_SELECT_TITLE = "SelectTitle";
    public final static String PROP_KEY_SELECT_ORGANIZATION = "SelectOrganization";
    public final static String DEFAULT_MASTER_ITEM_TITLES = "オーダー番号,シリアル,ユーザー名,受注番号";

    // Analysis
    public static final String KEY_TACTTIME_EARLIEST = "Filter.TactTimeEarliest";
    public static final String KEY_TACTTIME_SLOWEST = "Filter.TactTimeSlowest";
    public static final String KEY_DELAYREASON = "Filter.DelayReason";
    public static final String KEY_TIME_UNIT = "Filter.TimeUnit";

    // CellProductionMonitorConf
    public final static String PROP_KEY_SELECT_PANEL_TITLE = "SelectPanelTitle";
    public final static String PROP_KEY_SELECT_PANEL_COLUMN_NUM = "SelectPanelColumnNum";
    public final static String PROP_KEY_SELECT_PANEL_ROW_NUM = "SelectPanelRowNum";
    public final static String PROP_KEY_SELECT_PANEL_UNITTEMPLATE = "SelectPanelUnitTmeplate";
    public final static String PROP_KEY_SELECT_GRAPH_TITLE = "SelectGraphTitle";
    public final static String PROP_KEY_SELECT_GRAPH_COLUMN_NUM = "SelectGraphColumnNum";
    public final static String PROP_KEY_SELECT_GRAPH_ROW_NUM = "SelectGraphRowNum";
    public final static String PROP_KEY_SELECT_GRAPH_UNITTEMPLATE = "SelectGraphUnitTmeplate";
    public final static String PROP_KEY_SELECT_LIST_MAIN_TITLE_COLUMN = "SelectListMainTitleColumn";
    public final static String PROP_KEY_SELECT_LIST_SUB_TITLE_COLUMN = "SelectListSubTitleColumn";
    public final static String PROP_KEY_SELECT_LIST_UNITTEMPLATE = "SelectListUnitTemplate";
    public final static String PROP_KEY_MASTER_TITLE = "MasterColomunTitle";
    public final static String PROP_KEY_MASTER_COLUMN_NUM = "MasterColumnNum";
    public final static String PROP_KEY_MASTER_ROW_NUM = "MasterRowNum";
    public final static String PROP_KEY_MASTER_INTERVAL = "MasterInterval";
    public final static String PROP_KEY_SELECT_LIST2_UNITTEMPLATE = "SelectList2UnitTemplate";
    public final static String PROP_KEY_LIST2_TITLE_COLUMN = "SelectList2TitleColumn";
    public final static String PROP_KEY_LIST2_HOLIDAY_FILE = "SelectList2HolidayFile";
    public final static String PROP_KEY_LIST2_FONTSIZE_LIST = "SelectList2FontSizeList";
    public final static String PROP_KEY_LIST2_FONTSIZE_HEADER = "SelectList2FontSizeHeader";
    public final static String PROP_KEY_LIST2_COLUMN_WIDTH1 = "SelectList2ColumnWidth1_";
    public final static String PROP_KEY_LIST2_COLUMN_WIDTH2 = "SelectList2ColumnWidth2_";
    public final static String PROP_KEY_LIST2_COLUMN_WIDTH3 = "SelectList2ColumnWidth3_";
    public final static String PROP_KEY_LIST2_COLUMN_HIGHT = "SelectList2ColumnHight";
    public final static String DEFAULT_LIST2_HOLIDAY_FILE = System.getenv("ADFACTORY_HOME") + File.separator + "conf" + File.separator + "Calendar.ini";
    public final static String DEFAULT_LIST2_FONTSIZE_LIST = "15";
    public final static String DEFAULT_LIST2_FONTSIZE_HEADER = "40";
    public final static String DEFAULT_LIST2_COLUMN_WIDTH = "80.0";
    public final static String DEFAULT_LIST2_COLUMN_HIGHT = "40.0";
    public final static String DEFAULT_MASTER_COLUMN_NUM = "2,3,4";
    public final static String DEFAULT_MASTER_ROW_NUM = "2,3,4";
    public final static String DEFAULT_MASTER_TITLES = "オーダー番号,シリアル,ユーザー名,受注番号";
    public final static String DEFAULT_SELECT_COLUMN_NUM = "3";
    public final static String DEFAULT_SELECT_ROW_NUM = "3";
    public final static String DEFAULT_SELECT_TITLE = "シリアル";
    public final static String DEFAULT_SELECT_MAIN_TITLE = "オーダー番号";
    public final static String DEFAULT_SELECT_SUB_TITLE = "シリアル";
    public final static String DEFALT_MASTER_INTERVAL = "3000";

    // UnitImportConf
    public final static String KEY_UNIT_IMPORT_CSV_ENCODE = "unit_csv_encode";
}
