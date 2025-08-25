/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.component;

import adtekfuji.admanagerapp.systemsettingplugin.common.StatusRecordFactory;
import adtekfuji.clientservice.DisplayedStatusInfoFacade;
import adtekfuji.fxscene.ComponentHandler;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
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
import jp.adtekfuji.adFactory.entity.ResponseAnalyzer;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.master.DisplayedStatusInfoEntity;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.javafxcommon.property.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ステータス設定画面
 *
 * @author e-mori
 */
@FxComponent(id = "StatusEditCompo", fxmlPath = "/fxml/compo/status_edit_compo.fxml")
public class StatusEditCompoFxController implements Initializable, ComponentHandler {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private final SceneContiner sc = SceneContiner.getInstance();
    private final static DisplayedStatusInfoFacade displayedStatusInfoFacade = new DisplayedStatusInfoFacade();

    private LinkedList<DisplayedStatusInfoEntity> cashDatas = new LinkedList<>();
    private LinkedList<DisplayedStatusInfoEntity> operatDatas = new LinkedList<>();

    private static Thread registThread = null;

    @FXML
    private Button registButton;
    @FXML
    private VBox propertyPane;
    @FXML
    private Pane Progress;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //役割の権限によるボタン無効化.
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            registButton.setDisable(true);
        }
        runUpdateViewThread();
    }

    //TODO:メソッド1つでデータを生成しているため後で処理分割を行いダイアログの応対等の検討を行う
    @FXML
    private void OnRegist(ActionEvent event) {
        logger.info("regist start");
        runRegistThread();
    }

    /**
     * 登録用通信スレッド
     *
     * @return 保存を行わなかったときfalse　現状保存に失敗してもtrueを返すようになっている
     */
    private boolean runRegistThread() {
        Platform.runLater(() -> {
            blockUI(true);
        });
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    logger.info("Update");
                    for (DisplayedStatusInfoEntity entity : getUpdateDatas()) {
                        entity.updateData();
                        if (!ResponseAnalyzer.getAnalyzeResult(displayedStatusInfoFacade.update(entity))) {
                            return null;
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex, ex);
                } finally {
                    Platform.runLater(() -> {
                        blockUI(false);
                        runUpdateViewThread();
                    });
                }
                return null;
            }
        };
        registThread = new Thread(task);
        registThread.start();

        return true;
    }

    /**
     * テーブル画面更新用スレッド
     *
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
                @Override
                protected Void call() throws Exception {
                    try {
                        cashDatas.clear();
                        operatDatas.clear();

                        //中断理由全権取得
                        cashDatas = new LinkedList<>(displayedStatusInfoFacade.findAll());
                        operatDatas = new LinkedList<>(cloneInterrupt(cashDatas));
                        cashDatas.sort(Comparator.comparing(status -> status.getStatusName()));
                        operatDatas.sort(Comparator.comparing(status -> status.getStatusName()));
                        Platform.runLater(() -> {
                            propertyPane.getChildren().clear();
                            Table table = new Table(propertyPane.getChildren())
                                    .title(LocaleUtils.getString("key.EditStatus")).isColumnTitleRecord(Boolean.TRUE).styleClass("ContentTitleLabel");
                            table.setAbstractRecordFactory(new StatusRecordFactory(table, operatDatas));
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
     * 更新された情報の一覧取得
     *
     * @return 更新一覧
     */
    private List<DisplayedStatusInfoEntity> getUpdateDatas() {
        ArrayList<DisplayedStatusInfoEntity> subArr = new ArrayList();
        int index = 0;
        for (DisplayedStatusInfoEntity s : operatDatas) {
            if (!(s.getFontColor().equals(cashDatas.get(index).getFontColor())
                    && s.getBackColor().equals(cashDatas.get(index).getBackColor())
                    && s.getLightPattern().equals(cashDatas.get(index).getLightPattern())
                    && Objects.nonNull(s.getNotationName()) && s.getNotationName().equals(cashDatas.get(index).getNotationName())
                    && Objects.nonNull(s.getMelodyPath()) && s.getMelodyPath().equals(cashDatas.get(index).getMelodyPath())
                    && Objects.nonNull(s.getMelodyRepeat()) && s.getMelodyRepeat().equals(cashDatas.get(index).getMelodyRepeat()))) {
                logger.debug("編集されたデータ:{}", s.getStatusName());
                subArr.add(s);
            }
            index++;
        }

        return subArr;
    }

    /**
     * 編集用エンティティリストの作成
     *
     * @param entitys
     * @return
     */
    private List<DisplayedStatusInfoEntity> cloneInterrupt(List<DisplayedStatusInfoEntity> entitys) {

        List<DisplayedStatusInfoEntity> cloneEntitys = new ArrayList<>();

        entitys.stream().map((entity) -> {
            return new DisplayedStatusInfoEntity(entity);
        }).forEach((cloneEntity) -> {
            cloneEntitys.add(cloneEntity);
        });

        return cloneEntitys;
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        sc.blockUI("ContentNaviPane", flg);
        Progress.setVisible(flg);
    }
    
    /**
     * 変更がかけられたかチェック
     * 
     * @return 
     */
    private boolean isChanged() {
        // 編集権限なしは常に無変更
        if (!LoginUserInfoEntity.getInstance().checkRoleAuthority(RoleAuthorityTypeEnum.EDITED_RESOOURCE)) {
            return false;
        }

        if (!getUpdateDatas().isEmpty()) {
            return true;
        }
        return false;
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
                return runRegistThread();
            } else if (ButtonType.CANCEL == buttonType) {
                return false;
            }
        }
        return true;
    }
}
