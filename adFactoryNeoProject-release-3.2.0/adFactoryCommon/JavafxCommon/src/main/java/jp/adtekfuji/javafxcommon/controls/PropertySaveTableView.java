/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.javafxcommon.controls;

import adtekfuji.property.AdProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * テーブルビュー
 * 
 * テーブルビューのカラムの順序と幅を保持する。
 * init()を呼び出して、初期化すること。
 *
 * 参考: https://gist.github.com/y4nnick/ca976e58be23aab20dfbc8d81ea46816
 *
 * @param <T> オブジェクトタイプ
 * @author fu-kato
 */
public class PropertySaveTableView<T> extends TableView<T> {

    private final Logger logger = LogManager.getLogger();
    private final String propId = "adManagerUI";                // プロパティID
    private final String columnOrderLabel = "ColumnOrder";
    private final String columnWidthLabel = "ColumnWidth_";
    private final String columnVisibleLabel = "ColumnVisible";
    private List<TableColumn<T, ?>> unchangedColumns;
    private Properties properties;
    private String name;

    /**
     * テーブルビューを初期化する。
     *
     * @param name テーブルビュー名
     */
    public void init(String name) {
        try {
            if (!AdProperty.contains(propId)) {
                AdProperty.load(this.propId, this.propId + ".properties");
            }

            init(name, AdProperty.getProperties(this.propId));
        } catch (IOException ex2) {
            logger.fatal(ex2, ex2);
        }          
    }
    
    /**
     * テーブルビューを初期化する。
     *
     * @param name テーブルビュー名
     * @param properties プロパティ
     */
    public void init(String name, Properties properties) {
        logger.info("init start.");

        this.name = name;
        this.properties = properties;

        // カラムの順序
        //this.unchangedColumns = Collections.unmodifiableList(new ArrayList<>(getColumns()));
        this.unchangedColumns = new LinkedList<>(getColumns());

        getColumns().addListener((ListChangeListener.Change<? extends TableColumn<T, ?>> change) -> {
            while (change.next()) {
                if (change.wasRemoved() || change.wasAdded()) {
                    ObservableList<TableColumn<T, ?>> columns1 = getColumns();

                    if (change.wasRemoved()) {
                        LinkedHashSet<TableColumn<T, ?>> removedHashSet = new LinkedHashSet(this.unchangedColumns);   
                        for (TableColumn<T, ?> target : this.unchangedColumns) {
                            if (columns1.indexOf(target) < 0) {
                                // unchangedColumnsのみに存在するカラムを削除
                                removedHashSet.remove(target);
                            }
                        }
                        this.unchangedColumns = new LinkedList(removedHashSet);
                    }

                    StringBuilder order = new StringBuilder("");
                    for (int i = 0; i < columns1.size(); ++i) {
                        int unchangedIndex = this.unchangedColumns.indexOf(columns1.get(i));
                        if (unchangedIndex < 0) {
                            // カラムを最後尾に追加
                            this.unchangedColumns.add(columns1.get(i));
                            unchangedIndex = this.unchangedColumns.size();
                        }
                        order.append(unchangedIndex).append(",");
                    }
                    if (order.length() > 0) {
                        order.setLength(order.length() - 1);
                    }
                    storeProperty(this.name + this.columnOrderLabel, order.toString());
                }
            }
        });

        restoreColumn();

        // カラムの幅
        this.unchangedColumns.stream().forEach((column) -> {
            column.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
                if (oldWidth.equals(newWidth)) {
                    return;
                }
                String columnLabel = column.getText().toUpperCase();
                storeProperty(this.name + this.columnWidthLabel + columnLabel, newWidth + "");
            });
        });

        restoreWidth();
        
        restoreVisible();
        
        // カラムの表示プロパティ
        this.unchangedColumns.stream().forEach((column) -> {
            column.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
                if (oldValue.equals(newValue)) {
                    return;
                }
                ObservableList<TableColumn<T, ?>> columns1 = getColumns();
                StringBuilder hideColumn = new StringBuilder("");
                for (int i = 0; i < columns1.size(); ++i) {
                    if (!columns1.get(i).isVisible()) {
                        int unchangedIndex = this.unchangedColumns.indexOf(columns1.get(i));
                        if (unchangedIndex >= 0) {
                            hideColumn.append(unchangedIndex).append(",");
                        }
                    }
                }
                if (hideColumn.length() > 0) {
                    hideColumn.setLength(hideColumn.length() - 1);
                }
                storeProperty(this.name + this.columnVisibleLabel, hideColumn.toString());
            });
        });
        
        logger.info("init end.");
    }

    /**
     * カラムを並び替える。
     * 
     * カラムが設定ファイルには存在するがbaseに存在しない場合、カラムを表示しない。
     * カラムがbaseに存在するが設定ファイルに存在しない場合、カラムを表示する。
     *
     * @param base 対象のカラム
     * @return カラム
     */
    public List<TableColumn<T, ?>> permutate(List<TableColumn<T, ?>> base) {
        logger.info("permutate start.");
        List<TableColumn<T, ?>> orderedColumns = new ArrayList<>();

        try {
            String prop = this.properties.getProperty(this.name + this.columnOrderLabel);
            if (Objects.nonNull(prop)) {
                // 設定ファイルの順序をパース
                List<Integer> values = Arrays.asList(prop.split(",")).stream()
                        .filter(v -> !v.isEmpty())
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());

                // 設定ファイルの順序数が多い(カラムが削除された)場合、そのカラムは追加しない
                values.stream()
                        .filter(v -> v < base.size())
                        .map(base::get)
                        .forEach(orderedColumns::add);

                // 新しくカラムが追加された場合、追加した分を右に加える
                if (base.size() > orderedColumns.size()) {
                    List<TableColumn<T, ?>> additionalColumns = base.stream()
                            .filter(c -> !orderedColumns.contains(c))
                            .collect(Collectors.toList());

                    orderedColumns.addAll(additionalColumns);
                }
            } else {
                orderedColumns.addAll(base);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("permutate end.");
        }

        return orderedColumns;
    }

    /**
     * カラムの順序を復元する。
     */
    public void restoreColumn() {
        try {
            logger.info("restoreColumn start.");

            List<TableColumn<T, ?>> orderedColumns = permutate(this.unchangedColumns);
            if (orderedColumns.size() > 0) {
                getColumns().setAll(orderedColumns);
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("restoreColumn end.");
        }
    }

    /**
     * カラムの幅を復元する。
     */
    public void restoreWidth() {
        try {
            logger.info("restoreWidth start.");

            List<TableColumn<T, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<>(getColumns()));

            for (int i = 0; i < currentColumns.size(); i++) {

                TableColumn<T, ?> column = currentColumns.get(i);

                String label = this.name + this.columnWidthLabel + column.getText().toUpperCase();
                String prop = this.properties.getProperty(label);

                if (Objects.nonNull(prop)) {
                    Double width = Double.valueOf(prop);
                    column.setPrefWidth(width);
                    Double max = column.getMaxWidth();
                    Double min = column.getMinWidth();
                    column.setMaxWidth(width);
                    column.setMaxWidth(max);
                    column.setMinWidth(width);
                    column.setMinWidth(min);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("restoreWidth end.");
        }
    }

    /**
     * カラムを非表示にする。
     * 
     * @param base 対象のカラム
     */
    public void hideColumn(List<TableColumn<T, ?>> base) {
        logger.info("hideColumn start.");
        List<TableColumn<T, ?>> columns = new ArrayList<>();

        try {
            String prop = this.properties.getProperty(this.name + this.columnVisibleLabel);
            if (Objects.nonNull(prop)) {
                // 設定ファイルをパース
                List<Integer> values = Arrays.asList(prop.split(",")).stream()
                        .filter(v -> !v.isEmpty())
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());

                // 非表示のリスト作成
                values.stream()
                        .filter(v ->  v < this.unchangedColumns.size())
                        .map(this.unchangedColumns::get)
                        .forEach(columns::add);
                
                // 表示プロパティを設定
                base.stream()
                        .filter(v -> v.isVisible() == columns.contains(v))
                        .forEach(v -> v.setVisible(!columns.contains(v)));

            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("hideColumn end.");
        }
    }
    
    /**
     * カラムの表示性を復元する。
     */
    public void restoreVisible() {
        try {
            logger.info("restoreVisible start.");
            hideColumn(this.unchangedColumns);

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("restoreVisible end.");
        }
    }
    
    /**
     * プロパティを保存する。
     *
     * @param key プロパティ名
     * @param value プロパティ値
     */
    private void storeProperty(String key, String value) {
        try {
            AdProperty.getProperties(this.propId).setProperty(key, value);
            AdProperty.store(this.propId);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
    
    /**
     * カラムの並び順を取得する。
     *
     * @return カラムの並び順
     */
    public String getColumnsOrder() {
         return this.properties.getProperty(this.name + this.columnOrderLabel);
    }
    
    /**
     * カラムの並び順を保存する。
     *
     * @param columnsOrder カラムの並び順
     */
    public void storeColumnsOrder(String columnsOrder) {
        this.storeProperty(this.name + this.columnOrderLabel, columnsOrder);
    }
    
    
    /**
     * カラムの幅を取得する。
     *
     * @return　Map<カラム, 幅>
     */
    public Map<String, String> getColumnsWidth() {
        Map<String, String> map = new HashMap<>();
        try {
            logger.info("getColumnsWidth start.");
            List<TableColumn<T, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<>(getColumns()));
        
            for (int i = 0; i < currentColumns.size(); i++) {
                TableColumn<T, ?> column = currentColumns.get(i);
                String label = this.name + this.columnWidthLabel + column.getText().toUpperCase();
                String prop = this.properties.getProperty(label);

                if (Objects.nonNull(prop)) {
                    map.put(label, prop);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("getColumnsWidth end.");
        }
        return map;
    }
    
    /**
     * カラムの幅を保存する。
     *
     * @param　map Map<カラム, 幅>
     */
    public void storeColumnsWidth(Map<String, String> map) {
        for (Entry<String, String> entry : map.entrySet()) {
            String columnLabel = entry.getKey();
            storeProperty(columnLabel, entry.getValue());
        }
    }
    
    /**
     * カラムの表示性を取得する。
     *
     * @return　true: 表示、false: 非表示
     */
    public String getColumnsVisible() {
        return this.properties.getProperty(this.name + this.columnVisibleLabel);
    }

    /**
     * カラムの表示性を保存する。
     *
     * @param columnsVisible 表示性
     */
    public void storeColumnsVisible(String columnsVisible) {
        this.storeProperty(this.name + this.columnVisibleLabel, columnsVisible);
    }

}
