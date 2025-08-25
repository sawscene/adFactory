/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * 計画時間が重なっている計画実績トピックを内包
 *
 * @author s-heya
 */
public class AgendaPlan {
    private Date startDate;
    private Date endDate;
    Map<Long, List<AgendaTopic>> topics = new LinkedHashMap<>();
    private Long kanbanId;

    BiConsumer<Date, Date> defaultRefreshStartAndEndData =
            (nextStartDate, nextEndDate) -> {
                if (this.startDate.after(nextStartDate)) {
                    this.startDate = nextStartDate;
                }
                if (this.endDate.before(nextEndDate)) {
                    this.endDate = nextEndDate;
                }
            };

    BiConsumer<Date, Date> refreshStartAndEndDate = defaultRefreshStartAndEndData;



    public AgendaPlan(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AgendaPlan() {
        refreshStartAndEndDate =
                (newStartDate, newEndDate)->{
                    this.startDate = newStartDate;
                    this.endDate = newEndDate;
                    refreshStartAndEndDate = defaultRefreshStartAndEndData;
                };
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<Long, List<AgendaTopic>> getTopics() {
        return topics;
    }

    public void setTopics(Map<Long, List<AgendaTopic>> topics) {
        this.topics = topics;
    }

    /**
     * カンバンIDを取得する。
     * 
     * @return カンバンID
     */
    public Long getKanbanId() {
        return kanbanId;
    }

    /**
     * カンバンIDを設定する。
     * 
     * @param kanbanId カンバンID
     */
    public void setKanbanId(Long kanbanId) {
        this.kanbanId = kanbanId;
    }


    public boolean isNoScheduleConflict(AgendaTopic plan) {
        return (this.endDate.before(plan.getPlanStartTime())
                || this.endDate.equals(plan.getPlanStartTime())
                || this.startDate.after(plan.getPlanEndTime())
                || this.startDate.equals(plan.getPlanEndTime())
                );
    }

    public boolean isScheduleConflict(AgendaTopic plan) {
        return !isNoScheduleConflict(plan);
    }


    /**
     * 領域をtopicを使って拡張する。
     * @param plan 追加するトピック
     */
    public void expansion(AgendaTopic plan) {
        List<AgendaTopic> agendaTopics = topics.computeIfAbsent(plan.getKanbanId(), k->new ArrayList<>());
        agendaTopics.add(plan);
        // 開始と完了時間を更新
        this.refreshStartAndEndDate.accept(plan.getPlanStartTime(),plan.getPlanEndTime());
    }


    /**
     * 文字列表現を返す。
     * 
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "AgendaPlan{" + "startDate=" + startDate + ", endDate=" + endDate + ", kanbanId=" + kanbanId + '}';
    }
}
