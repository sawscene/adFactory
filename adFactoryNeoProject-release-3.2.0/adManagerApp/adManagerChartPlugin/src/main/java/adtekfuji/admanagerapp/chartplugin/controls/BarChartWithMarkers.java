/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * マーカー付きBarChart
 *
 * @param <X>
 * @param <Y>
 * @author s-heya
 */
public class BarChartWithMarkers<X,Y> extends BarChart<X,Y> {

    private final ObservableList<Data<X, Y>> horizontalMarkers;
    private final Map<Node, Text> nodeMap;

    /**
     * コンストラクタ
     *
     * @param xAxis
     * @param yAxis
     */
    public BarChartWithMarkers(@NamedArg("xAxis") Axis xAxis, @NamedArg("yAxis") Axis yAxis) {
        super(xAxis, yAxis);
        this.horizontalMarkers = FXCollections.observableArrayList();
        this.horizontalMarkers.addListener((InvalidationListener)observable -> layoutPlotChildren());
        this.nodeMap = new HashMap<>();
    }

    /**
     * マーカーを追加する。
     *
     * @param marker
     * @param title
     */
    public void addMarker(Data<X, Y> marker, String title) {
        Objects.requireNonNull(marker, "The marker must not be null.");

        if (this.horizontalMarkers.contains(marker)) {
            return;
        }

        Line line = new Line();
        line.setStroke(Color.RED);
        line.setStyle("-fx-stroke-width: 3px;");
        marker.setNode(line);
        this.getPlotChildren().add(line);

        Text text = new Text(title);
        text.setStyle("-fx-fill: Red;");
        this.nodeMap.put(marker.getNode(), text);
        //this.getPlotChildren().add(text);

        this.horizontalMarkers.add(marker);
    }

    /**
     * マーカーを削除する。
     *
     * @param marker
     */
    public void removeMarker(Data<X, Y> marker) {
        Objects.requireNonNull(marker, "The marker must not be null.");

        if (!this.horizontalMarkers.contains(marker)) {
            return;
        }

        Node node = marker.getNode();
        if (Objects.nonNull(node)) {
            if (this.nodeMap.containsKey(node)) {
                Text text = this.nodeMap.get(node);
                this.getPlotChildren().remove(text);
                this.nodeMap.remove(node);
            }

            this.getPlotChildren().remove(node);
            marker.setNode(null);
        }

        this.horizontalMarkers.remove(marker);
    }

    /**
     * レイアウトを更新する。
     */
    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        for (Data<X, Y> horizontalMarker : this.horizontalMarkers) {
            Line line = (Line) horizontalMarker.getNode();
            line.setStartX(0);
            line.setEndX(getBoundsInLocal().getWidth());
            line.setStartY(getYAxis().getDisplayPosition(horizontalMarker.getYValue()) + 0.5);
            line.setEndY(line.getStartY());
            line.toFront();

            //Node text = this.nodeMap.get(line);
            //text.relocate(line.getBoundsInParent().getMinX() + line.getBoundsInParent().getWidth() / 2 - text.prefWidth(-1) / 2, line.getBoundsInParent().getMinY() - 20);
            //text.toFront();
        }
    }

    /**
     * マーカーの表示非表示を結びつける
     *
     * @param observable
     */
    public void bindMarkerVisible(ObservableValue<Boolean> observable) {
        this.nodeMap.entrySet().stream()
                .forEach(m -> {
                    m.getKey().visibleProperty().bind(observable);
                    m.getValue().visibleProperty().bind(observable);
                });
    }
}
