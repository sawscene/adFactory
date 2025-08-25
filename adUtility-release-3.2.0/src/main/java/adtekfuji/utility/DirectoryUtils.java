package adtekfuji.utility;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * ディレクトリ操作に関するユーティリティクラス。
 * ディレクトリの作成、削除、再作成などの機能を提供します。
 */
public class DirectoryUtils {

    /**
     * 指定されたディレクトリを再帰的に削除します。
     * ディレクトリ内に存在する全てのファイルおよびサブディレクトリも削除されます。
     *
     * @param directoryPath 削除対象のディレクトリのパス
     * @return 削除が成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean deleteDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }
        File directory = new File(directoryPath);
        return deleteFileRecursively(directory);
    }

    /**
     * 指定されたファイルまたはディレクトリを再帰的に削除します。
     * ディレクトリの場合、その中の全てのファイルやディレクトリも削除されます。
     *
     * @param file 削除対象のファイルまたはディレクトリ
     * @return 削除が成功した場合はtrue、失敗した場合はfalse
     */
    private static boolean deleteFileRecursively(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (Objects.nonNull(contents)) {
                for (File f : contents) {
                    if (!deleteFileRecursively(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    /**
     * 指定されたパスにディレクトリを作成します。
     * 親ディレクトリが存在しない場合は作成に失敗します。
     *
     * @param directoryPath 作成するディレクトリのパス
     * @return 作成が成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean createDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }
        File directory = new File(directoryPath);
        return directory.mkdir();
    }

    /**
     * 指定されたパスにディレクトリを作成します。
     * 必要な親ディレクトリも同時に作成されます。
     *
     * @param directoryPath 作成するディレクトリのパス
     * @return 作成が成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean createDirectories(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }
        File directory = new File(directoryPath);
        return directory.mkdirs();
    }

    /**
     * 指定されたディレクトリを削除して再作成します。
     * ディレクトリが存在する場合は、その中身を全て削除してから新しく作り直します。
     * ディレクトリが存在しない場合は、新規作成します。
     *
     * @param directoryPath 再作成するディレクトリのパス
     * @return 再作成が成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean recreateDirectory(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }

        File directory = new File(directoryPath);

        if (directory.exists()) {
            if (!deleteDirectory(directoryPath)) {
                return false;
            }
        }

        return createDirectory(directoryPath);
    }

    /**
     * 指定されたディレクトリを削除して新規作成します。
     * ディレクトリが既に存在する場合、その中身を全て削除してから新たに作り直します。
     * ディレクトリが存在しない場合は、親ディレクトリも含めて作成します。
     *
     * @param directoryPath 再作成するディレクトリのパス
     * @return 処理が成功した場合はtrue、失敗した場合はfalse
     */
    public static boolean recreateDirectories(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            return false;
        }

        File directory = new File(directoryPath);

        if (directory.exists()) {
            if (!deleteDirectory(directoryPath)) {
                return false;
            }
        }

        return createDirectories(directoryPath);
    }
}