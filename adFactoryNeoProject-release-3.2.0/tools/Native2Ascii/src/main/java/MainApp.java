import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ネイティブ文字エンコーディングとASCIIの間で変換するためのNative2Asciiツール。
 * 
 * パフォーマンス最適化:
 * 1. より高速なファイル読み書きのためのバッファードI/Oを使用
 * 2. パフォーマンス向上のための正規表現パターンの事前コンパイル
 * 3. ガベージコレクションを減らすためのStringBuilderオブジェクトの再利用
 * 4. 文字単位ではなくチャンク単位でのデータ処理
 * 5. より効率的な文字列処理技術の使用
 */
public class MainApp {
    // パフォーマンス向上のためのバッファサイズ
    private static final int BUFFER_SIZE = 8192;
    // パフォーマンス向上のための事前コンパイルされたパターン
    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");

    public static void convert(String inputFilePath, String outputFilePath, boolean reverse, String encoding)
            throws IOException {
        Charset charset = Charset.forName(encoding);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(inputFilePath)), charset), BUFFER_SIZE);
             BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(outputFilePath)), charset), BUFFER_SIZE)) {
            if (reverse) {
                // ASCII から Unicode 形式に復元
                ascii2native(reader, writer);
            } else {
                // Unicode から ASCII エスケープに変換
                native2ascii(reader, writer);
            }
        }
    }

    private static void native2ascii(BufferedReader reader, BufferedWriter writer) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        int charsRead;
        StringBuilder hexBuilder = new StringBuilder(4); // 16進数フォーマット用に再利用

        while ((charsRead = reader.read(buffer)) != -1) {
            for (int i = 0; i < charsRead; i++) {
                char c = buffer[i];
                if (c < 0x80) {
                    writer.write(c);
                } else {
                    writer.write("\\u");
                    // 新しいStringオブジェクトを作成せずに16進数をフォーマット
                    hexBuilder.setLength(0);
                    String hex = Integer.toHexString(c);
                    for (int j = hex.length(); j < 4; j++) {
                        hexBuilder.append('0');
                    }
                    hexBuilder.append(hex);
                    writer.write(hexBuilder.toString());
                }
            }
        }
    }

    private static void ascii2native(BufferedReader reader, BufferedWriter writer) throws IOException {
        String line;
        Matcher matcher;
        StringBuilder sb = new StringBuilder(BUFFER_SIZE); // StringBuilderを再利用

        while ((line = reader.readLine()) != null) {
            matcher = UNICODE_PATTERN.matcher(line);
            sb.setLength(0);
            int lastEnd = 0;

            while (matcher.find()) {
                // マッチする前のテキストを追加
                sb.append(line, lastEnd, matcher.start());
                // Unicodeキャラクターに変換して追加
                char unicodeChar = (char) Integer.parseInt(matcher.group(1), 16);
                sb.append(unicodeChar);
                lastEnd = matcher.end();
            }

            // 残りのテキストを追加
            if (lastEnd < line.length()) {
                sb.append(line, lastEnd, line.length());
            }

            writer.write(sb.toString());
            writer.newLine();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("使用法: java Native2AsciiTool <入力ファイル> <出力ファイル> <逆変換: true/false> <エンコーディング>");
            return;
        }
        String inputFilePath = args[0];
        String outputFilePath = args[1];
        boolean reverse = Boolean.parseBoolean(args[2]);
        String encoding = args[3];

        convert(inputFilePath, outputFilePath, reverse, encoding);
    }
}
