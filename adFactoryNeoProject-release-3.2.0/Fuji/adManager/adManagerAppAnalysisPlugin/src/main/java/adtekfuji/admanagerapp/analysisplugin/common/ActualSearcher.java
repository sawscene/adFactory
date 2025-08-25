/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.analysisplugin.common;

import adtekfuji.utility.DateUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.actual.ActualResultEntity;
import jp.adtekfuji.adFactory.entity.search.ActualSearchCondition;
import jp.adtekfuji.adFactory.enumerate.KanbanStatusEnum;
import jp.adtekfuji.forfujiapp.clientservice.RestAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 実績検索クラス
 *
 * @author e-mori
 * @version 1.4.2
 * @since 2016.08.03.Wed
 */
public class ActualSearcher {

    private static final Logger logger = LogManager.getLogger();

    /**
     * 検索処理
     *
     * @param data 検索データ
     * @return 検索結果
     */
    public static List<ActualResultEntity> search(ActualSearchSettingData data) {
        logger.info(ActualSearcher.class.getName() + ":search start");
        ActualSearchCondition condition = createSearchCondition(data);
        logger.info(ActualSearcher.class.getName() + ":search end");
        return RestAPI.searchActualResult(condition);
    }

    /**
     * 検索結果生成処理
     *
     * @param data 検索データ
     * @return 検索用に変換した情報
     */
    private static ActualSearchCondition createSearchCondition(ActualSearchSettingData data) {
        // TODO:LocalDateからDateに変換するいいほうほうがない！
        // 有ったらそっちに乗り換え
        logger.info(ActualSearcher.class.getName() + ":createSearchCondition start");

        ActualSearchCondition condition = new ActualSearchCondition();
        condition.setFromDate(DateUtils.getBeginningOfDate(data.getStartDate()));
        condition.setToDate(DateUtils.getEndOfDate(data.getEndDate()));
        List<String> workNames = new ArrayList<>();
        for (WorkTableData work : data.getworkTabelDatas()) {
            String name = work.getItem().getWorkName();
            if (Objects.nonNull(name) || !name.equals("")) {
                workNames.add(name);
            }
        }
        condition.setWorkNameCollection(workNames);

        // ステータス設定:作業時間だけ必要なので現在は完了のみ設定
        List<KanbanStatusEnum> enums = new ArrayList<>();
        enums.add(KanbanStatusEnum.COMPLETION);
        enums.add(KanbanStatusEnum.SUSPEND);
        condition.setKanbanStatusCollection(enums);

        logger.info(ActualSearcher.class.getName() + ":createSearchCondition end");
        return condition;
    }

}
