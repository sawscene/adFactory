package adtekfuji.admanagerapp.component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import adtekfuji.admanagerapp.common.Constants;
import adtekfuji.clientservice.OrganizationInfoFacade;
import adtekfuji.clientservice.RoleInfoFacade;
import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import adtekfuji.utility.PropertyUtils;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.login.OrganizationLoginResult;
import jp.adtekfuji.adFactory.entity.master.RoleAuthorityInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LocaleTypeEnum;
import jp.adtekfuji.adFactory.enumerate.LoginAuthTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author ke.yokoi
 */
@FxComponent(id = "LoginCompo", fxmlPath = "/fxml/compo/login_compo.fxml")
public class LoginCompoFxController implements Initializable {

    private final Properties props = AdProperty.getProperties();
    private final Logger logger = LogManager.getLogger();
    private final SceneContiner sc = SceneContiner.getInstance();
    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
    private static final String CHARSET = "UTF-8";

    private static final String KEY_REMEMBER_USER_ID = "rememberUserId";
    private static final String KEY_USER_ID = "user_id";

    private static final String DEF_REMEMBER_USER_ID = "false";
    private static final String DEF_USER_ID = "";

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField txtUserid;
    @FXML
    private TextField txtPassword;
    @FXML
    private Button loginButton;
    @FXML
    private Pane progressPane;
    @FXML
    private CheckBox rememberUserIdCheckBox;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ログインIDを復元
        rememberUserIdCheckBox.setSelected(Boolean.parseBoolean(props.getProperty(KEY_REMEMBER_USER_ID, DEF_REMEMBER_USER_ID)));
        if (rememberUserIdCheckBox.isSelected()) {
            txtUserid.setText(props.getProperty(KEY_USER_ID, DEF_USER_ID));
            Platform.runLater(() -> txtPassword.requestFocus());
        }

        progressPane.setVisible(false);
        txtUserid.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                login();
            }
        });
        txtPassword.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                login();
            }
        });
        loginButton.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                login();
            }
        });

        this.rootPane.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                txtUserid.requestFocus();
            }
        });
    }

    /**
     * onLoginAction
     *
     * @param event
     */
    @FXML
    private void onLoginAction(ActionEvent event) {
        login();
    }

    private void blockUI(Boolean flg) {
        progressPane.setVisible(flg);
        sc.blockUI("loginPane", flg);
    }

    private void login() {
        blockUI(true);
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                loginThread();
                return null;
            }
        };
        new Thread(task).start();
    }

    private void loginThread() {
        OrganizationInfoFacade organizationInfoFacade = new OrganizationInfoFacade();
        StringBuilder retMessage = new StringBuilder();
        OrganizationLoginResult loginResult;

        LoginAuthTypeEnum type = LoginAuthTypeEnum.getEnum(props.getProperty(Constants.LOGIN_AUTH_TYPE, Constants.LOGIN_AUTH_TYPE_DEFAULT));
        switch (type) {
            case LDAP:
                loginResult = organizationInfoFacade.loginLdap(txtUserid.getText(), txtPassword.getText(), retMessage);
                break;
            case adFactory:
            default:
                loginResult = organizationInfoFacade.login(txtUserid.getText(), txtPassword.getText(), retMessage);
                break;
        }

        if (Objects.isNull(loginResult) || !loginResult.getIsSuccess()) {
            Platform.runLater(() -> {
                blockUI(false);
                sc.showAlert(Alert.AlertType.WARNING, LocaleUtils.getString("key.LoginErr"), LocaleUtils.getString(retMessage.toString()));
            });
            return;
        }

        // ログインIDの記憶
        props.setProperty(KEY_REMEMBER_USER_ID, String.valueOf(rememberUserIdCheckBox.isSelected()));
        if (rememberUserIdCheckBox.isSelected()) {
            props.setProperty(KEY_USER_ID, txtUserid.getText());
        } else {
            props.remove(KEY_USER_ID);
        }

        LoginUserInfoEntity loginUserInfoEntity = LoginUserInfoEntity.getInstance();
        OrganizationInfoEntity entity;
        try {
            entity = organizationInfoFacade.findName(URLEncoder.encode(txtUserid.getText(), CHARSET));
            loginUserInfoEntity.setId(entity.getOrganizationId());
            loginUserInfoEntity.setName(entity.getOrganizationName());
            loginUserInfoEntity.setLoginTime(new Date());
            loginUserInfoEntity.setAuthorityType(entity.getAuthorityType());
            loginUserInfoEntity.setLoginId(txtUserid.getText());
        } catch (UnsupportedEncodingException ex) {
            logger.fatal(ex, ex);
            return;
        }
        //役割権限の展開.
        List<String> roleAuthes = new ArrayList<>();
        RoleInfoFacade roleInfoFacade = new RoleInfoFacade();
        List<RoleAuthorityInfoEntity> roles = new ArrayList<>();
        for (Long id : entity.getRoleCollection()) {
            roles.addAll(Arrays.asList(roleInfoFacade.find(id)));
        }

        for (RoleAuthorityInfoEntity role : roles) {

            // admin権限の場合は無条件ですべての権限を取得する
            if (LoginUserInfoEntity.ADMIN_LOGIN_ID.equals(loginUserInfoEntity.getLoginId())) {

                roleAuthes.add(RoleAuthorityTypeEnum.REFERENCE_RESOOURCE.name());
                roleAuthes.add(RoleAuthorityTypeEnum.EDITED_RESOOURCE.name());
                roleAuthes.add(RoleAuthorityTypeEnum.REFERENCE_KANBAN.name());
                roleAuthes.add(RoleAuthorityTypeEnum.MAKED_KANBAN.name());
                roleAuthes.add(RoleAuthorityTypeEnum.REFERENCE_WORKFLOW.name());
                roleAuthes.add(RoleAuthorityTypeEnum.EDITED_WORKFLOW.name());
                roleAuthes.add(RoleAuthorityTypeEnum.OUTPUT_ACTUAL.name());
                roleAuthes.add(RoleAuthorityTypeEnum.MANAGED_LINE.name());
                roleAuthes.add(RoleAuthorityTypeEnum.DELETE_ACTUAL.name());
                roleAuthes.add(RoleAuthorityTypeEnum.RIGHT_ACCESS.name());
                roleAuthes.add(RoleAuthorityTypeEnum.APPROVAL_KANBAN.name());

            } else {

                if (role.getResourceReference()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.REFERENCE_RESOOURCE.name());
                }

                if (role.getResourceEdit()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.EDITED_RESOOURCE.name());
                }

                if (role.getKanbanReference()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.REFERENCE_KANBAN.name());
                }

                if (role.getKanbanCreate()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.MAKED_KANBAN.name());
                }

                if (role.getWorkflowReference()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.REFERENCE_WORKFLOW.name());
                }

                if (role.getWorkflowEdit()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.EDITED_WORKFLOW.name());
                }

                if (role.getActualOutput()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.OUTPUT_ACTUAL.name());
                }

                if (role.getLineManage()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.MANAGED_LINE.name());
                }

                if (role.getActualDel()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.DELETE_ACTUAL.name());
                }

                if (role.getAccessEdit()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.RIGHT_ACCESS.name());
                }

                if (role.getApprove()) {
                    roleAuthes.add(RoleAuthorityTypeEnum.APPROVAL_KANBAN.name());
                }
            }

        }
        loginUserInfoEntity.setRoleAuthCollection(roleAuthes);
   
        LocaleUtils.clearLocaleFile("locale");

        // 組織に設定された言語ファイルを取得
        List<LocaleFileInfoEntity> localeList = loginResult.getOrganizationInfo().getLocaleFileInfoCollection();
        // 組織に言語ファイルが設定されていたら個人用言語ファイルを作成する
        if (Objects.nonNull(localeList)) {
            LocaleFileInfoEntity adMangerLocale = null;
            LocaleFileInfoEntity custumLocale = null;
            for (LocaleFileInfoEntity locale : localeList) {
                if (Objects.equals(LocaleTypeEnum.ADMANAGER, locale.getLocaleType())) {
                    adMangerLocale = locale;
                } else if (Objects.equals(LocaleTypeEnum.CUSTUM, locale.getLocaleType())) {
                    custumLocale = locale;
                }
            }

            if (Objects.nonNull(adMangerLocale) || Objects.nonNull(custumLocale)) {

                Map<String, String> rbMap = Collections.list(rb.getKeys())
                        .stream()
                        .collect(Collectors.toMap(Function.identity(), rb::getString, (a, b) -> b, TreeMap::new));

                if (Objects.nonNull(adMangerLocale)) {
                    String localeString = adMangerLocale.resource().getResourceString();
                    try (BufferedReader reader = new BufferedReader(new StringReader(localeString))) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            String[] split = line.split("=");
                            if (split.length == 2) {
                                rbMap.put(split[0], split[1]);
                            }
                        }
                    } catch (IOException ex) {
                        logger.fatal(ex, ex);
                    }
                }

                // カスタム用言語ファイルを統合
                if (Objects.nonNull(custumLocale)) {
                    String localeString = custumLocale.resource().getResourceString();
                    try (BufferedReader reader = new BufferedReader(new StringReader(localeString))) {
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            String[] split = line.split("=");
                            if (split.length == 2) {
                                rbMap.put(split[0], split[1]);
                            }
                        }
                    } catch (IOException ex) {
                        logger.fatal(ex, ex);
                    }
                }

                // 個人用言語ファイルを作成
                File file = LocaleUtils.getLocaleFile("locale");
                if (Objects.nonNull(file)) {
                    try (BufferedOutputStream outBuffer = new BufferedOutputStream(new FileOutputStream(file));
                         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outBuffer, "8859_1"))) {
                        for (Map.Entry<String, String> entry : rbMap.entrySet()) {
                            String val = PropertyUtils.convertToUnicodeEscape(entry.getValue());
                            bw.write(entry.getKey() + "=" + val);
                            bw.newLine();
                        }
                        bw.flush();

                        // 言語リソースの再読み込み
                        LocaleUtils.load("locale");
                    } catch (IOException ex) {
                        logger.fatal(ex, ex);
                    }
                }
            }
        }

        Platform.runLater(() -> {
            blockUI(false);
            sc.trans("MainScene");
        });
    }


}
