/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.model.warehouse;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstLocation;
import jp.adtekfuji.adfactoryserver.entity.warehouse.MstProduct;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnDelivery;
import jp.adtekfuji.adfactoryserver.entity.warehouse.TrnMaterial;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 倉庫案内モデル
 * 
 * @author s-heya
 */
@Singleton
public class WarehouseModel extends WarehouseModelImpl {
    private final Logger logger = LogManager.getLogger();
  
    /**
     * 棚マスタ照会
     * 棚情報を取得する。
     * 
     * @param areaName 区画名
     * @param locationNo 棚番号
     * @return 棚マスタ
     */
    @Lock(LockType.READ)
    public MstLocation findLocation(String areaName, String locationNo) {
        return super.findLocation(areaName, locationNo, true);
    }
 
    /**
     * QRラベルによる資材照会
     * 資材情報を取得する。
     * 
     * @param materialNo 資材番号
     * @return 資材情報
     */
    @Lock(LockType.READ)
    public TrnMaterial findMaterial(String materialNo) {
        return super.findMaterial(materialNo, true);
    }
   
    /**
     * QRラベルによる資材照会
     * 資材情報を取得する。
     * 
     * @param materialNo 資材番号
     * @param isReadOnly 読み取り専用
     * @return 資材情報
     */
    @Override
    public TrnMaterial findMaterial(String materialNo, boolean isReadOnly) {
        return super.findMaterial(materialNo, isReadOnly);
    }

    /**
     * 現品票(納品書)による資材照会
     * 資材情報を取得する。
     * 
     * @param supplyNo 納入番号(発注番号/倉庫オーダー)
     * @return 資材情報
     */
    @Lock(LockType.READ)
    public TrnMaterial findMaterialBySupplyNo(String supplyNo) {
        return super.findMaterialBySupplyNo(supplyNo, true);
    }
  
    
    /**
     * 製造指示書による資材照会
     * 資材情報を取得する。 
     * 
     * @param orderNo 製造番号
     * @return 資材情報
     */
    @Lock(LockType.READ)
    public TrnMaterial findMaterialByOrderNo(String orderNo) {
        return super.findMaterialByOrderNo(orderNo, true);
    }

    /**
     * 部品マスタを取得する。
     * 
     * @param productNo 品目
     * @return 部品マスタ
     */
    public MstProduct findProduct(String productNo) {
        return super.findProduct(productNo, true);
    }

    /**
     * 部品マスタを取得する。
     * 
     * @param productNo 品目
     * @param productName 品名 (未使用)
     * @return 部品マスタ
     */
    public MstProduct findProduct(String productNo, String productName) {
        return super.findProduct(productNo, true);
    }
    
    /**
     * 出庫指示情報を取得する。
     * 
     * @param deliveryNo 出庫指示番号
     * @return 出庫指示情報
     */
    public TrnDelivery findDelivery(String deliveryNo) {
        return super.findDelivery(deliveryNo, true);
    }
    
    /**
     * 図番をキーにして部品マスタを取得する。
     * 
     * @param figureNo 図番
     * @return 部品マスタ
     */
    @Lock(LockType.READ)
    public MstProduct findProductByFigureNo(String figureNo) {
        return super.findProductByFigureNo(figureNo, true);
    }
}
