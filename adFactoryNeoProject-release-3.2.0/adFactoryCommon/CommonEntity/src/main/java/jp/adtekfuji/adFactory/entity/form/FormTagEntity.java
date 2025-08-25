package jp.adtekfuji.adFactory.entity.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledger_tag_list_entity")
public class FormTagEntity {

    public enum TagType {
        TEXT, // テキスト形式
        DATE, // 日付
        ADDITION, // 追加情報
    }

    static public class TagData {
        @XmlElement()
        TagType tagType; // タグタイプ

        @XmlElement()
        String text = null; // テキストデータ

        @XmlElement()
        Date date = null; // 日付データ

        @XmlElement()
        Long id = null; // idデータ

        /**
         * コンストラクタ
         */
        TagData(){}

        /**
         * コンストラクタ
         * @param text テキスト
         */
        TagData(String text) {
            this.tagType = TagType.TEXT;
            this.text = text;
        }

        /**
         * コンストラクタ
         * @param date 日付
         */
        TagData(Date date) {
            this.tagType = TagType.DATE;
            this.date = date;
        }

        /**
         * コンストラクタ
         * @param id ID
         */
        TagData(Long id) {
            this.tagType = TagType.ADDITION;
            this.id = id;
        }

        /**
         * タグタイプを取得
         * @return タグタイプ
         */
        public TagType getTagType() {
            return tagType;
        }

        /**
         * テキスト取得
         * @return テキスト
         */
        public String getText() {
            return text;
        }

        /**
         * 日付取得
         * @return 日付
         */
        public Date getDate() {
            return date;
        }

        /**
         * ID取得
         * @return ID
         */
        public Long getId() {
            return id;
        }
    }

    @XmlElement()
    @JsonProperty("tag_map")
    Map<String, TagData> tagMap = new HashMap<>(); //タグマップ

    // コンストラクタ
    public FormTagEntity() {

    }

    /**
     * テキスト設定
     * @param key タグ名
     * @param val 値
     */
    public void put(String key, String val) {
        tagMap.put(key, new TagData(val));
    }

    /**
     * 日付設定
     * @param key タグ名
     * @param val 値
     */
    public void put(String key, Date val) {
        tagMap.put(key, new TagData(val));
    }

    /**
     * ID設定
     * @param key タグ名
     * @param val 値
     */
    public void put(String key, Long val) {
        tagMap.put(key, new TagData(val));
    }

    /**
     * タグマップ取得
     * @return タグマップ
     */
    public Map<String, TagData> getTagMap() {
        if (tagMap.isEmpty()) { return new HashMap<>(); }
        return tagMap;
    }

}
