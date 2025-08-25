import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * MainAppのテストクラス
 */
public class MainAppTest {

    @TempDir
    Path tempDir;
    
    private Path outputPath;
    
    @BeforeEach
    void setUp() {
        outputPath = tempDir.resolve("output.txt");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(outputPath);
    }
    
    /**
     * native2ascii変換のテスト - 日本語テキストをASCIIエスケープシーケンスに変換
     */
    @Test
    void testNative2Ascii() throws IOException {
        // 入力ファイルのパス
        Path inputPath = Paths.get("src/test/resources/japanese.txt");
        
        // 変換を実行
        MainApp.convert(inputPath.toString(), outputPath.toString(), false, "UTF-8");
        
        // 期待される出力ファイルのパス
        Path expectedPath = Paths.get("src/test/resources/ascii.txt");
        
        // 出力ファイルと期待される出力ファイルの内容を比較
        List<String> actualLines = Files.readAllLines(outputPath, StandardCharsets.UTF_8);
        List<String> expectedLines = Files.readAllLines(expectedPath, StandardCharsets.UTF_8);
        
        assertEquals(expectedLines.size(), actualLines.size(), "行数が一致しません");
        for (int i = 0; i < expectedLines.size(); i++) {
            assertEquals(expectedLines.get(i), actualLines.get(i), "行 " + (i + 1) + " が一致しません");
        }
    }
    
    /**
     * ascii2native変換のテスト - ASCIIエスケープシーケンスを日本語テキストに変換
     */
    @Test
    void testAscii2Native() throws IOException {
        // 入力ファイルのパス
        Path inputPath = Paths.get("src/test/resources/ascii.txt");
        
        // 変換を実行
        MainApp.convert(inputPath.toString(), outputPath.toString(), true, "UTF-8");
        
        // 期待される出力ファイルのパス
        Path expectedPath = Paths.get("src/test/resources/japanese.txt");
        
        // 出力ファイルと期待される出力ファイルの内容を比較
        List<String> actualLines = Files.readAllLines(outputPath, StandardCharsets.UTF_8);
        List<String> expectedLines = Files.readAllLines(expectedPath, StandardCharsets.UTF_8);
        
        assertEquals(expectedLines.size(), actualLines.size(), "行数が一致しません");
        for (int i = 0; i < expectedLines.size(); i++) {
            assertEquals(expectedLines.get(i), actualLines.get(i), "行 " + (i + 1) + " が一致しません");
        }
    }
    
    /**
     * 空ファイルのテスト - native2ascii変換
     */
    @Test
    void testEmptyFileNative2Ascii() throws IOException {
        // 入力ファイルのパス
        Path inputPath = Paths.get("src/test/resources/empty.txt");
        
        // 変換を実行
        MainApp.convert(inputPath.toString(), outputPath.toString(), false, "UTF-8");
        
        // 出力ファイルが空であることを確認
        assertEquals(0, Files.size(outputPath), "出力ファイルが空ではありません");
    }
    
    /**
     * 空ファイルのテスト - ascii2native変換
     */
    @Test
    void testEmptyFileAscii2Native() throws IOException {
        // 入力ファイルのパス
        Path inputPath = Paths.get("src/test/resources/empty.txt");
        
        // 変換を実行
        MainApp.convert(inputPath.toString(), outputPath.toString(), true, "UTF-8");
        
        // 出力ファイルが空であることを確認
        assertEquals(0, Files.size(outputPath), "出力ファイルが空ではありません");
    }
    
    /**
     * 混合テキストのテスト - native2ascii変換
     */
    @Test
    void testMixedTextNative2Ascii() throws IOException {
        // 入力ファイルのパス
        Path inputPath = Paths.get("src/test/resources/mixed.txt");
        
        // 変換を実行
        MainApp.convert(inputPath.toString(), outputPath.toString(), false, "UTF-8");
        
        // 出力ファイルの内容を読み込み
        List<String> lines = Files.readAllLines(outputPath, StandardCharsets.UTF_8);
        
        // ASCII文字はそのまま、非ASCII文字は\\uXXXX形式に変換されていることを確認
        assertTrue(lines.get(0).contains("Hello, \\u"), "ASCII文字と\\uエスケープシーケンスの混合が正しくありません");
        assertTrue(lines.get(1).contains("This is a \\u"), "ASCII文字と\\uエスケープシーケンスの混合が正しくありません");
        assertTrue(lines.get(2).contains("It contains both ASCII and \\u"), "ASCII文字と\\uエスケープシーケンスの混合が正しくありません");
    }
    
    /**
     * 混合テキストのテスト - ascii2native変換（往復変換）
     */
    @Test
    void testMixedTextRoundTrip() throws IOException {
        // 入力ファイルのパス
        Path inputPath = Paths.get("src/test/resources/mixed.txt");
        
        // 一時ファイルのパス
        Path tempFilePath = tempDir.resolve("temp.txt");
        
        // native2ascii変換を実行
        MainApp.convert(inputPath.toString(), tempFilePath.toString(), false, "UTF-8");
        
        // ascii2native変換を実行
        MainApp.convert(tempFilePath.toString(), outputPath.toString(), true, "UTF-8");
        
        // 元のファイルと最終出力ファイルの内容を比較
        List<String> originalLines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        List<String> finalLines = Files.readAllLines(outputPath, StandardCharsets.UTF_8);
        
        assertEquals(originalLines.size(), finalLines.size(), "行数が一致しません");
        for (int i = 0; i < originalLines.size(); i++) {
            assertEquals(originalLines.get(i), finalLines.get(i), "行 " + (i + 1) + " が一致しません");
        }
        
        // 一時ファイルを削除
        Files.deleteIfExists(tempFilePath);
    }
}