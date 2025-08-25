package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.property.HeaderTextAndComboFieldRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.property.HeaderTextFieldRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ImportHeaderFormatFileUtil;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.*;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.importformat.*;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * CSV形式（ヘッダ名指定）フォーマット変更画面
 *
 * @author (AQTOR)Koga
 */
@FxComponent(id = "WorkPlanHeaderFormatChangeCompo", fxmlPath = "/fxml/compo/work_plan_header_format_compo.fxml")
public class WorkPlanHeaderFormatChangeCompoCpntroller implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale");

    private LinkedList<SimpleStringProperty> inputTab1HeaderCsvProcessHierarchyNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab1HeaderCsvProcessNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab2HeaderCsvProcessNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab3HeaderCsvWorkflowHierarchyNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab3HeaderCsvWorkflowNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab3HeaderCsvModelNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab3HeaderCsvProcessNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab4HeaderCsvWorkflowNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab5HeaderCsvKanbanNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab5HeaderCsvWorkflowNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab5HeaderCsvWorkNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab6HeaderCsvKanbanNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab6HeaderCsvWorkflowNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab6HeaderCsvWorkNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab7HeaderCsvKanbanHierarchyNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab7HeaderCsvKanbanNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab7HeaderCsvWorkflowNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab7HeaderCsvModelNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab7HeaderCsvProductNumNames = new LinkedList<>();

    private LinkedList<SimpleStringProperty> inputTab8HeaderCsvKanbanNames = new LinkedList<>();
    private LinkedList<SimpleStringProperty> inputTab8HeaderCsvWorkflowNames = new LinkedList<>();
    private final LinkedList<PropHeaderFormatInfo> headerCsvWorkPropInfos = new LinkedList<>();
    private final LinkedList<PropHeaderFormatInfo> headerCsvWorkflowPropInfos = new LinkedList<>();
    private final LinkedList<PropHeaderFormatInfo> headerCsvWorkKanbanPropInfos = new LinkedList<>();
    private final LinkedList<PropHeaderFormatInfo> headerCsvKanbanPropInfos = new LinkedList<>();

    private Object argument = null;

    /**
     * タブ *
     */
    @FXML
    private TabPane tabImportMode;
    /**
     * 工程情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab1HeaderCsvEncode;
    /**
     * 工程情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab1HeaderCsvFileName;
    /**
     * 工程情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab1HeaderCsvHeaderRow;
    /**
     * 工程情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab1HeaderCsvLineStart;
    /**
     * 工程情報タブ：工程階層 *
     */
    @FXML
    private VBox inputTab1HeaderCsvHierarchyPain;
    /**
     * 工程情報タブ：工程階層（区切り文字） *
     */
    @FXML
    private TextField inputTab1HeaderCsvDelimiter1;
    /**
     * 工程情報タブ：工程名 *
     */
    @FXML
    private VBox inputTab1HeaderCsvProcessNamePain;
    /**
     * 工程情報タブ：工程名（区切り文字） *
     */
    @FXML
    private TextField inputTab1HeaderCsvDelimiter2;
    /**
     * 工程情報タブ：タクトタイム *
     */
    @FXML
    private TextField inputTab1HeaderCsvTactTime;
    /**
     * 工程情報タブ：単位（タクトタイム） *
     */
    @FXML
    private ComboBox<String> inputTab1HeaderCsvTactTimeUnit;
    /**
     * 工程情報タブ：作業内容 *
     */
    @FXML
    private TextField inputTab1HeaderCsvWorkContent1;
    @FXML
    private TextField inputTab1HeaderCsvWorkContent2;
    @FXML
    private TextField inputTab1HeaderCsvWorkContent3;

    /**
     * 工程プロパティ情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab2HeaderCsvEncode;
    /**
     * 工程プロパティ情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab2HeaderCsvFileName;
    /**
     * 工程プロパティ情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab2HeaderCsvHeaderRow;
    /**
     * 工程プロパティ情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab2HeaderCsvLineStart;
    /**
     * 工程プロパティ情報タブ：工程名 *
     */
    @FXML
    private VBox inputTab2HeaderCsvProcessNamePain;
    /**
     * 工程プロパティ情報タブ：工程名（区切り文字） *
     */
    @FXML
    private TextField inputTab2HeaderCsvDelimiter1;
    /**
     * 工程プロパティ情報タブ：プロパティ *
     */
    @FXML
    private VBox inputTab2HeaderCsvPropertyPane;

    /**
     * 工程順情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab3HeaderCsvEncode;
    /**
     * 工程順情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab3HeaderCsvFileName;
    /**
     * 工程順情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab3HeaderCsvHeaderRow;
    /**
     * 工程順情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab3HeaderCsvLineStart;
    /**
     * 工程順情報タブ：工程順階層 *
     */
    @FXML
    private VBox inputTab3HeaderCsvWorkflowHierarchyPain;
    /**
     * 工程順情報タブ：工程順階層（区切り文字） *
     */
    @FXML
    private TextField inputTab3HeaderCsvDelimiter1;
    /**
     * 工程順情報タブ：工程順名 *
     */
    @FXML
    private VBox inputTab3HeaderCsvWorkflowNamePain;
    /**
     * 工程順情報タブ：工程順名（区切り文字） *
     */
    @FXML
    private TextField inputTab3HeaderCsvDelimiter2;
    /**
     * 工程順情報タブ：モデル名 *
     */
    @FXML
    private VBox inputTab3HeaderCsvModelNamePain;
    /**
     * 工程順情報タブ：モデル名（区切り文字） *
     */
    @FXML
    private TextField inputTab3HeaderCsvDelimiter3;
    /**
     * 工程順情報タブ：工程名 *
     */
    @FXML
    private VBox inputTab3HeaderCsvProcessNamePain;
    /**
     * 工程順情報タブ：工程名（区切り文字） *
     */
    @FXML
    private TextField inputTab3HeaderCsvDelimiter4;
    /**
     * 工程順情報タブ：組織 *
     */
    @FXML
    private TextField inputTab3HeaderCsvOrganization;
    /**
     * 工程順情報タブ：設備 *
     */
    @FXML
    private TextField inputTab3HeaderCsvEquipment;
    /**
     * 工程順情報タブ：工程の並び順 *
     */
    @FXML
    private TextField inputTab3HeaderCsvProcOrder;
    /**
     * 工程順情報タブ：工程接続 *
     */
    @FXML
    private ComboBox<String> inputTab3HeaderCsvProcCon;
    /**
     * 工程順情報タブ：完了工程を含めてリスケジュールするか？ *
     */
    @FXML
    private ComboBox<String> inputTab3IsRescheduleValue;

    /**
     * 工程順プロパティ情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab4HeaderCsvEncode;
    /**
     * 工程順プロパティ情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab4HeaderCsvFileName;
    /**
     * 工程順プロパティ情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab4HeaderCsvHeaderRow;
    /**
     * 工程順プロパティ情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab4HeaderCsvLineStart;
    /**
     * 工程順プロパティ情報タブ：工程順名 *
     */
    @FXML
    private VBox inputTab4HeaderCsvWorkflowNamePain;
    /**
     * 工程順プロパティ情報タブ：工程順名（区切り文字） *
     */
    @FXML
    private TextField inputTab4HeaderCsvDelimiter1;
    /**
     * 工程順プロパティ情報タブ：プロパティ *
     */
    @FXML
    private VBox inputTab4HeaderCsvPropertyPane;

    /**
     * 工程カンバン情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab5HeaderCsvEncode;

    /**
     * 工程カンバン情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab5HeaderCsvFileName;

    /**
     * 工程カンバン情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab5HeaderCsvHeaderRow;

    /**
     * 工程カンバン情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab5HeaderCsvLineStart;

    /**
     * 工程カンバン情報タブ：カンバン名 *
     */
    @FXML
    private VBox inputTab5HeaderCsvKanbanNamePain;

    /**
     * 工程カンバン情報タブ：カンバン名（区切り文字） *
     */
    @FXML
    private TextField inputTab5HeaderCsvDelimiter1;
    /**
     * カンバン情報タブ：工程順名 *
     */
    @FXML
    private VBox inputTab5HeaderCsvWorkflowNamePain;
    /**
     * カンバン情報タブ：工程順名（区切り文字） *
     */
    @FXML
    private TextField inputTab5HeaderCsvDelimiter2;
    /**
     * カンバン情報タブ：工程順リビジョン *
     */
    @FXML
    private TextField inputTab5HeaderCsvWorkflowRev;

    /**
     * 工程カンバン情報タブ：工程名 *
     */
    @FXML
    private VBox inputTab5HeaderCsvWorkNamePain;
    /**
     * 工程カンバン情報タブ：工程名（区切り文字） *
     */
    @FXML
    private TextField inputTab5HeaderCsvDelimiter3;

    /**
     * 工程カンバン情報タブ：タクトタイム *
     */
    @FXML
    private TextField inputTab5HeaderCsvTactTime;
    /**
     * 工程カンバン情報タブ：単位（タクトタイム） *
     */
    @FXML
    private ComboBox<String> inputTab5HeaderCsvTactTimeUnit;

    /**
     * 工程カンバン情報タブ：開始予定日 *
     */
    @FXML
    private TextField inputTab5HeaderCsvStartDate;

    /**
     * 工程カンバン情報タブ：完了予定日 *
     */
    @FXML
    private TextField inputTab5HeaderCsvEndDate;
    /**
     * 工程カンバン情報タブ：組織 *
     */
    @FXML
    private TextField inputTab5HeaderCsvOrganization;
    /**
     * 工程カンバン情報タブ：設備 *
     */
    @FXML
    private TextField inputTab5HeaderCsvEquipment;


    /**
     * 工程カンバンプロパティ情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab6HeaderCsvEncode;
    /**
     * 工程カンバンプロパティ情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab6HeaderCsvFileName;
    /**
     * 工程カンバンプロパティ情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab6HeaderCsvHeaderRow;
    /**
     * 工程カンバンプロパティ情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab6HeaderCsvLineStart;
    /**
     * 工程カンバンプロパティ情報タブ：カンバン名 *
     */
    @FXML
    private VBox inputTab6HeaderCsvKanbanNamePain;
    /**
     * 工程カンバンプロパティ情報タブ：カンバン名（区切り文字） *
     */
    @FXML
    private TextField inputTab6HeaderCsvDelimiter1;
    /**
     * 工程カンバン情報タブ：工程順名 *
     */
    @FXML
    private VBox inputTab6HeaderCsvWorkflowNamePain;
    /**
     * 工程カンバン情報タブ：工程順名（区切り文字） *
     */
    @FXML
    private TextField inputTab6HeaderCsvDelimiter2;
    /**
     * 工程カンバン情報タブ：工程順リビジョン *
     */
    @FXML
    private TextField inputTab6HeaderCsvWorkflowRev;
    /**
     * 工程カンバンプロパティ情報タブ：工程名 *
     */
    @FXML
    private VBox inputTab6HeaderCsvWorkNamePain;
    /**
     * 工程カンバン情報タブ：工程名（区切り文字） *
     */
    @FXML
    private TextField inputTab6HeaderCsvDelimiter3;
    /**
     * 工程カンバンプロパティ情報タブ：プロパティ *
     */
    @FXML
    private VBox inputTab6HeaderCsvPropertyPane;

    /**
     * カンバン情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab7HeaderCsvEncode;
    /**
     * カンバン情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab7HeaderCsvFileName;
    /**
     * カンバン情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab7HeaderCsvHeaderRow;
    /**
     * カンバン情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab7HeaderCsvLineStart;
    /**
     * カンバン情報タブ：カンバン階層 *
     */
    @FXML
    private VBox inputTab7HeaderCsvKanbanHierarchyPain;
    /**
     * カンバン情報タブ：カンバン階層（区切り文字） *
     */
    @FXML
    private TextField inputTab7HeaderCsvDelimiter1;
    /**
     * カンバン情報タブ：カンバン名 *
     */
    @FXML
    private VBox inputTab7HeaderCsvKanbanNamePain;
    /**
     * カンバン情報タブ：カンバン名（区切り文字） *
     */
    @FXML
    private TextField inputTab7HeaderCsvDelimiter2;
    /**
     * カンバン情報タブ：工程順名 *
     */
    @FXML
    private VBox inputTab7HeaderCsvWorkflowNamePain;
    /**
     * カンバン情報タブ：工程順名（区切り文字） *
     */
    @FXML
    private TextField inputTab7HeaderCsvDelimiter3;
    /**
     * カンバン情報タブ：工程順リビジョン *
     */
    @FXML
    private TextField inputTab7HeaderCsvWorkflowRev;
    /**
     * カンバン情報タブ：モデル名 *
     */
    @FXML
    private VBox inputTab7HeaderCsvModelNamePain;
    /**
     * カンバン情報タブ：モデル名（区切り文字） *
     */
    @FXML
    private TextField inputTab7HeaderCsvDelimiter4;
    /**
     * カンバン情報タブ：製造番号 *
     */
    @FXML
    private VBox inputTab7HeaderCsvProductNumPain;
    /**
     * カンバン情報タブ：製造番号（区切り文字） *
     */
    @FXML
    private TextField inputTab7HeaderCsvDelimiter5;
    /**
     * カンバン情報タブ：開始予定日 *
     */
    @FXML
    private TextField inputTab7HeaderCsvStartDate;
    /**
     * カンバン情報タブ：生産タイプ *
     */
    @FXML
    private TextField inputTab7ProductionTypeValue;
    /**
     * カンバン情報タブ：ロット数量 *
     */
    @FXML
    private TextField inputTab7LotNumValue;
    /**
     * カンバン情報タブ：カンバンステータス（初期値） *
     */
    @FXML
    private ComboBox<String> inputTab7KanbanInitStatusValue;

    /**
     * カンバンプロパティ情報タブ：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab8HeaderCsvEncode;
    /**
     * カンバンプロパティ情報タブ：ファイル名 *
     */
    @FXML
    private TextField inputTab8HeaderCsvFileName;
    /**
     * カンバンプロパティ情報タブ：ヘッダー行 *
     */
    @FXML
    private TextField inputTab8HeaderCsvHeaderRow;
    /**
     * カンバンプロパティ情報タブ：読み込み開始行 *
     */
    @FXML
    private TextField inputTab8HeaderCsvLineStart;
    /**
     * カンバンプロパティ情報タブ：カンバン名 *
     */
    @FXML
    private VBox inputTab8HeaderCsvKanbanNamePane;
    /**
     * カンバンプロパティ情報タブ：カンバン名（区切り文字） *
     */
    @FXML
    private TextField inputTab8HeaderCsvDelimiter1;
    /**
     * カンバンプロパティ情報タブ：工程順名 *
     */
    @FXML
    private VBox inputTab8HeaderCsvWorkflowNamePane;
    /**
     * カンバンプロパティ情報タブ：工程順名（区切り文字） *
     */
    @FXML
    private TextField inputTab8HeaderCsvDelimiter2;
    /**
     * カンバンプロパティ情報タブ：工程順リビジョン *
     */
    @FXML
    private TextField inputTab8HeaderCsvWorkflowRev;
    /**
     * カンバンプロパティ情報タブ：プロパティ *
     */
    @FXML
    private VBox inputTab8HeaderCsvPropertyPane;

    @FXML
    private Pane progressPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger.info(":initialize start");

        this.initItem();

        // プロパティファイル読み込み.
        this.loadSetting();

        logger.info(":initialize end");
    }

    /**
     * 引数をセットする
     * 
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        this.argument = argument;
    }

    /**
     * 操作をロックする
     *
     * @param block
     */
    private void blockUI(Boolean block) {
        sc.blockUI("ContentNaviPane", block);
        this.progressPane.setVisible(block);
    }

    /**
     * 登録ボタンのクリックイベント
     *
     * @param enent
     */
    @FXML
    private void onEntryAction(ActionEvent enent) {
        logger.info(":onEntryAction start");
        boolean isCancel = false;
        try {
            blockUI(true);

            if (!this.isCheck()) {
                logger.warn("入力エラー");
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.alert.inputValidation"));
                isCancel = true;
                return;
            }

            final ImportHeaderFormatInfo newHeaderFormatInfo = createImportHeaderFormatInfo();

            Task task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    // 現在のフォーマット設定を読み込む。
                    ImportHeaderFormatInfo importHeaderFormatInfo = ImportHeaderFormatFileUtil.load();
                    // 設定を更新する。
                    importHeaderFormatInfo.setWorkHeaderFormatInfo(newHeaderFormatInfo.getWorkHeaderFormatInfo());
                    importHeaderFormatInfo.setWorkPropHeaderFormatInfo(newHeaderFormatInfo.getWorkPropHeaderFormatInfo());
                    importHeaderFormatInfo.setWorkflowHeaderFormatInfo(newHeaderFormatInfo.getWorkflowHeaderFormatInfo());
                    importHeaderFormatInfo.setWorkflowPropHeaderFormatInfo(newHeaderFormatInfo.getWorkflowPropHeaderFormatInfo());
                    importHeaderFormatInfo.setWorkKanbanHeaderFormatInfo(newHeaderFormatInfo.getWorkKanbanHeaderFormatInfo());
                    importHeaderFormatInfo.setWorkKanbanPropHeaderFormatInfo(newHeaderFormatInfo.getWorkKanbanPropHeaderFormatInfo());
                    importHeaderFormatInfo.setKanbanHeaderFormatInfo(newHeaderFormatInfo.getKanbanHeaderFormatInfo());
                    importHeaderFormatInfo.setKanbanPropHeaderFormatInfo(newHeaderFormatInfo.getKanbanPropHeaderFormatInfo());
                    // 設定をファイルに保存する。
                    return ImportHeaderFormatFileUtil.save(importHeaderFormatInfo);
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        if (this.getValue()) {
                            if (Objects.nonNull(argument) && (argument instanceof String)) {
                                sc.setComponent("ContentNaviPane", "WorkPlanImportCompo", argument);
                            } else {
                                sc.setComponent("ContentNaviPane", "WorkPlanImportCompo");
                            }
                        } else {
                            logger.error("登録エラー");
                        }
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            isCancel = true;
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }
    }

    /**
     * キャンセルボタンのクリックイベント
     *
     * @param enent
     */
    @FXML
    private void onCancelAction(ActionEvent enent) {
        logger.info(":onCancelAction start");
        boolean chancelFlg = false;

        // TODO: 変更があったら保存確認
        if (!chancelFlg) {
            if (Objects.nonNull(argument) && (argument instanceof String)) {
                sc.setComponent("ContentNaviPane", "WorkPlanImportCompo", argument);
            } else {
                sc.setComponent("ContentNaviPane", "WorkPlanImportCompo");
            }
        }
        logger.info(":onCancelAction end");
    }

    /**
     * データチェック処理
     *
     * @return 結果
     */
    private boolean isCheck() {
        boolean result = true;

        try {
            // 工程情報の入力チェック
            if (!this.isInputCheckWorkTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_WORK);
                }
                result = false;
            }
            // 工程プロパティ情報の入力チェック
            if (!this.isInputCheckWorkPropTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_WORK_PROP);
                }
                result = false;
            }
            // 工程順情報の入力チェック
            if (!this.isInputCheckWorkflowTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_WORKFLOW);
                }
                result = false;
            }
            // 工程順プロパティ情報の入力チェック
            if (!this.isInputCheckWorkflowPropTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_WORKFLOW_PROP);
                }
                result = false;
            }
            // 工程カンバン情報の入力チェック
            if (!this.isInputCheckWorkKanbanTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_WORK_KANBAN);
                }
                result = false;
            }
            // 工程カンバンプロパティ情報入力チェック
            if (!this.isInputCheckWorkKanbanPropTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_WORK_KANBAN_PROP);
                }
                result = false;
            }
            // カンバン情報の入力チェック
            if (!this.isInputCheckKanbanTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_KANBAN);
                }
                result = false;
            }
            // カンバンプロパティ情報の入力チェック
            if (!this.isInputCheckKanbanPropTab()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_HAEDER_KANBAN_PROP);
                }
                result = false;
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            result = false;
        }

        logger.debug(" チェック結果:" + result);
        return result;
    }

    /**
     * 工程情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckWorkTab() {
        // 入力欄を通常表示にする。
        this.setWorkTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab1HeaderCsvFileName.getText())) {
            return true;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab1HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab1HeaderCsvHeaderRow)) {
            return false;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab1HeaderCsvLineStart)) {
            return false;
        }
        // 工程階層
        if (inputTab1HeaderCsvProcessHierarchyNames.isEmpty()
                || inputTab1HeaderCsvProcessHierarchyNames
                .stream()
                .map(SimpleStringProperty::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        // 工程階層
        if (inputTab1HeaderCsvProcessNames.isEmpty()
                || inputTab1HeaderCsvProcessNames
                .stream()
                .map(SimpleStringProperty::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        return true;
    }

    /**
     * 工程情報タブの全ての入力欄を通常表示にする。
     */
    private void setWorkTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvLineStart);       // 読み込み開始行

        // 工程階層
        ((GridPane) this.inputTab1HeaderCsvHierarchyPain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvDelimiter1);      // 工程階層（区切り文字）

        // 工程名
        ((GridPane) this.inputTab1HeaderCsvProcessNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvDelimiter2);      // 工程名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvTactTime);        // タクトタイム
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvTactTimeUnit);    // 単位（タクトタイム）
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvWorkContent1);    // 作業内容（１フィールド目）
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvWorkContent2);    // 作業内容（２フィールド目）
        ProductionNaviUtils.setFieldNormal(this.inputTab1HeaderCsvWorkContent3);    // 作業内容（３フィールド目）
    }

    /**
     * 工程プロパティ情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckWorkPropTab() {

        // 入力欄を通常表示にする。
        this.setWorkPropTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab2HeaderCsvFileName.getText())) {
            return true;
        }

        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab2HeaderCsvEncode)) {
            return false;
        }

        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab2HeaderCsvHeaderRow)) {
            return false;
        }

        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab2HeaderCsvLineStart)) {
            return false;
        }

        // 工程名
        if (inputTab2HeaderCsvProcessNames.isEmpty()
                || inputTab2HeaderCsvProcessNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        return true;
    }

    /**
     * 工程情報タブの全ての入力欄を通常表示にする。
     */
    private void setWorkPropTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab2HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab2HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab2HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab2HeaderCsvLineStart);       // 読み込み開始行

        // 工程名
        ((GridPane) this.inputTab2HeaderCsvProcessNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
        .getChildren().filtered(p -> p instanceof TextField)
        .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        // 工程名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab2HeaderCsvDelimiter1);

        // 工程プロパティ
        ((GridPane) this.inputTab2HeaderCsvPropertyPane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
    }

    /**
     * 工程順情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckWorkflowTab() {
        // 入力欄を通常表示にする。
        this.setWorkflowTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab3HeaderCsvFileName.getText())) {
            return true;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab3HeaderCsvLineStart)) {
            return false;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab3HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab3HeaderCsvHeaderRow)) {
            return false;
        }
        // 工程順階層
        if (inputTab3HeaderCsvWorkflowHierarchyNames.isEmpty()
                || inputTab3HeaderCsvWorkflowHierarchyNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // 工程順名
        if (inputTab3HeaderCsvWorkflowNames.isEmpty()
                || inputTab3HeaderCsvWorkflowNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // 工程名
        if (inputTab3HeaderCsvProcessNames.isEmpty()
                || inputTab3HeaderCsvProcessNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        return true;
    }

    /**
     * 工程順情報タブの全ての入力欄を通常表示にする。
     */
    private void setWorkflowTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvLineStart);       // 読み込み開始行

        // 工程順階層
        ((GridPane) this.inputTab3HeaderCsvWorkflowHierarchyPain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvDelimiter1);      // 工程順階層（区切り文字）

        // 工程順名
        ((GridPane) this.inputTab3HeaderCsvWorkflowNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvDelimiter2);      // 工程順名（区切り文字）

        // モデル名
        ((GridPane) this.inputTab3HeaderCsvModelNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvDelimiter3);      // モデル名（区切り文字）

        // 工程名
        ((GridPane) this.inputTab3HeaderCsvProcessNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvDelimiter4);      // 工程名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvOrganization);    // 組織
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvEquipment);       // 設備
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvProcOrder);       // 工程の並び順
        ProductionNaviUtils.setFieldNormal(this.inputTab3HeaderCsvProcCon);         // 工程接続
        ProductionNaviUtils.setFieldNormal(this.inputTab3IsRescheduleValue);        // 完了工程を含めてリスケジュールするか？
    }

    /**
     * 工程順プロパティ情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckWorkflowPropTab() {

        // 入力欄を通常表示にする。
        this.setWorkflowPropTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab4HeaderCsvFileName.getText())) {
            return true;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab4HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab4HeaderCsvHeaderRow)) {
            return false;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab4HeaderCsvLineStart)) {
            return false;
        }
        // 工程順名
        if (inputTab4HeaderCsvWorkflowNames.isEmpty()
                || inputTab4HeaderCsvWorkflowNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        return true;
    }

    /**
     * 工程順プロパティ情報タブの全ての入力欄を通常表示にする。
     */
    private void setWorkflowPropTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab4HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab4HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab4HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab4HeaderCsvLineStart);       // 読み込み開始行

        // 工程順名
        ((GridPane) this.inputTab4HeaderCsvWorkflowNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
        ProductionNaviUtils.setFieldNormal(this.inputTab4HeaderCsvDelimiter1);      // 工程順名（区切り文字）

        ((GridPane) this.inputTab4HeaderCsvPropertyPane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
    }

    /**
     * 工程カンバン情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckWorkKanbanTab() {

        // 入力欄を通常表示にする。
        this.setWorkKanbanTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab5HeaderCsvFileName.getText())) {
            return true;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab5HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab5HeaderCsvHeaderRow)) {
            return false;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab5HeaderCsvLineStart)) {
            return false;
        }
        // カンバン名
        if (inputTab5HeaderCsvKanbanNames.isEmpty()
                || inputTab5HeaderCsvKanbanNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // 工程順名
        if (inputTab5HeaderCsvWorkflowNames.isEmpty()
                || inputTab5HeaderCsvWorkflowNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // 工程名
        if (inputTab5HeaderCsvWorkNames.isEmpty()
                || inputTab5HeaderCsvWorkNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        return true;
    }


    /**
     * 工程カンバン情報タブの全ての入力欄を通常表示にする。
     */
    private void setWorkKanbanTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvLineStart);       // 読み込み開始行
        // カンバン名
        ((GridPane) this.inputTab5HeaderCsvKanbanNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvDelimiter1);      // カンバン名（区切り文字）
        // 工程順名（
        ((GridPane) this.inputTab5HeaderCsvWorkflowNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvDelimiter2);      // 工程順名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvWorkflowRev);     // 工程順リビジョン
        // 工程名
        ((GridPane) this.inputTab5HeaderCsvWorkNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvDelimiter3);      // 工程名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvTactTime);      // タクトタイム
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvTactTimeUnit);      // 単位（タクトタイム）
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvStartDate);      // 開始予定日
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvEndDate);      // 完了予定日
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvOrganization);      // 組織
        ProductionNaviUtils.setFieldNormal(this.inputTab5HeaderCsvEquipment);      // 設備
    }

    /**
     * 工程カンバンプロパティ情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckWorkKanbanPropTab() {

        // 入力欄を通常表示にする。
        this.setWorkKanbanPropTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab6HeaderCsvFileName.getText())) {
            return true;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab6HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab6HeaderCsvHeaderRow)) {
            return false;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab6HeaderCsvLineStart)) {
            return false;
        }
        // カンバン名
        if (inputTab6HeaderCsvKanbanNames.isEmpty()
                || inputTab6HeaderCsvKanbanNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        // 工程順名
        if (inputTab6HeaderCsvWorkflowNames.isEmpty()
                || inputTab6HeaderCsvWorkflowNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        // 工程名
        if (inputTab6HeaderCsvWorkNames.isEmpty()
                || inputTab6HeaderCsvWorkNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        return true;
    }

    /**
     * 工程カンバンプロパティ情報タブの全ての入力欄を通常表示にする。
     */
    private void setWorkKanbanPropTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvLineStart);       // 読み込み開始行
        // カンバン名
        ((GridPane) this.inputTab6HeaderCsvKanbanNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvDelimiter1);      // カンバン名（区切り文字）
        // 工程順名
        ((GridPane) this.inputTab6HeaderCsvWorkflowNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvDelimiter2);      // 工程順名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvWorkflowRev);     // 工程順リビジョン
        // 工程名
        ((GridPane) this.inputTab6HeaderCsvWorkNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
        ProductionNaviUtils.setFieldNormal(this.inputTab6HeaderCsvDelimiter3);      // 工程名（区切り文字）
        ((GridPane) this.inputTab6HeaderCsvPropertyPane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
    }

    /**
     * カンバン情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckKanbanTab() {

        // 入力欄を通常表示にする。
        this.setKanbanTabTextFieldNormal();

        // ファイル名が入力されていない場合
        if (StringUtils.isEmpty(this.inputTab7HeaderCsvFileName.getText())) {
            return true;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab7HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab7HeaderCsvHeaderRow)) {
            return false;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab7HeaderCsvLineStart)) {
            return false;
        }
        // カンバン階層
        if (inputTab7HeaderCsvKanbanHierarchyNames.isEmpty()
                || inputTab7HeaderCsvKanbanHierarchyNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // カンバン名
        if (inputTab7HeaderCsvKanbanNames.isEmpty()
                || inputTab7HeaderCsvKanbanNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // 工程順名
        if (inputTab7HeaderCsvWorkflowNames.isEmpty()
                || inputTab7HeaderCsvWorkflowNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }

        return true;
    }

    /**
     * カンバン情報タブの全ての入力欄を通常表示にする。
     */
    private void setKanbanTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvLineStart);       // 読み込み開始行

        // カンバン階層
        ((GridPane) this.inputTab7HeaderCsvKanbanHierarchyPain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvDelimiter1);      // カンバン階層（区切り文字）

        // カンバン名
        ((GridPane) this.inputTab7HeaderCsvKanbanNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvDelimiter2);      // カンバン名（区切り文字）

        // 工程順名
        ((GridPane) this.inputTab7HeaderCsvWorkflowNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvDelimiter3);      // 工程順名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvWorkflowRev);     // 工程順リビジョン

        // モデル名
        ((GridPane) this.inputTab7HeaderCsvModelNamePain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvDelimiter4);      // モデル名（区切り文字）

        // 製造番号
        ((GridPane) this.inputTab7HeaderCsvProductNumPain.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvDelimiter5);      // 製造番号（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab7HeaderCsvStartDate);       // 開始予定日
        ProductionNaviUtils.setFieldNormal(this.inputTab7ProductionTypeValue);      // 生産タイプ
        ProductionNaviUtils.setFieldNormal(this.inputTab7LotNumValue);              // ロット数量
        ProductionNaviUtils.setFieldNormal(this.inputTab7KanbanInitStatusValue);    // カンバンステータス（初期値）
    }

    /**
     * カンバンプロパティ情報の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckKanbanPropTab() {

        // 入力欄を通常表示にする。
        this.setKanbanPropTabTextFieldNormal();

        // ファイル名が入力されている場合
        if (StringUtils.isEmpty(this.inputTab8HeaderCsvFileName.getText())) {
            return true;
        }
        // エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab8HeaderCsvEncode)) {
            return false;
        }
        // ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab8HeaderCsvHeaderRow)) {
            return false;
        }
        // 読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab8HeaderCsvLineStart)) {
            return false;
        }
        // カンバン名
        if (inputTab8HeaderCsvKanbanNames.isEmpty()
                || inputTab8HeaderCsvKanbanNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        // 工程順名
        if (inputTab8HeaderCsvWorkflowNames.isEmpty()
                || inputTab8HeaderCsvWorkflowNames
                .stream()
                .map(StringExpression::getValue)
                .allMatch(String::isEmpty)) {
            return false;
        }
        return true;
    }

    /**
     * カンバンプロパティ情報タブの全ての入力欄を通常表示にする。
     */
    private void setKanbanPropTabTextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvEncode);          // エンコード
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvFileName);        // ファイル名
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvHeaderRow);       // ヘッダー行
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvLineStart);       // 読み込み開始行
        // カンバン名
        ((GridPane) this.inputTab8HeaderCsvKanbanNamePane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvDelimiter1);      // カンバン名（区切り文字）
        // 工程順名
        ((GridPane) this.inputTab8HeaderCsvWorkflowNamePane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvDelimiter2);      // 工程順名（区切り文字）
        ProductionNaviUtils.setFieldNormal(this.inputTab8HeaderCsvWorkflowRev);     // 工程順リビジョン
        ((GridPane) this.inputTab8HeaderCsvPropertyPane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof TextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
    }

    /**
     * 画面の情報からインポートフォーマット設定を作成する。
     *
     * @return インポートフォーマット設定
     */
    private ImportHeaderFormatInfo createImportHeaderFormatInfo() {
        ImportHeaderFormatInfo importHeaderFormatInfo = new ImportHeaderFormatInfo();
        try {
            // 工程情報
            importHeaderFormatInfo.setWorkHeaderFormatInfo(this.createWorkHeaderFormatInfo());
            // 工程プロパティ情報
            importHeaderFormatInfo.setWorkPropHeaderFormatInfo(this.createWorkPropHeaderFormatInfo());
            // 工程順情報
            importHeaderFormatInfo.setWorkflowHeaderFormatInfo(this.createWorkflowHeaderFormatInfo());
            // 工程順プロパティ情報
            importHeaderFormatInfo.setWorkflowPropHeaderFormatInfo(this.createWorkflowPropHeaderFormatInfo());
            // 工程カンバン情報
            importHeaderFormatInfo.setWorkKanbanHeaderFormatInfo(this.createWorkKanbanHeaderFormatInfo());
            // 工程カンバンプロパティ情報
            importHeaderFormatInfo.setWorkKanbanPropHeaderFormatInfo(this.createWorkKanbanPropHeaderFormatInfo());
            // カンバン情報
            importHeaderFormatInfo.setKanbanHeaderFormatInfo(this.createKanbanHeaderFormatInfo());
            // カンバンプロパティ情報
            importHeaderFormatInfo.setKanbanPropHeaderFormatInfo(this.createKanbanPropHeaderFormatInfo());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return importHeaderFormatInfo;
    }

    /**
     * 画面の情報から工程情報のフォーマット情報を作成する。
     *
     * @return 工程情報のフォーマット情報
     */
    private WorkHeaderFormatInfo createWorkHeaderFormatInfo() {
        WorkHeaderFormatInfo workHeaderFormatInfo = new WorkHeaderFormatInfo();

        // エンコード
        workHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab1HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        workHeaderFormatInfo.setHeaderCsvFileName(this.inputTab1HeaderCsvFileName.getText());
        // ヘッダー行
        workHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab1HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        workHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab1HeaderCsvLineStart.getText());
        // 工程階層
        workHeaderFormatInfo
                .setHeaderCsvProcessHierarchyNames(
                        this.inputTab1HeaderCsvProcessHierarchyNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程階層（区切り文字）
        workHeaderFormatInfo.setHeaderCsvHierarchyDelimiter(this.inputTab1HeaderCsvDelimiter1.getText());
        // 工程名
        workHeaderFormatInfo
                .setHeaderCsvProcessNames(
                        this.inputTab1HeaderCsvProcessNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程名（区切り文字）
        workHeaderFormatInfo.setHeaderCsvProcessDelimiter(this.inputTab1HeaderCsvDelimiter2.getText());
        // タクトタイム
        workHeaderFormatInfo.setHeaderCsvTactTime(this.inputTab1HeaderCsvTactTime.getText());
        // 単位（タクトタイム）
        workHeaderFormatInfo.setHeaderCsvTactTimeUnit(this.inputTab1HeaderCsvTactTimeUnit.getSelectionModel().getSelectedItem());
        // 作業内容
        workHeaderFormatInfo.setHeaderCsvWorkContent1(this.inputTab1HeaderCsvWorkContent1.getText());
        workHeaderFormatInfo.setHeaderCsvWorkContent2(this.inputTab1HeaderCsvWorkContent2.getText());
        workHeaderFormatInfo.setHeaderCsvWorkContent3(this.inputTab1HeaderCsvWorkContent3.getText());

        return workHeaderFormatInfo;
    }

    /**
     * 画面の情報から工程プロパティ情報のフォーマット情報を作成する。
     *
     * @return 工程プロパティ情報のフォーマット情報
     */
    private WorkPropHeaderFormatInfo createWorkPropHeaderFormatInfo() {
        WorkPropHeaderFormatInfo workPropHeaderFormatInfo = new WorkPropHeaderFormatInfo();

        // エンコード
        workPropHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab2HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        workPropHeaderFormatInfo.setHeaderCsvFileName(this.inputTab2HeaderCsvFileName.getText());
        // ヘッダー行
        workPropHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab2HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        workPropHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab2HeaderCsvLineStart.getText());
        // 工程名
        workPropHeaderFormatInfo
                .setHeaderCsvProcessNames(
                        this.inputTab2HeaderCsvProcessNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程名（区切り文字）
        workPropHeaderFormatInfo.setHeaderCsvProcessDelimiter(this.inputTab2HeaderCsvDelimiter1.getText());
        // プロパティ
        workPropHeaderFormatInfo.getHeaderCsvPropValues().clear();
        for (PropHeaderFormatInfo prop : this.headerCsvWorkPropInfos) {
            workPropHeaderFormatInfo.getHeaderCsvPropValues().add(new PropHeaderFormatInfo(prop.getPropValue(), prop.getPropName(), prop.getPropertyType()));
        }

        return workPropHeaderFormatInfo;
    }

    /**
     * 画面の情報から工程順情報のフォーマット情報を作成する。
     *
     * @return 工程順情報のフォーマット情報
     */
    private WorkflowHeaderFormatInfo createWorkflowHeaderFormatInfo() {
        WorkflowHeaderFormatInfo workflowHeaderFormatInfo = new WorkflowHeaderFormatInfo();

        // エンコード
        workflowHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab3HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        workflowHeaderFormatInfo.setHeaderCsvFileName(this.inputTab3HeaderCsvFileName.getText());
        // ヘッダー行
        workflowHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab3HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        workflowHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab3HeaderCsvLineStart.getText());
        // 工程順階層
        workflowHeaderFormatInfo
                .setHeaderCsvWorkflowHierarchyNames(
                        this.inputTab3HeaderCsvWorkflowHierarchyNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順階層（区切り文字）
        workflowHeaderFormatInfo.setHeaderCsvHierarchyDelimiter(this.inputTab3HeaderCsvDelimiter1.getText());
        // 工程順名
        workflowHeaderFormatInfo
                .setHeaderCsvWorkflowNames(
                        this.inputTab3HeaderCsvWorkflowNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順名（区切り文字）
        workflowHeaderFormatInfo.setHeaderCsvWorkflowDelimiter(this.inputTab3HeaderCsvDelimiter2.getText());
        // モデル名
        workflowHeaderFormatInfo
                .setHeaderCsvModelNames(
                        this.inputTab3HeaderCsvModelNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // モデル名（区切り文字）
        workflowHeaderFormatInfo.setHeaderCsvModelDelimiter(this.inputTab3HeaderCsvDelimiter3.getText());
        // 工程名
        workflowHeaderFormatInfo
                .setHeaderCsvProcessNames(
                        this.inputTab3HeaderCsvProcessNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程名（区切り文字）
        workflowHeaderFormatInfo.setHeaderCsvProcessNameDelimiter(this.inputTab3HeaderCsvDelimiter4.getText());
        // 組織
        workflowHeaderFormatInfo.setHeaderCsvOrganization(this.inputTab3HeaderCsvOrganization.getText());
        // 設備
        workflowHeaderFormatInfo.setHeaderCsvEquipment(this.inputTab3HeaderCsvEquipment.getText());
        // 工程の並び順
        workflowHeaderFormatInfo.setHeaderCsvProcOrder(this.inputTab3HeaderCsvProcOrder.getText());
        // 工程接続
        workflowHeaderFormatInfo.setHeaderCsvProcCon(
                Objects.equals(this.inputTab3HeaderCsvProcCon.getSelectionModel().getSelectedItem(), LocaleUtils.getString(WorkflowHeaderFormatInfo.PROCESS_TYPE.SERIAL.name))
                ? WorkflowHeaderFormatInfo.PROCESS_TYPE.SERIAL
                : WorkflowHeaderFormatInfo.PROCESS_TYPE.PARALLEL);
        // 完了工程を含めてリスケジュールするか？
        workflowHeaderFormatInfo.setIsReschedule(StringUtils.equals(this.inputTab3IsRescheduleValue.getSelectionModel().getSelectedItem(), LocaleUtils.getString("key.Yes")));

        return workflowHeaderFormatInfo;
    }

    /**
     * 画面の情報から工程順プロパティ情報のフォーマット情報を作成する。
     *
     * @return 工程順プロパティ情報のフォーマット情報
     */
    private WorkflowPropHeaderFormatInfo createWorkflowPropHeaderFormatInfo() {
        WorkflowPropHeaderFormatInfo workflowPropHeaderFormatInfo = new WorkflowPropHeaderFormatInfo();

        // エンコード
        workflowPropHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab4HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        workflowPropHeaderFormatInfo.setHeaderCsvFileName(this.inputTab4HeaderCsvFileName.getText());
        // ヘッダー行
        workflowPropHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab4HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        workflowPropHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab4HeaderCsvLineStart.getText());
        // 工程順名
        workflowPropHeaderFormatInfo
                .setHeaderCsvWorkflowNames(
                        this.inputTab4HeaderCsvWorkflowNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順名（区切り文字）
        workflowPropHeaderFormatInfo.setHeaderCsvWorkflowDelimiter(this.inputTab4HeaderCsvDelimiter1.getText());
        // プロパティ
        workflowPropHeaderFormatInfo.getHeaderCsvPropValues().clear();
        for (PropHeaderFormatInfo prop : this.headerCsvWorkflowPropInfos) {
            workflowPropHeaderFormatInfo.getHeaderCsvPropValues().add(new PropHeaderFormatInfo(prop.getPropValue(), prop.getPropName(), prop.getPropertyType()));
        }

        return workflowPropHeaderFormatInfo;
    }

    /**
     * 画面の情報から工程カンバンのフォーマット情報を作成する。
     *
     * @return 工程カンバンのフォーマット情報
     */
    private WorkKanbanHeaderFormatInfo createWorkKanbanHeaderFormatInfo() {
        WorkKanbanHeaderFormatInfo workKanbanHeaderFormatInfo = new WorkKanbanHeaderFormatInfo();

        // エンコード
        workKanbanHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab5HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        workKanbanHeaderFormatInfo.setHeaderCsvFileName(this.inputTab5HeaderCsvFileName.getText());
        // ヘッダー行
        workKanbanHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab5HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        workKanbanHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab5HeaderCsvLineStart.getText());
        // カンバン名
        workKanbanHeaderFormatInfo
                .setHeaderCsvKanbanNames(
                        this.inputTab5HeaderCsvKanbanNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // カンバン名（区切り文字）
        workKanbanHeaderFormatInfo.setHeaderCsvKanbanDelimiter(this.inputTab5HeaderCsvDelimiter1.getText());
        // 工程順名
        workKanbanHeaderFormatInfo
                .setHeaderCsvWorkflowNames(
                        this.inputTab5HeaderCsvWorkflowNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順名（区切り文字）
        workKanbanHeaderFormatInfo.setHeaderCsvWorkflowDelimiter(this.inputTab5HeaderCsvDelimiter2.getText());
        // 工程順リビジョン
        workKanbanHeaderFormatInfo.setHeaderCsvWorkflowRev(this.inputTab5HeaderCsvWorkflowRev.getText());
        // 工程名
        workKanbanHeaderFormatInfo
                .setHeaderCsvWorkNames(
                        this.inputTab5HeaderCsvWorkNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程名（区切り文字）
        workKanbanHeaderFormatInfo.setHeaderCsvWorkDelimiter(this.inputTab5HeaderCsvDelimiter3.getText());
        // タクトタイム
        workKanbanHeaderFormatInfo.setHeaderCsvTactTime(this.inputTab5HeaderCsvTactTime.getText());
        // 単位(タクトタイム)
        workKanbanHeaderFormatInfo.setHeaderCsvTactTimeUnit(this.inputTab5HeaderCsvTactTimeUnit.getSelectionModel().getSelectedItem());
        // 開始予定日
        workKanbanHeaderFormatInfo.setHeaderCsvStartDateTime(this.inputTab5HeaderCsvStartDate.getText());
        // 完了予定日
        workKanbanHeaderFormatInfo.setHeaderCsvEndDateTime(this.inputTab5HeaderCsvEndDate.getText());
        // 組織
        workKanbanHeaderFormatInfo.setHeaderCsvOrganization(this.inputTab5HeaderCsvOrganization.getText());
        // 設備
        workKanbanHeaderFormatInfo.setHeaderCsvEquipment(this.inputTab5HeaderCsvEquipment.getText());
        return workKanbanHeaderFormatInfo;
    }

    /**
     * 画面の情報から工程カンバンプロパティのフォーマット情報を作成する。
     *
     * @return 工程カンバンプロパティのフォーマット情報
     */
    private WorkKanbanPropHeaderFormatInfo createWorkKanbanPropHeaderFormatInfo() {
        WorkKanbanPropHeaderFormatInfo workKanbanPropHeaderFormatInfo = new WorkKanbanPropHeaderFormatInfo();

        // エンコード
        workKanbanPropHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab6HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        workKanbanPropHeaderFormatInfo.setHeaderCsvFileName(this.inputTab6HeaderCsvFileName.getText());
        // ヘッダー行
        workKanbanPropHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab6HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        workKanbanPropHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab6HeaderCsvLineStart.getText());
        // カンバン名
        workKanbanPropHeaderFormatInfo
                .setHeaderCsvKanbanNames(
                        this.inputTab6HeaderCsvKanbanNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // カンバン名（区切り文字）
        workKanbanPropHeaderFormatInfo.setHeaderCsvKanbanDelimiter(this.inputTab6HeaderCsvDelimiter1.getText());
        // 工程順名
        workKanbanPropHeaderFormatInfo
                .setHeaderCsvWorkflowNames(
                        this.inputTab6HeaderCsvWorkflowNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順名（区切り文字）
        workKanbanPropHeaderFormatInfo.setHeaderCsvWorkflowDelimiter(this.inputTab6HeaderCsvDelimiter2.getText());
        // 工程順リビジョン
        workKanbanPropHeaderFormatInfo.setHeaderCsvWorkflowRev(this.inputTab6HeaderCsvWorkflowRev.getText());
        // 工程名
        workKanbanPropHeaderFormatInfo
                .setHeaderCsvWorkNames(
                        this.inputTab6HeaderCsvWorkNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程名（区切り文字）
        workKanbanPropHeaderFormatInfo.setHeaderCsvWorkDelimiter(this.inputTab6HeaderCsvDelimiter2.getText());
        // プロパティ
        workKanbanPropHeaderFormatInfo.getHeaderCsvPropValues().clear();
        for (PropHeaderFormatInfo prop : this.headerCsvWorkKanbanPropInfos) {
            workKanbanPropHeaderFormatInfo.getHeaderCsvPropValues().add(new PropHeaderFormatInfo(prop.getPropValue(), prop.getPropName(), prop.getPropertyType()));
        }

        return workKanbanPropHeaderFormatInfo;
    }


    /**
     * 画面の情報からカンバンのフォーマット情報を作成する。
     *
     * @return カンバンのフォーマット情報
     */
    private KanbanHeaderFormatInfo createKanbanHeaderFormatInfo() {
        KanbanHeaderFormatInfo kanbanHeaderFormatInfo = new KanbanHeaderFormatInfo();

        // エンコード
        kanbanHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab7HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        kanbanHeaderFormatInfo.setHeaderCsvFileName(this.inputTab7HeaderCsvFileName.getText());
        // ヘッダー行
        kanbanHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab7HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        kanbanHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab7HeaderCsvLineStart.getText());
        // カンバン階層
        kanbanHeaderFormatInfo
                .setHeaderCsvKanbanHierarchyNames(
                        this.inputTab7HeaderCsvKanbanHierarchyNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // カンバン階層（区切り文字）
        kanbanHeaderFormatInfo.setHeaderCsvHierarchyDelimiter(this.inputTab7HeaderCsvDelimiter1.getText());
        // カンバン名
        kanbanHeaderFormatInfo
                .setHeaderCsvKanbanNames(
                        this.inputTab7HeaderCsvKanbanNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // カンバン名（区切り文字）
        kanbanHeaderFormatInfo.setHeaderCsvKanbanDelimiter(this.inputTab7HeaderCsvDelimiter2.getText());
        // 工程順名
        kanbanHeaderFormatInfo
                .setHeaderCsvWorkflowNames(
                        this.inputTab7HeaderCsvWorkflowNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順名（区切り文字）
        kanbanHeaderFormatInfo.setHeaderCsvWorkflowDelimiter(this.inputTab7HeaderCsvDelimiter3.getText());
        // 工程順リビジョン
        kanbanHeaderFormatInfo.setHeaderCsvWorkflowRev(this.inputTab7HeaderCsvWorkflowRev.getText());
        // モデル名
        kanbanHeaderFormatInfo
                .setHeaderCsvModelNames(
                        this.inputTab7HeaderCsvModelNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // モデル名（区切り文字）
        kanbanHeaderFormatInfo.setHeaderCsvModelDelimiter(this.inputTab7HeaderCsvDelimiter4.getText());
        // 製造番号
        kanbanHeaderFormatInfo
                .setHeaderCsvProductNumNames(
                        this.inputTab7HeaderCsvProductNumNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 製造番号（区切り文字）
        kanbanHeaderFormatInfo.setHeaderCsvProductDelimiter(this.inputTab7HeaderCsvDelimiter5.getText());
        // 開始予定日
        kanbanHeaderFormatInfo.setHeaderCsvStartDateTime(this.inputTab7HeaderCsvStartDate.getText());
        // 生産タイプ
        kanbanHeaderFormatInfo.setHeaderCsvProductionType(this.inputTab7ProductionTypeValue.getText());
        // ロット数
        kanbanHeaderFormatInfo.setHeaderCsvLotNum(this.inputTab7LotNumValue.getText());
        // カンバンステータス（初期値）
        final String status = this.inputTab7KanbanInitStatusValue.getSelectionModel().getSelectedItem();
        Arrays.stream(KanbanStatusEnum.values())
                .filter(item -> StringUtils.equals(LocaleUtils.getString(item.getResourceKey()), status))
                .findFirst()
                .ifPresent(kanbanHeaderFormatInfo::setKanbanInitStatus);

        return kanbanHeaderFormatInfo;
    }

    /**
     * 画面の情報からカンバンプロパティのフォーマット情報を作成する。
     *
     * @return カンバンプロパティのフォーマット情報
     */
    private KanbanPropHeaderFormatInfo createKanbanPropHeaderFormatInfo() {
        KanbanPropHeaderFormatInfo kanbanPropHeaderFormatInfo = new KanbanPropHeaderFormatInfo();

        // エンコード
        kanbanPropHeaderFormatInfo.setHeaderCsvFileEncode(this.inputTab8HeaderCsvEncode.getSelectionModel().getSelectedItem());
        // ファイル名
        kanbanPropHeaderFormatInfo.setHeaderCsvFileName(this.inputTab8HeaderCsvFileName.getText());
        // ヘッダー行
        kanbanPropHeaderFormatInfo.setHeaderCsvHeaderRow(this.inputTab8HeaderCsvHeaderRow.getText());
        // 読み込み開始行
        kanbanPropHeaderFormatInfo.setHeaderCsvStartRow(this.inputTab8HeaderCsvLineStart.getText());
        // カンバン名
        kanbanPropHeaderFormatInfo
                .setHeaderCsvKanbanNames(
                        this.inputTab8HeaderCsvKanbanNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // カンバン名（区切り文字）
        kanbanPropHeaderFormatInfo.setHeaderCsvKanbanDelimiter(this.inputTab8HeaderCsvDelimiter1.getText());
        // 工程順名
        kanbanPropHeaderFormatInfo
                .setHeaderCsvWorkflowNames(
                        this.inputTab8HeaderCsvWorkflowNames
                                .stream()
                                .map(StringExpression::getValue)
                                .collect(toList()));
        // 工程順名（区切り文字）
        kanbanPropHeaderFormatInfo.setHeaderCsvWorkflowDelimiter(this.inputTab8HeaderCsvDelimiter2.getText());
        // 工程順リビジョン
        kanbanPropHeaderFormatInfo.setHeaderCsvWorkflowRev(this.inputTab8HeaderCsvWorkflowRev.getText());
        // プロパティ
        kanbanPropHeaderFormatInfo.getHeaderCsvPropValues().clear();
        for (PropHeaderFormatInfo prop : this.headerCsvKanbanPropInfos) {
            kanbanPropHeaderFormatInfo.getHeaderCsvPropValues().add(new PropHeaderFormatInfo(prop.getPropValue(), prop.getPropName(), prop.getPropertyType()));
        }

        return kanbanPropHeaderFormatInfo;
    }

    /**
     * 設定情報読み込み処理
     */
    private void loadSetting() {
        logger.info("loadSetting");
        try {
            blockUI(true);

            Task task = new Task<ImportHeaderFormatInfo>() {
                @Override
                protected ImportHeaderFormatInfo call() throws Exception {
                    return ImportHeaderFormatFileUtil.load();
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        updateView(this.getValue());
                    } finally {
                        blockUI(false);
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    if (Objects.nonNull(this.getException())) {
                        logger.fatal(this.getException(), this.getException());
                    }
                    blockUI(false);
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            blockUI(false);
        }
    }

    /**
     * アイテムを初期化する。
     */
    private void initItem() {
        try {
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);
            // エンコード
            String encodeDatas = properties.getProperty(ProductionNaviPropertyConstants.KEY_MASTER_CSV_FILE_ENCODE, ProductionNaviPropertyConstants.MASTER_CSV_FILE_ENCODE);
            if (!encodeDatas.isEmpty()) {
                String[] buff = encodeDatas.split(",");
                for (String encodeData : buff) {
                    this.inputTab1HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab2HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab3HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab4HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab5HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab6HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab7HeaderCsvEncode.getItems().add(encodeData);
                    this.inputTab8HeaderCsvEncode.getItems().add(encodeData);
                }
            }
            // 工程情報タブ_単位
            this.inputTab1HeaderCsvTactTimeUnit.getItems().add(LocaleUtils.getString("key.time.second"));
            this.inputTab1HeaderCsvTactTimeUnit.getItems().add(LocaleUtils.getString("key.time.minute"));
            // 工程カンバン情報タブ_単位
            this.inputTab5HeaderCsvTactTimeUnit.getItems().add(LocaleUtils.getString("key.time.second"));
            this.inputTab5HeaderCsvTactTimeUnit.getItems().add(LocaleUtils.getString("key.time.minute"));
            // 工程順情報タブ_工程接続
            this.inputTab3HeaderCsvProcCon.getItems().add(LocaleUtils.getString("key.Series"));
            this.inputTab3HeaderCsvProcCon.getItems().add(LocaleUtils.getString("key.Parallel"));
            // 工程順情報_完了工程を含めてリスケジュールするか？
            this.inputTab3IsRescheduleValue.getItems().add(LocaleUtils.getString("key.Yes"));
            this.inputTab3IsRescheduleValue.getItems().add(LocaleUtils.getString("key.No"));
            // カンバン情報_カンバンステータス
            Arrays.stream(KanbanStatusEnum.values())
                    .map(KanbanStatusEnum::getResourceKey)
                    .filter(p -> Objects.equals(KanbanStatusEnum.PLANNING.getResourceKey(), p) || Objects.equals(KanbanStatusEnum.PLANNED.getResourceKey(), p))
                    .forEach(status -> this.inputTab7KanbanInitStatusValue.getItems().add(LocaleUtils.getString(status)));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面を更新する。
     *
     * @param importHeaderFormatInfo インポートフォーマット情報
     */
    private void updateView(ImportHeaderFormatInfo importHeaderFormatInfo) {
        // 工程情報タブ
        this.dispWorkHeaderFormatInfo(importHeaderFormatInfo.getWorkHeaderFormatInfo());
        // 工程プロパティ情報タブ
        this.dispWorkPropHeaderFormatInfo(importHeaderFormatInfo.getWorkPropHeaderFormatInfo());
        this.initWorkPropHeaderRecord();
        // 工程順情報タブ
        this.dispWorkflowHeaderFormatInfo(importHeaderFormatInfo.getWorkflowHeaderFormatInfo());
        // 工程順プロパティタブ
        this.dispWorkflowPropHeaderFormatInfo(importHeaderFormatInfo.getWorkflowPropHeaderFormatInfo());
        this.initWorkflowPropHeaderRecord();
        // 工程カンバン情報タブ
        this.dispWorkKanbanHeaderFormatInfo(importHeaderFormatInfo.getWorkKanbanHeaderFormatInfo());
        // 工程カンバンプロパティ情報タブ
        this.dispWorkKanbanPropHeaderFormatInfo(importHeaderFormatInfo.getWorkKanbanPropHeaderFormatInfo());
        this.initWorkKanbanPropHeaderRecord();
        // カンバン情報タブ
        this.dispKanbanHeaderFormatInfo(importHeaderFormatInfo.getKanbanHeaderFormatInfo());
        // カンバンプロパティ情報タブ
        this.dispKanbanPropHeaderFormatInfo(importHeaderFormatInfo.getKanbanPropHeaderFormatInfo());
        this.initKanbanPropHeaderRecord();
    }

    /**
     * 工程情報タブの表示を更新する。
     *
     * @param workHeaderFormatInfo 工程のフォーマット情報
     */
    private void dispWorkHeaderFormatInfo(WorkHeaderFormatInfo workHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab1HeaderCsvEncode.setValue(workHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab1HeaderCsvFileName.setText(workHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab1HeaderCsvHeaderRow.setText(workHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab1HeaderCsvLineStart.setText(workHeaderFormatInfo.getHeaderCsvStartRow());
            // 工程階層
            this.inputTab1HeaderCsvProcessHierarchyNames
                    = workHeaderFormatInfo
                    .getHeaderCsvProcessHierarchyNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab1HeaderCsvHierarchyPain, this.inputTab1HeaderCsvProcessHierarchyNames);
            // 工程階層（区切り文字）
            this.inputTab1HeaderCsvDelimiter1.setText(workHeaderFormatInfo.getHeaderCsvHierarchyDelimiter());
            // 工程名
            this.inputTab1HeaderCsvProcessNames
                    = workHeaderFormatInfo
                    .getHeaderCsvProcessNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab1HeaderCsvProcessNamePain, this.inputTab1HeaderCsvProcessNames);
            // 工程名（区切り文字）
            this.inputTab1HeaderCsvDelimiter2.setText(workHeaderFormatInfo.getHeaderCsvProcessDelimiter());
            // タクトタイム
            this.inputTab1HeaderCsvTactTime.setText(workHeaderFormatInfo.getHeaderCsvTactTime());
            // 単位
            this.inputTab1HeaderCsvTactTimeUnit.setValue(workHeaderFormatInfo.getHeaderCsvTactTimeUnit());
            // 作業内容
            this.inputTab1HeaderCsvWorkContent1.setText(workHeaderFormatInfo.getHeaderCsvWorkContent1());
            this.inputTab1HeaderCsvWorkContent2.setText(workHeaderFormatInfo.getHeaderCsvWorkContent2());
            this.inputTab1HeaderCsvWorkContent3.setText(workHeaderFormatInfo.getHeaderCsvWorkContent3());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程プロパティ情報タブの表示を更新する。
     *
     * @param workPropHeaderFormatInfo 工程プロパティのフォーマット情報
     */
    private void dispWorkPropHeaderFormatInfo(WorkPropHeaderFormatInfo workPropHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab2HeaderCsvEncode.setValue(workPropHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab2HeaderCsvFileName.setText(workPropHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab2HeaderCsvHeaderRow.setText(workPropHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab2HeaderCsvLineStart.setText(workPropHeaderFormatInfo.getHeaderCsvStartRow());
            // 工程名
            this.inputTab2HeaderCsvProcessNames
                    = workPropHeaderFormatInfo
                    .getHeaderCsvProcessNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab2HeaderCsvProcessNamePain, this.inputTab2HeaderCsvProcessNames);

            // 工程名（区切り文字）
            this.inputTab2HeaderCsvDelimiter1.setText(workPropHeaderFormatInfo.getHeaderCsvProcessDelimiter());
            // プロパティ
            this.headerCsvWorkPropInfos.clear();
            for (PropHeaderFormatInfo value : workPropHeaderFormatInfo.getHeaderCsvPropValues()) {
                this.headerCsvWorkPropInfos.add(value);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void initRecord(VBox pane, LinkedList<SimpleStringProperty> headerCsvPropInfo) {
        try {
            pane.getChildren().clear();

            Table table
                    = new Table(pane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(true)
                    .isAddRecord(true)
                    .maxRecord(99);

            HeaderTextFieldRecordFactory recordFactory
                    = new HeaderTextFieldRecordFactory(table, headerCsvPropInfo);

            table.setAbstractRecordFactory(recordFactory);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程プロパティペイン初期化
     */
    private void initWorkPropHeaderRecord() {
        try {
            this.inputTab2HeaderCsvPropertyPane.getChildren().clear();

            Table table = new Table(this.inputTab2HeaderCsvPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(true)
                    .isAddRecord(true)
                    .maxRecord(99);

            HeaderTextAndComboFieldRecordFactory recordFactory
                    = new HeaderTextAndComboFieldRecordFactory(rb, table, this.headerCsvWorkPropInfos);

            table.setAbstractRecordFactory(recordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順情報タブの表示を更新する。
     *
     * @param workflowHeaderFormatInfo 工程順のフォーマット情報
     */
    private void dispWorkflowHeaderFormatInfo(WorkflowHeaderFormatInfo workflowHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab3HeaderCsvEncode.setValue(workflowHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab3HeaderCsvFileName.setText(workflowHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab3HeaderCsvHeaderRow.setText(workflowHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab3HeaderCsvLineStart.setText(workflowHeaderFormatInfo.getHeaderCsvStartRow());
            // 工程順階層
            this.inputTab3HeaderCsvWorkflowHierarchyNames
                    = workflowHeaderFormatInfo
                    .getHeaderCsvWorkflowHierarchyNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab3HeaderCsvWorkflowHierarchyPain, this.inputTab3HeaderCsvWorkflowHierarchyNames);
            // 工程順階層（区切り文字）
            this.inputTab3HeaderCsvDelimiter1.setText(workflowHeaderFormatInfo.getHeaderCsvHierarchyDelimiter());
            // 工程順名
            this.inputTab3HeaderCsvWorkflowNames
                    = workflowHeaderFormatInfo
                    .getHeaderCsvWorkflowNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab3HeaderCsvWorkflowNamePain, this.inputTab3HeaderCsvWorkflowNames);
            // 工程順名（区切り文字）
            this.inputTab3HeaderCsvDelimiter2.setText(workflowHeaderFormatInfo.getHeaderCsvWorkflowDelimiter());
            // モデル名
            this.inputTab3HeaderCsvModelNames
                    = workflowHeaderFormatInfo
                    .getHeaderCsvModelNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab3HeaderCsvModelNamePain, this.inputTab3HeaderCsvModelNames);
            // モデル名（区切り文字）
            this.inputTab3HeaderCsvDelimiter3.setText(workflowHeaderFormatInfo.getHeaderCsvModelDelimiter());
            // 工程名
            this.inputTab3HeaderCsvProcessNames
                    = workflowHeaderFormatInfo
                    .getHeaderCsvProcessNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab3HeaderCsvProcessNamePain, this.inputTab3HeaderCsvProcessNames);
            // 工程名（区切り文字）
            this.inputTab3HeaderCsvDelimiter4.setText(workflowHeaderFormatInfo.getHeaderCsvProcessNameDelimiter());
            // 組織
            this.inputTab3HeaderCsvOrganization.setText(workflowHeaderFormatInfo.getHeaderCsvOrganization());
            // 設備
            this.inputTab3HeaderCsvEquipment.setText(workflowHeaderFormatInfo.getHeaderCsvEquipment());
            // 工程の並び順
            this.inputTab3HeaderCsvProcOrder.setText(workflowHeaderFormatInfo.getHeaderCsvProcOrder());
            // 工程接続
            this.inputTab3HeaderCsvProcCon.setValue(LocaleUtils.getString(workflowHeaderFormatInfo.getHeaderCsvProcCon().name));
            // 完了工程を含めてリスケジュールするか?
            if (workflowHeaderFormatInfo.getIsReschedule()) {
                this.inputTab3IsRescheduleValue.setValue(LocaleUtils.getString("key.Yes"));
            } else {
                this.inputTab3IsRescheduleValue.setValue(LocaleUtils.getString("key.No"));
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順プロパティ情報タブの表示を更新する。
     *
     * @param workflowPropHeaderFormatInfo 工程順プロパティのフォーマット情報
     */
    private void dispWorkflowPropHeaderFormatInfo(WorkflowPropHeaderFormatInfo workflowPropHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab4HeaderCsvEncode.setValue(workflowPropHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab4HeaderCsvFileName.setText(workflowPropHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab4HeaderCsvHeaderRow.setText(workflowPropHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab4HeaderCsvLineStart.setText(workflowPropHeaderFormatInfo.getHeaderCsvStartRow());
            // 工程順名
            this.inputTab4HeaderCsvWorkflowNames
                    = workflowPropHeaderFormatInfo
                    .getHeaderCsvWorkflowNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab4HeaderCsvWorkflowNamePain, this.inputTab4HeaderCsvWorkflowNames);
            // 工程順名（区切り文字）
            this.inputTab4HeaderCsvDelimiter1.setText(workflowPropHeaderFormatInfo.getHeaderCsvWorkflowDelimiter());
            // プロパティ
            this.headerCsvWorkflowPropInfos.clear();
            for (PropHeaderFormatInfo value : workflowPropHeaderFormatInfo.getHeaderCsvPropValues()) {
                this.headerCsvWorkflowPropInfos.add(value);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程順プロパティペイン初期化
     */
    private void initWorkflowPropHeaderRecord() {
        try {
            this.inputTab4HeaderCsvPropertyPane.getChildren().clear();

            Table table = new Table(this.inputTab4HeaderCsvPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(true)
                    .isAddRecord(true)
                    .maxRecord(99);

            HeaderTextAndComboFieldRecordFactory recordFactory
                    = new HeaderTextAndComboFieldRecordFactory(rb, table, this.headerCsvWorkflowPropInfos);

            table.setAbstractRecordFactory(recordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }


    /**
     * 工程カンバン情報タブの表示を更新する。
     *
     * @param workKanbanHeaderFormatInfo 工程カンバンのフォーマット情報
     */
    private void dispWorkKanbanHeaderFormatInfo(WorkKanbanHeaderFormatInfo workKanbanHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab5HeaderCsvEncode.setValue(workKanbanHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab5HeaderCsvFileName.setText(workKanbanHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab5HeaderCsvHeaderRow.setText(workKanbanHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab5HeaderCsvLineStart.setText(workKanbanHeaderFormatInfo.getHeaderCsvStartRow());
            // カンバン名
            this.inputTab5HeaderCsvKanbanNames
                    = workKanbanHeaderFormatInfo
                    .getHeaderCsvKanbanNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab5HeaderCsvKanbanNamePain, this.inputTab5HeaderCsvKanbanNames);
            // カンバン名（区切り文字）
            this.inputTab5HeaderCsvDelimiter1.setText(workKanbanHeaderFormatInfo.getHeaderCsvKanbanDelimiter());
            // 工程順名
            this.inputTab5HeaderCsvWorkflowNames
                    = workKanbanHeaderFormatInfo
                    .getHeaderCsvWorkflowNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab5HeaderCsvWorkflowNamePain, this.inputTab5HeaderCsvWorkflowNames);
            // 工程順名（区切り文字）
            this.inputTab5HeaderCsvDelimiter2.setText(workKanbanHeaderFormatInfo.getHeaderCsvWorkflowDelimiter());
            // 工程順リビジョン
            this.inputTab5HeaderCsvWorkflowRev.setText(workKanbanHeaderFormatInfo.getHeaderCsvWorkflowRev());
            // 工程名
            this.inputTab5HeaderCsvWorkNames
                    = workKanbanHeaderFormatInfo
                    .getHeaderCsvWorkNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab5HeaderCsvWorkNamePain, this.inputTab5HeaderCsvWorkNames);
            // 工程名（区切り文字）
            this.inputTab5HeaderCsvDelimiter3.setText(workKanbanHeaderFormatInfo.getHeaderCsvWorkDelimiter());
            // タクトタイム
            this.inputTab5HeaderCsvTactTime.setText(workKanbanHeaderFormatInfo.getHeaderCsvTactTime());
            // 単位
            this.inputTab5HeaderCsvTactTimeUnit.setValue(workKanbanHeaderFormatInfo.getHeaderCsvTactTimeUnit());
            // 開始予定日
            this.inputTab5HeaderCsvStartDate.setText(workKanbanHeaderFormatInfo.getHeaderCsvStartDateTime());
            // 完了予定日
            this.inputTab5HeaderCsvEndDate.setText(workKanbanHeaderFormatInfo.getHeaderCsvEndDateTime());
            // 組織
            this.inputTab5HeaderCsvOrganization.setText(workKanbanHeaderFormatInfo.getHeaderCsvOrganization());
            // 設備
            this.inputTab5HeaderCsvEquipment.setText(workKanbanHeaderFormatInfo.getHeaderCsvEquipment());
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンプロパティ情報タブの表示を更新する。
     *
     * @param workKanbanPropHeaderFormatInfo 工程カンバンプロパティのフォーマット情報
     */
    private void dispWorkKanbanPropHeaderFormatInfo(WorkKanbanPropHeaderFormatInfo workKanbanPropHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab6HeaderCsvEncode.setValue(workKanbanPropHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab6HeaderCsvFileName.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab6HeaderCsvHeaderRow.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab6HeaderCsvLineStart.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvStartRow());
            // カンバン名
            this.inputTab6HeaderCsvKanbanNames
                    = workKanbanPropHeaderFormatInfo
                    .getHeaderCsvKanbanNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab6HeaderCsvKanbanNamePain, this.inputTab6HeaderCsvKanbanNames);
            // カンバン名（区切り文字）
            this.inputTab6HeaderCsvDelimiter1.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvKanbanDelimiter());
            // 工程順名
            this.inputTab6HeaderCsvWorkflowNames
                    = workKanbanPropHeaderFormatInfo
                    .getHeaderCsvWorkflowNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab6HeaderCsvWorkflowNamePain, this.inputTab6HeaderCsvWorkflowNames);
            // 工程順名（区切り文字）
            this.inputTab6HeaderCsvDelimiter3.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvWorkflowDelimiter());
            // 工程順リビジョン
            this.inputTab6HeaderCsvWorkflowRev.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvWorkflowRev());
            // 工程名
            this.inputTab6HeaderCsvWorkNames
                    = workKanbanPropHeaderFormatInfo
                    .getHeaderCsvWorkNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab6HeaderCsvWorkNamePain, this.inputTab6HeaderCsvWorkNames);
            // 工程順名（区切り文字）
            this.inputTab6HeaderCsvDelimiter2.setText(workKanbanPropHeaderFormatInfo.getHeaderCsvWorkDelimiter());

            // プロパティ
            this.headerCsvWorkKanbanPropInfos.clear();
            for (PropHeaderFormatInfo value : workKanbanPropHeaderFormatInfo.getHeaderCsvPropValues()) {
                this.headerCsvWorkKanbanPropInfos.add(value);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンプロパティペイン初期化
     */
    private void initWorkKanbanPropHeaderRecord() {
        try {
            this.inputTab6HeaderCsvPropertyPane.getChildren().clear();

            Table table = new Table(this.inputTab6HeaderCsvPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(true)
                    .isAddRecord(true)
                    .maxRecord(99);

            HeaderTextAndComboFieldRecordFactory recordFactory
                    = new HeaderTextAndComboFieldRecordFactory(rb, table, this.headerCsvWorkKanbanPropInfos);

            table.setAbstractRecordFactory(recordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバン情報タブの表示を更新する。
     *
     * @param kanbanHeaderFormatInfo カンバンのフォーマット情報
     */
    private void dispKanbanHeaderFormatInfo(KanbanHeaderFormatInfo kanbanHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab7HeaderCsvEncode.setValue(kanbanHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab7HeaderCsvFileName.setText(kanbanHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab7HeaderCsvHeaderRow.setText(kanbanHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab7HeaderCsvLineStart.setText(kanbanHeaderFormatInfo.getHeaderCsvStartRow());
            // カンバン階層
            this.inputTab7HeaderCsvKanbanHierarchyNames
                    = kanbanHeaderFormatInfo
                    .getHeaderCsvKanbanHierarchyNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab7HeaderCsvKanbanHierarchyPain, this.inputTab7HeaderCsvKanbanHierarchyNames);
            // カンバン階層（区切り文字）
            this.inputTab7HeaderCsvDelimiter1.setText(kanbanHeaderFormatInfo.getHeaderCsvHierarchyDelimiter());
            // カンバン名
            this.inputTab7HeaderCsvKanbanNames
                    = kanbanHeaderFormatInfo
                    .getHeaderCsvKanbanNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab7HeaderCsvKanbanNamePain, this.inputTab7HeaderCsvKanbanNames);
            // カンバン名（区切り文字）
            this.inputTab7HeaderCsvDelimiter2.setText(kanbanHeaderFormatInfo.getHeaderCsvKanbanDelimiter());
            // 工程順名
            this.inputTab7HeaderCsvWorkflowNames
                    = kanbanHeaderFormatInfo
                    .getHeaderCsvWorkflowNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab7HeaderCsvWorkflowNamePain, this.inputTab7HeaderCsvWorkflowNames);
            // 工程順名（区切り文字）
            this.inputTab7HeaderCsvDelimiter3.setText(kanbanHeaderFormatInfo.getHeaderCsvWorkflowDelimiter());
            // 工程順リビジョン
            this.inputTab7HeaderCsvWorkflowRev.setText(kanbanHeaderFormatInfo.getHeaderCsvWorkflowRev());
            // モデル名
            this.inputTab7HeaderCsvModelNames
                    = kanbanHeaderFormatInfo
                    .getHeaderCsvModelNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab7HeaderCsvModelNamePain, this.inputTab7HeaderCsvModelNames);
            // モデル名（区切り文字）
            this.inputTab7HeaderCsvDelimiter4.setText(kanbanHeaderFormatInfo.getHeaderCsvModelDelimiter());
            // 製造番号
            this.inputTab7HeaderCsvProductNumNames
                    = kanbanHeaderFormatInfo
                    .getHeaderCsvProductNumNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab7HeaderCsvProductNumPain, this.inputTab7HeaderCsvProductNumNames);
            // 製造番号（区切り文字）
            this.inputTab7HeaderCsvDelimiter5.setText(kanbanHeaderFormatInfo.getHeaderCsvProductDelimiter());
            // 開始予定日時
            this.inputTab7HeaderCsvStartDate.setText(kanbanHeaderFormatInfo.getHeaderCsvStartDateTime());
            // 生産タイプ
            this.inputTab7ProductionTypeValue.setText(kanbanHeaderFormatInfo.getHeaderCsvProductionType());
            // ロット数
            this.inputTab7LotNumValue.setText(kanbanHeaderFormatInfo.getHeaderCsvLotNum());
            // カンバンステータス（初期値）
            this.inputTab7KanbanInitStatusValue.setValue(LocaleUtils.getString(kanbanHeaderFormatInfo.getKanbanInitStatus().getResourceKey()));
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンプロパティ情報タブの表示を更新する。
     *
     * @param kanbanPropHeaderFormatInfo カンバンプロパティのフォーマット情報
     */
    private void dispKanbanPropHeaderFormatInfo(KanbanPropHeaderFormatInfo kanbanPropHeaderFormatInfo) {
        try {
            // エンコード
            this.inputTab8HeaderCsvEncode.setValue(kanbanPropHeaderFormatInfo.getHeaderCsvFileEncode());
            // ファイル名
            this.inputTab8HeaderCsvFileName.setText(kanbanPropHeaderFormatInfo.getHeaderCsvFileName());
            // ヘッダー行
            this.inputTab8HeaderCsvHeaderRow.setText(kanbanPropHeaderFormatInfo.getHeaderCsvHeaderRow());
            // 読み込み開始行
            this.inputTab8HeaderCsvLineStart.setText(kanbanPropHeaderFormatInfo.getHeaderCsvStartRow());
            // カンバン名
            this.inputTab8HeaderCsvKanbanNames
                    = kanbanPropHeaderFormatInfo
                    .getHeaderCsvKanbanNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab8HeaderCsvKanbanNamePane, this.inputTab8HeaderCsvKanbanNames);
            // カンバン名（区切り文字）
            this.inputTab8HeaderCsvDelimiter1.setText(kanbanPropHeaderFormatInfo.getHeaderCsvKanbanDelimiter());
            // 工程順名
            this.inputTab8HeaderCsvWorkflowNames
                    = kanbanPropHeaderFormatInfo
                    .getHeaderCsvWorkflowNames()
                    .stream()
                    .map(SimpleStringProperty::new)
                    .collect(toCollection(LinkedList::new));
            this.initRecord(inputTab8HeaderCsvWorkflowNamePane, this.inputTab8HeaderCsvWorkflowNames);
            // 工程順名（区切り文字）
            this.inputTab8HeaderCsvDelimiter2.setText(kanbanPropHeaderFormatInfo.getHeaderCsvWorkflowDelimiter());
            // 工程順リビジョン
            this.inputTab8HeaderCsvWorkflowRev.setText(kanbanPropHeaderFormatInfo.getHeaderCsvWorkflowRev());
            // プロパティ
            this.headerCsvKanbanPropInfos.clear();
            for (PropHeaderFormatInfo value : kanbanPropHeaderFormatInfo.getHeaderCsvPropValues()) {
                this.headerCsvKanbanPropInfos.add(value);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンプロパティペイン初期化
     */
    private void initKanbanPropHeaderRecord() {
        try {
            this.inputTab8HeaderCsvPropertyPane.getChildren().clear();

            Table table = new Table(this.inputTab8HeaderCsvPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(true)
                    .isAddRecord(true)
                    .maxRecord(99);

            HeaderTextAndComboFieldRecordFactory recordFactory
                    = new HeaderTextAndComboFieldRecordFactory(rb, table, this.headerCsvKanbanPropInfos);

            table.setAbstractRecordFactory(recordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
}
