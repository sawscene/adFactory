/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.odbcserviceplugin;

import adtekfuji.clientservice.EquipmentInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.WorkKanbanInfoFacade;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportEntity;
import jp.adtekfuji.adFactory.entity.kanban.ActualProductReportResult;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.workkanban.WorkKanbanInfoEntity;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.adFactory.plugin.AdInterfaceServiceInterface;
import jp.adtekfuji.mainapp.LocalePluginInterface;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.charset.StandardCharsets;


/**
 * ODBCサービスプラグイン
 *
 * @author s-heya
 */
public class OdbcServicePlugin implements AdInterfaceServiceInterface {

    enum InterfaceType {
        FTP,
        ODBC
    }

    private final String SERVICE_NAME = "OdbcService";

    // JDBC
    private final String URL_DATABASE = "jdbc:postgresql://localhost/adInterfaceDB";
    private final String USER = "adtek";
    private final String PASSWORD = "adtek";

    // SQL文
    private final String SELECT_PROCESS = "SELECT * FROM trn_process p WHERE p.outputted IS NULL OR p.outputted = FALSE ORDER BY p.datetime, p.id";
    private final String UPDATE_PROCESS = "UPDATE trn_process SET outputted = ? WHERE id = ?";

    // 列名
    private final String COLUMN_ID = "id";
    private final String COLUMN_EVENTID = "event_id";
    private final String COLUMN_DATETIME = "datetime";
    private final String COLUMN_WORKID = "work_id";

    // 設定関連
    private final String SERVICE_ENABLE = "enable";
    private final String EQUIPMENT_IDENTIFIER = "equipmentIdentifier";
    private final String ORGANIZATION_NAME = "organizationName";
    private final String WORK_NAME = "workName";
    private final String DELAY = "delay";
    private final String SERVER_NAME = "serverName";

    private final String CSV_SEPARATOR = ",";

    private final int IDX_EQUIPMENT_IDENTIFIER = 0;
    private final int IDX_EVENT_ID = 1;
    private final int IDX_TIMESTAMP = 2;
    private final int IDX_WORK_ID = 3;

    private final OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
    private final KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
    private final WorkKanbanInfoFacade workKanbanInfoFacade = new WorkKanbanInfoFacade();
    private final EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();
    private final Logger logger = LogManager.getLogger();

    private String serverName;
    private final String usserName = "pi";
    private final String password = "raspberry";
    private final String sourcePath = "/home/pi/Public/ayaiot/adtek/adtek1/output";
    private final String targetPath = "C:\\adFactory\\temp";


    private Timer timer;
    private Connection sqlConnection = null;
    private Statement sqlStatement = null;
    private String workName;
    private long equipmentId;
    private long organizationId;
    private long transactionId;
    private long delay;
    private final InterfaceType interfaceType;

    /**
     * コンストラクタ
     *
     * @throws Exception
     */
    public OdbcServicePlugin() throws Exception {
        PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
        PluginLoader.load(LocalePluginInterface.class);

        AdProperty.load(SERVICE_NAME, "OdbcService.properties");

        if (!AdProperty.getProperties(SERVICE_NAME).containsKey("interfaceType")) {
            AdProperty.getProperties(SERVICE_NAME).setProperty("interfaceType", InterfaceType.FTP.name());
            AdProperty.store(SERVICE_NAME);
        }

        this.interfaceType = InterfaceType.valueOf(AdProperty.getProperties(SERVICE_NAME).getProperty("interfaceType"));
    }

    /**
     * サービスを開始する。
     *
     * @throws Exception
     */
    @Override
    public void startService() throws Exception {
        try {
            logger.info("startService start.");

            Boolean enable = Boolean.parseBoolean(AdProperty.getProperties(SERVICE_NAME).getProperty(SERVICE_ENABLE, "false"));
            if (!enable) {
                logger.info("OdbcServicePlugin is disabled.");
                return;
            }

            // 設備IDを取得
            String equipmentIdentifier = AdProperty.getProperties(SERVICE_NAME).getProperty(EQUIPMENT_IDENTIFIER, "unit1");
            EquipmentInfoEntity equipment = equipmentInfoFacade.findName(URLEncoder.encode(equipmentIdentifier, "UTF-8"));
            if (Objects.isNull(equipment.getEquipmentId())) {
                this.alert("設備を登録して下さい: " + equipmentIdentifier);
                return;
            }
            this.equipmentId = equipment.getEquipmentId();

            // 組織IDを取得
            String organizationName = AdProperty.getProperties(SERVICE_NAME).getProperty(ORGANIZATION_NAME, "溶接");
            OrganizationInfoEntity organization = organizationInfoFacade.findName(URLEncoder.encode(organizationName, "UTF-8"));
            if (Objects.isNull(organization.getOrganizationId())) {
                this.alert("組織を登録して下さい: " + organizationName);
                return;
            }
            this.organizationId = organization.getOrganizationId();

            // 工程名
            this.workName = AdProperty.getProperties(SERVICE_NAME).getProperty(WORK_NAME, "溶接");

            // 遅延時間
            this.delay = Long.parseLong(AdProperty.getProperties(SERVICE_NAME).getProperty(DELAY, "3000"));

            // サーバー名を取得
            this.serverName = AdProperty.getProperties(SERVICE_NAME).getProperty(SERVER_NAME, "192.168.11.12");

            // トランザクションID
            this.transactionId = 0L;

            File dir = new File(this.targetPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            this.timer = new Timer();
            this.schedule();
        }
        finally {
            logger.info("startService end.");
        }
    }

    /**
     * サービスを停止する。
     *
     * @throws Exception
     */
    @Override
    public void stopService() throws Exception {
        try {
            logger.info("stopService start.");

            this.timer.cancel();
            if (InterfaceType.ODBC == this.interfaceType) {
                this.closeDB();
            }
        }
        finally {
            logger.info("stopService end.");
        }
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
     * ポーリングを予約する。
     *
     */
    private void schedule() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (InterfaceType.FTP == interfaceType) {
                        pollingFtp();
                    } else {
                        pollingDB();
                    }
                }
                catch (Exception ex) {
                    logger.fatal(ex, ex);
                }
            }
        };

        this.timer.schedule(task, this.delay);
    }

    /**
     * プロセスデータをポーリング。
     */
    private synchronized void pollingDB() throws Exception {
        ResultSet resultSet = null;

        try {
            if (Objects.isNull(sqlStatement)) {
                this.openDB();
            }

            resultSet = sqlStatement.executeQuery(SELECT_PROCESS);

            while (resultSet.next()) {
                Integer id = resultSet.getInt(COLUMN_ID);
                String kanbanName = resultSet.getString(COLUMN_WORKID);
                Timestamp timeStamp = resultSet.getTimestamp(COLUMN_DATETIME);
                Date date = new Date(timeStamp.getTime());
                Integer eventId = resultSet.getInt(COLUMN_EVENTID);

                logger.info("Found unregistered data: {} {} {} {}", id, kanbanName, date, eventId);

                // カンバン
                KanbanInfoEntity kanban = this.kanbanInfoFacade.findName(kanbanName);
                if (Objects.isNull(kanban.getKanbanId())) {
                    logger.info("!!! Not found the kanban: " + kanbanName);
                    this.updateOutputted(id, true);
                    continue;
                }

                if (!(KanbanStatusEnum.PLANNED == kanban.getKanbanStatus()
                    || KanbanStatusEnum.WORKING == kanban.getKanbanStatus()
                    || KanbanStatusEnum.SUSPEND == kanban.getKanbanStatus())) {
                    logger.info("!!! Please check the kanban status: {} {}", kanbanName, kanban.getKanbanStatus());
                    this.updateOutputted(id, true);
                    continue;
                }

                // 工程カンバン
                WorkKanbanInfoEntity workKanban = this.workKanbanInfoFacade.getWorkKanban(kanban.getKanbanId(), this.workName);
                if (Objects.isNull(workKanban)) {
                    logger.info("!!! Not found the work kanban: " + kanbanName);
                    this.updateOutputted(id, true);
                    continue;
                }

                if (workKanban.getSkipFlag()) {
                    logger.info("!!! Skip the work kanban: " + kanbanName);
                    this.updateOutputted(id, true);
                    continue;
                }

                if (!(workKanban.getImplementFlag() && KanbanStatusEnum.COMPLETION != workKanban.getWorkStatus())) {
                    logger.info("!!! Please check the work status: {} {} {}", workKanban.getWorkKanbanId(), workKanban.getImplementFlag(), workKanban.getWorkStatus());
                    if (KanbanStatusEnum.COMPLETION == workKanban.getWorkStatus()) {
                        this.updateOutputted(id, true);
                    }
                    continue;
                }

                KanbanStatusEnum status = KanbanStatusEnum.OTHER;
                switch (eventId) {
                    case 1:
                        status = KanbanStatusEnum.WORKING;
                        break;
                    case 2:
                        status = KanbanStatusEnum.COMPLETION;
                        break;
                    case 3:
                        status = KanbanStatusEnum.SUSPEND;
                        break;
                }

                // 実績出力済みフラグを更新
                ActualProductReportEntity report = new ActualProductReportEntity(this.transactionId, kanban.getKanbanId(), workKanban.getWorkKanbanId(), equipmentId, organizationId, date, status, null, null);
                ActualProductReportResult result = this.kanbanInfoFacade.report(report);
                this.transactionId = result.getNextTransactionID();

                if (ServerErrorTypeEnum.SUCCESS == result.getResultType()) {
                    this.updateOutputted(id, true);
                    logger.info("Registered actual data: {}", kanbanName);
                } else {
                    logger.info("Failed to register actual data: {} {}", kanbanName, result.getResultType());
                }
            }

            resultSet.close();
        }
        catch (Exception ex) {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }

            this.closeDB();

            logger.fatal(ex, ex);
        }
        finally {
            this.schedule();
        }
    }

    /**
     * adInterfaceDBに接続する。
     *
     * @throws Exception
     */
    private void openDB() throws Exception {
	try {
            logger.info("openDB start.");

            Class.forName("org.postgresql.Driver");

            this.sqlConnection = DriverManager.getConnection(URL_DATABASE, USER, PASSWORD);
            this.sqlConnection.setAutoCommit(false);
            this.sqlStatement = this.sqlConnection.createStatement();

        } catch (ClassNotFoundException | SQLException ex) {
            if (this.sqlStatement != null) {
                this.sqlStatement.close();
                this.sqlStatement = null;
            }

            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }

            throw ex;
        }
        finally {
            logger.info("openDB end.");
        }
    }

    /**
     * adInterfaceDBを切断する。
     *
     * @throws Exception
     */
    private void closeDB() {
        try {
            logger.info("closeDB start.");

            if (this.sqlStatement != null) {
                this.sqlStatement.close();
                this.sqlStatement = null;
            }

            if (this.sqlConnection != null) {
                this.sqlConnection.close();
                this.sqlConnection = null;
            }
        }
        catch (SQLException ex) {
            logger.fatal(ex, ex);
        }
        finally {
            logger.info("closeDB end.");
	}
    }

    /**
     * 実績出力済みフラグを更新する
     *
     * @param id
     * @param outputted
     * @throws Exception
     */
    private void updateOutputted(Integer id, Boolean outputted) throws Exception {
        PreparedStatement preparedStatement = null;

        try {
            // 登録済みフラグを更新
            preparedStatement = this.sqlConnection.prepareStatement(UPDATE_PROCESS);
            preparedStatement.setBoolean(1, outputted);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

            this.sqlConnection.commit();
        }
        catch (Exception ex) {
            this.sqlConnection.rollback();
            throw ex;
        }
        finally {
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
        }
    }

    /**
     * エラーを表示する。
     *
     * @param message
     */
    private void alert(String message) {
        logger.error(message);
        JOptionPane.showMessageDialog(null, message, SERVICE_NAME, JOptionPane.ERROR_MESSAGE);
    }

    /**
     *
     *
     * @throws Exception
     */
    private synchronized void pollingFtp() throws Exception {

        FTPClient ftpClient = new FTPClient();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

            // サーバに接続
            ftpClient.connect(this.serverName);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.fatal("connect fail.");
            }

            // ログイン
            if (ftpClient.login(this.usserName, this.password) == false) {
                logger.fatal("login fail.");
            }

            // バイナリモードに設定
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            if (!ftpClient.changeWorkingDirectory(this.sourcePath)) {
                logger.fatal("changeWorkingDirectory fail.");
            }

            FTPFile[] flist = ftpClient.listFiles();
            for (int i = 0; i < flist.length; i++) {

                if (!flist[i].isFile() || !flist[i].getName().endsWith(".csv")) {
                    continue;
                }

                String fileName = flist[i].getName();
                logger.info(fileName);

                // ファイル受信
                File file = new File(this.targetPath + "\\" + fileName);
                if (file.exists()) {
                    file.delete();
                }

                try (FileOutputStream ostream = new FileOutputStream(file)) {
                    if (!ftpClient.retrieveFile(fileName, ostream)) {
                        logger.fatal("retrieveFile fail.");
                        return;
                    }

                }

                // データ解析
                List<List<String>> rows = this.parseCsv(file);

                for (List<String> row : rows) {
                    String kanbanName = row.get(IDX_WORK_ID);
                    Date date = sdf.parse(row.get(IDX_TIMESTAMP));
                    int eventId = Integer.parseInt(row.get(IDX_EVENT_ID));

                    // 実績を登録
                    if (this.regist(kanbanName, date, eventId)) {
                        ftpClient.deleteFile(fileName);
                    }
                }
            }
        }
        catch (Exception e) {
            logger.fatal(e, e);
        }
        finally {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }

            this.schedule();
        }
    }

    /**
     * 実績を登録する。
     *
     * @param kanbanName
     * @param date
     * @param eventId
     * @return
     * @throws Exception
     */
    private boolean regist(String kanbanName, Date date, int eventId) throws Exception {
        boolean success = false;

        try {
            logger.info("regist start: {} {} {}", kanbanName, date, eventId);

            // カンバン
            KanbanInfoEntity kanban = this.kanbanInfoFacade.findName(kanbanName);
            if (Objects.isNull(kanban.getKanbanId())) {
                logger.info("!!! Not found the kanban: " + kanbanName);
                return success;
            }

            if (!(KanbanStatusEnum.PLANNED == kanban.getKanbanStatus()
                || KanbanStatusEnum.WORKING == kanban.getKanbanStatus()
                || KanbanStatusEnum.SUSPEND == kanban.getKanbanStatus())) {
                logger.info("!!! Please check the kanban status: {} {}", kanbanName, kanban.getKanbanStatus());
            }

            // 工程カンバン
            WorkKanbanInfoEntity workKanban = this.workKanbanInfoFacade.getWorkKanban(kanban.getKanbanId(), this.workName);
            if (Objects.isNull(workKanban)) {
                logger.info("!!! Not found the work kanban: " + kanbanName);
                return success;
            }

            if (workKanban.getSkipFlag()) {
                logger.info("!!! Skip the work kanban: " + kanbanName);
                return success;
            }

            if (!(workKanban.getImplementFlag() && KanbanStatusEnum.COMPLETION != workKanban.getWorkStatus())) {
                logger.info("!!! Please check the work status: {} {} {}", workKanban.getWorkKanbanId(), workKanban.getImplementFlag(), workKanban.getWorkStatus());
                if (KanbanStatusEnum.COMPLETION == workKanban.getWorkStatus()) {
                }
                return success;
            }

            KanbanStatusEnum status = KanbanStatusEnum.OTHER;
            switch (eventId) {
                case 1:
                    status = KanbanStatusEnum.WORKING;
                    break;
                case 2:
                    status = KanbanStatusEnum.COMPLETION;
                    break;
                case 3:
                    status = KanbanStatusEnum.SUSPEND;
                    break;
            }

            // 実績出力済みフラグを更新
            ActualProductReportEntity report = new ActualProductReportEntity(this.transactionId, kanban.getKanbanId(), workKanban.getWorkKanbanId(), equipmentId, organizationId, date, status, null, null);
            ActualProductReportResult result = this.kanbanInfoFacade.report(report);
            this.transactionId = result.getNextTransactionID();

            success = true;
        }
        finally {
            logger.info("regist end.");
        }

        return success;
    }


    /**
     * CSVファイルを読み込む。
     * @param file
     * @return
     * @throws Exception
     */
    private List<List<String>> parseCsv(File file) throws Exception {
        List<List<String>> rows = new ArrayList<>();

        if (!file.exists()) {
            logger.info("File does not exist:{}", file);
            return rows;
        }

        InputStream input = new FileInputStream(file);
        InputStreamReader ireader = new InputStreamReader(input, StandardCharsets.ISO_8859_1);
        try (BufferedReader br = new BufferedReader(ireader)) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> row = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(line, CSV_SEPARATOR);
                while (st.hasMoreTokens()) {
                    String col = st.nextToken();
                    row.add(col);
                }
                rows.add(row);
            }
        }
        return rows;
    }
    @Override
    public void noticeActualCommand(Object cmd){
    }
}
