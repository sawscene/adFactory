/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.exportserviceplugin;

import adtekfuji.rest.RestClient;
import adtekfuji.rest.RestClientProperty;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import jp.adtekfuji.adFactory.adinterface.command.ActualNoticeCommand;
import jp.adtekfuji.adFactory.adinterface.command.WorkDetail;
import jp.adtekfuji.adFactory.adinterface.command.WorkResult;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity.ProcessInfo;
import jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity.ResponseMessage;
import jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity.SerialInfo;
import jp.adtekfuji.adinterfaceservice.exportserviceplugin.entity.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 完了報告通知情報プラグイン
 *
 * @author nouzawa
 */
public class ExportCompPlugin extends Thread implements AdInterfaceServiceInterface {

    private final String SERVICE_NAME = "ExportService";

    private final String PROP_UPLINK_FLAG = "上位連携フラグ";
    private final String PROP_SERIAL = "シリアル";
    private final String PROP_PROCESS = "工程";
    private final String PROP_WORK = "着工完工";

    private final String SEND_ERROR_PROCESS = "SendErrorProcess";// 送信エラーファイル (着工完工)
    private final String SEND_ERROR_SERIAL = "SendErrorSerial";// 送信エラーファイル (シリアル)

    private final String TEMP_PATH = System.getenv("ADFACTORY_HOME") + File.separator + "temp";

    private final String WEB_API_DATE_FORMAT = "yyyy/MM/dd'T'HH:mm:ss";// WebAPIで送信する日時フォーマット

    private final List<KanbanStatusEnum> TARTGET_STATUS_LIST = Arrays.asList(KanbanStatusEnum.WORKING, KanbanStatusEnum.COMPLETION);// 処理対象のステータス一覧

    private final List<String> WORK_START_VALUES = Arrays.asList("1", "3");// 着工を含む着工完工の値
    private final List<String> WORK_COMP_VALUES = Arrays.asList("2", "3");// 完工を含む着工完工の値

    private static final Logger logger = LogManager.getLogger();
    private final RestClient restClient;
    private final LinkedList<ActualNoticeCommand> recvQueue = new LinkedList<>();
    private boolean execution = true;
    private final Set<String> projectNoMst = new HashSet<>();

    private final ExportServiceConfig config = ExportServiceConfig.getInstance();

    /**
     * コンストラクタ
     */
    public ExportCompPlugin() {
        RestClientProperty restClientProperty = new RestClientProperty(this.config.getWebApiBaseUrl());
        restClientProperty.setMediaType(MediaType.APPLICATION_JSON_TYPE);

        this.config.getWebApiProcess();
        this.config.getWebApiSerial();
        this.config.getErrorLogBaseName();

        this.restClient = new RestClient(restClientProperty);
    }

    /**
     * サービスを開始する。
     *
     * @throws Exception
     */
    @Override
    public void startService() throws Exception {
        try {
            logger.info("Started ExportService.");

            super.start();

            this.execDaily();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * サービスを停止する。
     *
     * @throws Exception
     */
    @Override
    public void stopService() throws Exception {
        logger.info("Stopped ExportService.");

        this.execution = false;
        synchronized (this.recvQueue) {
            this.recvQueue.notify();
        }
        super.join();
    }

    /**
     * サービス名を取得する。
     *
     * @return
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    /**
     * 生産実績通知を受け取る
     *
     * @param cmd 生産実績通知
     */
    @Override
    public void noticeActualCommand(Object cmd) {
        ActualNoticeCommand command = (ActualNoticeCommand) cmd;

        // 処理対象外の工程カンバンステータスは処理しない。
        if (!TARTGET_STATUS_LIST.contains(command.getWorkKanbanStatus())) {
            return;
        }

        if (Objects.isNull(command.getWorkResult()) || Objects.isNull(command.getWorkResult().getDetails())) {
            // 実績データか実績詳細データがなければ抜ける
            return;
        }

        synchronized (this.recvQueue) {
            this.recvQueue.add(command);
            this.recvQueue.notify();
        }
    }

    /**
     * データ出力
     */
    @Override
    public void run() {
        while (execution) {
            try {
                synchronized (recvQueue) {
                    if (recvQueue.isEmpty()) {
                        try {
                            recvQueue.wait();
                        } catch (InterruptedException ex) {
                            logger.fatal(ex, ex);
                        }
                    }

                    if (!recvQueue.isEmpty()) {
                        ActualNoticeCommand command = recvQueue.removeFirst();
                        logger.info("CMD: {}", command);

                        List<WorkDetail> details = command.getWorkResult().getDetails();

                        // 上位連携フラグ
                        String uplinkFlag = this.getWorkDetailValue(details, PROP_UPLINK_FLAG);

                        if (StringUtils.equalsIgnoreCase(uplinkFlag, String.valueOf(Boolean.TRUE))) {
                            KanbanStatusEnum workKanbanStatus = command.getWorkKanbanStatus();
                            WorkResult workResult = command.getWorkResult();

                            ProcessInfo processInfo = this.createProcessInfo(workKanbanStatus, workResult);
                            if (Objects.nonNull(processInfo)) {
                                // 着工完工情報を通知する。
                                this.sendProcessInfo(processInfo);
                            }

                            if (KanbanStatusEnum.COMPLETION.equals(workKanbanStatus)) {
                                List<SerialInfo> serialInfos = this.createSerialInfos(workResult);
                                for (SerialInfo serialInfo : serialInfos) {
                                    // シリアル情報を通知する。
                                    this.sendSerialInfo(serialInfo);
                                }
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                logger.fatal(ex, ex);
            }
        }
    }

    /**
     * ファイルをダウンロードする。
     *
     * @param address FTPサーバー アドレス
     * @param port FTPサーバー ポート番号
     * @param user FTPサーバー ログインユーザー
     * @param password FTPサーバー ログインパスワード
     * @param fromPath FTPサーバー ダウンロードフォルダ
     * @param regex ファイル名のパターン
     * @throws Exception
     */
    private String downloadFtp(String address, Integer port, String user, String password, String fromPath, String regex) throws Exception {
        String localPath = null;
        FTPClient ftpClient = new FTPClient();

        try {
            logger.info("Connect FTP: {},{},{}", address, port, user);

            ftpClient.connect(address, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new Exception(String.format("Failed to connect to ftp server.(%d)", reply));
            }

            if (ftpClient.login(user, password) == false) {
                throw new Exception("Login to ftp server failed.");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(fromPath);

            FTPFile[] ftpFiles = ftpClient.listFiles();
            List<FTPFile> list = Arrays.asList(ftpFiles);

            // ファイル名で並べ替え(降順)、最新のファイルをダウンロード
            Comparator<FTPFile> comparator = Comparator.comparing(FTPFile::getName);
            Optional<FTPFile> opt = list.stream().filter(o -> Pattern.matches(regex, o.getName())).sorted(comparator.reversed()).findFirst();

            if (opt.isPresent()) {
                FTPFile file = opt.get();
                localPath = TEMP_PATH + File.separator + file.getName();
                logger.info("Download: " + localPath);
                try (FileOutputStream os = new FileOutputStream(localPath)) {
                    ftpClient.retrieveFile(fromPath + file.getName(), os);
                }
            }

        } catch (IOException ex) {
            throw ex;

        } finally {
            ftpClient.logout();
            ftpClient.disconnect();

            logger.info("Disconnect FTP: {},{}", address, port);
        }

        return localPath;
    }

    /**
     * 日次処理を実行する。
     */
    @Override
    public void execDaily() {
        try {
            String address = this.config.getFtpAddress();
            Integer port = this.config.getFtpServerPort();
            String user = this.config.getFtpLoginUser();
            String password = this.config.getFtpLoginPassword();
            String fromPath = this.config.getFtpFromDir();

            // 受注番号マスタを取得
            String filePath = this.downloadFtp(address, port, user, password, fromPath, "projectno_[0-9]{14}.tsv");
            if (!StringUtils.isEmpty(filePath)) {
                File file = new File(filePath);

                if (!file.exists()) {
                    logger.warn("File does not exist: " + filePath);
                    return;
                }

                this.projectNoMst.clear();

                FileReader fileReader = new FileReader(file);
                try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                    String data;
                    while ((data = bufferedReader.readLine()) != null) {
                        String array[] = data.split("\t");
                        if (array.length > 0) {
                            this.projectNoMst.add(array[0]);
                        }
                    }
                }

                // ダウンロードしたファイルを削除
                file.delete();
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 着工完工情報を作成する。
     *
     * @param workKanbanStatus 工程カンバンステータス
     * @param workResult 実績情報
     * @return 着工完工情報
     */
    private ProcessInfo createProcessInfo(KanbanStatusEnum workKanbanStatus, WorkResult workResult) {
        // 着工完工: 1:着工, 2:完工
        String work = this.getWorkDetailValue(workResult.getDetails(), PROP_WORK);
        if (KanbanStatusEnum.WORKING.equals(workKanbanStatus)
                && WORK_START_VALUES.contains(work)
                && Objects.isNull(workResult.getPairId())) {
            work = "1";
        } else if (KanbanStatusEnum.COMPLETION.equals(workKanbanStatus)
                && WORK_COMP_VALUES.contains(work)) {
            work = "2";
        } else {
            return null;
        }

        // ログ日時: 実績通知情報の実施日時 (yyyy/MM/ddTHH:mm:ss形式)
        String date = new SimpleDateFormat(WEB_API_DATE_FORMAT)
                .format(workResult.getImplementDatetime());

        // 工程: カンバンプロパティ(工程)の設定値
        String process = this.getWorkDetailValue(workResult.getDetails(), PROP_PROCESS);

        // 製造シリアル番号: カンバンプロパティ(シリアル)の設定値
        String serial = this.getWorkDetailValue(workResult.getDetails(), PROP_SERIAL);

        // 作業員ID: 実績通知情報の組織情報の組織識別子名
        String operator = workResult.getOrganizationIdentName();

        // 管理区分: 空文字
        String managementSection = "";

        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setDate(date);
        processInfo.setProcess(process);
        processInfo.setSerial(serial);
        processInfo.setWork(work);
        processInfo.setOperator(operator);
        processInfo.setManagementSection(managementSection);

        return processInfo;
    }

    /**
     * シリアル情報を作成する。
     *
     * @param workResult 実績情報
     * @return シリアル情報
     */
    private List<SerialInfo> createSerialInfos(WorkResult workResult) {
        List<SerialInfo> serialInfos = new LinkedList();

        if (Objects.isNull(workResult.getDetails())) {
            return serialInfos;
        }

        // ログ日時: 実績通知情報の実施日時 (yyyy/MM/ddTHH:mm:ss形式)
        String date = new SimpleDateFormat(WEB_API_DATE_FORMAT)
                .format(workResult.getImplementDatetime());

        // 工程: カンバンプロパティ(工程)の設定値
        String process = this.getWorkDetailValue(workResult.getDetails(), PROP_PROCESS);

        // 製造シリアル番号: カンバンプロパティ(シリアル)の設定値
        String serial = this.getWorkDetailValue(workResult.getDetails(), PROP_SERIAL);

        // 作業員ID: 実績通知情報の組織情報の組織識別子名
        String operator = workResult.getOrganizationIdentName();

        for (WorkDetail workDetail : workResult.getDetails()) {
            if (StringUtils.isEmpty(workDetail.getTag())
                    || !workDetail.getTag().contains("_SERIAL_")) {
                continue;
            }

            // 部品品目コード: 使用部品の品番
            String componentCode = workDetail.getName();

            // 部品シリアル番号: adProductにて入力されたシリアル番号
            String componentSerial = workDetail.getValue();

            SerialInfo serialInfo = new SerialInfo();
            serialInfo.setDate(date);
            serialInfo.setProcess(process);
            serialInfo.setSerial(serial);
            serialInfo.setComponentCode(componentCode);
            serialInfo.setComponentSerial(componentSerial);
            serialInfo.setOperator(operator);

            serialInfos.add(serialInfo);
        }

        return serialInfos;
    }

    /**
     * 実績詳細データリストから指定した項目名の値を取得する。
     *
     * @param workDetails 実績詳細データリスト
     * @param name 項目名
     * @return 値
     */
    private String getWorkDetailValue(List<WorkDetail> workDetails, String name) {
        String result = "";

        if (Objects.isNull(workDetails)) {
            return result;
        }

        Optional<WorkDetail> opt = workDetails.stream()
                .filter(p -> StringUtils.equals(p.getName(), name))
                .findFirst();
        if (opt.isPresent() && Objects.nonNull(opt.get().getValue())) {
            result = opt.get().getValue();
        }

        return result;
    }

    /**
     * WebAPIで、着工完工情報を送信する。
     *
     * @param processInfo 着工完工情報
     * @return 結果
     */
    private boolean sendProcessInfo(ProcessInfo processInfo) {
        logger.info("sendProcessInfo: processInfo={}", processInfo);
        boolean result = false;
        String body = null;
        try {
            String url = new StringBuilder()
                    .append(this.config.getWebApiBaseUrl())
                    .append(this.config.getWebApiProcess())
                    .toString();
            body = JsonUtils.objectToJson(processInfo);

            // Web API呼出し
            Response response = this.restClient.post(url, body);
            switch (response.getStatus()) {
                case 200:// OK
                case 201:// CREATED
                    result = true;
                    ResponseMessage responseMessage = this.getResponseMessage(response);
                    logger.info("sendProcessInfo success: {}", responseMessage.getMessage());
                    break;
                default:
                    Date date = this.parseWebApiDate(processInfo.getDate());
                    this.outputNoticeErrorFile(date, "sendProcessInfo error(" + response.getStatus() + "): " + body);
                    this.appendSendErrorFile(SEND_ERROR_PROCESS, body);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.outputNoticeErrorFile(new Date(), "sendProcessInfo error: " + body);
            this.appendSendErrorFile(SEND_ERROR_PROCESS, body);
        }

        return result;
    }

    /**
     * WebAPIで、シリアル情報を送信する。
     *
     * @param serialInfo シリアル情報
     * @return 結果
     */
    private boolean sendSerialInfo(SerialInfo serialInfo) {
        logger.info("sendSerialInfo: serialInfo={}", serialInfo);
        boolean result = false;
        String body = null;
        try {
            String url = new StringBuilder()
                    .append(this.config.getWebApiBaseUrl())
                    .append(this.config.getWebApiSerial())
                    .toString();
            body = JsonUtils.objectToJson(serialInfo);

            // Web API呼出し
            Response response = this.restClient.post(url, body);
            switch (response.getStatus()) {
                case 200:// OK
                case 201:// CREATED
                    result = true;
                    ResponseMessage responseMessage = this.getResponseMessage(response);
                    logger.info("sendSerialInfo success: {}", responseMessage.getMessage());
                    break;
                default:
                    Date date = this.parseWebApiDate(serialInfo.getDate());
                    this.outputNoticeErrorFile(date, "sendSerialInfo error(" + response.getStatus() + "): " + body);
                    this.appendSendErrorFile(SEND_ERROR_SERIAL, body);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            this.outputNoticeErrorFile(new Date(), "sendSerialInfo error: " + body);
            this.appendSendErrorFile(SEND_ERROR_SERIAL, body);
        }

        return result;
    }

    /**
     * メッセージをエラーファイルに出力する。
     *
     * @param date ログ日時
     * @param message メッセージ
     */
    private void outputNoticeErrorFile(Date date, String message) {
        try {
            // 終了日付、時刻取得
            String endDate = "";
            String endTime = "";
            if (Objects.nonNull(date)) {
                endDate = new SimpleDateFormat("yyyyMMdd").format(date);
            } else {
                endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
            }

            // エラーファイルのフルパスとファイル名を取得
            String path = new StringBuilder()
                    .append(System.getenv("ADFACTORY_HOME")).append(File.separator).append("logs").append(File.separator)
                    .append(this.config.getErrorLogBaseName()).append("_").append(endDate).append(".log")
                    .toString();

            String appendMessage = endTime + " ERROR " + message;

            this.appendFile(path, appendMessage);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 送信エラーファイルに、送信データを追加する。
     *
     * @param baseFileName 送信エラーファイルのベース名
     * @param body 送信データ
     */
    private void appendSendErrorFile(String baseFileName, String body) {
        try {
            String path = new StringBuilder()
                    .append(System.getenv("ADFACTORY_HOME")).append(File.separator).append("logs").append(File.separator)
                    .append(baseFileName).append("_").append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(".log")
                    .toString();

            this.appendFile(path, body);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ファイルにメッセージを追加する。
     *
     * @param path ファイルパス
     * @param message メッセージ
     */
    private void appendFile(String path, String message) {
        try {
            // ファイル更新
            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(message);
                bw.newLine();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * WebAPIの日時文字列をDateに変換する。
     *
     * @param dateString 日時文字列
     * @return Date
     */
    private Date parseWebApiDate(String dateString) {
        try {
            return new SimpleDateFormat(WEB_API_DATE_FORMAT).parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }

    /**
     * WebAPIのレスポンスから応答メッセージ情報を取得する。
     *
     * @param response WebAPIのレスポンス
     * @return 応答メッセージ情報
     */
    private ResponseMessage getResponseMessage(Response response) {
        ResponseMessage message = null;
        try {
            String body = response.readEntity(String.class);
            message = JsonUtils.jsonToObject(body, ResponseMessage.class);
        } catch(Exception ex) {
            logger.fatal(ex, ex);
        }
        return message;
    }
}
