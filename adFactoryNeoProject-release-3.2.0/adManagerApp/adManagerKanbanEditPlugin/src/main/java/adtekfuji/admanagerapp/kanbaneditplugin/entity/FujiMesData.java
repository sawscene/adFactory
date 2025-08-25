/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;


/**
 * 検査データの検査値情報 出力用データ。
 * @author okada
 */
public class FujiMesData {

    /** タグの種類 */
    public enum TagType {
        /** 測定値 */
        MEASURED_VALUE("", false),
        /** 完了判定 */
        COMPLETION_JUDGMENT("_OK$", true),
        /** カスタム */
        CUSTOM("_CUSTOM$|_CUSTOM_\\d*$", true),
        /** 部品 */
        PARTS("_SERIAL$", true),
        /** 測定機器 */
        MEASUREMENT("_EQUIPMENT$", true),
        /** 測定者 */
        MEASURER("TAG_ED(", false);
        
        private final String patternText;
        private final boolean suffix;
        private Pattern pattern;

        /**
         * コンストラクタ
         * 
         * @param patternText
         * @param suffix 
         */
        private TagType(String patternText, boolean suffix) {
            this.patternText = patternText;
            this.suffix = suffix;

            try {
                this.pattern = StringUtils.isEmpty(patternText) ? null : Pattern.compile(patternText);
            } catch (Exception ex) {
                System.out.print(ex);
            }
        }

        /**
         * タグの種類を判定する文字を取得。
         * 
         * @return タグの種類を判定する文字パターン
         */
        public String getPatternText() {
            return this.patternText;
        }

        /**
         * タグの種類を判定する文字が最後なのかを判定するフラグを取得。
         * 
         * @return true:文字の最後に付与されている。 / false:文字の最初に付与に付与されている。
         */
        public boolean isSuffix() {
            return this.suffix;
        }
        
        /**
         * 正規表現のパターンを取得する。
         * 
         * @return 正規表現のパターン
         */
        public Pattern getPattern() {
            return this.pattern;
        }      
    }
    
    /** 区分ID */
    private String categoryId;
    /** 区分IDに紐づくタグ名 */
    private String tagName;
    /** タグの種類 */
    private TagType tagType;
    /** 測定値 */
    private String measuredValue;
    /** 基準値下限 */
    private Double workPropLowerTolerance;
    /** 基準値上限 */
    private Double workPropUpperTolerance;
    /** 入力規則 */
    private String workPropValidationRule;
    // 完了チェック
    private boolean okCheck;
    // ユニークタグ
    private String uniqueTag;
    // 値の検証を実施する
    private boolean isVerify;
    
    /**
     * 検査データの検査値情報。
     *
     */
    public FujiMesData() {
        this.initialize("", "");
    }

    /**
     * 検査データ 出力用データ。
     * 
     * @param categoryId 区分ID
     * @param tagName    区分IDに紐づくタグ名
     */
    public FujiMesData(String categoryId, String tagName) {
        this.initialize(categoryId, tagName);
    }

    /**
     * 検査データ 出力用データ。
     * 
     * @param categoryId 区分ID
     * @param tagName    区分IDに紐づくタグ
     */
    private void initialize(String categoryId, String tagName) {
        this.categoryId = categoryId;
        this.tagName = tagName;
        this.tagType = TagType.MEASURED_VALUE;
        this.measuredValue = "";
        this.workPropLowerTolerance = Double.NaN;
        this.workPropUpperTolerance = Double.NaN;
        this.workPropValidationRule = "";
        this.okCheck = false;
        this.isVerify = true;
        
        // タグ名が未設定の場合
        if (adtekfuji.utility.StringUtils.isEmpty(tagName)) {
            return;
        }

        this.uniqueTag = tagName;
        
        String pattern;
        for (TagType tag : TagType.values()) {
            pattern = tag.getPatternText();
           
            // 判定する文字が未設定の場合
            if (adtekfuji.utility.StringUtils.isEmpty(pattern)) {
                continue;
            }
            
            // タグ種類の判定(タグ名の最後に付与されるパターン)
            if (tag.isSuffix()) {
                Matcher matcher = tag.getPattern().matcher(tagName);
                if (matcher.find()){
                    this.uniqueTag = tagName.substring(0, matcher.start());
                    this.tagType = tag;
                    break;
                }
            }

            // タグ種類の判定(タグ名の最初に付与されるパターン)
            if (!tag.isSuffix() && tagName.length() >= pattern.length() &&
                    tagName.substring(0, pattern.length()).equals(pattern)) {
                this.tagType = tag;
                break;
            }
        }
   }
    
    /**
     * 区分IDを取得する。
     *
     * @return
     */
    public String getCategoryId() {
        return this.categoryId;
    }

    /** 
     * 区分IDを設定する。
     * 
     * @param value
     */
    public void setCategoryId(String value) {
        this.categoryId = value;
    }

    /**
     * 区分IDに紐づくタグ名を取得する
     *
     * @return
     */
    public String getTagName() {
        return this.tagName;
    }

    /** 
     * 区分IDに紐づくタグ名を設定する。
     * 
     * @param value
     */
    public void setTagName(String value) {
        this.tagName = value;
    }

    /**
     * タグの種類を取得する
     *
     * @return
     */
    public TagType getTagType() {
        return this.tagType;
    }

    /** 
     * タグの種類を設定する。
     * 
     * @param value
     */
    public void setTagType(TagType value) {
        this.tagType = value;
    }
    /**
     * 測定値を取得する。
     * 
     * @return
     */
    public String getMeasuredValue() {
        return this.measuredValue;
    }

    /**
     * 測定値を設定する。
     * 
     * @param value
     */
    public void setMeasuredValue(String value) {
        this.measuredValue = value;
    }

    /**
     * 基準値下限を取得する。
     * ※Double.isNaN
     *
     * @return 基準値下限(未定義の場合;NaN)
     */
    public Double getWorkPropLowerTolerance() {
        return this.workPropLowerTolerance;
    }

    /**
     * 基準値下限を設定する。
     *
     * @param workPropLowerTolerance 基準値下限
     */
    public void setWorkPropLowerTolerance(Double workPropLowerTolerance) {
        this.workPropLowerTolerance = workPropLowerTolerance;
    }

    /**
     * 基準値上限を取得する。
     * ※Double.isNaN
     *
     * @return 基準値上限(未定義の場合;NaN)
     */
    public Double getWorkPropUpperTolerance() {
        return this.workPropUpperTolerance;
    }

    /**
     * 基準値上限を設定する。
     *
     * @param workPropUpperTolerance 基準値上限
     */
    public void setWorkPropUpperTolerance(Double workPropUpperTolerance) {
        this.workPropUpperTolerance = workPropUpperTolerance;
    }

    /**
     * 入力規則を取得する。
     *
     * @return 入力規則
     */
    public String getWorkPropValidationRule() {
        return workPropValidationRule;
    }

    /**
     * 入力規則を設定する。
     *
     * @param workPropValidationRule 入力規則
     */
    public void setWorkPropValidationRule(String workPropValidationRule) {
        this.workPropValidationRule = workPropValidationRule;
    }

    /**
     * 終了フラグを取得する。
     *
     * @return
     */
    public boolean isOkCheck() {
        return this.okCheck;
    }

    /**
     * 終了フラグを設定する。
     *
     * @param value
     */
    public void setOkCheck(boolean value) {
        this.okCheck = value;
    }

    /**
     * ユニークタグを取得する。
     * 
     * @return ユニークタグ
     */
    public String getUniqueTag() {
        return this.uniqueTag;
    }

    /**
     * ユニークタグを設定する。
     * 
     * @param uniqueTag ユニークタグ
     */
    public void setUniqueTag(String uniqueTag) {
        this.uniqueTag = uniqueTag;
    }

    /**
     * 検査データファイルに出力する値を取得する。(一部編集有)
     * 
     * @return 検査データファイルに出力する値
     */
    public String getFileOutValue() {
        // タグの種類が完了判定の場合、編集が発生
        if (TagType.COMPLETION_JUDGMENT == this.tagType) {
            if ("0".equals(this.measuredValue)) {
                return "false";
            } else if ("1".equals(this.measuredValue)) {
                return "true";
            }
        }
        return this.measuredValue;
    }

    /**
     * 値の検証を実施するかを返す。
     * 
     * @return 
     */
    public boolean isVerify() {
        return isVerify;
    }

    /**
     *  値の検証を実施するどうかを設定する。
     * 
     * @param isVerify 
     */
    public void setVerify(boolean isVerify) {
        this.isVerify = isVerify;
    }
  
    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "CheckValueInfoEntity{" +
                "categoryId=" + this.categoryId +
                ", tagName=" + this.tagName +
                ", tagType=" + this.tagType +
                ", measuredValue=" + this.measuredValue +
                ", workPropLowerTolerance=" + this.workPropLowerTolerance +
                ", workPropUpperTolerance=" + this.workPropUpperTolerance +
                ", workPropValidationRule=" + this.workPropValidationRule +
                ", okCheck=" + this.okCheck +
                ", uniqueTag=" + this.uniqueTag +
                "}";
    }
}
