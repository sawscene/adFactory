/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.component;

import adtekfuji.admanagerapp.workfloweditplugin.common.TraceabilityRecordFactory;
import adtekfuji.admanagerapp.workfloweditplugin.model.PdfModel;
import adtekfuji.admanagerapp.workfloweditplugin.net.HttpStorage;
import adtekfuji.admanagerapp.workfloweditplugin.net.RemoteStorage;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.clientservice.common.Paths;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.DateUtils;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.entity.object.ObjectInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkPropertyInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkSectionInfoEntity;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工程セクション設定ペインのコントローラー
 *
 * @author s-heya
 */
public class WorkSectionPane extends AnchorPane implements Initializable {

    private static final Long minRatio = 10L;
    private static final Logger logger = LogManager.getLogger();
    private static final SceneContiner sc = SceneContiner.getInstance();
    private static File defaultDir = new File(System.getProperty("user.home"), "Desktop");
    private static final double width;
    private static final double height;

    private ObservableList<ObjectInfoEntity> useParts;
    private List<EquipmentInfoEntity> manufactureEquipments;
    private List<EquipmentInfoEntity> measureEquipments;
    private PdfModel pdfModel;
    private boolean loaded;
    private MediaView mediaView;

    /**
     * 入力項目の有効/無効状態(true：無効、false：有効)
     */
    private final boolean isItemDisabled;

    private final WorkSectionInfoEntity workSection;
    private WorkInfoEntity workInfo;
    private Table traceabilityTable;
         
    @FXML
    private VBox rootPane;
    @FXML
    private VBox documentPane;
    @FXML
    private TextField fileNameField;
    @FXML
    private Label updatedDateLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private Pagination pagination;
    @FXML
    private AnchorPane mediaPane;
    @FXML
    private VBox traceabilityPane;

    /**
     * 選択ボタン
     */
    @FXML
    private Button choiceButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label sliderLabel;
    @FXML
    private Slider slider;
    @FXML
    private StackPane boardPane;

    /**
     * ページ分割
     */
    @FXML
    private CheckBox pageBreakCheckBox;

    ChangeListener<Number> listener = null;


    static {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(ClientServiceProperty.PDOC_WIDTH)) {
            properties.setProperty(ClientServiceProperty.PDOC_WIDTH, "1024.0");
        }
        width = Double.parseDouble(properties.getProperty(ClientServiceProperty.PDOC_WIDTH));

        if (!properties.containsKey(ClientServiceProperty.PDOC_HEIGHT)) {
            properties.setProperty(ClientServiceProperty.PDOC_HEIGHT, "600.0");
        }
        height = Double.parseDouble(properties.getProperty(ClientServiceProperty.PDOC_HEIGHT));
    }

    /**
     * コンストラクタ
     *
     * @param workSection 工程セクション情報
     * @param isItemDisabled 入力項目の有効/無効状態(true：無効、false：有効)
     */
    private WorkSectionPane(WorkSectionInfoEntity workSection, boolean isItemDisabled) {
        this.workSection = workSection;
        this.isItemDisabled = isItemDisabled;
    }

    public void reflesh()
    {
        Properties properties = AdProperty.getProperties();
        if (!properties.containsKey(ClientServiceProperty.PDOC_ZOOM_RATIO)) {
            properties.setProperty(ClientServiceProperty.PDOC_ZOOM_RATIO, "50");
        }
        double zoomRatio = Double.parseDouble(properties.getProperty(ClientServiceProperty.PDOC_ZOOM_RATIO));
        this.slider.setValue(zoomRatio); // デフォルト
    }

    /**
     * WorkSectionPaneを生成する。
     *
     * @param workSection 工程セクション情報
     * @param useParts モノ情報一覧
     * @param manufactureEquipments 製造設備一覧
     * @param measureEquipments 測定機器一覧
     * @param rb リソースバンドル
     * @param isItemDisabled 入力項目の有効/無効状態(true：無効、false：有効)
     * @return 工程セクション設定ペインのコントローラー
     */
    public static WorkSectionPane newInstance(WorkSectionInfoEntity workSection, ObservableList<ObjectInfoEntity> useParts, List<EquipmentInfoEntity> manufactureEquipments, List<EquipmentInfoEntity> measureEquipments, ResourceBundle rb, boolean isItemDisabled) {
        WorkSectionPane pane = newInstance(workSection, useParts, manufactureEquipments, measureEquipments, rb, isItemDisabled, null);

        return pane;
    }

    /**
     * WorkSectionPaneを生成する。(Overloading)
     *
     * @param workSection 工程セクション情報
     * @param useParts モノ情報一覧
     * @param manufactureEquipments 製造設備一覧
     * @param measureEquipments 測定機器一覧
     * @param rb リソースバンドル
     * @param isItemDisabled 入力項目の有効/無効状態(true：無効、false：有効)
     * @param workInfo 工程情報
     * @return 工程セクション設定ペインのコントローラー
     */
    public static WorkSectionPane newInstance(WorkSectionInfoEntity workSection, ObservableList<ObjectInfoEntity> useParts, List<EquipmentInfoEntity> manufactureEquipments, List<EquipmentInfoEntity> measureEquipments, ResourceBundle rb, boolean isItemDisabled, WorkInfoEntity workInfo) {
        WorkSectionPane pane = null;

        try {
            pane = new WorkSectionPane(workSection, isItemDisabled);
            pane.setUseParts(useParts);
            pane.setManufactureEquipments(manufactureEquipments);
            pane.setMeasureEquipments(measureEquipments);
            pane.getStylesheets().addAll(sc.getSceneProperties().getCsspathes());
            pane.workInfo = workInfo;

            FXMLLoader loader = new FXMLLoader(pane.getClass().getResource("/fxml/admanagerworkfloweditplugin/work_section_pane.fxml"), rb);
            loader.setController(pane);
            Parent root = loader.load();
            pane.getChildren().add(root);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return pane;
    }

    /**
     * WorkSectionPaneを初期化する。
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (!ClientServiceProperty.isLicensed("@DocumentViewer")) {
            // ドキュメント設定を非表示
            this.rootPane.getChildren().remove(this.documentPane);
        }

        // ページ番号
        this.pagination.currentPageIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.workSection.setPageNum(newValue.intValue());
        });

        // ドキュメント名
        if (this.workSection.hasDocument()) {
            this.fileNameField.setText(this.workSection.getFileName());
            this.updatedDateLabel.setText(DateUtils.format(this.workSection.getFileUpdated()));
            this.clearButton.setDisable(this.isItemDisabled);
            this.slider.setDisable(this.isItemDisabled);
        } else {
            this.clearButton.setDisable(true);
            this.slider.setDisable(true);
        }
        this.choiceButton.setDisable(this.isItemDisabled);

        // トレーサビリティ設定
        if (!ClientServiceProperty.isLicensed("@Traceability")) {
            // トレーサビリティ設定を非表示
            this.rootPane.getChildren().remove(this.traceabilityPane);
        } else {
            this.workSection.getTraceabilityCollection().sort(Comparator.comparing(o -> o.getWorkPropOrder()));

            // ページ分割チェックボックス
            this.pageBreakCheckBox = new CheckBox(LocaleUtils.getString("key.pageBreak"));
            this.pageBreakCheckBox.getStyleClass().add("ContentTitleLabel");

            // トレーサビリティの項目数が2未満の場合、ページ分割チェックボックスを無効化する。
            ObservableList<WorkPropertyInfoEntity> traceabilityList = FXCollections.observableList(this.workSection.getTraceabilityCollection());
            this.pageBreakCheckBox.setDisable(traceabilityList.size() < 2);

            if (!this.workSection.getTraceabilityCollection().isEmpty()) {
                WorkPropertyInfoEntity firstProperty = this.workSection.getTraceabilityCollection().get(0);

                this.pageBreakCheckBox.setSelected(Boolean.TRUE.equals(firstProperty.getPageBreakEnabled()));

                // チェックボックスの変更を監視し、セクション内の全ての WorkPropertyInfoEntity を更新
                this.pageBreakCheckBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                    this.workSection.getTraceabilityCollection().forEach(property -> property.setPageBreakEnabled(newValue))
                );
            }

            // チェックボックスを traceabilityPane の一番上に追加
            this.traceabilityPane.getChildren().add(0, this.pageBreakCheckBox);

            this.traceabilityTable = new Table(this.traceabilityPane.getChildren());
            this.traceabilityTable.isAddRecord(!this.isItemDisabled);
            this.traceabilityTable.isColumnTitleRecord(true);
            this.traceabilityTable.title(LocaleUtils.getString("key.TraceabilitySettings"));
            this.traceabilityTable.styleClass("ContentTitleLabel");

            // テーブルの行数が変更されたときにページ分割チェックボックスの状態を更新する。
            this.traceabilityTable.addRecordListener(event -> {
                updatePageBreakCheckbox(this.traceabilityTable.getRowCount() -1);
            });

            //工程情報workInfoパラメータをセット
            this.traceabilityTable.setAbstractRecordFactory(new TraceabilityRecordFactory(traceabilityTable, workSection.getTraceabilityCollection(), this.useParts, this.manufactureEquipments, this.measureEquipments, this.isItemDisabled, this.workInfo));
        }

        StringConverter<Double> formatter = new StringConverter<Double>() {
            @Override
            public String toString(Double val) {
                return String.format("%.0f%%", val);
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string);
            }
        };
        
        final String path = this.getClass().getResource("/image/btnZoomOFF.PNG").toExternalForm();
        this.sliderLabel.setStyle("-fx-background-image: url('" + path + "');");
        this.slider.setMin(0); // 最小値
        this.slider.setMax(100); // 最大値
        this.slider.setLabelFormatter(formatter);
        this.slider.setMinorTickCount(0);
        this.slider.setMajorTickUnit(50); // 大メモリの間隔
        this.slider.setShowTickMarks(true); // メモリ線表示
        this.slider.setShowTickLabels(true);
        reflesh();
    }

    /**
     * ファイルを選択する。
     *
     * @param event
     */
    @FXML
    private void onChoice(ActionEvent event) {
        try {
            logger.info("onChoice start.");

            FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.DocumentFile"), "*.pdf", "*.bmp", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.png", "*.mp4", "*.flv", "*.wav");
            //FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.DocumentFile"), "*.pdf", "*.bmp", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.tif", "*.tiff", "*.png");
            //FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.ImageFile"), "*.bmp", "*.jpg", "*.jpeg", "*.jpe", "*.gif", "*.tif", "*.tiff", "*.png");
            //FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter(LocaleUtils.getString("key.MediaFile"), "*.aif", "*.aiff", "*.fxm", "*.flv", "*.mp3", "*.mp4", "*.m4a", "*.m4v", "*.wav", "*.m3u8");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(defaultDir);
            fileChooser.setTitle(LocaleUtils.getString("key.FileChoice"));
            fileChooser.getExtensionFilters().addAll(filter1);
            //fileChooser.getExtensionFilters().addAll(filter2);

            File file = fileChooser.showOpenDialog(sc.getWindow());
            if (Objects.nonNull(file)) {
                logger.info("Selected: " + this.getCacheName());

                defaultDir = file.getParentFile();
                // ファイルのタイムスタンプ
                //Date updatedDate = new Date(file.lastModified());
                // 現在日時
                Date updatedDate = new Date();

                // キャッシュを削除
                this.deleteCacheData();

                // ファイルを読み込む
                this.fileNameField.setText(file.getName());
                this.updatedDateLabel.setText(DateUtils.format(updatedDate));

                this.workSection.setFileName(file.getName());
                this.workSection.setPhysicalName(RemoteStorage.getUploadFileName(file.getName()));
                this.workSection.setFileUpdated(updatedDate);
                this.workSection.setPageNum(0);
                this.workSection.setSourcePath(file.getPath());
                this.workSection.setChenged(true);
                this.render(file);
                this.loaded = true;

                this.clearButton.setDisable(false);
                this.slider.setDisable(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onChoice end.");
        }
    }

    /**
     * ドキュメントを消去する。
     *
     * @param event
     */
    @FXML
    private void onClear(ActionEvent event) {
        logger.info("Cleared: " + this.getCacheName());

        this.deleteCacheData();

        Date updatedDate = new Date();

        this.workSection.setFileName("");
        this.workSection.setPhysicalName(null);
        this.workSection.setPageNum(0);
        this.workSection.setSourcePath("");
        this.workSection.setChenged(true);
        this.workSection.setFileUpdated(updatedDate);

        this.fileNameField.setText("");
        this.updatedDateLabel.setText(DateUtils.format(updatedDate));

        this.imageView.setImage(null);
        this.imageView.setFitHeight(Double.NaN);
        this.imageView.setVisible(false);
        this.imageView.setManaged(false);

        this.pagination.setPageFactory(null);
        this.pagination.setVisible(false);
        this.pagination.setManaged(false);

        this.boardPane.setVisible(true);
        this.boardPane.setManaged(true);

        if (Objects.nonNull(this.mediaView)) {
            this.mediaView.getMediaPlayer().stop();
            this.mediaView = null;
        }
        this.mediaPane.setVisible(false);
        this.mediaPane.setManaged(false);
        this.mediaPane.getChildren().clear();

        // 勝手にスクロールされてしまう
        //this.clearButton.setDisable(true);
    }

    /**
     * 使用部品を設定する。
     *
     * @param useParts
     */
    public void setUseParts(ObservableList<ObjectInfoEntity> useParts) {
        this.useParts = useParts;
    }

    /**
     * 製造設備を設定する。
     *
     * @param manufactureEquipments
     */
    public void setManufactureEquipments(List<EquipmentInfoEntity> manufactureEquipments) {
        this.manufactureEquipments = manufactureEquipments;
    }

    /**
     * 測定機器を設定する。
     *
     * @param measureEquipments
     */
    public void setMeasureEquipments(List<EquipmentInfoEntity> measureEquipments) {
        this.measureEquipments = measureEquipments;
    }

    /**
     * ノードを取得する。
     *
     * @return
     */
    public Pane getPane() {
        return this.rootPane;
    }

    /**
     * トレーサビリティ設定用のページ分割チェックボックスを取得する。
     *
     * @return ページ分割チェックボックス値
     */
    public CheckBox getPageBreakCheckBox() {
        return this.pageBreakCheckBox;
    }

    /**
     * キャッシュデータ(ドキュメント)を読み込む。
     */
    public void loadCacheData() {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                synchronized (WorkSectionPane.this) {
                    if (!workSection.hasDocument() || loaded) {
                        return null;
                    }

                    try {
                        boolean getting = true;

                        String cacheName = getCacheName();

                        File file = new File(getCacheDirectoryPath() + File.separator + cacheName);
                        if (file.exists()) {

                            Date lastModified = new Date(file.lastModified());
                            if (lastModified.after(workSection.getFileUpdated())) {
                                getting = false;
                            } else {
                                // キャッシュデータが最新でない
                                file.delete();
                            }
                        }

                        if (getting) {
                            // キャッシュデータを最新にする
                            URL url = new URL(ClientServiceProperty.getServerUri());
                            URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), null, null, null);

                            RemoteStorage storage = new HttpStorage();
                            storage.configuration(uri.toASCIIString(), null, null);

                            StringBuilder sb = new StringBuilder();
                            sb.append(Paths.SERVER_DOWNLOAD_PDOC);
                            sb.append("/");
                            sb.append(workSection.getFkWorkId());
                            sb.append("/");
                            sb.append(cacheName);

                            File dir = new File(file.getParent());
                            if (!dir.exists()) {
                                dir.mkdirs();
                            } else {
                                dir.setLastModified(System.currentTimeMillis());
                            }

                            storage.download(sb.toString(), file.getPath());
                        }

                        loaded = true;
                        workSection.setSourcePath(file.getPath());

                        Platform.runLater(() -> render(file));
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    }
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    /**
     * ドキュメントをダウンロードする。
     */
    public void download() {
        try {
            boolean getting = true;
            String cacheName = this.getCacheName();

            File file = new File(this.getCacheDirectoryPath() + File.separator + cacheName);
            if (file.exists()) {
                Date lastModified = new Date(file.lastModified());
                if (lastModified.after(workSection.getFileUpdated())) {
                    getting = false;
                } else {
                    // キャッシュデータが最新でない
                    file.delete();
                }
            }

            if (getting) {

                // キャッシュデータを最新にする
                URL url = new URL(ClientServiceProperty.getServerUri());
                URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), null, null, null);

                RemoteStorage storage = new HttpStorage();
                storage.configuration(uri.toASCIIString(), null, null);

                StringBuilder sb = new StringBuilder();
                sb.append(Paths.SERVER_DOWNLOAD_PDOC);
                sb.append("/");
                sb.append(this.workSection.getFkWorkId());
                sb.append("/");
                sb.append(cacheName);

                storage.download(sb.toString(), file.getPath());
            }

            this.workSection.setSourcePath(file.getPath());
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * キャッシュデータを削除する。
     */
    public void deleteCacheData() {
        if (!this.workSection.hasDocument()) {
            return;
        }

        if (Objects.nonNull(this.pdfModel)) {
            this.pdfModel.close();
        }

        this.imageView.setImage(null);
        if (Objects.nonNull(this.mediaView)) {
            this.mediaView.getMediaPlayer().stop();
            this.mediaView = null;
        }

        File file = new File(this.getCacheDirectoryPath() + File.separator + this.getCacheName());
        if (file.exists()) {
            file.delete();
        }

        this.loaded = false;
    }

    /**
     * キャッシュデータが読み込まれているかどうかを返す。
     *
     * @return
     */
    public boolean isLoaded() {
        return this.loaded;
    }

    /**
     * リセットする。
     */
    public void reset() {
        this.loaded = false;
    }

    /**
     * 画像をレンダリングする。
     *
     * @param file
     */
    private void render(File file) {

        if (Objects.nonNull(listener)) {
            slider.valueProperty().removeListener(listener);
        }

        // 前処理
        this.boardPane.setVisible(false);
        this.boardPane.setManaged(false);
        
        this.imageView.setImage(null);
        this.imageView.setFitWidth(0);
        this.imageView.setFitHeight(0);
        this.imageView.setVisible(false);
        this.imageView.setManaged(false);

        this.pagination.setPageFactory(null);
        this.pagination.setVisible(false);
        this.pagination.setManaged(false);

        if (Objects.nonNull(this.mediaView)) {
            this.mediaView.getMediaPlayer().stop();
            this.mediaView = null;
        }
        this.mediaPane.setVisible(false);
        this.mediaPane.setManaged(false);
        this.mediaPane.getChildren().clear();

        if (file.getName().endsWith(".pdf")) {
            this.pdfModel = new PdfModel(file.toPath());
            this.pagination.setManaged(true);
            this.pagination.setPageCount(pdfModel.numPages());

            this.pagination.setPageFactory(index -> {
                ImageView page = new ImageView(this.pdfModel.getImage(index));
                page.setPreserveRatio(true);

                double sliderVal = this.slider.getValue();
                page.setFitWidth(sliderVal <= minRatio? width*minRatio/100 : width*this.slider.getValue()/100);
                page.setFitHeight(sliderVal <= minRatio ? height*minRatio/100 : height*this.slider.getValue()/100);

                this.listener = (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                    if (Objects.isNull(newVal)) {
                        return;
                    }

                    double ratio = newVal.doubleValue();
                    if (ratio <= minRatio) {
                        imageView.setFitWidth(width*minRatio/100);
                        imageView.setFitHeight(height*minRatio/100);
                    } else {
                        page.setFitWidth(width*ratio/100);
                        page.setFitHeight(height*ratio/100);
                    }
                    AdProperty.getProperties().setProperty(ClientServiceProperty.PDOC_ZOOM_RATIO, newVal.toString());
                };
                this.slider.valueProperty().addListener(this.listener);

                return page;
            });

            if (Objects.nonNull(this.workSection.getPageNum()) && this.workSection.getPageNum() < this.pdfModel.numPages()) {
                this.pagination.setCurrentPageIndex(this.workSection.getPageNum());
            }

            this.pagination.setVisible(true);

            // メディアファイルの正規表現(*.拡張子)でマッチング
        } else if (file.getName().matches(".+\\.(aif|aiff|fxm|flv|mp3|mp4|m4a|m4v|wav|m3u8)")) {
            //ファイルを読み込み
            Media m = new Media(file.toURI().toString());
            //動画の再生等の操作を実行できるオブジェクト
            MediaPlayer mp = new MediaPlayer(m);
            //動画パネルの挿入
            this.mediaView = new MediaView(mp);
            // メディア操作ボタン作成
            HBox buttonNode = this.createButton(mp);

            if (file.getName().matches(".+\\.(aif|aiff|mp3||wav)")) {
                //音声ファイルの場合
                this.mediaView.setFitWidth(0);
                this.mediaView.setFitHeight(0);
                buttonNode.getChildren().forEach(v -> v.setOpacity(1.0));
            } else {
                this.mediaView.setPreserveRatio(true);

                double sliderVal = this.slider.getValue();
                this.mediaView.setFitWidth(sliderVal <= minRatio ? width*minRatio/100 : width*this.slider.getValue()/100);
                this.mediaView.setFitHeight(sliderVal <= minRatio ? height*minRatio/100 : height*this.slider.getValue()/100);

                this.listener = (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                    if (Objects.isNull(newVal)) {
                        return;
                    }

                    double ratio = newVal.doubleValue();
                    if (ratio <= minRatio) {
                        imageView.setFitWidth(width*minRatio/100);
                        imageView.setFitHeight(height*minRatio/100);
                    } else {
                        mediaView.setFitWidth(width*ratio/100);
                        mediaView.setFitHeight(height*ratio/100);
                    }
                    AdProperty.getProperties().setProperty(ClientServiceProperty.PDOC_ZOOM_RATIO, newVal.toString());
                };
                this.slider.valueProperty().addListener(this.listener);
            }

            this.mediaPane.setManaged(true);
            this.mediaPane.getChildren().addAll(this.mediaView, buttonNode);
            this.mediaPane.setBottomAnchor(buttonNode, buttonNode.getHeight());
            this.mediaPane.setVisible(true);

        } else {
            Image image = new Image(file.toURI().toString());
            this.imageView.setManaged(true);
            this.imageView.setImage(image);

            double sliderVal = this.slider.getValue();
            this.imageView.setFitWidth(sliderVal <= minRatio ? width*minRatio/100 : width*this.slider.getValue()/100);
            this.imageView.setFitHeight(sliderVal <= minRatio ? height*minRatio/100 : height*this.slider.getValue()/100);
            this.imageView.setVisible(true);

            this.listener = (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                if (Objects.isNull(newVal)) {
                    return;
                }

                double ratio = newVal.doubleValue();
                if (ratio <= minRatio) {
                    imageView.setFitWidth(width*minRatio/100);
                    imageView.setFitHeight(height*minRatio/100);
                } else {
                    imageView.setFitWidth(width*ratio/100);
                    imageView.setFitHeight(height*ratio/100);
                }
                AdProperty.getProperties().setProperty(ClientServiceProperty.PDOC_ZOOM_RATIO, newVal.toString());
            };
            this.slider.valueProperty().addListener(this.listener);

        }
    }

    /**
     * キャッシュディレクトリのパスを取得する。 C:\adFactory\client\cache\pdoc\<WorkId>
     *
     * @return
     */
    private String getCacheDirectoryPath() {
        return Paths.CLIENT_CACHE_PDOC + File.separator + String.valueOf(this.workSection.getFkWorkId());
    }

    /**
     * キャッシュ名を取得する。
     *
     * @return
     */
    public String getCacheName() {
        if (!this.workSection.hasDocument()) {
            return "";
        }
        return this.workSection.getPhysicalName();
    }

    /**
     * 再生、停止ボタンを作成
     *
     * @param mp
     * @return
     */
    public HBox createButton(MediaPlayer mp) {
        // 表示コンポーネントを作成
        HBox root = new HBox(1.0);
        Button playButton = new Button("▶");
        Button stopButton = new Button("■");
        playButton.setStyle("ContentTextBox");
        stopButton.setStyle("ContentTextBox");
        playButton.setOpacity(0.5);
        stopButton.setOpacity(0.5);
        root.getChildren().add(playButton);
        root.getChildren().add(stopButton);

        // 再生ボタンにイベントを登録
        EventHandler<ActionEvent> playHandler = (e) -> {
            mp.play();
        };
        playButton.addEventHandler(ActionEvent.ACTION, playHandler);

        // 停止ボタンにイベントを登録
        EventHandler<ActionEvent> stopHandler = (e) -> {
            mp.stop();
        };
        stopButton.addEventHandler(ActionEvent.ACTION, stopHandler);

        return root;
    }

    /**
     * 各種入力項目の有効/無効状態を設定する。
     *
     * @param isDisabled true：無効、false：有効
     */
    public void setInputItemViewState(boolean isDisabled) {
        // 選択、消去ボタンの有効/無効状態を切り替え
        if (isDisabled) {
            this.clearButton.setDisable(isDisabled);
            this.slider.setDisable(isDisabled);
        } else {
            if (this.workSection.hasDocument()) {
                this.clearButton.setDisable(false);
                this.slider.setDisable(false);
            }
        }
        this.choiceButton.setDisable(isDisabled);

        if (ClientServiceProperty.isLicensed("@Traceability")) {
            // 品質トレーサビリティ項目の有効/無効状態を切り替え
            this.workSection.getTraceabilityCollection().sort(Comparator.comparing(o -> o.getWorkPropOrder()));
            this.traceabilityPane.getChildren().clear();

            // ページ分割チェックボックスの状態を復元する。
            if (Objects.isNull(this.pageBreakCheckBox)) {
                this.pageBreakCheckBox = new CheckBox(LocaleUtils.getString("key.pageBreak"));
                this.pageBreakCheckBox.getStyleClass().add("ContentTitleLabel");
            }
            if (!this.workSection.getTraceabilityCollection().isEmpty()) {
                WorkPropertyInfoEntity firstProperty = this.workSection.getTraceabilityCollection().get(0);
                this.pageBreakCheckBox.setSelected(Boolean.TRUE.equals(firstProperty.getPageBreakEnabled()));
            }
            this.traceabilityPane.getChildren().add(0, this.pageBreakCheckBox);

            this.traceabilityTable = new Table(this.traceabilityPane.getChildren());
            this.traceabilityTable.isAddRecord(!isDisabled);
            this.traceabilityTable.isColumnTitleRecord(true);
            this.traceabilityTable.title(LocaleUtils.getString("key.TraceabilitySettings"));
            this.traceabilityTable.styleClass("ContentTitleLabel");

            // テーブルの行数が変更されたときにページ分割チェックボックスの状態を更新する。
            this.traceabilityTable.addRecordListener(event -> {
                updatePageBreakCheckbox(this.traceabilityTable.getRowCount() - 1);
            });

            //工程情報workInfoパラメータをセット
            this.traceabilityTable.setAbstractRecordFactory(new TraceabilityRecordFactory(traceabilityTable, workSection.getTraceabilityCollection(), this.useParts, this.manufactureEquipments, this.measureEquipments, isDisabled, this.workInfo));
        }
    }

    /**
     * ドキュメントファイルを表示する。 ドキュメントファイルがキャッシュされている場合　、loadCacheDataを処理
     * ドキュメントファイルがキャッシュされていない場合、SourcePathを表示
     */
    public void showDocumentFile() {
        try {

            // ドキュメントが設定されていない場合は処理終了
            if (!workSection.hasDocument()) {
                return;
            }

            // キャッシュファイルの存在チェック
            File file = new File(getCacheDirectoryPath() + File.separator + getCacheName());
            if (file.exists()) {
                loadCacheData();
            } else {
                this.render(new File(workSection.getSourcePath()));
                this.loaded = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }
    
    /**
     * レイアウトを調整する。
     */
    public void adjustLayout() {
        if (Objects.nonNull(this.traceabilityTable)) {
            this.traceabilityTable.adjustLayout();
        }
    }

    /**
     * 画像を貼り付ける。
     *
     * @param image 画像
     */
    public void pasteImage(Image image) {
        try {
            if (image != null) {
                Date now = new Date();
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                File file = new File(System.getenv("ADFACTORY_HOME") + File.separator + "temp" + File.separator + "Clip-" + sf.format(now) + ".png");

                RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(renderedImage, "png", file);
                this.render(file);

                this.fileNameField.setText(file.getName());
                this.updatedDateLabel.setText(DateUtils.format(now));

                this.workSection.setFileName(file.getName());
                this.workSection.setPhysicalName(RemoteStorage.getUploadFileName(file.getName()));
                this.workSection.setFileUpdated(now);
                this.workSection.setPageNum(0);
                this.workSection.setSourcePath(file.getPath());
                this.workSection.setChenged(true);

                this.loaded = true;
                this.slider.setDisable(this.isItemDisabled);
                this.clearButton.setDisable(this.isItemDisabled);
            }

        } catch(IOException ex) {
            logger.fatal(ex);
		}
	}

    private void updatePageBreakCheckbox(int newRowCount) {
        if (newRowCount < 2) {
            pageBreakCheckBox.setDisable(true);
            pageBreakCheckBox.setSelected(false);
        } else {
            pageBreakCheckBox.setDisable(false);
        }
    }
}
