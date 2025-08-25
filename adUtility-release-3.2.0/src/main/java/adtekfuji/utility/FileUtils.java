package adtekfuji.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {
    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    public final static String SUCCESS = "Success";
    public final static String SOURCE_FILE_DONT_EXIST = "Source file does not exist";
    public final static String TARGET_FILE_ALREADY_EXISTS = "Target file already exists";
    public final static String ERROR_RENAMING_FILE = "Error renaming file";
    public final static String ERROR_MOVING_FILE = "Error moving file";

    /**
     * 指定されたファイルを別のファイル名にリネームします。
     *
     * @param sourcePath リネーム対象のファイルのパス
     * @param targetPath 新しいファイル名のパス
     * @return リネーム処理の結果を示す文字列
     *         - "Success": ファイルのリネームに成功した場合
     *         - "Error renaming file": ファイルのリネーム中にエラーが発生した場合
     */
    public static String renameFile(Path sourcePath, Path targetPath) {
        try {
            return moveFileImpl(sourcePath, targetPath);
        } catch (IOException e) {
            logger.error("Error renaming file from {} to {}: {}", sourcePath, targetPath, e.getMessage());
            return ERROR_RENAMING_FILE;
        }
    }

    /**
     * 指定されたファイルを移動元パスから移動先パスへ移動します。
     *
     * @param sourcePath 移動元ファイルのパス
     * @param targetPath 移動先ファイルのパス
     * @return 移動処理の結果を示す文字列
     *         - "Success": ファイルの移動に成功した場合
     *         - "Error moving file": ファイルの移動中にエラーが発生した場合
     */
    public static String moveFile(Path sourcePath, Path targetPath) {
        try {
            return moveFileImpl(sourcePath, targetPath);
        } catch (IOException e) {
            logger.error("Error moving file from {} to {}: {}", sourcePath, targetPath, e.getMessage());
            return ERROR_MOVING_FILE;
        }
    }


    /**
     * 指定されたファイルを指定されたパスへ移動します。
     *
     * @param sourcePath 移動元ファイルのパス
     * @param targetPath 移動先ファイルのパス
     * @return 移動処理の結果を示す文字列
     *         - "Success": ファイルの移動に成功した場合
     *         - "Source file does not exist": 移動元ファイルが存在しない場合
     *         - "Target file already exists": 移動先に既にファイルが存在する場合
     * @throws IOException ファイルの移動中にI/Oエラーが発生した場合
     */
    private static String moveFileImpl(Path sourcePath, Path targetPath) throws IOException {

            if (!Files.exists(sourcePath)) {
                logger.error("Source file does not exist: {}", sourcePath);
                return SOURCE_FILE_DONT_EXIST;
            }

            if (Files.exists(targetPath)) {
                logger.error("Target file already exists: {}", targetPath);
                return TARGET_FILE_ALREADY_EXISTS;
            }

            Files.move(sourcePath, targetPath, StandardCopyOption.ATOMIC_MOVE);
            return SUCCESS;
    }

}
