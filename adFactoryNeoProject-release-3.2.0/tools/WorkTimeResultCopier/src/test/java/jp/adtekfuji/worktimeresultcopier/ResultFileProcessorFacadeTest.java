/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.worktimeresultcopier;

import static java.util.stream.Collectors.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import adtekfuji.utility.Tuple;
import io.vavr.control.Either;
import org.junit.*;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * ResultFileProcessorFacadeのテストクラス
 * @author ke.yokoi
 */
public class ResultFileProcessorFacadeTest {

    // Common constants and fields
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static Class<?> workTimesResultClass;
    private static Field successField;
    private static String successValue;
    private static Method parseFileNameListLineMethod;
    private static Method parseWorkTimesResultFileLineMethod;

    /**
     * Helper method to access private methods via reflection
     */
    private static Method getPrivateMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    /**
     * Helper method to access WorkTimesResult inner class
     */
    private static Object createWorkTimesResult(String result, String data) throws Exception {
        Constructor<?> constructor = workTimesResultClass.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(result, data);
    }

    /**
     * Helper method to get string from WorkTimesResult
     */
    private static String getWorkTimesResultString(Object workTimesResult) throws Exception {
        Method getStringMethod = workTimesResultClass.getDeclaredMethod("getString");
        getStringMethod.setAccessible(true);
        return (String) getStringMethod.invoke(workTimesResult);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Initialize common reflection objects
        workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");
        successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);
        successValue = (String) successField.get(null);

        parseFileNameListLineMethod = getPrivateMethod(ResultFileProcessorFacade.class, "parseFileNameListLine", String.class);
        parseWorkTimesResultFileLineMethod = getPrivateMethod(ResultFileProcessorFacade.class, "parseWorkTimesResultFileLine", String.class);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * parseFileNameListLineメソッドのテスト
     * 正常なフォーマットの行を解析できることを確認
     */
    @Test
    public void testParseFileNameListLine_ValidFormat() throws Exception {
        System.out.println("testParseFileNameListLine_ValidFormat");

        // テストデータ
        String date = "20230101120000";
        String validLine = "testFile.csv, " + date;
        Date expectedDate = sdf.parse(date);

        // メソッド実行
        Either<String, Tuple<String, Date>> result =
                (Either<String, Tuple<String, Date>>) parseFileNameListLineMethod.invoke(null, validLine);

        // 検証
        assertTrue("Valid line should be parsed successfully", result.isRight());
        assertEquals("File name should match", "testFile.csv", result.get().getLeft());
        assertEquals("Date should match", expectedDate, result.get().getRight());
    }

    /**
     * parseFileNameListLineメソッドのテスト
     * 空の行を処理できることを確認
     */
    @Test
    public void testParseFileNameListLine_EmptyLine() throws Exception {
        System.out.println("testParseFileNameListLine_EmptyLine");

        // テストデータ
        String emptyLine = "";

        // メソッド実行
        Either<String, Tuple<String, Date>> result =
                (Either<String, Tuple<String, Date>>) parseFileNameListLineMethod.invoke(null, emptyLine);

        // 検証
        assertTrue("Empty line should be handled successfully", result.isRight());
        assertNull("File name should be null for empty line", result.get().getLeft());
        assertNull("Date should be null for empty line", result.get().getRight());
    }

    /**
     * parseFileNameListLineメソッドのテスト
     * 不正なフォーマットの行を処理できることを確認
     */
    @Test
    public void testParseFileNameListLine_InvalidFormat() throws Exception {
        System.out.println("testParseFileNameListLine_InvalidFormat");

        // テストデータ
        String invalidLine = "testFile.csv without date";

        // メソッド実行
        Either<String, Tuple<String, Date>> result =
                (Either<String, Tuple<String, Date>>) parseFileNameListLineMethod.invoke(null, invalidLine);

        // 検証
        assertTrue("Invalid line should return Left with error message", result.isLeft());
        assertEquals("Error message should match", "ファイルフォーマット異常です。", result.getLeft());
    }

    @Test
    public void testMath() {
        System.out.println("testMath");

        int time = 1;    //1msec
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
     * WorkTimesResultクラスのgetStringメソッドのテスト
     * 成功時の結果文字列が正しく生成されることを確認
     */
    @Test
    public void testWorkTimesResult_GetString_Success() throws Exception {
        System.out.println("testWorkTimesResult_GetString_Success");

        // インスタンス生成
        Object workTimesResult = createWorkTimesResult(successValue, "テスト成功データ");

        // getStringメソッドの実行
        String result = getWorkTimesResultString(workTimesResult);

        // 検証
        assertEquals("Success result should return only data", "テスト成功データ", result);
    }

    /**
     * WorkTimesResultクラスのgetStringメソッドのテスト
     * エラー時の結果文字列が正しく生成されることを確認
     */
    @Test
    public void testWorkTimesResult_GetString_Error() throws Exception {
        System.out.println("testWorkTimesResult_GetString_Error");

        // エラーインスタンス生成
        Object workTimesResult = createWorkTimesResult("エラー発生", "エラーデータ");

        // getStringメソッドの実行
        String result = getWorkTimesResultString(workTimesResult);

        // 検証
        assertEquals("Error result should format with error message and data", "エラー発生, \"[エラーデータ]\"", result);
    }

    /**
     * parseWorkTimesResultFileLineメソッドのテスト
     * 正常なフォーマットの行を解析できることを確認
     */
    @Test
    public void testParseWorkTimesResultFileLine_ValidFormat() throws Exception {
        System.out.println("testParseWorkTimesResultFileLine_ValidFormat");

        // テストデータ
        String[] validLine = {
                "a,b,c,d,e,f",
                "a,b,c,d,e,f,,g",
                "a,b,c,d,e,f,,g,h",
                "a,b,c,d,e,f,,g,h,i",
        };

        for (String s : validLine) {
            // メソッド実行
            ResultFileProcessorFacade.WorkTimesResult result = (ResultFileProcessorFacade.WorkTimesResult) parseWorkTimesResultFileLineMethod.invoke(null, s);

            // 検証
            assertTrue("Valid line should be parsed correctly", ResultFileProcessorFacade.WorkTimesResult.isSuccess(result));
        }
    }

    /**
     * parseWorkTimesResultFileLineメソッドのテスト
     * 不正なフォーマットの行を処理できることを確認
     */
    @Test
    public void testParseWorkTimesResultFileLine_InvalidFormat() throws Exception {
        System.out.println("testParseWorkTimesResultFileLine_InvalidFormat");

        // テストデータ
        String[] invalidLines = {
                "a",
                "a,b",
                "a,b,c",
                "a,b,c,d",
                "a,b,c,d,e",
                "a,b,c,d,e,f,error,",
        };

        for (String s : invalidLines) {
            // メソッド実行
            ResultFileProcessorFacade.WorkTimesResult result = (ResultFileProcessorFacade.WorkTimesResult) parseWorkTimesResultFileLineMethod.invoke(null, s);

            // 検証
            assertFalse("Invalid line should not be parsed correctly", ResultFileProcessorFacade.WorkTimesResult.isSuccess(result));
        }
    }

    /**
     * loadFileNameListメソッドのテスト
     * ファイルからファイル名リストを正しく読み込めることを確認
     */
    @Test
    public void testLoadFileNameList() throws Exception {
        System.out.println("testLoadFileNameList");

        // テスト用の一時ファイルを作成
        Path tempFile = Files.createTempFile("testFileNameList", ".txt");
        try {
            // テストデータをファイルに書き込む
            try (FileWriter writer = new FileWriter(tempFile.toFile())) {
                writer.write("file1.csv, 20230101120000\n");
                writer.write("file2.csv, 20230102120000\n");
                writer.write("file3.csv, 20230103120000\n");
            }

            // privateメソッドにアクセスするためのリフレクション設定
            Method loadFileNameListMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "loadFileNameList", File.class);
            loadFileNameListMethod.setAccessible(true);

            // メソッド実行
            Either<String, List<Tuple<String, Date>>> result =
                    (Either<String, List<Tuple<String, Date>>>) loadFileNameListMethod.invoke(null, tempFile.toFile());

            // 検証
            assertTrue("File should be loaded successfully", result.isRight());
            List<Tuple<String, Date>> fileNameList = result.get();
            assertEquals("Should have 3 file entries", 3, fileNameList.size());
            assertEquals("First file name should match", "file1.csv", fileNameList.get(0).getLeft());
            assertEquals("Second file name should match", "file2.csv", fileNameList.get(1).getLeft());
            assertEquals("Third file name should match", "file3.csv", fileNameList.get(2).getLeft());

            // 日付の検証
            Date expectedDate1 = sdf.parse("20230101120000");
            Date expectedDate2 = sdf.parse("20230102120000");
            Date expectedDate3 = sdf.parse("20230103120000");
            assertEquals("First date should match", expectedDate1, fileNameList.get(0).getRight());
            assertEquals("Second date should match", expectedDate2, fileNameList.get(1).getRight());
            assertEquals("Third date should match", expectedDate3, fileNameList.get(2).getRight());

        } finally {
            // テスト終了後に一時ファイルを削除
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * loadFileNameListメソッドのテスト
     * 不正なフォーマットのファイルを処理した場合のエラー処理を確認
     */
    @Test
    public void testLoadFileNameList_InvalidFormat() throws Exception {
        System.out.println("testLoadFileNameList_InvalidFormat");

        // テスト用の一時ファイルを作成
        Path tempFile = Files.createTempFile("testInvalidFileNameList", ".txt");
        try {
            // 不正なフォーマットのテストデータをファイルに書き込む
            try (FileWriter writer = new FileWriter(tempFile.toFile())) {
                writer.write("file1.csv, 20230101120000\n");
                writer.write("invalid line without date\n");  // 不正なフォーマット
                writer.write("file3.csv, 20230103120000\n");
            }

            // privateメソッドにアクセスするためのリフレクション設定
            Method loadFileNameListMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "loadFileNameList", File.class);
            loadFileNameListMethod.setAccessible(true);

            // メソッド実行
            Either<String, List<Tuple<String, Date>>> result =
                    (Either<String, List<Tuple<String, Date>>>) loadFileNameListMethod.invoke(null, tempFile.toFile());

            // 検証
            assertTrue("Should return Left with error message", result.isLeft());
            assertTrue("Error message should contain file name",
                    result.getLeft().contains(tempFile.getFileName().toString()));
            assertTrue("Error message should indicate format error",
                    result.getLeft().contains("ファイルフォーマット異常です"));

        } finally {
            // テスト終了後に一時ファイルを削除
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * loadFileNameListメソッドのテスト。
     * 存在しないファイルを指定した場合のエラーハンドリングを確認する。
     * テスト内容:
     * - 入力として存在しないファイルを指定した場合、結果がエラー（Left）であること。
     * - エラーメッセージが「ファイルの読み込みに失敗しました。」と含まれること。
     * 検証方法:
     * - リフレクションを使用してloadFileNameListメソッドを呼び出し、
     *   存在しないファイルを引数として渡す。
     * - メソッドの戻り値がLeftであることを確認し、さらにエラーメッセージの内容を検証する。
     *
     * @throws Exception テスト実行中に例外が発生した場合
     */
    @Test
    public void testLoadFileNameList_NoFile() throws Exception {
        System.out.println("testLoadFileNameList_NoFile");

        // privateメソッドにアクセスするためのリフレクション設定
        Method loadFileNameListMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                "loadFileNameList", File.class);
        loadFileNameListMethod.setAccessible(true);

        // メソッド実行
        Either<String, List<Tuple<String, Date>>> result =
                (Either<String, List<Tuple<String, Date>>>) loadFileNameListMethod.invoke(null, new File("no_file.txt"));

        // 検証
        assertTrue("Should return Left with error message", result.isLeft());
        assertTrue("Error message should indicate format error",
                result.getLeft().contains("ファイルの読み込みに失敗しました。"));
    }

    /**
     * parseWorkTimesResultFileメソッドのテスト
     * ファイルから作業時間結果を正しく読み込めることを確認
     */
    @Test
    public void testParseWorkTimesParseWorkTimesResultFile() throws Exception {
        System.out.println("testParseWorkTimesResultFile");

        // WorkTimesResultクラスへのアクセス
        Class<?> workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");

        // SUCCESS定数の取得
        java.lang.reflect.Field successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);

        // テスト用の一時ファイルを作成
        Path tempFile = Files.createTempFile("testWorkTimesResult", ".txt");
        try {
            // テストデータをファイルに書き込む
            try (FileWriter writer = new FileWriter(tempFile.toFile())) {
                writer.write("結果データ1,a,b,c,d,e\n");
                writer.write("結果データ2,a,b,c,d,e,,f,g\n");
                writer.write("エラーデータ1,a,b,c,d,e,エラー,g,h\n");
                writer.write("エラーデータ2,a\n");

            }

            // privateメソッドにアクセスするためのリフレクション設定
            Method parseWorkTimesResultFileMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "parseWorkTimesResultFile", File.class);
            parseWorkTimesResultFileMethod.setAccessible(true);

            // メソッド実行
            Either<String, List<?>> result =
                    (Either<String, List<?>>) parseWorkTimesResultFileMethod.invoke(null, tempFile.toFile());

            // 検証
            assertTrue("File should be parsed successfully", result.isRight());
            List<?> resultList = result.get();
            assertEquals("Should have 4 result entries", 4, resultList.size());

            // getStringメソッドの取得
            Method getStringMethod = workTimesResultClass.getDeclaredMethod("getString");
            getStringMethod.setAccessible(true);

            // 結果の検証
            assertEquals("First result should match", "結果データ1,a,b,c,d", getStringMethod.invoke(resultList.get(0)));
            assertEquals("Second result should match", "結果データ2,a,b,c,d", getStringMethod.invoke(resultList.get(1)));
            assertEquals("Error result should be formatted correctly", "h, \"[エラーデータ1,a,b,c,d,e,エラー,g,h]\"", getStringMethod.invoke(resultList.get(2)));
            assertEquals("Error result should be formatted correctly", "フォーマット異常, \"[エラーデータ2,a]\"", getStringMethod.invoke(resultList.get(3)));

        } finally {
            // テスト終了後に一時ファイルを削除
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * writeWorkTimesResultメソッドのテスト
     * 作業時間結果をファイルに正しく書き込めることを確認
     */
    @Test
    public void testWriteWorkTimesResult() throws Exception {
        System.out.println("testWriteWorkTimesResult");

        // WorkTimesResultクラスへのアクセス
        Class<?> workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");

        // コンストラクタの取得
        java.lang.reflect.Constructor<?> constructor = workTimesResultClass.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);

        // SUCCESS定数の取得
        java.lang.reflect.Field successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);

        // テスト用のWorkTimesResultオブジェクトを作成
        List<Object> results = new ArrayList<>();
        results.add(new ResultFileProcessorFacade.WorkTimesResult(ResultFileProcessorFacade.WorkTimesResult.SUCCESS, "テスト結果1"));
        results.add(new ResultFileProcessorFacade.WorkTimesResult(ResultFileProcessorFacade.WorkTimesResult.SUCCESS, "テスト結果2"));
        results.add(new ResultFileProcessorFacade.WorkTimesResult("エラー", "エラーデータ"));

        // テスト用の一時ファイルを作成
        Path tempFile = Files.createTempFile("testWriteWorkTimesResult", ".txt");
        try {
            // privateメソッドにアクセスするためのリフレクション設定
            Method writeWorkTimesResultMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "writeWorkTimesResult", List.class, File.class, String.class, String.class);
            writeWorkTimesResultMethod.setAccessible(true);

            // メソッド実行
            boolean result = (boolean) writeWorkTimesResultMethod.invoke(null, results, tempFile.toFile(), null, "Shift_JIS");

            // 検証
            assertTrue("File should be written successfully", result);

            // ファイルの内容を読み込んで検証
            List<String> lines = Files.readAllLines(tempFile, Charset.forName("Shift_JIS"));
            assertEquals("Should have 4 lines", 3, lines.size());
            assertEquals("First result should match", "テスト結果1", lines.get(0));
            assertEquals("Second result should match", "テスト結果2", lines.get(1));
            assertEquals("Error result should match", "エラー, \"[エラーデータ]\"", lines.get(2));

        } finally {
            // テスト終了後に一時ファイルを削除
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * generateFileNameメソッドのテスト
     * 存在しないファイル名を生成できることを確認
     */
    @Test
    public void testGenerateFileName() throws Exception {
        System.out.println("testGenerateFileName");

        // privateメソッドにアクセスするためのリフレクション設定
        Method generateFileNameMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                "generateFileName", String.class);
        generateFileNameMethod.setAccessible(true);

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testGenerateFileName");
        try {
            // テスト用のベースファイル名
            String baseFileName = tempDir.toString() + File.separator + "testFile";

            // メソッド実行
            Either<String, File> result =
                    (Either<String, File>) generateFileNameMethod.invoke(null, baseFileName);

            // 検証
            assertTrue("Should generate a valid file name", result.isRight());
            File generatedFile = result.get();
            assertTrue("Generated file name should contain the base name",
                    generatedFile.getName().startsWith("testFile_"));
            assertFalse("Generated file should not exist yet", generatedFile.exists());

            // ファイルを作成して再度テスト
            generatedFile.createNewFile();
            Either<String, File> result2 =
                    (Either<String, File>) generateFileNameMethod.invoke(null, baseFileName);

            assertTrue("Should generate a different file name", result2.isRight());
            File generatedFile2 = result2.get();
            assertNotEquals("Second generated file should be different from first",
                    generatedFile.getAbsolutePath(), generatedFile2.getAbsolutePath());
            assertFalse("Second generated file should not exist yet", generatedFile2.exists());

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    /**
     * createWorkTimesResultFileメソッドのテスト
     * 作業時間結果ファイルを正しく作成できることを確認
     */
    @Test
    public void testCreateWorkTimesResultFile() throws Exception {
        System.out.println("testCreateWorkTimesResultFile");

        // WorkTimesResultクラスへのアクセス
        Class<?> workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");

        // コンストラクタの取得
        java.lang.reflect.Constructor<?> constructor = workTimesResultClass.getDeclaredConstructor(String.class, String.class);
        constructor.setAccessible(true);

        // SUCCESS定数の取得
        java.lang.reflect.Field successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);
        String successValue = (String) successField.get(null);

        // テスト用のWorkTimesResultオブジェクトを作成
        List<Object> results = new ArrayList<>();
        results.add(constructor.newInstance(successValue, "テスト結果1"));
        results.add(constructor.newInstance(successValue, "テスト結果2"));
        results.add(constructor.newInstance("エラー", "エラーデータ"));

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testCreateWorkTimesResultFile");
        try {
            // テスト用のターゲットファイル
            File targetFile = new File(tempDir.toString() + File.separator + "resultFile.txt");

            // メソッドにアクセスするためのリフレクション設定
            Method createWorkTimesResultFileMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "createWorkTimesResultFile", List.class, File.class, String.class, String.class);
            createWorkTimesResultFileMethod.setAccessible(true);

            // メソッド実行
            Either<String, String> result =
                    (Either<String, String>) createWorkTimesResultFileMethod.invoke(null, results, targetFile, "abc", "UTF-8");

            // 検証
            assertTrue("File creation should succeed", result.isRight());
            assertTrue("Target file should exist", targetFile.exists());

            // ファイルの内容を読み込んで検証
            List<String> lines = Files.readAllLines(targetFile.toPath());
            assertEquals("Should have 4 lines (3 results + header)", 4, lines.size());
            assertTrue("Header line should contain abc", lines.get(0).contains("abc"));
            assertTrue("First line should contain test result 1", lines.get(1).contains("テスト結果1"));
            assertTrue("Second line should contain test result 2", lines.get(2).contains("テスト結果2"));
            assertTrue("Third line should contain error data", lines.get(3).contains("エラーデータ"));

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    /**
     * getLoadTargetListFilesメソッドのテスト
     * 読み込み対象のファイルリストを正しく取得できることを確認
     */
    @Test
    public void testGetLoadTargetListFiles() throws Exception {
        System.out.println("testGetLoadTargetListFiles");

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testGetLoadTargetListFiles");
        try {
            // テスト用のファイルを作成
            File file1 = new File(tempDir + File.separator + ResultFileProcessorFacade.READ_FILE_NAME_LIST_FILE_PREFIX + "1234567.txt");
            file1.createNewFile();
            File file2 = new File(tempDir + File.separator + ResultFileProcessorFacade.READ_FILE_NAME_LIST_WORK_TIME_RESULT_FILE_PREFIX + "123456.txt");
            file2.createNewFile();
            File file3 = new File(tempDir + File.separator + "abcde_123456.txt");
            file3.createNewFile();

            // メソッドにアクセスするためのリフレクション設定
            Method getLoadTargetListFilesMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "getLoadTargetListFiles", String.class);
            getLoadTargetListFilesMethod.setAccessible(true);

            // メソッド実行
            Either<String, List<File>> result =
                    (Either<String, List<File>>) getLoadTargetListFilesMethod.invoke(null, tempDir.toString());

            // 検証
            assertTrue("Should successfully get file list", result.isRight());
            List<File> files = result.get();
            assertEquals("Should find 2 files", 2, files.size());
            assertTrue("Should contain file1",
                    files.stream().anyMatch(f -> f.getName().equals(file1.getName())));
            assertTrue("Should contain file2",
                    files.stream().anyMatch(f -> f.getName().equals(file2.getName())));

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    /**
     * getLoadTargetListFilesメソッドのテスト。
     * 存在しないフォルダを指定した場合、エラーとなることを確認。
     * このテストでは、リフレクションを使用してgetLoadTargetListFilesメソッドを呼び出し、
     * 不正なパラメータを渡した場合に、戻り値のEitherがLeftとしてエラーメッセージを含むことを検証する。
     * 検証内容:
     * - 入力として指定されたフォルダが存在しない場合、結果がエラー（Left）であること。
     * - 期待されるエラーメッセージが「出力ファイル一覧の取得に失敗しました。」であること。
     *
     * @throws Exception メソッド実行中に例外が発生した場合
     */
    @Test
    public void testGetLoadTargetListFiles_FALSE() throws Exception {
        System.out.println("testGetLoadTargetListFiles_FALSE");

        // メソッドにアクセスするためのリフレクション設定
        Method getLoadTargetListFilesMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                "getLoadTargetListFiles", String.class);
        getLoadTargetListFilesMethod.setAccessible(true);

        // メソッド実行
        Either<String, List<File>> result =
                (Either<String, List<File>>) getLoadTargetListFilesMethod.invoke(null, "hogehogehoge");

        Assert.assertTrue("適当なフォルダを指定している為、失敗すること", result.isLeft());
        Assert.assertEquals("出力ファイルの取得に失敗すること", "出力ファイル一覧の取得に失敗しました。", result.getLeft());
    }

    /**
     * createFileMapメソッドのテスト
     * ファイルマップを正しく作成できることを確認
     */
    @Test
    public void testCreateFileMap() throws Exception {
        System.out.println("testCreateFileMap");

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testCreateFileMap");
        try {
            // テスト用のファイルを作成
            File file1 = new File(tempDir + File.separator + "file1.csv");
            file1.createNewFile();
            File file2 = new File(tempDir + File.separator + "file2.csv");
            file2.createNewFile();
            File file3 = new File(tempDir + File.separator + "other.txt");
            file3.createNewFile();

            // メソッドにアクセスするためのリフレクション設定
            Method createFileMapMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "createFileMap", File.class, Pattern.class);
            createFileMapMethod.setAccessible(true);

            // テスト用のパターン
            Pattern pattern = Pattern.compile(".*\\.csv");

            // メソッド実行
            Either<String, Map<String, File>> result =
                    (Either<String, Map<String, File>>) createFileMapMethod.invoke(null, tempDir.toFile(), pattern);

            // 検証
            assertTrue("Should successfully create file map", result.isRight());
            Map<String, File> fileMap = result.get();
            assertEquals("Should find 2 CSV files", 2, fileMap.size());
            assertTrue("Should contain file1.csv", fileMap.containsKey("file1"));
            assertEquals("Should contain file1.csv", file1.getName(), fileMap.get("file1").getName());

            assertTrue("Should contain file2.csv", fileMap.containsKey("file2"));
            assertEquals("Should contain file2.csv", file2.getName(), fileMap.get("file2").getName());

            assertFalse("Should not contain other.txt", fileMap.containsKey("other.txt"));

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    /**
     * createResultFileメソッドのテスト
     * 結果ファイルを正しく処理し、成功と失敗のフォルダに分けることを確認
     */
    @Test
    public void testCreateResultFile() throws Exception {
        System.out.println("testCreateResultFile");

        // WorkTimesResultクラスへのアクセス
        Class<?> workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");

        // SUCCESS定数の取得
        java.lang.reflect.Field successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);
        String successValue = (String) successField.get(null);

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testCreateResultFile");
        try {
            // 成功データフォルダと失敗データフォルダを作成
            File successFolder = new File(tempDir + File.separator + "success");
            successFolder.mkdir();
            File errorFolder = new File(tempDir + File.separator + "error");
            errorFolder.mkdir();

            // テスト用の入力ファイルを作成
            File inputFile = new File(tempDir + File.separator + "input.txt");
            try (FileWriter writer = new FileWriter(inputFile)) {
                writer.write("成功データ1,b,c,d,e,f\n");
                writer.write("成功データ2,b,c,d,e,f\n");
                writer.write("成功データ3,b,c,d,e,f,,h,i\n");
                writer.write("失敗データ1,b,c,d,e,f,失敗,h,i\n");
            }

            // メソッドにアクセスするためのリフレクション設定
            Method createResultFileMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "createResultFile", File.class, File.class, File.class, Consumer.class);
            createResultFileMethod.setAccessible(true);

            // メソッド実行
            Either<String, String> result =
                    (Either<String, String>) createResultFileMethod.invoke(null, inputFile, successFolder, errorFolder, null);

            // 検証
            assertTrue("File processing should succeed", result.isRight());

            // 成功フォルダ内のファイルを確認
            File[] successFiles = successFolder.listFiles();
            assertNotNull("Success folder should contain files", successFiles);
            assertEquals("Success folder should contain 1 file", 1, successFiles.length);

            // 失敗フォルダ内のファイルを確認
            File[] errorFiles = errorFolder.listFiles();
            assertNotNull("Error folder should contain files", errorFiles);
            assertEquals("Error folder should contain 1 file", 1, errorFiles.length);

            // 成功ファイルの内容を確認
            List<String> successLines = Files.readAllLines(successFiles[0].toPath(), StandardCharsets.UTF_8);
            assertEquals("Success file should contain 2 lines", 3, successLines.size());
            assertTrue("Success file should contain success data 1",
                    successLines.stream().anyMatch(line -> line.contains("成功データ1")));
            assertTrue("Success file should contain success data 2",
                    successLines.stream().anyMatch(line -> line.contains("成功データ2")));
            assertTrue("Success file should contain success data 3",
                    successLines.stream().anyMatch(line -> line.contains("成功データ3")));

            // 失敗ファイルの内容を確認
            List<String> errorLines = Files.readAllLines(errorFiles[0].toPath(), Charset.forName("Shift_JIS"));
            assertEquals("Error file should contain 1 line (line1 + Header)", 2, errorLines.size());
            assertTrue("Error file should contain Header", errorLines.get(0).contains("エラー内容, [出力内容]"));
            assertTrue("Error file should contain error data",
                    errorLines.get(1).contains("失敗データ1"));

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    /**
     * メソッド {@code testCreateResultFile_AllSuccess} は、テストケースを実行して
     * 指定の入力ファイルから全て成功されていた場合はファイル出力されないことを検証します。
     * このテストケースでは、次の内容を検証します:
     * 1. テスト用の入力ファイルを作成し、入力ファイルの内容が正しく処理されることを確認。
     * 2. リフレクションを使用して {@code createResultFile} メソッドにアクセスし、実行。
     * 3. 成功データが正しいディレクトリに格納されることを確認。
     * 4. 成功フォルダと失敗フォルダの内容が正しく分類され、適切に処理されていることを確認。
     * 5. 全ての処理が成功し、例外が発生しないことを保証。
     * 6. テスト終了後には一時ディレクトリおよび関連するファイルが確実に削除されること。
     * 検証プロセスでは、作成されたフォルダやファイルの内容を確認することで、
     * 処理が正確かつ期待通りの動作を行っているか検証します。
     *
     * @throws Exception テスト中に例外が発生する場合
     */
    @Test
    public void testCreateResultFile_AllSuccess() throws Exception {
        System.out.println("testCreateResultFile");

        // WorkTimesResultクラスへのアクセス
        Class<?> workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");

        // SUCCESS定数の取得
        java.lang.reflect.Field successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testCreateResultFile");
        try {
            // 成功データフォルダと失敗データフォルダを作成
            File successFolder = new File(tempDir + File.separator + "success");
            successFolder.mkdir();
            File errorFolder = new File(tempDir + File.separator + "error");
            errorFolder.mkdir();

            // テスト用の入力ファイルを作成(全て成功)
            File inputFile = new File(tempDir + File.separator + "input.txt");
            try (FileWriter writer = new FileWriter(inputFile)) {
                writer.write("成功データ1,b,c,d,e,f\n");
                writer.write("成功データ2,b,c,d,e,f\n");
                writer.write("成功データ3,b,c,d,e,f,,h,i\n");
            }

            // メソッドにアクセスするためのリフレクション設定
            Method createResultFileMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "createResultFile", File.class, File.class, File.class, Consumer.class);
            createResultFileMethod.setAccessible(true);

            // メソッド実行
            Either<String, String> result =
                    (Either<String, String>) createResultFileMethod.invoke(null, inputFile, successFolder, errorFolder, null);

            // 検証
            assertTrue("File processing should succeed", result.isRight());

            // 成功フォルダ内のファイルを確認
            File[] successFiles = successFolder.listFiles();
            assertNotNull("Success folder should contain files", successFiles);
            assertEquals("Success folder should contain 0 file", 0, successFiles.length);

            // 失敗フォルダ内のファイルを確認
            File[] errorFiles = errorFolder.listFiles();
            assertNotNull("Error folder should contain files", errorFiles);
            assertEquals("Error folder should contain 1 file", 0, errorFiles.length);

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }


    /**
     * testCreateResultFile_ALLFalseメソッドは、入力ファイルに含まれる全てのデータが失敗データの場合に、
     * 入力データを基に成功結果ファイルと失敗結果ファイルを正しく生成できるかを検証する単体テストです。
     *
     * 機能の概要:
     * - テストの実行中にリフレクションを使用して対象クラスや定数、メソッドにアクセスします。
     * - 一時ディレクトリを作成し、フォルダ構造やテスト対象の入力ファイルを準備します。
     * - 指定された成功データフォルダおよび失敗データフォルダに、期待される結果ファイルが正しく分離・生成されるかを検証します。
     * - 処理が例外を伴わず正常に完了することをテストします。
     *
     * テストが検証する項目:
     * 1. メソッドの実行結果が成功（Right側）として返ってくること。
     * 2. 成功データフォルダに生成されるファイル数が1であること。
     * 3. 失敗データフォルダに生成されるファイル数が1であること。
     * 4. 成功結果ファイルにはデータが含まれていない（失敗データのみが存在するため）こと。
     * 5. 失敗結果ファイルには、適切なヘッダーおよび失敗データが含まれていること。
     *
     * 特記事項:
     * - テスト終了後には作成された一時ディレクトリおよび、その内部のファイルやフォルダを全て削除します。
     * - このテストでは、SUCCESS定数および対象メソッドへのアクセスにリフレクションを使用します。
     *
     * 使用ライブラリ:
     * - JUnit5のアノテーションとアサーション機能
     * - java.nio.fileパッケージを用いたファイル操作
     * - Either型を扱うためのライブラリ（Functionalコンセプト）
     *
     * @throws Exception テスト実行中に例外が発生した場合
     */
    @Test
    public void testCreateResultFile_ALLFalse() throws Exception {
        System.out.println("testCreateResultFile");

        // WorkTimesResultクラスへのアクセス
        Class<?> workTimesResultClass = Class.forName("jp.adtekfuji.worktimeresultcopier.ResultFileProcessorFacade$WorkTimesResult");

        // SUCCESS定数の取得
        java.lang.reflect.Field successField = workTimesResultClass.getDeclaredField("SUCCESS");
        successField.setAccessible(true);

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testCreateResultFile");
        try {
            // 成功データフォルダと失敗データフォルダを作成
            File successFolder = new File(tempDir + File.separator + "success");
            successFolder.mkdir();
            File errorFolder = new File(tempDir + File.separator + "error");
            errorFolder.mkdir();

            // テスト用の入力ファイルを作成
            File inputFile = new File(tempDir + File.separator + "input.txt");
            try (FileWriter writer = new FileWriter(inputFile)) {
                writer.write("失敗データ1,b,c,d,e,f,失敗,h,i\n");
                writer.write("失敗データ2,b,c,d,e,f,失敗,h,i\n");
                writer.write("失敗データ3,b,c,d,e,f,失敗,h,i\n");
            }

            // メソッドにアクセスするためのリフレクション設定
            Method createResultFileMethod = ResultFileProcessorFacade.class.getDeclaredMethod(
                    "createResultFile", File.class, File.class, File.class, Consumer.class);
            createResultFileMethod.setAccessible(true);

            // メソッド実行
            Either<String, String> result =
                    (Either<String, String>) createResultFileMethod.invoke(null, inputFile, successFolder, errorFolder, null);

            // 検証
            assertTrue("File processing should succeed", result.isRight());

            // 成功フォルダ内のファイルを確認
            File[] successFiles = successFolder.listFiles();
            assertNotNull("Success folder should contain files", successFiles);
            assertEquals("Success folder should contain 1 file", 0, successFiles.length);

            // 失敗フォルダ内のファイルを確認
            File[] errorFiles = errorFolder.listFiles();
            assertNotNull("Error folder should contain files", errorFiles);
            assertEquals("Error folder should contain 1 file", 1, errorFiles.length);

            // 失敗ファイルの内容を確認
            List<String> errorLines = Files.readAllLines(errorFiles[0].toPath(), Charset.forName("Shift_JIS"));
            assertEquals("Error file should contain 4 line (line3 + Header)", 4, errorLines.size());
            assertTrue("Error file should contain Header", errorLines.get(0).contains("エラー内容, [出力内容]"));
            assertTrue("Error file should contain error data", errorLines.get(1).contains("失敗データ1"));
            assertTrue("Error file should contain error data", errorLines.get(2).contains("失敗データ2"));
            assertTrue("Error file should contain error data", errorLines.get(3).contains("失敗データ3"));
        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }


    /**
     * separateFilesメソッドのテスト
     * ファイルを正しく分離処理できることを確認
     */
    @Test
    public void testSeparateFiles1() throws Exception {
        System.out.println("testSeparateFiles");

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testSeparateFiles");
        try {
            // テスト用のディレクトリ構造を作成
            File inputDir = new File(tempDir + File.separator + "input");
            inputDir.mkdir();
            File outputDir = new File(tempDir + File.separator + "output");
            outputDir.mkdir();
            File shareDir = new File(tempDir + File.separator + "share");
            shareDir.mkdir();
            File readFileDir = new File(tempDir + File.separator + "readfile");
            readFileDir.mkdir();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now = new Date();
            Date beforeOneDate = new Date(now.getTime() - (24 * 60 * 60 * 1000 + 1));

            // テスト用のファイル名リストファイルを作成
            File fileNameListFile = new File(readFileDir, ResultFileProcessorFacade.READ_FILE_NAME_LIST_FILE_PREFIX + "aaaa.csv");
            try (FileWriter writer = new FileWriter(fileNameListFile)) {
                writer.write("QtestFile, 20230101120000\n");  // outにあり(成功のみ)
                writer.write("QtestFile1, 20230101120000\n");  // outにあり(失敗のみ)
                writer.write("QtestFile2, 20230101120000\n");  // outにあり(失敗を含む)
                writer.write("QtestFile3, 20230101120001\n"); // inにのみあり
                writer.write("QtestFile4, " + sdf.format(new Date()) +"\n"); // inにもoutにもない
                writer.write("QtestFile5, " + sdf.format(beforeOneDate) + "\n"); // inにもoutにもなく 1日以上たったもの
            }

            // テスト用の入力ファイルを作成
            // outあり(成功のみ)
            File outFile = new File(outputDir, "QtestFile.log");
            try (FileWriter writer = new FileWriter(outFile)) {
                writer.write("a,b,c,d,e,f\n");
            }

            // outあり(失敗のみ)
            File outFile1 = new File(outputDir, "QtestFile1.log");
            try (FileWriter writer = new FileWriter(outFile1)) {
                writer.write("a,b,c,d,e,f,g,h,i\n");
            }

            // outあり(失敗を含む)
            File outFile2 = new File(outputDir, "QtestFile2.log");
            try (FileWriter writer = new FileWriter(outFile2)) {
                writer.write("1,b,c,d,e,f\n");
                writer.write("2,b,c,d,e,f,g,h,i\n");
                writer.write("3,b,c,d,e,f\n");
                writer.write("4,b,c,d,e,f,g,h,i\n");
            }

            // inにのみあり
            File inFile = new File(inputDir, "QtestFile3.csv");
            try (FileWriter writer = new FileWriter(inFile)) {
                writer.write("a,b,c,d,e,f\n");
            }

            // OutputActualInfoクラスのモックを作成
            OutputActualInfo outputInfo = new OutputActualInfo();
            outputInfo.setReadFileNameAddress(readFileDir.getAbsolutePath());
            outputInfo.setShareFolder(shareDir.getAbsolutePath());
            outputInfo.setInFolder(inputDir.getAbsolutePath());
            outputInfo.setOutFolder(outputDir.getAbsolutePath());
            outputInfo.setUser("a");  // ローカルテストなのでユーザーは空文字
            outputInfo.setPassword("a");  // ローカルテストなのでパスワードは空文字

            // ResultFileProcessorFacadeのインスタンスを作成
            ResultFileProcessorFacade facade = new ResultFileProcessorFacade(outputInfo);

            // privateメソッドにアクセスするためのリフレクション設定
            Method separateFilesMethod = ResultFileProcessorFacade.class.getDeclaredMethod("separateFiles");
            separateFilesMethod.setAccessible(true);

            // メソッド実行
            Either<String, String> result = (Either<String, String>) separateFilesMethod.invoke(facade);

            // 検証
            assertTrue("File separation should false", result.isLeft());

            // inフォルダ内のファイルを確認
            Map<String,File> inFileMap = Stream.of(inputDir.listFiles()).collect(toMap(File::getName, Function.identity()));
            assertEquals("input folder should contain at least one file", 2, inFileMap.size());
            {
                assertTrue("input folder should contain file named QtestFile2#1.csv", inFileMap.containsKey("QtestFile2#1.csv"));
                List<String> qtestFileLines = Files.readAllLines(inFileMap.get("QtestFile2#1.csv").toPath());
                assertEquals("QtestFile2.log should contain 2 lines", 2, qtestFileLines.size());
                assertEquals("QtestFile2.log should contain success data 1", "1,b,c,d,e", qtestFileLines.get(0));
                assertEquals("QtestFile2.log should contain success data 2", "3,b,c,d,e", qtestFileLines.get(1));
            }

            {
                assertTrue("input folder should contain file named QtestFile3.csv", inFileMap.containsKey("QtestFile3.csv"));
            }

            // 共有フォルダ内のファイルを確認
            Map<String,File> shareFileMap = Stream.of(shareDir.listFiles()).collect(toMap(File::getName, Function.identity()));
            assertEquals("share folder should contain at least one file", 2, shareFileMap.size());
            {
                assertTrue("share folder should contain file named QtestFile1#1.log", shareFileMap.containsKey("QtestFile1#1.csv"));
                List<String> qtestFileLines = Files.readAllLines(shareFileMap.get("QtestFile1#1.csv").toPath(), Charset.forName("Shift_JIS"));
                assertEquals("QtestFile1.log should contain 2 lines", 2, qtestFileLines.size());
                assertEquals("QtestFile1.log should contain Header", ResultFileProcessorFacade.WorkTimesResult.getErrorHeader(), qtestFileLines.get(0));
                assertEquals("QtestFile1.log should contain success data 1", "i, \"[a,b,c,d,e,f,g,h,i]\"", qtestFileLines.get(1));
            }

            {
                assertTrue("share folder should contain file named QtestFile2#1.log", shareFileMap.containsKey("QtestFile2#1.csv"));
                List<String> qtestFileLines = Files.readAllLines(shareFileMap.get("QtestFile2#1.csv").toPath(), Charset.forName("Shift_JIS"));
                assertEquals("QtestFile2.log should contain 3 lines", 3, qtestFileLines.size());
                assertEquals("QtestFile2.log should contain Header", ResultFileProcessorFacade.WorkTimesResult.getErrorHeader(), qtestFileLines.get(0));
                assertEquals("QtestFile2.log should contain success data 1", "i, \"[2,b,c,d,e,f,g,h,i]\"", qtestFileLines.get(1));
                assertEquals("QtestFile2.log should contain success data 2", "i, \"[4,b,c,d,e,f,g,h,i]\"", qtestFileLines.get(2));
            }

            // 管理ファイルを確認
            File[] readFile = readFileDir.listFiles();
            assertEquals("read file folder should contain 1 file", 1, readFile.length);
            assertTrue("read file folder should contain file named read file", readFile[0].getName().startsWith(ResultFileProcessorFacade.READ_FILE_NAME_LIST_WORK_TIME_RESULT_FILE_PREFIX));
            {
                List<String> qtestFileLines = Files.readAllLines(readFile[0].toPath(), Charset.forName("Shift_JIS"));
                assertEquals("file should contain 3 lines", 3, qtestFileLines.size());
                assertTrue("file should contain QtestFile3", qtestFileLines.get(0).contains("QtestFile3"));
                assertTrue("file should contain QtestFile2", qtestFileLines.get(1).contains("QtestFile2"));
                assertTrue("file should contain QtestFile4", qtestFileLines.get(2).contains("QtestFile4"));
            }
        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    /**
     * separateFilesメソッドのテスト
     * ファイルを正しく分離処理できることを確認
     */
    @Test
    public void testSeparateFiles() throws Exception {
        System.out.println("testSeparateFiles");

        // テスト用の一時ディレクトリを作成
        Path tempDir = Files.createTempDirectory("testSeparateFiles");
        try {
            // テスト用のディレクトリ構造を作成
            File inputDir = new File(tempDir + File.separator + "input");
            inputDir.mkdir();
            File outputDir = new File(tempDir + File.separator + "output");
            outputDir.mkdir();
            File shareDir = new File(tempDir + File.separator + "share");
            shareDir.mkdir();
            File readFileDir = new File(tempDir + File.separator + "readfile");
            readFileDir.mkdir();


            // テスト用のファイル名リストファイルを作成
            File fileNameListFile = new File(readFileDir, ResultFileProcessorFacade.READ_FILE_NAME_LIST_FILE_PREFIX + "Qaaa.csv");
            try (FileWriter writer = new FileWriter(fileNameListFile)) {
                writer.write("QtestFile, 20230101120000\n");
            }

            // テスト用の入力ファイルを作成
            File inputFile = new File(outputDir, "QtestFile.log");
            try (FileWriter writer = new FileWriter(inputFile)) {
                writer.write("a,b,c,d,e,f\n");
            }

            // OutputActualInfoクラスのモックを作成
            OutputActualInfo outputInfo = new OutputActualInfo();
            outputInfo.setReadFileNameAddress(readFileDir.getAbsolutePath());
            outputInfo.setShareFolder(shareDir.getAbsolutePath());
            outputInfo.setInFolder(inputDir.getAbsolutePath());
            outputInfo.setOutFolder(outputDir.getAbsolutePath());
            outputInfo.setUser("a");  // ローカルテストなのでユーザーは空文字
            outputInfo.setPassword("a");  // ローカルテストなのでパスワードは空文字

            // ResultFileProcessorFacadeのインスタンスを作成
            ResultFileProcessorFacade facade = new ResultFileProcessorFacade(outputInfo);

            // privateメソッドにアクセスするためのリフレクション設定
            Method separateFilesMethod = ResultFileProcessorFacade.class.getDeclaredMethod("separateFiles");
            separateFilesMethod.setAccessible(true);

            // メソッド実行
            Either<String, String> result = (Either<String, String>) separateFilesMethod.invoke(facade);

            // 検証
            assertTrue("File separation should succeed", result.isRight());

            // 成功フォルダ内のファイルを確認
            File[] successFiles = shareDir.listFiles();
            assertNotNull("Success folder should contain files", successFiles);
            assertTrue("Success folder should contain at least one file", successFiles.length == 0);

        } finally {
            // テスト終了後に一時ディレクトリとファイルを削除
            Files.walk(tempDir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

}
