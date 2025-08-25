package jp.adtekfuji.javafxcommon.dialog;

import adtekfuji.utility.StringUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jp.adtekfuji.javafxcommon.dialog.DialogBox.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ダイアログボックスのコントローラー
 *
 * @author s-heya
 */
public class DialogBoxController {

    private final static Logger logger = LogManager.getLogger();

    private Status response = Status.CANCEL;

    @FXML
    private ImageView imageView;
    @FXML
    private Label messageLabel;
    @FXML
    private Label detailsLabel;
    @FXML
    private HBox actionParent;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnOk;

    /**
     * ダイアログタイプを設定する。
     *
     * @param dialogType
     */
    public void setDialogType(DialogBox.DialogType dialogType) {
        switch (dialogType) {
        case INFOMATION:
            this.imageView.setImage(new Image(this.getClass().getResourceAsStream("/image/information.png")));
            this.btnCancel.setVisible(false);
            Platform.runLater(() -> {
                btnOk.requestFocus();
            });
            break;
        case QUESTION:
            this.imageView.setImage(new Image(this.getClass().getResourceAsStream("/image/question.png")));
            Platform.runLater(() -> {
                btnCancel.requestFocus();
            });
            break;
        case WARNING:
            this.imageView.setImage(new Image(this.getClass().getResourceAsStream("/image/alert.png")));
            Platform.runLater(() -> {
                this.btnOk.requestFocus();
            });
            break;
        case ERROR:
            this.imageView.setImage(new Image(this.getClass().getResourceAsStream("/image/error.png")));
            this.btnCancel.setVisible(false);
            Platform.runLater(() -> {
                this.btnOk.requestFocus();
            });
            break;
        default:
        }
    }

    /**
     * メッセージを設定する。
     *
     * @param s
     */
    public void setMessage(String s) {
        this.messageLabel.setText(s);
    }

    /**
     * 詳細内容を設定する。
     *
     * @param s
     */
    public void setDetail(String s) {
        this.detailsLabel.setText(s);
    }

    /**
     * OKボタンのアクション
     *
     * @param e
     */
    @FXML
    protected void btnOkOnAction(ActionEvent e) {
        logger.info("OK is clicked.");

        this.setResponse(Status.OK);

        Node source = (Node) e.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * キャンセルボタンのアクション
     *
     * @param e
     */
    @FXML
    protected void btnCancelOnAction(ActionEvent e) {
        logger.info("Cancel is clicked.");

        this.setResponse(Status.CANCEL);

        Node source = (Node) e.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * 入力結果を取得する。
     *
     * @return
     */
    public Status getResponse() {
        return this.response;
    }

    /**
     * 入力結果を設定する。
     *
     * @param ans
     */
    private void setResponse(Status ans) {
        this.response = ans;
    }

    /**
     * OKボタンのテキストを設定する。
     *
     * @param string
     */
    public void setOkButtonText(String string) {
        this.btnOk.setText(string);
    }

    /**
     * キャンセルボタンのテキストを設定する。
     *
     * @param string
     */
    public void setCancelButtonText(String string) {
        assert !StringUtils.isEmpty(string);
        this.btnCancel.setVisible(true);
        this.btnCancel.setText(string);
    }

    /**
     * キャンセルボタンを非表示にする。
     */
    public void hideCancelButton() {
        this.actionParent.getChildren().remove(this.btnCancel);
    }
}