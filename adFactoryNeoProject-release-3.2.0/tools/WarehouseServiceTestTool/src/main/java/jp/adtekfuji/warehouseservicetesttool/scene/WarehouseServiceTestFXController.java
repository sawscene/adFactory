package jp.adtekfuji.warehouseservicetesttool.scene;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.warehouseservicetesttool.entity.BhtCommand;
import jp.adtekfuji.warehouseservicetesttool.entity.BhtStktake;
import jp.adtekfuji.warehouseservicetesttool.entity.BhtStktakeRes;
import jp.adtekfuji.warehouseservicetesttool.utils.BhtFileRecordConverter;
import jp.adtekfuji.warehouseservicetesttool.utils.FileUtility;
import jp.adtekfuji.warehouseservicetesttool.utils.WarehouseClientHandler;
import jp.adtekfuji.warehouseservicetesttool.utils.WarehouseClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WarehouseServiceTestFXController implements Initializable {

    private final Logger logger = LogManager.getLogger();

    private static final int TIMEOUT = 30 * 1000;

    private String remoteAddress = "127.0.0.1";
    private String ipAddress = "127.0.0.1";
    private String testDir;

    private final HashMap<String, Boolean> execMap = new HashMap<>();

    @FXML
    private TextField remoteAddressText;
    @FXML
    private TextField ipAddressText;
    @FXML
    private TextField handyCountText;
    @FXML
    private TextField testDirText;
    @FXML
    private GridPane operationPane;
    @FXML
    private Button stktakeButton;
    @FXML
    private Label label;
    @FXML
    private Pane progressPane;
    @FXML
    private ListView resultList;

    @FXML
    private void stktakeButtonAction(ActionEvent event) {
        try {
            blockUI(true);
            this.clearResult();

            this.remoteAddress = remoteAddressText.getText();// 倉庫サービスのIPアドレス
            this.ipAddress = ipAddressText.getText();// IPアドレス
            int handyCount = Integer.parseInt(handyCountText.getText());// ハンディ端末の台数
            this.testDir = testDirText.getText();// テンポラリフォルダ

            // 部品識別名のリストを読み込む。
            String controlFile = testDir + File.separator + "control.csv";
            List<String> list = FileUtility.readTextFile(controlFile);
            List<List<String>> controlsList = new ArrayList();

            int targetCount = list.size() / handyCount;

            for (int i = 0; i < handyCount; i++) {
                int sp = i * targetCount;
                int ep = sp + targetCount;
                if (i == handyCount - 1) {
                    ep = list.size();
                }
                List<String> controls = list.subList(sp, ep);
                controlsList.add(controls);
            }

            // ハンディ端末
            int serial = 502000;

            int serial2 = serial;
            execMap.clear();
            for (int i = 0; i < handyCount; i++) {
                serial2++;
                execMap.put(String.valueOf(serial2), true);
            }

            for (int i = 0; i < handyCount; i++) {
                // TODO: 作業者
                String workerID = "admin";
                String workerName = "admin";

                serial++;
                String serialNumber = String.valueOf(serial);

                List<String> controls = new ArrayList();
                for (String control : controlsList.get(i)) {
                    controls.add(control);
                }

                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        blockUI(true);
                        try {
                            Platform.runLater(() -> {
                                label.setText("");
                            });
                            testStktake(workerID, workerName, serialNumber, controls);
                        } catch (Exception ex) {
                            logger.fatal(ex, ex);
                        } finally {
                            Platform.runLater(() -> {
                                execMap.replace(serialNumber, false);
                                if (!execMap.containsValue(true)) {
                                    label.setText("comp");
                                    blockUI(false);
                                }
                            });
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(false);
        String path = new File(".").getAbsoluteFile().getParent();// カレントパス
        this.testDir = path + File.separator + "temp";
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            operationPane.setDisable(flg);
            progressPane.setVisible(flg);
        });
    }

    /**
     * 結果リストをクリアする。
     */
    private void clearResult() {
        Platform.runLater(() -> {
            this.resultList.getItems().clear();
        });
    }

    /**
     * 結果リストにメッセージを追加する。
     * @param message 
     */
    private void addResult(String message) {
        Platform.runLater(() -> {
            this.resultList.getItems().add(message);
        });
    }

    private void testStktake(String workerID, String workerName, String serialNumber, List<String> controls) {
        try {
            this.addResult(String.format("start: serial=%s", serialNumber));

            int count = 0;
            for (String control : controls) {
                count++;
                long stim = System.currentTimeMillis();

                this.stktake(workerID, workerName, serialNumber, control);

                long etim = System.currentTimeMillis();
                long tim = etim - stim;
                logger.info("***** time={} ms, serial={}, count={}, control={}", tim, serialNumber, count, control);
                this.addResult(String.format("time=%d ms, serial=%s, count=%s, control=%s", tim, serialNumber, count, control));
                waitFor(100);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            this.addResult(String.format("end: serial=%s", serialNumber));
        }
    }

    /**
     * ハンディ端末で棚卸作業を行なった場合と同じ処理を行なう。
     *
     * @param serialNumber ハンディ端末のシリアル番号
     * @param control 保管箱QRコード (部品識別名)
     */
    private void stktake(String workerID, String workerName, String serialNumber, String control) {
        String affiliName = "所属 0 (テスト) ";
        String affiliCode = "00";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String workDateTime = sdf.format(new Date());
        String workDate = workDateTime.substring(2, 10);// 作業日付
        String workTime = workDateTime.substring(11, 16);// 作業時刻

        String ftpDir = testDir + File.separator + serialNumber;
        String datFile = ftpDir + File.separator + BhtCommand.BHT_STKTAKE_DAT;
        String mstFile = ftpDir + File.separator + BhtCommand.BHT_STKTAKE_MST;
        String resFile = ftpDir + File.separator + BhtCommand.BHT_STKTAKE_RES;
        String datFileBak = ftpDir + File.separator + BhtCommand.BHT_STKTAKE_DAT + "_";

        List<String> list = new ArrayList<>();

        // ファイル送受信で使用するフォルダを作成する。
        FileUtility.createFolder(ftpDir);

        // 棚卸DATを削除する。
        FileUtility.deleteFile(datFile);
        FileUtility.deleteFile(datFileBak);
        // 棚卸MSTを削除する。
        FileUtility.deleteFile(mstFile);
        // 結果RESを削除する。
        FileUtility.deleteFile(resFile);

        // 保管箱QRコードを記述した棚卸DATを送信する。
        list.clear();
        BhtStktake stktakeDat = new BhtStktake(control, "", "", "", "", "", "", "", "", "", "", "", "", "", "");

        String dat = BhtFileRecordConverter.BhtStktakeDecoder(stktakeDat);
        list.add(dat);

        if (!FileUtility.writeTextFile(datFile, list)) {
            logger.fatal("control file write error: {}", BhtCommand.BHT_STKTAKE_DAT);
            return;
        }
        int res1 = this.upload(serialNumber, BhtCommand.BHT_STKTAKE_DAT);
        if (res1 != 0) {
            return;
        }

        // 棚卸MSTを受信する。
        int res2 = this.download(serialNumber, BhtCommand.BHT_STKTAKE_MST);
        if (res2 != 0) {
            return;
        }

        list = FileUtility.readTextFile(mstFile);
        if (list.isEmpty()) {
            logger.fatal("file is empty: {}", BhtCommand.BHT_STKTAKE_MST);
            return;
        }
        BhtStktake stktakeMst = BhtFileRecordConverter.BhtStktakeEncoder(list.get(0));

        // 棚卸在庫数を入力する。
        BhtStktake stktakeAct = new BhtStktake();
        stktakeAct.setControl(stktakeMst.getControl());
        stktakeAct.setChartNo(stktakeMst.getChartNo());
        stktakeAct.setArticleName(stktakeMst.getArticleName());
        stktakeAct.setStandard(stktakeMst.getStandard());
        stktakeAct.setMaker(stktakeMst.getMaker());
        stktakeAct.setRackNo(stktakeMst.getRackNo());
        stktakeAct.setStockNum(stktakeMst.getStockNum());
        stktakeAct.setTruthNum(stktakeMst.getStockNum());
        stktakeAct.setWorkDate(workDate);
        stktakeAct.setWorkTime(workTime);
        stktakeAct.setWorkerID(workerID);
        stktakeAct.setWorkerName(workerName);
        if (stktakeMst.getAffiliName().isEmpty()) {
            stktakeAct.setAffiliName(affiliName);
        } else {
            stktakeAct.setAffiliName(stktakeMst.getAffiliName());
        }
        if (stktakeMst.getAffiliCode().isEmpty()) {
            stktakeAct.setAffiliCode(affiliCode);
        } else {
            stktakeAct.setAffiliCode(stktakeMst.getAffiliCode());
        }
        stktakeAct.setLabelNo(stktakeMst.getLabelNo());

        String act = BhtFileRecordConverter.BhtStktakeDecoder(stktakeAct);
        list.add(act);

        // 棚卸DATをリネームする。
        FileUtility.moveFile(datFile, datFileBak);

        // 棚卸実績を記述した棚卸DATを送信する。
        if (!FileUtility.writeTextFile(datFile, list)) {
            logger.fatal("actual file write error: {}", BhtCommand.BHT_STKTAKE_DAT);
            return;
        }
        int res3 = this.upload(serialNumber, BhtCommand.BHT_STKTAKE_DAT);
        if (res3 != 0) {
            return;
        }

        // 棚卸結果DATを受信する。
        int res4 = this.download(serialNumber, BhtCommand.BHT_STKTAKE_RES);
        if (res4 != 0) {
            return;
        }

        list = FileUtility.readTextFile(resFile);
        if (list.isEmpty()) {
            logger.fatal("file is empty: {}", BhtCommand.BHT_STKTAKE_RES);
            return;
        }
        BhtStktakeRes stktakeRes = BhtFileRecordConverter.BhtStktakeResEncoder(list.get(0));
        logger.info("*** stktake result (control: {}, labelNumber: {}", stktakeRes.getControl(), stktakeRes.getLabelNo());
    }

    /**
     * 
     * @param serialNumber ハンディ端末のシリアル番号
     * @param fileName ファイル名
     * @return 結果 (0:成功)
     */
    private int upload(String serialNumber, String fileName) {
        int ret = -1;
        WarehouseClientService service = null;
        WarehouseClientHandler client = null;
        try {
            service = new WarehouseClientService(remoteAddress, serialNumber, ipAddress);
            if (Objects.isNull(service)) {
                logger.warn("service is null.");
                return -2;
            }

            String dir = testDir + File.separator + serialNumber;

            service.startService();
            client = service.getClient();
            int tim = 0;
            while (tim < TIMEOUT) {
                if (client.isExistChannel()) {
                    logger.info("getClient tim: {}", tim);
                    int result = client.uploadFile(dir, fileName);
                    if (result == 0) {
                        ret = 0;
                    }
                    break;
                }
                tim += waitFor(10);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        try {
            if (Objects.nonNull(service)) {
                service.stopService();
                service = null;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 
     * @param serialNumber ハンディ端末のシリアル番号
     * @param fileName ファイル名
     * @return 結果 (0:成功)
     */
    private int download(String serialNumber, String fileName) {
        int ret = -1;
        WarehouseClientService service = null;
        WarehouseClientHandler client = null;
        try {
            service = new WarehouseClientService(remoteAddress, serialNumber, ipAddress);
            if (Objects.isNull(service)) {
                logger.warn("service is null.");
                return -2;
            }

            String dir = testDir + File.separator + serialNumber;

            service.startService();
            client = service.getClient();
            int tim = 0;
            while (tim < TIMEOUT) {
                if (client.isExistChannel()) {
                    logger.info("getClient tim: {}", tim);
                    int result = client.downloadFile(dir, fileName);
                    if (result == 0) {
                        ret = 0;
                    }
                    break;
                }
                tim += waitFor(10);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        try {
            if (Objects.nonNull(service)) {
                service.stopService();
                service = null;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
        return ret;
    }

    /**
     * 
     * @param milliseconds
     * @return
     * @throws InterruptedException 
     */
    private int waitFor(int milliseconds) throws InterruptedException {
        synchronized (this) {
            this.wait(milliseconds);
        }
        return milliseconds;
    }
}
