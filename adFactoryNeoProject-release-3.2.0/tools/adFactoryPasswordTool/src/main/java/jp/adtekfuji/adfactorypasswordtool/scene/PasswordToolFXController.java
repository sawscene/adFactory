package jp.adtekfuji.adfactorypasswordtool.scene;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import jp.adtekfuji.adfactorypasswordtool.utility.CipherHelper;

public class PasswordToolFXController implements Initializable {

    private static final String CIPHER_KEY = "BpzEsGPXnkW2SdUW";  //キーは16文字で
    private static final String CIPHER_ALGORITHM = "AES";

    @FXML
    private TextField passwordText;
    @FXML
    private TextField passwordChkText;
    @FXML
    private TextField encText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void encButtonAction(ActionEvent event) {
        try {
            //パスワードと確認用パスワードの入力値を比較する。
            String pass = this.passwordText.getText();
            String chkpass = this.passwordChkText.getText();
            if (pass == null || !pass.equals(chkpass)) {
                // メッセージを表示して処理を終了する。
                Alert dialog = new Alert(Alert.AlertType.ERROR);
		dialog.setHeaderText(null);
		dialog.setContentText("パスワードと確認用パスワードが異なります。\n入力し直してください。");
		dialog.showAndWait();
                
                return;
            }
            // パスワードを暗号化する。
            String enc = CipherHelper.encrypt(this.passwordText.getText(), CIPHER_KEY, CIPHER_ALGORITHM);

            this.encText.setText(enc);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
