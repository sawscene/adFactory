/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.LabelRecordFactory;
import adtekfuji.admanagerapp.systemsettingplugin.utils.ResponseUtils;
import adtekfuji.clientservice.LabelInfoFacade;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.LabelInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ラベル設定画面のコントローラ
 *
 * @author kentarou.suzuki
 */
@FxComponent(id = "LabelEditCompo", fxmlPath = "/fxml/compo/label_edit_compo.fxml")
public class LabelEditCompoController implements Initializable, ComponentHandler {

    /**
     * ログ出力クラス
     */
    private final static Logger logger = LogManager.getLogger();
    /**
     * リソースバンドル
     */
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    /**
     * Sceneコンテナ
     */
    private final SceneContiner sc = SceneContiner.getInstance();
    /**
     * ラベルマスタ取得用RESTクラス
     */
    private final static LabelInfoFacade labelInfoFacade = new LabelInfoFacade();
    /**
     * ラベルマスタリスト(キャッシュ用)
     */
    private LinkedList<LabelInfoEntity> cacheDatas = new LinkedList<>();
    /**
     * ラベルマスタリスト(編集用)
     */
    private LinkedList<LabelInfoEntity> operateDatas = new LinkedList<>();
    /**
     * 登録用通信スレッド
     */
    private static Thread registThread = null;
    /**
     * ラベルマスタの表示順
     */
    private Integer displayOrder;
    /**
     * 最大登録件数
     */
    private final static Long RECORD_MAX_COUNT = 100L;
    /**
     * 最大文字数
     */
    private final static Long CHARACTER_MAX_LENGTH = 128L;

    /**
     * 登録ボタン
     */
    @FXML
    private Button registButton;
    /**
     * テーブル配置先のVBox
     */
    @FXML
    private VBox propertyPane;
    /**
     * プログレスインジケータ配置先のPane
     */
    @FXML
    private Pane Progress;

    /**
     * ラベル設定画面を初期化する。
     * 
     * @param url URL
     * @param rb リソースバンドル
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 役割の権限によるボタン無効化
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            registButton.setDisable(true);
        }
        runUpdateViewThread();
    }

    /**
     * 画面破棄時に変更内容があるかどうかを調べて変更があるなら保存する。
     * 
     * @return 変更内容がないか、変更内容があり保存した場合はtrue、変更内容があり保存しなかった場合はfalse
     */
    @Override
    public boolean destoryComponent() {
        if (isChanged()) {
            // 「入力内容が保存されていません。保存しますか?」を表示
            String title = LocaleUtils.getString("key.confirm");
            String message = LocaleUtils.getString("key.confirm.destroy");

            ButtonType buttonType = sc.showMessageBox(Alert.AlertType.NONE, title, message, new ButtonType[]{ButtonType.YES, ButtonType.NO, ButtonType.CANCEL}, ButtonType.CANCEL);
            if (ButtonType.YES == buttonType) {
                return runRegistThread();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 保存されていない変更内容があるかどうかを返す。
     * 
     * @return 保存されていない変更内容がある場合はtrue、そうでない場合はfalse
     */
    private boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        if (!getRemovedDatas().isEmpty() || !getAdditionalDatas().isEmpty() || !getUpdateDatas().isEmpty()) {
            return true;
        }
        return false;        
    }

    /**
     * 登録ボタン押下時のアクション
     * 
     * @param event イベント
     */
    @FXML
    private void OnRegist(ActionEvent event) {
        logger.info("regist start");

        runRegistThread();
    }

    /**
     * ラベルマスタを登録する。
     * 
     * @return 登録が成功した場合はtrue、登録が失敗、または、登録しなかった場合はfalse
     */
    private boolean runRegistThread() {
        Boolean isCancel = true;
        try {
            blockUI(true);

            // 入力チェック
            if (existsInvalidItems() || existsDuplicateRecords() || hasExceededMaxRecords()) {
                return false;
            }

            isCancel = false;

            Task task = new Task<ResponseEntity>() {
                /**
                 * Taskが実行されるときに呼び出される。
                 * 
                 * <pre>
                 * ラベルマスタを登録する。
                 * </pre>
                 * 
                 * @return バックグラウンド処理の結果(サーバからの応答)
                 * @throws Exception バックグラウンド操作中に発生した未処理の例外
                 */
                @Override
                protected ResponseEntity call() throws Exception {
                    ResponseEntity res = null;
                    
                    // 優先度を更新する
                    updateLabelPriority();
                    
                    logger.info("Delete");
                    for (LabelInfoEntity entity : getRemovedDatas()) {
                        entity.updateData();
                        res = labelInfoFacade.remove(entity.getLabelId());
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("Add");
                    for (LabelInfoEntity entity : getAdditionalDatas()) {
                        entity.updateData();
                        res = labelInfoFacade.add(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    logger.info("Update");
                    for (LabelInfoEntity entity : getUpdateDatas()) {
                        entity.updateData();
                        res = labelInfoFacade.update(entity);
                        if (!res.isSuccess()) {
                            return res;
                        }
                    }
                    return res;
                }

                /**
                 * Taskの状態がSUCCEEDED状態に遷移するたびに呼び出される。
                 * 
                 * <pre>
                 * UIロックを解除し、画面表示を更新する。
                 * サーバからの応答内容が成功でない場合は警告メッセージを表示する。
                 * </pre>
                 */
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

                        // ラベルマスタのキャッシュを削除する。
                        CacheUtils.removeCacheData(LabelInfoEntity.class);

                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        blockUI(false);
                        runUpdateViewThread();
                    }
                }

                /**
                 * Taskの状態がFAILED状態に遷移するたびに呼び出される。
                 * 
                 * <pre>
                 * UIロックを解除し、画面表示を更新する。
                 * </pre>
                 */
                @Override
                protected void failed() {
                    super.failed();
                    blockUI(false);
                    runUpdateViewThread();
                }
            };
            registThread = new Thread(task);
            registThread.start();

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
     * 画面表示を更新する。
     */
    private void runUpdateViewThread() {
        try {
            // 登録処理中の場合、その完了を待つ
            if (Objects.nonNull(registThread) && registThread.isAlive()) {
                registThread.join();
            }

            Platform.runLater(() -> {
                blockUI(true);
            });
            Task task = new Task<Void>() {
                /**
                 * Taskが実行されるときに呼び出される。
                 * 
                 * <pre>
                 * 画面表示を更新する。
                 * </pre>
                 * 
                 * @return バックグラウンド処理の結果(サーバからの応答)
                 * @throws Exception バックグラウンド操作中に発生した未処理の例外
                 */
                @Override
                protected Void call() throws Exception {
                    try {
                        cacheDatas.clear();
                        operateDatas.clear();

                        // ラベルマスタ全件取得
                        cacheDatas = new LinkedList<>(labelInfoFacade.findRange(null, null));
                        operateDatas = new LinkedList<>(cloneLabel(cacheDatas));
                        
                        // 優先度順でソートする
                        operateDatas.sort(Comparator.comparing(label -> label.getLabelPriority()));
                        
                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();
                            Table table = new Table(propertyPane.getChildren()).isAddRecord(Boolean.TRUE)
                                    .title(LocaleUtils.getString("key.EditLabel")).isColumnTitleRecord(Boolean.TRUE).styleClass("ContentTitleLabel");
                            table.setAbstractRecordFactory(new LabelRecordFactory(table, operateDatas));
                        });
                    } catch (Exception ex) {
                        logger.fatal(ex, ex);
                    } finally {
                        Platform.runLater(() -> {
                            blockUI(false);
                        });
                    }
                    return null;
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * UIをロックする。
     *
     * @param flg ロックする場合はtrue、ロック解除する場合はfalse
     */
    private void blockUI(boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }

    /**
     * 入力項目に不正値があるかどうかを返す。
     *
     * @return 入力項目に不正がある場合はtrue、そうでない場合はfalse
     */
    private boolean existsInvalidItems() {
        for (LabelInfoEntity entity : operateDatas) {
            // ラベル名：必須チェック
            if (StringUtils.isBlank(entity.getLabelName())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), LocaleUtils.getString("key.NotInputMessage"));
                return true;
            }
            // ラベル名：文字数チェック
            if (entity.getLabelName().length() > CHARACTER_MAX_LENGTH) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.warn.enterCharacters"), CHARACTER_MAX_LENGTH));
                return true;
            }
        }
        
        return false;
    }

    /**
     * 重複レコードがあるかどうかを返す。
     *
     * @return 重複データがある場合はtrue、そうでない場合はfalse
     */
    private boolean existsDuplicateRecords() {
        Set<String> labelNames = new HashSet<>();
        for (LabelInfoEntity operates : operateDatas) {
            // 別IDで同一名称の登録は不可
            long count = cacheDatas.stream().filter(caches -> caches.getLabelName().equals(operates.getLabelName()) && !caches.getLabelId().equals(operates.getLabelId())).count();
            if (count > 0) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), operates.getLabelName()));
                return true;
            }
            // 同一名称の複数登録は不可
            if (labelNames.contains(operates.getLabelName())) {
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.AddErrAlreadyMessage"), operates.getLabelName()));
                return true;
            } else {
                labelNames.add(operates.getLabelName());
            }
        }
        
        return false;
    }
    
    /**
     * 登録最大件数を超えているかどうかを返す。
     *
     * @return 登録最大件数を超えている場合はtrue、そうでない場合はfalse
     */
    private boolean hasExceededMaxRecords() {
        if (operateDatas.size() > RECORD_MAX_COUNT) {
            sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.Warning"), String.format(LocaleUtils.getString("key.warn.overMaxRecord"), RECORD_MAX_COUNT));
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 優先度を更新する。
     */
    private void updateLabelPriority() {
        displayOrder = 0;
        operateDatas.stream().forEach((entity) -> {
            entity.setLabelPriority(displayOrder);
            displayOrder += 1;
        });
    }

    /**
     * 追加された情報の一覧を取得する。
     *
     * @return 追加情報一覧
     */
    private List<LabelInfoEntity> getAdditionalDatas() {
        ArrayList<LabelInfoEntity> additionalCandidates = new ArrayList(operateDatas);
        ArrayList<LabelInfoEntity> additionals = new ArrayList();
        additionalCandidates.removeAll(cacheDatas);
        
        additionalCandidates.stream().forEach((entity) -> {
            if (Objects.isNull(entity.getLabelId())) {
                logger.debug("追加されたデータ:{}", entity.getLabelName());
                additionals.add(entity);
            }
        });
        return additionals;
    }

    /**
     * 更新された情報の一覧を取得する。
     *
     * @return 更新情報一覧
     */
    private List<LabelInfoEntity> getUpdateDatas() {
        ArrayList<LabelInfoEntity> updateCandidates = new ArrayList(operateDatas);
        ArrayList<LabelInfoEntity> updates = new ArrayList();
        updateLabelPriority();
        updateCandidates.removeAll(cacheDatas);
        
        updateCandidates.stream().forEach((entity) -> {
            if (Objects.nonNull(entity.getLabelId())) {
                logger.debug("編集されたデータ:{}", entity.getLabelName());
                updates.add(entity);
            }
        });
        return updates;
    }

    /**
     * 削除された情報の一覧を取得する。
     *
     * @return 削除情報一覧
     */
    private List<LabelInfoEntity> getRemovedDatas() {
        ArrayList<LabelInfoEntity> removeCandidates = new ArrayList(cacheDatas);
        ArrayList<LabelInfoEntity> removes = new ArrayList();
        removeCandidates.removeAll(operateDatas);        
        boolean delFlag = true;
        
        for (LabelInfoEntity entity : removeCandidates) {
            for (LabelInfoEntity op : operateDatas) {
                if (Objects.equals(entity.getLabelId(), op.getLabelId())) {
                    delFlag = false;
                    break;
                }
            }
            if (!delFlag) {
                delFlag = true;
            } else {
                logger.debug("削除されたデータ:{}", entity.getLabelName());
                removes.add(entity);
            }
        }
        return removes;
    }

    /**
     * 編集用のエンティティリストを作成する。
     *
     * @param entities クローン対象のエンティティリスト
     * @return クローンしたエンティティリスト
     */
    private List<LabelInfoEntity> cloneLabel(List<LabelInfoEntity> entities) {

        List<LabelInfoEntity> cloneEntities = new ArrayList<>();

        entities.stream().map((entity) -> {
            return new LabelInfoEntity(entity);
        }).forEach((cloneEntity) -> {
            cloneEntities.add(cloneEntity);
        });

        return cloneEntities;
    }
}
