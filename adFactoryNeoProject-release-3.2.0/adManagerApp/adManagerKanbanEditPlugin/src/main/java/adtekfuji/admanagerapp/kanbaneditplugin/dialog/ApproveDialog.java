/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.dialog;

import adtekfuji.admanagerapp.kanbaneditplugin.common.Constants;
import adtekfuji.clientservice.KanbanInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.DialogHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.kanban.ApprovalEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 承認ダイアログ
 *
 * @author y-harada
 */
@FxComponent(id = "ApproveDialog", fxmlPath = "/fxml/admanagerapp/kanbaneditplugin/approve_dialog.fxml")
public class ApproveDialog implements Initializable, ArgumentDelivery, DialogHandler {

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final Properties properties = AdProperty.getProperties();
    private final LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
    
    private int approveNum;
    private static final int MAX_CHARS = 256;
    
    private List<KanbanInfoEntity> kanbans;
    private Dialog dialog;
    private TextArea reasonTextArea;
    
    @FXML
    private VBox messageVBox;
    @FXML
    private ComboBox<Integer> approveNumComboBox;
    @FXML
    private Pane reasonPane;
    
    /**
     * 承認コンボボックス用セルファクトリー
     *
     */
    private class ApproveNumComboBoxCellFactory extends ListCell<Integer> {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText("");
            } else {
                setText(LocaleUtils.getString("key.Approve") + Integer.toString(item));
            }
        }
    }

    /**
     * コンストラクタ
     */
    public ApproveDialog() {
    }

    /**
     * 初期化
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.approveNum = Integer.parseInt(properties.getProperty(Constants.APPROVE_NUM, Constants.APPROVE_NUM_DEF)); // 承認者の人数設定を取得
        this.approveNum = this.approveNum <= 0 ? 0                    // 0以下は0
                        : this.approveNum >= 3 ? 3 : this.approveNum; // 3以上は3
        
        Callback<ListView<Integer>, ListCell<Integer>> comboCellFactory = (ListView<Integer> param) -> new ApproveNumComboBoxCellFactory();
        this.approveNumComboBox.setButtonCell(new ApproveNumComboBoxCellFactory());
        this.approveNumComboBox.setCellFactory(comboCellFactory);
        
        this.reasonTextArea = new TextArea() {
            @Override
            public void replaceText(int start, int end, String text) {
                if (text.matches("^(?!.*\r\n|\n|\r).*$")) {
                    super.replaceText(start, end, text);
                }
            }
            @Override
            public void replaceSelection(String text) {
                if (text.matches("^(?!.*\r\n|\n|\r).*$")) {
                    super.replaceSelection(text);
                }
            }
        };
        this.reasonTextArea.setTextFormatter(new TextFormatter<String>(change -> 
               change.getControlNewText().length() <= MAX_CHARS ? change : null));
        this.reasonTextArea.setPrefWidth(300);
        this.reasonTextArea.setPrefRowCount(2);
        this.reasonTextArea.setWrapText(true);
        this.reasonPane.getChildren().add(reasonTextArea);

        // 承認コンボボックス設定
        List<Integer> orderList = new ArrayList();
        for (int i = 1; i <= approveNum; i++) {
            orderList.add(i);   // 1,2,3,4...
        }
        this.approveNumComboBox.setItems(FXCollections.observableArrayList(orderList));
        this.approveNumComboBox.getSelectionModel().select(0);
    }

    /**
     * 引数取得
     * @param argument 引数
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof List) {
            this.kanbans = (List<KanbanInfoEntity>) argument;

            StringBuilder sb = new StringBuilder();
            if (!kanbans.isEmpty() && kanbans.size() == 1) {
                String kanbanName = kanbans.get(0).getKanbanName();
                if (kanbanName.length() > 27) {
                    // <カンバン名>
                    // を承認しますか？
                    Label kanbanNameLabel = new Label(kanbanName);
                    Label messageLabel = new Label(String.format(LocaleUtils.getString("key.ApprovalKanbanMessage"), ""));
                    this.messageVBox.getChildren().addAll(kanbanNameLabel, messageLabel);
                } else {
                    // <カンバン名>を承認しますか？
                    Label messageLabel = new Label(String.format(LocaleUtils.getString("key.ApprovalKanbanMessage"), kanbanName));
                    this.messageVBox.getChildren().addAll(messageLabel);
                }
            } else {
                // カンバンを承認しますか？
                Label messageLabel = new Label(LocaleUtils.getString("key.ApprovalKanbansMessage"));
                this.messageVBox.getChildren().addAll(messageLabel);
            }
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
        this.dialog.getDialogPane().getScene().getWindow().setOnCloseRequest((WindowEvent we) -> {
            this.cancelDialog();
        });
    }
    
    /**
     * 可ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onApproveButton(ActionEvent event) {
        logger.info("onApproveButton start.");
        this.update(Boolean.TRUE);
    }
    
    /**
     * 否ボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onDisApproveButton(ActionEvent event) {
        logger.info("onDisApproveButton start.");
        this.update(Boolean.FALSE);
    }
    
    /**
     * 取り消しボタンのアクション
     *
     * @param event イベント
     */
    @FXML
    private void onRevocationButton(ActionEvent event) {
        logger.info("onRevocationButton start.");
        this.update(null);
    }

    /**
     * 承認情報の更新
     *
     * @param approve 可・否・取り消し
     */
    private void update(Boolean approve) {
        logger.info("update start.");
        boolean isSuccess = true;
        Integer order = this.approveNumComboBox.getValue();
        String approver = Objects.isNull(this.loginUserInfoEntity.getName()) ? LoginUserInfoEntity.ADMIN_LOGIN_ID : this.loginUserInfoEntity.getName();
        String reason = this.reasonTextArea.getText();
        Date now = new Date();
        
        // 上書きの確認
        if(Objects.nonNull(approve)){
            boolean overwrite = false;
            for (KanbanInfoEntity kanban : this.kanbans) {
                if (Objects.isNull(kanban.getApproval())) {
                    // 承認情報を持っていないカンバンはスルー
                    continue;
                }

                List<ApprovalEntity> approves = JsonUtils.jsonToObjects(kanban.getApproval(), ApprovalEntity[].class);
                for (ApprovalEntity app : approves) {
                    if (Objects.equals(app.getOrder(), order) && Objects.nonNull(app.getApprove())) {
                        // 承認可否がnull(取り消し)でなければ承認情報が存在するので上書きの確認
                        overwrite = true;
                        break;
                    }
                }

                if (overwrite) {
                    // 上書き確認ダイアログ (すでに承認されているカンバンがあります。上書きしますか？)
                    ButtonType ret = sc.showOkCanselDialog(Alert.AlertType.CONFIRMATION, LocaleUtils.getString("key.Approve"), LocaleUtils.getString("key.ApproveOverwrite"));
                    if (!ret.equals(ButtonType.OK)) {
                        return;
                    }
                    break;
                }
            }
        }

        for (KanbanInfoEntity kanban : this.kanbans) {
            Long id = kanban.getKanbanId();
            Integer verInfo = kanban.getVerInfo();
            ApprovalEntity approval = new ApprovalEntity();
            approval.setOrder(order);
            approval.setApprove(approve);
            approval.setApprover(approver);
            approval.setReason(reason);
            approval.setDate(now);
            
            // 承認情報登録更新API
            if (!this.updateApproval(id, verInfo, approval)) {
                isSuccess = false;
            }
        }
        
        if (!isSuccess) {
            // エラーメッセージを表示する
            SceneContiner sc = SceneContiner.getInstance();
            sc.showAlert(Alert.AlertType.ERROR, LocaleUtils.getString("key.Approve"), LocaleUtils.getString("key.alert.systemError"));
        } else {
            // ダイアログを閉じる
            this.dialog.setResult(ButtonType.OK);
            this.dialog.close();
        }
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
     * カンバンIDを指定して、承認情報を追加・更新する。
     *
     * @param id カンバンID
     * @param approval 承認情報
     * @return True=成功
     */
    private boolean updateApproval(Long id, Integer verInfo, ApprovalEntity approval) {
        logger.info("updateApproval start: kanbanId={}", id);
        try {
            KanbanInfoFacade facade = new KanbanInfoFacade();
            ResponseEntity res = facade.updateApproval(id, verInfo, approval, loginUserInfoEntity.getId());
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
            logger.info("updateApproval end");
        }
    }
}
