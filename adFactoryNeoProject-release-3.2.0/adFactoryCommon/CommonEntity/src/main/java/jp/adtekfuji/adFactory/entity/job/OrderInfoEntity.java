/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 注番情報 (ELS)
 *
 * @author nar-nakamura
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrderInfoEntity implements Serializable {

    private StringProperty porderProperty;// 注番プロパティ
    private StringProperty hinmeiProperty;// 品名プロパティ
    private StringProperty kikakuKatasikiProperty;// 規格・型式プロパティ
    private StringProperty kbumoNameProperty;// 部門名プロパティ
    private StringProperty syanaiZubanProperty;// 図番プロパティ
    private StringProperty syanaiCommentProperty;// 社内コメントプロパティ
    private StringProperty kbanProperty;// 工程番号プロパティ
    private IntegerProperty kvolProperty;// 計画指示数プロパティ
    private IntegerProperty lvolProperty;// 指示数プロパティ
    private IntegerProperty defectProperty;// 不良数プロパティ
    private IntegerProperty remProperty;// 残り台数プロパティ

    @JsonProperty("PORDER")
    private String porder;// 注番

    @JsonProperty("HINMEI")
    private String hinmei;// 品名

    @JsonProperty("KIKAKU_KATASIKI")
    private String kikakuKatasiki;// 規格・型式

    @JsonProperty("KBUMONAME")
    private String kbumoName;// 部門名

    @JsonProperty("SYANAI_ZUBAN")
    private String syanaiZuban;// 図番

    @JsonProperty("SYANAI_COMMENT")
    private String syanaiComment;// 社内コメント

    @JsonProperty("KBAN")
    private String kban;// 工程番号

    @JsonProperty("KVOL")
    private Integer kvol;// 計画指示数

    @JsonProperty("LVOL")
    private Integer lvol;// 指示数

    @JsonProperty("DEFECT")
    private Integer defect;// 不良数

    @JsonProperty("REM")
    private Integer rem;// 残り台数

    @JsonProperty("SN")
    private List<String> sn;// シリアル番号

    @JsonProperty("WORKFLOW")
    private Long workflowId;// 工程順ID

    @JsonProperty("DEFECT_SERIALS")
    private Map<String, String> defectSerials;// 不良シリアル(シリアル番号, 不良理由)

    /**
     * コンストラクタ
     */
    public OrderInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param map 注番情報マップ
     */
    public OrderInfoEntity(Map<String, Object> map) {
        this.porder = (String) map.get("PORDER");
        this.hinmei = (String) map.get("HINMEI");
        this.kikakuKatasiki = (String) map.get("KIKAKU_KATASIKI");
        this.kbumoName = (String) map.get("KBUMONAME");
        this.syanaiZuban = (String) map.get("SYANAI_ZUBAN");
        this.syanaiComment = (String) map.get("SYANAI_COMMENT");
        this.kban = (String) map.get("KBAN");
        this.kvol = (Integer) map.get("KVOL");
        this.lvol = (Integer) map.get("LVOL");
        this.defect = (Integer) map.get("DEFECT");
        this.rem = (Integer) map.get("REM");
        this.sn = (List<String>) map.get("SN");

        // 工程順ID  ※.Object から Long へは直接キャストできないため、一旦 String に変換してから Long に変換する。
        if (Objects.isNull(map.get("WORKFLOW"))) {
            this.workflowId = null;
        } else {
            this.workflowId = Long.valueOf(String.valueOf(map.get("WORKFLOW")));
        }

        // 不良シリアル(シリアル番号, 不良理由)
        this.defectSerials = (Map<String, String>) map.get("DEFECT_SERIALS");
    }

    /**
     * コンストラクタ
     *
     * @param in 注番情報
     */
    public OrderInfoEntity(OrderInfoEntity in) {
        this.porder = in.getPorder();
        this.hinmei = in.getHinmei();
        this.kikakuKatasiki = in.getKikakuKatasiki();
        this.kbumoName = in.getKbumoName();
        this.syanaiZuban = in.getSyanaiZuban();
        this.syanaiComment = in.getSyanaiComment();
        this.kban = in.getKban();
        this.kvol = in.getKvol();
        this.lvol = in.getLvol();
        this.defect = in.getDefect();
        this.rem = in.getRem();

        if (Objects.nonNull(in.getSn())) {
            this.sn = new LinkedList();
            for (String serialNo : in.getSn()) {
                this.sn.add(serialNo);
            }
        }

        this.workflowId = in.getWorkflowId();

        if (Objects.nonNull(in.getDefectSerials())) {
            this.defectSerials = new LinkedHashMap();
            for (Map.Entry<String, String> defectSerial : in.getDefectSerials().entrySet()) {
                this.defectSerials.put(defectSerial.getKey(), defectSerial.getValue());
            }
        }
    }

    /**
     * 注番プロパティを取得する。
     *
     * @return 注番
     */
    public StringProperty porderProperty() {
        if (Objects.isNull(this.porderProperty)) {
            this.porderProperty = new SimpleStringProperty(this.porder);
        }
        return this.porderProperty;
    }

    /**
     * 品名プロパティを取得する。
     *
     * @return 品名
     */
    public StringProperty hinmeiProperty() {
        if (Objects.isNull(this.hinmeiProperty)) {
            this.hinmeiProperty = new SimpleStringProperty(this.hinmei);
        }
        return this.hinmeiProperty;
    }

    /**
     * 規格・型式プロパティを取得する。
     *
     * @return 規格・型式
     */
    public StringProperty kikakuKatasikiProperty() {
        if (Objects.isNull(this.kikakuKatasikiProperty)) {
            this.kikakuKatasikiProperty = new SimpleStringProperty(this.kikakuKatasiki);
        }
        return this.kikakuKatasikiProperty;
    }

    /**
     * 部門名プロパティを取得する。
     *
     * @return 部門名
     */
    public StringProperty kbumoNameProperty() {
        if (Objects.isNull(this.kbumoNameProperty)) {
            this.kbumoNameProperty = new SimpleStringProperty(this.kbumoName);
        }
        return this.kbumoNameProperty;
    }

    /**
     * 図番プロパティを取得する。
     *
     * @return 図番
     */
    public StringProperty syanaiZubanProperty() {
        if (Objects.isNull(this.syanaiZubanProperty)) {
            this.syanaiZubanProperty = new SimpleStringProperty(this.syanaiZuban);
        }
        return this.syanaiZubanProperty;
    }

    /**
     * 社内コメントプロパティを取得する。
     *
     * @return 社内コメント
     */
    public StringProperty syanaiCommentProperty() {
        if (Objects.isNull(this.syanaiCommentProperty)) {
            this.syanaiCommentProperty = new SimpleStringProperty(this.syanaiComment);
        }
        return this.syanaiCommentProperty;
    }

    /**
     * 工程番号プロパティを取得する。
     *
     * @return 工程番号
     */
    public StringProperty kbanProperty() {
        if (Objects.isNull(this.kbanProperty)) {
            this.kbanProperty = new SimpleStringProperty(this.kban);
        }
        return this.kbanProperty;
    }

    /**
     * 計画指示数プロパティを取得する。
     *
     * @return 計画指示数
     */
    public IntegerProperty kvolProperty() {
        if (Objects.isNull(this.kvolProperty)) {
            this.kvolProperty = new SimpleIntegerProperty(this.kvol);
        }
        return this.kvolProperty;
    }

    /**
     * 指示数プロパティを取得する。
     *
     * @return 指示数
     */
    public IntegerProperty lvolProperty() {
        if (Objects.isNull(this.lvolProperty)) {
            this.lvolProperty = new SimpleIntegerProperty(this.lvol);
        }
        return this.lvolProperty;
    }

    /**
     * 不良数プロパティを取得する。
     *
     * @return 不良数
     */
    public IntegerProperty defectProperty() {
        if (Objects.isNull(this.defectProperty)) {
            this.defectProperty = new SimpleIntegerProperty(this.defect);
        }
        return this.defectProperty;
    }

    /**
     * 残り台数プロパティを取得する。
     *
     * @return 残り台数
     */
    public IntegerProperty remProperty() {
        if (Objects.isNull(this.remProperty)) {
            this.remProperty = new SimpleIntegerProperty(this.rem);
        }
        return this.remProperty;
    }

    /**
     * 注番を取得する。
     *
     * @return 注番
     */
    public String getPorder() {
        if (Objects.nonNull(this.porderProperty)) {
            return this.porderProperty.get();
        }
        return this.porder;
    }

    /**
     * 注番を設定する。
     *
     * @param porder 注番
     */
    public void setPorder(String porder) {
        if (Objects.nonNull(this.porderProperty)) {
            this.porderProperty.set(porder);
        } else {
            this.porder = porder;
        }
    }

    /**
     * 品名を取得する。
     *
     * @return 品名
     */
    public String getHinmei() {
        if (Objects.nonNull(this.hinmeiProperty)) {
            return this.hinmeiProperty.get();
        }
        return this.hinmei;
    }

    /**
     * 品名を設定する。
     *
     * @param hinmei 品名
     */
    public void setHinmei(String hinmei) {
        if (Objects.nonNull(this.hinmeiProperty)) {
            this.hinmeiProperty.set(hinmei);
        } else {
            this.hinmei = hinmei;
        }
    }

    /**
     * 規格・型式を取得する。
     *
     * @return 規格・型式
     */
    public String getKikakuKatasiki() {
        if (Objects.nonNull(this.kikakuKatasikiProperty)) {
            return this.kikakuKatasikiProperty.get();
        }
        return this.kikakuKatasiki;
    }

    /**
     * 規格・型式を設定する。
     *
     * @param kikakuKatasiki 規格・型式
     */
    public void setKikakuKatasiki(String kikakuKatasiki) {
        if (Objects.nonNull(this.kikakuKatasikiProperty)) {
            this.kikakuKatasikiProperty.set(kikakuKatasiki);
        } else {
            this.kikakuKatasiki = kikakuKatasiki;
        }
    }

    /**
     * 部門名を取得する。
     *
     * @return 部門名
     */
    public String getKbumoName() {
        if (Objects.nonNull(this.kbumoNameProperty)) {
            return this.kbumoNameProperty.get();
        }
        return this.kbumoName;
    }

    /**
     * 部門名を設定する。
     *
     * @param kbumoName 部門名
     */
    public void setKbumoName(String kbumoName) {
        if (Objects.nonNull(this.kbumoNameProperty)) {
            this.kbumoNameProperty.set(kbumoName);
        } else {
            this.kbumoName = kbumoName;
        }
    }

    /**
     * 図番を取得する。
     *
     * @return 図番
     */
    public String getSyanaiZuban() {
        if (Objects.nonNull(this.syanaiZubanProperty)) {
            return this.syanaiZubanProperty.get();
        }
        return this.syanaiZuban;
    }

    /**
     * 図番を設定する。
     *
     * @param syanaiZuban 図番
     */
    public void setSyanaiZuban(String syanaiZuban) {
        if (Objects.nonNull(this.syanaiZubanProperty)) {
            this.syanaiZubanProperty.set(syanaiZuban);
        } else {
            this.syanaiZuban = syanaiZuban;
        }
    }

    /**
     * 社内コメントを取得する。
     *
     * @return 社内コメント
     */
    public String getSyanaiComment() {
        if (Objects.nonNull(this.syanaiCommentProperty)) {
            return this.syanaiCommentProperty.get();
        }
        return this.syanaiComment;
    }

    /**
     * 社内コメントを設定する。
     *
     * @param syanaiComment 社内コメント
     */
    public void setSyanaiComment(String syanaiComment) {
        if (Objects.nonNull(this.syanaiCommentProperty)) {
            this.syanaiCommentProperty.set(syanaiComment);
        } else {
            this.syanaiComment = syanaiComment;
        }
    }

    /**
     * 工程番号を取得する。
     *
     * @return 工程番号
     */
    public String getKban() {
        if (Objects.nonNull(this.kbanProperty)) {
            return this.kbanProperty.get();
        }
        return this.kban;
    }

    /**
     * 工程番号を設定する。
     *
     * @param kban 工程番号
     */
    public void setKban(String kban) {
        if (Objects.nonNull(this.kbanProperty)) {
            this.kbanProperty.set(kban);
        } else {
            this.kban = kban;
        }
    }

    /**
     * 計画指示数を取得する。
     *
     * @return 計画指示数
     */
    public Integer getKvol() {
        if (Objects.nonNull(this.kvolProperty)) {
            return this.kvolProperty.get();
        }
        return this.kvol;
    }

    /**
     * 計画指示数を設定する。
     *
     * @param kvol 計画指示数
     */
    public void setKvol(Integer kvol) {
        if (Objects.nonNull(this.kvolProperty)) {
            this.kvolProperty.set(kvol);
        } else {
            this.kvol = kvol;
        }
    }

    /**
     * 指示数を取得する。
     *
     * @return 指示数
     */
    public Integer getLvol() {
        if (Objects.nonNull(this.lvolProperty)) {
            return this.lvolProperty.get();
        }
        return this.lvol;
    }

    /**
     * 指示数を設定する。
     *
     * @param lvol 指示数
     */
    public void setLvol(Integer lvol) {
        if (Objects.nonNull(this.lvolProperty)) {
            this.lvolProperty.set(lvol);
        } else {
            this.lvol = lvol;
        }
    }

    /**
     * 不良数を取得する。
     *
     * @return 不良数
     */
    public Integer getDefect() {
        if (Objects.nonNull(this.defectProperty)) {
            return this.defectProperty.get();
        }
        return this.defect;
    }

    /**
     * 不良数を設定する。
     *
     * @param defect 不良数
     */
    public void setDefect(Integer defect) {
        if (Objects.nonNull(this.defectProperty)) {
            this.defectProperty.set(defect);
        } else {
            this.defect = defect;
        }
    }

    /**
     * 残り台数を取得する。
     *
     * @return 残り台数
     */
    public Integer getRem() {
        if (Objects.nonNull(this.remProperty)) {
            return this.remProperty.get();
        }
        return this.rem;
    }

    /**
     * 残り台数を設定する。
     *
     * @param rem 残り台数
     */
    public void setRem(Integer rem) {
        if (Objects.nonNull(this.remProperty)) {
            this.remProperty.set(rem);
        } else {
            this.rem = rem;
        }
    }

    /**
     * シリアル番号を取得する。
     *
     * @return シリアル番号
     */
    public List<String> getSn() {
        return this.sn;
    }

    /**
     * シリアル番号を設定する。
     *
     * @param sn シリアル番号
     */
    public void setSn(List<String> sn) {
        this.sn = sn;
    }

    /**
     * 工程順IDを取得する。
     *
     * @return 工程順ID
     */
    public Long getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 工程順IDを設定する。
     *
     * @param workflowId 工程順ID
     */
    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 不良シリアル(シリアル番号, 不良理由)を取得する。
     *
     * @return 不良シリアル(シリアル番号, 不良理由)
     */
    public Map<String, String> getDefectSerials() {
        return this.defectSerials;
    }

    /**
     * 不良シリアル(シリアル番号, 不良理由)を設定する。
     *
     * @param defectSerials 不良シリアル(シリアル番号, 不良理由)
     */
    public void setDefectSerials(Map<String, String> defectSerials) {
        this.defectSerials = defectSerials;
    }

    @Override
    public String toString() {
        return new StringBuilder("OrderInfoEntity{")
                .append("porder=").append(this.porder)
                .append(", hinmei=").append(this.hinmei)
                .append(", kikakuKatasiki=").append(this.kikakuKatasiki)
                .append(", kbumoName=").append(this.kbumoName)
                .append(", syanaiZuban=").append(this.syanaiZuban)
                .append(", syanaiComment=").append(this.syanaiComment)
                .append(", kban=").append(this.kban)
                .append(", kvol=").append(this.kvol)
                .append(", lvol=").append(this.lvol)
                .append(", defect=").append(this.defect)
                .append(", rem=").append(this.rem)
                .append(", sn=").append(this.sn)
                .append(", workflowId=").append(this.workflowId)
                .append("}")
                .toString();
    }
}
