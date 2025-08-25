/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adlinkservice.adlinkfpinspection;

import adtekfuji.utility.IniFile;
import adtekfuji.utility.StringUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import jp.adtekfuji.adlinkservice.command.DevRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * プロセスワーカー
 *
 * @author s-heya
 */
public class ProcessWorker {

    /**
     * 検査エラー
     */
    public enum InspectionErrorEnum {
        FILE_WRITE_ERROR,
        TIMEOUT_ERROR;
    }

    private static final Logger logger = LogManager.getLogger();

    private static final String BASE_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "adProductApp\\";
    private static final String COM_FILE_FOLDER = "ext";
    private static final String COM_FILE_NAME = "inspectionCommand.txt";

    private static final String INI_LINE_END = "\r\n";
    private static final String INI_SECTION = "inspectionCommand";
    private static final String KEY_COMMAND = "command";
    private static final String KEY_INSPECTION = "inspection";
    private static final String KEY_RESULT = "result";
    private static final String KEY_DATA = "data";

    private static ProcessWorker instance;
    private static List<String> inspectionValues;
    private static InspectionCommand inspectionResult;
    private final IniFile iniFile;
    private InspectionCommandFile commandFile;

    /**
     * コンストラクタ
     * 
     * @param iniFile 設定ファイル
     */
    private ProcessWorker(IniFile iniFile) {
        this.iniFile = iniFile;
    }

    /**
     * インスタンスを生成する。
     *
     * @param iniFile 設定ファイル
     * @return インスタンス
     */
    public static ProcessWorker createIncetance(IniFile iniFile) {
        instance = new ProcessWorker(iniFile);
        return instance;
    }

    /**
     * インスタンスを取得する。
     *
     * @return インスタンス
     */
    public static synchronized ProcessWorker getIncetance() {
        return instance;
    }

    /**
     * 検査結果を取得する。
     *
     * @return 検査結果リスト
     */
    public List<String> getInspectionValues() {
        return inspectionValues;
    }

    public InspectionCommand getInspectionResult() {
        return inspectionResult;
    }

    /**
     * 検査を開始する。
     *
     * @param command 要求コマンド
     * @throws java.lang.Exception
     */
    public void startInspectionResult(DevRequest command) throws Exception {
        logger.info("{0}::startInspection start.", ProcessWorker.class.getSimpleName());

        // 設備識別名
        String equipIdent = command.getEquipIdent();
        // 通信ファイルのエンコード
        String encode = this.iniFile.getString(equipIdent, "encode", "MS932");
        if (Arrays.asList("SHIFT_JIS", "SHIFT-JIS", "SJIS", "S-JIS").contains(encode.toUpperCase())) {
            encode = "MS932";
        }

        // 検査タイムアウト[sec]
        int timeout = this.iniFile.getInt(equipIdent, "timeout", 60);
        String inspectionCode = Objects.nonNull(command.getArgs()) ? command.getArgs().get(0) : "";

        inspectionValues = new LinkedList<>();
        inspectionResult = new InspectionCommand();
        inspectionResult = execInspection(encode, timeout, inspectionCode);

        if (inspectionResult != null) {
            // データ
            if (inspectionResult.getDatas() != null && !inspectionResult.getDatas().isEmpty()) {
                inspectionValues.addAll(inspectionResult.getDatas());
            }
        }

        logger.info("{0}::startInspection end.", ProcessWorker.class.getSimpleName());
    }

    /**
     * 検査
     *
     * @param encode エンコード
     * @param timeout タイムアウト
     * @param inspectionCode 検査コマンド(16進数)
     * @return 検査結果の検査コマンド情報
     * @throws Exception
     */
    private InspectionCommand execInspection(String encode, int timeout, String inspectionCode) throws Exception {
        InspectionCommand result = null;

        // 通信ファイルに検査開始情報を書き込む。
        if (!this.createCommandFile(encode, inspectionCode)) {
            // 書き込み失敗
            result = new InspectionCommand();
            result.setInspectionError(InspectionCommand.InspectionErrorEnum.FILE_WRITE_ERROR);
            return result;
        }

        // 通信ファイルを監視する。
        int checkCount = 0;
        while (true) {
            result = readCommandFile(encode);
            if (result != null
                    && result.getCommand() == InspectionCommand.FpCommandEnum.INSPECTION_RESULT) {
                break;
            }

            checkCount++;
            if (checkCount > (timeout * 10)) {
                // タイムアウト
                result = new InspectionCommand();
                result.setInspectionError(InspectionCommand.InspectionErrorEnum.TIMEOUT_ERROR);
                break;
            }

            TimeUnit.MILLISECONDS.sleep(100L);
        }

        return result;
    }

    /**
     * 検査開始の通信ファイルを作成する。
     *
     * @param encode エンコード
     * @param inspectionCode 検査コマンド(16進数)
     * @return 結果(true:成功, false:失敗)
     * @throws Exception
     */
    private boolean createCommandFile(String encode, String inspectionCode) throws Exception {
        boolean isCreate = false;

        // 通信ファイルのフォルダ(存在しない場合は作成)
        File folder = Paths.get(BASE_PATH, COM_FILE_FOLDER).toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 通信ファイル
        File file = new File(folder, COM_FILE_NAME);

        // 通信ファイルに検査開始情報を書き込む。(書き込み中はファイルをロック)
        try (FileOutputStream fos = new FileOutputStream(file.getPath());
                Writer writer = new BufferedWriter(new OutputStreamWriter(fos, encode));
                FileChannel fc = fos.getChannel();
                FileLock lock = fc.lock()) {
            if (lock != null) {
                // コマンド(検査開始)
                String command = Integer.toHexString(InspectionCommand.FpCommandEnum.INSPECTION_START.getIntValue());

                StringBuilder sb = new StringBuilder();
                sb.append("[").append(INI_SECTION).append("]").append(INI_LINE_END);
                sb.append(KEY_COMMAND).append("=").append(command).append(INI_LINE_END);
                sb.append(KEY_INSPECTION).append("=").append(inspectionCode).append(INI_LINE_END);
                sb.append(KEY_RESULT).append("=").append(INI_LINE_END);
                sb.append(KEY_DATA).append("=").append(INI_LINE_END);

                writer.write(sb.toString());
                writer.flush();

                isCreate = true;
            }
        } catch (FileNotFoundException ex) {
            // ファイルが他プロセスにロックされている場合、FileOutputStreamでFileNotFoundExceptionとなる。
            logger.error(ex, ex);
        }

        return isCreate;
    }

    /**
     * 通信ファイルを読み込む。
     *
     * @return 読み込んだ情報
     * @throws Exception
     */
    private InspectionCommand readCommandFile(String encode) throws Exception {
        // 通信ファイルパス
        Path path = Paths.get(BASE_PATH, COM_FILE_FOLDER, COM_FILE_NAME);

        // 通信ファイルを読み込む。
        try {
            this.commandFile = new InspectionCommandFile(path.toString(), encode);
        } catch (FileNotFoundException ex) {
            // ファイルが他プロセスにロックされている場合、FileNotFoundExceptionとなる。
            return null;
        }

        // コマンド
        InspectionCommand.FpCommandEnum command;
        String commandString = this.commandFile.getString(INI_SECTION, KEY_COMMAND, "");
        if (commandString.isEmpty()) {
            command = InspectionCommand.FpCommandEnum.INSPECTION_START;
        } else {
            command = InspectionCommand.FpCommandEnum.valueOf(Integer.valueOf(commandString, 16));
        }

        // 検査コマンド
        String inspection = this.commandFile.getString(INI_SECTION, KEY_INSPECTION, "");

        // 検査結果
        String commandResult = this.commandFile.getString(INI_SECTION, KEY_RESULT, "");

        // データ
        String inspectionDatas = commandFile.getString(INI_SECTION, KEY_DATA, "");
        if (StringUtils.isEmpty(inspectionDatas)) {
            inspectionDatas = "";
        }

        List<String> values;
        if (!inspectionDatas.isEmpty()) {
            String[] datas = inspectionDatas.split(",", 0);
            values = Arrays.asList(datas);
        } else {
            values = new LinkedList();
        }

        // 検査コマンド情報
        InspectionCommand result = new InspectionCommand();

        result.setCommand(command);
        result.setInspection(inspection);
        result.setResult(commandResult);
        result.setDatas(values);

        return result;
    }
}
