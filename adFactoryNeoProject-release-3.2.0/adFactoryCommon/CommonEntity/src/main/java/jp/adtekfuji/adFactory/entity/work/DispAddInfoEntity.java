/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import adtekfuji.locale.LocaleUtils;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 表示項目
 *
 * @author y-harada
 */
@Root(name = "dispAddInfo", strict = false)
@Default(DefaultType.FIELD)
public class DispAddInfoEntity {

    @Element(required = false)
    private Integer order;
    @Element(required = false)
    private TargetTypeEnum target;
    @Element(required = false)
    private String name;
    @Element(required = false)
    private String value;
    
    /**
     * 対象種別
     */
    public static enum TargetTypeEnum {

        KANBAN("key.Kanban"),
        WORKKANBAN("key.WorkKanban"),
        WORK("key.Process");
        
        private final String resourceKey;

        private TargetTypeEnum(String resourceKey) {
            this.resourceKey = resourceKey;
        }
        
        /**
         * 表示名を取得する。
         *
         * @param rb
         * @return
         */
        public String getDisplayName(ResourceBundle rb) {
            return LocaleUtils.getString(this.resourceKey);
        }
        
        /**
         * 表示名を全件取得する。
         *
         * @param rb
         * @return
         */
        public static List<String> getDisplayValues(ResourceBundle rb) {
            List<String> values = new ArrayList<>();
            TargetTypeEnum[] enumArray = TargetTypeEnum.values();
            for (TargetTypeEnum enumStr : enumArray) {
                values.add(LocaleUtils.getString(enumStr.resourceKey));
            }
            return values;
        }
    };

    /**
     * コンストラクタ
     */
    public DispAddInfoEntity() {
    }

    /**
     * 表示順を取得する。
     *
     * @return 表示順
     */
    public Integer getOrder() {
        return this.order;
    }

    /**
     * 表示順を設定する。
     *
     * @param order 表示順
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * 対象種別を取得する。
     *
     * @return 対象種別
     */
    public TargetTypeEnum getTarget() {
        return this.target;
    }

    /**
     * 対象種別を設定する。
     *
     * @param target 対象種別
     */
    public void setTarget(TargetTypeEnum target) {
        this.target = target;
    }

    /**
     * 項目名を取得する。
     *
     * @return 項目名
     */
    public String getName() {
        return this.name;
    }

    /**
     * 項目名を設定する。
     *
     * @param name 項目名
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.target);
        hash = 59 * hash + Objects.hashCode(this.name);
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
        final DispAddInfoEntity other = (DispAddInfoEntity) obj;
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "dispAddInfo{"
                + "order=" + this.order
                + ", target=" + this.target
                + ", name=" + this.name
                + ", value=" + this.value
                + '}';
    }
}
