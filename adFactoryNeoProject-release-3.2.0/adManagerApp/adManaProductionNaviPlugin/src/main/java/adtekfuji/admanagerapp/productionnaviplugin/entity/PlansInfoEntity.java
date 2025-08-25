/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.productionnaviplugin.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;
import jp.adtekfuji.adFactory.entity.schedule.ScheduleInfoEntity;

/**
 * 予定表入力データ
 * 
 * @author (TST)H.Nishimura
 * @version 2.0.0
 * @since 2018/09/28
 */
public class PlansInfoEntity {
    
    private String title;
    private List<OrganizationInfoEntity> fkOrganization = new ArrayList();
    private ScheduleInfoEntity entity = new ScheduleInfoEntity();

    /**
     * コンストラクタ
     * 
     * @param _title タイトル
     * @param _entity 予定表
     * @param _fkOrganization 組織
     */
    public PlansInfoEntity(String _title, ScheduleInfoEntity _entity, List<OrganizationInfoEntity> _fkOrganization){
        this.title = _title;
        this.entity = _entity;
        this.fkOrganization = _fkOrganization;

    }
    /**
     * コンストラクタ
     * 
     * @param _title タイトル
     * @param _entity 予定表
     * @param _fkOrganization 組織
     */
    public PlansInfoEntity(String _title, ScheduleInfoEntity _entity, OrganizationInfoEntity _fkOrganization){
        this.title = _title;
        this.entity = _entity;
        this.fkOrganization.clear();
        this.fkOrganization.add(_fkOrganization);
    }
        
    /**
     * タイトルの取得
     * 
     * @return タイトル
     */
    public String getTitle(){
        return this.title;
    }
    /**
     * タイトルの設定
     * 
     * @param _value タイトル
     */
    public void setTitle(String _value){
        this.title = _value;
    }

    /**
     * 予定IDの取得
     * 
     * @return 予定ID
     */
    public Long getScheduleId(){
        return this.entity.getScheduleId();
    }
    /**
     * 予定IDの設定
     * 
     * @param _value 予定ID
     */
    public void setScheduleId(Long _value){
        this.entity.setScheduleId(_value);
    }
    /**
     * 予定名の取得
     * 
     * @return 予定名
     */
    public String getScheduleName(){
        return this.entity.getScheduleName();
    }
    /**
     * 予定名の設定
     * 
     * @param _value 予定名
     */
    public void setScheduleName(String _value){
        this.entity.setScheduleName(_value);
    }
    /**
     * 予定開始日時の取得
     * 
     * @return 予定開始日時
     */
    public Date getScheduleFromDate(){
        return this.entity.getScheduleFromDate();
    }
    /**
     * 予定開始日時の設定
     * 
     * @param _value 予定開始日時
     */
    public void setScheduleFromDate(Date _value){
        this.entity.setScheduleFromDate(_value);
    }
    /**
     * 予定終了日時の取得
     * 
     * @return 予定終了日時
     */
    public Date getScheduleToDate(){
        return this.entity.getScheduleToDate();
    }
    /**
     * 予定終了日時の設定
     * 
     * @param _value 予定終了日時
     */
    public void setScheduleToDate(Date _value){
        this.entity.setScheduleToDate(_value);
    }

    /**
     * 組織情報の件数
     * 
     * @return 件数
     */
    public int countFkOrganization(){
        return this.fkOrganization.size();
    }
    
    /**
     * 組織情報の取得
     * 
     * @param idx IDX
     * @return 組織ID
     */
    public OrganizationInfoEntity getFkOrganization(int idx){
        if(idx > this.fkOrganization.size()){
            return null;
            
        }
        return this.fkOrganization.get(idx);
    }

    /**
     * 組織情報の取得
     * 
     * @return 組織情報
     */
    public List<OrganizationInfoEntity> getFkOrganization(){
        return this.fkOrganization;
    }

    /**
     * 組織情報の設定
     * 
     * @param _value 組織情報
     */
    public void setFkOrganization(OrganizationInfoEntity _value){
        this.fkOrganization.add(_value);
        this.entity.setFkOrganizationId(_value.getOrganizationId());
    }

    /**
     * 組織情報の設定
     * 
     * @param _value 組織情報
     */
    public void setFkOrganization(List<OrganizationInfoEntity> _value){
        this.fkOrganization = _value;
        if(_value != null && _value.size() > 0){
            this.entity.setFkOrganizationId(_value.get(_value.size()-1).getOrganizationId());
        }
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.entity.getVerInfo();
    }

    @Override
    public String toString() {
        return new StringBuilder("PlansInfoEntity{")
                .append("title=").append(this.title)
                .append(", ")
                .append("fkOrganization=").append(this.fkOrganization)
                .append(", ")
                .append("entity=").append(this.entity)
                .append("}")
                .toString();
    }
}
