package adtekfuji.admanagerapp.chartplugin.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 作業分析メニュー画面のコントローラー
 *
 * @author s-heya
 */
@FxComponent(id = "ChartNaviCompo", fxmlPath = "/fxml/chartplugin/NaviCompo.fxml")
public class NaviCompoController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();

    SceneContiner sc = SceneContiner.getInstance();

    MainSceneController mainSceneController;

    @FXML
    private Button timeLineButton;
    @FXML
    private Button kanbanSummaryButton;
    @FXML
    private Button workSummaryButton;
    @FXML
    private Button organizationSummaryButton;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * 作業分析
     *
     * @param event
     */
    @FXML
    private void onTimeLine(ActionEvent event) {
        logger.info("onTimeLine");
        sc.setComponent("ContentNaviPane", "ChartTimeLineCompo", this.mainSceneController);

        this.restoreStyle();
        this.timeLineButton.getStyleClass().remove("SideNaviButton");
        this.timeLineButton.getStyleClass().add("SelectedButton");
    }

    /**
     * カンバンの総作業時間
     *
     * @param event
     */
    @FXML
    private void onKanbanSummary(ActionEvent event) {
        logger.info("onKanbanSummary");
        sc.setComponent("ContentNaviPane", "ChartKanbanSummaryCompo");

        this.restoreStyle();
        this.kanbanSummaryButton.getStyleClass().remove("SideNaviButton");
        this.kanbanSummaryButton.getStyleClass().add("SelectedButton");
    }

    /**
     * 工程の平均作業時間
     *
     * @param event
     */
    @FXML
    private void onWorkSummary(ActionEvent event) {
        logger.info("onWorkSummary");
        sc.setComponent("ContentNaviPane", "ChartWorkSummaryCompo");

        this.restoreStyle();
        this.workSummaryButton.getStyleClass().remove("SideNaviButton");
        this.workSummaryButton.getStyleClass().add("SelectedButton");
    }

    /**
     * 作業者の平均作業時間
     *
     * @param event
     */
    @FXML
    private void onOrganizationSummary(ActionEvent event) {
        logger.info("onOrganizationSummary");
        sc.setComponent("ContentNaviPane", "ChartOrganizationSummaryCompo");

        this.restoreStyle();
        this.organizationSummaryButton.getStyleClass().remove("SideNaviButton");
        this.organizationSummaryButton.getStyleClass().add("SelectedButton");
    }

    /**
     * スタイルを元に戻す。
     */
    private void restoreStyle() {
        if (this.timeLineButton.getStyleClass().contains("SelectedButton")) {
            this.timeLineButton.getStyleClass().remove("SelectedButton");
            this.timeLineButton.getStyleClass().add("SideNaviButton");
        }

        if (this.kanbanSummaryButton.getStyleClass().contains("SelectedButton")) {
            this.kanbanSummaryButton.getStyleClass().remove("SelectedButton");
            this.kanbanSummaryButton.getStyleClass().add("SideNaviButton");
        }

        if (this.workSummaryButton.getStyleClass().contains("SelectedButton")) {
            this.workSummaryButton.getStyleClass().remove("SelectedButton");
            this.workSummaryButton.getStyleClass().add("SideNaviButton");
        }

        if (this.organizationSummaryButton.getStyleClass().contains("SelectedButton")) {
            this.organizationSummaryButton.getStyleClass().remove("SelectedButton");
            this.organizationSummaryButton.getStyleClass().add("SideNaviButton");
        }
    }

    /**
     * 与えられた引数を記録
     *
     * @param argument
     */
    @Override
    public void setArgument(Object argument) {
        this.mainSceneController = (MainSceneController) argument;
    }
}
