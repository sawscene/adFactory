/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.admanagerapp.kanbaneditplugin.net.HttpStorage;
import adtekfuji.admanagerapp.kanbaneditplugin.net.RemoteStorage;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.KanbanReportInfoFacede;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanReportInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import static jp.adtekfuji.adFactory.enumerate.ReportTypeEnum.KANBAN_REPORT;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 帳票一覧ダイアログ
 *
 * @author y-harada
 */
@FxComponent(id = "ReportListDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/report_list_dialog.fxml")
public class ReportListDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final Properties properties = AdProperty.getProperties();
    
    private static final int RANGE = 20;
    private static final String EXPORT_DIR = "reportListExportDir";
    private static final SimpleDateFormat SDFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");  
    private static final String NEW_LINE = "\r\n";
    
    private List<KanbanInfoEntity> kanbans;
    private Dialog dialog;

    private final ObservableList<DispReportEntity> list = FXCollections.observableArrayList();
    
    @FXML
    private StackPane anchorPane;
    @FXML
    private Label existLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<DispReportEntity> reportListTableView;
    @FXML
    private TableColumn<DispReportEntity, Boolean> checkColumn = new TableColumn<>("Selected");; // チェック
    @FXML
    private TableColumn<DispReportEntity, String> nameColumn; // 名称
    @FXML
    private TableColumn<DispReportEntity, String> kanbanNameColumn; // カンバン名
    @FXML
    private TableColumn<DispReportEntity, String> timeColumn; // 保存日時
    @FXML
    private Button exportButton;
    @FXML
    private Button openButton;
    
    @FXML
    private Pane Progress;
    

    /**
     * 帳票一覧表示用エンティティ
     *
     * @author y-harada
     */
    public class DispReportEntity {

        private final BooleanProperty selected = new SimpleBooleanProperty();
        private final StringProperty kanbanName = new SimpleStringProperty();
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty time = new SimpleStringProperty();
        private KanbanReportInfoEntity entity;

        /**
         * コンストラクタ
         * @param entity カンバン帳票情報エンティティ
         * @param sdFormat 日時の表示フォーマット
         */
        public DispReportEntity(KanbanReportInfoEntity entity, SimpleDateFormat sdFormat) {
            this.entity = entity;
            this.selected.setValue(Boolean.FALSE);
            this.kanbanName.setValue(entity.getKanbanName());
            String[] split = entity.getFilePath().split("/");
            String fileName = split[ split.length-1 ];
            this.name.setValue(fileName);
            this.time.setValue((sdFormat.format(entity.getOutputDate())));
        }
        public BooleanProperty selectedProperty() {return this.selected;}
        public StringProperty kanbanNameProperty() {return this.kanbanName;}
        public StringProperty nameProperty() {return name;}
        public StringProperty timeProperty() {return time;}
        public KanbanReportInfoEntity getEntity() {return entity;}
    }
    
    /**
     * コンストラクタ
     */
    public ReportListDialog() {

    }

    /**
     * 初期化
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        reportListTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.cleanupCacheDir();
        this.Progress.setVisible(false);
        
        this.reportListTableView.setEditable(true);
        // CheckBox
        CheckBox allCheckBox = new CheckBox();
        allCheckBox.selectedProperty().addListener(b -> allCheck(allCheckBox.isSelected()));
        this.checkColumn.setGraphic(allCheckBox);
        this.checkColumn.setCellValueFactory(f -> f.getValue().selectedProperty());
        this.checkColumn.setCellFactory(param -> {
            return new CheckBoxTableCell<>();
        });
        // 項目
        this.nameColumn.setCellValueFactory((TableColumn.CellDataFeatures<DispReportEntity, String> param) -> param.getValue().nameProperty());
        // カンバン名
        this.kanbanNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<DispReportEntity, String> param) -> param.getValue().kanbanNameProperty());
        // 保存日時
        this.timeColumn.setCellValueFactory((TableColumn.CellDataFeatures<DispReportEntity, String> param) -> param.getValue().timeProperty());
    }

    /**
     * 引数取得
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof List) {
            this.kanbans = (List<KanbanInfoEntity>) argument;
            
            blockUI(true);
            Platform.runLater(() -> {
                try {
                    // 画面更新
                    this.updateView();
                } catch(Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    blockUI(false);
                }

            });
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
     * 画面更新処理
     */
    private void updateView() {
        logger.info("updateView start");
        boolean isCancel = false;
        try {
            blockUI(true);
            ((CheckBox)this.checkColumn.getGraphic()).setSelected(false);
            this.reportListTableView.getItems().clear();
            this.reportListTableView.getSortOrder().clear();
            this.list.clear();
            
            final List<Long> ids = kanbans.stream().map(p -> p.getKanbanId()).collect(Collectors.toList());
            final List<KanbanReportInfoEntity> reports = this.getKanbanReports(ids);
            
            // 帳票一覧取得
            for (KanbanReportInfoEntity item : reports) {
                boolean isContain = false;
                for (DispReportEntity addedItem : this.list) {
                    if (addedItem.getEntity().getFilePath().equals(item.getFilePath())) {
                        isContain = true;
                        break;
                    }
                }
                
                if (!isContain) {
                    String kanbanName = kanbans.stream().filter(v -> v.getKanbanId().equals(item.getKanbanId())).map(v -> v.getKanbanName()).findFirst().orElse("");
                    kanbanName = item.getReportType().equals(KANBAN_REPORT) ? kanbanName : "";
                    item.setKanbanName(kanbanName);

                    DispReportEntity entity = new DispReportEntity(item, SDFORMAT);
                    entity.selectedProperty().addListener(b -> {
                        boolean isNotSelected
                                = this.reportListTableView
                                .getItems()
                                .stream()
                                .map(DispReportEntity::selectedProperty)
                                .noneMatch(BooleanExpression::getValue);
                        deleteButton.setDisable(isNotSelected || !LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.MAKED_KANBAN));
                        exportButton.setDisable(isNotSelected);
                        openButton.setDisable(isNotSelected);
                    });
                    this.list.add(entity);
                }
            }

            this.list.addAll();
            this.reportListTableView.setItems(this.list);
            
            this.existLabel.setText(String.format(LocaleUtils.getString("key.ReportListExistMessage"), this.list.size()));
            
            isCancel = true;
        
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
            logger.info("updateView end");
        }
    }
    
    /**
     * 削除ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onDeleteButton(ActionEvent event) {
        logger.info("onDeleteButton start");
        try {
            // チェックされた列を取得
            List<DispReportEntity> selectItems = this.getSelectedRow();
            if (selectItems.isEmpty()) {
              return;
            }

            // 削除
            for (DispReportEntity item : selectItems) {
                // サーバーに保存しているファイルを削除
                if (!this.delete(item.getEntity().getFilePath(), item.nameProperty().getValue())) {
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReportList"), String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.File")));
                    break;
                }
                // カンバン帳票情報を削除
                if (!this.deleteKanbanReport(item.getEntity().getKanbanReportId())){
                    sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReportList"), String.format(LocaleUtils.getString("key.FailedToDelete"), LocaleUtils.getString("key.File")));
                    break;
                }
            }
            // 画面更新
            this.updateView();
            
        } catch (Exception ex) {
            logger.info(ex, ex);
        } finally {
            logger.info("onDeleteButton end");
        }
    }
    
    /**
     *  削除
     * 
     * @param filePath 削除パス
     * @param fileName 削除ファイル名
     *
     */
    private boolean delete(String filePath, String fileName) {
        logger.info("delete start");
        
        StringBuilder sb = new StringBuilder();
        String[] split = filePath.split("/");
        for (int i = 1; i < split.length - 1 ; i++) {
            sb.append("/");
            sb.append(split[i]);
        }
        String serverPath = sb.toString();
        Map<String, String> transfers = new HashMap<>();
        Set<String> deleteFileName = new HashSet<>();
        deleteFileName.add(fileName);
        
        Stage stage = new Stage(StageStyle.UTILITY);
        try {
            Label deleteLabel = new Label();
            deleteLabel.setPrefWidth(300.0);
            deleteLabel.setText("deleteing...");

            ProgressBar progress = new ProgressBar();
            progress.setPrefWidth(300.0);
            progress.setVisible(true);

            VBox pane = new VBox();
            pane.setPadding(new Insets(16.0, 16.0, 24.0, 16.0));
            pane.setSpacing(8.0);
            pane.getChildren().addAll(deleteLabel, progress);

            stage.setTitle("adManagerApp");
            stage.setScene(new Scene(pane));
            stage.setAlwaysOnTop(true);
            stage.setOnCloseRequest(event -> {
                logger.info("Closing the window...");
                event.consume();
            });
            stage.show();

            // 帳票ファイルを削除
            URL url = new URL(ClientServiceProperty.getServerUri());
            RemoteStorage storage = new HttpStorage();
            storage.configuration(url.getHost(), null, null);

            Object task = storage.createUploader(serverPath, transfers, deleteFileName);

            ExecutorService executor = Executors.newSingleThreadExecutor();

            // 削除スレッドを実行して、終了するまで待機する
            Future<Boolean> future = executor.submit((Callable<Boolean>) task);  
            executor.shutdown();
            Boolean succeeded = future.get();

            return succeeded;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            logger.info("delete end ");
            stage.close();
        }
    }

    /**
     * 開くボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onOpenButton(ActionEvent event) {
        logger.info("onOpenButton start");
        // チェックされた列を取得
        List<DispReportEntity> selectItems = this.getSelectedRow();
        if (selectItems.isEmpty()) {
          return;
        }
        
        // ダウンロード
        List<File> files = new ArrayList<>();
        List<DispReportEntity> failedDownload = new ArrayList<>();
        for(DispReportEntity entity : selectItems) {
            File file = this.download(entity.getEntity().getFilePath(), false);
            if (Objects.nonNull(file)) {
                files.add(file);
            } else {
                failedDownload.add(entity);
            }
        }


        // 帳票を開く
        List<File> failedOpen = new ArrayList<>();
        for (File file : files) {
             try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } catch (IOException ex) {
                logger.fatal(ex, ex);
                failedOpen.add(file);
            }
        }
        
        StringBuilder sb = new StringBuilder();
        //ダウンロードに失敗
        if (!failedDownload.isEmpty()) {
            sb.append(String.format(LocaleUtils.getString("key.FailedDownloadCount"), failedDownload.size()));
            sb.append(NEW_LINE);
        }
        // 開くのに失敗
        if (!failedOpen.isEmpty()) {
            sb.append(String.format(LocaleUtils.getString("key.FailedFileOpenCount"), failedOpen.size()));
            sb.append(NEW_LINE);
        }
        if(sb.length() != 0){
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReportList"), sb.toString());
        }
    }    
    
    /**
     * エクスポートボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onExportButton(ActionEvent event) {
        logger.info("onExportButton start");
        try {
            final Path outputDir = showOutputDialog(event);
            properties.setProperty(EXPORT_DIR, outputDir.toString());
            
            // チェックされた列を取得
            List<DispReportEntity> selectItems = this.getSelectedRow();
            if (selectItems.isEmpty()) {
              return;
            }

            // ダウンロード
            List<File> files = new ArrayList<>();
            List<DispReportEntity> failedDownload = new ArrayList<>();
            for(DispReportEntity entity : selectItems) {
                File file = this.download(entity.getEntity().getFilePath(), outputDir.toString(), true);
                if (Objects.nonNull(file)) {
                    files.add(file);
                } else {
                    failedDownload.add(entity);
                }
            }

            //ダウンロードに失敗
            if (failedDownload.isEmpty()) {
                // 全て成功
                sc.showAlert(Alert.AlertType.INFORMATION, LocaleUtils.getString("key.ReportList"), LocaleUtils.getString("key.SuccessExport"));
            } else {
                // 失敗あり
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.ReportList"), String.format(LocaleUtils.getString("key.FailedDownloadCount"), failedDownload.size()));
            }

        } catch (Exception ex) {
            logger.fatal(ex,ex);
        } finally {
            logger.info("onExportButton end");
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
     * 全列チェック
     * 
     * @pram check True=チェック False=チェックを外す
     */
    private void allCheck(boolean check) {
        try {
            for (int i = 0; i < this.reportListTableView.getItems().size(); i++) {
                this.reportListTableView.getItems().get(i).selectedProperty().set(check);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * チェックされた列のデータを取得
     * 
     * @return 帳票一覧表示用エンティティのリスト
     */
    private List<DispReportEntity> getSelectedRow() {
        try {
            List<DispReportEntity> ret = new ArrayList<>();
            
            for (int i = 0; i < this.reportListTableView.getItems().size(); i++) {
                if (this.reportListTableView.getItems().get(i).selectedProperty().getValue()) {
                    ret.add(this.reportListTableView.getItems().get(i));
                }
            }
            return ret;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList<>();
        }
    }
    
    /**
     * キャッシュフォルダを削除
     * 
     */
    public void cleanupCacheDir() {
        try {
            this.deleteDir(Paths.CLIENT_CACHE_REPORT);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ファイルおよびディレクトリを削除します。
     * ディレクトリが指定された場合は、ディレクトリ内のファイルも強制的に削除されます。
     *
     * @param path フルパスを指定します。(C:サンプル)など
     * @return 削除件数
     */
    public static int deleteDir(String path) {
        File file = new File(path);
        // 存在チェック
        if ( !file.exists() ){
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
     * 帳票をダウンロードする。
     * 
     * @param downloadPath ダウンロード元パス
     * @param isExport true: 帳票をエクスポートする、false: 帳票をエクスポートしない
     * @return ダウンロードした帳票
     */
    public File download(String downloadPath, boolean isExport) {
        String cacheDir = Paths.CLIENT_CACHE_REPORT + File.separator;
        return this.download(downloadPath, cacheDir, isExport);
    }

    
    /**
     * 帳票をダウンロードする。
     * 
     * @param downloadPath ダウンロード元パス
     * @param outputPath 出力先
     * @param isExport true: 帳票をエクスポートする、false: 帳票をエクスポートしない
     * @return ダウンロードした帳票
     */
    public File download(String downloadPath, String outputPath, boolean isExport) {
        logger.info("download start");
        try {
            
            String[] split = downloadPath.split("/");
            String fileName = split[ split.length-1 ];

            File file;
            if (isExport) {
                file = fileExist(new File(outputPath + File.separator + fileName));
            } else {
                file = new File(outputPath + File.separator + fileName);
                if (file.exists()) {
                    return file;
                }
            }

            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                dir.setLastModified(System.currentTimeMillis());
            }

            StringBuilder sb = new StringBuilder();
            sb.append(Paths.SERVER_DOWNLOAD);
            for (int i = 1; i < split.length -1; i++) {
                sb.append("/");
                sb.append(split[i]);
            }
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20"); // ファイル名は漢字等が入る可能性があるためURLエンコードを施す
            sb.append("/");
            sb.append(encodedFileName);
                
            URL url = new URL(ClientServiceProperty.getServerUri());
            URI uri = new URI(url.getProtocol(), null, url.getHost(),  url.getPort(), null, null, null);

            RemoteStorage storage = new HttpStorage();
            storage.configuration(uri.toASCIIString(), null, null);

            storage.download(sb.toString(), file.getPath());
            return file;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        } finally {
            logger.info("download end");
        }
    }
    
    /**
     * 帳票出力先ディレクトリ選択ダイアログを表示する
     *
     * @param event イベント
     * @return 出力された帳票ファイルのパス
     */
    private Path showOutputDialog(ActionEvent event) {
        Node node = (Node) event.getSource();
        
        Path outputDir = java.nio.file.Paths.get(String.valueOf(properties.getProperty(EXPORT_DIR, System.getProperty("user.home") + File.separator + "Desktop")));

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(outputDir.toFile());

        return java.nio.file.Paths.get(dirChooser.showDialog(node.getScene().getWindow()).getAbsolutePath());
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
     * カンバンIDで帳票一覧を取得する
     *
     * @param kanbanId カンバンID
     * @return 帳票一覧
     */
    private List<KanbanReportInfoEntity> getKanbanReports(List<Long> kanbanIds) {
        logger.info("getKanbanReports start");
        try {
            List<KanbanReportInfoEntity> entitys = new ArrayList<>();
            KanbanReportInfoFacede facade = new KanbanReportInfoFacede();
            Long actualMax = facade.countByKanbanId(kanbanIds);
            for (int nowCnt = 0; nowCnt <= actualMax; nowCnt += RANGE) {
                entitys.addAll(facade.findByKanbanId(kanbanIds, nowCnt, nowCnt + RANGE - 1));
            }
            return entitys;
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return null;
        } finally {
            logger.info("getKanbanReports end");
        }
    }
    
    /**
     * カンバン帳票IDで帳票情報を削除する
     *
     * @param id 帳票
     * @return 帳票一覧
     */
    private boolean deleteKanbanReport(Long Id) {
        logger.info("deleteKanbanReport start");
        try {
            KanbanReportInfoFacede facade = new KanbanReportInfoFacede();
            ResponseEntity res = facade.delete(Id);
            if (Objects.nonNull(res) && res.isSuccess()) {
                // 成功
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            logger.info("deleteKanbanReport end");
        }
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
}
