/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.organization.LocaleFileInfoEntity;
import jp.adtekfuji.adFactory.enumerate.LocaleTypeEnum;

/**
 * 言語ファイル情報
 *
 * @author HN)y-harada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LocaleFileInfo")
public class LocaleFileEntity {

    @XmlElement()
    @JsonProperty("type")
    private LocaleTypeEnum localeType;

    @XmlElement(name = "resource")
    @JsonIgnore
    private ResourceEntity resource;

    @XmlElement()
    @JsonIgnore
    private boolean isUpdate = false;

    /**
     * コンストラクタ
     */
    public LocaleFileEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param localeType ロケール種別
     * @param resource リソース
     */
    public LocaleFileEntity(LocaleTypeEnum localeType, ResourceEntity resource) {
        this.localeType = localeType;
        this.resource = resource;
    }

    /**
     * リソースを取得する
     *
     * @return リソース
     */
    public ResourceEntity resource(){
        return this.resource;
    }

    /**
     * リソースを設定する
     *
     * @param resource リソース
     */
    public void setResource(ResourceEntity resource){
        this.resource = resource;
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
     * 更新フラグを取得する
     *
     * @return 更新フラグ
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
            this.resource = new ResourceEntity();
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
            this.resource = new ResourceEntity();
        }
        this.resource().setResourceKey(key);
    }

    /**
     * LocaleFileInfoEntity にキャストする
     *
     * @return LocaleFileInfoEntity
     */
    public LocaleFileInfoEntity cast(){
        return new LocaleFileInfoEntity(this.localeType, this.resource.cast());
    }

}
