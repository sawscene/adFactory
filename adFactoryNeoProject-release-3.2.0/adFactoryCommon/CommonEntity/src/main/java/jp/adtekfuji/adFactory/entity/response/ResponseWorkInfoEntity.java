/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.response;

import java.io.Serializable;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jp.adtekfuji.adFactory.entity.ResponseEntity;
import jp.adtekfuji.adFactory.entity.work.WorkInfoEntity;

/**
 * 結果情報 (工程)
 *
 * @author nar-nakamura
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "responseWork")
public class ResponseWorkInfoEntity extends ResponseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement()
    private WorkInfoEntity value;

    /**
     * コンストラクタ
     */
    public ResponseWorkInfoEntity() {
    }

    /**
     * 値を取得する。
     *
     * @return 値
     */
    public WorkInfoEntity getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("ResponseWorkInfoEntity{")
                .append("isSuccess=").append(this.isSuccess())
                .append(", uri=").append(this.getUri())
                .append(", errorType=").append(this.getErrorType())
                .append(", errorCode=").append(this.getErrorCode())
                .append(", value=").append(this.value)
                .append("}")
                .toString();
    }
}
