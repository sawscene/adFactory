package adtekfuji.utility;

import io.vavr.control.Either;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import adtekfuji.locale.LocaleUtils;
import java.util.ResourceBundle;

public  class NetworkFileUtil {

    public final static String SUCCESS = "SUCCESS";
    public final static String FAIL = "key.NetworkFolderError"; //ネットワークフォルダ接続時に不明なエラーが発生しました。
    public final static String NOT_CONNECTED = "key.NotConnectedNetworkFolder"; //ネットワークフォルダの接続に失敗しました。ログファイルを保存しお問い合わせください。
    public final static String NOT_AUTHENTICATED = "key.NotAuthenticatedNetworkFolder"; //ネットワークフォルダの認証情報が間違っています。
    public final static String NOT_AUTHORIZED = "key.NotAuthorizedNetworkFolder"; //ネットワークフォルダへのアクセスが拒否されました。
    public final static String NOT_FOUND = "key.NotFoundNetworkFolder"; //ネットワークフォルダが見つかりません。
    public final static String NOT_AVAILABLE = "key.NotAvailableNetworkFolder"; //ネットワークフォルダの再接続中にエラーが発生しました。
    public final static String NOT_MULTIPLE_LOGINS = "key.NotMultipleLoginsNetworkFolder"; //ネットワークフォルダに異なる資格情報での複数接続は許可されません。
    
    private final static Map<String, String> connectStatusMap = new HashMap<String, String>(){{
       put("system error 5", NOT_AUTHORIZED);
       put("access is denied", NOT_AUTHORIZED);
       put("アクセスが拒否されました。", NOT_AUTHORIZED);
       put("system error 1326", NOT_AUTHENTICATED);
       put("logon failure", NOT_AUTHENTICATED);
       put("ユーザー名またはパスワードが正しくありません。", NOT_AUTHENTICATED);
       put("system error 86", NOT_AUTHENTICATED);
       put("指定されたネットワーク パスワードが間違っています。", NOT_AUTHENTICATED);
       put("system error 53", NOT_FOUND);
       put("network path was not found", NOT_FOUND);
       put("ネットワークパスが見つかりませんでした。", NOT_FOUND);
       put("system error 55", NOT_FOUND);
       put("指定されたネットワーク リソースまたはデバイスは利用できません。", NOT_FOUND);
       put("system error 67", NOT_FOUND);
       put("network name cannot be found", NOT_FOUND);
       put("ネットワーク名が見つかりません。", NOT_FOUND);
       put("system error 1208", NOT_AVAILABLE);
       put("error occurred while reconnecting", NOT_AVAILABLE);
       put("拡張エラーが発生しました。", NOT_AVAILABLE);
       put("system error 1219", NOT_MULTIPLE_LOGINS);
       put("同じユーザーによる、サーバーまたは共有リソースへの複数のユーザー名での複数の接続は許可されません。", NOT_MULTIPLE_LOGINS);
    }};

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.load("locale");

    /**
     * 指定されたURLのネットワークフォルダ接続されているかを確認します。
     *
     * @param url 確認するネットワークフォルダのURL
     * @return true: 存在する場合、false: 存在しない場合
     */
    public static boolean isConnected(String url) {
        File networkFolder = new File(url);
        return networkFolder.exists();

    }

    /**
     * ネットワーク共有フォルダへの接続処理を実行します。
     *
     * @param url 接続先のネットワークフォルダのURL
     * @param user 接続に使用するユーザー名
     * @param password 接続に使用するパスワード
     * @return 接続が成功した場合はFileオブジェクトを右側に持つEither。失敗した場合はエラーメッセージを左側に持つEither。
     */
    private static Either<String, File> connectImpl(String url, String user, String password) {
        if (user == null || StringUtils.isEmpty(user)) {
            return Either.left(LocaleUtils.getString(NOT_AUTHENTICATED));
        }
        if (password == null || StringUtils.isEmpty(password)) {
            return Either.left(LocaleUtils.getString(NOT_AUTHENTICATED));
        }
        
        String command = String.format("cmd /c chcp 65001 > nul && net use %s /USER:%s %s", url, user, password);
        Process process = null;
        java.io.BufferedReader reader = null;
        StringBuilder output = new StringBuilder();

        try {
            process = Runtime.getRuntime().exec(command);
            reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            boolean success = process.waitFor(10, TimeUnit.SECONDS);
            int exitCode = process.exitValue();

            if (success && exitCode == 0) {
                // 接続成功
                return Either.right(new File(url));
            }

            // ネットワークフォルダに失敗した場合
            String errorOutput = output.toString().toLowerCase();
            logger.error(errorOutput);
            return Either.left(LocaleUtils.getString(connectStatusMap
                    .entrySet()
                    .stream()
                    .filter(entry -> errorOutput.contains(entry.getKey()))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(FAIL)));

        } catch (Exception ex) {
            logger.error(ex, ex);
            return Either.left(LocaleUtils.getString(NOT_CONNECTED));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error(e, e);
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }


    /**
     * Connect to a network share
     * @param url The network share URL
     * @param user The username for authentication
     * @param password The password for authentication
     * @return Optional containing the Path if connection was successful, empty Optional otherwise
     */
    public static Either<String, File> connect(String url, String user, String password) {
        if (StringUtils.isEmpty(url)) {
            return Either.left(LocaleUtils.getString(NOT_FOUND));
        }

        if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
            return Either.left(LocaleUtils.getString(NOT_AUTHENTICATED));
        }

        if (isConnected(url)) {
            return Either.right(new File(url));
        }
        return connectImpl(url, user, password);
    }
}
