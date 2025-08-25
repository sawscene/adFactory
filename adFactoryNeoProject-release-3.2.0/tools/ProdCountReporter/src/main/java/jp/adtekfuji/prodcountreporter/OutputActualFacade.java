/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.*;

import java.util.Objects;
import java.util.Base64;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

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
    private final static String LINE_SEPARATOR = System.lineSeparator();
    private final static String SQL_URL = "jdbc:postgresql://%s:%d/adFactoryDB2";
    public Integer Port = 15432;
    private final static String SQL_USER = "postgres";
    private final static String SQL_PASSWORD = "@dtek1977";
    public final static String USUALLY_ERROR = "usuallyError";
    public final static String STATUS_DATA_ERROR = "statusDataError";
    public final static String EXCEPTION_DATA_ERROR = "exceptionDataError";
    public final static String ERROR = "Error";
    public final static String ERROR_MESSAGE = "ErrorMessage";
    public final static String ORDER = "[[指図番号, 作業/活動番号, 確認対象歩留, 転記日付]]";
    private final static String EXCEPTION_ORDER = "[[指図番号, 作業/活動番号, 確認対象歩留, 転記日付, 実績ID]]";
    public final static String RESPONS_ORDER = "[[指図番号, 作業/活動番号, 確認対象歩留, 転記日付, メッセージクラス, メッセージ番号]]";

    public final static String ORDER_NUMBER = "order_number"; // 指図番号
    public final static String WORK_ID = "work_id"; // 作業/活動番号
    public final static String QUANTITY = "quantity"; // 確認対象歩留
    public final static String COMP_DATETIME = "comp_datetime"; // 完了日時
    public final static String ACTUAL_ID = "actual_id"; // 実績ID


    private final static List<String> errorMailMessage = Arrays.asList(ERROR, ORDER, ERROR_MESSAGE);
    private final static List<String> exceptionDataErrorMailMessage = Arrays.asList(ERROR, EXCEPTION_ORDER, ERROR_MESSAGE);
    private final static List<String> statusErrorMailMessage = Arrays.asList(ERROR, RESPONS_ORDER, ERROR_MESSAGE);
    private final static Map<String, List<String>> mailMessage = new HashMap<>(){{
        put(USUALLY_ERROR, errorMailMessage);
        put(STATUS_DATA_ERROR, statusErrorMailMessage);
        put(EXCEPTION_DATA_ERROR, exceptionDataErrorMailMessage);
    }};


    private final static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final OutputActualInfo info;

    public enum SEARCH_TYPE {
        INTERVAL_TIME,
        FROM_TO_SEARCH,
        LAST_UPDATE
    }

    /**
     * データチェック
     *
     * @param request データチェックをする対象
     * @return データチェック結果
     */
    private static Either<Map<String, String>, Request> CheckRequestData(Request request) {

        return Either.right(request);

//        final List<String> message = request.CheckData();
//
//        if (message.isEmpty()) {
//            return Either.right(request);
//        }
//
//        Map<String, String> err = request.createMessage();
//        err.put(ERROR, "データチェック異常");
//        err.put(ERROR_MESSAGE, String.join(",", "異常データ : " + String.join(", ", message)));
//        logger.fatal(err);
//        return Either.left(err);
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

        String sqlCondition;
        if (SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            sqlCondition = "tar.implement_datetime BETWEEN '" + df.format(info.getFromSearchDatetime()) + "' AND '" + df.format(info.getToSearchDatetime()) + "'";
        } else if (info.isNotChangedLastUpdateTime() && !"0".equals(info.getFromLastSearchActualId())) {
            sqlCondition = "tar.actual_id BETWEEN " + (Integer.parseInt(info.getFromLastSearchActualId()) + 1) + " AND (select last_value from trn_actual_result_actual_id_seq)";
        } else {
            sqlCondition = "tar.implement_datetime BETWEEN '" + df.format(info.lastUpdateTimeProperty().get()) + "' AND '" + df.format(new Date()) + "'";
        }

        return "SELECT x.val AS order_number," // 指図番号
                + " y.val AS quantity," // 確認対象歩留
                + " z.val AS work_id," // 作業/活動番号
                + " tk.comp_datetime AS comp_datetime," // 日付
                + " tar.actual_id AS actual_id" // 実績ID
                + " FROM trn_actual_result tar"
                + " JOIN trn_kanban tk ON tk.kanban_id = tar.kanban_id"
                + " JOIN JSONB_TO_RECORDSET(tk.kanban_add_info) AS w(key TEXT, val TEXT) ON w.key = '完了数連携' AND w.val = 'YES'"
                + " JOIN JSONB_TO_RECORDSET(tk.kanban_add_info) AS x(key TEXT, val TEXT) ON x.key = '指図番号'"
                + " JOIN JSONB_TO_RECORDSET(tk.kanban_add_info) AS y(key TEXT, val TEXT) ON y.key = '数量' AND y.val != '0'"
                + " JOIN JSONB_TO_RECORDSET(tk.kanban_add_info) AS z(key TEXT, val TEXT) ON z.key = '作業番号'"
                + " WHERE " + sqlCondition
                + " AND tar.actual_status = 'COMPLETION'"
                + " AND tar.implement_datetime = tk.actual_comp_datetime"
                + " AND tk.kanban_status = 'COMPLETION'";
    }

    /**
     * 工数実績ファイルを作成して、HTTP転送する。
     *
     * @param searchType 出力タイプ
     * @return 報告数
     */
    public int output(SEARCH_TYPE searchType) {

        Date fromDate;
        Date toDate;
        if (SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            fromDate = info.getFromSearchDatetime();
            toDate = info.getToSearchDatetime();
        } else {
            fromDate = info.lastUpdateTimeProperty().get();
            toDate = new Date();
        }

        logger.info("Starting OutputActualFacade::output : {}", searchType);
        logger.info("Intake period：" + fromDate + " ～ " + toDate);

        final Either<Map<String, List<Map<String, String>>>, Integer> ret = implOutput(searchType);

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
     * 指定したヘッダー名に対して、大文字小文字を無視して一致するヘッダーを取得する。
     *
     * @param headers    ヘッダ情報を格納したマップ
     * @param headerName 取得したいヘッダーの名前
     * @return 指定したヘッダー名と一致するヘッダー値のリストを格納したOptional
     */
    private Optional<List<String>> getHeaderIgnoreCase(Map<String, List<String>> headers, String headerName) {
        return headers.entrySet()
                .stream()
                .filter(entry -> Objects.nonNull(entry.getKey()))
                .filter(entry -> entry.getKey().equalsIgnoreCase(headerName))
                .findFirst()
                .map(Map.Entry::getValue);
    }


    /**
     * CSRFトークンを取得するメソッド。
     * 指定されたURLにGETリクエストを送信し、認証情報を使用してCSRFトークンを取得します。
     * トークンが取得できない場合や通信に失敗した場合は、エラーメッセージを返します。
     *
     * @param url           CSRFトークンを取得するためのリクエストURL
     * @param authorization ベーシック認証用の認証情報
     * @return トークン取得に成功した場合はTokenResponse、失敗した場合はエラー情報を収めたMapを持つEitherオブジェクト
     */
    private Either<Map<String, String>, TokenResponse> getCSRFToken(String url, String authorization) {
        logger.info("getCSRFToken url : {}", url);

        Either<String, Either<Map<String, String>, TokenResponse>> httpGetConnectResult = HttpUtil.connect(url, conn -> {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("authorization", "Basic " + authorization);
            conn.setRequestProperty("x-csrf-token", "Fetch");

            // トークンの取得
            Either<String, String> httpResult = HttpUtil.response(conn);
            if (httpResult.isLeft()) {
                Map<String, String> err = new HashMap<>();
                err.put(ERROR, "トークン取得のHttp通信に失敗");
                err.put(ERROR_MESSAGE, HttpUtil.response(conn).getLeft());
                return Either.left(err);
            }

            // トークンを取出し
            Optional<List<String>> csrfTokens = getHeaderIgnoreCase(conn.getHeaderFields(), "x-csrf-token");
            if (csrfTokens.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put(ERROR, "トークンの取得に失敗");
                return Either.left(err);
            }

            Optional<List<String>> cookie = getHeaderIgnoreCase(conn.getHeaderFields(), "set-cookie");
            if (cookie.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put(ERROR, "セッションIDの取得に失敗");
                return Either.left(err);
            }

            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.xCsrfToken = csrfTokens.get().getFirst();
            cookie.get()
                    .stream()
                    .filter(item -> item.startsWith("SAP_SESSIONID"))
                    .findFirst()
                    .ifPresent(item -> tokenResponse.sessionID = item);

            return Either.right(tokenResponse);
        });

        if (httpGetConnectResult.isRight()) {
            return httpGetConnectResult.get();
        }

        Map<String, String> err = new HashMap<>();
        err.put(ERROR, "トークン取得のHttp通信に失敗");
        err.put(ERROR_MESSAGE, httpGetConnectResult.getLeft());
        return Either.left(err);
    }

    Gson gson = new Gson();

    /**
     * 指定されたURLに対してメッセージをPOSTリクエストで送信し、レスポンスを処理します。
     * 成功時またはエラー時の情報を含むEitherオブジェクトを返します。
     *
     * @param url           メッセージを送信する先のURL
     * @param authorization Basic認証に使用する認証情報
     * @param scrfToken     CSRFトークンの値
     * @param message       POSTリクエストで送信するメッセージ
     * @return 成功時は空のマップ、エラー時はエラー情報を含むマップを収めたEitherオブジェクト
     */
    private Either<Map<String, String>, Map<String, String>> postMessage(String url, String authorization, TokenResponse tokenResponse, String message) {
        logger.info("postMessage url: {}, message: {}", url, message);

        Either<String, Either<Map<String, String>, Map<String, String>>> httpPostConnectResult = HttpUtil.connect(url, con -> {

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("authorization", "Basic " + authorization);
            con.setRequestProperty("cookie", tokenResponse.sessionID);
            con.setRequestProperty("x-csrf-token", tokenResponse.xCsrfToken);
            con.setRequestProperty("content-type", "application/JSON; charset=utf-8");
            con.setRequestProperty("accept", "application/json");

            Either<String, Map<String, String>> httpResult
                    = HttpUtil.write(con, message)
                    .flatMap(l -> HttpUtil.response(con))
                    .map(l -> gson.fromJson(l, Response.class))
                    .flatMap(response -> {
                        if (Objects.isNull(response)) {
                            return Either.left("レスポンスエラー");
                        }

                        Response.ResultItem outresult = response.d.resultItem;
                        if ("S".equals(outresult.resultStatus)) {
                            return Either.right(new HashMap<>());
                        }

                        Map<String, String> err = new HashMap<>();
                        err.put(ERROR, "RESULT_STATUS：" + outresult.resultStatus);
                        err.put(RESPONS_ORDER, "[" + ("[" + outresult.aufnr + ", " + outresult.vornr + ", " + outresult.lmnga + ", " + outresult.budat + ", " + outresult.messageClass + ", " + outresult.messageNo + "]") + "]");
                        err.put(ERROR_MESSAGE, outresult.errorMessage);
                        return Either.right(err);
                    });

            if (httpResult.isLeft()) {
                Map<String, String> err = new HashMap<>();
                err.put(ERROR, "データ送信のHttp通信に失敗");
                err.put(ERROR_MESSAGE, httpResult.getLeft());
                return Either.left(err);
            }
            return Either.right(httpResult.get());
        });

        if (httpPostConnectResult.isRight()) {
            return httpPostConnectResult.get();
        }

        logger.fatal("sendMessage : {}",message);
        Map<String, String> err = new HashMap<>();
        err.put(ERROR, "データ送信のHttp通信に失敗");
        err.put(ERROR_MESSAGE, httpPostConnectResult.getLeft());
        return Either.left(err);
    }

    /**
     * 指定されたURLに対してメッセージを送信する処理を行います。
     * 最大リトライ回数を超えるまで繰り返し通信を試み、送信の成功または最終的な失敗結果を返します。
     *
     * @param url           ベースとなるAPIのURL
     * @param authorization ベーシック認証用の認証情報
     * @param message       送信するメッセージ内容
     * @param maxRetry      最大リトライ回数
     * @param numInterval   通信リトライ間隔（ミリ秒）
     * @return 成功時は結果を収めた右のマップ、失敗時はエラー情報を収めた左のマップを持つEitherオブジェクト
     */
    Either<Map<String, String>, Map<String, String>> sendMessage(String url, String authorization, String message, int maxRetry, int numInterval) {
        final String tokenUrl = url + "/sap/opu/odata/SAP/ZPP004_SRV/$metadata";
        final String postUrl = url + "/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet";

        Either<Map<String, String>, Map<String, String>> result;
        for (int retry = 0; ; ++retry) {
            // トークンの取得
            result = getCSRFToken(tokenUrl, authorization)
                    .flatMap(tokenResponse -> postMessage(postUrl, authorization, tokenResponse, message));

            if (result.isRight()) {
                logger.info("送信が成功しました。");
                return result;
            }

            if (retry >= maxRetry - 1) {
                break;
            }

            logger.warn("送信に失敗しました。リトライします。");
            try {
                Thread.sleep(numInterval);
            } catch (InterruptedException e) {
                logger.error("スレッドの待機中に割り込みが発生しました。", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
        logger.fatal("SAPとの通信に失敗しました。");
        return result;
    }

    /**
     * SQLを使用して実績データを取得します。
     * SQLクエリの生成とデータベース接続、データの取得、エラー処理を行います。
     *
     * @param searchType データの取得方法を指定する検索タイプ（例: INTERVAL_TIME, FROM_TO_SEARCH, LAST_UPDATE）
     * @return データ取得が成功した場合は結果データのリストを含むEitherオブジェクト（右側）、
     * エラー発生時にはエラーメッセージを含むマップのリストを格納したEitherオブジェクト（左側）
     */
    Either<Map<String, List<Map<String, String>>>, List<Map<String, String>>> getActualResult(SEARCH_TYPE searchType) {
        String sql = createGetActualResultQuery(searchType);

        // SQL接続
        Either<String, Optional<DataTable>> optActualResult = SQLUtil.connect(String.format(SQL_URL, info.getAdFactoryAddress(), Port), SQL_USER, SQL_PASSWORD,
                conn -> {
                    Statement stmt = conn.createStatement();
                    // DBからデータ受信
                    return DataTable.create(stmt.executeQuery(sql));
                });

        if (optActualResult.isLeft()) {
            Map<String, String> err = new HashMap<>();
            err.put(ERROR, "データベース接続に失敗しました。");
            err.put(ERROR_MESSAGE, optActualResult.getLeft());
            Map<String, List<Map<String, String>>> errorMessages = new HashMap<>();
            errorMessages.put(USUALLY_ERROR, Collections.singletonList(err));
            logger.fatal(err);
            return Either.left(errorMessages);
        }

        if (optActualResult.get().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put(ERROR, "SQL構文の解析異常が発生しました。開発者に問い合わせてください。");
            logger.fatal(err);
            Map<String, List<Map<String, String>>> errorMessages = new HashMap<>();
            errorMessages.put(USUALLY_ERROR, Collections.singletonList(err));
            return Either.left(errorMessages);
        }

        List<Map<String, String>> result = optActualResult.get().get().toMapList();
        logger.info("SQL：{}", sql);
        logger.info("actual_size：{}", result.size());
        result.stream()
                .sorted(Comparator.comparing(item -> item.get(ACTUAL_ID)))
                .forEach(item -> logger.info(
                "DEB quantity:{} work_id:{} order:{} date:{} actual_id:{}",
                item.get(ORDER_NUMBER), // 指図番号
                item.get(WORK_ID), // 作業/活動番号
                item.get(QUANTITY), // 確認対象歩留
                item.get(COMP_DATETIME), // 完了日時
                item.get(ACTUAL_ID) // 実績ID
        ));

        // 取得結果をMapへ変換
        return Either.right(result);
    }

    public static Request createRequest(Map<String, String> data) {
        Request request = new Request();
        request.inData.aufnr = data.get(ORDER_NUMBER);
        request.inData.vornr = data.get(WORK_ID);
        request.inData.lmnga = data.get(QUANTITY);
        request.inData.budat = convertToDateFormat(data.get(COMP_DATETIME));
        return request;
    }

    /**
     * 工数連携システムへ実績報告を行う
     *
     * @param searchType 報告タイプ
     * @return 報告結果
     */
    private Either<Map<String, List<Map<String, String>>>, Integer> implOutput(SEARCH_TYPE searchType) {
        // データの取得
        Either<Map<String, List<Map<String, String>>>, List<Map<String, String>>> actualResult = getActualResult(searchType);
        if (actualResult.isLeft()) {
            logger.error("SQL実行失敗");
            return Either.left(actualResult.getLeft());
        }

        List<Map<String, String>> actualResultList = actualResult.get();

        List<Request> requestData = new ArrayList<>();
        List<Map<String, String>> exceptionRequestDataErrorMessages = new ArrayList<>();

        actualResultList
                .forEach(item -> {
                    try {
                        requestData.add(createRequest(item));
                    } catch (Exception ex) {
                        Map<String, String> err = new HashMap<>();
                        err.put(ERROR, "例外データ検知");
                        err.put(EXCEPTION_ORDER, "[" + ("[" + item.get(ORDER_NUMBER) + ", " + item.get(WORK_ID) + ", " + item.get(QUANTITY) + ", " + item.get(COMP_DATETIME) + ", " + item.get(ACTUAL_ID) + "]") + "]");
                        err.put(ERROR_MESSAGE, ex.getMessage());
                        exceptionRequestDataErrorMessages.add(err);
                    }
                });

        // 例外データをログ出力
        exceptionRequestDataErrorOutputLog(exceptionRequestDataErrorMessages);

        // 入力データの確認
        List<Either<Map<String, String>, Request>> dataCheckErrorMessages
                = requestData
                .stream()
                .map(OutputActualFacade::CheckRequestData)
                .toList();

        // 通常エラーメッセージ一覧
        List<Map<String, String>> usuallyErrorMessages
                = new ArrayList<>(dataCheckErrorMessages
                .stream()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .toList());

        // jsonに変換
        List<String> jsonList
                = dataCheckErrorMessages
                .stream()
                .filter(Either::isRight)
                .map(Either::get)
                .map(gson::toJson)
                .toList();

        final String userName = info.getAuthUser();
        final String passWord = info.getAuthPassword();
        final String auth = userName + ":" + passWord;
        final String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        int maxRetry = Integer.parseInt(info.getMaxRetry());
        int numInterval = Integer.parseInt(info.getNumInterval()) * 1000;

        List<Map<String, String>> httpErrorMessages = new ArrayList<>();
        List<Map<String, String>> statusErrorMessages = new ArrayList<>();

        for (int n = 0; n < jsonList.size(); ++n) {
            Either<Map<String, String>, Map<String, String>> result = sendMessage(info.getHttpAddress(), encodedAuth, jsonList.get(n), maxRetry, numInterval);
            if (result.isRight()) {
                if (!result.get().isEmpty()) {
                    statusErrorMessages.add(result.get());
                }
                continue;
            }

            Map<String, String> err = result.getLeft();
            String msg
                    = jsonList.subList(n, jsonList.size())
                    .stream()
                    .map(json -> gson.fromJson(json, Request.class).createMessage())
                    .collect(joining(LINE_SEPARATOR));
            err.put(ORDER, msg);
            logger.fatal(err);
            httpErrorMessages.add(err);
            break;
        }

        Map<String, List<Map<String, String>>> errorMessages = new HashMap<>();
        usuallyErrorMessages.addAll(httpErrorMessages);
        errorMessages.put(USUALLY_ERROR, usuallyErrorMessages);
        errorMessages.put(STATUS_DATA_ERROR, statusErrorMessages);
        errorMessages.put(EXCEPTION_DATA_ERROR, exceptionRequestDataErrorMessages);

        // ステータスエラーをログ出力
        outputStatusErrorLog(statusErrorMessages);

        if (!SEARCH_TYPE.FROM_TO_SEARCH.equals(searchType)) {
            // 現在時刻と取得時のactual_idを保存する
            info.setLastUpdateDateTime(new Date());
            OptionalInt optMaxActualId
                    = actualResultList
                    .stream()
                    .map(l -> l.getOrDefault("actual_id", "0"))
                    .mapToInt(Integer::parseInt)
                    .max();
            optMaxActualId.ifPresent(i -> info.setFromLastSearchActualId(String.valueOf(i)));
        }

        if (!info.save()) {
            Map<String, String> err = new HashMap<>();
            err.put(ERROR, "設定保存に失敗");
            usuallyErrorMessages.add(err);
            logger.fatal(err);
        }

        return errorMessages.values().stream().allMatch(List::isEmpty)
                ? Either.right(requestData.size())
                : Either.left(errorMessages);
    }

    /**
     * エラー発生通知メールを送信する。
     *
     * @param fromDate 日時指定の先頭
     * @param toDate   日時指定の末尾
     */
    private void sendErrorMail(Date fromDate, Date toDate, Map<String, List<Map<String, String>>> message) {
        logger.info("sendErrorMail start");
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            int timeout = info.getErrorMailTimeout() * 1000;

            MailProperty prop = new MailProperty();
            prop.setHost(info.getErrorMailServer());
            prop.setPort(info.getErrorMailPort());
            prop.setUser(info.getErrorMailUser());
            prop.setPassword(info.getErrorMailPassword());
            prop.setConnectionTimeout(timeout);
            prop.setTimeout(timeout);
            prop.setStarttlsEnable(info.getErrorMailStartTLSEnable());

            String from = info.getErrorMailFrom();
            String to = info.getErrorMailTo();
            String subject = "【adFactory 完了数連携】 実績情報出力エラー";

            StringBuilder content = new StringBuilder();
            content.append("adFactory 完了数連携の実績情報出力でエラーが発生しました。");
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
            // エラーメッセージの追加
            content.append(createMessage(message));

            MailUtils mail = new MailUtils(prop);
            mail.send(from, to, subject, content.toString());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("sendErrorMail end");
        }
    }

    /**
     * メッセージデータを基にメッセージ文字列を生成します。
     *
     * @param message メッセージデータのマップ。キーはメッセージのタイプ、値はそのタイプに対応するデータのリスト。
     *                各リスト要素はキーと値のマップで表されます。
     * @return メッセージ文字列。すべてのメッセージデータを処理し、結合した結果を返します。
     */
    public String createMessage(Map<String, List<Map<String, String>>> message) {
        return message.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> createMessage(mailMessage.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.joining(LINE_SEPARATOR));
    }

    /**
     * 指定されたヘッダーリストとメッセージデータリストを基に、メッセージ文字列を生成します。
     * ヘッダーはカンマ区切りで結合され、各メッセージデータが対応するヘッダー順に並べられて出力されます。
     * ヘッダーに対応するデータが存在しない場合は、"none" が使用されます。
     *
     * @param header メッセージ生成時に使用するヘッダー情報を格納したリスト
     * @param messageMaps ヘッダーごとのデータを格納したマップのリスト
     * @return 生成されたメッセージ文字列。ヘッダー部とメッセージデータ部が改行で区切られた形式で返されます。
     */
    private String createMessage(List<String> header, List<Map<String, String>> messageMaps) {
        return StringUtils.join(header, ", ") + LINE_SEPARATOR // ヘッダー部
                + messageMaps
                .stream() // 本体作成
                .map(messageMap ->
                        header.stream()
                                .map(l -> messageMap.getOrDefault(l, "none"))
                                .collect(joining(", ")))
                .collect(joining(LINE_SEPARATOR));
    }

    /**
     * 日付のフォーマットを変更
     *
     * @param dateTimeString yyyy-MM-dd HH:mm:ssフォーマットの日付文字列
     * @return yyyymmddフォーマットの日付文字列
     */
    public static String convertToDateFormat(String dateTimeString) {
        if (Objects.isNull(dateTimeString) || dateTimeString.isEmpty()) {
            return dateTimeString;
        }

        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, inputFormatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return dateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    /**
     * レスポンデータのステータスがEのデータをログ出力
     *
     * @param messages メッセージリスト
     */
    private void outputStatusErrorLog(List<Map<String, String>> messages) {

        if (Objects.isNull(messages) || messages.isEmpty()) {
            return;
        }

        logger.fatal("statusError_E_size：" + messages.size()
                + LINE_SEPARATOR
                + createMessage(statusErrorMailMessage, messages));
    }

    /**
     * 例外データをログ出力
     *
     * @param messages メッセージリスト
     */
    private void exceptionRequestDataErrorOutputLog(List<Map<String, String>> messages) {

        if (Objects.isNull(messages) || messages.isEmpty()) {
            return;
        }

        logger.fatal("exceptionRequestData_size：" + messages.size()
                + LINE_SEPARATOR
                + createMessage(exceptionDataErrorMailMessage, messages));
    }
}
