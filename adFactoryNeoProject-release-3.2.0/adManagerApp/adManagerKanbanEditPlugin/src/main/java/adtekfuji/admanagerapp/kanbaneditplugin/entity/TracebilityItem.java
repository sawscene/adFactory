/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.enumerate.WorkPropertyCategoryEnum;

/**
 * 品質データ修正ダイアログ 表示用エンティティ
 *
 * @author y-harada
 */
public class TracebilityItem {

    private String name;                // 項目
    private WorkPropertyCategoryEnum WorkPropCategory;  // 種別
    private StringProperty nameProperty;
    private String tag;                 // タグ
    private StringProperty tagProperty;
    private String currentValue;        // 現在値
    private StringProperty currentValueProperty;
    private String inputValue;          // 修正値
    private StringProperty inputValueProperty;

    private Long actualId;
    private InputType inputType;
    private List<String> inputList;
    
    private int order;
    private boolean editable;

    /**
     * 入力形式
     */
    public static enum InputType {
        TEXTFIELD,
        TEXTFIELDTIME,
        TEXTAREA,
        CHECKBOX,
        COMBOBOX
    };

    /**
     * コンストラクタ
     */
    public TracebilityItem() {
    }

    /**
     * コンストラクタ
     *
     * @param name 項目名
     * @param tag タグ
     * @param value 現在値
     * @param actualId 実績ID
     * @param inputList 入力リスト
     * @param order 表示順
     * @param editable true:編集有効、false:編集無効
     */
    public TracebilityItem(String name,WorkPropertyCategoryEnum category, String tag, String value, Long actualId, List<String> inputList, int order, boolean editable) {
        this.name = name;
        this.WorkPropCategory = category;
        this.tag = tag;
        this.currentValue = value;
        this.inputValue = value;
        this.actualId = actualId;
        this.order = order;
        this.editable = editable;

        if (this.tag.endsWith("_OK")) {
            // チェックボックス
            this.inputType = InputType.CHECKBOX;
        } else if (this.tag.endsWith("_COMMENT")) {
            // テキストエリア
            this.inputType = InputType.TEXTAREA;
        } else if (this.tag.matches("TAG_TM(.*)")){
            // 日時のテキストエリア
            this.inputType = InputType.TEXTFIELDTIME;
        } else if (Objects.nonNull(inputList) && !inputList.isEmpty()) {
            // コンボボックス
            this.inputType = InputType.COMBOBOX;
            this.inputList = inputList;
        }else if (this.tag.equals(this.name) && this.WorkPropCategory.equals(WorkPropertyCategoryEnum.TIMESTAMP)){
            this.inputType = InputType.TEXTFIELDTIME;
        } else {
            // テキストフィールド
            this.inputType = InputType.TEXTFIELD;
        }
    }

    /**
     * 項目名プロパティを取得する。
     *
     * @return 項目名プロパティ
     */
    public StringProperty nameProperty() {
        if (Objects.isNull(this.nameProperty)) {
            this.nameProperty = new SimpleStringProperty(this.name);
        }
        return this.nameProperty;
    }

    /**
     * 項目名を取得する。
     *
     * @return 項目名
     */
    public String getName() {
        if (Objects.nonNull(this.nameProperty)) {
            return this.nameProperty.get();
        }
        return this.name;
    }

    /**
     * タグプロパティを取得する。
     *
     * @return タグプロパティ
     */
    public StringProperty tagProperty() {
        if (Objects.isNull(this.tagProperty)) {
            this.tagProperty = new SimpleStringProperty(this.tag);
        }
        return this.tagProperty;
    }

    /**
     * タグを取得する。
     *
     * @return タグ
     */
    public String getTag() {
        if (Objects.nonNull(this.tagProperty)) {
            return this.tagProperty.get();
        }
        return this.tag;
    }

    /**
     * 現在値を取得する。
     *
     * @return 現在値
     */
    public StringProperty currentValueProperty() {
        if (Objects.isNull(this.currentValueProperty)) {
            this.currentValueProperty = new SimpleStringProperty(this.currentValue);
        }
        return this.currentValueProperty;
    }

    /**
     * 修正値プロパティを取得する。
     *
     * @return 修正値
     */
    public StringProperty inputValueProperty() {
        if (Objects.isNull(this.inputValueProperty)) {
            this.inputValueProperty = new SimpleStringProperty(this.inputValue);
        }
        return this.inputValueProperty;
    }

    /**
     * 修正値を取得する。
     *
     * @return 修正値
     */
    public String getInputValue() {
        if (Objects.nonNull(this.inputValueProperty)) {
            return this.inputValueProperty.get();
        }
        return this.inputValue;
    }

    /**
     * 修正値を設定する。
     *
     * @param value 修正値
     */
    public void setInputValue(String value) {
        if (Objects.nonNull(this.inputValueProperty)) {
            this.inputValueProperty.set(value);
        } else {
            this.inputValue = value;
        }
    }

    /**
     * 表示タイプを取得する。
     *
     * @return 表示タイプ
     */
    public InputType getInputType() {
        return Objects.isNull(this.inputType) ? InputType.TEXTFIELD : this.inputType;
    }

    /**
     * 文字列リストを取得する。
     *
     * @return リスト
     */
    public List<String> getInputList() {
        if (Objects.isNull(this.inputList)) {
            this.inputList = new ArrayList<>();
        }
        return this.inputList;
    }

    /**
     * 実績IDを取得する。
     *
     * @return 実績ID
     */
    public Long getActualId() {
        return this.actualId;
    }

    /**
     * 値が変更されたかどうかを返す。
     *
     * @return true:変更済、false:未変更
     */
    public boolean isChanged() {
        boolean ret;
        if (Objects.nonNull(this.currentValueProperty) && Objects.nonNull(this.inputValueProperty)) {
            ret = !Objects.equals(this.currentValueProperty.getValue(), this.inputValueProperty.getValue());
        } else {
            ret = !this.currentValue.equals(this.inputValue);
        }
        return ret;
    }

    /**
     * 表示順を取得する。
     * 
     * @return 表示順
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * 編集有効性を取得する。
     * 
     * @return true:編集有効、false:編集無効
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * ハッシュ値を取得する。
     * 
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.tag);
        return hash;
    }

    /**
     * オブジェクトを比較する。
     * @param obj オブジェクト
     * @return true:一致、false:不一致
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TracebilityItem other = (TracebilityItem) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.tag, other.tag);
    }

    /**
     * 文字列表現を取得する。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "TracebilityItem{name=" + name + ", tag=" + tag + ", currentValue=" + currentValue + ", actualId=" + actualId + ", type=" + inputType + ", order=" + order + ", editable=" + editable + '}';
    }


}
