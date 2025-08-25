/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.admanagerapp.kanbaneditplugin.common.*;
import adtekfuji.admanagerapp.kanbaneditplugin.enumerate.LedgerProcResultType;
import adtekfuji.clientservice.ActualResultInfoFacade;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DataValidator;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.adFactory.utility.PropertyUtils;

/**
 * QRコード印刷ダイアログ
 *
 * @author y-harada
 */
@FxComponent(id = "QRCodePrintDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/qrcode_print_dialog.fxml")
public class QRCodePrintDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    private static final String OUTPUT_DIR = adtekfuji.clientservice.common.Paths.CLIENT_CACHE_REPORT + File.separator + "qrcode";
    private static final String vbScriptPath = System.getenv("ADFACTORY_HOME") + File.separator + "bin" + File.separator + "print_excel.vbs";

    private KanbanInfoEntity kanbanEntity;
    private Dialog dialog;

    private String printFilePath;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox printNumComboBox;
    @FXML
    private Pane Progress;

    /**
     * コンストラクタ
     */
    public QRCodePrintDialog() {

    }

    /**
     * 初期化
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final ObservableList<Integer> list = FXCollections.observableArrayList(IntStream.rangeClosed(1, 10).boxed().collect(toList()));
        this.printNumComboBox.setItems(list);
        this.printNumComboBox.setValue(list.get(0));

        this.printNumComboBox.getEditor().textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            TextField editor = this.printNumComboBox.getEditor();
            String text = editor.getText();
            if (text.length() > 3) {
                editor.setText(oldValue);
            } else if (!DataValidator.isValid(text, DataValidator.MATCH_NUMBER, false)) {
                editor.setText(oldValue);
                this.printNumComboBox.requestFocus();
            }
        });

        this.Progress.setVisible(false);
    }

    /**
     * 引数取得
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof KanbanInfoEntity) {
            this.kanbanEntity = (KanbanInfoEntity) argument;
        }
    }

    /**
     * ダイアログ設定
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }
    
    /**
     * 印刷ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onPrintButton(ActionEvent event) {
        logger.info("onPrintButton start");
        boolean isCancel = true;
        try {
            blockUI(true);

            try {
               int num = Integer.parseInt((String)this.printNumComboBox.getValue());
               if (num == 0) {
                   return;
               }
            } catch (NumberFormatException ex){
                // 変換できない場合エラー
                logger.fatal(ex);
                return;
            }

            this.cleanupCacheDir();

            File dir = new File(OUTPUT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                dir.setLastModified(System.currentTimeMillis());
            }

            boolean reportLeaveTags = false; // 置換されなかったタグを残す

            final Path outputDir = Paths.get(OUTPUT_DIR);
            final boolean isRemoveTag = !reportLeaveTags;

            List<Long> kanbanIds = new ArrayList();
            kanbanIds.add(kanbanEntity.getKanbanId());

            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    createLedgerThread(outputDir, kanbanIds, isRemoveTag);
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        if (Objects.nonNull(printFilePath) && !printFilePath.isEmpty()) {
                            print(printFilePath);
                        }
                        cancelDialog();
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    try {
                        if (Objects.nonNull(this.getException())) {
                            logger.fatal(this.getException(), this.getException());
                        }
                        // エラー
                        sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QRCodePrint"), LocaleUtils.getString("key.alert.systemError"));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                    }
                }
            };
            new Thread(task).start();

            isCancel = false;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
            logger.info("onPrintButtonend");
        }
    }

    /**
     * 印刷
     *
     * @param filePath 印刷するファイルのパス
     */
    private void print(String filePath) {
        try {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int num = Integer.parseInt((String)printNumComboBox.getValue());
                    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", vbScriptPath, filePath, String.valueOf(num));
                    Process process = pb.start();
                    process.waitFor(15, TimeUnit.SECONDS);
                    return null;
                }
            };
            new Thread(task).start();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("print");
        }
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onCancelButton(ActionEvent event) {
        this.cancelDialog();
    }

    /**
     * キャンセル処理
     */
    private void cancelDialog() {
        try {
            this.dialog.setResult(ButtonType.CANCEL);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * キャッシュフォルダを削除
     * 
     */
    public void cleanupCacheDir() {
        try {
            deleteDir(OUTPUT_DIR);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ファイルおよびディレクトリを削除します。<br/>
     * ディレクトリが指定された場合は、ディレクトリ内のファイルも強制的に削除されます。
     *
     * @param path フルパスを指定します。(C:サンプル)など
     * @return 削除件数
     */
    public static int deleteDir(String path) {
        File file = new File(path);
        // 存在チェック
        if (!file.exists()){
            return 0;
        }

        // ファイルの場合
        if (file.isFile()) {
            file.delete();
            return 1;
        }
        
        int counter = 0;
        // ディレクトリの場合
        if( file.isDirectory() ) {
            // 一覧を取得
            File[] files = file.listFiles();
            for (File target : files) {
                deleteDir(target.getPath());
            }
            // ディレクトリ削除
            file.delete();
            counter++;
        }
        return counter;
    }

    /**
     * UIロック
     *
     * @param flg True＝ロック
     */
    private void blockUI(Boolean flg) {
        this.anchorPane.setDisable(flg);
        Progress.setVisible(flg);
    }

    /**
     * ファイルが存在するか確認し、ある場合はファイル名に_をつける
     *
     * @param file ファイル
     * @return ファイル
     */
    private File fileExist(File file) {
        try {
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                sb.append(file.getParent());
                sb.append(File.separator);
                sb.append("_");
                sb.append(file.getName());
                
                File addFile = new File(sb.toString());
                file = this.fileExist(addFile);
            }
            return file;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * 帳票出力スレッド
     *
     * @param outputDir 出力先ディレクトリパス
     * @param kanbanIds カンバンID一覧
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @throws Exception
     */
    private void createLedgerThread(Path outputDir, List<Long> kanbanIds, boolean isRemoveTag) throws Exception {
        boolean isFailed = false;

        // カンバンID一覧を指定して、カンバン情報一覧を取得する。
        List<KanbanInfoEntity> kanbans = this.getKanbans(kanbanIds);
        if (Objects.isNull(kanbans) || kanbans.isEmpty()) {
            isFailed = true;
        }

        // カンバンID一覧を指定して、工程実績情報一覧を取得する。
        List<ActualResultEntity> actuals = this.getActualResults(kanbanIds);
        final Comparator<ActualResultEntity> dateComparator = (p1, p2) -> p1.getImplementDatetime().compareTo(p2.getImplementDatetime());
        final Comparator<ActualResultEntity> actualIDComparator = (p1, p2) -> p1.getActualId().compareTo(p2.getActualId());

        for (KanbanInfoEntity kanban : kanbans) {
            List<ActualResultEntity> kanbanActuals = actuals.stream()
                    .filter(p -> Objects.equals(p.getFkKanbanId(), kanban.getKanbanId()))
                    .sorted(dateComparator.thenComparing(actualIDComparator))
                    .collect(Collectors.toList());

            kanban.setActualResultCollection(kanbanActuals);

            LedgerProcResultType result = createLedger(kanban, outputDir, isRemoveTag);
            switch (result) {
                case SUCCESS:
                case SUCCESS_INCOMPLETE:
                    break;
                default:
                    isFailed = true;
                    break;
            }
        }

        if (isFailed) {
            // エラー
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.QRCodePrint"), LocaleUtils.getString("key.alert.systemError"));
        }
    }

    /**
     * 帳票出力
     *
     * @param kanban カンバン情報
     * @param outputDir 出力先ディレクトリパス
     * @param isRemoveTag 未変換のタグを削除する？(true:する, false:しない)
     * @return
     */
    private LedgerProcResultType createLedger(KanbanInfoEntity kanban, Path outputDir, boolean isRemoveTag) {
        LedgerProcResultType ret = LedgerProcResultType.FAILD_OTHER;

        boolean isOutputFailed = false;
        try {
            // 帳票テンプレートファイルパス
            List<SimpleStringProperty> pathProps = PropertyUtils.stringToPropertyList(kanban.getLedgerPath(), "\\|");
            SimpleStringProperty pathProp = pathProps.stream().filter(v -> v.getValue().contains("qrcode_template")).findFirst().orElse(null);
            if (Objects.isNull(pathProp) || pathProp.get().isEmpty()) {
                // 未登録
                return LedgerProcResultType.TEMPLATE_UNREGISTERED;
            }

            // 帳票データ取得処理
            KanbanLedgerPermanenceData ledgerData = new KanbanLedgerPermanenceData();
            ledgerData.setLedgerFilePass(kanban.getLedgerPath());
            ledgerData.setKanbanInfoEntity(kanban);

            // 工程カンバンをセットする。
            ledgerData.setWorkKanbanInfoEntities(kanban.getWorkKanbanCollection());
            if (Objects.isNull(ledgerData.getWorkKanbanInfoEntities())) {
                // 工程カンバンなし
                return LedgerProcResultType.GET_INFO_FAILURED;
            }

            // 追加工程カンバンをセットする。
            ledgerData.setSeparateworkWorkKanbanInfoEntities(kanban.getSeparateworkKanbanCollection());
            if (Objects.isNull(ledgerData.getSeparateworkWorkKanbanInfoEntities())) {
                // 工程カンバンなし
                return LedgerProcResultType.GET_INFO_FAILURED;
            }

            // 実績をセットする。
            ledgerData.setActualResultInfoEntities(kanban.getActualResultCollection());
            if (Objects.isNull(ledgerData.getActualResultInfoEntities())) {
                // 実績なし
                return LedgerProcResultType.GET_INFO_FAILURED;
            }

            // 拡張タグ／部品トレースは使用しない
            ledgerData.setUseExtensionTag(false);
            ledgerData.setEnablePartsTrace(false);
            // QRコードを使用する
            ledgerData.setUseQRCodeTag(true);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now = new Date();
            String dateString = df.format(now);

            // 帳票テンプレートごとに帳票ファイルを出力する。
            File templateFile = new File(pathProp.getValue());
            String templateName = templateFile.getName().substring(0, templateFile.getName().lastIndexOf('.'));

            StringBuilder filename = new StringBuilder()
                    .append(ledgerData.getKanbanInfoEntity().getKanbanName())
                    .append("_")
                    .append(ledgerData.getKanbanInfoEntity().getWorkflowName())
                    .append("_")
                    .append(dateString)
                    .append("(")
                    .append(templateName)
                    .append(")");

            String ledgerFileName = filename.toString();

            LedgerSheetFactory ledgerFactory = new LedgerSheetFactory();

            Path inFilePath = Paths.get(templateFile.getPath());
            if (!Files.exists(inFilePath)) {
                // 帳票テンプレートファイルがない
                isOutputFailed = true;
                ret = LedgerProcResultType.TEMPLATE_NONE;
                return ret;
            }

            String inFileExt = templateFile.toString().substring(templateFile.toString().lastIndexOf("."));
            Path outputFile = Paths.get(outputDir.toString(), ledgerFileName + inFileExt);
            for (int cnt = 2; Files.exists(outputFile); cnt++) {
                outputFile = Paths.get(outputDir.toString(), ledgerFileName + "_" + cnt + inFileExt);
            }

            String workbookName = "workbook";

            // 帳票テンプレートを読み込む。
            ledgerFactory.mergeTemplateWorkbook(workbookName, inFilePath.toFile());

            final LedgerTagCase ledgerTagCase
                    = StringUtils.equals(AdProperty.getProperties().getProperty(LedgerTagCase.name, InsensitiveCaseLedgerTag.name), NaturalCaseLedgerTag.name)
                    ? NaturalCaseLedgerTag.instance
                    : InsensitiveCaseLedgerTag.instance;


            // タグを置換する。
            List<String> faildReplaceTags = ledgerFactory.replaceTags(workbookName, ledgerData, 1, false, isRemoveTag, ledgerTagCase);

            // ワークブックを保存する。
            if (ledgerFactory.saveWorkbook(workbookName, outputFile.toFile())) {
                if (!faildReplaceTags.isEmpty()) {
                    if (!isOutputFailed) {
                        isOutputFailed = true;
                        ret = LedgerProcResultType.SUCCESS_INCOMPLETE;
                    }
                }
            } else {
                isOutputFailed = true;
            }

            printFilePath = outputFile.toAbsolutePath().toString();

            if (!isOutputFailed) {
                ret = LedgerProcResultType.SUCCESS;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            ret = LedgerProcResultType.ERROR_OCCURED;
        }
        return ret;
    }

    /**
     * カンバンID一覧を指定して、カンバン情報一覧(詳細情報付き)を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return カンバン情報一覧
     */
    private List<KanbanInfoEntity> getKanbans(List<Long> kanbanIds) {
        try {
            int KANBAN_RANGE = 20;
            KanbanInfoFacade kanbanInfoFacade = new KanbanInfoFacade();
            List<KanbanInfoEntity> kanbans = new LinkedList();

            for (int rangeFrom = 0; rangeFrom < kanbanIds.size(); rangeFrom += KANBAN_RANGE) {
                int rangeTo = rangeFrom + KANBAN_RANGE;
                if (rangeTo > kanbanIds.size()) {
                    rangeTo = kanbanIds.size();
                }

                List<Long> rangeIds = kanbanIds.subList(rangeFrom, rangeTo);
                kanbans.addAll(kanbanInfoFacade.find(rangeIds, true));
            }

            return kanbans;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }

    /**
     * カンバンID一覧を指定して、工程実績情報一覧を取得する。
     *
     * @param kanbanIds カンバンID一覧
     * @return 工程実績情報一覧
     */
    private List<ActualResultEntity> getActualResults(List<Long> kanbanIds) {
        try {
            int ACTUAL_KANBAN_RANGE = 10;
            List<ActualResultEntity> actuals = new ArrayList<>();
            ActualResultInfoFacade facade = new ActualResultInfoFacade();

            for (int rangeFrom = 0; rangeFrom < kanbanIds.size(); rangeFrom += ACTUAL_KANBAN_RANGE) {
                int rangeTo = rangeFrom + ACTUAL_KANBAN_RANGE;
                if (rangeTo > kanbanIds.size()) {
                    rangeTo = kanbanIds.size();
                }

                List<Long> rangeIds = kanbanIds.subList(rangeFrom, rangeTo);
                actuals.addAll(facade.find(rangeIds, true));
            }

            return actuals;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        }
    }
}
