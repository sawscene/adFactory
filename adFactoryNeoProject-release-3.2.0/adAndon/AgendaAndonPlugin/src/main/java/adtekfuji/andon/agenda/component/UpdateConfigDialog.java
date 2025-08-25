/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.component;

import adtekfuji.admanagerapp.andonsetting.component.AgendaMonitorSettingController;
import adtekfuji.admanagerapp.andonsetting.component.AndonSettingController;
import adtekfuji.admanagerapp.andonsetting.component.lite.LiteMonitorSettingController;
import adtekfuji.andon.agenda.common.AgendaSettings;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.andon.enumerate.AndonMonitorTypeEnum;
import jp.adtekfuji.andon.property.AndonMonitorLineProductSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author phamvanthanh
 */
@FxComponent(id = "UpdateConfigDialog", fxmlPath = "/fxml/dialog/agenda_config_dialog.fxml")
public class UpdateConfigDialog implements Initializable {

    private final Logger logger = LogManager.getLogger();

    @FXML
    private ScrollPane scrollPane;

    private AndonMonitorLineProductSetting setting;
    private AndonSettingController settingController;
    private HeaderCompoController headerController;
    private final boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());

    private Stage parent;

    public void setParent(Stage parent) {
        this.parent = parent;
    }

    /**
     * 進捗モニタ設定を与える<br>
     * この値をアジェンダモニター設定に表示し編集する。
     *
     * @param setting
     */
    public void setAndonSetting(AndonMonitorLineProductSetting setting) {
        this.setting = setting;
    }

    public void setHeaderController(HeaderCompoController controller) {
        this.headerController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    readThread();
                    return null;
                }
            };
            new Thread(task).start();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    private void readThread() {
        Platform.runLater(() -> {
            // アジェンダモニター設定を表示
            AndonMonitorTypeEnum monitorType = setting.getMonitorType();
            switch(monitorType) {
                case AGENDA:
                    settingController = new AgendaMonitorSettingController(null, AgendaSettings.getMonitorId(), this.setting.getAgendaMonitorSetting().clone(), parent, false);
                    scrollPane.setContent(settingController);
                    break;
                case LITE_MONITOR:
                    settingController = new LiteMonitorSettingController(null, AgendaSettings.getMonitorId(), this.setting.getAgendaMonitorSetting().clone(), parent);
                    scrollPane.setContent(settingController);
                    break;
            }
        });
    }

    /**
     * 設定ファイルの保存する。
     */
    @FXML
    private void onButtonApply() {
        logger.info("onButtonApply start.");

        try {
            if (!this.settingController.isValidItems()) {
                return;
            }

            if (Objects.isNull(this.settingController)) {
                return;
            }

            AgendaSettings.save(this.settingController.getInputResult());

            if (0 == AgendaSettings.getMonitorId()) {
                this.headerController.updateDisplay();
            }

            Stage stage = (Stage) this.scrollPane.getScene().getWindow();
            stage.close();

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * Handle event click on [Cancel] button
     */
    @FXML
    private void onButtonCancel() {
        Stage stage = (Stage) scrollPane.getScene().getWindow();
        stage.close();
    }
}
