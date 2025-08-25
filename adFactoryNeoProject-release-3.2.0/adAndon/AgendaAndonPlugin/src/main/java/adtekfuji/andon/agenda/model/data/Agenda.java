/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.LinkedList;
import java.util.List;
import jp.adtekfuji.adFactory.entity.master.AddInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * 予実情報
 *
 * @author s-heya
 */
public class Agenda {

    private Long id;
    private String title1;
    private String title2;
    private String fontColor;
    private String backColor;
    private Boolean blink;
    private Long delayTimeMillisec;
    // 計画情報
    private final List<AgendaPlan> plans = new LinkedList<>();
    // 実績情報
    private final List<AgendaGroup> actusls = new LinkedList<>();
    // 生産進捗情報
    private final List<AgendaPlan> progress = new LinkedList<>();
    // 実績の行数
    private int rowCount = 0;
    // 追加情報
    private    List<AddInfoEntity> kanbanAddInfos;

    private boolean isShowPlanAndActualLabel = true;

    private long planWork;      // 予定工数
    private long actualWork;    // 実績工数
    
    public Agenda() {
    }

    public Agenda(Long id, String title1, String title2, String fontColor, String backgraundColor) {
        this.id = id;
        this.title1 = title1;
        this.title2 = title2;
        this.fontColor = fontColor;
        this.backColor = backgraundColor;
        this.isShowPlanAndActualLabel = true;
    }

    public Long getId() {
        return id;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public Boolean isBlink() {
        return blink;
    }

    public void setBlink(Boolean blink) {
        this.blink = blink;
    }

    public Long getDelayTimeMillisec() {
        return delayTimeMillisec;
    }

    public void setDelayTimeMillisec(Long delayTimeMillisec) {
        this.delayTimeMillisec = delayTimeMillisec;
    }

    public List<AgendaPlan> getPlans() {
        return this.plans;
    }

    public List<AgendaGroup> getActuals() {
        return this.actusls;
    }

    /**
     * 生産進捗情報一覧を取得する
     * 
     * @return 生産進捗情報一覧
     */
    public List<AgendaPlan> getProgress() {
        return this.progress;
    }

    /**
     * 行数を取得する。
     * 
     * @return 行数(縦時間軸の場合は列数)
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * 行数を設定する。
     * 
     * @param rowCount 行数(縦時間軸の場合は列数)
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * 予実ラベルの表示、非表示設定を取得
     * @return true:表示 false:非表示
     */
    public boolean isShowPlanAndActualLabel() {
        return isShowPlanAndActualLabel;
    }

    /**
     * 予実ラベルの表示、非表示を設定
     * @param showPlanAndActualLabel true:表示 false:非表示
     */
    public void setShowPlanAndActualLabel(boolean showPlanAndActualLabel) {
        isShowPlanAndActualLabel = showPlanAndActualLabel;
    }

    /**
     * 追加情報取得
     * @return 
     */
    public List<AddInfoEntity> getKanbanAddInfos() {
        return this.kanbanAddInfos;
    }

    /**
     * 追加情報設定
     * @param kanbanAddInfoValue 
     */
    public void setKanbanAddInfos(String kanbanAddInfoValue) {
        if (!Strings.isBlank(kanbanAddInfoValue)) {
            this.kanbanAddInfos = JsonUtils.jsonToObjects(kanbanAddInfoValue, AddInfoEntity[].class);
        }
    }

    public long getPlanWork() {
        return planWork;
    }

    public void setPlanWork(long planWork) {
        this.planWork = planWork;
    }

    public long getActualWork() {
        return actualWork;
    }

    public void setActualWork(long actualWork) {
        this.actualWork = actualWork;
    }

    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "Agenda{" + "title1=" + title1 + ", title2=" + title2 + ", fontColor=" + fontColor + ", backgraundColor=" + backColor + ", isBlink=" + blink +
                ", delayTimeMillisec=" + delayTimeMillisec + '}';
    }
}
