/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.utility;

import adtekfuji.clientservice.EquipmentInfoFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.andon.property.WorkEquipmentSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ツール
 *
 * @author s-heya
 */
public class MonitorTools {

    private static final String COUNTDOWN_MMSS = "%s%02d:%02d";
    private static final String COUNTDOWN_HHMMSS = "%s%02d:%02d:%02d";
    private static final long MAX_LOAD_SIZE = 20;

    /**
     * タクトタイムを表示文字列に整形する。
     *
     * @param time
     * @return
     */
    public static String formatTaktTime(long time) {
        double doubleValue;
        int hour;
        int min;
        int sec;
        String symbol;

        if (time >= 0) {
            symbol = " ";
            doubleValue = time / 1000D;
        } else {
            symbol = "-";
            doubleValue = -(time / 1000D);
        }
        hour = (int) (doubleValue / 3600D);
        min = (int) (doubleValue % 3600D / 60D);
        sec = (int) (doubleValue % 3600D % 60D);
        return String.format(COUNTDOWN_HHMMSS, symbol, hour, min, sec);
    }

    /**
     * フォントサイズを取得する。
     *
     * @param text
     * @param width
     * @param height
     * @param defaultSize
     * @return
     */
    public static double getFontSize(String text, double width, double height, double defaultSize) {
        Text helper = new Text(text);
        helper.setFont(Font.font("Meiryo UI", defaultSize));
        double size = width >= helper.getBoundsInLocal().getWidth() ? defaultSize : defaultSize * (width / helper.getBoundsInLocal().getWidth() * 0.95);
        helper.setFont(Font.font("Meiryo UI", size));
        return height >= helper.getBoundsInLocal().getHeight() ? size : size * (height / helper.getBoundsInLocal().getHeight() * 0.95);
    }

    /**
     * 対象ラインの設備IDリストを取得する。
     *
     * @param lineId 対象ラインID
     * @return 対象ラインの設備IDリスト (対象ラインと直下の設備)
     */
    public static List<Long> getLineEquipmentIds(Long lineId) {
        Logger logger = LogManager.getLogger();
        logger.info("getLineEquipmentIds: lineId={}", lineId);
        try {
            if (Objects.isNull(lineId)) {
                return new ArrayList();
            }

            List<Long> lineEquipmentIds = new ArrayList();

            lineEquipmentIds.add(lineId);

            EquipmentInfoFacade equipmentInfoFacade = new EquipmentInfoFacade();

            Long equipmentCount = equipmentInfoFacade.getAffilationHierarchyCount(lineId);
            for (long from = 0; from <= equipmentCount; from += MAX_LOAD_SIZE) {
                List<EquipmentInfoEntity> entities = equipmentInfoFacade.getAffilationHierarchyRange(lineId, from, from + MAX_LOAD_SIZE - 1);
                List<Long> ids = entities.stream().map(p -> p.getEquipmentId()).collect(Collectors.toList());
                lineEquipmentIds.addAll(ids);
            }

            return lineEquipmentIds;

        } catch (Exception ex) {
            logger.fatal(ex, ex);
            return new ArrayList();
        }
    }

    /**
     * 対象設備選択一覧から対象設備ID一覧を取得する。
     *
     * @param workEquipments 対象設備選択一覧
     * @return 対象設備ID一覧
     */
    public static List<Long> getWorkEquipmentIds(List<WorkEquipmentSetting> workEquipments) {
        List<Long> workEquipmentIds = new ArrayList();
        if (Objects.nonNull(workEquipmentIds)) {
            for (WorkEquipmentSetting workEquipment : workEquipments) {
                workEquipmentIds.addAll(workEquipment.getEquipmentIds());
            }
        }
        return workEquipmentIds.stream().distinct().collect(Collectors.toList());
    }
}
