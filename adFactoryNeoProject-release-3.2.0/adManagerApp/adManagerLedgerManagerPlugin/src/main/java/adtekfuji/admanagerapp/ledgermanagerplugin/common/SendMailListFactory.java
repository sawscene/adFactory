/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.ledgermanagerplugin.common;

import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.utility.StringUtils;
import adtekfuji.clientservice.ClientServiceProperty;
import java.util.*;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigElementEntity;
import jp.adtekfuji.adFactory.entity.summaryreport.SummaryReportConfigInfoEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.javafxcommon.controls.RestrictedTextField;
import jp.adtekfuji.javafxcommon.property.*;
import jp.adtekfuji.javafxcommon.selectcompo.SelectDialogEntity;
import jp.adtekfuji.javafxcommon.utils.CacheUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jp.adtekfuji.javafxcommon.property.Record;

/**
 * 送付先項目のファクトリークラス
 *
 * @author s-heya
 */
public class SendMailListFactory extends AbstractRecordFactory<SummaryReportConfigInfoEntity> {

    private final SceneContiner sc = SceneContiner.getInstance();

    /**
     * 承認機能オプションが有効か
     */
    private final boolean isLicensedApproval = ClientServiceProperty.isLicensed(LicenseOptionType.ApprovalOption.getName());
    private final Logger logger = LogManager.getLogger();
    private WorkInfoEntity workInfo;

    /**
     * コンストラクタ
     *
     * @param table サマリーレポート設定情報
     * @param entity サマリーレポート設定情報
     */
    public SendMailListFactory(Table<SummaryReportConfigInfoEntity> table, SummaryReportConfigInfoEntity entity){
        super(table, new LinkedList<>(Collections.singletonList(entity)));

    }

    /**
     * メールリストを配列に編集
     * 
     * @param mailList メールリスト
     * @return メールリストの配列
     */
    String createMailDisplayText(List<String> mailList)
    {
        if(Objects.isNull(mailList)
        || mailList.isEmpty()
        || StringUtils.isEmpty(mailList.get(0))) {
            return "";
        }

        int num;
        final int maxSize = 15;
        StringBuilder txt = new StringBuilder(mailList.get(0));
        if (txt.length() > maxSize) {
            txt = new StringBuilder(txt.substring(0, maxSize) + "...");
            num = mailList.size() - 1;
        } else {
            int n = 1;
            String sep = ",";
            for (; n < mailList.size(); ++n) {
                if (txt.length() + mailList.get(n).length() > maxSize) {
                    break;
                }
                txt.append(sep).append(mailList.get(n));
            }
            num = mailList.size() - n;
        }

        if (num>0) {
            txt.append(" (+").append(num).append(")");
        }
        return txt.toString();
    }

    /**
     * 編集行を生成する
     *
     * @param entity 紐づけるエンティティ
     * @return 編集行をセットしたRecord
     */
    @Override
    protected Record createRecord(SummaryReportConfigInfoEntity entity) {
        Record record = new Record(super.getTable(), false);

        LinkedList<AbstractCell> cells = new LinkedList<>();

        cells.add(new CellLabel(record, LocaleUtils.getString("key.Addressee")+ LocaleUtils.getString("key.RequiredMark")).addStyleClass("ContentTitleLabel"));
        List<String> mailList = CacheUtils.getCacheOrganization(entity.getMails())
                .stream()
                .map(OrganizationInfoEntity::getOrganizationName)
                .collect(Collectors.toList());
        String txt = createMailDisplayText(mailList);

        CellTextField textCell = new CellTextField(record, new SimpleStringProperty(txt), true);
        textCell.addStyleClass("ContentTextBox");
        cells.add(textCell);
        cells.add(new CellButton(record, new SimpleStringProperty(LocaleUtils.getString("key.Choice")), createAction(textCell, entity), null).addStyleClass("ContentTextBox"));

        record.setCells(cells);

        return record;
    }

    /**
     * クラス情報を取得する
     *
     * @return 自身のClass オブジェクト
     */
    @Override
    public Class getEntityClass() {
        return SummaryReportConfigElementEntity.class;
    }

    /**
     * 選択ボタンのアクション
     * 
     * @param textCell 値を表示するコントロール
     * @param entity 紐づけるエンティティ
     * @return EventHandlerクラス
     */
    EventHandler createAction(CellTextField textCell, SummaryReportConfigInfoEntity entity) {
        return (EventHandler) (Event event) -> {
            //
            //        Button cellButton = (Button) event.getSource();
            //        WorkEquipmentSetting workEquip = (WorkEquipmentSetting) cellButton.getUserData();
           List<OrganizationInfoEntity> selectOrganizations = CacheUtils.getCacheOrganization(entity.getMails());

            //

//            String hoge = ((RestrictedTextField) (textCell.getNode())).getText();

            SelectDialogEntity<OrganizationInfoEntity> selectDialogEntity = new SelectDialogEntity<>();
            selectDialogEntity.organizations(selectOrganizations);
            ButtonType ret = sc.showComponentDialog(LocaleUtils.getString("key.Organization"), "OrganizationSelectionCompo", selectDialogEntity);

            if (ret.equals(ButtonType.OK)) {
                //            List<Long> equipIds = new ArrayList<>();
                //            for (EquipmentInfoEntity equip : selectDialogEntity.getEquipments()) {
                //                equipIds.add(equip.getEquipmentId());
                //            }
                //            workEquip.setEquipmentIds(equipIds);
                //            cellButton.setText(getEquipmentsName(equipIds));
                List<String> mailNameList = selectDialogEntity.getOrganizations()
                        .stream()
                        .map(OrganizationInfoEntity::getOrganizationName)
                        .collect(Collectors.toList());
                String txt = createMailDisplayText(mailNameList);

                ((RestrictedTextField) (textCell.getNode())).setText(txt);

                List<Long> mailList = selectDialogEntity.getOrganizations()
                        .stream()
                        .map(OrganizationInfoEntity::getOrganizationId)
                        .collect(Collectors.toList());
                entity.setMails(mailList);
            }

        };
    }

}
