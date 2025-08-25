/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adfactoryserver.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jp.adtekfuji.adfactoryserver.entity.work.WorkEntity;
import jp.adtekfuji.adfactoryserver.entity.workflow.WorkflowEntity;

/**
 * REST APIの送受信用オブジェクト
 * 使用方法：@XmlSeeAlsoと@XmlElementRefsに対象のEntityを追加する
 * 
 * @author y-harada
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "objectParam")
@XmlSeeAlso({WorkEntity.class, WorkflowEntity.class})
public class ObjectParam<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementRefs({
        @XmlElementRef(name = "work", type = WorkEntity.class),
        @XmlElementRef(name = "workflow", type = WorkflowEntity.class),
    })
    private List<T> listParam;

    /**
     * コンストラクタ
     */
    public ObjectParam() {
    }

    /**
     * コンストラクタ
     * 
     * @param listParam 一覧
     */
    public ObjectParam(List<T> listParam) {
        this.listParam = listParam;
    }

    /**
     * リストを取得する。
     * 
     * @return リスト
     */
    public List<T> getList() {
        return listParam;
    }

    /**
     * リストを設定する。
     * 
     * @param listParam リスト
     */
    public void setList(List<T> listParam) {
        this.listParam = listParam;
    }
}
