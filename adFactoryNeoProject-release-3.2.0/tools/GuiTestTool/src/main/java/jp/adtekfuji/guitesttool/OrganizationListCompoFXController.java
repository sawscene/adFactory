package jp.adtekfuji.guitesttool;

import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FxComponent(id = "OrganizationListCompo", fxmlPath = "/fxml/organization_list_compo.fxml")
public class OrganizationListCompoFXController implements Initializable {

    private final long MAX_LOAD_SIZE = 10;

    @FXML
    private Pane progressPane;
    @FXML
    private TableView<OrganizationInfoEntity> organizationList;
    @FXML
    private TableColumn<OrganizationInfoEntity, Long> organizationIdColumn;
    @FXML
    private TableColumn<OrganizationInfoEntity, Long> organizationParentColumn;
    @FXML
    private TableColumn<OrganizationInfoEntity, String> organizationNameColumn;
    @FXML
    private TableColumn<OrganizationInfoEntity, String> organizationIdentColumn;

    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final OrganizationInfoFacade organizationFacade = new OrganizationInfoFacade();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        blockUI(true);

        organizationIdColumn.setCellValueFactory(new PropertyValueFactory<>("organizationId"));
        organizationParentColumn.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        organizationNameColumn.setCellValueFactory(new PropertyValueFactory<>("organizationName"));
        organizationIdentColumn.setCellValueFactory(new PropertyValueFactory<>("organizationIdentify"));

        organizationList.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                //工程カンバンリスト表示へ.
                OrganizationInfoEntity organization = organizationList.getSelectionModel().getSelectedItems().get(0);
                createOrganizationWorkDialog(organization);
            }
        });

        //組織一覧表示.
        updateOrganization();
    }

    private void blockUI(Boolean flg) {
        Platform.runLater(() -> {
            sc.blockUI("OrganizationListCompo", flg);
            progressPane.setVisible(flg);
        });
    }

    @FXML
    private void onKeyPressed(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.F5)) {
            updateOrganization();
        }
    }

    private void updateOrganization() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    readOrganization();
                } finally {
                    blockUI(false);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void readOrganization() {
        ObservableList<OrganizationInfoEntity> table = FXCollections.observableArrayList();
        long max = organizationFacade.count();
        for (long count = 0; count <= max; count += MAX_LOAD_SIZE) {
            List<OrganizationInfoEntity> organizatons = organizationFacade.findRange(count, count + MAX_LOAD_SIZE - 1);
            table.addAll(organizatons);
            Platform.runLater(() -> {
                organizationList.setItems(table);
                organizationList.getSortOrder().add(organizationIdColumn);
            });
        }
    }

    private void createOrganizationWorkDialog(OrganizationInfoEntity organization) {
        sc.showModelessDialog("「" + organization.getOrganizationName() + "」の工程カンバン一覧", "OrganizationWorkListCompo", organization.getOrganizationId());
    }

}
