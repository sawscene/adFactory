/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import adtekfuji.property.AdProperty;
import adtekfuji.utility.StringUtils;
import java.util.Objects;
import java.util.Properties;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import jp.adtekfuji.javafxcommon.common.Constants;

/**
 * リサイザー
 * 
 * @author s-heya
 */
public class DragResizer {

    private static final int RESIZE_MARGIN = 5;
    private static final int MIN_WIDTH = 40;
    
    private final Region region;
    private double pos;
    private boolean initialized;
    private boolean dragging;
    private ObservableList<Region> cells;
    private String name;

    /**
     * コンストラクタ
     * 
     * @param region Region
     */
    private DragResizer(Region region) {
        this.region = region;
    }

    /**
     * コンストラクタ
     * 
     * @param region ヘッダーセル
     * @param cells データセル
     * @param name 保存名
     */
    private DragResizer(Region region, ObservableList<Region> cells, String name) {
        this.region = region;
        this.cells = cells;
        this.name = name;
    }

    /**
     * コントロールの幅をマウスドラッグでリサイズできるようにする。
     * 
     * @param region ヘッダーセル
     * @param cells データセル
     * @param name 保存名
     */
    public static void resizable(Region region, ObservableList<Region> cells, String name) {
        final DragResizer resizer = new DragResizer(region, cells, name);
        resizer.initialize();
        
        region.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mousePressed(event);
            }
        });
        region.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseDragged(event);
            }
        });
        region.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseOver(event);
            }
        });
        region.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseReleased(event);
            }
        });
    }

    /**
     * マウスリリース
     * 
     * @param event MouseEvent
     */
    protected void mouseReleased(MouseEvent event) {
        this.dragging = false;
        this.region.setCursor(Cursor.DEFAULT);
    }

    /**
     * マウスオーバー
     * 
     * @param event MouseEvent
     */
    protected void mouseOver(MouseEvent event) {
        if (this.isInDraggableZone(event) || dragging) {
            this.region.setCursor(Cursor.H_RESIZE);
        } else {
            this.region.setCursor(Cursor.DEFAULT);
        }
    }

    /**
     * マウスカーソルがドラッグ領域内に存在するかどうかを返す。
     * 
     * @param event MouseEvent
     * @return 
     */
    protected boolean isInDraggableZone(MouseEvent event) {
        return event.getX() > (this.region.getWidth() - RESIZE_MARGIN);
    }

    /**
     * マウスドラッグ
     * 
     * @param event MouseEvent
     */
    protected void mouseDragged(MouseEvent event) {
        if (!this.dragging) {
            return;
        }
        
        double newWidth = this.region.getMinWidth() + (event.getX() - this.pos);
        
        if (newWidth < MIN_WIDTH) {
            return;
        }
        
        this.region.setMinWidth(newWidth);
        this.region.setPrefWidth(newWidth);
        
        if (Objects.nonNull(this.cells)) {
            this.cells.forEach(o -> {
                o.setMinWidth(newWidth);
                o.setPrefWidth(newWidth);
            });
        }
        
        this.pos = event.getX();
        
        if (!StringUtils.isEmpty(this.name)) {
            try {
                Properties properties = AdProperty.getProperties(Constants.DEFAULT_UI_PROPERTY_NAME);
                properties.setProperty(name, String.valueOf(newWidth));
                AdProperty.store(Constants.DEFAULT_UI_PROPERTY_NAME);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * マウスプレス
     * 
     * @param event MouseEvent
     */
    protected void mousePressed(MouseEvent event) {
        if (!this.isInDraggableZone(event)) {
            return;
        }
        this.dragging = true;
        if (!this.initialized) {
            this.region.setMinWidth(this.region.getWidth());
            this.initialized = true;
        }
        this.pos = event.getX();
    }
    
    /**
     * 初期化する。
     */
    protected void initialize() {
        this.cells.addListener((Change<? extends Region> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    this.restore();
                    break;
                }
            }
        });
        
        this.restore();
    }
    
    /**
     * 幅を戻す。
     */
    private void restore() {
        try {
            if (StringUtils.isEmpty(this.name)) {
                return;
            } 
            
            Properties properties = AdProperty.getProperties(Constants.DEFAULT_UI_PROPERTY_NAME);
            if (Objects.isNull(properties)) {
                AdProperty.load(Constants.DEFAULT_UI_PROPERTY_NAME, Constants.DEFAULT_UI_PROPERTY_NAME + ".properties");
                properties = AdProperty.getProperties(Constants.DEFAULT_UI_PROPERTY_NAME);
            }

            String value = properties.getProperty(name);

            if (!StringUtils.isEmpty(value)) {
                double width = Double.valueOf(value);
                this.region.setMinWidth(width);
                this.region.setPrefWidth(width);
                if (Objects.nonNull(this.cells)) {
                    this.cells.forEach(o -> {
                        o.setMinWidth(width);
                        o.setPrefWidth(width);
                    });
                }
            }

        } catch (Exception ex) {
        }        
    }
}

