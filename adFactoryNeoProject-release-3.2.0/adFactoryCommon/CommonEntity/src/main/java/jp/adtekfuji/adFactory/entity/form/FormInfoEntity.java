package jp.adtekfuji.adFactory.entity.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ledger_info_entity")
public class FormInfoEntity {

    public enum FormCategoryEnum {
        WORKFLOW("workflow"); // 工程順に設定され帳票

        final String name;
        FormCategoryEnum(String name) {
            this.name = name;
        }

        /**
         * JSON生成時にLedgerCategoryEnumオブジェクトを文字列に変換する。
         */
        public static class Serializer extends JsonSerializer<FormCategoryEnum> {
            @Override
            public void serialize(FormCategoryEnum value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
                generator.writeString(value.name);
            }
        }

        /**
         * JSON解析時に文字列をLedgerCategoryEnumオブジェクトに変換する。
         */
        public static class Deserializer extends JsonDeserializer<FormCategoryEnum> {
            @Override
            public FormCategoryEnum deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                final String name = parser.getText();
                return Stream.of(FormCategoryEnum.values())
                        .filter(item -> item.name.equals(name))
                        .findFirst().orElse(null);
            }
        }
    }

    @XmlElement()
    @JsonSerialize(using = FormCategoryEnum.Serializer.class)
    @JsonDeserialize(using = FormCategoryEnum.Deserializer.class)
    FormCategoryEnum ledgerCategory; //帳票カテゴリー

    @XmlElement()
    @JsonProperty("id")
    Long id; // ID

    @XmlElement()
    @JsonProperty("name")
    String name; // 項目名

    @XmlElement()
    @JsonProperty("formPath")
    String formPath; // 帳票テンプレートパス

    @XmlElement()
    @JsonProperty("fromDate")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date fromDate; // 帳票開始日時

    @XmlElement()
    @JsonProperty("toDate")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Date toDate; // 帳票完了日時

    /**
     * コンストラクタ
     */
    public FormInfoEntity() {
    }

    /**
     * コンストラクタ
     * @param ledgerCategory カテゴリ
     * @param id ID
     * @param name 名称
     * @param formPath 帳票テンプレートパス
     */
    public FormInfoEntity(FormCategoryEnum ledgerCategory, Long id, String name, String formPath) {
        this.ledgerCategory = ledgerCategory;
        this.id = id;
        this.name = name;
        this.formPath = formPath;
    }


    /**
     * カテゴリ取得
     * @return カテゴリー
     */
    public FormCategoryEnum getLedgerCategory() {
        return ledgerCategory;
    }

    /**
     * カテゴリ設定
     * @param ledgerCategory カテゴリ
     */
    public void setLedgerCategory(FormCategoryEnum ledgerCategory) {
        this.ledgerCategory = ledgerCategory;
    }

    /**
     * ID取得
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * ID設定
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 項目名取得
     * @return 項目名
     */
    public String getName() {
        return name;
    }

    /**
     * 項目名設定
     * @param name 項目名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 帳票テンプレートパス取得
     * @return 帳票テンプレートパス
     */
    public String getFormPath() {
        return formPath;
    }

    /**
     * 帳票テンプレートパス設定
     * @param formPath 帳票テンプレートパス
     */
    public void setFormPath(String formPath) {
        this.formPath = formPath;
    }

    /**
     * 帳票開始日取得
     * @return 帳票開始日
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * 帳票開始日設定
     * @param fromDate 帳票開始日
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * 帳票完了日取得
     * @return 帳票完了日
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * 帳票完了日設定
     * @param toDate 帳票完了日
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "FormInfoEntity{" +
                "ledgerCategory=" + ledgerCategory +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", ledgerPath='" + formPath + '\'' +
                '}';
    }
}
