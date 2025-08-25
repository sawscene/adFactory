package jp.adtekfuji.adFactory.entity.summaryreport;

public class CountRankingElementEntity {
    public String name;

    public Long count;

    public CountRankingElementEntity() {
    }

    public CountRankingElementEntity(String name, Long count) {
        this.name = name;
        this.count = count;
    }
}
