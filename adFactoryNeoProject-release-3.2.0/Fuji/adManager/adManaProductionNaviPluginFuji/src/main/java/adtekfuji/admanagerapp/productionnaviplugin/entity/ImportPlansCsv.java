/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity;

import com.opencsv.bean.CsvBindByName;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 予定表
 * 
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class ImportPlansCsv {

    /**
     * 予定名
     */
    @CsvBindByName(column ="予定名称", required = true )
    private String plansName;

    /** 
     * 予定開始日時
     */
    @CsvBindByName(column ="予定開始日時", required = true)
    private String startDatetime;

    /**
     * 予定終了日時
     */
    @CsvBindByName(column ="予定終了日時", required = true)
    private String stopDatetime;

    /**
     * 組織識別名
     */
    @CsvBindByName(column ="組織識別名", required = true)
    private String organization;

    /**
     * 予定名称の取得
     * 
     * @return 予定名称
     */
    public String getPlansName(){
        return this.plansName;
    }
    
    /**
     * 予定名称の設定
     * 
     * @param _value 予定名称
     */
    public void setPlansName(String _value){
        this.plansName = _value;
    }

    /**
     * 予定開始日時の取得
     * 
     * @return 予定開始日時
     */
    public String getStartDatetime(){
        return this.startDatetime;
    }
    /**
     * 予定開始日時の設定
     * 
     * @param _value 予定開始日時
     */
    public void setStartDatetime(String _value){
        this.startDatetime = _value;
    }
    /**
     * 予定終了日時の取得
     * 
     * @return 予定終了日時
     */
    public String getStopDatetime(){
        return this.stopDatetime;
    }
    /**
     * 予定終了日時の設定
     * 
     * @param _value 予定終了日時
     */
    public void setStopDatetime(String _value){
        this.stopDatetime = _value;
    }
    /**
     * 組織識別名の取得
     * 
     * @return 組織識別名
     */
    public String getOrganization(){
        return this.organization;
    }
    /**
     * 組織識別名の設定
     * 
     * @param _value 組織識別名
     */
    public void setOrganization(String _value){
        this.organization = _value;
    }
    
    /**
     * カラム
     * 
     * @param idxName 予定名称のポジション
     * @param idxStartDate 予定開始日時のポジション
     * @param idxStopDate 予定終了日時のポジション
     * @param idxOrganization 組織識別名のポジション
     * @return 
     */
    public static String[] getColumns(int idxName, int idxStartDate, int idxStopDate, int idxOrganization){
        final Logger logger = LogManager.getLogger();
        
        if(idxName<0 || idxStartDate<0 || idxStopDate<0 || idxOrganization<0){
            logger.error(" Columns Idx Error");
            return null;
        }

        int maxIdx = idxName > idxStartDate ? idxName : idxStartDate;
        maxIdx = maxIdx > idxStopDate ? maxIdx : idxStopDate;
        maxIdx = maxIdx > idxOrganization ? maxIdx : idxOrganization;
        
        String[] value = new String[maxIdx];
        Arrays.fill(value, "");
        
        for(int i=0 ; i<value.length ; i++){
            if( idxName-1 == i){
                value[i] = "plansName";
            }else if( idxStartDate-1 == i){
                value[i] = "startDatetime";
            }else if( idxStopDate-1 == i){
                value[i] = "stopDatetime";
            }else if( idxOrganization-1 == i){
                value[i] = "organization";
            }
        }

        return value;
    }


    /**
     * 
     * @return 
     */
    @Override
    public String toString(){
        return "plansName=" + this.plansName + ", startDatetime=" + this.startDatetime + ", stopDatetime=" + stopDatetime + ", organization=" + organization;
    }


    /**
     * 
     * @return 
     */
    public String toStringCharacter(){
        return "予定名=" + this.plansName + ", 予定開始日時=" + this.startDatetime + ", 予定終了日時=" + stopDatetime + ", 組織識別名=" + organization;
    }

    /**
     * 全ての項目が存在するかチェック
     * 
     * @return 
     */
    public boolean isNotEmpty(){
        return (StringUtils.isNotEmpty(this.plansName) && StringUtils.isNotEmpty(this.startDatetime) && StringUtils.isNotEmpty(this.stopDatetime) && StringUtils.isNotEmpty(this.organization) );
    }

    /**
     * 全ての項目が存在しないかチェック
     * 
     * @return 
     */
    public boolean isEmpty(){
        return (StringUtils.isEmpty(this.plansName) || StringUtils.isEmpty(this.startDatetime) || StringUtils.isEmpty(this.stopDatetime) || StringUtils.isEmpty(this.organization) );
    }
}
