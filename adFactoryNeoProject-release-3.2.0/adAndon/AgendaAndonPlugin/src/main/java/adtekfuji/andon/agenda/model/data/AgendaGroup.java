/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.andon.agenda.model.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 計画時間が重なっている計画実績トピックを内包
 *
 * @author s-heya
 */
public class AgendaGroup {
    private Date startDate;
    private Date endDate;
    List<AgendaTopic> topics = new ArrayList<>();


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

    public AgendaGroup(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AgendaGroup() {
        refreshStartAndEndDate =
                (newStartDate, newEndDate)->{
                    this.startDate = newStartDate;
                    this.endDate = newEndDate;
                    refreshStartAndEndDate = defaultRefreshStartAndEndData;
                };
    }

    /**
     * 領域をtopicを使って拡張する。
     * @param topic 追加するトピック
     */
    public void expansion(AgendaTopic topic)
    {
        this.topics.add(topic);
        this.refreshStartAndEndDate.accept(topic.getPlanStartTime(), topic.getPlanEndTime());
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

    public List<AgendaTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<AgendaTopic> topics) {
        this.topics = topics;
    }

    /**
     *  グループとトピックの日付が重なっていないか?
     * @param topic トピック
     * @return 重なっていない場合はtrueを返す。
     */
    public boolean isNoScheduleConflict(AgendaTopic topic)
    {
        return (   this.endDate.before(topic.getPlanStartTime())
                || this.endDate.equals(topic.getPlanStartTime())
                || this.startDate.after (topic.getPlanEndTime())
                || this.startDate.equals(topic.getPlanEndTime()));
    }

    /**
     *  グループとトピックの日付が重なるか?
     * @param topic トピック
     * @return 重なっている場合はtrueを返す。
     */
    public boolean isScheduleConflict(AgendaTopic topic)
    {
            return !isNoScheduleConflict(topic);
    }


    /**
     * AgendaTopicをグループ化。
     *
     * @param topics
     * @return
     */
    public static List<AgendaGroup> groupBy(List<AgendaTopic> topics) {
        List<AgendaGroup> groups = new ArrayList<>();

        topics.stream()
                .sorted(Comparator.comparing(AgendaTopic::getPlanStartTime))
                .forEach(topic -> {
                    // スケジュールが重複した要素を抽出
                    AgendaGroup scheduleConflictGroup
                            = groups
                            .stream()
                            .filter(group -> group.isNoScheduleConflict(topic))
                            .findFirst()
                            .orElseGet(() -> {
                                // 予定が重なるグループがなければ新規に作成
                                AgendaGroup tmp = new AgendaGroup();
                                groups.add(tmp);
                                return tmp;
                            });
                    scheduleConflictGroup.expansion(topic);
                });

        return groups;
    }

    @Override
    public String toString() {
        return "AgendaGroup{" + "startDate=" + startDate + ", endDate=" + endDate + '}';
    }
}
