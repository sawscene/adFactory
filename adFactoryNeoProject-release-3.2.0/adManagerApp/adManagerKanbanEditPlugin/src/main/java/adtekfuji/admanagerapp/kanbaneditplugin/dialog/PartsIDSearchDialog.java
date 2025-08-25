/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.clientservice.PartsInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.PartsRemoveCondition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * パーツID検索ダイアログ
 *
 * @author k-maruoka
 */
@FxComponent(id = "PartsIDSearchDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/parts_id_search_dialog.fxml")
public class PartsIDSearchDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();

    private final SceneContiner sc = SceneContiner.getInstance();

    private static final SimpleDateFormat SDFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private Dialog dialog;

    private final ObservableList<PartsIDSearchDialog.DispPartsEntity> list = FXCollections.observableArrayList();

    private final PartsInfoFacade facade = new PartsInfoFacade();

    @FXML
    private StackPane pane;
    @FXML
    private TextField partsIDTextField; // パーツID
    @FXML
    private Button searchButton; // 検索ボタン

    @FXML
    private TableView<PartsIDSearchDialog.DispPartsEntity> partsTableView; // パーツリスト
    @FXML
    private TableColumn<PartsIDSearchDialog.DispPartsEntity, Boolean> checkColumn = new TableColumn<>("Selected"); // チェック
    @FXML
    private TableColumn<PartsIDSearchDialog.DispPartsEntity, String> partsIDColumn; // パーツID
    @FXML
    private TableColumn<PartsIDSearchDialog.DispPartsEntity, String> timeColumn; // 保存日時

    @FXML
    private Button deleteButton; // 削除ボタン
    @FXML
    private Button closeButton; // 閉じるボタン

    @FXML
    private Pane progressPane;

    /**
     * パーツ一覧表示用エンティティ
     *
     * @author k-maruoka
     */
    public class DispPartsEntity {

        private final BooleanProperty selected = new SimpleBooleanProperty();
        private final StringProperty partsID = new SimpleStringProperty();
        private final StringProperty time = new SimpleStringProperty();
        private PartsInfoEntity entity;

        /**
         * コンストラクタ
         *
         * @param entity 完成品情報
         * @param sdFormat 日時の表示フォーマット
         */
        public DispPartsEntity(PartsInfoEntity entity, SimpleDateFormat sdFormat) {
            this.entity = entity;
            this.selected.setValue(Boolean.FALSE);
            this.partsID.setValue(entity.getPartsId());
            this.time.setValue((sdFormat.format(entity.getCompDatetime())));
        }

        public BooleanProperty selectedProperty() {
            return this.selected;
        }

        public StringProperty partsIDProperty() {
            return this.partsID;
        }

        public StringProperty timeProperty() {
            return this.time;
        }

        public PartsInfoEntity getEntity() {
            return this.entity;
        }
    }

    /**
     * コンストラクタ
     */
    public PartsIDSearchDialog() {
    }

    /**
     * Initializes the controller class.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.partsTableView.setPlaceholder(new Label(LocaleUtils.getString("key.ListIsEmpty")));

        this.progressPane.setVisible(false);

        this.partsTableView.setEditable(true);

        // CheckBox
        CheckBox allCheckBox = new CheckBox();
        allCheckBox.selectedProperty().addListener(b -> {
            allCheck(allCheckBox.isSelected());
            this.setButtonDisable();
        });
        this.checkColumn.setGraphic(allCheckBox);
        this.checkColumn.setCellValueFactory(f -> f.getValue().selectedProperty());
        this.checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(this.checkColumn));
        this.checkColumn.setCellFactory(column -> {
            CheckBoxTableCell<PartsIDSearchDialog.DispPartsEntity, Boolean> cell = new CheckBoxTableCell<>();
            cell.setSelectedStateCallback(index -> {
                BooleanProperty selectedProperty = partsTableView.getItems().get(index).selectedProperty();
                // チェック状態を監視してボタン活性・非活性設定を行なう
                selectedProperty.addListener((observable, oldValue, newValue) -> {
                    this.setButtonDisable();
                });
                return selectedProperty;
            });
            return cell;
        });

        // パーツID
        this.partsIDColumn.setCellValueFactory((TableColumn.CellDataFeatures<PartsIDSearchDialog.DispPartsEntity, String> param) -> param.getValue().partsIDProperty());
        // 製造日
        this.timeColumn.setCellValueFactory((TableColumn.CellDataFeatures<PartsIDSearchDialog.DispPartsEntity, String> param) -> param.getValue().timeProperty());

        // パーツIDの幅を自動調整
        this.partsTableView.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.partsIDColumn.setPrefWidth(newValue.doubleValue() - this.checkColumn.getWidth() - this.timeColumn.getWidth() - 15);
            this.partsIDTextField.setPrefWidth(this.partsIDColumn.getPrefWidth());
        });
        
        // 画面更新処理
        this.updateView();

        // ボタン活性・非活性設定
        this.setButtonDisable();
    }

    /**
     * 全行チェック
     *
     * @pram check True=チェック False=チェックを外す
     */
    private void allCheck(boolean check) {
        try {
            for (int i = 0; i < this.partsTableView.getItems().size(); i++) {
                this.partsTableView.getItems().get(i).selectedProperty().set(check);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * ダイアログ設定
     *
     * @param dialog ダイアログ
     */
    @Override
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        Stage stage = (Stage) this.dialog.getDialogPane().getScene().getWindow();
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            this.pane.requestLayout();
        });
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            this.pane.requestLayout();
        });
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.closeDialog();
        });
    }

    /**
     * UIロック
     *
     * @param flg True＝ロック
     */
    private void blockUI(Boolean flg) {
        this.pane.setDisable(flg);
        this.progressPane.setVisible(flg);
    }

    /**
     * 引数を設定する。
     *
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {

    }

    /**
     * 検索ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onSearch(ActionEvent event) {
        logger.info("onSearch start.");
        blockUI(true);
        try {
            this.updateView();
            this.setButtonDisable();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onSearch end.");
            blockUI(false);
        }
    }

    /**
     * 削除ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onDelete(ActionEvent event) {
        logger.info("onDelete start.");
        blockUI(true);
        try {
            // 「選択されたパーツIDを削除します。よろしいですか?」を表示して「はい」が押下されなければ終了
            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.CONFIRMATION,
                    LocaleUtils.getString("key.confirm"),
                    LocaleUtils.getString("key.confirm.removePartsID"),
                    new ButtonType[]{ButtonType.YES, ButtonType.NO}, ButtonType.NO);
            if (ButtonType.YES != buttonType) {
                return;
            }

            // 選択されたパーツIDを削除
            if (!this.deleteParts()) {
                // 失敗なら「パーツIDの削除に失敗しました。」を表示して終了
                sc.showMessageBox(Alert.AlertType.ERROR,
                        LocaleUtils.getString("key.Error"),
                        LocaleUtils.getString("key.FailedToRemovePartsID"),
                        new ButtonType[]{ButtonType.OK}, ButtonType.OK);
                return;
            }
            
            // 再表示
            this.updateView();
            this.setButtonDisable();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onDelete end.");
            blockUI(false);
        }
    }

    /**
     * 閉じるボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onClose(ActionEvent event) {
        logger.info("onClose start.");
        try {
            this.closeDialog();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("onClose end.");
        }
    }

    /**
     * ボタンの活性・非活性を設定する。
     *
     */
    private void setButtonDisable() {
        this.deleteButton.setDisable(!this.isAnyPartsChecked());
    }

    /**
     * ダイアログを閉じる。
     */
    private void closeDialog() {
        try {
            this.dialog.setResult(ButtonType.CLOSE);
            this.dialog.close();
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * いずれかのパーツがチェックされているか判定する。
     *
     * @return true=いずれかのパーツがチェックされている false=いずれのパーツもチェックされていない
     */
    private boolean isAnyPartsChecked() {
        return partsTableView.getItems().stream()
                .anyMatch(item -> checkColumn.getCellData(item));
    }

    /**
     * チェックされているパーツ一覧を取得する。
     *
     * @return チェックされているPartsInfoEntityのリスト
     */
    private List<PartsInfoEntity> getCheckedPartsInfoEntities() {
        return partsTableView.getItems().stream()
                .filter(item -> item.selectedProperty().get())
                .map(item -> item.getEntity())
                .collect(Collectors.toList());
    }

    /**
     * パーツを削除する。
     *
     * @return true=成功 false=失敗
     */
    private boolean deleteParts() {
        PartsRemoveCondition condition = new PartsRemoveCondition();
        List<PartsInfoEntity> entities = getCheckedPartsInfoEntities();
        condition.setItems(entities);
        ResponseEntity res = this.facade.removeForced(condition);
        return Boolean.TRUE.equals(res.isSuccess());
    }

    /**
     * 画面を更新する。
     */
    private void updateView() {
        // パーツIDでパーツを検索して画面に表示する。
        this.list.clear();
        List<PartsInfoEntity> entities = this.facade.searchParts(
                "%" + this.partsIDTextField.getText() + "%");
        for (PartsInfoEntity entity : entities) {
            this.list.add(new DispPartsEntity(entity, SDFORMAT));
        }
        this.list.addAll();
        this.partsTableView.setItems(this.list);
    }
}
