/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbanregistpreprocessplugin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adFactory.entity.kanban.KanbanInfoEntity;
import jp.adtekfuji.adFactory.entity.kanban.KanbanPropertyInfoEntity;
import jp.adtekfuji.adFactory.enumerate.CustomPropertyTypeEnum;
import jp.adtekfuji.adFactory.utility.KanbanRegistPreprocessResultEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author e-mori
 */
public class CollateKanbanPro {

    private static final Logger logger = LogManager.getLogger();

    //プロパティの定数
    private static final String PROPERTY_NAME_ORDER_NUMBER = "オーダー番号";
    private static final String PROPERTY_NAME_ORDER_SERIAL = "シリアル";
    private static final String PROPERTY_PROJECT_NO_ORDER_NUMBER = "受注番号";

    /**
     * カンバン登録前処理
     *
     * @param mastarDataList
     * @param entity
     * @return 登録前処理の実行結果
     */
    public static KanbanRegistPreprocessResultEntity collateKanbanProData(List<List<String[]>> mastarDataList, KanbanInfoEntity entity) {
        logger.info("To collate kanban property data");
        List<KanbanPropertyInfoEntity> propertyInfoEntitys = new ArrayList<>(entity.getPropertyCollection());
        boolean projectNoFlag = false;
        boolean orderNumberFlag = false;
        boolean orderSerialFlag = false;

        //マスターデータのファイルが見つからなかった場合判定処理を行わずに判定成功を返す
        if (mastarDataList.size() <= 0) {
            logger.warn("no mastar file");
            return new KanbanRegistPreprocessResultEntity(Boolean.TRUE, null);
        }
        //オーダ・受注番号・シリアルのどれも入っていない場合判定処理を行わずに判定成功を返す
        for (KanbanPropertyInfoEntity property : propertyInfoEntitys) {
            if (Objects.isNull(property.getKanbanPropertyValue())) {
                property.setKanbanPropertyValue("");
            }
            switch (property.getKanbanPropertyName()) {
                case PROPERTY_PROJECT_NO_ORDER_NUMBER:
                    projectNoFlag = true;
                    break;
                case PROPERTY_NAME_ORDER_NUMBER:
                    orderNumberFlag = true;
                    break;
                case PROPERTY_NAME_ORDER_SERIAL:
                    orderSerialFlag = true;
                    break;
                default:
                    break;
            }
        }
        if (!(projectNoFlag || orderNumberFlag || orderSerialFlag)) {
            logger.warn("no collate");
            return new KanbanRegistPreprocessResultEntity(Boolean.TRUE, null);
        } else if ((projectNoFlag && orderSerialFlag) || (projectNoFlag && orderNumberFlag)) {
            return new KanbanRegistPreprocessResultEntity(Boolean.FALSE, "key.NothingOrderNumber");
        }

        //ファイル
        for (List<String[]> mastarFileData : mastarDataList) {
            //レコード
            if (mastarFileData.get(0).length == 1) {
                if (projectNoFlag) {
                    if (checkProjectNo(mastarFileData, propertyInfoEntitys)) {
                        return new KanbanRegistPreprocessResultEntity(Boolean.TRUE, null);
                    }
                }
            } else if (mastarFileData.get(0).length == 2) {
                if ((orderNumberFlag && orderSerialFlag) || orderNumberFlag) {
                    if (checkOrderNumberAndSerial(mastarFileData, propertyInfoEntitys)) {
                        return new KanbanRegistPreprocessResultEntity(Boolean.TRUE, null);
                    }
                }
            }
        }
        logger.warn("no match");
        return new KanbanRegistPreprocessResultEntity(Boolean.FALSE, "key.NothingOrderNumber");
    }

    /**
     * 受注番号の紹介
     *
     * @param mastarFile
     * @param propertyInfoEntitys
     * @return
     */
    private static boolean checkProjectNo(List<String[]> mastarFile, List<KanbanPropertyInfoEntity> propertyInfoEntitys) {
        for (String[] mastarFileRecord : mastarFile) {
            for (String mastarFileData : mastarFileRecord) {
                if (propertyInfoEntitys.stream().anyMatch((propertyInfoEntity) -> (mastarFileData.equals(propertyInfoEntity.getKanbanPropertyValue())))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkOrderNumberAndSerial(List<String[]> mastarFile, List<KanbanPropertyInfoEntity> propertyInfoEntitys) {
        // オーダー番号のみだった場合シリアルをプロパティに追加
        boolean isSerial = false;
        for (KanbanPropertyInfoEntity propertyInfoEntity : propertyInfoEntitys) {
            if (propertyInfoEntity.getKanbanPropertyName().equals(PROPERTY_NAME_ORDER_SERIAL)) {
                isSerial = true;
                break;
            }
        }
        if (!isSerial) {
            propertyInfoEntitys.add(new KanbanPropertyInfoEntity(null, null, PROPERTY_NAME_ORDER_SERIAL, CustomPropertyTypeEnum.TYPE_STRING, "", null));
        }
//        if (propertyInfoEntitys.size() == 1) {
//            propertyInfoEntitys.add(new KanbanPropertyInfoEntity(null, null, PROPERTY_NAME_ORDER_SERIAL, CustomPropertyTypeEnum.TYPE_STRING, "", null));
//        }        
        boolean orderNumFlag = false;
        boolean serialFlag = false;
        for (String[] mastarFileRecord : mastarFile) {
            if (mastarFileRecord.length != 2) {
                continue;
            }
            for (KanbanPropertyInfoEntity propertyInfoEntity : propertyInfoEntitys) {
                if (propertyInfoEntity.getKanbanPropertyName().equals(PROPERTY_NAME_ORDER_NUMBER)) {
                    if (propertyInfoEntity.getKanbanPropertyValue().equals(mastarFileRecord[0])) {
                        orderNumFlag = true;
                    }
                }
                if (propertyInfoEntity.getKanbanPropertyName().equals(PROPERTY_NAME_ORDER_SERIAL)) {
                    if (propertyInfoEntity.getKanbanPropertyValue().equals(mastarFileRecord[1])) {
                        serialFlag = true;
                    }
                }
            }
            if (orderNumFlag && serialFlag) {
                return true;
            }
            orderNumFlag = false;
            serialFlag = false;
        }
        return false;
    }
}
