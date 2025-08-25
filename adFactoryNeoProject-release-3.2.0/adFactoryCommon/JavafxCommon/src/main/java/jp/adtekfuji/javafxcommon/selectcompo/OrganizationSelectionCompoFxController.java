/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.selectcompo;

import adtekfuji.cash.CashManager;
import adtekfuji.clientservice.RoleInfoFacade;
import adtekfuji.fxscene.ArgumentDelivery;
import adtekfuji.fxscene.FxComponent;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import adtekfuji.locale.LocaleUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.javafxcommon.treecell.OrganizationTreeCell;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 組織選択ダイアログ
 *
 * @author e-mori
 */
@FxComponent(id = "OrganizationSelectionCompo", fxmlPath = "/fxml/compo/organization_selection_compo.fxml")
public class OrganizationSelectionCompoFxController implements Initializable, ArgumentDelivery {

    private final Logger logger = LogManager.getLogger();
    private final static long ROOT_ID = 0;

    private SelectDialogEntity settingDialogEntity;
    
    /**
     * 承認権限のある組織のみ表示
     */
    private boolean approvalAuthorityOnly = false;

    /**
     * 選択された組織一覧
     */
    private List<OrganizationInfoEntity> entities;

    @FXML
    private TreeView<OrganizationInfoEntity> hierarchyTree;
    @FXML
    private ListView<OrganizationInfoEntity> itemList;
    @FXML
    private Pane progressPane;
    @FXML
    private StackPane stackPane;

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.hierarchyTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.itemList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 選択済リスト
        Callback<ListView<OrganizationInfoEntity>, ListCell<OrganizationInfoEntity>> cellFactory = (ListView<OrganizationInfoEntity> param) -> new ListItemCell();
        this.itemList.setCellFactory(cellFactory);

        blockUI(true);

        Task task = new Task<TreeItem<OrganizationInfoEntity>>() {
            @Override
            protected TreeItem<OrganizationInfoEntity> call() throws Exception {
                CashManager cache = CashManager.getInstance();

                // キャッシュに組織情報を読み込む。(未キャッシュの場合のみ)
                CacheUtils.createCacheOrganization(true);

                // 削除済みのデータを取り除く。
                entities = ((List<OrganizationInfoEntity>) cache.getItemList(OrganizationInfoEntity.class, new ArrayList<>()))
                        .stream().filter(p -> Objects.isNull(p.getRemoveFlag()) || !p.getRemoveFlag())
                        .collect(Collectors.toList());

                if (approvalAuthorityOnly) {
                    // 役割一覧を取得
                    RoleInfoFacade roleFacade = new RoleInfoFacade();
                    List<RoleAuthorityInfoEntity> approvalRoles = roleFacade.findAll();
                    
                    // 承認権限のある組織のみに抽出する。
                    List<OrganizationInfoEntity> approvers = new ArrayList<>();
                    entities.forEach((approver) -> {
                        boolean hasAuthority = false;
                        if (Objects.equals(approver.getAuthorityType(), AuthorityEnum.SYSTEM_ADMIN)) {
                            hasAuthority = true;
                        } else {
                            List<RoleAuthorityInfoEntity> roles = approvalRoles.stream()
                                    .filter(p -> approver.getRoleCollection().contains(p.getRoleId()))
                                    .collect(Collectors.toList());
                            hasAuthority = roles.stream().anyMatch(p -> p.getApprove());
                        }

                        if (hasAuthority) {
                            approvers.addAll(getParentOrganizations(approver.getOrganizationId()));
                            approvers.add(approver);
                        }
                    });
                    
                    entities = approvers.stream().distinct().collect(Collectors.toList());
                }

                return createRoot();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                try {
                    TreeItem<OrganizationInfoEntity> rootItem = this.getValue();

                    hierarchyTree.setRoot(rootItem);
                    hierarchyTree.setCellFactory((TreeView<OrganizationInfoEntity> o) -> new OrganizationTreeCell());

                    rootItem.setExpanded(true);

                } catch (Exception ex) {
                    logger.fatal(ex, ex);
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
    }

    @Override
    public void setArgument(Object argument) {
        if (argument instanceof SelectDialogEntity) {
            this.settingDialogEntity = (SelectDialogEntity) argument;
            this.itemList.getItems().addAll(settingDialogEntity.getOrganizations());
            this.settingDialogEntity.organizationsProperty().bind(itemList.itemsProperty());
            this.approvalAuthorityOnly = settingDialogEntity.isApprovalAuthorityOnly();
        }
    }

    @FXML
    private void OnAdd(ActionEvent event) {
        for (TreeItem<OrganizationInfoEntity> item : hierarchyTree.getSelectionModel().getSelectedItems()) {
            if (Objects.nonNull(item.getParent())
                    && !itemList.getItems().contains(item.getValue())) {
                this.itemList.getItems().add(item.getValue());
            }
        }
    }

    @FXML
    private void OnRemove(ActionEvent event) {
        if (Objects.nonNull(this.itemList.getSelectionModel().getSelectedItem())) {
            itemList.getSelectionModel()
                    .getSelectedIndices()
                    .stream()
                    .sorted(Comparator.reverseOrder())
                    .mapToInt(Integer::intValue)
                    .forEach(index -> this.itemList.getItems().remove(index));
        }
    }

    /**
     * ListView表示用セル
     *
     */
    class ListItemCell extends ListCell<OrganizationInfoEntity> {

        @Override
        protected void updateItem(OrganizationInfoEntity item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                this.setText(item.getOrganizationName());
            } else {
                this.setText("");
            }
        }
    }

    /**
     * UIロック
     *
     * @param flg
     */
    private void blockUI(Boolean flg) {
        this.stackPane.setDisable(flg);
        this.progressPane.setVisible(flg);
    }

    /**
     * ツリーの親階層生成
     *
     */
    private TreeItem<OrganizationInfoEntity> createRoot() {
        TreeItem<OrganizationInfoEntity> rootItem = new TreeItem<>(new OrganizationInfoEntity(ROOT_ID, null, LocaleUtils.getString("key.Organization"), null));
        try {
            logger.info("createRoot start.");

            rootItem.getChildren().clear();

            List<OrganizationInfoEntity> rootList = new ArrayList<>();
            this.entities.stream().filter((entity) -> Objects.nonNull(entity.getParentId()) && entity.getParentId() == ROOT_ID).forEach((entity) -> {
                rootList.add(entity);
            });

            rootList.sort((a, b) -> a.getOrganizationName().compareTo(b.getOrganizationName()));
            rootList.stream().forEach((entity) -> {
                addHierarchies(new TreeItem<>(entity), rootItem);
            });
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("createRoot end.");
        }
        return rootItem;
    }

    /**
     * 階層を追加する。
     *
     * @param childItem 子階層
     * @param parentItem 親階層
     */
    private void addHierarchies(TreeItem<OrganizationInfoEntity> childItem, TreeItem<OrganizationInfoEntity> parentItem) {
        try {
            List<OrganizationInfoEntity> children = new ArrayList<>();

            this.entities.stream().filter((entity) -> (Objects.equals(entity.getParentId(), childItem.getValue().getOrganizationId()))).forEach((entity) -> {
                children.add(entity);
            });

            if (!children.isEmpty()) {
                children.sort((a, b) -> a.getOrganizationName().compareTo(b.getOrganizationName()));
                children.stream().forEach((entity) -> addHierarchies(new TreeItem<>(entity), childItem));
            }

            parentItem.getChildren().add(childItem);
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 指定された組織IDの組織情報の上位の組織階層一覧を取得する。
     * 
     * @param organizationId 組織ID
     * @return 親階層の組織一覧
     */
    private List<OrganizationInfoEntity> getParentOrganizations(Long organizationId) {

        List<OrganizationInfoEntity> parents = new ArrayList<>();
        OrganizationInfoEntity target = getOrganizationById(organizationId);
        if (Objects.nonNull(target)) {
            Long parentOrganizationId = target.getParentId();
            while (parentOrganizationId != ROOT_ID) {
                OrganizationInfoEntity parent = getOrganizationById(parentOrganizationId);
                if (Objects.nonNull(parent)) {
                    parents.add(parent);
                    parentOrganizationId = parent.getParentId();
                }
            }
        }
        
        return parents;
    }

    /**
     * 指定された組織IDの組織情報を取得する。
     * 
     * @param organizationId 組織ID
     * @return 組織情報
     */
    private OrganizationInfoEntity getOrganizationById(Long organizationId) {
        Optional<OrganizationInfoEntity> opt = entities.stream()
                .filter(p -> organizationId.equals(p.getOrganizationId()))
                .findFirst();

        if (opt.isPresent()) {
            return opt.get();
        } else {
            return null;
        }
    }
}
