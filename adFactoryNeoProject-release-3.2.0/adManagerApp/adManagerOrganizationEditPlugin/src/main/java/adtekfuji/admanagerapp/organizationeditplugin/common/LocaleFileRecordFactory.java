/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.organizationeditplugin.common;

import adtekfuji.locale.LocaleUtils;
import java.util.Arrays;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.FileChooser;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LocaleTypeEnum;
import jp.adtekfuji.javafxcommon.property.CellFileChooser;
import jp.adtekfuji.javafxcommon.property.CellLabel;

/**
 * 言語ファイルレコードファクトリ
 *
 * @author HN)y-harada
 */
public class LocaleFileRecordFactory extends AbstractRecordFactory<LocaleFileInfoEntity> {

    private final static ResourceBundle rb = LocaleUtils.getBundle("locale");
    private final static List<FileChooser.ExtensionFilter> filter = Arrays.asList(new FileChooser.ExtensionFilter("Properties Files", "*.properties"));

    /**
     * コンストラクタ
     *
     * @param table テーブル
     * @param entitys エンティティ
     */
    public LocaleFileRecordFactory(Table table, LinkedList<LocaleFileInfoEntity> entitys) {
        super(table, entitys);
    }

    @Override
    protected Record createCulomunTitleRecord() {
        return new Record(super.getTable(), false);
    }

    @Override
    protected Record createRecord(LocaleFileInfoEntity info) {
        try {
            Record record = new Record(super.getTable(), false);

            LinkedList<AbstractCell> cells = new LinkedList<>();
            // テキスト
            cells.add(new CellLabel(record, new SimpleStringProperty(LocaleTypeEnum.getMessage(rb, info.getLocaleType()))).addStyleClass("ContentTextBox"));
            // ファイルパス
            cells.add(new CellFileChooser(record, info.resource().resourceKeyProperty(), "ContentTextBox", "DeleteButton", "", LocaleFileRecordFactory.filter, true));

            record.setCells(cells);
        
            return record;

        } catch (Exception ex) {
            System.err.println(ex);
            return null;
        }
    }

    @Override
    public Class getEntityClass() {
        return ResourceInfoEntity.class;
    }
}
