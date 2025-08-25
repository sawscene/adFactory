/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.workfloweditplugin.property;

import adtekfuji.locale.LocaleUtils;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellButton;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * ファイルパス設定レコード生成クラス
 *
 * @author nar-nakamura
 */
public class FilePathRecordFactory extends AbstractRecordFactory<SimpleStringProperty> {

    private final ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 選択ボタンのアクション
     */
    private EventHandler<ActionEvent> onActionEvent = (ActionEvent event) -> {
    };

    /**
     * ファイルパス設定レコード生成クラス
     *
     * @param table ファイルパス設定テーブル
     * @param entities ファイルパスのリスト
     */
    public FilePathRecordFactory(Table table, LinkedList<SimpleStringProperty> entities) {
        super(table, entities);
    }

    /**
     * 選択ボタンのアクションイベントリスナーを設定する。
     *
     * @param onActionEvent 
     */
    public void setOnActionEventListener(EventHandler<ActionEvent> onActionEvent) {
        this.onActionEvent = onActionEvent;
    }

    /**
     * ファイルパス設定テーブルに、指定したファイルパスのレコードを追加する。
     *
     * @param entity ファイルパス
     * @return ファイルパス設定レコード
     */
    @Override
    protected Record createRecord(SimpleStringProperty entity) {
        Record record = new Record(super.getTable(), true);
        LinkedList<AbstractCell> cells = new LinkedList<>();

        // テキスト入力欄
        cells.add(new CellTextField(record, entity).setPrefWidth(480.0).addStyleClass("ContentTextBox"));
        // 選択ボタン
        cells.add(new CellButton(record, new SimpleStringProperty(LocaleUtils.getString("key.Choice")), onActionEvent, entity).addStyleClass("ContentTextBox"));

        record.setCells(cells);

        return record;
    }

    /**
     * エンティティの型を取得する。
     *
     * @return エンティティの型
     */
    @Override
    public Class getEntityClass() {
        return SimpleStringProperty.class;
    }
}
