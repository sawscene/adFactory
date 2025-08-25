/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.summaryreportplugin;

import adtekfuji.locale.LocaleUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import adtekfuji.utility.StringUtils;
import adtekfuji.utility.Tuple;
import jp.adtekfuji.adFactory.entity.model.*;
import jp.adtekfuji.adFactory.entity.summaryreport.*;
import jp.adtekfuji.adFactory.enumerate.CategoryEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import jp.adtekfuji.adinterfaceservice.mail.MailProperty;
import jp.adtekfuji.adinterfaceservice.mail.MailUtils;
import jp.adtekfuji.adinterfaceservice.summaryreportplugin.builder.SimpleHtmlBuilder;
import org.apache.logging.log4j.LogManager;

/**
 * レポート作成
 *
 * @author shizuka.hirano
 * @author shizuka.hirano
 */
public class ReportSender {

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
     * 表の書式
     */
    static final String TABLE_STYLE = "background-color: #FFFFFF;color:#000000; font-weight: bold; border-color: #808080; border-collapse:collapse; margin-bottom: 0.5em;";

    /**
     * 表の標準カラー
     */
    static final String DEFAULT_COLOR = "#FFFFFF";

    /**
     * TitleColor
     */
    static final String TITLE_COLOR = "#d9e1f1";

    /**
     * 表タイトルの書式
     */
    static final String TITLE_STYLE = "background-color: #d9e1f1;";

    /**
     * ボタンの書式
     */
    final String BUTTON_STYLE = "height: 50px; background-color: #70ad47;";

    /**
     * リンクの書式
     */
    final String LINK_STYLE = "line-height:3; display: block; color: #000000; text-align: center; font-weight: bold; text-decoration: none; mso-border-alt:none;";

    /**
     * 警告背景色
     */
    static final String WARNING_BACKGROUND_COLOR = "background-color: %s;";

    /**
     * 警告文字色
     */
    final String WARNING_TEXT_COLOR = "color: %s;";

    /**
     * 枠線なし(左)
     */
    static final String BORDER_LEFT_NON = "border-left-style: none;";

    /**
     * 枠線なし(右)
     */
    static final String BORDER_RIGHT_NON = "border-right-style: none;";

    /**
     * 空白行
     */
    static final String BRANK_HEIGHT = "height:40px;";

    /**
     * 表の横幅
     */
    static final String TABLE_SIZE = "width: 450px;";

    /**
     * ボタンの横幅
     */
    final String BUTTON_SIZE = "width: 350px";

    /**
     * 項目名の横幅
     */
    static final String ITEM_NAME_SIZE = "padding: 4px;";

    /**
     * 項目名の横幅（ランキング）
     */
    static final String ITEM_NAME_SIZE_RANKING = "width: 170px;";

    /**
     * 順位の横幅
     */
    static final String RANKING_SIZE = "width: 30px;";

    /**
     * 値の横幅
     */
    static final String VALUE_SIZE = "padding: 4px;";

    /**
     * 中央揃え（配置）
     */
    static final String CENTER_STYLE = "align: center;";

    /**
     * 中央揃え（文字）
     */
    static final String TEXT_ARIGIN_CENTER = "text-align: center;";

    /**
     * 右揃え（文字）
     */
    static final String TEXT_ARIGIN_RIGHT = "text-align: right;";

    /**
     * 左揃え（文字）
     */
    static final String TEXT_ARIGIN_LEFT = "text-align: left;";

    /**
     * 太字
     */
    static final String FONT_BOLD = "font-weight: bold;";

    /**
     * DOCTYPE
     */
    static final String DPCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

    /**
     * 日付フォーマット
     */
    private static final String TIME_FORMAT_HHMM = "%2d:%02d";

    /**
     * リソースバンドル
     */
    private final ResourceBundle rb = ResourceBundle.getBundle("locale.locale");


    /**
     * ログ出力用クラス
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    /**
     * サマリーレポート設定(properties)
     */
    private Properties properties;

    /**
     * サマリーレポート設定(json)
     */
    private SummaryReportSetting setting;


    MailUtils mail = null;
    public ReportSender(MailProperty prop) {
        this.mail = new MailUtils(prop);
    }


    /**
     * 製品生産数
     * @param result
     * @return
     */
    static String convertNumberOfProductsProducedToHtml(CategoryEnum type, String result)
    {
        final List<NumberOfProductsProducedEntity> entities = JsonUtils.jsonToObjects(result, NumberOfProductsProducedEntity[].class);
        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();

        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);

        for (NumberOfProductsProducedEntity entity: entities) {
            productionBuilder.table(1, TABLE_STYLE);

            // 計画数
            if (Objects.nonNull(entity.planProductNumber)) {
                productionBuilder
                        .tr()
                        .td(LocaleUtils.getString("key.PlanValue"), ITEM_NAME_SIZE + TEXT_ARIGIN_LEFT + TITLE_STYLE)
                        .td(String.valueOf(entity.planProductNumber.longValue()), TEXT_ARIGIN_RIGHT + VALUE_SIZE)
                        ._tr();
            }

            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.ActualValue"), ITEM_NAME_SIZE + TEXT_ARIGIN_LEFT + TITLE_STYLE)
                    .td(String.valueOf(entity.actualProductNumber.longValue()), TEXT_ARIGIN_RIGHT + VALUE_SIZE)
                    ._tr();

            // 計画との差, 達成率
            if (Objects.nonNull(entity.planProductNumber) && entity.planProductNumber > 0.0001) {
                final double diff = entity.actualProductNumber - entity.planProductNumber;
                // 計画との差
                productionBuilder.tr()
                        .td(LocaleUtils.getString("key.PlanDifference"), ITEM_NAME_SIZE + TEXT_ARIGIN_LEFT + TITLE_STYLE)
                        .td(String.valueOf((long) diff), TEXT_ARIGIN_RIGHT + VALUE_SIZE)
                        ._tr();

                // 達成率
                final double ratio = (entity.actualProductNumber / entity.planProductNumber) * 100. ;
                final String warningColor = Objects.nonNull(entity.threshold) && ratio < entity.threshold ? "#" + entity.warningBackColor.substring(2,8) : DEFAULT_COLOR;
                final String backGroundColorStyle = String.format(WARNING_BACKGROUND_COLOR, warningColor);
                productionBuilder.tr()
                        .td(LocaleUtils.getString("key.AchievementRate"), ITEM_NAME_SIZE + TEXT_ARIGIN_LEFT + TITLE_STYLE)
                        .td(String.format("%,.1f %s", ratio, LocaleUtils.getString("key.Percent")), TEXT_ARIGIN_RIGHT + VALUE_SIZE + backGroundColorStyle)
                        ._tr();
            }
            productionBuilder._table();
        }

        return productionBuilder.toString();
    }

    /**
     * 工程生産数
     * @param type
     * @param result
     * @return
     */
    static String convertNumberOfProcessProducedToHtml(CategoryEnum type, String result)
    {
        final List<NumberOfProcessProducedEntity> entities = JsonUtils.jsonToObjects(result, NumberOfProcessProducedEntity[].class);

        // 要素
        final List<List<Tuple<String, String>>> tableElements = new ArrayList<List<Tuple<String, String>>>() {{
            // タイトル
            add(Arrays.asList(
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, "No"),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.Process")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.PlanValue")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.ActualValue")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.PlanDifference")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.AchievementRate"))
            ));

            int n = 0;
            for (NumberOfProcessProducedEntity entity : entities) {
                final String plan = Objects.isNull(entity.planProducedNumber) ? "-" : String.valueOf(entity.planProducedNumber.longValue());
                final String actual = String.valueOf(entity.actualProducedNumber.longValue());
                final Double diff =
                        (Objects.isNull(entity.planProducedNumber) || entity.planProducedNumber < 0.0001)
                                ? null
                                : entity.actualProducedNumber - entity.planProducedNumber;

                final Double ratio =
                        (Objects.isNull(entity.planProducedNumber) || entity.planProducedNumber < 0.0001)
                                ? null
                                : (entity.actualProducedNumber / entity.planProducedNumber) * 100.;
                final String color =
                        (Objects.nonNull(ratio) && Objects.nonNull(entity.threshold) && ratio < entity.threshold)
                                ? "#" + entity.warningBackColor.substring(2, 8)
                                : DEFAULT_COLOR;
                final String backGroundColorStyle = String.format(WARNING_BACKGROUND_COLOR, color);

                add(Arrays.asList(
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, String.valueOf(++n)),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_LEFT + VALUE_SIZE, entity.workName),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, plan),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, actual),
                        Objects.isNull(diff) ? new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, "-") : new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, String.valueOf(diff.longValue())),
                        Objects.isNull(diff) ? new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, "-") : new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, String.format("%,.1f %s", ratio, LocaleUtils.getString("key.Percent")))
                ));
            }
        }};

        // 計画を表示するか?
        final boolean isShowPlan = entities.stream().map(entity->entity.planProducedNumber).anyMatch(Objects::nonNull);
        final Function<List<Tuple<String, String>>, List<Tuple<String, String>>> elementSelector
                = isShowPlan
                ? Function.identity()
                : list -> Arrays.asList(list.get(0), list.get(1), list.get(3));

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();

        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);

        productionBuilder.table(1, TABLE_STYLE);
        tableElements
                .stream()
                .map(elementSelector)
                .forEach(element -> {
                    productionBuilder.tr();
                    element.forEach(item -> {
                        productionBuilder.td(item.getRight(), item.getLeft());
                    });
                    productionBuilder._tr();
                });

        productionBuilder._table();

        return productionBuilder.toString();
    }

    /**
     * 製品平均作業時間
     * @param type
     * @param result
     * @return
     */
    static String convertAverageProductWorkingHourToHtml(CategoryEnum type, String result)
    {
        final List<AverageProductWorkingHourEntity> entities = JsonUtils.jsonToObjects(result, AverageProductWorkingHourEntity[].class);


        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();

        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);

        for (AverageProductWorkingHourEntity entity: entities) {
            productionBuilder.table(1, TABLE_STYLE);

            if (Objects.nonNull(entity.planWorkProductTime)) {
                productionBuilder
                        .tr()
                        .td(LocaleUtils.getString("key.StandardTime"), ITEM_NAME_SIZE + TITLE_STYLE)
                        .td(timeToString(entity.planWorkProductTime.longValue()/1000), TEXT_ARIGIN_RIGHT + VALUE_SIZE)
                        ._tr();
            }

            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.AverageWorkTime"), ITEM_NAME_SIZE + TITLE_STYLE)
                    .td(timeToString(entity.actualWorkProductTime.longValue()/1000), TEXT_ARIGIN_RIGHT + VALUE_SIZE)
                    ._tr();

            // 計画との差, 達成率
            if (Objects.nonNull(entity.planWorkProductTime) && entity.planWorkProductTime > 0.0001) {
                final double diff = entity.planWorkProductTime - entity.actualWorkProductTime;
                // 計画との差
                productionBuilder.tr()
                        .td(LocaleUtils.getString("key.WorkTimeDescription"), ITEM_NAME_SIZE + TITLE_STYLE)
                        .td(timeToString((long) diff/1000), TEXT_ARIGIN_RIGHT + VALUE_SIZE)
                        ._tr();

                // 達成率
                final Double ratio = entity.actualWorkProductTime < 0.0001 ? null : (entity.planWorkProductTime / entity.actualWorkProductTime) * 100.;
                final String warningColor = Objects.nonNull(entity.threshold) && Objects.nonNull(ratio) && ratio < entity.threshold ? "#" + entity.warningBackColor.substring(2,8) : DEFAULT_COLOR;
                final String backGroundColorStyle = String.format(WARNING_BACKGROUND_COLOR, warningColor);
                productionBuilder.tr()
                        .td(LocaleUtils.getString("key.AchievementRate"), ITEM_NAME_SIZE + TITLE_STYLE)
                        .td(Objects.isNull(ratio) ? "-" : String.format("%,.1f %s", ratio, LocaleUtils.getString("key.Percent")), TEXT_ARIGIN_RIGHT + VALUE_SIZE + backGroundColorStyle)
                        ._tr();
            }
            productionBuilder._table();
        }

        return productionBuilder.toString();
    }

    /**
     * 工程の平均作業時間
     * @param type
     * @param result
     * @return
     */
    static String convertWorkAverageWorkTimeTimeToHtml(CategoryEnum type, String result)
    {
        List<WorkAverageWorkTimeEntity> entities = JsonUtils.jsonToObjects(result, WorkAverageWorkTimeEntity[].class);

        final List<List<Tuple<String, String>>> tableElements = new ArrayList<List<Tuple<String, String>>>() {{
            // タイトル
            add(Arrays.asList(
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, "No"),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.Process")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.StandardTime")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.AverageWorkTime")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.WorkTimeDescription")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.AchievementRate"))
            ));

            // 要素
            int n = 0;
            for (WorkAverageWorkTimeEntity entity : entities) {
                final String standardTime = Objects.isNull(entity.planWorkTime) ? "-" : timeToString(entity.planWorkTime.longValue()/1000);
                final String averageWorkTime = Objects.isNull(entity.actualWorkTime) ? "-" : timeToString(entity.actualWorkTime.longValue()/1000);
                final String diff
                        = (Objects.isNull(entity.planWorkTime) || Objects.isNull(entity.actualWorkTime))
                        ? null
                        : timeToString((long) (entity.actualWorkTime - entity.planWorkTime)/1000);
                final Double ratio
                        = (Objects.isNull(entity.planWorkTime) || Objects.isNull(entity.actualWorkTime) || entity.actualWorkTime < 0.0001)
                        ? null
                        : (entity.planWorkTime / entity.actualWorkTime) * 100.;

                final String color =
                        (Objects.nonNull(ratio) && Objects.nonNull(entity.threshold) && ratio < entity.threshold)
                                ? "#" + entity.warningBackColor.substring(2, 8)
                                : DEFAULT_COLOR;
                final String backGroundColorStyle = String.format(WARNING_BACKGROUND_COLOR, color);

                add(Arrays.asList(
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, String.valueOf(++n)),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_LEFT + VALUE_SIZE, entity.workName),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, standardTime),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, averageWorkTime),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, StringUtils.isEmpty(diff) ? "-" : diff),
                        Objects.isNull(ratio) ? new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, "-") : new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, String.format("%,.1f %s", ratio, LocaleUtils.getString("key.Percent"))
                        )));
            }
        }};


        // 計画を表示するか?
        final boolean isShowPlan = entities.stream().map(entity->entity.planWorkTime).anyMatch(Objects::nonNull);
        final Function<List<Tuple<String, String>>, List<Tuple<String, String>>> elementSelector
                = isShowPlan
                ? Function.identity()
                : list -> Arrays.asList(list.get(0), list.get(1), list.get(3));

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);
        tableElements
                .stream()
                .map(elementSelector)
                .forEach(element -> {
                    productionBuilder.tr();
                    element.forEach(item -> {
                        productionBuilder.td(item.getRight(), item.getLeft());
                    });
                    productionBuilder._tr();
                });

        productionBuilder._table();

        return productionBuilder.toString();
    }

    /**
     * 稼働率
     * @param type
     * @param result
     * @return
     */
    static String convertOverallLineUtilizationToHtml(CategoryEnum type, String result)
    {
        List<OverallLineUtilizationEntity> entities = JsonUtils.jsonToObjects(result, OverallLineUtilizationEntity[].class);
        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);

        for (OverallLineUtilizationEntity entity: entities) {
            productionBuilder.table(1, TABLE_STYLE);

            // 稼働可能時間
            if (Objects.nonNull(entity.planUtilizationTime)) {
                productionBuilder
                        .tr()
                        .td(LocaleUtils.getString("key.workingPossibleTime"), ITEM_NAME_SIZE + TITLE_STYLE)
                        .td(timeToString(entity.planUtilizationTime.longValue() / 1000), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                        ._tr();
            }

            // 実稼働時間
            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.ProductionTime"), ITEM_NAME_SIZE + TITLE_STYLE)
                    .td(timeToString(entity.actualUtilizationTime.longValue()/1000), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();

            if (Objects.nonNull(entity.planUtilizationTime)  && entity.planUtilizationTime > 0.0001) {
                final double ratio = (entity.actualUtilizationTime / entity.planUtilizationTime) * 100;
                final String warningColor = Objects.nonNull(entity.threshold) && ratio < entity.threshold ? "#" + entity.warningBackColor.substring(2,8) : DEFAULT_COLOR;
                final String backGroundColorStyle = String.format(WARNING_BACKGROUND_COLOR, warningColor);
                // 稼働率
                productionBuilder.tr()
                        .td(LocaleUtils.getString("key.OperationRate"), ITEM_NAME_SIZE + TITLE_STYLE)
                        .td(String.format("%,.1f %s", ratio, LocaleUtils.getString("key.Percent")), TEXT_ARIGIN_CENTER + VALUE_SIZE + backGroundColorStyle)
                        ._tr();
            }
            productionBuilder._table();
        }
        return productionBuilder.toString();
    }

    /**
     * 作業者の稼働率
     * @param type
     * @param result
     * @return
     */
    static String convertOperatingRatePerWorkerToHtml(CategoryEnum type, String result)
    {
        List<OperatingRatePerWorkerEntity> entities = JsonUtils.jsonToObjects(result, OperatingRatePerWorkerEntity[].class);

        final List<List<Tuple<String, String>>> tableElements = new ArrayList<List<Tuple<String, String>>>() {{
            // タイトル
            add(Arrays.asList(
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, "No"),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.authorityWoker")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.workingPossibleTime")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.ProductionTime")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.OperationRate"))
            ));

            // 要素
            int n = 0;
            for (OperatingRatePerWorkerEntity entity : entities) {
                final String workingPossibleTime = Objects.isNull(entity.planOperatingTime) ? "-" : timeToString(entity.planOperatingTime.longValue() / 1000);
                final String ProductionTime = timeToString(entity.actualOperatingTime.longValue() / 1000);
                final Double ratio =
                        (Objects.isNull(entity.planOperatingTime) || entity.planOperatingTime < 0.0001)
                                ? null
                                : (entity.actualOperatingTime / entity.planOperatingTime) * 100;
                final String color =
                        (Objects.nonNull(ratio) && Objects.nonNull(entity.threshold) && ratio < entity.threshold)
                                ? "#" + entity.warningBackColor.substring(2, 8)
                                : DEFAULT_COLOR;
                final String backGroundColorStyle = String.format(WARNING_BACKGROUND_COLOR, color);

                add(Arrays.asList(
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, String.valueOf(++n)),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_LEFT + VALUE_SIZE, entity.organizationName + "(" + entity.organizationIdentify + ")"), // 作業者
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, workingPossibleTime),
                        new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, ProductionTime),
                        Objects.isNull(ratio) ? new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_CENTER + VALUE_SIZE, "-") : new Tuple<>(backGroundColorStyle + TEXT_ARIGIN_RIGHT + VALUE_SIZE, String.format("%,.1f %s", ratio, LocaleUtils.getString("key.Percent")))
                ));
            }
        }};

        // 計画を表示するか?
        final boolean isShowPlan = entities.stream().map(entity -> entity.planOperatingTime).anyMatch(Objects::nonNull);
        final Function<List<Tuple<String, String>>, List<Tuple<String, String>>> elementSelector
                = isShowPlan
                ? Function.identity()
                : list -> Arrays.asList(list.get(0), list.get(1), list.get(3));



        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);

        tableElements
                .stream()
                .map(elementSelector)
                .forEach(element -> {
                    productionBuilder.tr();
                    element.forEach(item -> {
                        productionBuilder.td(item.getRight(), item.getLeft());
                    });
                    productionBuilder._tr();
                });

        productionBuilder._table();
        return productionBuilder.toString();
    }

    /**
     * 工程内作業のバラツキ
     * @param type
     * @param result
     * @return
     */
    static String convertVariationToHtml(CategoryEnum type, String result)
    {
        List<VariationEntity> entities = JsonUtils.jsonToObjects(result, VariationEntity[].class);

        // タイトル
        final List<String> tableTitleElements =Arrays.asList(
                "No",
                LocaleUtils.getString("key.ProcessName"),
                LocaleUtils.getString("key.itemsAverage"),
                LocaleUtils.getString("key.itemsStandardDeviation")
        );

        final List<List<Tuple<String, String>>> tableElements = new ArrayList<List<Tuple<String, String>>>() {{
            // タイトル
            add(Arrays.asList(
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, "No"),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.ProcessName")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.itemsAverage")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.itemsStandardDeviation"))
            ));

            // 要素
            int n = 0;
            for (VariationEntity entity : entities) {
                final String itemsAverage = timeToString((long) entity.average/1000);
                final String itemsStandardDeviation = timeToString((long) entity.distributed / 1000);
                add(Arrays.asList(
                        new Tuple<>(TEXT_ARIGIN_CENTER + VALUE_SIZE, String.valueOf(++n)),
                        new Tuple<>(TEXT_ARIGIN_LEFT + VALUE_SIZE, entity.workName),
                        new Tuple<>(TEXT_ARIGIN_RIGHT + VALUE_SIZE, itemsAverage),
                        new Tuple<>(TEXT_ARIGIN_RIGHT + VALUE_SIZE, itemsStandardDeviation)
                ));
            }
        }};

        final Function<List<Tuple<String, String>>, List<Tuple<String, String>>> elementSelector = Function.identity();

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);
        tableElements
                .stream()
                .map(elementSelector)
                .forEach(element -> {
                    productionBuilder.tr();
                    element.forEach(item -> {
                        productionBuilder.td(item.getRight(), item.getLeft());
                    });
                    productionBuilder._tr();
                });
        productionBuilder._table();

        return productionBuilder.toString();
    }


    /**
     * ラインバランス
     * @param type
     * @param result
     * @return
     */
    static String convertLineBalanceToHtml(CategoryEnum type, String result)
    {
        List<LineBalanceEntity> entities = JsonUtils.jsonToObjects(result, LineBalanceEntity[].class);
        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);

        for (LineBalanceEntity entity: entities) {
            productionBuilder.table(1, TABLE_STYLE);

            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.OrderProcessesName"), ITEM_NAME_SIZE + TITLE_STYLE)
                    .td(entity.workflowName)
                    ._tr();

            final String criticalPath = String.join("-", entity.criticalPath);



            // 工程順
            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.OrderProcesses"), ITEM_NAME_SIZE + TITLE_STYLE)
                    .td(criticalPath, TEXT_ARIGIN_LEFT)
                    ._tr();

            // 計画値
            if (Objects.nonNull(entity.planWorkTime)) {
                productionBuilder.tr()
                        .td(LocaleUtils.getString("key.StandardTime"), ITEM_NAME_SIZE + TITLE_STYLE)
                                .td(timeToString(entity.planWorkTime.longValue()/1000), TEXT_ARIGIN_RIGHT)
                                        ._tr();
            }

            // 平均作業時間
            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.AverageWorkTime"), ITEM_NAME_SIZE + TITLE_STYLE)
                    .td(timeToString(entity.actualWorkTime.longValue()/1000), TEXT_ARIGIN_RIGHT)
                    ._tr();

            // ラインバランス率
            productionBuilder.tr()
                    .td(LocaleUtils.getString("key.LineBalanceRate"), ITEM_NAME_SIZE + TITLE_STYLE)
                    .td(String.format("%,.0f %s", entity.lineBalanceRate, LocaleUtils.getString("key.Percent")), TEXT_ARIGIN_RIGHT)
                    ._tr();
            productionBuilder._table();
            productionBuilder.br();

        }
        return productionBuilder.toString();
    }

    static String convertInterProcessWaitingTimeToHtml(CategoryEnum type, String result)
    {
        List<InterProcessWaitingTimeEntity> entities = JsonUtils.jsonToObjects(result, InterProcessWaitingTimeEntity[].class);

        // タイトル
        final List<String> tableTitleElements =Arrays.asList(
                "No",
                LocaleUtils.getString("key.PreProcess"),
                LocaleUtils.getString("key.PostProcess"),
                LocaleUtils.getString("key.itemsAverage")
        );

        // 要素
        final List<List<Tuple<String, String>>> tableElements = new ArrayList<List<Tuple<String, String>>>() {{
            // タイトル
            add(Arrays.asList(
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, "No"),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.PreProcess")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.PostProcess")),
                    new Tuple<>(TEXT_ARIGIN_CENTER + TITLE_STYLE + ITEM_NAME_SIZE, LocaleUtils.getString("key.itemsAverage"))
            ));

            int n = 0;
            for (InterProcessWaitingTimeEntity entity : entities) {
                final String itemsAverage = timeToString(entity.waitTimeAverage.longValue() / 1000);
                add(Arrays.asList(
                        new Tuple<>(TEXT_ARIGIN_CENTER + VALUE_SIZE, String.valueOf(++n)),
                        new Tuple<>(TEXT_ARIGIN_LEFT + VALUE_SIZE, entity.fromWork),
                        new Tuple<>(TEXT_ARIGIN_LEFT + VALUE_SIZE, entity.toWork),
                        new Tuple<>(TEXT_ARIGIN_RIGHT + VALUE_SIZE, timeToString(entity.waitTimeAverage.longValue() / 1000))
                ));
            }
        }};

        final Function<List<Tuple<String, String>>, List<Tuple<String, String>>> elementSelector = Function.identity();

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);
        tableElements
                .stream()
                .map(elementSelector)
                .forEach(element -> {
                    productionBuilder.tr();
                    element.forEach(item -> {
                        productionBuilder.td(item.getRight(), item.getLeft());
                    });
                    productionBuilder._tr();
                });

        productionBuilder._table();

        return productionBuilder.toString();
    }

    static String convertInterProcessDelayRankingToHtml(CategoryEnum type, String result)
    {
        List<TimeRankingElementEntity> entities
                = JsonUtils
                .jsonToObjects(result, TimeRankingElementEntity[].class)
                .stream()
                .sorted(Comparator.comparing(entity->entity.time))
                .collect(Collectors.toList());
        Collections.reverse(entities);

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();

        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);

        final int maxRanking = 3;
        final int displayNum = Math.min(maxRanking, entities.size());
        for (int n = 0; n < displayNum; ++n) {
            final double time = entities.get(n).time/1000;
            productionBuilder.tr()
                    .td(String.valueOf(n+1), VALUE_SIZE)
                    .td(entities.get(n).name, VALUE_SIZE)
                    .td(timeToString((long)time), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();
        }

        // それ以外
        if (displayNum < entities.size()) {
            final double time = entities.subList(displayNum, entities.size()).stream().mapToDouble(entity->entity.time).sum()/1000;
            productionBuilder.tr()
                    .td("-", VALUE_SIZE)
                    .td(LocaleUtils.getString("key.Other"), VALUE_SIZE)
                    .td(timeToString((long)time), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();
        }

        // 合計
        final double time = entities.stream().mapToDouble(entity->entity.time).sum()/1000;
        productionBuilder.tr()
                .td("-", VALUE_SIZE)
                .td(LocaleUtils.getString("key.Total"), ITEM_NAME_SIZE)
                .td(timeToString((long)time), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                ._tr();

        productionBuilder._table();

        return productionBuilder.toString();
    }

    static String convertInterruptRanking(CategoryEnum type, String result)
    {
        List<TimeRankingElementEntity> entities
                = JsonUtils
                .jsonToObjects(result, TimeRankingElementEntity[].class)
                .stream()
                .sorted(Comparator.comparing(entity->entity.time))
                .collect(Collectors.toList());
        Collections.reverse(entities);

        if (entities.isEmpty()) {
            return "";
        }

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);

        final int maxRanking = 3;
        final int displayNum = Math.min(maxRanking, entities.size());
        for (int n = 0; n < displayNum; ++n) {
            final double time = entities.get(n).time/1000;
            productionBuilder.tr()
                    .td(String.valueOf(n+1), VALUE_SIZE)
                    .td(entities.get(n).name, VALUE_SIZE)
                    .td(timeToString((long)time), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();
        }

        // それ以外
        if (displayNum < entities.size()) {
            final double time = entities.subList(displayNum, entities.size()).stream().mapToDouble(entity->entity.time).sum()/1000;
            productionBuilder.tr()
                    .td("-", VALUE_SIZE)
                    .td(LocaleUtils.getString("key.Other"), ITEM_NAME_SIZE)
                    .td(timeToString((long)time), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();
        }

        // 合計
        final double time = entities.stream().mapToDouble(entity->entity.time).sum()/1000;
        productionBuilder.tr()
                .td("-", VALUE_SIZE)
                .td(LocaleUtils.getString("key.Total"), VALUE_SIZE)
                .td(timeToString((long)time), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                ._tr();

        productionBuilder._table();

        return productionBuilder.toString();
    }

    static String convertCallRanking(CategoryEnum type, String result)
    {
        List<CountRankingElementEntity> entities
                = JsonUtils.jsonToObjects(result, CountRankingElementEntity[].class)
                .stream()
                .sorted(Comparator.comparing(entity->entity.count))
                .collect(Collectors.toList());
        Collections.reverse(entities);

        SimpleHtmlBuilder productionBuilder = new SimpleHtmlBuilder();
        productionBuilder.h1(LocaleUtils.getString(type.getValue()), TITLE_STYLE);
        productionBuilder.table(1, TABLE_STYLE);

        final int maxRanking = 3;
        final int displayNum = Math.min(maxRanking, entities.size());
        for (int n = 0; n < displayNum; ++n) {
            final double count = entities.get(n).count;
            productionBuilder.tr()
                    .td(String.valueOf(n+1), VALUE_SIZE)
                    .td(entities.get(n).name, VALUE_SIZE)
                    .td(String.format("%d", entities.get(n).count), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();
        }

        // それ以外
        if (displayNum < entities.size()) {
            final double count = entities.subList(displayNum, entities.size()).stream().mapToDouble(entity->entity.count).sum();
            productionBuilder.tr()
                    .td("-")
                    .td(LocaleUtils.getString("key.Other"), ITEM_NAME_SIZE)
                    .td(String.format("%d", count), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                    ._tr();
        }

        // 合計
        final double count = entities.stream().mapToDouble(entity->entity.count).sum();
        productionBuilder.tr()
                .td("-")
                .td(LocaleUtils.getString("key.Total"), ITEM_NAME_SIZE)
                .td(String.format("%d", (long) count), TEXT_ARIGIN_CENTER + VALUE_SIZE)
                ._tr();

        productionBuilder._table();

        return productionBuilder.toString();
    }


    static String convertToHtml(SummaryReportInfoEntityElement entity) {
        final CategoryEnum type = entity.elementType;
        switch(entity.elementType) {
            // 製品の生産数
            case NUMBER_OF_PRODUCTS_PRODUCED:
                return convertNumberOfProductsProducedToHtml(type, entity.result);
                //工程の生産数
            case NUMBER_OF_PROCESSES_PRODUCED:
                return convertNumberOfProcessProducedToHtml(type, entity.result);
                // 製品の平均作業時間
            case AVERAGE_PRODUCT_WORKING_HOURS:
                return convertAverageProductWorkingHourToHtml(type, entity.result);
                //工程の平均作業時間
            case WORK_AVERAGE_WORK_TIME:
                return convertWorkAverageWorkTimeTimeToHtml(type, entity.result);
                //ライン全体の稼働率
            case OVERALL_LINE_UTILIZATION:
                return convertOverallLineUtilizationToHtml(type, entity.result);
                //作業者毎の稼働率
            case OPERATING_RATE_PER_WORKER:
                return convertOperatingRatePerWorkerToHtml(type, entity.result);
                //工程内作業のバラツキ
            case IN_PROCESS_WORK_VARIATION:
                return convertVariationToHtml(type, entity.result);
                //作業者間のバラツキ(工程内)
            case VARIATION_AMONG_WORKERS:
                return convertVariationToHtml(type, entity.result);
                //設備完のバラツキ(工程内)
            case VARIATION_IN_EQUIPMENT_COMPLETION:
                return convertVariationToHtml(type, entity.result);
                //ラインバランス
            case LINE_BALANCE:
                return convertLineBalanceToHtml(type, entity.result);
                //工程間待ち時間
            case INTER_PROCESS_WAITING_TIME:
                return convertInterProcessWaitingTimeToHtml(type, entity.result);
                //遅延ランキング
            case DELAY_RANKING:
                return convertInterProcessDelayRankingToHtml(type, entity.result);
                //中断ランキング
            case INTERRUPT_RANKING:
                return convertInterruptRanking(type, entity.result);
                //呼出ランキング
            case CALL_RANKING:
                return convertCallRanking(type, entity.result);
            default:
                return "";
        }
    }



    public void send(List<String> mailList, SummaryReportInfoEntity summaryReportInfoEntity)
    {
        logger.info("send Mail to {}", mailList);

        if (!SummaryReportInfoEntity.isValid(summaryReportInfoEntity)) {
            logger.fatal("valid Error!! {}", summaryReportInfoEntity);
            return;
        }

        SimpleHtmlBuilder builder = new SimpleHtmlBuilder();
        builder.line(DPCTYPE).html();
        builder.head().meta("utf-8")._head();
        builder.body();

        // adFactoryロゴ
        builder.line("<p style=\"font-family:Arial Black; font-size:70px; background-color:#3366cc; color:white\"> <i>adFactory</i><sub style=\"font-size:30%\">&#0174</sub> <i>Report</i></p>");

        final String element = String.join("<br>", Arrays.asList(
                LocaleUtils.getString("key.ReportName") + ": " + summaryReportInfoEntity.title,
                LocaleUtils.getString("key.Period") + ": " + summaryReportInfoEntity.period,
                LocaleUtils.getString("key.AggregateUnit") + ": " + LocaleUtils.getString(summaryReportInfoEntity.aggregateUnit),
                LocaleUtils.getString("key.PropertyName") + ": " + summaryReportInfoEntity.itemName
        ));
        builder.line("<p style=\"font-size:20px\">" + element +"</p>");


        // 本体
        summaryReportInfoEntity.summaryReportInfoEntityElements
                .stream()
                .map(ReportSender::convertToHtml)
                        .forEach(line->builder.line(line).br());
        builder.line("<div styel=\"margin-bottom: 0px; padding-bottom: 0px\">produced by </div>");
        builder.line("<div lang=\"en\" style=\"font-family:'Arial Black'; font-size:30px; color:#00438F; margin-to: 0px; padding-top: 0px\"><i>ADTEK FUJI<i></div>");

        builder._body()
                ._html();

        final String mailTitle = String.format("%s (%s)", summaryReportInfoEntity.title, summaryReportInfoEntity.period);

        ServerErrorTypeEnum ret = mail.send(mailList, "adFactory Report " + mailTitle , builder.toString());

    }

    /**
     * ミリ秒単位の時間ををH:mm形式の文字列に変換
     *
     * @param time ミリ秒単位の時間
     * @return H:mm形式文字列
     */
    private String changeTimeFormat(Long time) {
        double doubleValue = time / 1000D;
        int hour = (int) (doubleValue / 3600D);
        // 最小単位の分は切り上げ
        int min = (int) Math.ceil(doubleValue % 3600D / 60D);
        return String.format(TIME_FORMAT_HHMM, hour, min);
    }

    /**
     * 秒から適当な時間表記に変換
     * @param time 秒
     * @return 時間表記
     */
    static private String timeToString(Long time)
    {
        final long signed = time < 0 ? -1 : 1;
        time = Math.abs(time);

        final long hour = time/3600;
        time -= hour*3600;
        final long minute = time/60;
        time -= minute*60;
        final long second = time;

        if (hour>0) {
            return String.format("%d%s %d%s %d%s", signed*hour, LocaleUtils.getString("key.Hour"), minute, LocaleUtils.getString("key.time.minute"), second, LocaleUtils.getString("key.time.second"));
        }

        if (minute>0) {
            return String.format("%d%s %d%s", signed*minute, LocaleUtils.getString("key.time.minute"), second, LocaleUtils.getString("key.time.second"));
        }

        return String.format("%d%s", signed*second, LocaleUtils.getString("key.time.second"));
    }


}
