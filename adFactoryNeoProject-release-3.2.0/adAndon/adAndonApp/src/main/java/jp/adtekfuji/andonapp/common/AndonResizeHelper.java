/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andonapp.common;

import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static jp.adtekfuji.andon.common.Constants.SHOW_FRAME;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ウィンドウフレームなしで表示・サイズ変更を行わせるためのヘルパークラス
 *
 * @see
 * <a href="https://gist.github.com/Simonwep/642587d0e307de6da6347ba56f396231">参考元</a>
 *
 * @author fu-kato
 */
public class AndonResizeHelper {

    private static final Logger logger = LogManager.getLogger();

    private final Stage stage;
    private final Scene scene;
    private final double minWidth;
    private final double minHeight;
    private final boolean resizable;

    private final int BORDER_WIDTH = 6;

    private double sceneX = 0;
    private double sceneY = 0;
    private Cursor cursorEvent = Cursor.DEFAULT;
    private double stageWidth;
    private double stageHeight;
    private double screenX;
    private double screenY;

    final ContextMenu contextMenu = new ContextMenu();

    public AndonResizeHelper(Stage stage, double minWidth, double minHeight) {
        this(stage, true, minWidth, minHeight);
    }

    /**
     * 画面ドラッグによる移動やUNDECORATEDウィンドウをサイズ変更可能にする。<br>
     * また右クリックによるメニューも表示する。
     *
     * @param stage
     * @param resizable
     * trueの場合ウィンドウサイズ変更でない場合(UNDECORATED等)であっても変更可能にする。もともと変更可能なものならtrueにしてはいけない。
     * @param minWidth
     * @param minHeight
     */
    public AndonResizeHelper(Stage stage, boolean resizable, double minWidth, double minHeight) {
        this.stage = stage;
        this.scene = stage.getScene();
        this.minWidth = minWidth;
        this.minHeight = minHeight;

        this.resizable = resizable;

        scene.addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);

        createContextMenu();
    }

    private void createContextMenu() {
        final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

        {
            // フルスクリーン・解除

            final Function<Boolean, String> selectFullscreenText = b -> b ? LocaleUtils.getString("key.FullScreenExit") : LocaleUtils.getString("key.FullScreen");
            final MenuItem item = new MenuItem(selectFullscreenText.apply(stage.isFullScreen()));

            // 右クリック時に文字列を決めるとEscで全画面を解除した場合などに正常に切り替えられないため全画面切り替え時に文字列を決定する
            stage.fullScreenProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                item.setText(selectFullscreenText.apply(newValue));
            });
            item.setOnAction((ActionEvent e) -> {
                stage.setFullScreen(!stage.isFullScreen());
            });

            contextMenu.getItems().add(item);
        }
        {
            // フレームの表示（要再起動）

            final Function<Boolean, String> selectShowFrameText = b -> b ? LocaleUtils.getString("key.HideFrame") : LocaleUtils.getString("key.ShowFrame");
            final boolean showFrame = Boolean.valueOf(AdProperty.getProperties().getProperty(SHOW_FRAME, String.valueOf(false)));

            final MenuItem item = new MenuItem(selectShowFrameText.apply(showFrame));
            item.setOnAction((ActionEvent e) -> {
                AdProperty.getProperties().setProperty(SHOW_FRAME, String.valueOf(!showFrame));
            });

            contextMenu.getItems().add(item);
        }
        {
            // 最小化

            final MenuItem item = new MenuItem(LocaleUtils.getString("key.Minimize"));
            item.setOnAction((ActionEvent e) -> {
                stage.setIconified(true);
            });

            contextMenu.getItems().add(item);
        }
        {
            // 終了

            final MenuItem item = new MenuItem(LocaleUtils.getString("key.Exit"));
            item.setOnAction((ActionEvent e) -> {
                // stage.close()だとウィンドウ座標が記録されないのでイベントを発火する
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            });

            contextMenu.getItems().add(item);
        }
    }

    private void mouseMoved(MouseEvent event) {
        if (Objects.equals(MouseEvent.MOUSE_MOVED, event.getEventType())) {
            if (resizable) {
                updateCursor(event);
            }
        }
        event.consume();
    }

    private void mousePressed(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            sceneX = event.getSceneX() + scene.getX(); // ウィンドウフレームが存在する場合その分だけsceneの値がずれるためgetXを追加しておく
            sceneY = event.getSceneY() + scene.getY();
            screenX = event.getScreenX();
            screenY = event.getScreenY();
            stageWidth = stage.getWidth();
            stageHeight = stage.getHeight();
        }

        if (event.getButton().equals(MouseButton.SECONDARY) && !contextMenu.isShowing()) {
            contextMenu.show(scene.getRoot(), event.getScreenX(), event.getScreenY());
        } else {
            contextMenu.hide();
        }

        event.consume();
    }

    private void mouseDragged(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && !stage.isFullScreen()) {

            if (Cursor.DEFAULT.equals(cursorEvent)) {
                stage.setX(event.getScreenX() - sceneX);
                stage.setY(event.getScreenY() - sceneY);
            } else if (resizable) {
                resize(event);
            }

        }
        event.consume();
    }

    private void resize(MouseEvent event) {
        final double sh = stageHeight + (event.getScreenY() - screenY);
        final double nh = stageHeight - (event.getScreenY() - screenY);
        final double ww = stageWidth - (event.getScreenX() - screenX);
        final double ew = stageWidth + (event.getScreenX() - screenX);
        if (Cursor.NW_RESIZE.equals(cursorEvent)) {
            if (nh > minHeight) {
                stage.setHeight(nh);
                stage.setY(event.getScreenY() - sceneY);
            }
            if (ww > minWidth) {
                stage.setWidth(ww);
                stage.setX(event.getScreenX() - sceneX);
            }
        } else if (Cursor.SW_RESIZE.equals(cursorEvent)) {
            if (sh > minHeight) {
                stage.setHeight(sh);
            }
            if (ww > minWidth) {
                stage.setWidth(ww);
                stage.setX(event.getScreenX() - sceneX);
            }
        } else if (Cursor.NE_RESIZE.equals(cursorEvent)) {
            if (nh > minHeight) {
                stage.setHeight(nh);
                stage.setY(event.getScreenY() - sceneY);
            }
            if (ew > minWidth) {
                stage.setWidth(ew);
            }
        } else if (Cursor.SE_RESIZE.equals(cursorEvent)) {
            if (sh > minHeight) {
                stage.setHeight(sh);
            }
            if (ew > minWidth) {
                stage.setWidth(ew);
            }
        } else if (Cursor.E_RESIZE.equals(cursorEvent)) {
            if (ew > minWidth) {
                stage.setWidth(ew);
            }
        } else if (Cursor.W_RESIZE.equals(cursorEvent)) {
            if (ww > minWidth) {
                stage.setWidth(ww);
                stage.setX(event.getScreenX() - sceneX);
            }
        } else if (Cursor.N_RESIZE.equals(cursorEvent)) {
            if (nh > minHeight) {
                stage.setHeight(nh);
                stage.setY(event.getScreenY() - sceneY);
            }
        } else if (Cursor.S_RESIZE.equals(cursorEvent)) {
            if (sh > minHeight) {
                stage.setHeight(sh);
            }
        }
    }

    private void updateCursor(MouseEvent event) {
        final double mouseEventX = event.getSceneX();
        final double mouseEventY = event.getSceneY();
        final double sceneWidth = scene.getWidth();
        final double sceneHeight = scene.getHeight();
        final boolean lb = mouseEventX < BORDER_WIDTH;
        final boolean tb = mouseEventY < BORDER_WIDTH;
        final boolean rb = mouseEventX > sceneWidth - BORDER_WIDTH;
        final boolean bb = mouseEventY > sceneHeight - BORDER_WIDTH;
        if (lb && tb) {
            cursorEvent = Cursor.NW_RESIZE;
        } else if (lb && bb) {
            cursorEvent = Cursor.SW_RESIZE;
        } else if (rb && tb) {
            cursorEvent = Cursor.NE_RESIZE;
        } else if (rb && bb) {
            cursorEvent = Cursor.SE_RESIZE;
        } else if (lb) {
            cursorEvent = Cursor.W_RESIZE;
        } else if (rb) {
            cursorEvent = Cursor.E_RESIZE;
        } else if (tb) {
            cursorEvent = Cursor.N_RESIZE;
        } else if (bb) {
            cursorEvent = Cursor.S_RESIZE;
        } else {
            cursorEvent = Cursor.DEFAULT;
        }

        scene.setCursor(cursorEvent);
    }
}
