/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.common;

import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * 承認ルート一覧編集画面上下ボタン生成クラス
 *
 * @author shizuka.hirano
 * @param <S> オブジェクト
 */
public class ApprovalButtonTableCell<S> extends TableCell<S, Button> {

    /**
     * セルに配置されるボタン
     */
    private final Button actionButton;

    /**
     * コンストラクタ
     *
     * @param label　ラベル
     * @param function セル
     */
    public ApprovalButtonTableCell(String label, Function<S, S> function) {
        this.actionButton = new Button(label);
        this.setStyle("-fx-padding: 0;");
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        this.actionButton.setMaxWidth(Double.MAX_VALUE);
    }

    /**
     * テーブルビューの行データ取得
     *
     * @return 行データ
     */
    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    /**
     * テーブルカラム設定
     *
     * @param <S> テーブルデータクラス
     * @param label　ラベル
     * @param function　セル
     * @return　テーブルカラム
     */
    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(String label, Function< S, S> function) {
        return param -> new ApprovalButtonTableCell<>(label, function);
    }

    /**
     * ボタン表示更新
     *
     * @param item　ボタン
     * @param empty 空白確認(true:空白 false:表示])
     */
    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}
