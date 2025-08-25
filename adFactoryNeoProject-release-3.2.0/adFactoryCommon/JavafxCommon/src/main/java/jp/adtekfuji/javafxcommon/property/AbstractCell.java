/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 *
 * @author e-mori
 */
public abstract class AbstractCell {

    private CellInterface cellInterface;
    private Node node;
    private final List<String> styles = new ArrayList<>();
    private boolean isDisable;
    private double width;
    private double minWidth;
    private double maxWidth;
    private double height;
    private Consumer<Node> nodeConsumer = null;
    private boolean isResize;

    /**
     * コンストラクタ
     *
     * @param cellInterface
     */
    protected AbstractCell(CellInterface cellInterface) {
        this.cellInterface = cellInterface;
        this.isDisable = false;
        this.width = Double.NaN;
        this.minWidth = Double.NaN;
        this.maxWidth = Double.NaN;
        this.height = Double.NaN;
    }

    /**
     * コンストラクタ
     *
     * @param cellInterface
     * @param isDisable セルを無効にするかどうか
     */
    protected AbstractCell(CellInterface cellInterface, boolean isDisable) {
        this.cellInterface = cellInterface;
        this.isDisable = isDisable;
        this.width = Double.NaN;
        this.minWidth = Double.NaN;
        this.maxWidth = Double.NaN;
        this.height = Double.NaN;
    }

    public abstract void createNode();

    public AbstractCell addStyleClass(String styleClass) {
        this.styles.add(styleClass);
        return this;
    }

    public void setNode(Node node) {

        if(Objects.isNull(node)) {
            return;
        }

        this.node = node;
        this.node.setDisable(this.isDisable);
        if (this.node instanceof Region) {
            Region region = (Region) this.node;
            if (!Double.isNaN(this.width)) {
                region.setPrefWidth(this.width);
                region.setMinWidth(this.width);
                region.setMaxWidth(this.width);
            }
            if (!Double.isNaN(this.minWidth)) {
                region.setMinWidth(this.minWidth);
            }
            if (!Double.isNaN(this.maxWidth)) {
                region.setMaxWidth(this.maxWidth);
            }
            if (!Double.isNaN(this.height)) {
                region.setPrefHeight(this.height);
            }
        }

        if(Objects.nonNull(nodeConsumer)) {
            nodeConsumer.accept(node);
        }

        node.getStyleClass().addAll(styles);
    }

    public Node getNode() {
        if (Objects.isNull(this.node)) {
            this.createNode();
        }
        return node;
    }

    public CellInterface getCellInterface() {
        return cellInterface;
    }

    public void setCellInterface(CellInterface cellInterface) {
        this.cellInterface = cellInterface;
    }

    /**
     * セルが無効かどうかを返す
     *
     * @return
     */
    public boolean isDisable() {
        return this.isDisable;
    }

    /**
     * セルを無効にするかどうかを設定する
     *
     * @param isDisable
     * @return
     */
    public AbstractCell setDisable(boolean isDisable) {
        this.isDisable = isDisable;
        if (Objects.nonNull(node)) {
            this.node.setDisable(isDisable);
        }
        return this;
    }

    /**
     * 幅を設定する。
     *
     * @param width
     * @return
     */
    public AbstractCell setPrefWidth(double width) {
        this.width = width;
        if (this.node instanceof Region) {
            ((Region) this.node).setPrefWidth(this.width);
        }
        return this;
    }
    
    /**
     * 最小幅を設定する。
     *
     * @param minWidth
     * @return
     */
    public AbstractCell setMinWidth(double minWidth) {
        this.minWidth = minWidth;
        if (this.node instanceof Region) {
            ((Region) this.node).setMinWidth(this.minWidth);
        }
        return this;
    }
    
    /**
     * 最大幅を設定する。
     *
     * @param maxWidth
     * @return
     */
    public AbstractCell setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
        if (this.node instanceof Region) {
            ((Region) this.node).setMaxWidth(this.maxWidth);
        }
        return this;
    }

    /**
     * 高さを設定する。
     *
     * @param height
     * @return
     */
    public AbstractCell setPrefHeight(double height) {
        this.height = height;
        if (this.node instanceof Region) {
            ((Region) this.node).setPrefHeight(this.height);
        }
        return this;
    }

    /**
     * ノードにプロパティへ設定関数を設定
     * @param nodeConsumer プロパティ設定関数を設定
     */
    public AbstractCell setNodeConsumer(Consumer<Node> nodeConsumer) {
        this.nodeConsumer = nodeConsumer;
        if(Objects.nonNull(nodeConsumer) && Objects.nonNull(node)) {
            nodeConsumer.accept(node);
        }
        return this;
    }

    /**
     * リサイズ可能かどうかを返す。
     * 
     * @return true: リサイズ可、false: リサイズ不可
     */
    public boolean isResize() {
        return isResize;
    }

    /**
     * リサイズ可能かどうかを設定する。
     * 
     * @param isResize true: リサイズ可、false: リサイズ不可
     */
    public AbstractCell setResize(boolean isResize) {
        this.isResize = isResize;
        return this;
    }

    
}
