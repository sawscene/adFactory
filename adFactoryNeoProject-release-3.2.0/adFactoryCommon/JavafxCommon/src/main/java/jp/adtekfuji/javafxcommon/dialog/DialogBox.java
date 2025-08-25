/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.dialog;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.fxscene.SceneProperties;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import jakarta.ws.rs.WebApplicationException;
import jakarta.xml.bind.MarshalException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import javax.net.ssl.SSLException;
import jp.adtekfuji.adFactory.enumerate.ServerErrorTypeEnum;
import jp.adtekfuji.javafxcommon.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ダイアログボックス
 *
 * @author s-heya
 */
public class DialogBox {

    private final static Logger logger = LogManager.getLogger();
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale");

    /**
     * ダイアログボックスの種類
     */
    public enum DialogType {

        INFOMATION,
        QUESTION,
        WARNING,
        ERROR;
    }

    /**
     * 入力結果
     */
    public enum Status {

        CANCEL,
        OK;
    }

    /**
     * ダイアログボックスを表示する。
     *
     * @param owner
     * @param title
     * @param message
     * @param type
     * @return
     */
    public static Status Show(Window owner, String title, String message, DialogType type) {
        return Show(owner, title, message, null, null, null, type);
    }

    /**
     * ダイアログボックスを表示する。
     *
     * @param owner
     * @param title
     * @param message
     * @param details
     * @param okButtonText
     * @param cancelButtonText
     * @param type
     * @return
     */
    public synchronized static Status Show(Window owner, String title, String message, String details, String okButtonText, String cancelButtonText, DialogType type) {
        Status status = null;
        SceneContiner sc = SceneContiner.getInstance();

        try {
            logger.info(DialogBox.class.getSimpleName() + "::Show start.");
            logger.info("Show the dialogBox: {0}, {1}", message, details);

            assert owner != null;

            DialogBoxController controller = new DialogBoxController();

            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("DialogBox.fxml"), rb);
            loader.setController(controller);
            loader.load();

            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            SceneProperties sp = SceneContiner.getInstance().getSceneProperties();
            scene.getStylesheets().addAll(sp.getCsspathes());

            // Seane Builderでテンプレートからダイアログボックスを作るとラベルは1文字表示ができない。
            // 必ず幅を設定すること！！
            controller.setMessage(message);
            controller.setDialogType(type);

            if (!StringUtils.isEmpty(details)) {
                controller.setDetail(details);
            }

            if (!StringUtils.isEmpty(okButtonText)) {
                controller.setOkButtonText(okButtonText);
            }

            if (!StringUtils.isEmpty(cancelButtonText)) {
                controller.setCancelButtonText(cancelButtonText);
            } else {
                controller.hideCancelButton();
            }

            sc.blockUI(true);

            if (Platform.isFxApplicationThread()) {

                Stage stage = new Stage(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setTitle(title);
                stage.setResizable(false);
                stage.showAndWait();

                status = controller.getResponse();
                logger.info("Operator response: {0}", status);
            } else {
                Platform.runLater(() -> {
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.initOwner(owner);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setTitle(title);
                    stage.setResizable(false);
                    stage.showAndWait();
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            if (Objects.nonNull(sc)) {
                sc.blockUI(false);
            }
            logger.info(DialogBox.class.getSimpleName() + "::Show end.");
        }

        return status;
    }

    public synchronized static Status Show(Window owner, String title, String message, String details, String okButtonText, String cancelButtonText, DialogType type, long millisecond) {
        Status status = null;
        SceneContiner sc = SceneContiner.getInstance();

        try {
            logger.info(DialogBox.class.getSimpleName() + "::Show start.");
            logger.info("Show the dialogBox: {0}, {1}", message, details);

            assert owner != null;

            DialogBoxController controller = new DialogBoxController();

            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("DialogBox.fxml"), rb);
            loader.setController(controller);
            loader.load();

            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            SceneProperties sp = SceneContiner.getInstance().getSceneProperties();
            scene.getStylesheets().addAll(sp.getCsspathes());

            // Seane Builderでテンプレートからダイアログボックスを作るとラベルは1文字表示ができない。
            // 必ず幅を設定すること！！
            controller.setMessage(message);
            controller.setDialogType(type);

            if (!StringUtils.isEmpty(details)) {
                controller.setDetail(details);
            }

            if (!StringUtils.isEmpty(okButtonText)) {
                controller.setOkButtonText(okButtonText);
            }

            if (!StringUtils.isEmpty(cancelButtonText)) {
                controller.setCancelButtonText(cancelButtonText);
            } else {
                controller.hideCancelButton();
            }

            sc.blockUI(true);

            if (Platform.isFxApplicationThread()) {

                Stage stage = new Stage(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setTitle(title);
                stage.setResizable(false);

                Timeline timer = new Timeline(new KeyFrame(Duration.millis(millisecond), (event) -> stage.close()));
                timer.setCycleCount(1);
                timer.play();

                stage.showAndWait();

                status = controller.getResponse();
                logger.info("Operator response: {0}", status);
            } else {
                Platform.runLater(() -> {
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.initOwner(owner);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setTitle(title);
                    stage.setResizable(false);

                    Timeline timer = new Timeline(new KeyFrame(Duration.millis(millisecond), (event) -> stage.close()));
                    timer.setCycleCount(1);
                    timer.play();

                    stage.showAndWait();
                });
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        } finally {
            if (Objects.nonNull(sc)) {
                sc.blockUI(false);
            }
            logger.info(DialogBox.class.getSimpleName() + "::Show end.");
        }

        return status;
    }

    /**
     * アラートを表示する。
     *
     * @param messageKey
     * @param detailsKey
     * @param args
     */
    public static void alert(String messageKey, String detailsKey, Object... args) {
        SceneContiner sc = SceneContiner.getInstance();

        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(messageKey);

            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(detailsKey)) {
                sb.append(LocaleUtils.getString(detailsKey));
            }

            if (args.length > 0) {
                String errorInfo = "\n" + LocaleUtils.getString(Locale.ALERT_ERRORINFO);
                sb.append(errorInfo);

                for (Object arg : args) {
                    sb.append("\n");
                    sb.append(arg);
                }
            }

            String okButtonText = LocaleUtils.getString(Locale.OK);
            // String cancelButtonText = LocaleUtils.getString(Locale.CANCEL);
            DialogBox.Status status = DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, sb.toString(), okButtonText, "", DialogBox.DialogType.ERROR);
        } catch (Exception ex) {
            logger.fatal(ex);

            if (Objects.nonNull(rb)) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(LocaleUtils.getString(Locale.APPLICATION_TITLE));
                alert.getDialogPane().setHeaderText(LocaleUtils.getString(messageKey));
                alert.getDialogPane().setContentText(LocaleUtils.getString(detailsKey));
                alert.showAndWait();
            }
        }
    }

    /**
     * アラートを表示する。javafx.scene.control.Alertを使用。
     *
     * @param messageKey
     * @param detailsKey
     */
    public static void simpleAlert(String messageKey, String detailsKey) {
        try {
            if (Objects.nonNull(rb)) {
                if (Platform.isFxApplicationThread()) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle(LocaleUtils.getString(Locale.APPLICATION_TITLE));
                    alert.getDialogPane().setHeaderText(LocaleUtils.getString(messageKey));
                    alert.getDialogPane().setContentText(LocaleUtils.getString(detailsKey));
                    alert.showAndWait();
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle(LocaleUtils.getString(Locale.APPLICATION_TITLE));
                        alert.getDialogPane().setHeaderText(LocaleUtils.getString(messageKey));
                        alert.getDialogPane().setContentText(LocaleUtils.getString(detailsKey));
                        alert.showAndWait();
                    });
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 再試行用のアラートを表示する。
     *
     * @param messageKey
     * @param detailsKey
     * @param args
     * @return
     */
    public static Boolean retry(String messageKey, String detailsKey, Object... args) {
        SceneContiner sc = SceneContiner.getInstance();
        Boolean isRetry = false;

        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(messageKey);

            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(detailsKey)) {
                sb.append(LocaleUtils.getString(detailsKey));
            }

            if (args.length > 0) {
                String errorInfo = "\n" + LocaleUtils.getString(Locale.ALERT_ERRORINFO);
                sb.append(errorInfo);

                for (Object arg : args) {
                    sb.append("\n");
                    sb.append(arg);
                }
            }

            String okButtonText = LocaleUtils.getString(Locale.RETRY);
            String cancelButtonText = LocaleUtils.getString(Locale.CANCEL);
            if (DialogBox.Status.OK == DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, sb.toString(), okButtonText, cancelButtonText, DialogBox.DialogType.ERROR)) {
                isRetry = true;
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }

        return isRetry;
    }

    /**
     * アラートを表示する。
     *
     * @param errorType
     */
    public static void alert(ServerErrorTypeEnum errorType) {
        String messageKey;
        String detailsKey;

        switch (errorType) {
            case SUCCESS:
                return;
            case NOT_PERMIT_EQUIPMENT:
                messageKey = Locale.ALERT_EQUIPMENT_LOGIN;
                detailsKey = Locale.ALERT_EQUIPMENT_LOGIN_DETAILS;
                break;
            case NOT_LOGINID_ORGANIZATION:
                messageKey = Locale.ALERT_ORGANIZATION_LOGIN;
                detailsKey = Locale.ALERT_ORGANIZATION_LOGIN_DETAILS;
                break;
            case LICENSE_ERROR:
                messageKey = Locale.ALERT_LICENSE_ERROR;
                detailsKey = Locale.ALERT_LICENSE_ERROR_DETAILS;
                break;
            case NOTFOUND_WORKFLOW:
                messageKey = Locale.ALERT_NOTFOUND_WORKFLOW_ERROR;
                detailsKey = Locale.ALERT_WORKFLOW_ERROR_DETAILS;
                break;
            case NOTFOUND_ORGANIZATION:
                messageKey = Locale.ALERT_NOTFOUND_ORGANIZATION_ERROR;
                detailsKey = Locale.ALERT_NOTFOUND_ORGANIZATION_ERROR_DETAILS;
                break;
            case INVALID_ARGUMENT:
                messageKey = Locale.ALERT_INVALID_ARGUMENT_ERROR;
                detailsKey = Locale.ALERT_INVALID_ARGUMENT_ERROR_DETAILS;
                break;
            case IDENTNAME_OVERLAP:
                messageKey = Locale.ALERT_IDENTNAME_OVERLAP_ERROR;
                detailsKey = Locale.ALERT_IDENTNAME_OVERLAP_ERROR_DETAILS;
                break;
            default:
                DialogBox.alert(Locale.ALERT_SERVERERROR, null, errorType.toString());
                return;
        }

        if (!StringUtils.isEmpty(messageKey) && !StringUtils.isEmpty(detailsKey)) {
            DialogBox.simpleAlert(messageKey, detailsKey);
        }
    }

    /**
     * アラートを表示する。
     *
     * @param ex
     * @return
     */
    public static Boolean alert(Exception ex) {
        if (ex instanceof ExecutionException || ex instanceof WebApplicationException) {
            return DialogBox.alert((Exception)ex.getCause());
        } else if (ex instanceof MarshalException) {
            Throwable throwable = ((MarshalException)ex).getLinkedException();
            if (Objects.nonNull(throwable)) {
                return DialogBox.alert((Exception)throwable);
            }
            return DialogBox.alert((Exception)ex.getCause());
        }
        return DialogBox.alert2(ex);
    }

    /**
     * アラートを表示する。
     *
     * @param ex
     * @return 再試行するかどうかを返す。
     */
    private static Boolean alert2(Exception ex) {
        Boolean isRetry = false;

        if (ex instanceof UnknownHostException || ex instanceof SocketTimeoutException) {
            DialogBox.alert(Locale.ALERT_UNKNOWN_SERVER, Locale.ALERT_UNKNOWN_SERVER_DETAILS);
        } else if (ex instanceof ConnectException) {
            DialogBox.alert(Locale.ALERT_CONNCT_SERVER, Locale.ALERT_CONNCT_SERVER_DETAILS);
        } else if (ex instanceof SSLException) {
            DialogBox.alert(Locale.ALERT_ACCESS_DENIED, Locale.ALERT_ACCESS_DENIED_DETAILS, ex.getMessage());
        } else if (ex instanceof NoSuchAlgorithmException) {
            DialogBox.alert(Locale.ALERT_CONNCT_SERVER, Locale.ALERT_CONNCT_SERVER_DETAILS, ex.getMessage());
        } else {
            DialogBox.alert(Locale.ALERT_SYSTEMERROR, Locale.ALERT_SYSTEMERROR_DETAILS, ex.getMessage());
        }

        return isRetry;
    }

    /**
     * アラートを表示する。
     *
     * @param messageKey
     * @param detailsKey
     * @param args
     */
    public static void warn(String messageKey, String detailsKey, Object... args) {
        SceneContiner sc = SceneContiner.getInstance();

        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(messageKey);

             StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(detailsKey)) {
                sb.append(LocaleUtils.getString(detailsKey));
            }

            if (args.length > 0) {
                String errorInfo = "\n" + LocaleUtils.getString(Locale.ALERT_ERRORINFO);
                sb.append(errorInfo);

                for (Object arg : args) {
                    sb.append("\n");
                    sb.append(arg);
                }
            }

            String okButtonText = LocaleUtils.getString(Locale.OK);
            DialogBox.Status status = DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, sb.toString(), okButtonText, "", DialogBox.DialogType.WARNING);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }
    /**
     * 問い合わせメッセージを表示する。
     *
     * @param messageKey
     * @param detailsKey
     * @return
     */
    public static Status question(String messageKey, String detailsKey) {
        SceneContiner sc = SceneContiner.getInstance();
        DialogBox.Status status = Status.CANCEL;

        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(messageKey);
            String details = LocaleUtils.getString(detailsKey);

            String okButtonText = LocaleUtils.getString(Locale.YES);
            String cancelButtonText = LocaleUtils.getString(Locale.NO);
            status = DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, details, okButtonText, cancelButtonText, DialogBox.DialogType.QUESTION);
        } catch (Exception ex) {
            logger.fatal(ex);
        }

        return status;
    }

    /**
     * メッセージを表示する。
     *
     * @param messageKey
     * @param detailsKey
     */
    public static void info(String messageKey, String detailsKey) {
        SceneContiner sc = SceneContiner.getInstance();

        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(messageKey);
            String details = LocaleUtils.getString(detailsKey);

            String okButtonText = LocaleUtils.getString(Locale.OK);
            DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, details, okButtonText, null, DialogBox.DialogType.INFOMATION);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * 指定した時間だけメッセージを表示する。
     *
     * @param messageKey
     * @param detailsKey
     * @param millisecond
     */
    public static void info(String messageKey, String detailsKey, long millisecond) {
        SceneContiner sc = SceneContiner.getInstance();

        try {
            String title = LocaleUtils.getString(Locale.APPLICATION_TITLE);
            String message = LocaleUtils.getString(messageKey);
            String details = LocaleUtils.getString(detailsKey);

            String okButtonText = LocaleUtils.getString(Locale.OK);
            DialogBox.Show(sc.getStage().getScene().getWindow(), title, message, details, okButtonText, null, DialogBox.DialogType.INFOMATION, millisecond);
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }
}
