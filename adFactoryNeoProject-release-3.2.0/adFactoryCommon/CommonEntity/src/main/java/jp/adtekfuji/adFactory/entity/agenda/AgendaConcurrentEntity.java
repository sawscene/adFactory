/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.agenda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * 並列予実情報
 *
 * @author ke.yokoi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "agendaConcurrent")
public class AgendaConcurrentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "agendaItems")
    @XmlElement(name = "agendaItem")
    private List<AgendaItemEntity> itemCollection = new ArrayList();// 予実情報一覧

    /**
     * コンストラクタ
     */
    public AgendaConcurrentEntity() {
    }

    /**
     * 予実情報一覧を取得する。
     *
     * @return 予実情報一覧
     */
    public List<AgendaItemEntity> getItemCollection() {
        return itemCollection;
    }

    /**
     * 予実情報一覧を設定する。
     *
     * @param itemCollection 予実情報一覧
     */
    public void setItemCollection(List<AgendaItemEntity> itemCollection) {
        this.itemCollection = itemCollection;
    }

    /**
     * 予実情報を追加する。
     *
     * @param item 予実情報
     * @return 並列予実情報
     */
    public AgendaConcurrentEntity addItem(AgendaItemEntity item) {
        this.itemCollection.add(item);
        return this;
    }

    /**
     * 予実情報を追加する。
     *
     * @param items 予実情報
     * @return 並列予実情報
     */
    public AgendaConcurrentEntity addItems(AgendaItemEntity... items) {
        this.itemCollection.addAll(Arrays.asList(items));
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AgendaConcurrentEntity other = (AgendaConcurrentEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("AgendaConcurrentEntity{")
                .append("}")
                .toString();
    }
}
