package jp.adtekfuji.testldap;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class FXMLController implements Initializable {
    
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField urlField;
    @FXML
    private TextField domainField;
    @FXML
    private TextField userIdField;
    @FXML
    private TextField passwordField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void onLogin(ActionEvent event) {
        this.loginLdap();
    }
    
    @FXML
    private void onClose(ActionEvent event) {
        pane.getScene().getWindow().hide();
    }

    /**
     * LDAP認証を行う。
     */
    private void loginLdap() {
        
        // LDAP接続情報
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, this.urlField.getText());
        props.put(Context.REFERRAL, "ignore");
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.SECURITY_PRINCIPAL, this.userIdField.getText() + "@" + this.domainField.getText()); // ユーザーID＠ドメイン名
        props.put(Context.SECURITY_CREDENTIALS, this.passwordField.getText()); // パスワード
        
        // LDAPS(SSL)の場合
        if (this.urlField.getText().startsWith("ldaps")) {
            props.put("java.naming.ldap.factory.socket", "jp.adtekfuji.testldap.SimpleSSLSocketFactory");
        }

        try {
            // LDAP認証を行う
            DirContext context = new InitialDirContext(props);

            // LDAP認証が成功したので後片付け
            context.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("LDAP認証テスト");
            alert.getDialogPane().setHeaderText("認証に成功しました");
            alert.showAndWait();

        } catch (Exception ex) {
            // その他のエラー
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("LDAP認証テスト");
            alert.getDialogPane().setHeaderText("認証に失敗しました");
            alert.getDialogPane().setContentText(ex.toString());
            alert.showAndWait();
        }
    }
}
