package jp.adtekfuji.addatabaseapp;

import adtekfuji.utility.IniFile;
import adtekfuji.utility.PathUtils;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jp.adtekfuji.addatabase.common.AdDatabaseConfig;
import jp.adtekfuji.addatabase.utils.LocaleUtils;
import jp.adtekfuji.addatabaseapp.controller.PostgresManager;
import jp.adtekfuji.addatabaseapp.postgres.PGContents;
import jp.adtekfuji.addatabaseapp.utils.AdDatabaseUtils;
import jp.adtekfuji.addatabaseapp.utils.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * メインクラス
 *
 * @author e-mori
 */
public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger();
    private static final PostgresManager updateManager = new PostgresManager();

    private static final String LOCALE_BASE = "jp.adtekfuji.addatabase.locale.locale";

    /**
     * アプリケーション起動処理
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("prism.lcdtext", "false"); // 文字が虹色になるのを防止するために設定

        LocaleUtils.load(LOCALE_BASE, AdDatabaseConfig.getLocale());// 言語リソース読込

        // データベースメンテナンス画面
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/addatabaseapp/database_maintenance_compo.fxml"), LocaleUtils.getResourceBundle());

        Scene scene = new Scene(root);

        stage.setTitle(LocaleUtils.getString("key.adDatabaseAppTitle") + " - " + LocaleUtils.getString("key.version") + " " + getVersion());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting the application:{}", MainApp.getVersion());
        logger.info("args:{},{}", args.length, args);

        AdDatabaseConfig.load();// プロパティファイル読込
        List<String> argsList = Arrays.asList(args);
        if (!argsList.isEmpty() && ((argsList.contains("-update"))
                || (argsList.contains("-backup"))
                || (argsList.contains("-maintenance")))) {

            // DBバックアップのサイレント実行
            if (argsList.contains("-backup")) {
                try {
                    logger.info("Execute backup...");

                    // バックアップファイル生成
                    String nowDt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String backupDir = AdDatabaseConfig.getBackupDir();
                    File file = new File(backupDir, String.format("%s_%s.backup", PGContents.ADFACTORY_DB, nowDt));
                    
                    String filePath = file.getPath();
                    if (!updateManager.backupData(filePath)) {
                        logger.fatal("Backup failed.");
                    } else {
                        logger.info("Backup completed.");
                    }

                    // バックアップファイルの上限数を超えている場合、古いファイルを削除する。
                    AdDatabaseUtils.deleteOldBackupFiles(backupDir);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            // DBアップデートのサイレント実行
            if (argsList.contains("-update")) {
                try {
                    logger.info("Silent update...");
                    if (!updateManager.updateTable()) {
                        logger.fatal("Update failed.");
                    } else {
                        logger.info("Update completed.");
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
            // DBメンテナンスのサイレント実行
            if (argsList.contains("-maintenance")) {
                try {
                    logger.info("Execute maintenance...");
                    if (!updateManager.executeMaintenance()) {
                        logger.fatal("Database maintenance failed.");
                    } else {
                        logger.info("Database maintenance completed.");
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
        } else {
            // ファイルロックによる多重起動防止
            File tempDir = new File(System.getenv("ADFACTORY_HOME") + File.separator + "temp");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            File lockFile = new File(tempDir, "adDatabaseApp.lock");
            lockFile.deleteOnExit();
            try (FileChannel fc = FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE); FileLock lock = fc.tryLock()) {
                if (Objects.isNull(lock)) {
                    // すでに起動している
                    logger.info("Other process locked: {}", lockFile);
                    throw new RuntimeException(String.format("Other process locked: %s", lockFile.getName()));
                }

                launch(args);

            } catch (IOException | RuntimeException ex) {
                logger.fatal(ex, ex);
            }
        }
        AdDatabaseConfig.store();// プロパティファイル更新

        logger.info("Shutdown the application.");

        // Java VMを終了する
        System.exit(0);
    }

    /**
     * バージョンを取得する。
     *
     * @return
     */
    private static String getVersion() {
        String version = "";
        try {

            String path = PathUtils.getRootPath() + "\\version.ini";
            if (FileUtils.exists(new File(path))) {
                IniFile iniFile = new IniFile(path);
                version = iniFile.getString("Version", "Ver", null);
            } else {
                logger.warn("Nothing version.ini.");
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return version;
    }
}
