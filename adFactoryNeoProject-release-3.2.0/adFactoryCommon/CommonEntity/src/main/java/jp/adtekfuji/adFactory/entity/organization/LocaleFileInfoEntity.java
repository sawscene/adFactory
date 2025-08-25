/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.organization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.resource.ResourceInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LocaleTypeEnum;

/**
 * 言語ファイル情報
 *
 * @author HN)y-harada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LocaleFileInfo")
public class LocaleFileInfoEntity {

    @XmlElement()
    @JsonProperty("type")
    private LocaleTypeEnum localeType;

    @XmlElement(name = "resource")
    @JsonIgnore
    private ResourceInfoEntity resource;

    @XmlElement()
    @JsonIgnore
    private boolean isUpdate = false;

    /**
     * コンストラクタ
     */
    public LocaleFileInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param localeType ロケール種別
     * @param resource リソース
     */
    public LocaleFileInfoEntity(LocaleTypeEnum localeType, ResourceInfoEntity resource) {
        this.localeType = localeType;
        this.resource = resource;
    }

    /**
     * コンストラクタ
     *
     * @param other
     */
    public LocaleFileInfoEntity(LocaleFileInfoEntity other) {
        this.localeType = other.localeType;
        this.resource = new ResourceInfoEntity(other.resource());
        this.isUpdate = other.isUpdate;
    }

    /**
     * リソースエンティティ
     *
     * @return リソース
     */
    public ResourceInfoEntity resource(){
        return resource;
    }

    /**
     * 言語リソースを取得する。
     * (OpenJDKの対応により追加)
     * 
     * @return 
     */
    public ResourceInfoEntity getResource() {
        return resource;
    }

    /**
     * ロケール種別を取得する
     *
     * @return ロケール種別
     */
    public LocaleTypeEnum getLocaleType(){
        return this.localeType;
    }

    /**
     * ロケール種別を取得する
     *
     * @param type　ロケール種別
     */
    public void setLocaleType(LocaleTypeEnum type){
        this.localeType = type;
    }

    /**
     * 更新フラグを設定する
     *
     * @return  更新フラグ
     */
    public boolean getIsUpdate(){
        return this.isUpdate;
    }

    /**
     * 更新フラグを設定する
     *
     * @param flg 更新フラグ
     */
    public void setIsUpdate(boolean flg){
        this.isUpdate = flg;
    }

    /**
     * リソースIDを取得する(JSON用)
     * 
     * @return リソースID
     */
    @JsonProperty("id")
    private Long getEntityResouceId(){
        return this.resource().getResourceId();
    }

    /**
     * リソースIDを設定する(JSON用)
     * 
     * @return リソースID
     */
    @JsonProperty("id")
    private void setEntityResouceId(Long id){
        if(Objects.isNull(this.resource)) {
            this.resource = new ResourceInfoEntity();
        }
        this.resource().setResourceId(id);
    }

    /**
     * リソースキーを取得する(JSON用)
     * 
     * @return リソースキー
     */
    @JsonProperty("key")
    private String getEntityResouceKey(){
        return this.resource().getResourceKey();
    }

    /**
     * リソースキーを設定する(JSON用)
     *
     * @return リソースキー
     */
    @JsonProperty("key")
    private void setEntityResouceKey(String key){
        if(Objects.isNull(this.resource)) {
            this.resource = new ResourceInfoEntity();
        }
        this.resource().setResourceKey(key);
    }

    /**
     * エンティティに差異があれば更新フラグを立てる
     *
     * @param target 更新フラグを立てるリスト
     * @param comparison 比較リスト
     */
    public static void updateFlags(List<LocaleFileInfoEntity> target, List<LocaleFileInfoEntity> comparison) {

        if (Objects.isNull(target)) {
            return;
        }

        java.util.Iterator<LocaleFileInfoEntity> it1 = target.iterator();
        java.util.Iterator<LocaleFileInfoEntity> it2 = Objects.isNull(comparison) ? null : comparison.iterator();

        while (it1.hasNext()) {
            LocaleFileInfoEntity entity1 = it1.next();
            if (Objects.isNull(it2) || !it2.hasNext()) {
                entity1.setIsUpdate(true);
                continue;
            }
            LocaleFileInfoEntity entity2 = it2.next();
            if (!Objects.equals(entity1, entity2)) {
               entity1.setIsUpdate(true);
            }
        }
    }

     /**
     * オブジェクトを比較する。
     *
     * @param obj オブジェクト
     * @return true:同じである、false:異なる
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocaleFileInfoEntity other = (LocaleFileInfoEntity) obj;
        if (!Objects.equals(this.localeType, other.localeType)
                ||!Objects.equals(this.isUpdate, other.isUpdate)
                ||!Objects.equals(this.resource, other.resource)) {
            return false;
        }
        return true;
    }

     /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.localeType);
        hash = 11 * hash + Objects.hashCode(this.resource);
        hash = 11 * hash + (this.isUpdate ? 1 : 0);
        return hash;
    }
}
