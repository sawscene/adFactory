/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import java.io.Serializable;
import java.util.List;
import jakarta.xml.bind.annotation.*;

import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;
import jp.adtekfuji.adFactory.entity.workflow.WorkflowInfoEntity;

/**
 * REST APIの送受信用オブジェクト
 * 
 * @author yu.nara
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ListWrapper")
public class ListWrapper<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "dataList")
    @XmlElement(name = "data")
    private List<T> data;
;

    /**
     * コンストラクタ
     */
    public ListWrapper() {
    }

    /**
     * コンストラクタ
     * 
     * @param data リスト
     */
    public ListWrapper(List<T> data) {
        this.data = data;
    }

    /**
     * リストを取得する。
     * 
     * @return リスト
     */
    public List<T> getList() {
        return data;
    }

    /**
     * リストを設定する。
     * 
     * @param listParam リスト
     */
    public void setList(List<T> data) {
        this.data = data;
    }
}
