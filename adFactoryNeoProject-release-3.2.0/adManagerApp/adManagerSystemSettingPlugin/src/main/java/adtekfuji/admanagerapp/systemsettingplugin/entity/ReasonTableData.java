/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.master.ReasonInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ReasonTypeEnum;

/**
 * 理由テーブルデータクラス
 *
 * @author hato
 */
public class ReasonTableData {

    private ReasonInfoEntity reasonInfoEntity;

    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final StringProperty reasonNameProperty = new SimpleStringProperty();

    private boolean isAdded = false;
    private boolean isEdited = false;
    private boolean isDeleted = false;
    private Long reasonCategoryId;

    /**
     * コンストラクタ
     */
    public ReasonTableData() {
        this.reasonInfoEntity = new ReasonInfoEntity(ReasonTypeEnum.TYPE_DEFECT);
        this.reasonNameProperty.setValue("");
        this.isAdded = true;
    }

    /**
     * コンストラクタ
     */
    public ReasonTableData(ReasonInfoEntity entity) {
        this.reasonInfoEntity = entity;
        this.reasonNameProperty.setValue(entity.getReason());
    }

    /**
     * 追加対象かどうか
     *
     * @return TRUE=追加対象
     */
    public boolean isAdded() {
        return this.isAdded;
    }

    /**
     * 追加対象か設定する
     *
     * @param isAdded 設定値
     */
    public void setIsAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    /**
     * 更新対象かどうか
     *
     * @return TRUE=更新対象
     */
    public boolean isEdited() {
        return this.isEdited;
    }

    /**
     * 更新対象か設定する
     *
     * @param isEdited 設定値
     */
    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    /**
     * 削除対象かどうか
     *
     * @return TRUE=更新対象
     */
    public boolean isDeleted() {
        return this.isDeleted;
    }

    /**
     * 削除対象か設定する
     *
     * @param isDeleted 設定値
     */
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 選択プロパティ
     */
    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    /**
     * 選択されているか
     */
    public Boolean isSelected() {
        return this.selected.get();
    }

    /**
     * 選択されているか設定する
     *
     * @Param selected 設定値
     */
    public void setSelected(Boolean selected) {
        this.selected.set(selected);
    }

    /**
     * 理由プロパティ
     *
     * @return 理由プロパティ
     */
    public StringProperty reasonNameProperty() {
        return this.reasonNameProperty;
    }

    /**
     * 理由を取得する
     *
     * @return 理由
     */
    public String getReasonName() {
        return this.reasonNameProperty.get();
    }

    /**
     * 理由を取得する
     *
     * @param reasonName 理由
     */
    public void setReasonName(String reasonName) {
        if (!reasonName.equals(reasonNameProperty.get())) {
            this.reasonNameProperty.set(reasonName);
        }
    }

    /**
     * 理由情報エンティティを取得する
     *
     * @return 理由情報エンティティ
     */
    public ReasonInfoEntity getReasonInfoEntity() {
        return this.reasonInfoEntity;
    }

    /**
     * 理由情報エンティティを設定する
     *
     * @param reasonInfoEntity 理由情報エンティティ
     */
    public void setReasonInfoEntity(ReasonInfoEntity reasonInfoEntity) {
        this.reasonInfoEntity = reasonInfoEntity;
    }

    /**
     * 理由区分IDを取得する
     *
     * @return 理由区分ID
     */
    public Long getReasonCategoryId() {
        return reasonCategoryId;
    }

    /**
     * 理由区分IDを設定する
     *
     * @param reasonCategoryId 理由区分ID
     */
    public void setReasonCategoryId(Long reasonCategoryId) {
        this.reasonCategoryId = reasonCategoryId;
    }
}
