package adtekfuji.admanagerapp.chartplugin.common;

/**
 * 定数クラス
 *
 * @author s-heya
 */
public class Constants {

    public static final Double SCREEN_MIN_WIDTH = 1024.0;
    public static final Double SCREEN_MIN_HEIGHT = 768.0;
    public static final long REST_RANGE = 200;
    public static final String FORMAT_HHMMSS = "%s%02d:%02d:%02d";

    /**
     * プロパティID
     */
    public static final String PROPETRY_NAME = "adManagerChartPlugin";

    public static final String TIMELINE_FROM_DATE = "timeLineFromDate";
    public static final String TIMELINE_TO_DATE = "timeLineToDate";
    public static final String TIMELINE_FROM_TIME = "timeLineFromTime";
    public static final String TIMELINE_TO_TIME = "timeLineToTime";
    public static final String TIMELINE_INTERVAL = "timeLineInterval";
    public static final String TIMELINE_WORKFLOW_ID = "timeLineWorkflowId";
    public static final String TIMELINE_WORK_LIST = "timeLineWorkList";
    public static final String TIMELINE_WORK_ID = "timeLineWorkId";
    public static final String TIMELINE_TAKT_TIME = "timeLineTaktTime";

    public static final String EXPORT_CHARSET = "report_csv_encode_type";
    public static final String EXPORT_DIRECTORY = "exportDir";
    public static final String EXPORT_FILENAME_PATTERN_TIMELINE = "exportTimeLine";
    public static final String EXPORT_FILENAME_PATTERN_KANBAN = "exportKanban";
    public static final String EXPORT_FILENAME_PATTERN_WORK = "exportWork";
    public static final String EXPORT_FILENAME_PATTERN_PERSON = "exportPerson";
    public static final String DEF_EXPORT_PATTERN_TIMELINE = "TimeLine_%tY-%tm-%td_%tH%tM%tS.csv";
    public static final String DEF_EXPORT_PATTERN_KANBAN = "Statistics_Kanban_%tY-%tm-%td_%tH%tM%tS.csv";
    public static final String DEF_EXPORT_PATTERN_WORK = "Statistics_Work_%tY-%tm-%td_%tH%tM%tS.csv";
    public static final String DEF_EXPORT_PATTERN_PERSON = "Statistics_Person_%tY-%tm-%td_%tH%tM%tS.csv";

    /**
     * タクトタイムの最大値(ミリ秒)
     */
    public static final int TAKT_TIME_MAX_MILLIS = 1731599000;
}
