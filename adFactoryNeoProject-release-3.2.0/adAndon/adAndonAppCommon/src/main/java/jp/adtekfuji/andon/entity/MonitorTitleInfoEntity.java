/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.andon.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.andon.enumerate.HorizonAlignmentTypeEnum;

/**
 * 進捗モニタ タイトル情報
 *
 * @author ke.yokoi
 */
@XmlRootElement(name = "monitorTitleInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorTitleInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;// タイトル
    private HorizonAlignmentTypeEnum horizonAlignment;// 水平位置

    /**
     * コンストラクタ
     */
    public MonitorTitleInfoEntity() {
    }

    /**
     * タイトルを設定して、タイトル情報を取得する。
     *
     * @param title タイトル
     * @return タイトル情報
     */
    public MonitorTitleInfoEntity title(String title) {
        this.title = title;
        return this;
    }

    /**
     * 水平位置を設定して、タイトル情報を取得する。
     *
     * @param horizonAlignment 水平位置
     * @return タイトル情報
     */
    public MonitorTitleInfoEntity horizonAlignment(HorizonAlignmentTypeEnum horizonAlignment) {
        this.horizonAlignment = horizonAlignment;
        return this;
    }

    /**
     * タイトルを取得する。
     *
     * @return タイトル
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * タイトルを設定する。
     *
     * @param title タイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 水平位置を取得する。
     *
     * @return 水平位置
     */
    public HorizonAlignmentTypeEnum getHorizonAlignment() {
        return this.horizonAlignment;
    }

    /**
     * 水平位置を設定する。
     *
     * @param horizonAlignment 水平位置
     */
    public void setHorizonAlignment(HorizonAlignmentTypeEnum horizonAlignment) {
        this.horizonAlignment = horizonAlignment;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.title);
        hash = 89 * hash + Objects.hashCode(this.horizonAlignment);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonitorTitleInfoEntity other = (MonitorTitleInfoEntity) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (this.horizonAlignment != other.horizonAlignment) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("MonitorTitleInfoEntity{")
                .append("title=").append(this.title)
                .append(", ")
                .append("horizonAlignment=").append(this.horizonAlignment)
                .append("}")
                .toString();
    }
}
