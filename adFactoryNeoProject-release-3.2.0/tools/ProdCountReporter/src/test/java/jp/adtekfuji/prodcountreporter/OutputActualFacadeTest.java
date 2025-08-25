/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.prodcountreporter;

import static org.hamcrest.CoreMatchers.is;

import adtekfuji.utility.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.vavr.control.Either;
import jp.adtekfuji.prodcountreporter.json.Request;
import jp.adtekfuji.prodcountreporter.json.Response;
import jp.adtekfuji.prodcountreporter.json.TokenResponse;
import org.junit.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * @author ke.yokoi
 */
public class OutputActualFacadeTest {

    private HttpServer server;
    private static final int PORT = 44300;
    private static final String HOST = "localhost";
    private static final String CSRF_TOKEN = "test-csrf-token-123";
    private static final String SAP_SESSION_ID = "SAP_SESSIONID=test-session-id-123";
    private static final String TEST_RESPONSE = "{\"d\":{\"OUT_RESULT\":{\"ID\":\"\",\"RESULT_STATUS\":\"S\"}}}";
    private static final String TEST_ERROR_RESPONSE = "{\"d\":{\"OUT_RESULT\":{\"ID\":\"\",\"RESULT_STATUS\":\"E\",\"AUFNR\":\"aaaaaaaaaaaa\",\"VORNR\":\"bbbb\",\"LMNGA\":\"1234567890\",\"BUDAT\":\"20250409\",\"AUERU\":\"A\",\"CANCEL_KBN\":\"B\",\"MESSAGE_CLASS\":\"abcdefghijklmnopqrst\",\"MESSAGE_NO\":\"1234\",\"ErrorMessage\":\"エラーが発生しました\"}}}";

    public static final List<Request> requests = new ArrayList<>();

    private Map<String, String> requestHeaders = new HashMap<>();
    private String lastRequestBody = "";
    private OutputActualInfo mockInfo;
    private OutputActualFacade facade;
    Gson gson = new Gson();


    public OutputActualFacadeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        // HTTPサーバーの作成と開始
        server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);

        // コンテキストハンドラの設定
        server.createContext("/sap/opu/odata/SAP/ZPP004_SRV/$metadata", new CsrfTokenHandler());
        server.createContext("/sap/opu/odata/SAP/ZPP004_SRV_not_token/$metadata", new CsrfNoTokenHandler(10));

        // 送信
        server.createContext("/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet", new DataHandler());
        server.createContext("/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet_not_register", new DataNotRegisterHandler(0));

        // テスト
        server.createContext("/CsrTokenRetry2DataRegisterRetry3/sap/opu/odata/SAP/ZPP004_SRV/$metadata", new CsrfNoTokenHandler(2));
        server.createContext("/CsrTokenRetry2DataRegisterRetry3/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAM", new DataNotRegisterHandler(3));

        // エグゼキュータの設定
        server.setExecutor(Executors.newFixedThreadPool(10));

        // サーバー開始
        server.start();

        System.out.println("テストHTTPサーバーがポート " + PORT + " で開始されました");

        // モックOutputActualInfoの作成
        mockInfo = new OutputActualInfo();
        mockInfo.httpAddressProperty().set("http://" + HOST + ":" + PORT);
        mockInfo.authUserProperty().set("testuser");
        mockInfo.authPasswordProperty().set("testpass");
        mockInfo.maxRetryProperty().set("3");
        mockInfo.numIntervalProperty().set("1");

        // OutputActualFacadeの作成
        facade = new OutputActualFacade(mockInfo);

        {
            Request request = new Request();
            request.inData = new Request.InData();
            request.inData.aufnr = "aaaaaaaaaaaa"; // 指図番号
            request.inData.vornr = "bbbb"; // 作業/活動番号
            request.inData.lmnga = "12345"; // 数値
            requests.add(request);
        }

        {
            Request request = new Request();
            request.inData = new Request.InData();
            request.inData.aufnr = "bbbbbbbbbbb"; // 指図番号
            request.inData.vornr = "cccc"; // 作業/活動番号
            request.inData.lmnga = "67890"; // 数値
            requests.add(request);
        }



    }

    @After
    public void tearDown() {
        if (server != null) {
            server.stop(0);
            System.out.println("テストHTTPサーバーが停止しました");
        }
    }


    private boolean isValidJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(json); // JSONが解析可能かをチェック
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * CSVファイルからデータを読み込み、INSERT文を生成する
     * @param csvFilePath CSVファイルのパス
     * @param tableName テーブル名
     * @return 生成されたINSERT文
     * @throws IOException ファイル読み込みエラー
     */
    private String generateSqlFromCsv(String csvFilePath, String tableName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(csvFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            // ヘッダー行を読み込む
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSVファイルが空です: " + csvFilePath);
            }

            String[] headers = headerLine.split(",");

            // カラム名部分の構築
            StringBuilder columnNames = new StringBuilder();
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) {
                    columnNames.append(", ");
                }
                columnNames.append(headers[i]);
            }

            // INSERT文の開始部分
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(tableName).append(" (")
               .append(columnNames).append(") VALUES ");

            // データ行を読み込む
            String line;
            boolean firstRow = true;
            while ((line = reader.readLine()) != null) {
                if (!firstRow) {
                    sql.append(", ");
                }

                // 値の部分を構築
                sql.append("(");

                // CSVの行を解析（簡易的な実装）
                List<String> values = parseCSVLine(line);

                for (int i = 0; i < values.size(); i++) {
                    if (i > 0) {
                        sql.append(", ");
                    }

                    String value = values.get(i);

                    // 数値はそのまま
                    if (value.matches("^\\d+$") || value.equals("0")) {
                        sql.append(value);
                    } else {
                        // JSONの検証とエスケープ
                        if (isValidJson(value)) {
                            value = value.replace("'", "''");
                        }
                        sql.append("'").append(value).append("'");
                    }
                }


                sql.append(")");
                firstRow = false;
            }

            sql.append(";");
            return sql.toString();
        }
    }

    /**
     * CSV行を解析する（簡易的な実装）
     * @param line CSV行
     * @return 解析された値のリスト
     */
    private List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // 次の文字がダブルクォーテーションの場合（エスケープ対応）
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentValue.append('"');
                    i++; // 次のダブルクォーテーションをスキップ
                } else {
                    // クォートの開始・終了を切り替え
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // カンマがクォート外にある場合、値をリストに追加
                result.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                // その他の文字を追加
                currentValue.append(c);
            }
        }

        // 最後の値を追加
        result.add(currentValue.toString());
        return result;
    }

    @Ignore
    @Test
    public void createGetActualResultQueryTest() throws SQLException, IOException {
        PostgreSQLContainer<?> postgresContainer
                = new PostgreSQLContainer<>(DockerImageName.parse("postgres:11.4"))
                .withDatabaseName("adFactoryDB2")
                .withUsername("postgres")
                .withPassword("@dtek1977")
                .withInitScript("postgresql/create_adfactorydb.sql")
                .withInitScript("postgresql/create_adfactorydb_tables.sql");

        postgresContainer.start();

        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:" + postgresContainer.getFirstMappedPort() + "/adFactoryDB2", "postgres", "@dtek1977");
        Statement statement = connection.createStatement();

        // CSVファイルからSQLを生成して実行
        String actualResultSql = generateSqlFromCsv("/postgresql/insert_trn_actual_result.csv", "trn_actual_result");
        statement.execute(actualResultSql);

        String kanbanSql = generateSqlFromCsv("/postgresql/insert_trn_kanban.csv", "trn_kanban");
        statement.execute(kanbanSql);

        statement.execute("SELECT setval('trn_actual_result_actual_id_seq', 500)");

        OutputActualInfo info = new OutputActualInfo();
        info.setFromLastSearchActualId("1");
        info.isNotChangedLastUpdateTime();
        info.setAdFactoryServerAddress("localhost");

        OutputActualFacade outputActualFacade = new OutputActualFacade(info);
        outputActualFacade.Port = postgresContainer.getFirstMappedPort();

        var result = outputActualFacade.getActualResult(OutputActualFacade.SEARCH_TYPE.LAST_UPDATE);
        Assert.assertTrue("取得成功", result.isRight());
        assertEquals("1件のみ取得", 1, result.get().size());
        assertEquals("id3が取得", "3", result.get().getFirst().get("actual_id"));

    }


    @Test
    public void ResponseConvertTest()
    {
        {
            Response actual = gson.fromJson(TEST_RESPONSE, Response.class);

            Response expected = new Response();
            expected.d = new Response.DataWrapper();
            expected.d.resultItem = new Response.ResultItem();
            expected.d.resultItem.resultStatus = "S";

            Assert.assertEquals("JSONの解析結果が同じ", actual, expected);
        }

        {
            Response actual = gson.fromJson(TEST_ERROR_RESPONSE, Response.class);

            Response expected = new Response();
            expected.d = new Response.DataWrapper();
            expected.d.resultItem = new Response.ResultItem();
            expected.d.resultItem.resultStatus = "E";
            expected.d.resultItem.aufnr = "aaaaaaaaaaaa";
            expected.d.resultItem.vornr = "bbbb";
            expected.d.resultItem.lmnga = 1234567890L;
            expected.d.resultItem.budat = "20250409";
            expected.d.resultItem.aueru = "A";
            expected.d.resultItem.cancelKbn = "B";
            expected.d.resultItem.messageClass = "abcdefghijklmnopqrst";
            expected.d.resultItem.messageNo = "1234";
            expected.d.resultItem.errorMessage = "エラーが発生しました";

            Assert.assertEquals("JSONの解析結果が同じ", actual, expected);
        }

    }

    @Test
    public void RequestConvertTest()
    {
        Request request = new Request();
        request.inData = new Request.InData();
        request.inData.aufnr = "aaaaaaaaaaaa"; // 指図番号
        request.inData.vornr = "bbbb"; // 作業/活動番号
        request.inData.lmnga = "12345"; // 数値

        Gson gson = new Gson();
        String actual = gson.toJson(request);
        Assert.assertEquals("JSONの変換が正しい", actual, "{\"IN_DATA\":{\"AUFNR\":\"aaaaaaaaaaaa\",\"VORNR\":\"bbbb\",\"LMNGA\":\"12345\"},\"OUT_RESULT\":{}}");
    }


    /**
     * getCSRFTokenメソッドのテスト
     * プライベートメソッドをリフレクションを使用してテスト
     */
    @Test
    public void testGetCSRFToken() throws Exception {
        System.out.println("testGetCSRFToken");
        // プライベートメソッドへのアクセス
        Method getCSRFTokenMethod = OutputActualFacade.class.getDeclaredMethod("getCSRFToken", String.class, String.class);
        getCSRFTokenMethod.setAccessible(true);

        // 認証情報の作成
        String auth = "testuser:testpass";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        {
            // 通信成功
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV/$metadata";
            Either<Map<String, String>, TokenResponse> result = (Either<Map<String, String>, TokenResponse>) getCSRFTokenMethod.invoke(facade, url, encodedAuth);

            // 結果の検証
            assertTrue("CSRFトークン取得は成功すべきです", result.isRight());
            assertEquals("取得したCSRFトークンが正しいこと", CSRF_TOKEN, result.get().xCsrfToken);
        }

        {
            // 通信に失敗
            String url = "http://hogehogehogehoge";
            Either<Map<String, String>, TokenResponse> result = (Either<Map<String, String>, TokenResponse>) getCSRFTokenMethod.invoke(facade, url, encodedAuth);

            // 結果の検証
            assertTrue("通信は失敗すべきです", result.isLeft());
            assertEquals("トークン取得のHttp通信に失敗", "トークン取得のHttp通信に失敗", result.getLeft().get(OutputActualFacade.ERROR));
        }


        {
            // 通信に失敗
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP001_SRV/hogehoge";
            Either<Map<String, String>, TokenResponse> result = (Either<Map<String, String>, TokenResponse>) getCSRFTokenMethod.invoke(facade, url, encodedAuth);

            // 結果の検証
            assertTrue("通信は失敗すべきです", result.isLeft());
            assertEquals("トークン取得のHttp通信に失敗", "トークン取得のHttp通信に失敗", result.getLeft().get(OutputActualFacade.ERROR));
        }

        {
            // トークンの取得に失敗
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV_not_token/$metadata";
            Either<Map<String, String>, TokenResponse> result = (Either<Map<String, String>, TokenResponse>) getCSRFTokenMethod.invoke(facade, url, encodedAuth);

            // 結果の検証
            assertTrue("CSRFトークン取得は失敗すべきです", result.isLeft());
            assertEquals("CSRFトークン取得失敗", "トークンの取得に失敗", result.getLeft().get(OutputActualFacade.ERROR));
        }

    }

    /**
     * postMessageメソッドのテスト
     * プライベートメソッドをリフレクションを使用してテスト
     */
    @Test
    public void testPostMessage() throws Exception {
        System.out.println("testPostMessage");

        // プライベートメソッドへのアクセス
        Method postMessageMethod = OutputActualFacade.class.getDeclaredMethod(
                "postMessage", String.class, String.class, TokenResponse.class, String.class);
        postMessageMethod.setAccessible(true);

        // 認証情報の作成
        String auth = "testuser:testpass";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        // テストデータ
        String message = gson.toJson(requests.getFirst());

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.xCsrfToken = CSRF_TOKEN;
        tokenResponse.sessionID = SAP_SESSION_ID;

        {
            // 登録成功
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet";
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) postMessageMethod.invoke(facade, url, encodedAuth, tokenResponse, message);

            // 結果の検証
            assertTrue("POSTリクエストは成功すべきです", result.isRight());
            assertTrue("レスポンスは空のマップであるべきです", result.get().isEmpty());
        }

        {
            // 登録失敗(ステータス異常)
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet_not_register";
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) postMessageMethod.invoke(facade, url, encodedAuth, tokenResponse, message);

            Response.ResultItem outresult = gson.fromJson(TEST_ERROR_RESPONSE, Response.class).d.resultItem;
            String expected = "[" + ("[" + outresult.aufnr + ", " + outresult.vornr + ", " + outresult.lmnga + ", " + outresult.budat + ", " + outresult.messageClass + ", " + outresult.messageNo + "]") + "]";

            // 結果の検証
            assertTrue("POSTリクエストは成功すべきです", result.isRight());
            assertEquals("状態エラーが設定されていること", expected, result.get().get(OutputActualFacade.RESPONS_ORDER));
            assertEquals("メッセージも正しく取得している事", outresult.errorMessage, result.get().get(OutputActualFacade.ERROR_MESSAGE));
        }

        {
            // 通信に失敗
            String url = "http://hogehogehogehoge";
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) postMessageMethod.invoke(facade, url, encodedAuth, tokenResponse, message);

            assertTrue("POSTリクエストは失敗すべきです", result.isLeft());
            assertEquals("データ送信のHttp通信に失敗すべき", "データ送信のHttp通信に失敗", result.getLeft().get(OutputActualFacade.ERROR));
        }

        {
            // 登録失敗 (CSRF_TOKENを適当なものにすると登録失敗返るように実装)
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV/hogehoge";
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) postMessageMethod.invoke(facade, url, encodedAuth, tokenResponse, message);

            Response.ResultItem outresult = gson.fromJson(TEST_ERROR_RESPONSE, Response.class).d.resultItem;
            String expected = "[" + ("[" + outresult.aufnr + ", " + outresult.vornr + ", " + outresult.lmnga + ", " + outresult.budat + ", " + outresult.messageClass + ", " + outresult.messageNo + "]") + "]";

            // 結果の検証
            assertTrue("POSTリクエストは失敗すべきです", result.isLeft());
            assertEquals("データ送信のHttp通信に失敗すべき", "データ送信のHttp通信に失敗", result.getLeft().get(OutputActualFacade.ERROR));
        }

        {
            TokenResponse tokenResponseNoToken = new TokenResponse();
            tokenResponse.xCsrfToken = "hogehoge";
            tokenResponse.sessionID = SAP_SESSION_ID;

            // 通信に失敗
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet";
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) postMessageMethod.invoke(facade, url, encodedAuth, tokenResponseNoToken, message);

            assertTrue("POSTリクエストは失敗すべきです", result.isLeft());
            assertEquals("データ送信のHttp通信に失敗すべき", "データ送信のHttp通信に失敗", result.getLeft().get(OutputActualFacade.ERROR));
            assertEquals("データ送信のHttp通信に失敗すべき", "レスポンス異常 : 403", result.getLeft().get(OutputActualFacade.ERROR_MESSAGE));
        }

        {
            TokenResponse tokenResponseNoToken = new TokenResponse();
            tokenResponse.xCsrfToken = CSRF_TOKEN;
            tokenResponse.sessionID = "hogehoge";

            // 通信に失敗
            String url = "http://" + HOST + ":" + PORT + "/sap/opu/odata/SAP/ZPP004_SRV/IN_PARAMSet";
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) postMessageMethod.invoke(facade, url, encodedAuth, tokenResponseNoToken, message);

            assertTrue("POSTリクエストは失敗すべきです", result.isLeft());
            assertEquals("データ送信のHttp通信に失敗すべき", "データ送信のHttp通信に失敗", result.getLeft().get(OutputActualFacade.ERROR));
            assertEquals("データ送信のHttp通信に失敗すべき", "レスポンス異常 : 403", result.getLeft().get(OutputActualFacade.ERROR_MESSAGE));
        }

    }

    /**
     * sendMessageメソッドのテスト
     * プライベートメソッドをリフレクションを使用してテスト
     */
    @Test
    public void testSendMessage() throws Exception {
        System.out.println("testSendMessage");

        // プライベートメソッドへのアクセス
        Method sendMessageMethod = OutputActualFacade.class.getDeclaredMethod(
                "sendMessage", String.class, String.class, String.class, int.class, int.class);
        sendMessageMethod.setAccessible(true);

        // 認証情報の作成
        String auth = "testuser:testpass";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        // テストデータ
        String baseUrl = "http://" + HOST + ":" + PORT;
        String message = gson.toJson(requests.getFirst());
        int numInterval = 1000;



        {
            int maxRetry = 1;
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) sendMessageMethod.invoke(facade, baseUrl + "/CsrTokenRetry2DataRegisterRetry3", encodedAuth, message, maxRetry, numInterval);

            // 結果の検証
            assertTrue("トークン取得エラー", result.isLeft());
            assertEquals("レスポンスは空のマップであるべきです", result.getLeft().get(OutputActualFacade.ERROR), "トークンの取得に失敗");
        }

        {
            int maxRetry = 2;
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) sendMessageMethod.invoke(facade, baseUrl + "/CsrTokenRetry2DataRegisterRetry3", encodedAuth, message, maxRetry, numInterval);

            // 結果の検証
            assertTrue("トークン取得エラー", result.isLeft());
            assertEquals("レスポンスは空のマップであるべきです", result.getLeft().get(OutputActualFacade.ERROR), "データ送信のHttp通信に失敗");
        }

        {
            int maxRetry = 3;
            // メソッド呼び出し
            Either<Map<String, String>, Map<String, String>> result = (Either<Map<String, String>, Map<String, String>>) sendMessageMethod.invoke(facade, baseUrl + "/CsrTokenRetry2DataRegisterRetry3", encodedAuth, message, maxRetry, numInterval);

            Response.ResultItem outresult = gson.fromJson(TEST_ERROR_RESPONSE, Response.class).d.resultItem;
            String expected = "[" + ("[" + outresult.aufnr + ", " + outresult.vornr + ", " + outresult.lmnga + ", " + outresult.budat + ", " + outresult.messageClass + ", " + outresult.messageNo + "]") + "]";

            // 結果の検証
            assertTrue("送信は成功すべきです", result.isRight());
            assertEquals("状態エラーが設定されていること", expected, result.get().get(OutputActualFacade.RESPONS_ORDER));
            assertEquals("メッセージも正しく取得している事", outresult.errorMessage, result.get().get(OutputActualFacade.ERROR_MESSAGE));
        }

    }


    @Test
    public void testMath() {
        System.out.println("testMath");

        int time = 1;    //1 msec
        int out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 1000;    //1sec
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 10000;    //10sec
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 59999;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 60000;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 60001;
        out = (int) Math.ceil((double) time / 60.0 / 1000.0);
        assertThat(out, is(2));
    }

    @Test
    public void testRound() {
        System.out.println("testRound");

        int time = 29999;    //29.999sec
        int out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(0));

        time = 30000;    //30.000sec
        out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));

        time = 30001;    //30.001sec
        out = (int) Math.round((double) time / 60.0 / 1000.0);
        assertThat(out, is(1));
    }

    /**
     * createMessage(Map<String, List<Map<String, String>>> message)メソッドのテスト
     */
    @Test
    public void testCreateMessage() {
        System.out.println("testCreateMessage");

        // テスト用のデータを作成
        Map<String, List<Map<String, String>>> testMessage = new HashMap<>();

        // USUALLY_ERRORのテストデータ
        List<Map<String, String>> usuallyErrorList = new ArrayList<>();
        Map<String, String> usuallyError1 = new HashMap<>();
        usuallyError1.put(OutputActualFacade.ERROR, "エラー1");
        usuallyError1.put(OutputActualFacade.ERROR_MESSAGE, "エラーメッセージ1");
        usuallyError1.put(OutputActualFacade.ORDER, "[[指図番号1, 作業番号1, 歩留1, 日付1]]");
        usuallyErrorList.add(usuallyError1);

        Map<String, String> usuallyError2 = new HashMap<>();
        usuallyError2.put(OutputActualFacade.ERROR, "エラー2");
        usuallyError2.put(OutputActualFacade.ERROR_MESSAGE, "エラーメッセージ2");
        usuallyError2.put(OutputActualFacade.ORDER, "[[指図番号2, 作業番号2, 歩留2, 日付2]]");
        usuallyErrorList.add(usuallyError2);

        testMessage.put(OutputActualFacade.USUALLY_ERROR, usuallyErrorList);

        // STATUS_DATA_ERRORのテストデータ
        List<Map<String, String>> statusErrorList = new ArrayList<>();
        Map<String, String> statusError = new HashMap<>();
        statusError.put(OutputActualFacade.ERROR, "ステータスエラー");
        statusError.put(OutputActualFacade.RESPONS_ORDER, "[[指図番号3, 作業番号3, 歩留3, 日付3, メッセージクラス3, メッセージ番号3]]");
        statusError.put(OutputActualFacade.ERROR_MESSAGE, "ステータスエラーメッセージ");
        statusErrorList.add(statusError);

        testMessage.put(OutputActualFacade.STATUS_DATA_ERROR, statusErrorList);

        // 空のリストを持つエントリ（フィルタリングされるはず）
        testMessage.put(OutputActualFacade.EXCEPTION_DATA_ERROR, new ArrayList<>());

        // OutputActualFacadeのインスタンスを作成
        OutputActualFacade testFacade = new OutputActualFacade(mockInfo);

        // createMessageメソッドを呼び出し
        String result = testFacade.createMessage(testMessage);

        // 期待される結果
        String expectedUsuallyError = "Error, [[指図番号, 作業/活動番号, 確認対象歩留, 転記日付]], ErrorMessage" + System.lineSeparator() +
                "エラー1, [[指図番号1, 作業番号1, 歩留1, 日付1]], エラーメッセージ1" + System.lineSeparator() +
                "エラー2, [[指図番号2, 作業番号2, 歩留2, 日付2]], エラーメッセージ2";

        String expectedStatusError = "Error, [[指図番号, 作業/活動番号, 確認対象歩留, 転記日付, メッセージクラス, メッセージ番号]], ErrorMessage" + System.lineSeparator() +
                "ステータスエラー, [[指図番号3, 作業番号3, 歩留3, 日付3, メッセージクラス3, メッセージ番号3]], ステータスエラーメッセージ";

        String expected = expectedUsuallyError + System.lineSeparator() + expectedStatusError;

        // 結果の検証
        assertEquals("createMessageの結果が期待通りであること", expected, result);

        // 空のマップの場合のテスト
        Map<String, List<Map<String, String>>> emptyMessage = new HashMap<>();
        String emptyResult = testFacade.createMessage(emptyMessage);
        assertEquals("空のマップの場合は空文字列が返されること", "", emptyResult);
    }

    /**
     * CSRFトークンエンドポイント用ハンドラ
     */
    class CsrfTokenHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // リクエストヘッダーを検証用に保存
            exchange.getRequestHeaders().forEach((key, values) ->
                    requestHeaders.put(key, String.join(", ", values)));

            // CSRFトークンをヘッダーに設定
            exchange.getResponseHeaders().add("x-csrf-token", CSRF_TOKEN);
            exchange.getResponseHeaders().add("Content-Type", "application/xml");
            exchange.getResponseHeaders().add("set-cookie", "test");
            exchange.getResponseHeaders().add("set-cookie", SAP_SESSION_ID);

            // 空のレスポンスボディ
            String response = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\"></edmx:Edmx>";
            exchange.sendResponseHeaders(200, response.length());

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    /**
     * CSRFトークンエンドポイント用ハンドラ
     */
    class CsrfNoTokenHandler implements HttpHandler {

        int retry;
        int count = 0;

        public CsrfNoTokenHandler(int retry)
        {
            this.retry = retry;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // リクエストヘッダーを検証用に保存
            exchange.getRequestHeaders().forEach((key, values) ->
                    requestHeaders.put(key, String.join(", ", values)));



            if (count >= retry) {
                exchange.getResponseHeaders().add("x-csrf-token", CSRF_TOKEN);
            }
            ++count;

            exchange.getResponseHeaders().add("set-cookie", "test");
            exchange.getResponseHeaders().add("set-cookie", SAP_SESSION_ID);

            // CSRFトークンをヘッダーに設定
            exchange.getResponseHeaders().add("Content-Type", "application/xml");

            // 空のレスポンスボディ
            String response = "<?xml version=\"1.0\" encoding=\"utf-8\"?><edmx:Edmx Version=\"1.0\" xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\"></edmx:Edmx>";
            exchange.sendResponseHeaders(200, response.length());

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }


    /**
     * データエンドポイント用ハンドラ
     */
    class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // リクエストヘッダーを検証用に保存
            exchange.getRequestHeaders().forEach((key, values) ->
                    requestHeaders.put(key, String.join(", ", values)));

            // リクエストボディ読み取り
            try (InputStream is = exchange.getRequestBody();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    requestBody.append(line);
                }
                lastRequestBody = requestBody.toString();
                Request request = gson.fromJson(lastRequestBody, Request.class);
                Assert.assertEquals("同じデータ", requests.getFirst(), request);
            }

            // CSRFトークン確認
            String csrfToken = exchange.getRequestHeaders().getFirst("x-csrf-token");
            String sessionID = exchange.getRequestHeaders().getFirst("cookie");

            if (CSRF_TOKEN.equals(csrfToken) && SAP_SESSION_ID.equals(sessionID)) {
                // 有効なトークン、成功を返す
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, TEST_RESPONSE.length());

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(TEST_RESPONSE.getBytes());
                }
            } else {
                // 無効なトークン、エラーを返す
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(403, TEST_ERROR_RESPONSE.length());

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(TEST_ERROR_RESPONSE.getBytes());
                }
            }
        }
    }

    /**
     * データエンドポイント用ハンドラ
     */
    class DataNotRegisterHandler implements HttpHandler {

        int retry;
        public DataNotRegisterHandler(int retry) {
            this.retry = retry;
        }

        int count;
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // リクエストヘッダーを検証用に保存
            exchange.getRequestHeaders().forEach((key, values) ->
                    requestHeaders.put(key, String.join(", ", values)));

            // リクエストボディ読み取り
            try (InputStream is = exchange.getRequestBody();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    requestBody.append(line);
                }
                lastRequestBody = requestBody.toString();
            }

            // CSRFトークン確認
            String csrfToken = exchange.getRequestHeaders().getFirst("x-csrf-token");

            if (CSRF_TOKEN.equals(csrfToken) && count >= retry) {
                // 有効なトークン、成功を返す
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseHeaders().add("Transfer-Encoding", "chunked");
                exchange.sendResponseHeaders(200, 0);

                try (OutputStream os = exchange.getResponseBody()) {
                    byte[] responseBytes = TEST_ERROR_RESPONSE.getBytes(StandardCharsets.UTF_8);
                    // 適切なサイズのチャンクで送信
                    int chunkSize = 50;
                    for (int i = 0; i < responseBytes.length; i += chunkSize) {
                        int length = Math.min(chunkSize, responseBytes.length - i);
                        os.write(responseBytes, i, length);
                        os.flush();
                    }
                }

            } else {
                ++count;
                // 無効なトークン、エラーを返す
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(403, TEST_ERROR_RESPONSE.length());

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(TEST_ERROR_RESPONSE.getBytes());
                }
            }
        }
    }
}
