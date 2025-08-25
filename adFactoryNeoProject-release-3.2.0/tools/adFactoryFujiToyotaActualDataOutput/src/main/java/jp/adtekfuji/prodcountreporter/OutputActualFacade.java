/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter;

import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

import com.google.gson.GsonBuilder;
import io.vavr.control.Either;
import jp.adtekfuji.prodcountreporter.json.*;
import jp.adtekfuji.prodcountreporter.mail.MailProperty;
import jp.adtekfuji.prodcountreporter.mail.MailUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;

/**
 * @author yu.nara
 */
public class OutputActualFacade {

    private final static Logger logger = LogManager.getLogger();
    private final static String LINE_SEPARATOR = "\r\n";
    private final static String SQL_URL = "jdbc:postgresql://%s:15432/adFactoryDB2";
    private final static String SQL_USER = "postgres";
    private final static String SQL_PASSWORD = "@dtek1977";

    private final static String ERROR = "Error";
    private final static String ERROR_MESSAGE = "ErrorMessage";
    private final static String ORDER = "[[Order, Serial, Quantity]]";
//    private final static String SERIAL = "Serial";
    private final static String PROCESS_CODE = "ProcessCode";
    private final static String MACHINE_NO = "MachineNo";
    private final static String WORKERS = "Workers";
    private final static String START_DATE_TIME = "StartDateTime";
    private final static String END_DATE_TIME = "EndDateTime";
    private final static List<String> errorMailMessage = Arrays.asList(ERROR, ORDER, PROCESS_CODE, MACHINE_NO, WORKERS, START_DATE_TIME, END_DATE_TIME, ERROR_MESSAGE);

    private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final OutputActualInfo info;

    private final static Map<String, String> converter = new HashMap<String, String>() {{
        put("OrderNo", "製造オーダ");
        put("Serial", "製造オーダシリアル");
        put("ProcessCode", "工程コード");
        put("MachineNo", "機械番号");
        put("Code", "コード");
        put("Quantity","数量");
    }};

    public enum SEARCH_TYPE {
        INTERVAL_TIME,
        FROM_TO_SEARCH,
        LAST_UPDATE
    }


    /**
     * @param inList 入力データ
     * @return Request
     */
    static Either<Map<String, String>, Request> createRequest(List<List<Map<String, String>>> inList, String worker) {

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inList.get(0).sort(Comparator.comparing(l -> {
            try {
                return df.parse(l.get("implement_datetime"));
            } catch (ParseException e) {
                return new Date();
            }
        }));

        Map<String, List<String>> statusCheck = new HashMap<String, List<String>>() {{
            put("WORKING", Arrays.asList("WORKING"));
            put("SUSPEND", Arrays.asList("COMPLETION", "SUSPEND"));
            put("COMPLETION", Arrays.asList("SUSPEND"));
            put("FIRST", Arrays.asList("WORKING"));
        }};

        Request request = new Request();
        try {
            request.Orders
                    = new ArrayList<>(
                    inList.stream()
                            .map(l -> l.get(0))
                            .collect(groupingBy(
                                    l -> l.get(converter.get("OrderNo")) + ":" + l.get(converter.get("Serial")),
                                    collectingAndThen(
                                            toList(),
                                            list -> {
                                                final Integer quantity
                                                        = list.stream().anyMatch(l2 -> !l2.containsKey(converter.get("Quantity")))
                                                        ? null
                                                        : list.stream().map(l2 -> l2.getOrDefault(converter.get("Quantity"), "0")).mapToInt(Integer::parseInt).sum();

                                                return new Order(
                                                        list.get(0).get(converter.get("OrderNo")),
                                                        StringUtils.isEmpty(list.get(0).get(converter.get("Serial"))) ? "" : String.format("%7s", list.get(0).get(converter.get("Serial"))).replace(" ", "0"),
                                                        Objects.isNull(quantity) ? null : Integer.toString(quantity));
                                            }
                                    )))
                            .values());

            List<Map<String, String>> in = inList.get(0);
            if (!StringUtils.equals(in.get(0).get("organization_identify"), in.get(0).get("organization_name"))) {
                worker = in.get(0).get("organization_identify");
            }

            request.StartDateTime = df.parse(in.get(0).get("implement_datetime"));
            request.EndDateTime = df.parse(in.get(in.size() - 1).get("implement_datetime"));
            request.MainWorker = worker;
            request.Workers.add(request.MainWorker);
//            request.OrderNo = in.get(0).get(converter.get("OrderNo"));
//            request.Serial = in.get(0).get(converter.get("Serial"));
            request.ProcessCode = in.get(0).get(converter.get("ProcessCode"));
            request.MachineNo
                    = in.get(0).containsKey(converter.get("MachineNo"))
                    ? String.format("%2s", in.get(0).get(converter.get("MachineNo"))).replace(" ", "0")
                    : null;
            request.Note = "adFactory";

//            if (Objects.isNull(request.OrderNo)) request.OrderNo = "";
//            if (Objects.isNull(request.Serial)) request.Serial = "";
            if (Objects.isNull(request.ProcessCode)) request.ProcessCode = "";
            if (Objects.isNull(request.MachineNo)) request.MachineNo = "";
            if (Objects.isNull(request.MainWorker)) request.MainWorker = "";

            String beforeStatus = "FIRST";
            for (int n = 0; n < in.size(); ++n) {
                if (statusCheck.get(in.get(n).get("actual_status")).contains(beforeStatus)) {
                    Map<String, String> err = request.createMessage();
                    err.put(ERROR, "状態遷移異常");
                    return Either.left(err);
                }

                if (beforeStatus.equals("SUSPEND") || beforeStatus.equals("COMPLETION")) {
                    Interrupt interrupt = new Interrupt();
                    interrupt.StartDateTime = df.parse(in.get(n - 1).get("implement_datetime"));
                    interrupt.EndDateTime = df.parse(in.get(n).get("implement_datetime"));
                    request.Interrupts.add(interrupt);
                }

                beforeStatus = in.get(n).get("actual_status");
            }

            return Either.right(request);
        } catch (ParseException ex) {
            Map<String, String> err = request.createMessage();
            err.put(ERROR, "時間変換に失敗");
            err.put(ERROR_MESSAGE, ex.getMessage());
            return Either.left(err);
        }
    }


    /**
     * データチェック
     *
     * @param request データチェックをする対象
     * @return データチェック結果
     */
    private static Either<Map<String, String>, Request> CheckRequestData(Request request) {
        final List<String> message = request.CheckData();

        if (message.isEmpty()) {
            return Either.right(request);
        }

        Map<String, String> err = request.createMessage();
        err.put(ERROR, "データチェック異常");
        err.put(ERROR_MESSAGE, String.join(",", "異常データ : " + String.join(", ", message)));
        logger.fatal(err);
        return Either.left(err);
    }

    public OutputActualFacade(OutputActualInfo info) {
        this.info = info;
    }

    /**
     * adFactoryDBより実績を取得
     *
     * @param searchType 取得するタイプ
     * @return SQL文
     */
    private String createGetActualResultQuery(SEARCH_TYPE searchType) {
        if (SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            return "SELECT distinct(k.actual_id), k.kanban_id, k.work_kanban_id, k.rework_num, k.actual_status, k.implement_datetime, k.actual_add_info, tk.kanban_add_info, tw.work_kanban_add_info, mo.organization_name, mo.organization_identify, me.equipment_add_info, k.interrupt_reason " +
                    "FROM trn_actual_result k " +
                    "JOIN (SELECT i.work_kanban_id, i.rework_num " +
                    "FROM trn_work_kanban w " +
                    "JOIN (SELECT a.work_kanban_id, a.rework_num " +
                    "FROM trn_actual_result a " +
                    "WHERE a.implement_datetime BETWEEN '" + df.format(info.getFromSearchDatetime()) + "' AND '" + df.format(info.getToSearchDatetime()) +
                    "' AND a.actual_status = 'COMPLETION') i " +
                    "ON w.work_kanban_id=i.work_kanban_id AND (w.rework_num <> i.rework_num OR w.work_status = 'COMPLETION')) a " +
                    "ON k.work_kanban_id = a.work_kanban_id AND (k.rework_num = a.rework_num OR (k.rework_num ISNULL AND a.rework_num ISNULL)) " +
                    "JOIN trn_kanban tk ON tk.kanban_id=k.kanban_id " +
                    "JOIN trn_work_kanban tw ON tw.work_kanban_id=k.work_kanban_id " +
                    "LEFT JOIN mst_organization mo ON mo.organization_id = k.organization_id " +
                    "LEFT JOIN mst_equipment me ON me.equipment_id = k.equipment_id";
        }


        // プロパティ－ロード時と変更が無かった場合
        if (info.isNotChangedLastUpdateTime() && !"0".equals(info.getFromLastSearchActualId())) {
            return "SELECT distinct(k.actual_id), k.kanban_id, k.work_kanban_id, k.rework_num, k.actual_status, k.implement_datetime, k.actual_add_info, tk.kanban_add_info, tw.work_kanban_add_info, mo.organization_name, mo.organization_identify, me.equipment_add_info, k.interrupt_reason " +
                    "FROM trn_actual_result k " +
                    "JOIN (SELECT i.work_kanban_id, i.rework_num " +
                    "FROM trn_work_kanban w " +
                    "JOIN (SELECT a.work_kanban_id, a.rework_num " +
                    "FROM trn_actual_result a " +
                    "WHERE a.actual_id BETWEEN " + (Integer.parseInt(info.getFromLastSearchActualId()) + 1) + " AND (select last_value from trn_actual_result_actual_id_seq)" +
                    " AND a.actual_status = 'COMPLETION') i " +
                    "ON w.work_kanban_id=i.work_kanban_id AND (w.rework_num <> i.rework_num OR w.work_status = 'COMPLETION')) a " +
                    "ON k.work_kanban_id = a.work_kanban_id AND (k.rework_num = a.rework_num OR (k.rework_num ISNULL AND a.rework_num ISNULL))" +
                    "JOIN trn_kanban tk ON tk.kanban_id=k.kanban_id " +
                    "JOIN trn_work_kanban tw ON tw.work_kanban_id=k.work_kanban_id " +
                    "LEFT JOIN mst_organization mo ON mo.organization_id = k.organization_id " +
                    "LEFT JOIN mst_equipment me ON me.equipment_id = k.equipment_id";
        }

        return "SELECT distinct(k.actual_id), k.kanban_id, k.work_kanban_id, k.rework_num, k.actual_status, k.implement_datetime, k.actual_add_info, tk.kanban_add_info, tw.work_kanban_add_info, mo.organization_name, mo.organization_identify, me.equipment_add_info, k.interrupt_reason " +
                "FROM trn_actual_result k " +
                "JOIN (SELECT i.work_kanban_id, i.rework_num " +
                "FROM trn_work_kanban w " +
                "JOIN (SELECT a.work_kanban_id, a.rework_num " +
                "FROM trn_actual_result a " +
                "WHERE a.implement_datetime BETWEEN '" + df.format(info.lastUpdateTimeProperty().get()) + "' AND '" + df.format(new Date()) +
                "' AND a.actual_status = 'COMPLETION') i " +
                "ON w.work_kanban_id=i.work_kanban_id AND (w.rework_num <> i.rework_num OR w.work_status = 'COMPLETION')) a " +
                "ON k.work_kanban_id = a.work_kanban_id AND (k.rework_num = a.rework_num OR (k.rework_num ISNULL AND a.rework_num ISNULL)) " +
                "JOIN trn_kanban tk ON tk.kanban_id=k.kanban_id " +
                "JOIN trn_work_kanban tw ON tw.work_kanban_id=k.work_kanban_id " +
                "LEFT JOIN mst_organization mo ON mo.organization_id = k.organization_id " +
                "LEFT JOIN mst_equipment me ON me.equipment_id = k.equipment_id";
    }


    /**
     * 工数実績ファイルを作成して、HTTP転送する。
     *
     * @param searchType 出力タイプ
     * @return 報告数
     */
    public int output(SEARCH_TYPE searchType) {
        logger.info("Starting OutputActualFacade::output : {}", searchType);

        Date fromDate;
        Date toDate;
        if (SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            fromDate = info.getFromSearchDatetime();
            toDate = info.getToSearchDatetime();
        } else {
            fromDate = info.lastUpdateTimeProperty().get();
            toDate = new Date();
        }

        final Either<List<Map<String, String>>, Integer> ret = implOutput(searchType);

        // 正常
        if (ret.isRight()) {
            logger.info("Starting OutputActualFacade::output : success");
            return ret.get();
        }

        // 異常発生
        sendErrorMail(fromDate, toDate, ret.getLeft());
        return -1;
    }

    /**
     * 工数連携システムへ実績報告を行う
     *
     * @param searchType 報告タイプ
     * @return 報告結果
     */
    private Either<List<Map<String, String>>, Integer> implOutput(SEARCH_TYPE searchType) {

        String sql = createGetActualResultQuery(searchType);

        // SQL接続
        Either<String, Optional<DataTable>> optActualResult = SQLUtil.connect(String.format(SQL_URL, info.getAdFactoryAddress()), SQL_USER, SQL_PASSWORD,
                conn -> {
                    Statement stmt = conn.createStatement();
                    // DBからデータ受信
                    return DataTable.create(stmt.executeQuery(sql));
                });

        if (optActualResult.isLeft()) {
            Map<String, String> err = new HashMap<>();
            err.put(ERROR, "データベース接続失敗");
            err.put(ERROR_MESSAGE, optActualResult.getLeft());
            logger.fatal(err);
            return Either.left(Collections.singletonList(err));
        }

        if (!optActualResult.get().isPresent()) {
            Map<String, String> err = new HashMap<>();
            err.put(ERROR, "SQL構文の解析異常しました。開発者に問い合わせてください。");
            logger.fatal(err);
            return Either.left(Collections.singletonList(err));
        }

        // 取得結果をMapへ変換
        List<Map<String, String>> actualResultList = optActualResult.get().get().toMapList();

        final Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
        // 追加情報もMapへ展開
        actualResultList.forEach(l -> {
            Map<String, String> addInfoMap =
                    Stream.of(gson.fromJson(l.get("kanban_add_info"), AddInfo[].class), // カンバン追加情報
                                    gson.fromJson(l.get("work_kanban_add_info"), AddInfo[].class), // 工程カンバン追加情報
                                    gson.fromJson(l.get("equipment_add_info"), AddInfo[].class), // 設備追加情報
                                    gson.fromJson(l.get("actual_add_info"), AddInfo[].class))// 作業実績追加情報
                            .filter(Objects::nonNull)
                            .map(Arrays::asList)
                            .flatMap(Collection::stream)
                            .collect(HashMap::new, (m, addInfo) -> m.put(addInfo.key, addInfo.val), HashMap::putAll);
            l.putAll(addInfoMap);
        });


        actualResultList.forEach(item -> logger.info(
                "DEB date:{} order:{} wk_id:{} statu:{} reason:{} multi:{}",
                item.get("implement_datetime"),
                item.get("製造オーダ"),
                item.get("work_kanban_id"),
                item.get("actual_status"),
                item.get("interrupt_reason"),
                item.get("Multi")));


        // 作業開始からのペア探索用マップを作成 カンバンID 繰り返し, 作業者
        Map<String, List<Map<String, String>>> actualResultMap
                = actualResultList
                .stream()
                .filter(l->!StringUtils.equals(l.get("interrupt_reason"),"FORCED"))
                .collect(groupingBy(l->l.get("work_kanban_id") + ":" + l.get("rework_num") + ":" + l.get("organization_identify")));

        Map<Integer, Integer> actualIdMap = new HashMap<>();
        List<List<List<Map<String, String>>>> workGroup = new ArrayList<>();
        actualResultMap
                .values()
                .forEach(l -> {
                    // 同一作業でグループ化
                    // 実勢idと連立番号を取得
                    List<Integer> multiList
                            = l
                            .stream()
                            .map(l2 -> l2.computeIfAbsent("Multi", l2_ -> l2.get("actual_id")))
                            .map(l2 -> l2.split(","))
                            .map(Arrays::asList)
                            .flatMap(Collection::stream)
                            .map(Integer::parseInt)
                            .collect(toList());

                    // グループidを検索、無ければマップの最後に挿入
                    int index
                            = multiList
                            .stream()
                            .map(actualIdMap::get)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(workGroup.size());

                    // actualIdをMapを更新
                    multiList.forEach(i -> actualIdMap.put(i, index));
                    if (workGroup.size() <= index) {
                        workGroup.add(new ArrayList<>());
                    }

                    // 連立作業同士をグループ
                    workGroup.get(index).add(l);
                });


        // JSONへ変換
        List<Either<Map<String, String>, String>> requestDataList
                = workGroup
                .stream()
                .map(l -> createRequest(l, info.getMainWorker()))
                .map(request ->
                        request
                                // データチェック
                                .flatMap(OutputActualFacade::CheckRequestData)
                                // JSONへ変換
                                .map(gson::toJson))
                .collect(toList());

        // エラーメッセージ一覧
        List<Map<String, String>> errorMessages = requestDataList
                .stream()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .collect(java.util.stream.Collectors.toList());

        // 送信データ(JSON)
        List<String> jsons = requestDataList
                .stream()
                .filter(Either::isRight)
                .map(Either::get)
                .collect(toList());

        // HTTP通信
        for (String json : jsons) {
            //System.out.println(json);
            Either<String, Boolean> httpConnectResult = HttpUtil.connect(info.getHttpAddress(), con -> {
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/JSON; charset=utf-8");

                Either<String, Boolean> httpResult =
                        // 報告
                        HttpUtil.write(con, json)
                                // 応答
                                .flatMap(l -> HttpUtil.response(con))
                                .map(l -> gson.fromJson(l, Response.class))
                                .flatMap(response -> response.Code == 0
                                        ? Either.right(true) // 成功
                                        : Either.left(response.Code + ":" + response.Message)); // 異常

                if (httpResult.isLeft()) {
                    final Request request = gson.fromJson(json, Request.class);
                    Map<String, String> err = request.createMessage();
                    err.put(ERROR, "Http通信に失敗");
                    err.put(ERROR_MESSAGE, httpResult.getLeft());
                    errorMessages.add(err);
                    logger.fatal(err);
                }
                logger.info(json);
                return true;
            });

            if (httpConnectResult.isLeft()) {
                final Request request = gson.fromJson(json, Request.class);
                Map<String, String> err = request.createMessage();
                err.put(ERROR, "Http通信に失敗");
                err.put(ERROR_MESSAGE, httpConnectResult.getLeft());
                errorMessages.add(err);
                logger.fatal(err);
            }
        }

        if (!SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            //　現在時刻と取得時のactual_idを保存する
            info.setLastUpdateDateTime(new Date());
            OptionalInt optMaxActualId = actualResultList.stream()
                    .map(l -> l.getOrDefault("actual_id", "0"))
                    .mapToInt(Integer::parseInt)
                    .max();
            optMaxActualId.ifPresent(i -> info.setFromLastSearchActualId(String.valueOf(i)));
        }

        if (!info.save()) {
            Map<String, String> err = new HashMap<>();
            err.put(ERROR, "設定保存に失敗");
            errorMessages.add(err);
            logger.fatal(err);
        }


        return 0 == errorMessages.size()
                ? Either.right(jsons.size())
                : Either.left(errorMessages);
    }


    /**
     * エラー発生通知メールを送信する。
     *
     * @param fromDate 日時指定の先頭
     * @param toDate   日時指定の末尾
     */
    private void sendErrorMail(Date fromDate, Date toDate, List<Map<String, String>> message) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);

            String from = info.getErrorMailFrom();
            String to = info.getErrorMailTo();
            String subject = "【adFactory 工数連携】 実績情報出力エラー";

            StringBuilder content = new StringBuilder();
            content.append("adFactory 工数連携の実績情報出力でエラーが発生しました。");
            content.append(LINE_SEPARATOR);
            content.append(LINE_SEPARATOR);
            content.append("データ取り込み間隔： ");
            content.append(dateFormatter.format(fromDate));
            content.append(" ～ ");
            content.append(dateFormatter.format(toDate));
            content.append(LINE_SEPARATOR);
            content.append("エラー発生日時: ");
            content.append(dateFormatter.format(new Date()));
            content.append(LINE_SEPARATOR);
            content.append("エラー詳細:");
            content.append(LINE_SEPARATOR);
            content.append(CreateMessage(message));

            MailUtils mail = new MailUtils(prop);
            mail.send(from, to, subject, content.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    String CreateMessage(List<Map<String, String>> message) {
        return StringUtils.join(errorMailMessage, ", ") // ヘッダ作成
                + LINE_SEPARATOR
                + message.stream() // 本体作成
                .map(e -> errorMailMessage.stream()
                        .map(l -> e.getOrDefault(l, "none"))
                        .collect(joining(", ")))
                .collect(joining(LINE_SEPARATOR));
    }
}
