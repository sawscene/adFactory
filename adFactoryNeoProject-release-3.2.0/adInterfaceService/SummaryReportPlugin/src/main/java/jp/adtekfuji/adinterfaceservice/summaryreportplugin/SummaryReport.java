/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.summaryreportplugin;

import java.text.SimpleDateFormat;
import java.util.*;

import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.ResourceInfoFacade;
import adtekfuji.clientservice.SummaryReportFacade;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;

import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import jp.adtekfuji.adFactory.adinterface.command.SummaryReportNoticeCommand;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportInfoEntityElement;
import jp.adtekfuji.adFactory.enumerate.ResourceTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SendFrequencyEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.mail.MailProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.stream.Collectors.*;

/**
 * サマリーレポート
 *
 * @author yu.nara
 */
public class SummaryReport extends Thread implements AdInterfaceServiceInterface {

    /**
     * サービス名
     */
    private final String SERVICE_NAME = "SummaryReport";

    /**
     * サマリーレポート設定 (properties)
     */
    private final String PROPERTY_NAME = "summaryReport.properties";

    /**
     * メールサーバアドレスの取得キー
     */
    private final static String KEY_MAIL_HOST = "MailHost";

    /**
     * メールサーバアドレスの取得キー
     */
    private final static String KEY_MAIL_PORT = "MailPort";

    /**
     * メール認証の取得キー
     */
    private final static String KEY_MAIL_ENABLE_AUTH = "MailEnableAuth";

    /**
     * メール文字コードの取得キー
     */
    private final static String KEY_MAIL_CHARSET = "MailCharset";

    /**
     * メールユーザ名の取得キー
     */
    private final static String KEY_MAIL_USER = "MailUser";

    /**
     * メールパスワードの取得キー
     */
    private final static String KEY_MAIL_PASSWORD = "MailPassword";

    /**
     * メールTLSの取得キー
     */
    private final static String KEY_MAIL_ENABLE_TLS = "MailEnableTLS";

    /**
     * メール接続タイムアウトの取得キー
     */
    private final static String KEY_MAIL_CONNECTION_TIMEOUT = "MailConnectionTimeout";

    /**
     * メールタイムアウト時間の取得キー
     */
    private final static String KEY_MAIL_TIMEOUT = "MailTimeout";

    /**
     * メール送信者の取得キー
     */
    private final static String KEY_MAIL_FROM = "MailFrom";

    /**
     * 送信メールポートのデフォルト値
     */
    private final static String DEFAULT_MAIL_PORT = "25";

    /**
     * 送信メール文字コードのデフォルト値
     */
    private final static String DEFAULT_MAIL_CHARSET = "MS932";

    /**
     * 認証のデフォルト値
     */
    private final static String DEFAULT_MAIL_ENABLE_AUTH = "false";

    /**
     * TLSのデフォルト値
     */
    private final static String DEFAULT_MAIL_ENABLE_TLS = "false";

    /**
     * 接続タイムアウト時間のデフォルト値
     */
    private final static String DEFALUT_MAIL_CONNECTION_TIMEOUT = "30000";

    /**
     * 送信タイムアウト時間のデフォルト値
     */
    private final static String DEFALUT_MAIL_TIMEOUT = "30000";

    /**
     * サマリーレポート設定ファイル(json)の読み込み間隔(ミリ秒)の取得キー
     */
    private final static String KEY_LOAD_SETTING_INTERVAL = "LoadSettingInterval";

    /**
     * サマリーレポート設定ファイル(json)の読み込み間隔(ミリ秒)の初期値
     */
    private final static String DEFAULT_LOAD_SETTING_INTERVAL = "10000";

    /**
     * 組織情報取得用Facade
     */
    private final OrganizationInfoFacade organizationInfoFacode = new OrganizationInfoFacade();

    /**
     * ログ出力用クラス
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * 受信した実績通知コマンドを蓄積するキュー
     */
    private final LinkedList<SummaryReportNoticeCommand> recvQueue = new LinkedList<>();

    /**
     * サービス実行状態
     * <pre>
     * true：実行中、false：停止中
     * </pre>
     */
    private boolean execution = false;

    /**
     * サマリーレポート設定 (properties)
     */
    private Properties properties;

    /**
     * サマリーレポート設定ファイル(json)の定期読み込み用タイマー
     */
    private Timer timer;

    /**
     * サマリーレポート設定ファイル(json)の読み込み間隔(ミリ秒)
     */
    private Long loadSettingIntervalMS;



    /**
     * コンストラクタ
     */
    public SummaryReport() {
        // 起動時に設定を読込む
        recvQueue.add(new SummaryReportNoticeCommand(SummaryReportNoticeCommand.COMMAND.LOAD_CONFIG));
        LocaleUtils.load("locale");
    }

    /**
     * このプラグインによって実行されるアクション
     */
    @Override
    public void run() {
        // 送信データリスト
        List<Tuple<Date, SummaryReportConfigInfoEntity>> sendList = new ArrayList<>();
        int prevDay = getDay();
        final SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        while (execution) {
            synchronized (recvQueue) {
                if (recvQueue.isEmpty()) {
                    try {
                        recvQueue.wait();
                    } catch (InterruptedException ex) {
                        logger.fatal(ex, ex);
                    }
                    if (recvQueue.isEmpty()) {
                        continue;
                    }
                }

                SummaryReportNoticeCommand cmd = recvQueue.removeFirst();
                try {
                    switch (cmd.getCommand()) {
                        case SEND_MAIL: {
                            // 単独メール送信
                            SummaryReportNoticeCommand.SendMailConfig config = JsonUtils.jsonToObject(cmd.getConfig(), SummaryReportNoticeCommand.SendMailConfig.class);
                            List<SummaryReportConfigInfoEntity> entities = LoadConfig();
                            logger.info("Send TestMail {} {}", entities.size(), config.sendIndex);
                            if (entities.size() > config.sendIndex) {
                                SummaryReportConfigInfoEntity entity = entities.get(Math.toIntExact(config.sendIndex));
                                entity.setSendDate(sf.format(toDateTime(entity)));
                                this.sendSummaryReport(entity);
                            }
                        }
                        break;
                        case START_SERVER:
                        case LOAD_CONFIG: {
                            logger.info("Load Config");
                            // 本日(現在時刻以降)の報告対象を設定
                            final Date now = new Date();
                            sendList = LoadConfig()
                                    .stream()
                                    .filter(entity -> !entity.getDisable())
                                    .filter(SummaryReport::isSendToday)
                                    .map(entity -> new Tuple<>(toDateTime(entity), entity))
                                    .filter(entity -> now.before(entity.getLeft()))
                                    .collect(toList());
                            sendList.forEach(item -> item.getRight().setSendDate(sf.format(item.getLeft())));
                        }
                        break;
                        case SEND_TIME_CHECK: {
                            // 送信実施処理
                            final Date now = new Date();
                            final int nowDay = getDay(now); //現在の日にち

                            if (nowDay != prevDay) {
                                // 日を跨いだ場合は本日分を追加
                                sendList.addAll(
                                        LoadConfig()
                                                .stream()
                                                .filter(entity -> !entity.getDisable())
                                                .filter(SummaryReport::isSendToday)
                                                .map(entity -> new Tuple<>(toDateTime(entity), entity))
                                                .collect(toList()));
                                sendList.forEach(item -> item.getRight().setSendDate(sf.format(item.getLeft())));
                                prevDay = nowDay;
                            }

                            // 送信するものを振り分ける
                            Map<Boolean, List<Tuple<Date, SummaryReportConfigInfoEntity>>>
                                    summaryReportGroup =
                                    sendList.stream()
                                            .collect(groupingBy(entity -> now.before(entity.getLeft())));

                            // 次回送信分のサマリーレポート
                            sendList = summaryReportGroup.getOrDefault(true, new ArrayList<>());

                            // サマリーレポート送信
                            summaryReportGroup
                                    .getOrDefault(false, new ArrayList<>())
                                    .stream()
                                    .map(Tuple::getRight)
                                    .forEach(this::sendSummaryReport);
                        }
                        break;
                        default:
                            logger.info("undefind command {}", cmd.getCommand());
                            break;
                    }
                }catch(RuntimeException ex) {
                    logger.fatal(ex, ex);
                }
            }
        }
    }

    // 日を取得
    static private int getDay() {
        return getDay(new Date());
    }

    // 日を取得
    static private int getDay(Date date) {
        Calendar preSendDate = Calendar.getInstance();
        preSendDate.setTime(date);
        return preSendDate.get(Calendar.DATE);

    }

    static private final ResourceInfoFacade resourceInfoFacade = new ResourceInfoFacade();

    /**
     * 設定をサーバーから取得する
     * @return 設定
     */
    private List<SummaryReportConfigInfoEntity> LoadConfig() {
        for (int retry=0; retry<=5; ++retry) {
            ResourceInfoEntity resourceInfoEntity = resourceInfoFacade.findByTypeKey(ResourceTypeEnum.SUMMARY_REPORT_CONFIG, "Default");
            if (Objects.nonNull(resourceInfoEntity)) {
                return JsonUtils.jsonToObjects(resourceInfoEntity.getResourceString(), SummaryReportConfigInfoEntity[].class);
            }
            logger.fatal("error : SummaryReport Load retry {}", retry);
            try {
                Thread.sleep(5000);
            } catch(InterruptedException ex) {
                logger.fatal(ex, ex);
            }
        }
        logger.fatal("error : SummaryReport Load");
        return new ArrayList<>();
    }

    /**
     * 送信日か?
     * @param entity
     * @return true : 送信日 / false : 送信日ではない
     */
    static private boolean isSendToday(SummaryReportConfigInfoEntity entity) {
        if (Objects.isNull(entity)) {
            return false;
        }

        final SendFrequencyEnum sendFrequencyEnum = entity.getSendFrequency();

        // 毎日
        if (SendFrequencyEnum.EVERYDAY.equals(sendFrequencyEnum)) {
            return true;
        }

        // 毎月
        if (SendFrequencyEnum.MONTHLY.equals(sendFrequencyEnum)) {
            final SimpleDateFormat sdf = new SimpleDateFormat("dd");
            final int nowDate = Integer.parseInt(sdf.format(new Date()));
            final int sendDate = Integer.parseInt(entity.getSendDate());

            Calendar cal = Calendar.getInstance();
            final int maxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            return nowDate == Math.min(sendDate, maxDate);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        final int week = cal.get(Calendar.DAY_OF_WEEK);

        switch (entity.getSendFrequency()) {
            case MONDAY: // 月曜日
                return Calendar.MONDAY == week;
            case TUESDAY: // 火曜日
                return Calendar.TUESDAY == week;
            case WEDNESDAY: // 水曜日
                return Calendar.WEDNESDAY == week;
            case THURSDAY: // 木曜日
                return Calendar.THURSDAY == week;
            case FRIDAY: // 金曜日
                return Calendar.FRIDAY == week;
            case SATURDAY: // 土曜日
                return Calendar.SATURDAY == week;
            case SUNDAY: // 日曜日
                return Calendar.SUNDAY == week;
        }
        return false;
    }

    /**
     * 時間へ変更
     * @param entity
     * @return 時間
     */
    static Date toDateTime(SummaryReportConfigInfoEntity entity) {
        final String[] times = entity.getSendTime().split(":");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if (SendFrequencyEnum.MONTHLY.equals(entity.getSendFrequency())) {
            final int sendDate = Integer.parseInt(entity.getSendDate());
            final int maxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, Math.min(sendDate, maxDate));
        }

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    SummaryReportFacade summaryReportFacade = new SummaryReportFacade();
    private void sendSummaryReport(SummaryReportConfigInfoEntity entity) {
        logger.info("start sendSummaryReport");
//        Integer timeout = this.summaryReportFacade.getReadTimeout();
        try {
//            this.summaryReportFacade.setReadTimeout(3600*1000); // 60分


            List<String> mailList = organizationInfoFacode
                    .mailingList(entity.getMails())
                    .stream()
                    .filter(mail -> !StringUtils.isEmpty(mail))
                    .collect(toList());

            if (Objects.isNull(mailList) || mailList.isEmpty()) {
                logger.fatal("mailList Is Empty");
                return;
            }

            SummaryReportInfoEntity ret = summaryReportFacade.calculate(entity);
            if (Objects.isNull(ret)) {
                logger.fatal("fatal : summaryReportFacade.calculate");
            }

            getMailProperty(this.properties)
                    .map(ReportSender::new)
                    .ifPresent(reportSender -> reportSender.send(mailList, ret));
        } finally {
            logger.info("end sendSummaryReport");
            //this.summaryReportFacade.setReadTimeout(timeout);
        }
    }

    /**
     * サービスを開始する。
     *
     * @throws Exception
     */
    @Override
    public void startService() throws Exception {
        logger.info("Start SummaryReport start.");
        AdProperty.load(SERVICE_NAME, PROPERTY_NAME);
        this.properties = AdProperty.getProperties(SERVICE_NAME);
        this.loadSettingIntervalMS = Long.parseLong(this.properties.getProperty(KEY_LOAD_SETTING_INTERVAL, DEFAULT_LOAD_SETTING_INTERVAL));

        this.timer = new Timer();
        this.schedule();

        if (!this.execution) {
            this.execution = true;
            super.start();
        }
    }

    /**
     * サービスを停止する。
     *
     * @throws Exception
     */
    @Override
    public void stopService() throws Exception {
        logger.info("Stop SummaryReport start.");
        this.timer.cancel();
        this.execution = false;
        synchronized (this.recvQueue) {
            this.recvQueue.notify();
        }
        super.join();
    }

    /**
     * 通知コマンドを受信した。
     *
     * @param command 通知コマンド
     */
    @Override
    public void notice(Object command) {

        logger.info("Notice SummaryReport start.");
        if (command instanceof SummaryReportNoticeCommand) {
            SummaryReportNoticeCommand cmd = (SummaryReportNoticeCommand) command;
            synchronized (recvQueue) {
                recvQueue.add(cmd);
                recvQueue.notify();
            }
        }
    }

    /**
     * サービス名を取得する。
     *
     * @return サービス名
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    /**
     * 定期処理を実行する。
     */
    private void schedule() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

        TimerTask task = new TimerTask() {
            /**
             * このタイマー・タスクによって実行されるアクション
             */
            @Override
            public void run() {
                SummaryReportNoticeCommand cmd = new SummaryReportNoticeCommand(SummaryReportNoticeCommand.COMMAND.SEND_TIME_CHECK);
                synchronized (recvQueue) {
                    if (recvQueue
                            .stream()
                            .noneMatch(entity->SummaryReportNoticeCommand.COMMAND.SEND_TIME_CHECK.equals(entity.getCommand()))) {
                        recvQueue.add(cmd);
                        recvQueue.notify();
                    }
                }
            }
        };

        this.timer.schedule(task, 0, this.loadSettingIntervalMS);
    }

    /**
     * メール設定情報を取得する。
     *
     * @return メール設定情報(設定値取得不可の場合はnull)
     */
    static Optional<MailProperty> getMailProperty(Properties properties) {

        // メール関連の設定値を取得
        final String mailHost = properties.getProperty(KEY_MAIL_HOST, "");
        final String mailFrom = properties.getProperty(KEY_MAIL_FROM, "");

        // メール設定が取得不可の場合は処理中止
        if (StringUtils.isEmpty(mailHost) || StringUtils.isEmpty(mailFrom)) {
            return Optional.empty();
        }

        final String mailPortStr = properties.getProperty(KEY_MAIL_PORT, DEFAULT_MAIL_PORT);
        final String mailIsEnableAuthStr = properties.getProperty(KEY_MAIL_ENABLE_AUTH, DEFAULT_MAIL_ENABLE_AUTH);
        final String mailCharset = properties.getProperty(KEY_MAIL_CHARSET, DEFAULT_MAIL_CHARSET);
        final String mailUser = properties.getProperty(KEY_MAIL_USER, "");
        final String mailPassword = properties.getProperty(KEY_MAIL_PASSWORD, "");
        final String mailIsEnableTLSStr = properties.getProperty(KEY_MAIL_ENABLE_TLS, DEFAULT_MAIL_ENABLE_TLS);
        final String mailConnectionTimeoutStr = properties.getProperty(KEY_MAIL_CONNECTION_TIMEOUT, DEFALUT_MAIL_CONNECTION_TIMEOUT);
        final String mailTimeoutStr = properties.getProperty(KEY_MAIL_TIMEOUT, DEFALUT_MAIL_TIMEOUT);



        final Integer mailPort = Integer.valueOf(mailPortStr);
        final boolean mailIsEnableAuth = Boolean.parseBoolean(mailIsEnableAuthStr);
        final boolean mailIsEnableTLS = Boolean.parseBoolean(mailIsEnableTLSStr);
        final Integer mailConnectionTimeout = Integer.valueOf(mailConnectionTimeoutStr);
        final Integer mailTimeout = Integer.valueOf(mailTimeoutStr);

        // メール設定情報をセット
        MailProperty prop = new MailProperty();
        prop.setHost(mailHost);
        prop.setPort(mailPort);
        prop.setIsEnableAuth(mailIsEnableAuth);
        prop.setUser(mailUser);
        prop.setPassword(mailPassword);
        prop.setIsEnableTLS(mailIsEnableTLS);
        prop.setConnectionTimeout(mailConnectionTimeout);
        prop.setTimeout(mailTimeout);
        prop.setCharset(mailCharset);
        prop.setMailFrom(mailFrom);

        return Optional.of(prop);
    }
}
