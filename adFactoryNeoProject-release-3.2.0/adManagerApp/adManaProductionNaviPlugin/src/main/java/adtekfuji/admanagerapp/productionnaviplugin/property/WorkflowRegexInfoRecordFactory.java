/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.property;

import adtekfuji.locale.LocaleUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import jp.adtekfuji.adFactory.entity.importformat.WorkflowRegexInfo;
import jp.adtekfuji.javafxcommon.property.AbstractCell;
import jp.adtekfuji.javafxcommon.property.AbstractRecordFactory;
import jp.adtekfuji.javafxcommon.property.CellButton;
import jp.adtekfuji.javafxcommon.property.CellLabel;
import jp.adtekfuji.javafxcommon.property.CellTextField;
import jp.adtekfuji.javafxcommon.property.Record;
import jp.adtekfuji.javafxcommon.property.Table;

/**
 * 工程順 正規表現情報レコード生成クラス
 *
 * @author nar-nakamura
 */
public class WorkflowRegexInfoRecordFactory extends AbstractRecordFactory<WorkflowRegexInfo> {

    private String regexLabelText = null;
    private String workflowLabelText = null;

    /**
     * 選択ボタンのアクション
     */
    private EventHandler<ActionEvent> onActionEvent = (ActionEvent event) -> {
    };

    /**
     * コンストラクタ
     *
     * @param table
     * @param entities 
     * @param regexLabelText 
     * @param workflowLabelText 
     */
    public WorkflowRegexInfoRecordFactory(Table table, LinkedList<WorkflowRegexInfo> entities, String regexLabelText, String workflowLabelText) {
        super(table, entities);
        this.regexLabelText = regexLabelText;
        this.workflowLabelText = workflowLabelText;
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
     * 項目名レコードを追加する。
     *
     * @return 項目名レコード
     */
    @Override
    protected Record createCulomunTitleRecord() {
        Record titleColumnrecord = new Record(super.getTable(), false);
        List<AbstractCell> cells = new ArrayList();

        // 正規表現
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(this.regexLabelText)).addStyleClass("ContentTitleLabel"));
        // 工程順
        cells.add(new CellLabel(titleColumnrecord, new SimpleStringProperty(this.workflowLabelText)).addStyleClass("ContentTitleLabel"));

        titleColumnrecord.setTitleCells(cells);

        return titleColumnrecord;
    }

    /**
     * レコードを追加する。
     *
     * @param entity 正規表現情報
     * @return 工程順 正規表現情報レコード
     */
    @Override
    protected Record createRecord(WorkflowRegexInfo entity) {
        Record record = new Record(super.getTable(), true);
        record.setEditableOrder(true);

        LinkedList<AbstractCell> cells = new LinkedList<>();

        // 正規表現
        cells.add(new CellTextField(record, entity.regexProperty()).setPrefWidth(200.0).addStyleClass("ContentTextBox"));
        // 工程順
        cells.add(new CellTextField(record, entity.workflowDispNameProperty()).setPrefWidth(400.0).addStyleClass("ContentTextBox").setDisable(true));
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
        return WorkflowRegexInfo.class;
    }
}
