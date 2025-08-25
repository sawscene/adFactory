/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.forfujiapp.entity.monitor;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 進捗モニタリスト表示用情報クラス
 *
 * @author ek.mori
 * @version 1.4.2
 * @since 2016.10.21.Fri
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "columnTitleData")
public class MonitorListColumnTitleData {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "titles")
    @XmlElement(name = "title")
    private List<String> titles;

    public MonitorListColumnTitleData() {
    }

    public MonitorListColumnTitleData(List<String> titles) {
        this.titles = titles;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
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
        final MonitorListInfoEntity other = (MonitorListInfoEntity) obj;
        return true;
    }

    @Override
    public String toString() {
        return "MonitorListColumnTitleData{" + "titles=" + titles + '}';
    }
}
