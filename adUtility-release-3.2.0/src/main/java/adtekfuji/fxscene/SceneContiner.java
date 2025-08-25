/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.fxscene;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.FindClasses;
import adtekfuji.plugin.PluginLoader;
import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 画面遷移と表示領域に対するコンポーネントの表示切り替えを行う.
 *
 * @author ke.yokoi
 */
public class SceneContiner {

    class SceneDefinition {
        private String name;
        private URL url;
    }

    private final static Logger logger = LogManager.getLogger();
    private static SceneContiner instance = null;
    private final SceneProperties sp;
    private final Stage stage;
    private final StageProperties properties;
    private final Map<String, SceneDefinition> sceneDefinitions = new HashMap<>();
    private final Map<String, String> fxComponents = new HashMap<>();
    private final Map<String, Object> components = new HashMap<>();
    private final Map<String, Object> componentArea = new HashMap<>();
    private final Map<String, Map<String, Double>> componentSize = new HashMap<>();

    private final Map<ButtonType, String> buttonTitleMap = new HashMap<ButtonType, String>() {
        {
            put(ButtonType.OK, "key.Ok");
            put(ButtonType.CANCEL, "key.Cancel");
            put(ButtonType.CLOSE, "key.Close");
            put(ButtonType.NO, "key.No");
            put(ButtonType.YES, "key.Yes");
        }
    };

    // 現在表示されているコンポーネント
    private final Map<Pane, ComponentHandler> currentComponents = new HashMap<>();

    /**
     * 表示待ちコンポーネント
     */
    private static final ObjectProperty<Pane> showingPane = new SimpleObjectProperty<>();

    /**
     * コンポーネントが表示されたら、フォーカスを設定する。
     */
    private static final ChangeListener<Number> listener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
        if (Objects.nonNull(showingPane.get())) {
            Pane pane = (Pane) showingPane.get();
            pane.requestFocus();
            pane.widthProperty().removeListener(SceneContiner.listener);
            showingPane.set(null);
        }
    };

    private SceneContiner(final SceneProperties sp) {
        this.sp = sp;
        this.stage = sp.getStage();
        this.properties = new StageProperties(stage, sp.getProperties());
        this.sceneDefinitions.clear();
        this.fxComponents.clear();
        this.components.clear();
        searchAnnotation();
    }

    public static void createInstance(final SceneProperties sp) {
        if (Objects.isNull(sp)) {
            logger.fatal(new IllegalArgumentException());
        }
        if (Objects.isNull(instance)) {
            instance = new SceneContiner(sp);
        }
    }

    public static SceneContiner getInstance() {
        if (Objects.isNull(instance)) {
            logger.fatal("not create instance");
        }
        return instance;
    }

    private void searchAnnotation() {
        try {
            List<Class> classes = FindClasses.find("adtekfuji", "jp.adtekfuji");
            classes.stream().forEach((clazz) -> {
                // FxSceneアノテーション
                FxScene scene = (FxScene) clazz.getAnnotation(FxScene.class);
                if (Objects.nonNull(scene)) {
                    SceneDefinition sceneDef = new SceneDefinition();
                    sceneDef.name = clazz.getSimpleName();
                    sceneDef.url = clazz.getResource(scene.fxmlPath());

                    this.sceneDefinitions.put(scene.id(), sceneDef);
                    logger.debug("searchAnnotation-FxScene:{},{}", scene.id(), sceneDef);
                }

                // FxComponentアノテーション
                FxComponent compo = (FxComponent) clazz.getAnnotation(FxComponent.class);
                if (Objects.nonNull(compo)) {
                    SceneDefinition sceneDef = new SceneDefinition();
                    sceneDef.name = clazz.getSimpleName();
                    sceneDef.url = clazz.getResource(compo.fxmlPath());

                    this.sceneDefinitions.put(compo.id(), sceneDef);
                    this.fxComponents.put(compo.id(), compo.fxmlPath());
                    logger.debug("searchAnnotation-fxComponents:{},{}", compo.id(), compo.fxmlPath());
                }
            });
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }


    /**
     * 指定されたIDに基づいてシーンを切り替えるメソッドです。このメソッドは、現在のコンポーネントを破棄し、
     * 新しいシーンに必要なコンテンツを設定します。
     *
     * @param id シーンの識別子。切り替え対象のシーンを特定するために使用します。
     * @param argument シーン切り替え時に引数として渡されるオブジェクト。
     * @return シーンの切り替えが正常に完了した場合はtrue、失敗した場合はfalseを返します。
     */
    public boolean trans(String id, Object argument) {
        logger.info("trans: {}", id);

        SceneDefinition scene = this.sceneDefinitions.get(id);
        if (Objects.isNull(scene)) {
            logger.fatal(new IllegalArgumentException(id + " is notthing."));
            return false;
        }

        for (ComponentHandler componentHandler : this.currentComponents.values()) {
            // コンポーネントを破棄
            if (!componentHandler.destoryComponent()) {
                logger.info("Switching component is denied: {}", componentHandler.getClass().getSimpleName());
                return false;
            }
        }

        this.currentComponents.clear();
        this.replaceSceneContent(scene, argument);

        return true;
    }


    /**
     * 指定されたIDを使用してトランザクションを処理します。
     *
     * @param id トランザクションに使用する識別子
     * @return トランザクションが成功した場合はtrue、それ以外はfalse
     */
    public boolean trans(String id) {
        return trans(id, null);
    }

    /**
     * コンポーネントを切り替える。
     *
     * @author ke.yokoi
     * @param pane 描画領域となるPaneオブジェクト
     * @param component コンポーネント識別文字
     * @return
     */
    public boolean setComponent(Pane pane, String component) {
        return setComponent(pane, component, null);
    }


    /**
     * 指定されたPaneに指定されたコンポーネントを設定します。
     * また、必要に応じて引数を提供します。
     *
     * @param pane 設定先のPaneオブジェクト
     * @param component 設定するコンポーネントの名前
     * @param argument コンポーネントに渡す引数
     * @return コンポーネントの設定が成功した場合はtrue、失敗した場合はfalse
     */
    public boolean setComponent(Pane pane, String component, Object argument) {
        logger.info("setComponent:{},{}", pane, component);
        if (Objects.isNull(pane)) {
            logger.fatal(new IllegalArgumentException("Area is null."));
            return false;
        }
        if (!this.sceneDefinitions.containsKey(component)) {
            logger.fatal(new IllegalArgumentException(component + " is notthing."));
            return false;
        }
        return this.changeSceneContent(pane, component, argument, false);
    }

    /**
     * コンポーネントを切り替える。
     *
     * @param pane
     * @param component
     * @param argument
     * @param copied
     * @return
     */
    public boolean setComponent(Pane pane, String component, Object argument, boolean copied) {
        logger.info("setComponent:{},{}", pane, component);
        if (Objects.isNull(pane)) {
            logger.fatal(new IllegalArgumentException("Area is null."));
            return false;
        }
        if (!this.sceneDefinitions.containsKey(component)) {
            logger.fatal(new IllegalArgumentException(component + " is notthing."));
            return false;
        }
        return this.changeSceneContent(pane, component, argument, copied);
    }

    /**
     * 指定描画位置のコンポーネントを切り替えます.(識別文字指定)
     *
     * @author ke.yokoi
     * @param area 描画領域識別文字
     * @param component コンポーネント識別文字
     * @return
     */
    public boolean setComponent(String area, String component) {
        return this.setComponent(area, component, null);
    }

    /**
     * 指定描画位置のコンポーネントを切り替えます.(識別文字指定)
     *
     * @author ke.yokoi
     * @param area 描画領域識別文字
     * @param component コンポーネント識別文字
     * @param argument コントロールクラスへの引数
     * @return
     */
    public boolean setComponent(String area, String component, Object argument) {
        logger.info("setComponent:{},{}", area, component);
        
        if (!this.componentArea.containsKey(area)) {
            logger.fatal(new IllegalArgumentException(area + " is notthing"));
            return false;
        }
        
        if (!this.sceneDefinitions.containsKey(component)) {
            logger.fatal(new IllegalArgumentException(component + " is notthing"));
            return false;
        }

        return this.changeSceneContent((Pane) componentArea.get(area), component, argument, false);
    }

    /**
     * 警告ダイアログ表示.
     *
     * @param type javafx.scene.control.Alert.AlertType
     * @param title ダイアログタイトル
     * @param message ダイアログメッセージ
     */
    public void showAlert(Alert.AlertType type, String title, String message) {
        try {
            logger.info("showAlert start:{},{}", type, title);

            Alert alert = new Alert(type, "", ButtonType.OK);
            alert.initOwner(stage);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.showAndWait();
        } finally {
            logger.info("showAlert end.");
        }
    }

    /**
     * ダイアログの所有者を指定して警告ダイアログ表示.
     *
     * @param type
     * @param title
     * @param message
     * @param owner
     */
    public void showAlert(Alert.AlertType type, String title, String message, Stage owner) {
        try {
            logger.info("showAlert start:{},{}", type, title);

            final ButtonType buttonType = ButtonType.OK;
            Alert alert = new Alert(type, "", buttonType);
            alert.initOwner(owner);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.showAndWait();

            Node button = alert.getDialogPane().lookupButton(buttonType);
            if (button instanceof  Button) {
                ((Button) button).setText(LocaleUtils.getString(buttonTitleMap.get(buttonType)));
            }

        } finally {
            logger.info("showAlert end.");
        }
    }

    public void showAlert(Alert.AlertType type, String title, String message, String details) {
        try {
            logger.info("showAlert start:{},{}", type, title);

            final ButtonType buttonType = ButtonType.OK;
            Alert alert = new Alert(type, "", buttonType);
            alert.initOwner(this.stage);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.setContentText(details);
            alert.showAndWait();

            Node button = alert.getDialogPane().lookupButton(buttonType);
            if (button instanceof  Button) {
                ((Button) button).setText(LocaleUtils.getString(buttonTitleMap.get(buttonType)));
            }

        } finally {
            logger.info("showAlert end.");
        }
    }

    /**
     * メッセージボックスをタイトルとヘッダー文字列のみ表示する。本文には何も表示しない。
     *
     * @param alertType
     * @param title
     * @param message
     * @param buttonTypes
     * @param defaultButton
     * @return
     */
    public ButtonType showMessageBox(Alert.AlertType alertType, String title, String message, ButtonType[] buttonTypes, ButtonType defaultButton) {
        return showMessageBox(alertType, title, message, null, buttonTypes, defaultButton);
    }

    /**
     * メッセージボックスを表示する。
     *
     * @param alertType
     * @param title ダイアログボックスのウィンドウタイトルとして表示する文字列
     * @param message ヘッダーとして表示する文字列
     * @param content 本文として表示する文字列
     * @param buttonTypes
     * @param defaultButton
     * @return
     */
    public ButtonType showMessageBox(Alert.AlertType alertType, String title, String message, String content, ButtonType[] buttonTypes, ButtonType defaultButton) {
        try {
            logger.info("showMessageBox start:{},{}", alertType, title);

            Alert alert = new Alert(alertType, "", buttonTypes);
            alert.initOwner(this.stage);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.setContentText(content);
            alert.setResult(defaultButton);

            Stream.of(buttonTypes)
                    .filter(buttonTitleMap::containsKey)
                    .forEach(buttonType -> {
                        Node button = alert.getDialogPane().lookupButton(buttonType);
                        if (button instanceof  Button) {
                            ((Button) button).setText(LocaleUtils.getString(buttonTitleMap.get(buttonType)));
                        }
                    });

            ButtonType result = alert.showAndWait().orElse(defaultButton);
            logger.info("Choice:{}", result);

            return result;
        } finally {
            logger.info("showMessageBox end.");
        }
    }

    /**
     * メッセージボックスを表示する。
     *
     * @param alertType
     * @param title ダイアログボックスのウィンドウタイトルとして表示する文字列
     * @param message ヘッダーとして表示する文字列
     * @param content 本文として表示する文字列
     * @param buttonTypes
     * @param defaultButton
     * @param owner 親ウィンドウ
     * @return
     */
    public ButtonType showMessageBox(Alert.AlertType alertType, String title, String message, String content, ButtonType[] buttonTypes, ButtonType defaultButton, Window owner) {
        try {
            logger.info("showMessageBox start:{},{}", alertType, title);

            Alert alert = new Alert(alertType, "", buttonTypes);
            alert.initOwner(owner);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(message);
            alert.setContentText(content);
            alert.setResult(defaultButton);

            Stream.of(buttonTypes)
                    .filter(buttonTitleMap::containsKey)
                    .forEach(buttonType -> {
                        Node button = alert.getDialogPane().lookupButton(buttonType);
                        if (button instanceof  Button) {
                            ((Button) button).setText(LocaleUtils.getString(buttonTitleMap.get(buttonType)));
                        }
                    });

            ButtonType result = alert.showAndWait().orElse(defaultButton);
            logger.info("Choice:{}", result);

            return result;
        } finally {
            logger.info("showMessageBox end.");
        }
    }

    /**
     * 選択ダイアログ表示.
     *
     * @param type javafx.scene.control.Alert.AlertType
     * @param title ダイアログタイトル
     * @param message ダイアログメッセージ
     * @return 押下結果
     */
    public ButtonType showOkCanselDialog(Alert.AlertType type, String title, String message) {
        return this.showMessageBox(type, title, message, new ButtonType[]{ButtonType.OK, ButtonType.CANCEL}, ButtonType.CANCEL);
    }

    /**
     * 選択ダイアログにメッセージと本文を表示するダイアログを作成する
     *
     * @param type javafx.scene.control.Alert.AlertType
     * @param title ダイアログタイトル
     * @param message ダイアログメッセージ
     * @param content 本文
     * @return 押下結果
     */
    public ButtonType showOkCanselDialog(Alert.AlertType type, String title, String message, String content) {
        return this.showMessageBox(type, title, message, content, new ButtonType[]{ButtonType.OK, ButtonType.CANCEL}, ButtonType.CANCEL);
    }

    /**
     * テキスト入力ダイアログ表示.
     *
     * @param title ダイアログタイトル
     * @param message ダイアログメッセージ
     * @param contentText 入力内容説明
     * @param defaultValue テキスト初期値
     * @return 押下結果
     */
    public String showTextInputDialog(String title, String message, String contentText, String defaultValue) {
        logger.info("showTextInputDialog:{},{}", title, message);
        TextInputDialog dlg = new TextInputDialog(defaultValue);
        dlg.initOwner(stage);
        dlg.initStyle(StageStyle.UTILITY);
        dlg.setTitle(title);
        dlg.setHeaderText(message);
        dlg.setContentText(contentText);

        Stream.of(ButtonType.OK, ButtonType.CANCEL)
                .forEach(buttonType -> {
                    Node button = dlg.getDialogPane().lookupButton(buttonType);
                    if (button instanceof  Button) {
                        ((Button) button).setText(LocaleUtils.getString(buttonTitleMap.get(buttonType)));
                    }
                });

        Optional<String> result = dlg.showAndWait();
        //TODO:Cancel時の処理
        if (result.equals(Optional.empty())) {
            return null;
        }
        return result.get();
    }

    /**
     * モーダルダイアログ表示.
     *
     * @param title
     * @param component
     * @param argument
     * @return
     */
    public ButtonType showComponentDialog(String title, String component, Object argument) {
        logger.info("showComponentDialog:{},{}", title, component);
        Dialog dlg = createComponentDialog(Modality.WINDOW_MODAL, title, component, argument, stage);
        Optional<ButtonType> result = dlg.showAndWait();
        return result.get();
    }

    /**
     * リサイズ可能なモーダレスダイアログを表示する。
     * 
     * @param title     ダイアログタイトル
     * @param component コンポーネント名
     * @param argument  コンポーネントに渡す引数
     * @param resize    ダイアログをリサイズできるようにするか (true:リサイズ可、false:リサイズ不可)
     * @return アクション結果を返す
     */
    public ButtonType showComponentDialog(String title, String component, Object argument, boolean resize) {
        logger.info("showComponentDialog:{},{}", title, component);

        Dialog dlg = createComponentDialog(Modality.WINDOW_MODAL, title, component, argument, stage);
        dlg.setResizable(resize);

        DialogProperties dlgProperties = new DialogProperties(dlg, AdProperty.getProperties(), component);
        if (resize) {
            // ダイアログの表示位置とサイズを復元
            dlgProperties.restoration();
        }

        Optional<ButtonType> result = dlg.showAndWait();

        if (resize) {
            // ダイアログの表示位置とサイズを保存
            dlgProperties.storation();
        }

        return result.get();
    }

    /**
     * モーダルダイアログ表示.
     *
     * @param title
     * @param component
     * @param argument
     * @param buttonTypes
     * @param owner
     * @return
     */
    public ButtonType showComponentDialog(String title, String component, Object argument, ButtonType[] buttonTypes, Stage owner) {
        logger.info("showComponentDialog:{},{}", title, component);
        Dialog dialog = createComponentDialog(Modality.WINDOW_MODAL, title, component, argument, owner);

        dialog.getDialogPane().getButtonTypes().clear();
        for (ButtonType buttonType : buttonTypes) {
            dialog.getDialogPane().getButtonTypes().add(buttonType);
            Button button = (Button) dialog.getDialogPane().lookupButton(buttonType);
            button.getStyleClass().add("ContentButton");

            if (button instanceof  Button && buttonTitleMap.containsKey(buttonType)) {
                ((Button) button).setText(LocaleUtils.getString(buttonTitleMap.get(buttonType)));
            }
        }

        Optional<ButtonType> result = dialog.showAndWait();
        return result.get();
    }

    /**
     * モーダルダイアログ表示.
     *
     * @param title
     * @param component
     * @param argument
     * @param buttonTypes
     * @return
     */
    public ButtonType showComponentDialog(String title, String component, Object argument, ButtonType[] buttonTypes) {
        logger.info("showComponentDialog:{},{}", title, component);
        return this.showComponentDialog(title, component, argument, buttonTypes, stage);
    }

    /**
     * モーダレスダイアログ表示.
     *
     * @param title
     * @param component
     * @param argument
     */
    public void showModelessDialog(String title, String component, Object argument) {
        logger.info("showModelessDialog:{},{}", title, component);
        Dialog dlg = createComponentDialog(Modality.NONE, title, component, argument, stage);
        dlg.show();
    }

    /**
     * 所有者を指定してモーダルダイアログ表示.
     *
     * @param title
     * @param component
     * @param argument
     * @param owner
     * @return
     */
    public ButtonType showComponentDialog(String title, String component, Object argument, Stage owner) {
        logger.info("showComponentDialog:{},{}", title, component);
        Dialog dlg = createComponentDialog(Modality.WINDOW_MODAL, title, component, argument, owner);
        Optional<ButtonType> result = dlg.showAndWait();
        return result.get();
    }
    
    /**
     * リサイズ可能なモーダレスダイアログを表示する。
     * 
     * @param title     ダイアログタイトル
     * @param component コンポーネント名
     * @param argument  コンポーネントに渡す引数
     * @param owner
     * @param resize    ダイアログをリサイズできるようにするか (true:リサイズ可、false:リサイズ不可)
     * @return アクション結果を返す
     */
    
    public ButtonType showComponentDialog(String title, String component, Object argument, Stage owner, boolean resize) {
        logger.info("showComponentDialog:{},{}", title, component);
        Dialog dlg = createComponentDialog(Modality.WINDOW_MODAL, title, component, argument, owner);
        dlg.setResizable(resize);

        DialogProperties dlgProperties = new DialogProperties(dlg, AdProperty.getProperties(), component);
        if (resize) {
            // ダイアログの表示位置とサイズを復元
            dlgProperties.restoration();
        }

        Optional<ButtonType> result = dlg.showAndWait();

        if (resize) {
            // ダイアログの表示位置とサイズを保存
            dlgProperties.storation();
        }
        return result.get();
    }

    private Dialog createComponentDialog(Modality modality, String title, String component, Object argument, Stage owner) {
        Dialog dlg = new Dialog();

        if (modality == Modality.NONE) {
            dlg.initOwner(null);
            dlg.initStyle(StageStyle.DECORATED);
            dlg.setResizable(true);
        } else {
            dlg.initOwner(owner);
            dlg.initStyle(StageStyle.UTILITY);
            dlg.setResizable(false);
        }

        dlg.initModality(modality);
        dlg.setTitle(title);

        // ボタン表示
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button btn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        btn.getStyleClass().add("ContentButton");
        btn.setText(LocaleUtils.getString(buttonTitleMap.get(ButtonType.OK)));
        btn = (Button) dlg.getDialogPane().lookupButton(ButtonType.CANCEL);
        btn.getStyleClass().add("ContentButton");
        btn.setText(LocaleUtils.getString(buttonTitleMap.get(ButtonType.CANCEL)));

        // コンポーネント表示
        try {
            ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
            SceneDefinition sceneDef = this.sceneDefinitions.get(component);

            FXMLLoader loader = new FXMLLoader(sceneDef.url, rb);
            loader.setClassLoader(PluginLoader.getClassLoarder());
            Pane pane = (Pane) loader.load();

            if (Objects.nonNull(argument)) {
                ArgumentDelivery controller = (ArgumentDelivery) loader.getController();
                controller.setArgument(argument);
            }
            dlg.getDialogPane().setContent(pane);
        } catch (NullPointerException | IOException ex) {
            logger.warn(ex, ex);
            // 404 not found
            dlg.setContentText("404 not found.");
        }

        return dlg;
    }

    /**
     * モーダルダイアログを表示する。
     *
     * @param title タイトル
     * @param component コンポーネント
     * @param argument 前画面からの引数
     * @return 処理結果
     */
    public ButtonType showDialog(String title, String component, Object argument) {
        return showDialog(title, component, argument, stage, false);
    }

    /**
     * モーダルダイアログを表示する。
     *
     * @param title タイトル
     * @param component コンポーネント
     * @param argument 前画面からの引数
     * @param owner 親画面
     * @return 処理結果
     */
    public ButtonType showDialog(String title, String component, Object argument, Stage owner) {
        return showDialog(title, component, argument, owner, false);
    }

    /**
     * モーダルダイアログを表示する。
     *
     * @param title タイトル
     * @param component コンポーネント
     * @param argument 前画面からの引数
     * @param owner 親画面
     * @param resize true:ウィンドウサイズ可変、false:ウィンドウサイズ固定
     * @return 処理結果
     */
    public ButtonType showDialog(String title, String component, Object argument, Stage owner, boolean resize) {
        Dialog<ButtonType> dlg = new Dialog();

        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.initOwner(owner);
        dlg.initStyle(StageStyle.UTILITY);
        dlg.setResizable(resize);
        dlg.setTitle(title);

        // コンポーネント表示
        try {
            ResourceBundle rb = LocaleUtils.getBundle("locale");
            SceneDefinition sceneDef = this.sceneDefinitions.get(component);

            FXMLLoader loader = new FXMLLoader(sceneDef.url, rb);
            loader.setClassLoader(PluginLoader.getClassLoarder());
            Pane pane = (Pane) loader.load();
            Object controller = loader.getController();

            if (controller instanceof ArgumentDelivery) {
                ((ArgumentDelivery) controller).setArgument(argument);
            }

            if (controller instanceof DialogHandler) {
                ((DialogHandler) controller).setDialog(dlg);
            }

            dlg.getDialogPane().setContent(pane);
        } catch (NullPointerException | IOException ex) {
            logger.warn(ex, ex);
            // 404 not found
            dlg.setContentText("404 not found.");
        }

        Optional<ButtonType> result = dlg.showAndWait();
        return result.get();
    }

    /**
     * モードレスダイアログを開く。
     * 
     * @param title タイトル
     * @param component コンポーネント
     * @param argument パラメーター
     * @param owner 親ウィンドウ
     * @param resize true:ウィンドウサイズ可変、false:ウィンドウサイズ固定
     * @return ダイアログ
     */
    public Dialog showModelessDialog(String title, String component, Object argument, Stage owner, boolean resize) {
        Dialog<ButtonType> dlg = new Dialog();

        dlg.initModality(Modality.WINDOW_MODAL);
        dlg.initOwner(owner);
        dlg.initStyle(StageStyle.UTILITY);
        dlg.setResizable(resize);
        dlg.setTitle(title);

        try {
            ResourceBundle rb = LocaleUtils.getBundle("locale");
            SceneDefinition sceneDef = this.sceneDefinitions.get(component);

            FXMLLoader loader = new FXMLLoader(sceneDef.url, rb);
            loader.setClassLoader(PluginLoader.getClassLoarder());
            Pane pane = (Pane) loader.load();
            Object controller = loader.getController();

            if (controller instanceof ArgumentDelivery) {
                ((ArgumentDelivery) controller).setArgument(argument);
            }

            if (controller instanceof DialogHandler) {
                ((DialogHandler) controller).setDialog(dlg);
            }

            dlg.getDialogPane().setContent(pane);
        } catch (NullPointerException | IOException ex) {
            logger.warn(ex, ex);
            // 404 not found
            dlg.setContentText("404 not found.");
        }

        dlg.show();

        return dlg;
    }

    /**
     * コンポーネントの表示切り替え.
     *
     * @param area 描画領域識別文字
     * @param flg　表示/非表示
     */
    public void visibleArea(String area, Boolean flg) {
        logger.info("visibleArea:{},{}", area, flg);
        if (!componentArea.containsKey(area)) {
            logger.fatal(new IllegalArgumentException(area + " is notthing"));
            return;
        }
        Node node = (Node) componentArea.get(area);
        node.setVisible(flg);
    }

    /**
     * 画面全体の操作禁止切り替え.
     *
     * @param flg ブロック/解除
     */
    public void blockUI(Boolean flg) {
        logger.info("blockUI:{}", flg);
        stage.getScene().getRoot().setDisable(flg);
    }

    /**
     * コンポーネントの操作禁止切り替え.
     *
     * @param area 描画領域識別文字
     * @param flg ブロック/解除
     */
    public void blockUI(String area, Boolean flg) {
        logger.info("blockUI:{},{}", area, flg);
        if (!componentArea.containsKey(area)) {
            logger.fatal(new IllegalArgumentException(area + " is notthing"));
            return;
        }
        Pane pane = (Pane) componentArea.get(area);
        pane.setDisable(flg);
    }

    private void replaceSceneContent(SceneDefinition sceneDef, Object argument) {
        try {
            Scene scene = stage.getScene();
            ResourceBundle rb = LocaleUtils.load("locale");

            FXMLLoader loader = new FXMLLoader(sceneDef.url, rb);
            loader.setClassLoader(PluginLoader.getClassLoarder());
            Parent root = (Parent) loader.load();

            componentArea.clear();
            Object obj = loader.getController();
            if (obj instanceof ArgumentDelivery argumentDelivery) {
                argumentDelivery.setArgument(argument);
            }

            Field[] fs2 = obj.getClass().getDeclaredFields();
            for (Field f : fs2) {
                f.setAccessible(true);
                ComponentArea comp = f.getAnnotation(ComponentArea.class);
                if (Objects.nonNull(comp)) {
                    componentArea.put((String)f.getName(), f.get(obj));
                }
            }

            if (scene == null) {
                // アプリケーション起動時
                scene = new Scene(root);
                scene.getStylesheets().addAll(sp.getCsspathes());
                stage.setTitle(sp.getAppTitle());
                if (Objects.nonNull(sp.getAppIcon())) {
                    stage.getIcons().add(sp.getAppIcon());
                }
                stage.setScene(scene);
                stage.setMinHeight(sp.getMinHeight());
                stage.setMinWidth(sp.getMinWidth());
                properties.restoration();
                stage.setOnCloseRequest((WindowEvent event) -> {
                    for (ComponentHandler componentHandler : currentComponents.values()) {
                        if (!componentHandler.destoryComponent()) {
                            logger.info("Close request is denied: {}", componentHandler.getClass().getSimpleName());
                            event.consume();
                            return;
                        }
                    }
                    properties.storation();
                });
                stage.show();
            } else {
                //2回目以降は差し替えるだけ.
                scene.setRoot(root);
            }
        } catch (NullPointerException | IOException ex) {
            logger.warn(ex, ex);
            //404 not found
            Label node = new Label("404 not found");
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(node);
                stage.setScene(scene);
                stage.show();
            } else {
                stage.getScene().setRoot(node);
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * コンポーネントを切り替える
     *
     * @param current
     * @param component
     * @param argument
     * @param copied
     */
    private boolean changeSceneContent(Pane current, String component, Object argument, boolean copied) {
        try {
            if (this.currentComponents.containsKey(current)) {
                ComponentHandler componentHandler = this.currentComponents.get(current);
                // コンポーネントを破棄
                if (!componentHandler.destoryComponent()) {
                    logger.info("Switching component is denied: {}", componentHandler.getClass().getSimpleName());
                    return false;
                }
            }

            ResourceBundle rb = LocaleUtils.getBundle("locale");
            SceneDefinition sceneDef = this.sceneDefinitions.get(component);

            FXMLLoader loader = new FXMLLoader(sceneDef.url, rb);
            loader.setClassLoader(PluginLoader.getClassLoarder());
            Pane pane = (Pane) loader.load();

            Object controller = loader.getController();
            if (!copied) {
                this.components.put(component, controller);
            } else {
                // 進捗モニターではプラグインを複製する
                int id = 1;
                for (String key : this.components.keySet()) {
                    if (key.startsWith(component)) {
                        id++;
                    }
                }
                this.components.put(component + "." + String.valueOf(id), controller);
            }

            showingPane.set(pane);
            pane.widthProperty().addListener(listener);

            if (Objects.nonNull(argument) && controller instanceof ArgumentDelivery) {
                ArgumentDelivery argumentDelivery = (ArgumentDelivery) controller;
                argumentDelivery.setArgument(argument);
            }

            if (controller instanceof ComponentHandler) {
                this.currentComponents.put(current, (ComponentHandler) controller);
            } else {
                this.currentComponents.remove(current);
            }

            current.getChildren().clear();
            current.getChildren().addAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);

            // save size current
            Map<String, Double> compoSize = new HashMap<>();
            compoSize.put("MinHeight", pane.getMinHeight());
            compoSize.put("MinWidth", pane.getMinWidth());
            compoSize.put("PrefHeight", pane.getPrefHeight());
            compoSize.put("PrefWidth", pane.getPrefWidth());
            componentSize.put(component, compoSize);

            return true;
        } catch (NullPointerException | IOException ex) {
            logger.warn(ex, ex);
            current.getChildren().clear();
            current.getChildren().add(new Label("404 not found"));
            return false;
        }
    }

    /**
     * get Stage
     *
     * @return
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * get current mapping
     *
     * @return
     */
    public Map<String, String> getFxComponents() {
        return fxComponents;
    }

    /**
     * get current object mapping
     *
     * @return
     */
    public Map<String, Object> getFxComponentObjects() {
        return components;
    }

    /**
     * get size of current
     *
     * @param component
     * @return
     */
    public Map<String, Double> getSizeComponent(String component) {
        return componentSize.get(component);
    }

    /**
     * シーンプロパティを取得する
     *
     * @return
     */
    public SceneProperties getSceneProperties() {
        return this.sp;
    }

    /**
     * コンポーネントが存在するかどうかを返す
     *
     * @param component
     * @return
     */
    public boolean containsComponent(String component) {
        return this.sceneDefinitions.containsKey(component);
    }

    /**
     * Windowを取得する。
     *
     * @return
     */
    public Window getWindow() {
        return this.stage.getScene().getWindow();
    }
}
