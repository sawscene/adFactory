/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.ReasonRecordFactory2;
import adtekfuji.admanagerapp.systemsettingplugin.utils.ResponseUtils;
import adtekfuji.clientservice.ReasonInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonCategoryInfoEntity;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 理由編集コントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "ReasonEditCompo", fxmlPath = "/fxml/admanagersystemsettingplugin/reason_edit_compo.fxml")
public class ReasonEditController extends ReasonCategoryEditController implements Initializable, ArgumentDelivery, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ReasonInfoFacade reasonFacade = new ReasonInfoFacade();

    private LinkedList<ReasonInfoEntity> oldValues = new LinkedList<>();
    private final LinkedList<ReasonInfoEntity> newValues = new LinkedList<>();

    private static Thread thread = null;

    @FXML
    private Label titleLabel;
    @FXML
    private Button registButton;
    @FXML
    private VBox propertyPane;

    /**
     * コンストラクタ
     */
    public ReasonEditController() {
    }
    
    /**
     * 画面を初期化する。
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        
        // 役割の権限によるボタン無効化.
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            registButton.setDisable(true);
        }
        
        updateView();
    }

    /**
     * パラメーターを設定する。
     * 
     * @param argument 
     */
    @Override
    public void setArgument(Object argument) {
        if (argument instanceof ReasonTypeEnum) {
            ReasonTypeEnum type = (ReasonTypeEnum) argument;
            logger.info("setArgument: ReasonTypeEnum={}", type);
            
            this.setReasonCategory(type);

            switch (type) {
                case TYPE_INTERRUPT:
                    this.titleLabel.setText(LocaleUtils.getString("key.EditInterruptReasonTitle"));
                    break;
                case TYPE_DELAY:
                    this.titleLabel.setText(LocaleUtils.getString("key.EditDelayReasonTitle"));
                    break;
                case TYPE_CALL:
                    this.titleLabel.setText(LocaleUtils.getString("key.callReasons"));
                    break;
            }
        
            this.updateTree(0L);
        }
    }
    
    /**
     * 保存ボタンが押下された。
     * 
     * @param event 
     */
    @FXML
    private void onRegist(ActionEvent event) {
        logger.info("onRegist start");
        this.regist(null);
    }

    /**
     * 変更内容を登録する。
     *
     * @return true: 成功、false: 失敗  
     */
    @Override
    protected boolean regist(ReasonCategoryInfoEntity category) {
        
        if (Objects.isNull(category)) {
            category = getSelectedReasonCategory();
            if (Objects.isNull(category)) {
                return true;
            }
        }
        
        final Long categoryId = category.getId();
                        
        Boolean isCancel = true;
        try {
            blockUI(true);

            if (checkEmpty()) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return false;
            }

            // 重複チェック
            Set<String> set = new HashSet<>();
            for (ReasonInfoEntity reason : newValues) {
                long count = oldValues.stream().filter(p -> StringUtils.equalsIgnoreCase(p.getReason(), reason.getReason()) 
                        && !Objects.equals(p.getId(), reason.getId())).count();

                if (count > 0) {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), 
                            String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), reason.getReason()));
                    return false;
                }

                if (set.contains(reason.getReason())) {
                    sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), 
                            String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), reason.getReason()));
                    return false;
                } else {
                    set.add(reason.getReason());
                }
            }

            isCancel = false;

            Task task = new Task<ResponseEntity>() {
                @Override
                protected ResponseEntity call() throws Exception {
                    ResponseEntity res = null;

                    for (ReasonInfoEntity entity : getRemoved()) {
                        entity.updateData();
                        res = reasonFacade.remove(entity.getId());
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }

                    for (ReasonInfoEntity entity : getAdded()) {

                        entity.setType(getReasonType());
                        entity.setReasonCategoryId(categoryId);
                        entity.updateData();

                        res = reasonFacade.add(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }

                    for (ReasonInfoEntity entity : getUpdated()) {
                        entity.updateData();
                        res = reasonFacade.update(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    return res;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    try {
                        // 処理結果
                        ResponseEntity res = this.getValue();
                        if (Objects.isNull(res) || Objects.isNull(res.getErrorType())) {
                            return;
                        }

                        if (!ServerErrorTypeEnum.SUCCESS.equals(res.getErrorType())) {
                            ResponseUtils.showAlertDialog(res);
                        }

                        CacheUtils.removeCacheData(ReasonInfoEntity.class);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                        updateView();
                    }
                }

                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                    updateView();
                }
            };
            thread = new Thread(task);
            thread.start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            if (isCancel) {
                blockUI(false);
            }
        }

        return true;
    }

    /**
     * 画面を更新する。
     *
     */
    private void updateView() {
        try {
            if (Objects.nonNull(thread) && thread.isAlive()) {
                thread.join();
            }

            Platform.runLater(() -> {
                registButton.setDisable(true);
                blockUI(true);
            });
            
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        oldValues.clear();
                        newValues.clear();

                        ReasonCategoryInfoEntity category = getSelectedReasonCategory();
                        if (Objects.isNull(category) || category.getId() == 0L) {
                            Platform.runLater(() -> propertyPane.getChildren().clear());
                            return null;
                        }
                        
                        oldValues = new LinkedList<>(reasonFacade.findAllByCategoryName(category.getReasonCategoryName(), getReasonType()));
                        oldValues.stream()
                                .map(o -> new ReasonInfoEntity(o))
                                .forEach(o -> newValues.add(o));
                        newValues.sort(Comparator.comparing(o -> o.getReason()));

                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();

                            Table table = new Table(propertyPane.getChildren())
                                    .styleClass("ContentTitleLabel")
                                    .title(category.getReasonCategoryName())
                                    .isColumnTitleRecord(Boolean.TRUE)
                                    .isAddRecord(Boolean.TRUE);
                                        
                            table.setAbstractRecordFactory(new ReasonRecordFactory2(table, newValues));

                            if (LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
                                registButton.setDisable(false);
                            }
                        });
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        Platform.runLater(() -> blockUI(false));
                    }
                    return null;
                }
            };
            this.thread = new Thread(task);
            this.thread.start();

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 追加された理由を取得する。
     *
     * @return 理由情報一覧
     */
    private List<ReasonInfoEntity> getAdded() {
        ArrayList<ReasonInfoEntity> subArr1 = new ArrayList(newValues);
        ArrayList<ReasonInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(oldValues);
        subArr1.stream().forEach(o -> {
            if (Objects.isNull(o.getId())) {
                subArr2.add(o);
            }
        });
        return subArr2;
    }

    /**
     * 更新された理由を取得する。
     *
     * @return 理由情報一覧
     */
    private List<ReasonInfoEntity> getUpdated() {
        ArrayList<ReasonInfoEntity> subArr1 = new ArrayList(newValues);
        ArrayList<ReasonInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(oldValues);
        subArr1.stream().forEach(o -> {
            if (Objects.nonNull(o.getId())) {
                subArr2.add(o);
            }
        });
        return subArr2;
    }

    /**
     * 削除された理由を取得する。
     *
     * @return 理由情報一覧
     */
    private List<ReasonInfoEntity> getRemoved() {
        boolean delFlag = true;
        ArrayList<ReasonInfoEntity> subArr1 = new ArrayList(oldValues);
        ArrayList<ReasonInfoEntity> subArr2 = new ArrayList();
        subArr1.removeAll(newValues);
        for (ReasonInfoEntity s : subArr1) {
            for (ReasonInfoEntity op : newValues) {
                if (Objects.equals(s.getId(), op.getId())) {
                    delFlag = false;
                    break;
                }
            }
            if (!delFlag) {
                delFlag = true;
            } else {
                subArr2.add(s);
            }
        }
        return subArr2;
    }

    /**
     * 未入力チェック
     *
     * @return 未入力:true
     */
    private boolean checkEmpty() {
        if (getAdded().stream().anyMatch((e) -> (Objects.isNull(e.getReason()) || e.getReason().isEmpty()))) {
            return true;
        }
        return getUpdated().stream().anyMatch((e) -> (Objects.isNull(e.getReason()) || e.getReason().isEmpty()));
    }
    
    /**
     * 変更がかけられたかチェック
     * 
     * @return
     */
    @Override
    protected boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        return !getRemoved().isEmpty() || !getAdded().isEmpty() || !getUpdated().isEmpty();        
    }

    /**
     * 画面破棄時に内容に変更がないか調べて変更があるなら保存する
     * 
     * @return 
     */
    @Override
    public boolean destoryComponent() {
        if (isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return regist(null);
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }

    /**
     * 理由区分が選択された。
     * 
     * @param category 理由区分
     */
    @Override
    protected void selectedReasonCategory(ReasonCategoryInfoEntity category) {
        updateView();
    }
}
