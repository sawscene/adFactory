/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.admanagerapp.kanbaneditplugin.common.Constants;
import adtekfuji.admanagerapp.kanbaneditplugin.net.HttpStorage;
import adtekfuji.admanagerapp.kanbaneditplugin.net.RemoteStorage;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.KanbanReportInfoFacede;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanReportInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReportTypeEnum;
import jp.adtekfuji.javafxcommon.Config;

/**
 * 2019/12/06 作業完了以外の帳票出力対応 引数で指定したファイルを開く事が可能なダイアログ
 *
 */
public class FileOpenDialog extends Application {

    private final static Logger logger = LogManager.getLogger();
    private static final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    // 項目
    private AnchorPane root;
    private GridPane grid;
    private CheckBox fileServerSendCheck;
    private CheckBox fileOpenCheck;
    private Button okButton;

    // 設定値
    private final String title;
    private final String text;
    private final List<Path> filePathList;
    private final List<KanbanReportInfoEntity> reportInfos;

    /**
     * ファイルを開くことが可能なダイアログ
     *
     * @param title ダイアログのタイトル
     * @param text ダイアログに表示する文章
     * @param filePathList 開く対象のファイル
     * @param reportInfos カンバン帳票情報リスト
     */
    public FileOpenDialog(String title, String text, List<Path> filePathList, List<KanbanReportInfoEntity> reportInfos) {
        this.title = title;
        this.text = text;
        this.filePathList = filePathList;
        this.reportInfos = reportInfos;
    }

    /**
     *
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        // プロパティの取得
        Properties props = AdProperty.getProperties(Constants.PROPERTY_NAME);
        // シーンコンテナの取得
        SceneProperties sp = SceneContiner.getInstance().getSceneProperties();

        // ダイアログ上に表示する項目の設定
        this.root = new AnchorPane();

        this.grid = new GridPane();
        this.grid.setPadding(new Insets(16, 12, 12, 16));
        this.grid.setMinWidth(360.0);
        this.grid.setMaxWidth(800.0);
        this.grid.setHgap(8.0);
        this.grid.setVgap(8.0);

        ColumnConstraints col1 = new ColumnConstraints(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Priority.SOMETIMES, HPos.LEFT, true);
        ColumnConstraints col2 = new ColumnConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_PREF_SIZE, Priority.SOMETIMES, HPos.LEFT, true);
        this.grid.getColumnConstraints().clear();
        this.grid.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Priority.SOMETIMES, VPos.TOP, true);
        RowConstraints row2 = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_PREF_SIZE, Priority.SOMETIMES, VPos.CENTER, true);
        RowConstraints row3 = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_PREF_SIZE, Priority.SOMETIMES, VPos.CENTER, true);
        this.grid.getRowConstraints().clear();
        this.grid.getRowConstraints().addAll(row1, row2, row3);

        // テキスト
        Label label = new Label(this.text);

        // 画像
        Image image = new Image("/org/controlsfx/dialog/dialog-information.png");
        ImageView iconImage = new ImageView();
        iconImage.setImage(image);
        iconImage.setVisible(true);
        
        // 帳票をサーバーに保存するチェックボックス
        this.fileServerSendCheck = new CheckBox(LocaleUtils.getString("key.SaveToServer"));
        this.fileServerSendCheck.setSelected(Boolean.valueOf(props.getProperty(Config.SEND_EXCEL_FILE, "true")));

        // 帳票を開くチェックボックス
        this.fileOpenCheck = new CheckBox(LocaleUtils.getString("key.OpenReportFile"));
        this.fileOpenCheck.setSelected(Boolean.valueOf(props.getProperty(Config.OPEN_EXCEL_FILE, "true")));

        // 下段
        HBox bottonBox = new HBox();
        bottonBox.setAlignment(Pos.CENTER_RIGHT);

        // OKボタン
        this.okButton = new Button(LocaleUtils.getString("key.Ok"));
        this.okButton.setPrefSize(75, 20);

        // OKボタンイベント処理
        this.okButton.setOnAction((ActionEvent) -> {
            boolean isFailed = false;
            if (this.fileServerSendCheck.isSelected()) {
                // チェックがついている場合は帳票をサーバーにFTPで保存
                List<String> failedUpload = new ArrayList<>();
                for (Path filePath : this.filePathList) {
                    // FTP送信
                    if(!this.uploadOutputReport(filePath)) {
                        logger.fatal("Upload Failed");
                        isFailed = true;
                        failedUpload.add(filePath.getFileName().toString());
                    }
                }
                for (KanbanReportInfoEntity info : this.reportInfos) {
                    if(failedUpload.contains(info.getFileName())){
                        // 失敗していたらカンバン帳票情報も登録しない
                        continue;
                    }
                    // カンバン帳票情報登録
                    KanbanReportInfoFacede facade = new KanbanReportInfoFacede();
                    ResponseEntity res = facade.regist(info);
                    if (!res.isSuccess()) {
                        isFailed = true;
                    }
                }
            }
            if (this.fileOpenCheck.isSelected()) {
                // チェックがついている場合は帳票を開く
                try {
                    for (Path filePath : this.filePathList) {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(filePath.toFile());
                    }
                } catch (IOException ex) {
                    logger.error(ex.getStackTrace());
                    isFailed = true;
                }
            }
            if (isFailed) {
                // エラーメッセージを表示する
                SceneContiner sc = SceneContiner.getInstance();
                sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.KanbanOutLedger"), LocaleUtils.getString("key.alert.systemError"));
            }
            
            // チェックボックスの設定値を保持する
            props.setProperty(Config.SEND_EXCEL_FILE, String.valueOf(fileServerSendCheck.isSelected()));
            props.setProperty(Config.OPEN_EXCEL_FILE, String.valueOf(fileOpenCheck.isSelected()));

            // ダイアログを閉じる
            root.getScene().getWindow().hide();
        });

        bottonBox.getChildren().addAll(this.okButton);

        this.grid.add(label, 0, 0);
        this.grid.add(iconImage, 1, 0, 1, 2);
        this.grid.add(this.fileServerSendCheck, 0, 1, 2, 1);
        this.grid.add(this.fileOpenCheck, 0, 2, 2, 1);
        this.grid.add(bottonBox, 0, 3, 2, 1);

        this.root.getChildren().addAll(this.grid);

        AnchorPane.setTopAnchor(this.grid, 0.0);
        AnchorPane.setBottomAnchor(this.grid, 0.0);
        AnchorPane.setLeftAnchor(this.grid, 0.0);
        AnchorPane.setRightAnchor(this.grid, 0.0);

        // ダイアログを開く
        // ダイアログの幅を可変
        Scene scene = new Scene(this.root);
        scene.getStylesheets().addAll(sp.getCsspathes());
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.setTitle(this.title);
        stage.showAndWait();
    }
    
    /**
     * 帳票ファイルをFTPでアップロードする
     * 
     * @param path パス
     */
    private boolean uploadOutputReport(Path path) {
        // アップロードするファイルを抽出
        Map<String, String> transfers = new HashMap<>();
        Set<String> deletes = new HashSet<>();
        transfers.put(path.getFileName().toString(), path.toString());

        Stage stage = new Stage(StageStyle.UTILITY);

        try {
            Label updateLabel = new Label();
            updateLabel.setPrefWidth(300.0);
            updateLabel.setText(LocaleUtils.getString("key.UploadDocuments"));

            ProgressBar progress = new ProgressBar();
            progress.setPrefWidth(300.0);
            progress.setVisible(true);

            VBox pane = new VBox();
            pane.setPadding(new Insets(16.0, 16.0, 24.0, 16.0));
            pane.setSpacing(8.0);
            pane.getChildren().addAll(updateLabel, progress);

            stage.setTitle("adManagerApp");
            stage.setScene(new Scene(pane));
            stage.setAlwaysOnTop(true);
            stage.setOnCloseRequest(event -> {
                logger.info("Closing the window...");
                event.consume();
            });
            stage.show();

            // 帳票ファイルをアップロード
            URL url = new URL(ClientServiceProperty.getServerUri());
            RemoteStorage storage = new HttpStorage();
            storage.configuration(url.getHost(), null, null);

            KanbanReportInfoEntity info = null;
            for (KanbanReportInfoEntity entity : this.reportInfos) {
                if(entity.getFilePath().equals(path.toString())){
                    info = entity;
                    break;
                }
            }

            String id = Objects.nonNull(info) && info.getReportType().equals(ReportTypeEnum.KANBAN_REPORT) ? String.valueOf(info.getKanbanId()) : "report";
            String yyyyMM =  new SimpleDateFormat("yyyyMM").format(new Date());
            String serverPath = Paths.SERVER_UPLOAD_REPORT + "/" + yyyyMM +  "/" + id;

            StringBuilder sb = new StringBuilder();
            sb.append(Paths.SERVER_UPLOAD_REPORT);
            sb.append("/");
            sb.append(yyyyMM);
            sb.append("/");
            sb.append(id);
            sb.append("/");
            sb.append(path.getFileName().toString());

            // ファイルパスを送り先に書き換え
            this.reportInfos.stream().filter(v -> v.getFilePath().equals(path.toString())).forEach(v -> v.setFilePath(sb.toString()));

            Object task = storage.createUploader(serverPath, transfers, deletes);

            ExecutorService executor = Executors.newSingleThreadExecutor();

            // アップロードスレッドを実行
            Future<Boolean> future = executor.submit((Callable<Boolean>) task);  
            executor.shutdown();
            Boolean succeeded = future.get();
            logger.info("delete end: " + succeeded);
            return succeeded;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return false;
        } finally {
            stage.close();
        }

    }
}
