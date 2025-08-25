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
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 列の順序と幅を記録するTreeTableView<br>
 *
 * 必ずinitを呼んで初期化すること。<br>
 *
 * <p>
 * 参考元：<a href="URL">https://gist.github.com/y4nnick/ca976e58be23aab20dfbc8d81ea46816</a>
 * </p>
 *
 * @param <T>
 * @author s-heya
 */
public class PropertySaveTreeTableView<T> extends TreeTableView<T> {

    private final Logger logger = LogManager.getLogger();

    //デフォルトのレイアウト設定ファイル
    private final String defaultPropertyName = "adManagerUI";

    private final String columnOrderLabel = "ColumnOrder";
    private final String columnWidthLabel = "ColumnWidth_";
    private final String columnVisibleLabel = "ColumnVisible";

    private List<TreeTableColumn<T, ?>> unchangedColumns;

    private String prefix;

    /**
     * TableViewを初期化し列の幅・順序を復元・保存する またレイアウト保存用の設定ファイルを作成する
     *
     * @param prefix 設定ファイルに保存するときの接頭辞
     */
    public void init(String prefix) {
        try {
            logger.info("init start.");

            this.prefix = prefix;

            AdProperty.load(this.defaultPropertyName, this.defaultPropertyName + ".properties");

            //列の順序
            this.unchangedColumns = Collections.unmodifiableList(new ArrayList<>(this.getColumns()));

            getColumns().addListener((ListChangeListener.Change<? extends TreeTableColumn<T, ?>> change) -> {
                while (change.next()) {
                    if (change.wasRemoved() || change.wasAdded()) {
                        ObservableList<TreeTableColumn<T, ?>> list = this.getColumns();
                        String order = "";
                        for (int i = 0; i < list.size(); ++i) {
                            int unchangedIndex = this.unchangedColumns.indexOf(list.get(i));
                            if (unchangedIndex >= 0) {
                                order += unchangedIndex + ((i == list.size() - 1) ? "" : ",");
                            }
                        }
                        storeProperty(this.prefix + this.columnOrderLabel, order);
                    }
                }
            });

            restoreColumn();

            //列の幅
            this.unchangedColumns.stream().forEach((column) -> {
                column.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
                    String columnLabel = column.getText().toUpperCase();
                    storeProperty(this.prefix + this.columnWidthLabel + columnLabel, newWidth + "");
                });
            });

            restoreWidth();

            restoreVisible();

            // 列の表示プロパティ
            this.unchangedColumns.stream().forEach((column) -> {
                column.visibleProperty().addListener((observableValue, oldWidth, newWidth) -> {
                    ObservableList<TreeTableColumn<T, ?>> list = this.getColumns();
                    String hideColumn = "";
                    for (int i = 0; i < list.size(); ++i) {
                        if (!list.get(i).isVisible()) {
                            int unchangedIndex = this.unchangedColumns.indexOf(list.get(i));
                            if (unchangedIndex >= 0) {
                                hideColumn += unchangedIndex + ((i == list.size() - 1) ? "" : ",");
                            }
                        }
                    }
                    storeProperty(this.prefix + this.columnVisibleLabel, hideColumn);
                });
            });
        
        } catch (IOException ex) {
            logger.fatal(ex, ex);

        } finally {
            logger.info("init end.");
        }
    }

    /**
     * 設定ファイルに一致するように列を並び替える<br>
     * <br>
     * 設定ファイルに一致しない場合次のように処理する<br>
     * ・設定ファイルに列が存在するがbaseに存在しない場合<br>
     * →存在しない列は追加しない<br>
     * ・baseに存在するが設定ファイルに存在しない場合<br>
     * →追加する<br>
     *
     * @param base 並び替えの元となる列
     * @return 並び替えた列
     */
    public List<TreeTableColumn<T, ?>> permutate(List<TreeTableColumn<T, ?>> base) {
        logger.info("permutate start.");
        List<TreeTableColumn<T, ?>> orderedColumns = new ArrayList<>();

        try {
            Properties properties = AdProperty.getProperties(this.defaultPropertyName);
            String prop = properties.getProperty(this.prefix + this.columnOrderLabel);
            if (Objects.nonNull(prop)) {
                // 設定ファイルの順序をパース
                List<Integer> values = Arrays.asList(prop.split(",")).stream()
                        .filter(v -> !v.isEmpty())
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());

                // 設定ファイルの順序数が多い(列が削除された)場合、その列は追加しない
                values.stream()
                        .filter(v -> v < base.size())
                        .map(base::get)
                        .forEach(orderedColumns::add);

                // 新しく列が追加された場合、追加した分を右に加える
                if (base.size() > orderedColumns.size()) {
                    List<TreeTableColumn<T, ?>> additionalColumns = base.stream()
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
     * 設定ファイルに保存した列の順序を復元する
     */
    private void restoreColumn() {
        try {
            logger.info("restoreColumn start.");

            List<TreeTableColumn<T, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<TreeTableColumn<T, ?>>(getColumns()));

            List<TreeTableColumn<T, ?>> orderedColumns = permutate(currentColumns);
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
     * 設定ファイルに保存した各列の幅を復元する
     */
    private void restoreWidth() {
        try {
            logger.info("restoreWidth start.");

            Properties properties = AdProperty.getProperties(this.defaultPropertyName);
            List<TreeTableColumn<T, ?>> currentColumns = Collections.unmodifiableList(new ArrayList<TreeTableColumn<T, ?>>(getColumns()));

            for (int i = 0; i < currentColumns.size(); i++) {

                TreeTableColumn<T, ?> column = currentColumns.get(i);

                String label = this.prefix + this.columnWidthLabel + column.getText().toUpperCase();
                String prop = properties.getProperty(label);

                if (Objects.nonNull(prop)) {
                    Double width = Double.valueOf(prop);
                    column.setPrefWidth(width);
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("restoreWidth end.");
        }
    }

    /**
     * 設定ファイルに一致するように列の表示プロパティを変更する
     * @param base このリストの列の表示非表示を変更する
     */
    public void hideColumn(List<TreeTableColumn<T, ?>> base) {
        logger.info("hideColumn start.");
        List<TreeTableColumn<T, ?>> columns = new ArrayList<>();

        try {
            Properties properties = AdProperty.getProperties(this.defaultPropertyName);
            String prop = properties.getProperty(this.prefix + this.columnVisibleLabel);
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
                
                // 非表示
                base.stream()
                        .filter(v -> columns.contains(v))
                        .forEach(v -> v.setVisible(false));

            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("hideColumn end.");
        }
    }
    
    /**
     * 設定ファイルに保存した列の表示プロパティを復元する
     */
    private void restoreVisible() {
        try {
            logger.info("restoreVisible start.");
            hideColumn(getColumns());

        } catch (Exception ex) {
            logger.fatal(ex, ex);
        } finally {
            logger.info("restoreVisible end.");
        }
    }
    
    /**
     * 列の順序と幅をプロパティに記録する
     *
     * @param key プロパティ名
     * @param value プロパティ値
     */
    private void storeProperty(String key, String value) {
        try {
            AdProperty.getProperties(this.defaultPropertyName).setProperty(key, value);
            AdProperty.store(this.defaultPropertyName);
        } catch (IOException ex) {
            logger.fatal(ex, ex);
        }
    }
}
