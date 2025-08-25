/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFloorLayoutEditor;

import adtekfuji.fxscene.FxComponent;
import adtekfuji.fxscene.SceneContiner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Canvas FXML Controller class.
 *
 * @author ke.yokoi
 */
@FxComponent(id = "Canvas", fxmlPath = "/fxml/canvas.fxml")
public class CanvasFxController implements Initializable {

    private static final Logger logger = LogManager.getLogger();
    private ResourceBundle resource;
    private final SceneContiner sc = SceneContiner.getInstance();
    private final Map<Integer, IconObject> nodes = Collections.synchronizedMap(new HashMap<>());
    private IconObject selectedNode = null;
    private double initIconPosX = 100;
    private double initIconPosY = 100;
    private final double offsetIconPosX = Setting.GetIconDefalutWidth() / 2;
    private final double offsetIconPosY = Setting.GetIconDefalutHeight() * 1.1;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane canvasPane;
    @FXML
    private ImageView imageBackground;

    /**
     * constructor.
     */
    public CanvasFxController() {
    }

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.resource = rb;
        this.canvasPane.addEventHandler(KeyEvent.KEY_PRESSED, keyOperationHandler);
        this.imageBackground.setFitWidth(Setting.GetResolutionWidth());
        this.imageBackground.setFitHeight(Setting.GetResolutionHeight());
        this.imageBackground.addEventHandler(MouseEvent.MOUSE_PRESSED, selectedHandler);

        Platform.runLater(() -> {
            // 背景画像があれば表示する.
            File file = new File(Setting.GetBackImagePath());
            if (file.exists()) {
                updateBackImage(file);
            }
            // アイコン設定があれば表示する.
            for (int loop = 1; loop <= Setting.GetMaxIconNum(); loop++) {
                if (Setting.GetIconExist(loop)) {
                    IconObject rect = new IconObject(
                            loop, String.valueOf(loop),
                            Setting.GetIconForeColor(),
                            Setting.GetIconBackColor(),
                            Setting.GetIconPosX(loop),
                            Setting.GetIconPosY(loop),
                            Setting.GetIconWidth(loop),
                            Setting.GetIconHeight(loop));
                    addNode(loop, rect);
                }
            }
        });
    }

    /**
     * アイコン追加.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onAddAction(ActionEvent event) {
        // IDを決定する。数値が空いているところを採用する。
        int id = 0;
        for (int loop = 1; loop <= Setting.GetMaxIconNum(); loop++) {
            if (!nodes.containsKey(loop)) {
                id = loop;
                break;
            }
        }
        if (id == 0 || id > Setting.GetMaxIconNum()) {
            sc.showMessageBox(Alert.AlertType.INFORMATION, "", String.format(resource.getString("key.maxIcons"), Setting.GetMaxIconNum()), new ButtonType[]{ButtonType.OK}, ButtonType.OK);
            return;
        }
        // 表示位置を決定する.
        double x = this.initIconPosX;
        double y = this.initIconPosY;
        this.initIconPosX += this.offsetIconPosX;
        this.initIconPosY += this.offsetIconPosY;
        if ((this.initIconPosX + Setting.GetIconDefalutWidth()) > this.scrollPane.getWidth()) {
            this.initIconPosX = 100;
        }
        if ((this.initIconPosY + Setting.GetIconDefalutHeight()) > this.scrollPane.getHeight()) {
            this.initIconPosY = 100;
        }
        // 追加.
        IconObject rect = new IconObject(
                id, String.valueOf(id),
                Setting.GetIconForeColor(), Setting.GetIconBackColor(),
                x, y,
                Setting.GetIconDefalutWidth(), Setting.GetIconDefalutHeight());
        this.addNode(id, rect);
        Changed.setChanged(true);
    }

    /**
     * アイコン削除.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onDeleteAction(ActionEvent event) {
        this.removeNode();
        Changed.setChanged(true);
    }

    /**
     * 画像ファイル選択.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onSelectImage(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resource.getString("key.selectBackImage"));
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(sc.getStage());
            if (selectedFile != null) {
                Setting.SetBackImagePath(selectedFile.getPath());
                updateBackImage(selectedFile);
                Changed.setChanged(true);
            }
        }
        finally {
            canvasPane.setFocusTraversable(true);
        }
    }

    /**
     * 設定保存.
     *
     * @param event ActionEvent
     */
    @FXML
    private void onSave(ActionEvent event) {
        ButtonType buttonType = sc.showMessageBox(Alert.AlertType.INFORMATION, "", resource.getString("key.overwriteLayout"), new ButtonType[]{ButtonType.OK, ButtonType.CANCEL}, ButtonType.CANCEL);
        if (buttonType == ButtonType.OK) {
            Setting.store();
            //sc.showMessageBox(Alert.AlertType.INFORMATION, "", resource.getString("key.postSave"), new ButtonType[]{ButtonType.OK}, ButtonType.OK);
            Changed.setChanged(false);
        }
    }

    /**
     * 背景画像を更新する.
     *
     * @param imagePath 画像ファイルパス
     */
    private void updateBackImage(File imagePath) {
        Image image;
        try {
            image = new Image(new FileInputStream(imagePath));
            imageBackground.setImage(image);
            if (!Setting.GetFitResolution()) {
                // 背景画像を等倍サイズで表示する
                imageBackground.setFitHeight(image.getHeight());
                imageBackground.setFitWidth(image.getWidth());
            }
        }
        catch (FileNotFoundException ex) {
            logger.fatal(ex, ex);
        }
    }

    /**
     * 初期起動およびアイコン追加ボタン押下によるアイコン追加処理
     *
     * @param id
     * @param iconObject
     */
    private void addNode(Integer id, IconObject iconObject) {
        iconObject.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, selectedHandler);
        iconObject.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, viewProperty);
        iconObject.getNode().addEventHandler(KeyEvent.KEY_PRESSED, keyOperationHandler);
        synchronized (nodes) {
            nodes.put(id, iconObject);
            canvasPane.getChildren().add(iconObject.getNode());
        }
    }

    /**
     * アイコン削除処理
     */
    private void removeNode() {
        synchronized (nodes) {
            if (selectedNode != null) {
                nodes.remove(selectedNode.getId());
                Setting.RemoveIcon(selectedNode.getId());
                canvasPane.getChildren().remove(selectedNode.getNode());
            }
        }
    }

    /**
     * キーボードによるアイコン移動処理
     *
     * @param x
     * @param y
     */
    private void moveNode(double x, double y) {
        if (selectedNode != null) {
            selectedNode.moveNode(x, y);
        }
    }

    /**
     * アイコン選択イベント処理
     */
    private final EventHandler<MouseEvent> selectedHandler = (MouseEvent event) -> {
        synchronized (nodes) {
            this.selectedNode = null;
            nodes.forEach((id, node) -> {
                Node target = (Node) event.getTarget();
                if (node.hasNode(target)) {
                    node.setSelected(target);
                    this.selectedNode = node;
                } else {
                    node.setUnselected();
                }
            });
        }
        event.consume();
    };

    /**
     * アイコンプロパティ表示処理
     */
    private final EventHandler<MouseEvent> viewProperty = (MouseEvent event) -> {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            if (this.selectedNode != null) {
                IconObjectBackup backup = this.selectedNode.getBackup();
                ButtonType ret = sc.showComponentDialog("Property", "Property", this.selectedNode);
                if (!ret.equals(ButtonType.OK)) {
                    this.selectedNode.setRestore(backup);
                }
            }
        }
        event.consume();
    };

    /**
     * キーボードイベント処理
     */
    private final EventHandler<KeyEvent> keyOperationHandler = (KeyEvent event) -> {
        switch (event.getCode()) {
            case PAGE_UP:
                scrollPane.setVvalue(scrollPane.vvalueProperty().get() - scrollPane.viewportBoundsProperty().getValue().getHeight() / 2);
                break;
            case PAGE_DOWN:
                scrollPane.setVvalue(scrollPane.vvalueProperty().get() + scrollPane.viewportBoundsProperty().getValue().getHeight() / 2);
                break;

            case DELETE:
                this.removeNode();
                break;
            case UP:
                this.moveNode(0, -1);
                break;
            case DOWN:
                this.moveNode(0, 1);
                break;
            case LEFT:
                this.moveNode(-1, 0);
                break;
            case RIGHT:
                this.moveNode(1, 0);
                break;
        }
        event.consume();
    };
}
