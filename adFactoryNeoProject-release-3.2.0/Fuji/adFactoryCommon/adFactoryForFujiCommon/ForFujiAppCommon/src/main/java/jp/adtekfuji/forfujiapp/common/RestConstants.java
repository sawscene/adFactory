package jp.adtekfuji.forfujiapp.common;

/**
 * REST用定数
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.13.Thr
 */
public class RestConstants {

    // RESOURCE BASIC
    public final static String PATH_DISPLAYEDSTATUS = "/mst/status";
    public final static String PATH_BREAKTIME = "/mst/breaktime";
    public final static String PATH_DELAY = "/mst/delay_reason";
    public final static String PATH_ORGANIZATION = "/organization";
    public final static String PATH_EQUIPMENT = "/equipment";
    public final static String PATH_WORK = "/work";
    public final static String PATH_WORKFLOW = "/workflow";
    public final static String PATH_KANBAN = "/kanban";
    public final static String PATH_ACTUAL = "/actual";
    public final static String PATH_AGENDA = "/agenda";
    public final static String PATH_TOPIC = "/topic";

    // RESOURCE CUSTOM
    public final static String PATH_UNIT_TEMPLATE = "/unittemplate";
    public final static String PATH_UNIT = "/unit";
    public final static String PATH_MONITOR = "/monitor";
    public final static String PATH_PANEL = "/panel";
    public final static String PATH_GRAPH = "/graph";
    public final static String PATH_LIST = "/list";

    // PATH
    public final static String PATH_SEPARATOR = "/";
    public final static String PATH_FIND = "/find";
    public final static String PATH_COUNT = "/count";
    public final static String PATH_TACTTIME = "/tacttime";
    public final static String PATH_RANGE = "/range";
    public final static String PATH_TREE = "/tree";
    public final static String PATH_HIERARCHY = "/hierarchy";
    public final static String PATH_NAME = "/name";
    public final static String PATH_STATUS = "/status";
    public final static String PATH_SEARCH = "/search";
    public final static String PATH_BASICSEARCH = "/basicsearch";
    public final static String PATH_RUSULTS = "/results";
    public final static String PATH_COPY = "/copy";
    public final static String PATH_REPORT = "/report";
    public final static String PATH_EXIST = "/exist";
    public final static String PATH_FLOW = "/flow";
    public final static String PATH_SEPARATE = "/separate";
    public final static String PATH_ANCESTORS = "/ancestors";
    public final static String PATH_ID_TARGET = "/%s";

    // QUERY
    public final static String QUERY_PATH = "?";
    public final static String QUERY_AND = "&";
    public final static String QUERY_FROM_TO = "from=%s&to=%s";
    public final static String QUERY_NAME = "name=%s";
    public final static String QUERY_ID = "id=%s";
    public final static String QUERY_USER = "user=%s";
    public final static String QUERY_DATE = "date=%s";
    public final static String QUERY_KANBANID = "kanbanid=%s";
    public final static String QUERY_UNITID = "unitId=%s";
    public final static String QUERY_TITLE_OPTION = "title_option=%s";
    public final static String QUERY_TITLE_MAIN = "mainTitle=%s";
    public final static String QUERY_TITLE_SUB = "subTitle=%s";
    public final static String QUERY_HAS_CHILD = "hasChild=%s";
    public final static String QUERY_ALL = "all=%s";
}
