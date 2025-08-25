package adtekfuji.admanagerapp.productionnaviplugin.component;

import adtekfuji.admanagerapp.productionnaviplugin.common.ProductionNaviPropertyConstants;
import adtekfuji.admanagerapp.productionnaviplugin.property.TextFieldRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.property.WorkflowRegexInfoRecordFactory;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ImportFormatFileUtil;
import adtekfuji.admanagerapp.productionnaviplugin.utils.ProductionNaviUtils;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.importformat.ImportFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.KanbanStatusFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.ProductFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkKanbanPropFormatInfo;
import jp.adtekfuji.adFactory.entity.importformat.WorkflowRegexInfo;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 製造情報フォーマット変更画面
 *
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
@FxComponent(id = "WorkPlanFormatChangeCompo", fxmlPath = "/fxml/compo/work_plan_format_compo.fxml")
public class WorkPlanFormatChangeCompoCpntroller implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();

    private static final int INPUT_MAX_SIZE = 3;

    private final LinkedList<WorkflowRegexInfo> workflowRegexInfos = new LinkedList<>();
    private final LinkedList<SimpleStringProperty> csvKanbanPropInfos = new LinkedList<>();
    private final LinkedList<SimpleStringProperty> xlsKanbanPropInfos = new LinkedList<>();
    private final LinkedList<SimpleStringProperty> csvWorkKanbanPropInfos = new LinkedList<>();
    private final LinkedList<SimpleStringProperty> xlsWorkKanbanPropInfos = new LinkedList<>();

    private Object argument = null;

    /**
     * タブ *
     */
    @FXML
    private TabPane tabImportMode;
    /**
     * CSV形式：カンバン：入力エリア：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab1CsvEncode;
    /**
     * CSV形式：カンバン：入力エリア：ファイル名 *
     */
    @FXML
    private TextField inputTab1CsvFileName;
    /**
     * EXCEL形式：カンバン：入力エリア：シート名 *
     */
    @FXML
    private TextField inputTab1ExcelSheetName;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab1CsvLineStart;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab1ExcelLineStart;

    /**
     * CSV形式：カンバン：入力エリア：カンバン階層名 *
     */
    @FXML
    private TextField inputTab1CsvValue1;
    /**
     * EXCEL形式：カンバン：入力エリア：カンバン階層名 *
     */
    @FXML
    private TextField inputTab1ExcelValue1;
    /**
     * CSV形式：カンバン：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab1CsvValue2;
    /**
     * Excel形式：カンバン：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab1ExcelValue2;
    /**
     * CSV形式：カンバン：入力エリア：工程順名 *
     */
    @FXML
    private TextField inputTab1CsvValue3;
    /**
     * Excel形式：カンバン：入力エリア：工程順名 *
     */
    @FXML
    private TextField inputTab1ExcelValue3;
    /**
     * CSV形式：カンバン：入力エリア：工程順版数 *
     */
    @FXML
    private TextField inputTab1CsvValue4;
    /**
     * Excel形式：カンバン：入力エリア：工程順版数 *
     */
    @FXML
    private TextField inputTab1ExcelValue4;
    /**
     * CSV形式：カンバン：入力エリア：モデル名 *
     */
    @FXML
    private TextField inputTab1CsvValue5;
    /**
     * CSV形式：カンバン：入力エリア：モデル名 *
     */
    @FXML
    private TextField inputTab1ExcelValue5;
    /**
     * CSV形式：カンバン：入力エリア：開始予定日時 *
     */
    @FXML
    private TextField inputTab1CsvValue6;
    /**
     * Excel形式：カンバン：入力エリア：開始予定日時 *
     */
    @FXML
    private TextField inputTab1ExcelValue6;
    /**
     * CSV形式：カンバン：入力エリア：生産タイプ *
     */
    @FXML
    private TextField inputTab1CsvValue7;
    /**
     * Excel形式：カンバン：入力エリア：生産タイプ *
     */
    @FXML
    private TextField inputTab1ExcelValue7;
    /**
     * CSV形式：カンバン：入力エリア：ロット数量 *
     */
    @FXML
    private TextField inputTab1CsvValue8;
    /**
     * Excel形式：カンバン：入力エリア：ロット数量 *
     */
    @FXML
    private TextField inputTab1ExcelValue8;
    /**
     * CSV形式：カンバン：入力エリア：製造番号
     */
    @FXML
    private TextField inputTab1CsvValue9;
    /**
     * EXCEL形式：カンバン：入力エリア：製造番号
     */
    @FXML
    private TextField inputTab1ExcelValue9;
    /**
     * CSV形式：カンバン：入力エリア：開始シリアル番号
     */
    @FXML
    private TextField inputTab1CsvValue10;
    /**
     * EXCEL形式：カンバン：入力エリア：終了シリアル番号
     */
    @FXML
    private TextField inputTab1ExcelValue10;
    /**
     * CSV形式：カンバン：入力エリア：終了シリアル番号
     */
    @FXML
    private TextField inputTab1CsvValue11;
    /**
     * EXCEL形式：カンバン：入力エリア：終了シリアル番号
     */
    @FXML
    private TextField inputTab1ExcelValue11;

    // 標準作業時間 イトーキ様 カスタム対応、enableCycleTimeImport=true の場合のみに表示
    @FXML
    private Label inputTab1Label12;
    @FXML
    private TextField inputTab1CsvValue12;
    @FXML
    private TextField inputTab1ExcelValue12;

    /**
     * CSV形式：カンバンプロパティ：入力エリア：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab2CsvEncode;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：ファイル名 *
     */
    @FXML
    private TextField inputTab2CsvFileName;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：シート名 *
     */
    @FXML
    private TextField inputTab2ExcelSheetName;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab2CsvLineStart;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab2ExcelLineStart;

    /**
     * CSV形式：カンバンプロパティ：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab2CsvValue1;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab2ExcelValue1;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：工程順名 *
     */
    @FXML
    private TextField inputTab2CsvValue2;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：工程順名 *
     */
    @FXML
    private TextField inputTab2ExcelValue2;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：プロパティ名 *
     */
    @FXML
    private TextField inputTab2CsvValue3;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：プロパティ名 *
     */
    @FXML
    private TextField inputTab2ExcelValue3;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：型 *
     */
    @FXML
    private TextField inputTab2CsvValue4;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：型 *
     */
    @FXML
    private TextField inputTab2ExcelValue4;
    /**
     * CSV形式：カンバンプロパティ：入力エリア：値 *
     */
    @FXML
    private TextField inputTab2CsvValue5;
    /**
     * EXCEL形式：カンバンプロパティ：入力エリア：値 *
     */
    @FXML
    private TextField inputTab2ExcelValue5;

    @FXML
    private ToggleGroup formatToggle;
    @FXML
    private ToggleButton format1Button;
    @FXML
    private ToggleButton format2Button;

    @FXML
    private Label tab2F1LineStartLabel;
    @FXML
    private Label tab2F1KanbanNameLabel;
    @FXML
    private Label tab2F1WorkflowNameLabel;
    @FXML
    private Label tab2F1PropNameLabel;
    @FXML
    private Label tab2F1PropTypeLabel;
    @FXML
    private Label tab2F1PropValueLabel;

    @FXML
    private Label tab2F2HeaderRowLabel;
    @FXML
    private Label tab2F2StartRowLabel;
    @FXML
    private Label tab2F2KanbanNameLabel;
    @FXML
    private Label tab2F2WorkflowNameLabel;
    @FXML
    private Label tab2F2PropertyLabel;

    @FXML
    private TextField tab2F2CsvHeaderRow;// CSV形式：ヘッダー行
    @FXML
    private TextField tab2F2XlsHeaderRow;// Excel形式：ヘッダー行
    @FXML
    private TextField tab2F2CsvStartRow;// CSV形式：読み込み開始行
    @FXML
    private TextField tab2F2XlsStartRow;// Excel形式：読み込み開始行
    @FXML
    private TextField tab2F2CsvKanbanName;// CSV形式：カンバン名
    @FXML
    private TextField tab2F2XlsKanbanName;// Excel形式：カンバン名
    @FXML
    private TextField tab2F2CsvWorkflowName;// CSV形式：工程順名
    @FXML
    private TextField tab2F2XlsWorkflowName;// Excel形式：工程順名
    @FXML
    private VBox tab2F2CsvPropertyPane;// CSV形式：プロパティ
    @FXML
    private VBox tab2F2XlsPropertyPane;// Excel形式：プロパティ

    /**
     * CSV形式：工程カンバン：入力エリア：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab3CsvEncode;
    /**
     * CSV形式：工程カンバン：入力エリア：ファイル名 *
     */
    @FXML
    private TextField inputTab3CsvFileName;
    /**
     * EXCEL形式：工程カンバン：入力エリア：シート名 *
     */
    @FXML
    private TextField inputTab3ExcelSheetName;
    /**
     * CSV形式：工程カンバン：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab3CsvLineStart;
    /**
     * EXCEL形式：工程カンバン：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab3ExcelLineStart;

    /**
     * CSV形式：工程カンバン：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab3CsvValue1;
    /**
     * EXCEL形式：工程カンバン：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab3ExcelValue1;
    /**
     * CSV形式：工程カンバン：入力エリア：工程の番号 *
     */
    @FXML
    private TextField inputTab3CsvValue2;
    /**
     * EXCEL形式：工程カンバン：入力エリア：工程の番号 *
     */
    @FXML
    private TextField inputTab3ExcelValue2;
    /**
     * CSV形式：工程カンバン：入力エリア：スキップフラグ *
     */
    @FXML
    private TextField inputTab3CsvValue3;
    /**
     * EXCEL形式：工程カンバン：入力エリア：スキップフラグ *
     */
    @FXML
    private TextField inputTab3ExcelValue3;
    /**
     * CSV形式：工程カンバン：入力エリア：タクトタイム *
     */
    @FXML
    private TextField inputTab3CsvValue9;
    /**
     * EXCEL形式：工程カンバン：入力エリア：タクトタイム *
     */
    @FXML
    private TextField inputTab3ExcelValue9;
    /**
     * CSV形式：工程カンバン：入力エリア：開始予定日時 *
     */
    @FXML
    private TextField inputTab3CsvValue4;
    /**
     * EXCEL形式：工程カンバン：入力エリア：開始予定日時 *
     */
    @FXML
    private TextField inputTab3ExcelValue4;
    /**
     * CSV形式：工程カンバン：入力エリア：完了予定日時 *
     */
    @FXML
    private TextField inputTab3CsvValue5;
    /**
     * EXCEL形式：工程カンバン：入力エリア：完了予定日時 *
     */
    @FXML
    private TextField inputTab3ExcelValue5;
    /**
     * CSV形式：工程カンバン：入力エリア：組織識別名 *
     */
    @FXML
    private TextField inputTab3CsvValue6;
    /**
     * EXCEL形式：工程カンバン：入力エリア：組織識別名 *
     */
    @FXML
    private TextField inputTab3ExcelValue6;
    /**
     * CSV形式：工程カンバン：入力エリア：設備識別名 *
     */
    @FXML
    private TextField inputTab3CsvValue7;
    /**
     * EXCEL形式：工程カンバン：入力エリア：設備識別名 *
     */
    @FXML
    private TextField inputTab3ExcelValue7;
    
    // 2019/12/18 工程名項目の追加対応 START
    /**
     * CSV形式：工程カンバン：入力エリア：工程名 *
     */
    @FXML
    private TextField inputTab3CsvValue8;
    /**
     * EXCEL形式：工程カンバン：入力エリア：工程名 *
     */
    @FXML
    private TextField inputTab3ExcelValue8;
    // 2019/12/18 工程名項目の追加対応 END

    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab4CsvEncode;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：ファイル名 *
     */
    @FXML
    private TextField inputTab4CsvFileName;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：シート名 *
     */
    @FXML
    private TextField inputTab4ExcelSheetName;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab4CsvLineStart;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab4ExcelLineStart;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab4CsvValue1;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab4ExcelValue1;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：工程の番号 *
     */
    @FXML
    private TextField inputTab4CsvValue2;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：工程の番号 *
     */
    @FXML
    private TextField inputTab4ExcelValue2;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：プロパティ名 *
     */
    @FXML
    private TextField inputTab4CsvValue3;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：プロパティ名 *
     */
    @FXML
    private TextField inputTab4ExcelValue3;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：型 *
     */
    @FXML
    private TextField inputTab4CsvValue4;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：型 *
     */
    @FXML
    private TextField inputTab4ExcelValue4;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：値 *
     */
    @FXML
    private TextField inputTab4CsvValue5;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：値 *
     */
    @FXML
    private TextField inputTab4ExcelValue5;
    /**
     * CSV形式：工程カンバンプロパティ：入力エリア：工程名 *
     */
    @FXML
    private TextField inputTab4CsvValue6;
    /**
     * EXCEL形式：工程カンバンプロパティ：入力エリア：工程名 *
     */
    @FXML
    private TextField inputTab4ExcelValue6;

    // 2020/02/25 MES連携 フォーマットB追加 START
    @FXML
    private ToggleGroup inputTab4FormatToggle;
    @FXML
    private ToggleButton inputTab4Format1Button;
    @FXML
    private ToggleButton inputTab4Format2Button;

    @FXML
    private Label tab4F1LineStartLabel;
    @FXML
    private Label tab4F1KanbanNameLabel;
    @FXML
    private Label tab4F1WorkNameLabel;
    @FXML
    private Label tab4F1WorkNoLabel;
    @FXML
    private Label tab4F1PropNameLabel;
    @FXML
    private Label tab4F1PropTypeLabel;
    @FXML
    private Label tab4F1PropValueLabel;

    @FXML
    private Label tab4F2HeaderRowLabel;
    @FXML
    private Label tab4F2LineStartLabel;
    @FXML
    private Label tab4F2KanbanNameLabel;
    @FXML
    private Label tab4F2WorkNameLabel;
    @FXML
    private Label tab4F2WorkNoLabel;
    @FXML
    private Label tab4F2PropertyLabel;

    @FXML
    private TextField inputTab4F2CsvHeaderRow;// CSV形式：ヘッダー行
    @FXML
    private TextField inputTab4F2ExcelHeaderRow;// Excel形式：ヘッダー行
    @FXML
    private TextField inputTab4F2CsvLineStart;// CSV形式：読み込み開始行
    @FXML
    private TextField inputTab4F2ExcelLineStart;// Excel形式：読み込み開始行
    @FXML
    private TextField inputTab4F2CsvKanbanName;// CSV形式：カンバン名
    @FXML
    private TextField inputTab4F2ExcelKanbanName;// Excel形式：カンバン名
    @FXML
    private TextField inputTab4F2CsvWorkName;// CSV形式：工程名
    @FXML
    private TextField inputTab4F2ExcelWorkName;// Excel形式：工程名
    @FXML
    private TextField inputTab4F2CsvWorkNo;// CSV形式：工程番号
    @FXML
    private TextField inputTab4F2ExcelWorkNo;// Excel形式：工程番号
    @FXML
    private VBox inputTab4F2CsvPropertyPane;// CSV形式：プロパティ
    @FXML
    private VBox inputTab4F2ExcelPropertyPane;// Excel形式：プロパティ
    
    // プロパティを組み合わせて読み込む
    @FXML
    private VBox inputTab4F2UnionPropVBox;
    @FXML
    private CheckBox inputTab4F2IsCheckUnionProp;// チェックボックス
    @FXML
    private TextField inputTab4F2UnionPropNewName;// 新しいプロパティ名
    @FXML
    private TextField inputTab4F2UnionPropLeftName;// 組み合わせるプロパティ名(左)
    @FXML
    private TextField inputTab4F2UnionPropRightName;// 組み合わせるプロパティ名(右)
    // 2020/02/25 MES連携 フォーマットB追加 END
    
    /**
     * CSV形式：カンバンステータス：入力エリア：エンコード *
     */
    @FXML
    private ComboBox<String> inputTab5CsvEncode;
    /**
     * CSV形式：カンバンステータス：入力エリア：ファイル名 *
     */
    @FXML
    private TextField inputTab5CsvFileName;
    /**
     * EXCEL形式：カンバンステータス：入力エリア：シート名 *
     */
    @FXML
    private TextField inputTab5ExcelSheetName;
    /**
     * CSV形式：カンバンステータス：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab5CsvLineStart;
    /**
     * EXCEL形式：カンバンステータス：入力エリア：読み込み開始行 *
     */
    @FXML
    private TextField inputTab5ExcelLineStart;

    /**
     * CSV形式：カンバンステータス：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab5CsvValue1;
    /**
     * EXCEL形式：カンバンステータス：入力エリア：カンバン名 *
     */
    @FXML
    private TextField inputTab5ExcelValue1;
    /**
     * CSV形式：カンバンステータス：入力エリア：ステータス *
     */
    @FXML
    private TextField inputTab5CsvValue2;
    /**
     * EXCEL形式：カンバンステータス：入力エリア：ステータス *
     */
    @FXML
    private TextField inputTab5ExcelValue2;

    @FXML
    private CheckBox checkWorkflowWithModel;
    @FXML
    private CheckBox checkWorkflowRegex;
    @FXML
    private VBox workflowRegexInfoPane;

    @FXML
    private CheckBox checkKanbanHierarchy;
    @FXML
    private TextField kanbanHierarchyTextField;

    // 製品情報
    @FXML
    private Tab tabProduct;
    @FXML
    private ComboBox<String> inputTab6CsvEncode;// CSV形式：エンコード
    @FXML
    private TextField inputTab6CsvFileName;// CSV形式：ファイル名
    @FXML
    private TextField inputTab6ExcelSheetName;// Excel形式：シート名
    @FXML
    private TextField inputTab6CsvLineStart;// CSV形式：読み込み開始行
    @FXML
    private TextField inputTab6ExcelLineStart;// Excel形式：読み込み開始行
    @FXML
    private TextField inputTab6CsvUniqueID;// CSV形式：ユニークID
    @FXML
    private TextField inputTab6ExcelUniqueID;// Excel形式：ユニークID
    @FXML
    private TextField inputTab6CsvKanbanName;// CSV形式：カンバン名
    @FXML
    private TextField inputTab6ExcelKanbanName;// Excel形式：カンバン名

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

        // 未対応のため製品情報タブを非表示
        this.tabImportMode.getTabs().remove(this.tabProduct);

        logger.info(":initialize end");
    }

    /**
     * 工程順 選択ボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onSelectWorkflow(ActionEvent event) {
        logger.info("onSelectWorkflow");
        try {
            blockUI(true);

            Button button = (Button) event.getSource();

            WorkflowRegexInfo regexInfo = (WorkflowRegexInfo) button.getUserData();

            // 選択中の工程順をセットする。
            SelectDialogEntity<WorkflowInfoEntity> selectDialogEntity = new SelectDialogEntity();
            selectDialogEntity.setUseLatestRev(Objects.isNull(regexInfo.isUseLatest()) ? false : regexInfo.isUseLatest());
            if (Objects.nonNull(regexInfo.getWorkflow())) {
                // TODO: 選択中の工程順をセットすると、ダイアログでエラーになるので、選択ダイアログを修正したら下をコメント解除する。
//                selectDialogEntity.setWorkflows(Arrays.asList(regexInfo.getWorkflow()));
            }
            selectDialogEntity.setVisibleUseLatestRev(true);// 「常に最新版を使用する」を表示する。

            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.OrderProcesses"), "WorkflowSingleSelectionCompo", selectDialogEntity);
            if (ret.equals(ButtonType.OK) && !selectDialogEntity.getWorkflows().isEmpty()) {
                WorkflowInfoEntity workflow = selectDialogEntity.getWorkflows().get(0);

                regexInfo.setUseLatest(selectDialogEntity.isUseLatestRev());
                regexInfo.setWorkflow(workflow);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            blockUI(false);
        }
    }

    /**
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        this.argument = argument;
    }

    /**
     * カンバン情報タブの全ての入力欄を通常表示にする。
     */
    private void setTab1TextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvFileName);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue5);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue6);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue7);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue8);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue9);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue10);
        ProductionNaviUtils.setFieldNormal(this.inputTab1CsvValue11);

        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue5);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue6);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue7);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue8);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue9);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue10);
        ProductionNaviUtils.setFieldNormal(this.inputTab1ExcelValue11);
    }

    /**
     * カンバンプロパティ情報タブの全ての入力欄を通常表示にする。
     */
    private void setTab2TextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvFileName);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab2CsvValue5);
        ProductionNaviUtils.setFieldNormal(this.tab2F2CsvHeaderRow);
        ProductionNaviUtils.setFieldNormal(this.tab2F2CsvStartRow);
        ProductionNaviUtils.setFieldNormal(this.tab2F2CsvKanbanName);
        ProductionNaviUtils.setFieldNormal(this.tab2F2CsvWorkflowName);
        ((GridPane) this.tab2F2CsvPropertyPane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof RestrictedTextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));

        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab2ExcelValue5);
        ProductionNaviUtils.setFieldNormal(this.tab2F2XlsHeaderRow);
        ProductionNaviUtils.setFieldNormal(this.tab2F2XlsStartRow);
        ProductionNaviUtils.setFieldNormal(this.tab2F2XlsKanbanName);
        ProductionNaviUtils.setFieldNormal(this.tab2F2XlsWorkflowName);
        ((GridPane) this.tab2F2XlsPropertyPane.getChildren().filtered(p -> p instanceof GridPane).get(0))
                .getChildren().filtered(p -> p instanceof RestrictedTextField)
                .forEach(field -> ProductionNaviUtils.setFieldNormal((TextField) field));
    }

    /**
     * 工程カンバン情報タブの全ての入力欄を通常表示にする。
     */
    private void setTab3TextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvFileName);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue5);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue6);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue7);
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue8);  // 2019/12/18 工程名項目の追加対応
        ProductionNaviUtils.setFieldNormal(this.inputTab3CsvValue9);  // 2020/02/20 MES連携 タクトタイム追加
 
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue5);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue6);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue7);
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue8);  // 2019/12/18 工程名項目の追加対応
        ProductionNaviUtils.setFieldNormal(this.inputTab3ExcelValue9);  // 2020/02/20 MES連携 タクトタイム追加
    }

    /**
     * 工程カンバンプロパティ情報タブの全ての入力欄を通常表示にする。
     */
    private void setTab4TextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvFileName);
        // フォーマットA(CSV)
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvValue5);
        ProductionNaviUtils.setFieldNormal(this.inputTab4CsvValue6);
        // フォーマットB(CSV)
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2CsvHeaderRow);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2CsvKanbanName);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2CsvWorkName);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2CsvWorkNo);
        // フォーマットA(Excel)
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelValue2);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelValue3);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelValue4);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelValue5);
        ProductionNaviUtils.setFieldNormal(this.inputTab4ExcelValue6);
        // フォーマットB(Excel)
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2ExcelHeaderRow);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2ExcelKanbanName);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2ExcelWorkName);
        ProductionNaviUtils.setFieldNormal(this.inputTab4F2ExcelWorkNo);
    }

    /**
     * カンバンステータス情報タブの全ての入力欄を通常表示にする。
     */
    private void setTab5TextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab5CsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputTab5CsvFileName);
        ProductionNaviUtils.setFieldNormal(this.inputTab5CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab5CsvValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab5CsvValue2);

        ProductionNaviUtils.setFieldNormal(this.inputTab5ExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputTab5ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab5ExcelValue1);
        ProductionNaviUtils.setFieldNormal(this.inputTab5ExcelValue2);
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

            final ImportFormatInfo newFormatInfo = createImportFormatInfo();

            Task task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    // 現在のフォーマット設定を読み込む。
                    ImportFormatInfo importFormatInfo = ImportFormatFileUtil.load();
                    // 設定を更新する。
                    importFormatInfo.setKanbanFormatInfo(newFormatInfo.getKanbanFormatInfo());
                    importFormatInfo.setKanbanPropFormatInfo(newFormatInfo.getKanbanPropFormatInfo());
                    importFormatInfo.setWorkKanbanFormatInfo(newFormatInfo.getWorkKanbanFormatInfo());
                    importFormatInfo.setWorkKanbanPropFormatInfo(newFormatInfo.getWorkKanbanPropFormatInfo());
                    importFormatInfo.setKanbanStatusFormatInfo(newFormatInfo.getKanbanStatusFormatInfo());
                    importFormatInfo.setProductFormatInfo(newFormatInfo.getProductFormatInfo());
                    // 設定をファイルに保存する。
                    return ImportFormatFileUtil.save(importFormatInfo);
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
     * 画面の情報からインポートフォーマット設定を作成する。
     *
     * @return インポートフォーマット設定
     */
    private ImportFormatInfo createImportFormatInfo() {
        ImportFormatInfo importFormatInfo = new ImportFormatInfo();
        try {
            // カンバン
            importFormatInfo.setKanbanFormatInfo(this.createKanbanFormatInfo());
            // カンバンプロパティ
            importFormatInfo.setKanbanPropFormatInfo(this.createKanbanPropFormatInfo());
            // 工程カンバン
            importFormatInfo.setWorkKanbanFormatInfo(this.createWorkKanbanFormatInfo());
            // 工程カンバンプロパティ
            importFormatInfo.setWorkKanbanPropFormatInfo(this.createWorkKanbanPropFormatInfo());
            // カンバンステータス
            importFormatInfo.setKanbanStatusFormatInfo(this.createKanbanStatusFormatInfo());
            // 製品
            importFormatInfo.setProductFormatInfo(this.createProductFormatInfo());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }

        return importFormatInfo;
    }

    /**
     * 画面の情報からカンバンのフォーマット情報を作成する。
     *
     * @return カンバンのフォーマット情報
     */
    private KanbanFormatInfo createKanbanFormatInfo() {
        KanbanFormatInfo kanbanFormatInfo = new KanbanFormatInfo();

        // ファイル名
        kanbanFormatInfo.setCsvFileName(this.inputTab1CsvFileName.getText());
        // エンコード
        kanbanFormatInfo.setCsvFileEncode(this.inputTab1CsvEncode.getSelectionModel().getSelectedItem());
        // シート名
        kanbanFormatInfo.setXlsSheetName(this.inputTab1ExcelSheetName.getText());

        // 読み込み開始行
        kanbanFormatInfo.setCsvStartRow(this.inputTab1CsvLineStart.getText());
        kanbanFormatInfo.setXlsStartRow(this.inputTab1ExcelLineStart.getText());
        // カンバン階層
        kanbanFormatInfo.setCsvHierarchyName(this.inputTab1CsvValue1.getText());
        kanbanFormatInfo.setXlsHierarchyName(this.inputTab1ExcelValue1.getText());
        // カンバン名
        kanbanFormatInfo.setCsvKanbanName(this.inputTab1CsvValue2.getText());
        kanbanFormatInfo.setXlsKanbanName(this.inputTab1ExcelValue2.getText());
        // 工程順名
        kanbanFormatInfo.setCsvWorkflowName(this.inputTab1CsvValue3.getText());
        kanbanFormatInfo.setXlsWorkflowName(this.inputTab1ExcelValue3.getText());
        // 工程順版数
        kanbanFormatInfo.setCsvWorkflowRev(this.inputTab1CsvValue4.getText());
        kanbanFormatInfo.setXlsWorkflowRev(this.inputTab1ExcelValue4.getText());
        // モデル名
        kanbanFormatInfo.setCsvModelName(this.inputTab1CsvValue5.getText());
        kanbanFormatInfo.setXlsModelName(this.inputTab1ExcelValue5.getText());
        // 作業開始日時
        kanbanFormatInfo.setCsvStartDateTime(this.inputTab1CsvValue6.getText());
        kanbanFormatInfo.setXlsStartDateTime(this.inputTab1ExcelValue6.getText());
        // ロット生産
        kanbanFormatInfo.setCsvProductionType(this.inputTab1CsvValue7.getText());
        kanbanFormatInfo.setXlsProductionType(this.inputTab1ExcelValue7.getText());
        // ロット数量
        kanbanFormatInfo.setCsvLotNum(this.inputTab1CsvValue8.getText());
        kanbanFormatInfo.setXlsLotNum(this.inputTab1ExcelValue8.getText());
        // 製造番号
        kanbanFormatInfo.setCsvProductionNumber(this.inputTab1CsvValue9.getText());
        kanbanFormatInfo.setXlsProductionNumber(this.inputTab1ExcelValue9.getText());
        // 開始シリアル番号
        kanbanFormatInfo.setCsvStartSerial(this.inputTab1CsvValue10.getText());
        kanbanFormatInfo.setXlsStartSerial(this.inputTab1ExcelValue10.getText());
        // 終了シリアル番号
        kanbanFormatInfo.setCsvEndSerial(this.inputTab1CsvValue11.getText());
        kanbanFormatInfo.setXlsEndSerial(this.inputTab1ExcelValue11.getText());
        // 標準作業時間
        kanbanFormatInfo.setCsvCycleTime(this.inputTab1CsvValue12.getText());
        kanbanFormatInfo.setXlsCycleTime(this.inputTab1ExcelValue12.getText());

        // モデル名と工程順名の組み合わせで工程順を指定する
        kanbanFormatInfo.setIsCheckWorkflowWithModel(this.checkWorkflowWithModel.isSelected());

        // モデル名で工程順を指定する
        kanbanFormatInfo.setIsCheckWorkflowRegex(this.checkWorkflowRegex.isSelected());

        // モデル名の条件と工程順
        int order = 0;
        for (WorkflowRegexInfo regexInfo : this.workflowRegexInfos) {
            regexInfo.setOrder(order++);
        }

        kanbanFormatInfo.getWorkflowRegexInfos().clear();
        kanbanFormatInfo.getWorkflowRegexInfos().addAll(this.workflowRegexInfos);

        // カンバン階層を指定する
        kanbanFormatInfo.setIsCheckKanbanHierarchy(this.checkKanbanHierarchy.isSelected());
        kanbanFormatInfo.setKanbanHierarchyName(this.kanbanHierarchyTextField.getText());

        return kanbanFormatInfo;
    }

    /**
     * 画面の情報からカンバンプロパティのフォーマット情報を作成する。
     *
     * @return カンバンプロパティのフォーマット情報
     */
    private KanbanPropFormatInfo createKanbanPropFormatInfo() {
        KanbanPropFormatInfo kanbanPropFormatInfo = new KanbanPropFormatInfo();

        // ファイル名
        kanbanPropFormatInfo.setCsvFileName(this.inputTab2CsvFileName.getText());
        // エンコード
        kanbanPropFormatInfo.setCsvFileEncode(this.inputTab2CsvEncode.getSelectionModel().getSelectedItem());
        // シート名
        kanbanPropFormatInfo.setXlsSheetName(this.inputTab2ExcelSheetName.getText());

        // フォーマット選択
        kanbanPropFormatInfo.setSelectedFormat((String) this.formatToggle.getSelectedToggle().getUserData());

        // フォーマット１
        // 読み込み開始行
        kanbanPropFormatInfo.setCsvStartRow(this.inputTab2CsvLineStart.getText());
        kanbanPropFormatInfo.setXlsStartRow(this.inputTab2ExcelLineStart.getText());
        // カンバン名
        kanbanPropFormatInfo.setCsvKanbanName(this.inputTab2CsvValue1.getText());
        kanbanPropFormatInfo.setXlsKanbanName(this.inputTab2ExcelValue1.getText());
        // 工程順名
        kanbanPropFormatInfo.setCsvWorkflowName(this.inputTab2CsvValue2.getText());
        kanbanPropFormatInfo.setXlsWorkflowName(this.inputTab2ExcelValue2.getText());
        // プロパティ名
        kanbanPropFormatInfo.setCsvPropName(this.inputTab2CsvValue3.getText());
        kanbanPropFormatInfo.setXlsPropName(this.inputTab2ExcelValue3.getText());
        // プロパティ型
        kanbanPropFormatInfo.setCsvPropType(this.inputTab2CsvValue4.getText());
        kanbanPropFormatInfo.setXlsPropType(this.inputTab2ExcelValue4.getText());
        // プロパティ値
        kanbanPropFormatInfo.setCsvPropValue(this.inputTab2CsvValue5.getText());
        kanbanPropFormatInfo.setXlsPropValue(this.inputTab2ExcelValue5.getText());

        // フォーマット２
        // ヘッダー行
        kanbanPropFormatInfo.setF2CsvHeaderRow(this.tab2F2CsvHeaderRow.getText());
        kanbanPropFormatInfo.setF2XlsHeaderRow(this.tab2F2XlsHeaderRow.getText());
        // 読み込み開始行
        kanbanPropFormatInfo.setF2CsvStartRow(this.tab2F2CsvStartRow.getText());
        kanbanPropFormatInfo.setF2XlsStartRow(this.tab2F2XlsStartRow.getText());
        // カンバン名
        kanbanPropFormatInfo.setF2CsvKanbanName(this.tab2F2CsvKanbanName.getText());
        kanbanPropFormatInfo.setF2XlsKanbanName(this.tab2F2XlsKanbanName.getText());
        // 工程順名
        kanbanPropFormatInfo.setF2CsvWorkflowName(this.tab2F2CsvWorkflowName.getText());
        kanbanPropFormatInfo.setF2XlsWorkflowName(this.tab2F2XlsWorkflowName.getText());

        // プロパティ
        kanbanPropFormatInfo.getF2CsvPropValues().clear();
        for (SimpleStringProperty prop : this.csvKanbanPropInfos) {
            kanbanPropFormatInfo.getF2CsvPropValues().add(prop.getValue());
        }

        kanbanPropFormatInfo.getF2XlsPropValues().clear();
        for (SimpleStringProperty prop : this.xlsKanbanPropInfos) {
            kanbanPropFormatInfo.getF2XlsPropValues().add(prop.getValue());
        }

        return kanbanPropFormatInfo;
    }

    /**
     * 画面の情報から工程カンバンのフォーマット情報を作成する。
     *
     * @return 工程カンバンのフォーマット情報
     */
    private WorkKanbanFormatInfo createWorkKanbanFormatInfo() {
        WorkKanbanFormatInfo workKanbanFormatInfo = new WorkKanbanFormatInfo();

        // ファイル名
        workKanbanFormatInfo.setCsvFileName(this.inputTab3CsvFileName.getText());
        // エンコード
        workKanbanFormatInfo.setCsvFileEncode(this.inputTab3CsvEncode.getSelectionModel().getSelectedItem());
        // シート名
        workKanbanFormatInfo.setXlsSheetName(this.inputTab3ExcelSheetName.getText());

        // 読み込み開始行
        workKanbanFormatInfo.setCsvStartRow(this.inputTab3CsvLineStart.getText());
        workKanbanFormatInfo.setXlsStartRow(this.inputTab3ExcelLineStart.getText());
        // カンバン名
        workKanbanFormatInfo.setCsvKanbanName(this.inputTab3CsvValue1.getText());
        workKanbanFormatInfo.setXlsKanbanName(this.inputTab3ExcelValue1.getText());
        // 工程の番号
        workKanbanFormatInfo.setCsvWorkNum(this.inputTab3CsvValue2.getText());
        workKanbanFormatInfo.setXlsWorkNum(this.inputTab3ExcelValue2.getText());
        // スキップ
        workKanbanFormatInfo.setCsvSkipFlag(this.inputTab3CsvValue3.getText());
        workKanbanFormatInfo.setXlsSkipFlag(this.inputTab3ExcelValue3.getText());
        // 作業開始日時
        workKanbanFormatInfo.setCsvStartDateTime(this.inputTab3CsvValue4.getText());
        workKanbanFormatInfo.setXlsStartDateTime(this.inputTab3ExcelValue4.getText());
        // 完了予定日時
        workKanbanFormatInfo.setCsvCompDateTime(this.inputTab3CsvValue5.getText());
        workKanbanFormatInfo.setXlsCompDateTime(this.inputTab3ExcelValue5.getText());
        // 組織識別名
        workKanbanFormatInfo.setCsvOrganizationIdentName(this.inputTab3CsvValue6.getText());
        workKanbanFormatInfo.setXlsOrganizationIdentName(this.inputTab3ExcelValue6.getText());
        // 設備識別名
        workKanbanFormatInfo.setCsvEquipmentIdentName(this.inputTab3CsvValue7.getText());
        workKanbanFormatInfo.setXlsEquipmentIdentName(this.inputTab3ExcelValue7.getText());
        // 工程名 2019/12/18 工程名項目の追加対応
        workKanbanFormatInfo.setCsvWorkName(this.inputTab3CsvValue8.getText());  
        workKanbanFormatInfo.setXlsWorkName(this.inputTab3ExcelValue8.getText());
        // タクトタイム 2020/02/20 MES連携 タクトタイム追加
        workKanbanFormatInfo.setCsvTactTime(this.inputTab3CsvValue9.getText());
        workKanbanFormatInfo.setXlsTactTime(this.inputTab3ExcelValue9.getText());

        return workKanbanFormatInfo;
    }

    /**
     * 画面の情報から工程カンバンプロパティのフォーマット情報を作成する。
     *
     * @return 工程カンバンプロパティのフォーマット情報
     */
    private WorkKanbanPropFormatInfo createWorkKanbanPropFormatInfo() {
        WorkKanbanPropFormatInfo workKanbanPropFormatInfo = new WorkKanbanPropFormatInfo();

        // ファイル名
        workKanbanPropFormatInfo.setCsvFileName(this.inputTab4CsvFileName.getText());
        // エンコード
        workKanbanPropFormatInfo.setCsvFileEncode(this.inputTab4CsvEncode.getSelectionModel().getSelectedItem());
        // シート名
        workKanbanPropFormatInfo.setXlsSheetName(this.inputTab4ExcelSheetName.getText());

        // フォーマット選択
        workKanbanPropFormatInfo.setSelectedFormat((String) this.inputTab4FormatToggle.getSelectedToggle().getUserData());       
        
        // フォーマットA
        // 読み込み開始行
        workKanbanPropFormatInfo.setCsvStartRow(this.inputTab4CsvLineStart.getText());
        workKanbanPropFormatInfo.setXlsStartRow(this.inputTab4ExcelLineStart.getText());
        // カンバン名
        workKanbanPropFormatInfo.setCsvKanbanName(this.inputTab4CsvValue1.getText());
        workKanbanPropFormatInfo.setXlsKanbanName(this.inputTab4ExcelValue1.getText());
        // 工程の番号
        workKanbanPropFormatInfo.setCsvWorkNum(this.inputTab4CsvValue2.getText());
        workKanbanPropFormatInfo.setXlsWorkNum(this.inputTab4ExcelValue2.getText());
        // プロパティ名
        workKanbanPropFormatInfo.setCsvPropName(this.inputTab4CsvValue3.getText());
        workKanbanPropFormatInfo.setXlsPropName(this.inputTab4ExcelValue3.getText());
        // プロパティ型
        workKanbanPropFormatInfo.setCsvPropType(this.inputTab4CsvValue4.getText());
        workKanbanPropFormatInfo.setXlsPropType(this.inputTab4ExcelValue4.getText());
        // プロパティ値
        workKanbanPropFormatInfo.setCsvPropValue(this.inputTab4CsvValue5.getText());
        workKanbanPropFormatInfo.setXlsPropValue(this.inputTab4ExcelValue5.getText());
        // 工程名
        workKanbanPropFormatInfo.setCsvWorkName(this.inputTab4CsvValue6.getText());
        workKanbanPropFormatInfo.setXlsWorkName(this.inputTab4ExcelValue6.getText());
        
        // フォーマット２
        // ヘッダー行
        workKanbanPropFormatInfo.setF2CsvHeaderRow(this.inputTab4F2CsvHeaderRow.getText());
        workKanbanPropFormatInfo.setF2XlsHeaderRow(this.inputTab4F2ExcelHeaderRow.getText());
        // 読み込み開始行
        workKanbanPropFormatInfo.setF2CsvStartRow(this.inputTab4F2CsvLineStart.getText());
        workKanbanPropFormatInfo.setF2XlsStartRow(this.inputTab4F2ExcelLineStart.getText());
        // カンバン名
        workKanbanPropFormatInfo.setF2CsvKanbanName(this.inputTab4F2CsvKanbanName.getText());
        workKanbanPropFormatInfo.setF2XlsKanbanName(this.inputTab4F2ExcelKanbanName.getText());
        // 工程名
        workKanbanPropFormatInfo.setF2CsvWorkName(this.inputTab4F2CsvWorkName.getText());
        workKanbanPropFormatInfo.setF2XlsWorkName(this.inputTab4F2ExcelWorkName.getText());
        // 工程の番号
        workKanbanPropFormatInfo.setF2CsvWorkNo(this.inputTab4F2CsvWorkNo.getText());
        workKanbanPropFormatInfo.setF2XlsWorkNo(this.inputTab4F2ExcelWorkNo.getText());

        // プロパティ
        workKanbanPropFormatInfo.getF2CsvPropValues().clear();
        for (SimpleStringProperty prop : this.csvWorkKanbanPropInfos) {
            workKanbanPropFormatInfo.getF2CsvPropValues().add(prop.getValue());
        }
        workKanbanPropFormatInfo.getF2XlsPropValues().clear();
        for (SimpleStringProperty prop : this.xlsWorkKanbanPropInfos) {
            workKanbanPropFormatInfo.getF2XlsPropValues().add(prop.getValue());
        }
        
        // プロパティを組み合わせて読みこむ
        workKanbanPropFormatInfo.setF2IsCheckUnionProp(this.inputTab4F2IsCheckUnionProp.isSelected());
        workKanbanPropFormatInfo.setF2UnionPropNewName(this.inputTab4F2UnionPropNewName.getText());
        workKanbanPropFormatInfo.setF2UnionPropLeftName(this.inputTab4F2UnionPropLeftName.getText());
        workKanbanPropFormatInfo.setF2UnionPropRightName(this.inputTab4F2UnionPropRightName.getText());
        
        return workKanbanPropFormatInfo;
    }

    /**
     * 画面の情報からカンバンステータスのフォーマット情報を作成する。
     *
     * @return カンバンステータスのフォーマット情報
     */
    private KanbanStatusFormatInfo createKanbanStatusFormatInfo() {
        KanbanStatusFormatInfo kanbanStatusFormatInfo = new KanbanStatusFormatInfo();

        // ファイル名
        kanbanStatusFormatInfo.setCsvFileName(this.inputTab5CsvFileName.getText());
        // エンコード
        kanbanStatusFormatInfo.setCsvFileEncode(this.inputTab5CsvEncode.getSelectionModel().getSelectedItem());
        // シート名
        kanbanStatusFormatInfo.setXlsSheetName(this.inputTab5ExcelSheetName.getText());

        // 読み込み開始行
        kanbanStatusFormatInfo.setCsvStartRow(this.inputTab5CsvLineStart.getText());
        kanbanStatusFormatInfo.setXlsStartRow(this.inputTab5ExcelLineStart.getText());
        // カンバン名
        kanbanStatusFormatInfo.setCsvKanbanName(this.inputTab5CsvValue1.getText());
        kanbanStatusFormatInfo.setXlsKanbanName(this.inputTab5ExcelValue1.getText());
        // カンバンステータス
        kanbanStatusFormatInfo.setCsvKanbanStatus(this.inputTab5CsvValue2.getText());
        kanbanStatusFormatInfo.setXlsKanbanStatus(this.inputTab5ExcelValue2.getText());

        return kanbanStatusFormatInfo;
    }

    /**
     * データチェック処理
     *
     * @return
     */
    private boolean isCheck() {
        boolean result = true;

        try {
            // カンバンの入力チェック
            logger.debug("カンバン情報 入力エラーチェック");
            if (!this.isInputCheckTab1()) {
                logger.debug("カンバン情報に入力エラーあり");
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_KANBAN);
                }
                result = false;
            }
            // カンバンプロパティの入力チェック
            logger.debug("カンバンプロパティ 入力エラーチェック");
            if (!this.isInputCheckTab2()) {
                logger.debug("カンバンプロパティに入力エラーあり");
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_KANBAN_PROPERTY);
                }
                result = false;
            }
            // 工程カンバンの入力チェック
            logger.debug("工程カンバン情報 入力エラーチェック");
            if (!this.isInputCheckTab3()) {
                logger.debug("工程カンバン情報に入力エラーあり");
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_WORK_KANBAN);
                }
                result = false;
            }
            // 工程カンバンプロパティの入力チェック
            logger.debug("工程カンバンプロパティ情報 入力エラーチェック");
            if (!this.isInputCheckTab4()) {
                logger.debug("工程カンバンプロパティ情報に入力エラーあり");
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_WORK_KANBAN_PROPERTY);
                }
                result = false;
            }
            // カンバンステータスの入力チェック
            logger.debug("カンバンステータス 入力エラーチェック");
            if (!this.isInputCheckTab5()) {
                logger.debug("カンバンステータス情報に入力エラーあり");
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_KANBAN_STATUS);
                }
                result = false;
            }
            // 製品の入力チェック
            if (!this.isInputCheckTab6()) {
                if (result) {
                    this.tabImportMode.getSelectionModel().select(ProductionNaviUtils.WORKPLAN_TAB_IDX_PRODUCT);
                }
                result = false;
            }

            logger.debug(" チェック結果:" + result);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
            result = false;
        }

        logger.debug(" チェック結果:" + result);
        return result;
    }

    /**
     * カンバンの入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckTab1() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTab1TextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab1CsvEncode)) {
            value = false;
        }

        // CSV形式：ファイル名
        if (!ProductionNaviUtils.isNotNull(this.inputTab1CsvFileName)) {
            value = false;
        }

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab1CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab1CsvLineStart);
            value = false;
        }

        // CSV形式：カンバン階層
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue1, !this.checkKanbanHierarchy.isSelected())) {
            value = false;
        } else if (!this.checkKanbanHierarchy.isSelected()) {
            if (!this.withinRange(this.inputTab1CsvValue1.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue1);
                value = false;
            }
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue2)) {
            value = false;
        } else if (!this.withinRange(this.inputTab1CsvValue2.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab1CsvValue2);
            value = false;
        }

        // CSV形式：工程順名
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue3, !this.checkWorkflowRegex.isSelected())) {
            logger.warn("CSV形式：工程順名 数値エラー");
            value = false;
        } else if (!this.checkWorkflowRegex.isSelected()) {
            if (!this.withinRange(this.inputTab1CsvValue3.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue3);
                value = false;
            }
        }

        // CSV形式：工程順版数
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue4, false)) {
            logger.warn("CSV形式：工程順版数 数値エラー");
            value = false;
        } else if (!this.checkWorkflowRegex.isSelected() && !this.inputTab1CsvValue4.getText().isEmpty()) {
            if (!this.withinRange(this.inputTab1CsvValue4.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue4);
                value = false;
            }
        }

        // CSV形式：モデル名
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue5, this.checkWorkflowRegex.isSelected())) {
            logger.warn("CSV形式：モデル名 数値エラー");
            value = false;
        } else if (this.checkWorkflowRegex.isSelected() || !this.inputTab1CsvValue5.getText().isEmpty()) {
            if (!this.withinRange(this.inputTab1CsvValue5.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue5);
                value = false;
            }
        }

        // CSV形式：作業開始日時
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue6, false)) {
            logger.warn("CSV形式：作業開始日時 数値エラー");
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue6.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue6.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue6);
                value = false;
            }
        }

        // CSV形式：生産タイプ
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue7, false)) {
            logger.warn("CSV形式：生産タイプ 数値エラー");
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue7.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue7.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue7);
                value = false;
            }
        }

        // CSV形式：ロット数量
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue8, false)) {
            logger.warn("CSV形式：ロット数量 数値エラー");
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue8.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue8.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue8);
                value = false;
            }
        }

        // CSV形式：モデル名
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue9, false)) {
            logger.warn("CSV形式：製造番号 数値エラー");
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue9.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue9.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue9);
                value = false;
            }
        }

       // Excel形式：製造番号
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue9, false)) {
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue9.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue9.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue9);
                value = false;
            }
        }  

        // Excel形式：開始シリアル番号
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue10, false)) {
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue10.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue10.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue10);
                value = false;
            }
        } 

        // Excel形式：終了シリアル番号
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue11, false)) {
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue11.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue11.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue11);
                value = false;
            }
        }

        // 標準作業時間
        if (!ProductionNaviUtils.isNumber(this.inputTab1CsvValue12, false)) {
            value = false;
        } else if (!StringUtils.isEmpty(this.inputTab1CsvValue12.getText())) {
            if (!this.withinRange(this.inputTab1CsvValue12.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab1CsvValue12);
                value = false;
            }
        }

        //  Excel形式：シート名
        if (!ProductionNaviUtils.isNotNull(this.inputTab1ExcelSheetName)) {
            value = false;
        }

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab1ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab1ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab1ExcelLineStart);
            value = false;
        }

        // Excel形式：カンバン階層
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue1, INPUT_MAX_SIZE, !this.checkKanbanHierarchy.isSelected())) {
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue2, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：工程順名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue3, INPUT_MAX_SIZE, !this.checkWorkflowRegex.isSelected())) {
            value = false;
        }

        // Excel形式：工程順版数
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue4, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：モデル名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue5, INPUT_MAX_SIZE, this.checkWorkflowRegex.isSelected())) {
            value = false;
        }

        // Excel形式：作業開始日時
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue6, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：生産タイプ
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue7, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：ロット数量
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue8, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：製造番号
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue9, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：開始シリアル番号
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue10, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：終了シリアル番号
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue11, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：標準作業時間
        if (!ProductionNaviUtils.isAlphabet(this.inputTab1ExcelValue12, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // モデル名の条件と工程順
        for (WorkflowRegexInfo regexInfo : this.workflowRegexInfos) {
            if (StringUtils.isEmpty(regexInfo.getRegex()) || StringUtils.isEmpty(regexInfo.getWorkflowName())) {
                value = false;
            }
        }

        // カンバン階層を指定する場合のカンバン階層
        if (this.checkKanbanHierarchy.isSelected()) {
            if (StringUtils.isEmpty(this.kanbanHierarchyTextField.getText())) {
                ProductionNaviUtils.setFieldError(this.kanbanHierarchyTextField);
                value = false;
            } else {
                ProductionNaviUtils.setFieldNormal(this.kanbanHierarchyTextField);
            }
        } else {
            ProductionNaviUtils.setFieldNormal(this.kanbanHierarchyTextField);
        }

        return value;
    }

    /**
     * カンバンプロパティの入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckTab2() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTab2TextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab2CsvEncode)) {
            value = false;
        }

        // CSV形式：ファイル名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab2CsvFileName)) {
        //    value = false;
        //}

        // Excel形式：シート名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab2ExcelSheetName)) {
        //    value = false;
        //}

        if (this.formatToggle.getSelectedToggle().getUserData().equals("2")) {
            value = isInputCheckTab2F2();
        } else {
            value = isInputCheckTab2F1();
        }

        return value;
    }

    /**
     * カンバンプロパティの入力チェック(フォーマットA)
     *
     * @return 結果
     */
    private boolean isInputCheckTab2F1() {
        boolean value = true;

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab2CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab2CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab2CsvLineStart);
            value = false;
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab2CsvValue1)) {
            value = false;
        } else if (!this.withinRange(this.inputTab2CsvValue1.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab2CsvValue1);
            value = false;
        }

        // CSV形式：工程順名
        if (!StringUtils.isEmpty(this.inputTab2CsvValue2.getText())) {
            if (!ProductionNaviUtils.isNumber(this.inputTab2CsvValue2)) {
                value = false;
            } else if (!this.withinRange(this.inputTab2CsvValue2.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab2CsvValue2);
                value = false;
            }
        }

        // CSV形式：プロパティ名
        if (!ProductionNaviUtils.isNumber(this.inputTab2CsvValue3)) {
            value = false;
        } else if (!this.withinRange(this.inputTab2CsvValue3.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab2CsvValue3);
            value = false;
        }

        // CSV形式：型
        if (!ProductionNaviUtils.isNumber(this.inputTab2CsvValue4)) {
            value = false;
        } else if (!this.withinRange(this.inputTab2CsvValue4.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab2CsvValue4);
            value = false;
        }

        // CSV形式：値
        if (!ProductionNaviUtils.isNumber(this.inputTab2CsvValue5)) {
            value = false;
        } else if (!this.withinRange(this.inputTab2CsvValue5.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab2CsvValue5);
            value = false;
        }

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab2ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab2ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab2ExcelLineStart);
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab2ExcelValue1, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：工程順名
        if (!StringUtils.isEmpty(this.inputTab2ExcelValue2.getText())) {
            if (!ProductionNaviUtils.isAlphabet(this.inputTab2ExcelValue2, INPUT_MAX_SIZE)) {
                value = false;
            }
        }

        // Excel形式：プロパティ名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab2ExcelValue3, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：型
        if (!ProductionNaviUtils.isAlphabet(this.inputTab2ExcelValue4, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：値
        if (!ProductionNaviUtils.isAlphabet(this.inputTab2ExcelValue5, INPUT_MAX_SIZE)) {
            value = false;
        }

        return value;
    }

    /**
     * カンバンプロパティの入力チェック(フォーマットB)
     *
     * @return 結果
     */
    private boolean isInputCheckTab2F2() {
        boolean value = true;

        // CSV形式：ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.tab2F2CsvHeaderRow)) {
            value = false;
        } else if (!this.withinRange(this.tab2F2CsvHeaderRow.getText())) {
            ProductionNaviUtils.setFieldError(this.tab2F2CsvHeaderRow);
            value = false;
        }

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.tab2F2CsvStartRow)) {
            value = false;
        } else if (!this.withinRange(this.tab2F2CsvStartRow.getText())) {
            ProductionNaviUtils.setFieldError(this.tab2F2CsvStartRow);
            value = false;
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.tab2F2CsvKanbanName)) {
            value = false;
        } else if (!this.withinRange(this.tab2F2CsvKanbanName.getText())) {
            ProductionNaviUtils.setFieldError(this.tab2F2CsvKanbanName);
            value = false;
        }

        // CSV形式：工程順名
        if (!StringUtils.isEmpty(this.tab2F2CsvWorkflowName.getText())) {
            if (!ProductionNaviUtils.isNumber(this.tab2F2CsvWorkflowName)) {
                value = false;
            } else if (!this.withinRange(this.tab2F2CsvWorkflowName.getText())) {
                ProductionNaviUtils.setFieldError(this.tab2F2CsvWorkflowName);
                value = false;
            }
        }

        // CSV形式：プロパティ
        List<Node> properties = ((GridPane) this.tab2F2CsvPropertyPane.getChildren().filtered(
                p -> p instanceof GridPane).get(0)).getChildren().filtered(p -> p instanceof RestrictedTextField);
        for (Node property : properties) {
            TextField propField = (TextField) property;
            if (!ProductionNaviUtils.isNumber(propField)) {
                value = false;
            } else if (!this.withinRange(propField.getText())) {
                ProductionNaviUtils.setFieldError(propField);
                value = false;
            }
        }

        // Excel形式：ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.tab2F2XlsHeaderRow)) {
            value = false;
        } else if (!this.withinRange(this.tab2F2XlsHeaderRow.getText())) {
            ProductionNaviUtils.setFieldError(this.tab2F2XlsHeaderRow);
            value = false;
        }

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.tab2F2XlsStartRow)) {
            value = false;
        } else if (!this.withinRange(this.tab2F2XlsStartRow.getText())) {
            ProductionNaviUtils.setFieldError(this.tab2F2XlsStartRow);
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.tab2F2XlsKanbanName, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：工程順名
        if (!StringUtils.isEmpty(this.tab2F2XlsWorkflowName.getText())) {
            if (!ProductionNaviUtils.isAlphabet(this.tab2F2XlsWorkflowName, INPUT_MAX_SIZE)) {
                value = false;
            }
        }

        // Excel形式：プロパティ
        properties = ((GridPane) this.tab2F2XlsPropertyPane.getChildren().filtered(
                p -> p instanceof GridPane).get(0)).getChildren().filtered(p -> p instanceof RestrictedTextField);
        for (Node property : properties) {
            TextField propField = (TextField) property;
            if (!ProductionNaviUtils.isAlphabet(propField, INPUT_MAX_SIZE)) {
                value = false;
            }
        }

        return value;
    }

    /**
     * 工程カンバンの入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckTab3() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTab3TextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab3CsvEncode)) {
            value = false;
        }

        // CSV形式：ファイル名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab3CsvFileName)) {
        //    value = false;
        //}

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab3CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab3CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab3CsvLineStart);
            value = false;
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue1)) {
            value = false;
        } else if (!this.withinRange(this.inputTab3CsvValue1.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab3CsvValue1);
            value = false;
        }
        
        // CSV形式：工程名・工程の番号 2019/12/18 工程名項目の追加対応  
        if (!ProductionNaviUtils.isNotNull(this.inputTab3CsvValue2)
                && !ProductionNaviUtils.isNotNull(this.inputTab3CsvValue8)) {
            value = false;
        } else {
            if (!this.inputTab3CsvValue2.getText().isEmpty()) {
                if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue2)) {
                    value = false;
                } else if (!this.withinRange(this.inputTab3CsvValue2.getText())) {
                    ProductionNaviUtils.setFieldError(this.inputTab3CsvValue2);
                    value = false;
                }
            }
            if (!this.inputTab3CsvValue8.getText().isEmpty()) {
                if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue8)) {
                    value = false;
                } else if (!this.withinRange(this.inputTab3CsvValue8.getText())) {
                    ProductionNaviUtils.setFieldError(this.inputTab3CsvValue8);
                    value = false;
                }
            }
        }

        // CSV形式：スキップ
        if (ProductionNaviUtils.isNotNull(this.inputTab3CsvValue3.getText())) {
            if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue3)) {
                value = false;
            } else if (!this.withinRange(this.inputTab3CsvValue3.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab3CsvValue3);
                value = false;
            }
        }

        // CSV形式：タクトタイム
        if (ProductionNaviUtils.isNotNull(this.inputTab3CsvValue9.getText())) {
            if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue9, false)) {
                value = false;
            } else if (!this.withinRange(this.inputTab3CsvValue9.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab3CsvValue9);
                value = false;
            }
        }

        // CSV形式：作業開始日時
        if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue4)) {
            value = false;
        } else if (!this.withinRange(this.inputTab3CsvValue4.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab3CsvValue4);
            value = false;
        }

        // CSV形式：完了予定日時
        if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue5)) {
            value = false;
        } else if (!this.withinRange(this.inputTab3CsvValue5.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab3CsvValue5);
            value = false;
        }

        // CSV形式：組織識別名
        if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue6)) {
            value = false;
        } else if (!this.withinRange(this.inputTab3CsvValue6.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab3CsvValue6);
            value = false;
        }

        // CSV形式：設備識別名
        if (ProductionNaviUtils.isNotNull(this.inputTab3CsvValue7.getText())) {
            if (!ProductionNaviUtils.isNumber(this.inputTab3CsvValue7, false)) {
                value = false;
            } else if (!this.withinRange(this.inputTab3CsvValue7.getText())) {
                ProductionNaviUtils.setFieldError(this.inputTab3CsvValue7);
                value = false;
            }
        }

        // Excel形式：シート名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab3ExcelSheetName)) {
        //    value = false;
        //}

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab3ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab3ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab3ExcelLineStart);
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue1, INPUT_MAX_SIZE)) {
            ProductionNaviUtils.setFieldError(this.inputTab3ExcelValue1);
            value = false;
        }
        
        // Excel形式：工程名・工程の番号 2019/12/18 工程名項目の追加対応  
        if (!ProductionNaviUtils.isNotNull(this.inputTab3ExcelValue2)
                && !ProductionNaviUtils.isNotNull(this.inputTab3ExcelValue8)) {
            value = false;
        } else {
            if (!this.inputTab3ExcelValue2.getText().isEmpty()
                    && !ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue2, INPUT_MAX_SIZE)) {
                value = false;
            }
            if (!this.inputTab3ExcelValue8.getText().isEmpty()
                    && !ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue8, INPUT_MAX_SIZE)) {
                value = false;
            }
        }

        // Excel形式：スキップ
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue3, INPUT_MAX_SIZE, false)) {
            value = false;
        }
        
        // Excel形式：タクトタイム
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue9, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        // Excel形式：作業開始日時
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue4, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：完了予定日時
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue5, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：組織識別名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue6, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：設備識別名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab3ExcelValue7, INPUT_MAX_SIZE, false)) {
            value = false;
        }

        return value;
    }

    /**
     * 工程カンバンプロパティの入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckTab4() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTab4TextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab4CsvEncode)) {
            value = false;
        }
        
        if (this.formatToggle.getSelectedToggle().getUserData().equals("2")) {
            value = isInputCheckTab4F2();
        } else {
            value = isInputCheckTab4F1();
        }
        return value;
    }

        
    /**
     * 工程カンバンプロパティの入力チェック(フォーマットA)
     *
     * @return 結果
     */
    private boolean isInputCheckTab4F1() {
        boolean value = true;
        
        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab4CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4CsvLineStart);
            value = false;
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab4CsvValue1)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4CsvValue1.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4CsvValue1);
            value = false;
        }

        // CSV形式：工程名・工程の番号
        if (!ProductionNaviUtils.isNotNull(this.inputTab4CsvValue2)
                && !ProductionNaviUtils.isNotNull(this.inputTab4CsvValue6)) {
            value = false;
        } else {
            if (!this.inputTab4CsvValue2.getText().isEmpty()) {
                if (!ProductionNaviUtils.isNumber(this.inputTab4CsvValue2)) {
                    value = false;
                } else if (!this.withinRange(this.inputTab4CsvValue2.getText())) {
                    ProductionNaviUtils.setFieldError(this.inputTab4CsvValue2);
                    value = false;
                }
            }
            if (!this.inputTab4CsvValue6.getText().isEmpty()) {
                if (!ProductionNaviUtils.isNumber(this.inputTab4CsvValue6)) {
                    value = false;
                } else if (!this.withinRange(this.inputTab4CsvValue6.getText())) {
                    ProductionNaviUtils.setFieldError(this.inputTab4CsvValue6);
                    value = false;
                }
            }
        }

        // CSV形式：プロパティ名
        if (!ProductionNaviUtils.isNumber(this.inputTab4CsvValue3)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4CsvValue3.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4CsvValue3);
            value = false;
        }

        // CSV形式：型
        if (!ProductionNaviUtils.isNumber(this.inputTab4CsvValue4)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4CsvValue4.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4CsvValue4);
            value = false;
        }

        // CSV形式：値
        if (!ProductionNaviUtils.isNumber(this.inputTab4CsvValue5)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4CsvValue5.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4CsvValue5);
            value = false;
        }

        // Excel形式：シート名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab4ExcelSheetName)) {
        //    value = false;
        //}

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab4ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4ExcelLineStart);
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab4ExcelValue1, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：工程名・工程の番号
        if (!ProductionNaviUtils.isNotNull(this.inputTab4ExcelValue2)
                && !ProductionNaviUtils.isNotNull(this.inputTab4ExcelValue6)) {
            value = false;
        } else {
            if (!this.inputTab4ExcelValue2.getText().isEmpty()
                    && !ProductionNaviUtils.isAlphabet(this.inputTab4ExcelValue2, INPUT_MAX_SIZE)) {
                value = false;
            }
            if (!this.inputTab4ExcelValue6.getText().isEmpty()
                    && !ProductionNaviUtils.isAlphabet(this.inputTab4ExcelValue6, INPUT_MAX_SIZE)) {
                value = false;
            }
        }

        // Excel形式：プロパティ名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab4ExcelValue3, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：型
        if (!ProductionNaviUtils.isAlphabet(this.inputTab4ExcelValue4, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：値
        if (!ProductionNaviUtils.isAlphabet(this.inputTab4ExcelValue5, INPUT_MAX_SIZE)) {
            value = false;
        }

        return value;
    }
    
    /**
     * 工程カンバンプロパティの入力チェック(フォーマットB)
     *
     * @return 結果
     */
    private boolean isInputCheckTab4F2() {
        boolean value = true;
        
        // CSV形式：ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab4F2CsvHeaderRow)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4F2CsvHeaderRow.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4F2CsvHeaderRow);
            value = false;
        }

        // CSV形式：読み込み開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab4F2CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4F2CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4F2CsvLineStart);
            value = false;
        }
        
        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab4F2CsvKanbanName)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4F2CsvKanbanName.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4F2CsvKanbanName);
            value = false;
        }
        
        // CSV形式：工程名・工程の番号
        if (!ProductionNaviUtils.isNotNull(this.inputTab4F2CsvWorkName)
                && !ProductionNaviUtils.isNotNull(this.inputTab4F2CsvWorkNo)) {
            value = false;
        } else {
            if (!this.inputTab4F2CsvWorkName.getText().isEmpty()) {
                if (!ProductionNaviUtils.isNumber(this.inputTab4F2CsvWorkName)) {
                    value = false;
                } else if (!this.withinRange(this.inputTab4F2CsvWorkName.getText())) {
                    ProductionNaviUtils.setFieldError(this.inputTab4F2CsvWorkName);
                    value = false;
                }
            }
            if (!this.inputTab4F2CsvWorkNo.getText().isEmpty()) {
                if (!ProductionNaviUtils.isNumber(this.inputTab4F2CsvWorkNo)) {
                    value = false;
                } else if (!this.withinRange(this.inputTab4F2CsvWorkNo.getText())) {
                    ProductionNaviUtils.setFieldError(this.inputTab4F2CsvWorkNo);
                    value = false;
                }
            }
        }

        // CSV形式：プロパティ
        List<Node> properties = ((GridPane) this.inputTab4F2CsvPropertyPane.getChildren().filtered(
                p -> p instanceof GridPane).get(0)).getChildren().filtered(p -> p instanceof RestrictedTextField);
        for (Node property : properties) {
            TextField propField = (TextField) property;
            if (!ProductionNaviUtils.isNumber(propField)) {
                value = false;
            } else if (!this.withinRange(propField.getText())) {
                ProductionNaviUtils.setFieldError(propField);
                value = false;
            }
        }

        // Excel形式：ヘッダー行
        if (!ProductionNaviUtils.isNumber(this.inputTab4F2ExcelHeaderRow)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4F2ExcelHeaderRow.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4F2ExcelHeaderRow);
            value = false;
        }

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab4F2ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab4F2ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab4F2ExcelLineStart);
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab4F2ExcelKanbanName, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：工程名・工程の番号
        if (!ProductionNaviUtils.isNotNull(this.inputTab4F2ExcelWorkName)
                && !ProductionNaviUtils.isNotNull(this.inputTab4F2ExcelWorkNo)) {
            value = false;
        } else {
            if (!this.inputTab4F2ExcelWorkName.getText().isEmpty()
                    && !ProductionNaviUtils.isAlphabet(this.inputTab4F2ExcelWorkName, INPUT_MAX_SIZE)) {
                value = false;
            }
            if (!this.inputTab4F2ExcelWorkNo.getText().isEmpty()
                    && !ProductionNaviUtils.isAlphabet(this.inputTab4F2ExcelWorkNo, INPUT_MAX_SIZE)) {
                value = false;
            }
        }

        // Excel形式：プロパティ
        properties = ((GridPane) this.inputTab4F2ExcelPropertyPane.getChildren().filtered(
                p -> p instanceof GridPane).get(0)).getChildren().filtered(p -> p instanceof RestrictedTextField);
        for (Node property : properties) {
            TextField propField = (TextField) property;
            if (!ProductionNaviUtils.isAlphabet(propField, INPUT_MAX_SIZE)) {
                value = false;
            }
        }


        return value;
    }

    /**
     * カンバンステータスの入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckTab5() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTab5TextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab5CsvEncode)) {
            value = false;
        }

        // CSV形式：ファイル名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab5CsvFileName)) {
        //    value = false;
        //}

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab5CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab5CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab5CsvLineStart);
            value = false;
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab5CsvValue1)) {
            value = false;
        } else if (!this.withinRange(this.inputTab5CsvValue1.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab5CsvValue1);
            value = false;
        }

        // CSV形式：ステータス
        if (!ProductionNaviUtils.isNumber(this.inputTab5CsvValue2)) {
            value = false;
        } else if (!this.withinRange(this.inputTab5CsvValue2.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab5CsvValue2);
            value = false;
        }

        // Excel形式：シート名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab5ExcelSheetName)) {
        //    value = false;
        //}

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab5ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab5ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab5ExcelLineStart);
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab5ExcelValue1, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：ステータス
        if (!ProductionNaviUtils.isAlphabet(this.inputTab5ExcelValue2, INPUT_MAX_SIZE)) {
            value = false;
        }

        return value;
    }

    /**
     * 設定情報読み込み処理
     */
    private void loadSetting() {
        logger.info("loadSetting");
        try {
            blockUI(true);

            Task task = new Task<ImportFormatInfo>() {
                @Override
                protected ImportFormatInfo call() throws Exception {
                    return ImportFormatFileUtil.load();
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
            this.format1Button.setUserData("1");
            this.format2Button.setUserData("2");
            this.inputTab4Format1Button.setUserData("1");
            this.inputTab4Format2Button.setUserData("2");
            
            AdProperty.load(ProductionNaviPropertyConstants.PROPERTY_TAG, ProductionNaviPropertyConstants.PROPERTY_FILE);
            Properties properties = AdProperty.getProperties(ProductionNaviPropertyConstants.PROPERTY_TAG);

            String encodeDatas = properties.getProperty(ProductionNaviPropertyConstants.KEY_MASTER_CSV_FILE_ENCODE, ProductionNaviPropertyConstants.MASTER_CSV_FILE_ENCODE);
            if (!encodeDatas.isEmpty()) {
                String[] buff = encodeDatas.split(",");
                for (String encodeData : buff) {
                    this.inputTab1CsvEncode.getItems().add(encodeData);
                    this.inputTab2CsvEncode.getItems().add(encodeData);
                    this.inputTab3CsvEncode.getItems().add(encodeData);
                    this.inputTab4CsvEncode.getItems().add(encodeData);
                    this.inputTab5CsvEncode.getItems().add(encodeData);
                    this.inputTab6CsvEncode.getItems().add(encodeData);
                }
            }

            // 工程順読み込み設定に関するチェックはそれぞれ排他的な選択のみ許す
            this.checkWorkflowRegex.selectedProperty().addListener((ob, o, n) -> {
                if (n) {
                    this.checkWorkflowWithModel.setSelected(false);
                }
            });
            this.checkWorkflowWithModel.selectedProperty().addListener((ob, o, n) -> {
                if (n) {
                    this.checkWorkflowRegex.setSelected(false);
                }
            });

            // 標準作業時間の表示
            if (Boolean.valueOf(AdProperty.getProperties().getProperty("enableCycleTimeImport", "false"))) {
                this.inputTab1Label12.setManaged(true);
                this.inputTab1CsvValue12.setManaged(true);
                this.inputTab1ExcelValue12.setManaged(true);
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 画面を更新する。
     *
     * @param importFormatInfo インポートフォーマット情報
     */
    private void updateView(ImportFormatInfo importFormatInfo) {
        this.dispKanbanFormatInfo(importFormatInfo.getKanbanFormatInfo());
        this.dispKanbanPropFormatInfoValue(importFormatInfo.getKanbanPropFormatInfo());
        this.dispWorkKanbanFormatInfoValue(importFormatInfo.getWorkKanbanFormatInfo());
        this.dispWorkKanbanPropFormatInfoValue(importFormatInfo.getWorkKanbanPropFormatInfo());
        this.dispKanbanStatusFormatInfoValue(importFormatInfo.getKanbanStatusFormatInfo());
        this.dispProductFormatInfoValue(importFormatInfo.getProductFormatInfo());
        this.initWorkflowRegexRecord();

        this.initKanbanPropCsvRecord();
        this.initKanbanPropXlsRecord();
        this.initWorkKanbanPropCsvRecord();
        this.initWorkKanbanPropXlsRecord();
    }

    /**
     * カンバン情報タブの表示を更新する。
     *
     * @param kanbanFormatInfo カンバンのフォーマット情報
     */
    private void dispKanbanFormatInfo(KanbanFormatInfo kanbanFormatInfo) {
        try {
            // ファイル名
            this.inputTab1CsvFileName.setText(kanbanFormatInfo.getCsvFileName());

            // エンコード
            this.inputTab1CsvEncode.setValue(kanbanFormatInfo.getCsvFileEncode());

            // シート名
            this.inputTab1ExcelSheetName.setText(kanbanFormatInfo.getXlsSheetName());

            // 読み込み開始行
            this.inputTab1CsvLineStart.setText(kanbanFormatInfo.getCsvStartRow());
            this.inputTab1ExcelLineStart.setText(kanbanFormatInfo.getXlsStartRow());

            // カンバン階層名
            this.inputTab1CsvValue1.setText(kanbanFormatInfo.getCsvHierarchyName());
            this.inputTab1ExcelValue1.setText(kanbanFormatInfo.getXlsHierarchyName());

            // カンバン名
            this.inputTab1CsvValue2.setText(kanbanFormatInfo.getCsvKanbanName());
            this.inputTab1ExcelValue2.setText(kanbanFormatInfo.getXlsKanbanName());

            // 工程順名
            this.inputTab1CsvValue3.setText(kanbanFormatInfo.getCsvWorkflowName());
            this.inputTab1ExcelValue3.setText(kanbanFormatInfo.getXlsWorkflowName());

            // 工程順版数
            this.inputTab1CsvValue4.setText(kanbanFormatInfo.getCsvWorkflowRev());
            this.inputTab1ExcelValue4.setText(kanbanFormatInfo.getXlsWorkflowRev());

            // モデル名
            this.inputTab1CsvValue5.setText(kanbanFormatInfo.getCsvModelName());
            this.inputTab1ExcelValue5.setText(kanbanFormatInfo.getXlsModelName());

            // 開始予定日時
            this.inputTab1CsvValue6.setText(kanbanFormatInfo.getCsvStartDateTime());
            this.inputTab1ExcelValue6.setText(kanbanFormatInfo.getXlsStartDateTime());

            // ロット生産
            this.inputTab1CsvValue7.setText(kanbanFormatInfo.getCsvProductionType());
            this.inputTab1ExcelValue7.setText(kanbanFormatInfo.getXlsProductionType());

            // ロット数量
            this.inputTab1CsvValue8.setText(kanbanFormatInfo.getCsvLotNum());
            this.inputTab1ExcelValue8.setText(kanbanFormatInfo.getXlsLotNum());

            // 製造番号
            this.inputTab1CsvValue9.setText(kanbanFormatInfo.getCsvProductionNumber());
            this.inputTab1ExcelValue9.setText(kanbanFormatInfo.getXlsProductionNumber());

            // 開始シリアル番号
            this.inputTab1CsvValue10.setText(kanbanFormatInfo.getCsvStartSerial());
            this.inputTab1ExcelValue10.setText(kanbanFormatInfo.getXlsStartSerial());

            // 終了シリアル番号
            this.inputTab1CsvValue11.setText(kanbanFormatInfo.getCsvEndSerial());
            this.inputTab1ExcelValue11.setText(kanbanFormatInfo.getXlsEndSerial());

            // 標準作業時間
            this.inputTab1CsvValue12.setText(kanbanFormatInfo.getCsvCycleTime());
            this.inputTab1ExcelValue12.setText(kanbanFormatInfo.getXlsCycleTime());

            // モデル名と工程順名の組み合わせで工程順を指定する
            this.checkWorkflowWithModel.setSelected(kanbanFormatInfo.getIsCheckWorkflowWithModel());
            
            // モデル名で工程順を指定する
            this.checkWorkflowRegex.setSelected(kanbanFormatInfo.getIsCheckWorkflowRegex());

            // モデル名の条件と工程順
            this.workflowRegexInfos.clear();
            this.workflowRegexInfos.addAll(kanbanFormatInfo.getWorkflowRegexInfos());

            // カンバン階層を指定する
            this.checkKanbanHierarchy.setSelected(kanbanFormatInfo.getIsCheckKanbanHierarchy());
            this.kanbanHierarchyTextField.setText(kanbanFormatInfo.getKanbanHierarchyName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンプロパティ情報タブの表示を更新する。
     *
     * @param kanbanPropFormatInfo カンバンプロパティのフォーマット情報
     */
    private void dispKanbanPropFormatInfoValue(KanbanPropFormatInfo kanbanPropFormatInfo) {
        try {
            // ファイル名
            this.inputTab2CsvFileName.setText(kanbanPropFormatInfo.getCsvFileName());

            // エンコード
            this.inputTab2CsvEncode.setValue(kanbanPropFormatInfo.getCsvFileEncode());

            // シート名
            this.inputTab2ExcelSheetName.setText(kanbanPropFormatInfo.getXlsSheetName());

            // フォーマット選択
            switch (kanbanPropFormatInfo.getSelectedFormat()) {
                case "2":
                    this.formatToggle.selectToggle(this.format2Button);
                    this.format2Button.setSelected(true);
                    break;
                default:
                    this.formatToggle.selectToggle(this.format1Button);
                    this.format1Button.setSelected(true);
            }
            this.onFormatButton(null);

            // フォーマット１
            // 読み込み開始行
            this.inputTab2CsvLineStart.setText(kanbanPropFormatInfo.getCsvStartRow());
            this.inputTab2ExcelLineStart.setText(kanbanPropFormatInfo.getXlsStartRow());

            // カンバン名
            this.inputTab2CsvValue1.setText(kanbanPropFormatInfo.getCsvKanbanName());
            this.inputTab2ExcelValue1.setText(kanbanPropFormatInfo.getXlsKanbanName());

            // 工程順名
            this.inputTab2CsvValue2.setText(kanbanPropFormatInfo.getCsvWorkflowName());
            this.inputTab2ExcelValue2.setText(kanbanPropFormatInfo.getXlsWorkflowName());

            // プロパティ名
            this.inputTab2CsvValue3.setText(kanbanPropFormatInfo.getCsvPropName());
            this.inputTab2ExcelValue3.setText(kanbanPropFormatInfo.getXlsPropName());

            // プロパティ型
            this.inputTab2CsvValue4.setText(kanbanPropFormatInfo.getCsvPropType());
            this.inputTab2ExcelValue4.setText(kanbanPropFormatInfo.getXlsPropType());

            // プロパティ値
            this.inputTab2CsvValue5.setText(kanbanPropFormatInfo.getCsvPropValue());
            this.inputTab2ExcelValue5.setText(kanbanPropFormatInfo.getXlsPropValue());

            // フォーマット２
            // ヘッダー行
            this.tab2F2CsvHeaderRow.setText(kanbanPropFormatInfo.getF2CsvHeaderRow());
            this.tab2F2XlsHeaderRow.setText(kanbanPropFormatInfo.getF2XlsHeaderRow());

            // 読み込み開始行
            this.tab2F2CsvStartRow.setText(kanbanPropFormatInfo.getF2CsvStartRow());
            this.tab2F2XlsStartRow.setText(kanbanPropFormatInfo.getF2XlsStartRow());

            // カンバン名
            this.tab2F2CsvKanbanName.setText(kanbanPropFormatInfo.getF2CsvKanbanName());
            this.tab2F2XlsKanbanName.setText(kanbanPropFormatInfo.getF2XlsKanbanName());

            // 工程順名
            this.tab2F2CsvWorkflowName.setText(kanbanPropFormatInfo.getF2CsvWorkflowName());
            this.tab2F2XlsWorkflowName.setText(kanbanPropFormatInfo.getF2XlsWorkflowName());

            // プロパティ
            this.csvKanbanPropInfos.clear();
            for (String value : kanbanPropFormatInfo.getF2CsvPropValues()) {
                this.csvKanbanPropInfos.add(new SimpleStringProperty(value));
            }

            this.xlsKanbanPropInfos.clear();
            for (String value : kanbanPropFormatInfo.getF2XlsPropValues()) {
                this.xlsKanbanPropInfos.add(new SimpleStringProperty(value));
            }

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバン情報タブの表示を更新する。
     *
     * @param workKanbanFormatInfo 工程カンバンのフォーマット情報
     */
    private void dispWorkKanbanFormatInfoValue(WorkKanbanFormatInfo workKanbanFormatInfo) {
        try {
            // ファイル名
            this.inputTab3CsvFileName.setText(workKanbanFormatInfo.getCsvFileName());

            // エンコード
            this.inputTab3CsvEncode.setValue(workKanbanFormatInfo.getCsvFileEncode());

            // シート名
            this.inputTab3ExcelSheetName.setText(workKanbanFormatInfo.getXlsSheetName());

            // 読み込み開始行
            this.inputTab3CsvLineStart.setText(workKanbanFormatInfo.getCsvStartRow());
            this.inputTab3ExcelLineStart.setText(workKanbanFormatInfo.getXlsStartRow());

            // カンバン名
            this.inputTab3CsvValue1.setText(workKanbanFormatInfo.getCsvKanbanName());
            this.inputTab3ExcelValue1.setText(workKanbanFormatInfo.getXlsKanbanName());

            // 工程の番号
            this.inputTab3CsvValue2.setText(workKanbanFormatInfo.getCsvWorkNum());
            this.inputTab3ExcelValue2.setText(workKanbanFormatInfo.getXlsWorkNum());

            // スキップフラグ
            this.inputTab3CsvValue3.setText(workKanbanFormatInfo.getCsvSkipFlag());
            this.inputTab3ExcelValue3.setText(workKanbanFormatInfo.getXlsSkipFlag());

            // 開始予定日時
            this.inputTab3CsvValue4.setText(workKanbanFormatInfo.getCsvStartDateTime());
            this.inputTab3ExcelValue4.setText(workKanbanFormatInfo.getXlsStartDateTime());

            // 完了予定日時
            this.inputTab3CsvValue5.setText(workKanbanFormatInfo.getCsvCompDateTime());
            this.inputTab3ExcelValue5.setText(workKanbanFormatInfo.getXlsCompDateTime());

            // 組織識別名
            this.inputTab3CsvValue6.setText(workKanbanFormatInfo.getCsvOrganizationIdentName());
            this.inputTab3ExcelValue6.setText(workKanbanFormatInfo.getXlsOrganizationIdentName());

            // 設備識別名
            this.inputTab3CsvValue7.setText(workKanbanFormatInfo.getCsvEquipmentIdentName());
            this.inputTab3ExcelValue7.setText(workKanbanFormatInfo.getXlsEquipmentIdentName());
            
            // 工程名 2019/12/18 工程名項目の追加対応  
            this.inputTab3CsvValue8.setText(workKanbanFormatInfo.getCsvWorkName());
            this.inputTab3ExcelValue8.setText(workKanbanFormatInfo.getXlsWorkName());
            
            // タクトタイム 2020/02/20 MES連携 タクトタイム追加
            this.inputTab3CsvValue9.setText(workKanbanFormatInfo.getCsvTactTime());
            this.inputTab3ExcelValue9.setText(workKanbanFormatInfo.getXlsTactTime());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンプロパティ情報タブの表示を更新する。
     *
     * @param workKanbanPropFormatInfo 工程カンバンプロパティのフォーマット情報
     */
    private void dispWorkKanbanPropFormatInfoValue(WorkKanbanPropFormatInfo workKanbanPropFormatInfo) {
        try {
            // ファイル名
            this.inputTab4CsvFileName.setText(workKanbanPropFormatInfo.getCsvFileName());

            // エンコード
            this.inputTab4CsvEncode.setValue(workKanbanPropFormatInfo.getCsvFileEncode());

            // シート名
            this.inputTab4ExcelSheetName.setText(workKanbanPropFormatInfo.getXlsSheetName());
            
            // フォーマット選択
            switch (workKanbanPropFormatInfo.getSelectedFormat()) {
                case "2":
                    this.inputTab4FormatToggle.selectToggle(this.inputTab4Format2Button);
                    this.inputTab4Format2Button.setSelected(true);
                    break;
                default:
                    this.inputTab4FormatToggle.selectToggle(this.inputTab4Format1Button);
                    this.inputTab4Format1Button.setSelected(true);
            }
            this.onTab4FormatButton(null);

            // フォーマットA
            // 読み込み開始行
            this.inputTab4CsvLineStart.setText(workKanbanPropFormatInfo.getCsvStartRow());
            this.inputTab4ExcelLineStart.setText(workKanbanPropFormatInfo.getXlsStartRow());

            // カンバン名
            this.inputTab4CsvValue1.setText(workKanbanPropFormatInfo.getCsvKanbanName());
            this.inputTab4ExcelValue1.setText(workKanbanPropFormatInfo.getXlsKanbanName());

            // 工程の番号
            this.inputTab4CsvValue2.setText(workKanbanPropFormatInfo.getCsvWorkNum());
            this.inputTab4ExcelValue2.setText(workKanbanPropFormatInfo.getXlsWorkNum());

            // プロパティ名
            this.inputTab4CsvValue3.setText(workKanbanPropFormatInfo.getCsvPropName());
            this.inputTab4ExcelValue3.setText(workKanbanPropFormatInfo.getXlsPropName());

            // プロパティ型
            this.inputTab4CsvValue4.setText(workKanbanPropFormatInfo.getCsvPropType());
            this.inputTab4ExcelValue4.setText(workKanbanPropFormatInfo.getXlsPropType());

            // プロパティ値
            this.inputTab4CsvValue5.setText(workKanbanPropFormatInfo.getCsvPropValue());
            this.inputTab4ExcelValue5.setText(workKanbanPropFormatInfo.getXlsPropValue());

            // 工程名
            this.inputTab4CsvValue6.setText(workKanbanPropFormatInfo.getCsvWorkName());
            this.inputTab4ExcelValue6.setText(workKanbanPropFormatInfo.getXlsWorkName());
            
            // フォーマットB
            // ヘッダー行
            this.inputTab4F2CsvHeaderRow.setText(workKanbanPropFormatInfo.getF2CsvHeaderRow());
            this.inputTab4F2ExcelHeaderRow.setText(workKanbanPropFormatInfo.getF2XlsHeaderRow());

            // 読み込み開始行
            this.inputTab4F2CsvLineStart.setText(workKanbanPropFormatInfo.getF2CsvStartRow());
            this.inputTab4F2ExcelLineStart.setText(workKanbanPropFormatInfo.getF2XlsStartRow());

            // カンバン名
            this.inputTab4F2CsvKanbanName.setText(workKanbanPropFormatInfo.getF2CsvKanbanName());
            this.inputTab4F2ExcelKanbanName.setText(workKanbanPropFormatInfo.getF2XlsKanbanName());
            
            // 工程名
            this.inputTab4F2CsvWorkName.setText(workKanbanPropFormatInfo.getF2CsvWorkName());
            this.inputTab4F2ExcelWorkName.setText(workKanbanPropFormatInfo.getF2XlsWorkName());
            
            // 工程の番号
            this.inputTab4F2CsvWorkNo.setText(workKanbanPropFormatInfo.getF2CsvWorkNo());
            this.inputTab4F2ExcelWorkNo.setText(workKanbanPropFormatInfo.getF2XlsWorkNo());

            // プロパティ
            this.csvWorkKanbanPropInfos.clear();
            for (String value : workKanbanPropFormatInfo.getF2CsvPropValues()) {
                this.csvWorkKanbanPropInfos.add(new SimpleStringProperty(value));
            }
            this.xlsWorkKanbanPropInfos.clear();
            for (String value : workKanbanPropFormatInfo.getF2XlsPropValues()) {
                this.xlsWorkKanbanPropInfos.add(new SimpleStringProperty(value));
            }
            
            // プロパティを組み合わせて読み込む
            this.inputTab4F2IsCheckUnionProp.setSelected(workKanbanPropFormatInfo.getF2IsCheckUnionProp());
            this.inputTab4F2UnionPropNewName.setText(workKanbanPropFormatInfo.getF2UnionPropNewName());
            this.inputTab4F2UnionPropLeftName.setText(workKanbanPropFormatInfo.getF2UnionPropLeftName());
            this.inputTab4F2UnionPropRightName.setText(workKanbanPropFormatInfo.getF2UnionPropRightName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * カンバンステータス情報タブの表示を更新する。
     *
     * @param kanbanStatusFormatInfo カンバンステータスのフォーマット情報
     */
    private void dispKanbanStatusFormatInfoValue(KanbanStatusFormatInfo kanbanStatusFormatInfo) {
        try {
            // ファイル名
            this.inputTab5CsvFileName.setText(kanbanStatusFormatInfo.getCsvFileName());

            // エンコード
            this.inputTab5CsvEncode.setValue(kanbanStatusFormatInfo.getCsvFileEncode());

            // シート名
            this.inputTab5ExcelSheetName.setText(kanbanStatusFormatInfo.getXlsSheetName());

            // 読み込み開始行
            this.inputTab5CsvLineStart.setText(kanbanStatusFormatInfo.getCsvStartRow());
            this.inputTab5ExcelLineStart.setText(kanbanStatusFormatInfo.getXlsStartRow());

            // カンバン名
            this.inputTab5CsvValue1.setText(kanbanStatusFormatInfo.getCsvKanbanName());
            this.inputTab5ExcelValue1.setText(kanbanStatusFormatInfo.getXlsKanbanName());

            // ステータス
            this.inputTab5CsvValue2.setText(kanbanStatusFormatInfo.getCsvKanbanStatus());
            this.inputTab5ExcelValue2.setText(kanbanStatusFormatInfo.getXlsKanbanStatus());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * モデル名の条件と工程順のリストを初期化する。
     */
    private void initWorkflowRegexRecord() {
        try {
            this.workflowRegexInfoPane.getChildren().clear();

            Table workflowRegexTable = new Table(this.workflowRegexInfoPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(true)
                    .isAddRecord(true)
                    .maxRecord(99);

            WorkflowRegexInfoRecordFactory workflowRegexRecordFactory = new WorkflowRegexInfoRecordFactory(workflowRegexTable, this.workflowRegexInfos, LocaleUtils.getString("key.ModelName.Regex"), LocaleUtils.getString("key.workflow"));
            workflowRegexRecordFactory.setOnActionEventListener(event -> {
                onSelectWorkflow(event);
            });

            workflowRegexTable.setAbstractRecordFactory(workflowRegexRecordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 範囲内か？
     *
     * @param value
     * @return
     */
    private boolean withinRange(String value) {
        boolean result = false;
        try {
            Integer num = Integer.valueOf(value);
            if (num > 0 && num < 1000) {
                // 範囲内
                result = true;
            }
        } catch (NumberFormatException e) {
            // 数値以外
        }
        return result;
    }

    /**
     * フォーマットボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onFormatButton(ActionEvent event) {
        try {
            Toggle selectedToggle = formatToggle.getSelectedToggle();
            if (Objects.isNull(selectedToggle)) {
                // 選択中のボタンを再度選択した時、選択が解除されるのをキャンセルする。
                selectedToggle = (Toggle) event.getSource();
                selectedToggle.setSelected(true);
            }

            if (selectedToggle.equals(this.format2Button)) {
                setVisibleKanbanPropGroup1(false);
                setVisibleKanbanPropGroup2(true);
            } else {
                setVisibleKanbanPropGroup1(true);
                setVisibleKanbanPropGroup2(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * カンバンプロパティ情報のフォーマット１の表示状態を設定する。
     *
     * @param isVisible 表示状態 (true：表示, false：非表示)
     */
    private void setVisibleKanbanPropGroup1(boolean isVisible) {
        // 読み込み開始行
        this.tab2F1LineStartLabel.setVisible(isVisible);
        this.inputTab2CsvLineStart.setVisible(isVisible);
        this.inputTab2ExcelLineStart.setVisible(isVisible);
        // カンバン名
        this.tab2F1KanbanNameLabel.setVisible(isVisible);
        this.inputTab2CsvValue1.setVisible(isVisible);
        this.inputTab2ExcelValue1.setVisible(isVisible);
        // 工程順名
        this.tab2F1WorkflowNameLabel.setVisible(isVisible);
        this.inputTab2CsvValue2.setVisible(isVisible);
        this.inputTab2ExcelValue2.setVisible(isVisible);
        // プロパティ名
        this.tab2F1PropNameLabel.setVisible(isVisible);
        this.inputTab2CsvValue3.setVisible(isVisible);
        this.inputTab2ExcelValue3.setVisible(isVisible);
        // プロパティ型
        this.tab2F1PropTypeLabel.setVisible(isVisible);
        this.inputTab2CsvValue4.setVisible(isVisible);
        this.inputTab2ExcelValue4.setVisible(isVisible);
        // プロパティ値
        this.tab2F1PropValueLabel.setVisible(isVisible);
        this.inputTab2CsvValue5.setVisible(isVisible);
        this.inputTab2ExcelValue5.setVisible(isVisible);
    }

    /**
     * カンバンプロパティ情報のフォーマット２の表示状態を設定する。
     *
     * @param isVisible 表示状態 (true：表示, false：非表示)
     */
    private void setVisibleKanbanPropGroup2(boolean isVisible) {
        // ヘッダー行
        this.tab2F2HeaderRowLabel.setVisible(isVisible);
        this.tab2F2CsvHeaderRow.setVisible(isVisible);
        this.tab2F2XlsHeaderRow.setVisible(isVisible);
        // 読み込み開始行
        this.tab2F2StartRowLabel.setVisible(isVisible);
        this.tab2F2CsvStartRow.setVisible(isVisible);
        this.tab2F2XlsStartRow.setVisible(isVisible);
        // カンバン名
        this.tab2F2KanbanNameLabel.setVisible(isVisible);
        this.tab2F2CsvKanbanName.setVisible(isVisible);
        this.tab2F2XlsKanbanName.setVisible(isVisible);
        // 工程順名
        this.tab2F2WorkflowNameLabel.setVisible(isVisible);
        this.tab2F2CsvWorkflowName.setVisible(isVisible);
        this.tab2F2XlsWorkflowName.setVisible(isVisible);
        // プロパティ
        this.tab2F2PropertyLabel.setVisible(isVisible);
        this.tab2F2CsvPropertyPane.setVisible(isVisible);
        this.tab2F2XlsPropertyPane.setVisible(isVisible);
    }
    
    /**
     * 工程カンバンプロパティ情報タブのフォーマットボタンのアクション
     *
     * @param event
     */
    @FXML
    private void onTab4FormatButton(ActionEvent event) {
        try {
            Toggle selectedToggle = inputTab4FormatToggle.getSelectedToggle();
            if (Objects.isNull(selectedToggle)) {
                // 選択中のボタンを再度選択した時、選択が解除されるのをキャンセルする。
                selectedToggle = (Toggle) event.getSource();
                selectedToggle.setSelected(true);
            }

            if (selectedToggle.equals(this.inputTab4Format2Button)) {
                setVisibleWorkKanbanPropGroup1(false);
                setVisibleWorkKanbanPropGroup2(true);
            } else {
                setVisibleWorkKanbanPropGroup1(true);
                setVisibleWorkKanbanPropGroup2(false);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 工程カンバンプロパティ情報のフォーマット１の表示状態を設定する。
     *
     * @param isVisible 表示状態 (true：表示, false：非表示)
     */
    private void setVisibleWorkKanbanPropGroup1(boolean isVisible) {
        // 読み込み開始行
        this.tab4F1LineStartLabel.setVisible(isVisible);
        this.inputTab4CsvLineStart.setVisible(isVisible);
        this.inputTab4ExcelLineStart.setVisible(isVisible);
        // カンバン名
        this.tab4F1KanbanNameLabel.setVisible(isVisible);
        this.inputTab4CsvValue1.setVisible(isVisible);
        this.inputTab4ExcelValue1.setVisible(isVisible);
        // 工程名
        this.tab4F1WorkNameLabel.setVisible(isVisible);
        this.inputTab4CsvValue2.setVisible(isVisible);
        this.inputTab4ExcelValue2.setVisible(isVisible);
        // 工程の番号
        this.tab4F1WorkNoLabel.setVisible(isVisible);
        this.inputTab4CsvValue6.setVisible(isVisible);
        this.inputTab4ExcelValue6.setVisible(isVisible);
        // プロパティ名
        this.tab4F1PropNameLabel.setVisible(isVisible);
        this.inputTab4CsvValue3.setVisible(isVisible);
        this.inputTab4ExcelValue3.setVisible(isVisible);
        // プロパティ型
        this.tab4F1PropTypeLabel.setVisible(isVisible);
        this.inputTab4CsvValue4.setVisible(isVisible);
        this.inputTab4ExcelValue4.setVisible(isVisible);
        // プロパティ値
        this.tab4F1PropValueLabel.setVisible(isVisible);
        this.inputTab4CsvValue5.setVisible(isVisible);
        this.inputTab4ExcelValue5.setVisible(isVisible);
    }

    /**
     * 工程カンバンプロパティ情報のフォーマット２の表示状態を設定する。
     *
     * @param isVisible 表示状態 (true：表示, false：非表示)
     */
    private void setVisibleWorkKanbanPropGroup2(boolean isVisible) {
        // ヘッダー行
        this.tab4F2HeaderRowLabel.setVisible(isVisible);
        this.inputTab4F2CsvHeaderRow.setVisible(isVisible);
        this.inputTab4F2ExcelHeaderRow.setVisible(isVisible);
        // 読み込み開始行
        this.tab4F2LineStartLabel.setVisible(isVisible);
        this.inputTab4F2CsvLineStart.setVisible(isVisible);
        this.inputTab4F2ExcelLineStart.setVisible(isVisible);
        // カンバン名
        this.tab4F2KanbanNameLabel.setVisible(isVisible);
        this.inputTab4F2CsvKanbanName.setVisible(isVisible);
        this.inputTab4F2ExcelKanbanName.setVisible(isVisible);
        // 工程名
        this.tab4F2WorkNameLabel.setVisible(isVisible);
        this.inputTab4F2CsvWorkName.setVisible(isVisible);
        this.inputTab4F2ExcelWorkName.setVisible(isVisible);
        // 工程の番号
        this.tab4F2WorkNoLabel.setVisible(isVisible);
        this.inputTab4F2CsvWorkNo.setVisible(isVisible);
        this.inputTab4F2ExcelWorkNo.setVisible(isVisible);
        // プロパティ
        this.tab4F2PropertyLabel.setVisible(isVisible);
        this.inputTab4F2CsvPropertyPane.setVisible(isVisible);
        this.inputTab4F2ExcelPropertyPane.setVisible(isVisible);
        this.inputTab4F2CsvPropertyPane.setManaged(isVisible);   // フォーマット1側にサイズの変更を反映させないようにvisibleだけでなくmanagedも操作
        this.inputTab4F2ExcelPropertyPane.setManaged(isVisible);
        //プロパティを組み合わせて読み込む
        this.inputTab4F2UnionPropVBox.setVisible(isVisible);
       
    }
    
    /**
     *
     */
    private void setTab6TextFieldNormal() {
        ProductionNaviUtils.setFieldNormal(this.inputTab6CsvEncode);
        ProductionNaviUtils.setFieldNormal(this.inputTab6CsvFileName);
        ProductionNaviUtils.setFieldNormal(this.inputTab6CsvLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab6CsvUniqueID);
        ProductionNaviUtils.setFieldNormal(this.inputTab6CsvKanbanName);

        ProductionNaviUtils.setFieldNormal(this.inputTab6ExcelSheetName);
        ProductionNaviUtils.setFieldNormal(this.inputTab6ExcelLineStart);
        ProductionNaviUtils.setFieldNormal(this.inputTab6ExcelUniqueID);
        ProductionNaviUtils.setFieldNormal(this.inputTab6ExcelKanbanName);
    }

    /**
     * 画面の情報から製品のフォーマット情報を作成する。
     *
     * @return 製品のフォーマット情報
     */
    private ProductFormatInfo createProductFormatInfo() {
        ProductFormatInfo productFormatInfo = new ProductFormatInfo();

        // ファイル名
        productFormatInfo.setCsvFileName(this.inputTab6CsvFileName.getText());
        // エンコード
        productFormatInfo.setCsvFileEncode(this.inputTab6CsvEncode.getSelectionModel().getSelectedItem());
        // シート名
        productFormatInfo.setXlsSheetName(this.inputTab6ExcelSheetName.getText());

        // 読み込み開始行
        productFormatInfo.setCsvStartRow(this.inputTab6CsvLineStart.getText());
        productFormatInfo.setXlsStartRow(this.inputTab6ExcelLineStart.getText());
        // ユニークID
        productFormatInfo.setCsvUniqueID(this.inputTab6CsvUniqueID.getText());
        productFormatInfo.setXlsUniqueID(this.inputTab6ExcelUniqueID.getText());
        // カンバン名
        productFormatInfo.setCsvKanbanName(this.inputTab6CsvKanbanName.getText());
        productFormatInfo.setXlsKanbanName(this.inputTab6ExcelKanbanName.getText());

        return productFormatInfo;
    }

    /**
     * 製品情報タブの表示を更新する。
     *
     * @param productFormatInfo 製品のフォーマット情報
     */
    private void dispProductFormatInfoValue(ProductFormatInfo productFormatInfo) {
        try {
            // ファイル名
            this.inputTab6CsvFileName.setText(productFormatInfo.getCsvFileName());

            // エンコード
            this.inputTab6CsvEncode.setValue(productFormatInfo.getCsvFileEncode());

            // シート名
            this.inputTab6ExcelSheetName.setText(productFormatInfo.getXlsSheetName());

            // 読み込み開始行
            this.inputTab6CsvLineStart.setText(productFormatInfo.getCsvStartRow());
            this.inputTab6ExcelLineStart.setText(productFormatInfo.getXlsStartRow());

            // ユニークID
            this.inputTab6CsvUniqueID.setText(productFormatInfo.getCsvUniqueID());
            this.inputTab6ExcelUniqueID.setText(productFormatInfo.getXlsUniqueID());

            // カンバン名
            this.inputTab6CsvKanbanName.setText(productFormatInfo.getCsvKanbanName());
            this.inputTab6ExcelKanbanName.setText(productFormatInfo.getXlsKanbanName());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     */
    private void initKanbanPropCsvRecord() {
        try {
            this.tab2F2CsvPropertyPane.getChildren().clear();

            Table table = new Table(this.tab2F2CsvPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(false)
                    .isAddRecord(true)
                    .maxRecord(99);

            TextFieldRecordFactory recordFactory = new TextFieldRecordFactory(table, this.csvKanbanPropInfos);

            table.setAbstractRecordFactory(recordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     *
     */
    private void initKanbanPropXlsRecord() {
        try {
            this.tab2F2XlsPropertyPane.getChildren().clear();

            Table table = new Table(this.tab2F2XlsPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(false)
                    .isAddRecord(true)
                    .maxRecord(99);

            TextFieldRecordFactory recordFactory = new TextFieldRecordFactory(table, this.xlsKanbanPropInfos);

            table.setAbstractRecordFactory(recordFactory);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     *
     */
    private void initWorkKanbanPropCsvRecord() {
        try {
            this.inputTab4F2CsvPropertyPane.getChildren().clear();

            Table table = new Table(this.inputTab4F2CsvPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(false)
                    .isAddRecord(true)
                    .maxRecord(99);

            TextFieldRecordFactory recordFactory = new TextFieldRecordFactory(table, this.csvWorkKanbanPropInfos);

            table.setAbstractRecordFactory(recordFactory);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     *
     */
    private void initWorkKanbanPropXlsRecord() {
        try {
            this.inputTab4F2ExcelPropertyPane.getChildren().clear();

            Table table = new Table(this.inputTab4F2ExcelPropertyPane.getChildren())
                    .styleClass("ContentTitleLabel")
                    .isColumnTitleRecord(false)
                    .isAddRecord(true)
                    .maxRecord(99);

            TextFieldRecordFactory recordFactory = new TextFieldRecordFactory(table, this.xlsWorkKanbanPropInfos);

            table.setAbstractRecordFactory(recordFactory);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 製品の入力チェック
     *
     * @return 結果
     */
    private boolean isInputCheckTab6() {
        boolean value = true;

        // 入力欄を通常表示にする。
        this.setTab6TextFieldNormal();

        // CSV形式：エンコード
        if (!ProductionNaviUtils.isNotNull(this.inputTab6CsvEncode)) {
            value = false;
        }

        // CSV形式：ファイル名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab6CsvFileName)) {
        //    value = false;
        //}

        // CSV形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab6CsvLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab6CsvLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab6CsvLineStart);
            value = false;
        }

        // CSV形式：ユニークID
        if (!ProductionNaviUtils.isNumber(this.inputTab6CsvUniqueID)) {
            value = false;
        } else if (!this.withinRange(this.inputTab6CsvUniqueID.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab6CsvUniqueID);
            value = false;
        }

        // CSV形式：カンバン名
        if (!ProductionNaviUtils.isNumber(this.inputTab6CsvKanbanName)) {
            value = false;
        } else if (!this.withinRange(this.inputTab6CsvKanbanName.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab6CsvKanbanName);
            value = false;
        }

        // Excel形式：シート名
        //if (!ProductionNaviUtils.isNotNull(this.inputTab6ExcelSheetName)) {
        //    value = false;
        //}

        // Excel形式：開始行
        if (!ProductionNaviUtils.isNumber(this.inputTab6ExcelLineStart)) {
            value = false;
        } else if (!this.withinRange(this.inputTab6ExcelLineStart.getText())) {
            ProductionNaviUtils.setFieldError(this.inputTab6ExcelLineStart);
            value = false;
        }

        // Excel形式：ユニークID
        if (!ProductionNaviUtils.isAlphabet(this.inputTab6ExcelUniqueID, INPUT_MAX_SIZE)) {
            value = false;
        }

        // Excel形式：カンバン名
        if (!ProductionNaviUtils.isAlphabet(this.inputTab6ExcelKanbanName, INPUT_MAX_SIZE)) {
            value = false;
        }

        return value;
    }
}
