/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.systemsettingplugin.entity;

import adtekfuji.locale.LocaleUtils;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.organization.OrganizationInfoEntity;

/**
 * 承認テーブル画面表示用データ
 *
 * @author shizuka.hirano
 */
public class ApprovalTableData {

    /**
     * リソースバンドル
     */
    private final static ResourceBundle rb = LocaleUtils.getBundle("locale.locale");

    /**
     * 承認順名プロパティ
     */
    private final StringProperty approvalOrderNameProperty = new SimpleStringProperty();

    /**
     * 組織識別名プロパティ
     */
    private final StringProperty organizationIdentifyProperty = new SimpleStringProperty();

    /**
     * 組織名プロパティ
     */
    private final StringProperty organizationNameProperty = new SimpleStringProperty();

    /**
     * メールアドレスプロパティ
     */
    private StringProperty mailAddressProperty;

    /**
     * メールアドレス
     */
    private String mailAddress;

    /**
     * 組織情報
     */
    private OrganizationInfoEntity organizationInfo = new OrganizationInfoEntity();

    /**
     * ルートID
     */
    private Long routeId;

    /**
     * 承認順
     */
    private Integer approvalOrder;

    /**
     * 組織ID
     */
    private Long organizationId;

    /**
     * 最終承認者
     */
    private Boolean approvalFinal;

    /**
     * コンストラクタ
     */
    public ApprovalTableData() {
    }

    /**
     * コンストラクタ
     *
     * @param entity 組織情報
     * @param routeId ルートID
     * @param index 選択行のインデックス
     * @param size 承認者テーブルの件数
     */
    public ApprovalTableData(OrganizationInfoEntity entity, Long routeId, int index, int size) {
        this.organizationInfo = entity;
        this.routeId = routeId;
        this.approvalOrder = index;
        this.organizationId = entity.getOrganizationId();
        this.organizationIdentifyProperty.setValue(entity.getOrganizationIdentify());
        this.organizationNameProperty.setValue(entity.getOrganizationName());
        this.mailAddress = entity.getMailAddress();
        if (size == index) {
            this.approvalFinal = true;
            this.approvalOrderNameProperty.setValue(LocaleUtils.getString("key.FinalApprover"));
        } else {
            this.approvalFinal = false;
            this.approvalOrderNameProperty.setValue(LocaleUtils.getString("key.Authorizer") + index);
        }
    }

    /**
     * 承認順名プロパティを取得する。
     *
     * @return 承認順名プロパティ
     */
    public StringProperty approvalOrderNameProperty() {
        return this.approvalOrderNameProperty;
    }

    /**
     * 承認順名を取得する。
     *
     * @return 承認順名
     */
    public String getApprovalOrderName() {
        return this.approvalOrderNameProperty.get();
    }

    /**
     * 承認順名を設定する。
     *
     * @param value 承認順名
     */
    public void setApprovalOrderName(String value) {
        this.approvalOrderNameProperty.set(value);
    }

    /**
     * 組織識別名プロパティを取得する。
     *
     * @return 組織識別名プロパティ
     */
    public StringProperty organizationIdentifyProperty() {
        return this.organizationIdentifyProperty;
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentify() {
        return this.organizationIdentifyProperty.get();
    }

    /**
     * 組織識別名を設定する。
     *
     * @param value 組織識別名
     */
    public void setOrganizationIdentify(String value) {
        if (!value.equals(organizationIdentifyProperty.get())) {
            this.organizationIdentifyProperty.set(value);
        }
    }

    /**
     * 組織名プロパティを取得する。
     *
     * @return 組織名プロパティ
     */
    public StringProperty organizationNameProperty() {
        return this.organizationNameProperty;
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        return this.organizationNameProperty.get();
    }

    /**
     * 組織名を設定する。
     *
     * @param value 組織名
     */
    public void setOrganizationName(String value) {
        if (!value.equals(organizationNameProperty.get())) {
            this.organizationNameProperty.set(value);
        }
    }

    /**
     * メールアドレスプロパティを取得する。
     *
     * @return メールアドレス
     */
    public StringProperty mailAddressProperty() {
        if (Objects.isNull(this.mailAddressProperty)) {
            this.mailAddressProperty = new SimpleStringProperty(this.mailAddress);
        }
        return this.mailAddressProperty;
    }

    /**
     * メールアドレスを取得する。
     *
     * @return メールアドレス
     */
    public String getMailAddress() {
        if (Objects.nonNull(this.mailAddressProperty)) {
            return this.mailAddressProperty.get();
        }
        return this.mailAddress;
    }

    /**
     * メールアドレスを設定する。
     *
     * @param mailAddress メールアドレス
     */
    public void setMailAddress(String mailAddress) {
        if (Objects.nonNull(this.mailAddressProperty)) {
            this.mailAddressProperty.set(mailAddress);
        } else {
            this.mailAddress = mailAddress;
        }
    }

    /**
     * 組織情報を取得する。
     *
     * @return 組織情報
     */
    public OrganizationInfoEntity getOrganizationInfo() {
        return this.organizationInfo;
    }

    /**
     * 組織情報を設定する。
     *
     * @param value 組織情報
     */
    public void setOrganizationInfo(OrganizationInfoEntity value) {
        this.organizationInfo = value;
    }

    /**
     * ルートIDを取得する。
     *
     * @return ルートID
     */
    public Long getRouteId() {
        return this.routeId;
    }

    /**
     * ルートIDを設定する。
     *
     * @param value ルートID
     */
    public void setRouteId(Long value) {
        this.routeId = value;
    }

    /**
     * 承認順を取得する。
     *
     * @return 承認順
     */
    public Integer getApprovalOrder() {
        return this.approvalOrder;
    }

    /**
     * 承認順を設定する。
     *
     * @param value 承認順
     */
    public void setApprovalOrder(Integer value) {
        this.approvalOrder = value;
    }

    /**
     * ルートIDを取得する。
     *
     * @return ルートID
     */
    public Long getOrganizationId() {
        return this.organizationId;
    }

    /**
     * ルートIDを設定する。
     *
     * @param value ルートID
     */
    public void setOrganizationId(Long value) {
        this.organizationId = value;
    }

    /**
     * 最終承認者を取得する。
     *
     * @return 最終承認者
     */
    public Boolean getApprovalFinal() {
        return this.approvalFinal;
    }

    /**
     * 最終承認者を設定する。
     *
     * @param value 最終承認者
     */
    public void setApprovalFinal(Boolean value) {
        this.approvalFinal = value;
    }

    /**
     * ハッシュコードを取得する。
     * 
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.organizationIdentifyProperty);
        return hash;
    }

    /**
     * オブジェクトが等しいかどうかを取得する。
     * 
     * @param obj 比較対象のオブジェクト
     * @return オブジェクトが等しい場合はtrue、それ以外の場合はfalse
     */
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
        final ApprovalTableData other = (ApprovalTableData) obj;

        return Objects.equals(this.getOrganizationIdentify(), other.getOrganizationIdentify());
    }

     /**
     * オブジェクトをコピーする
     *
     * @return 承認テーブル画面表示用データ
     */
    @Override
    public ApprovalTableData clone() {
        ApprovalTableData entity = new ApprovalTableData();

        entity.setOrganizationInfo(this.getOrganizationInfo());
        entity.setApprovalOrderName(this.getApprovalOrderName());
        entity.setOrganizationIdentify(this.getOrganizationIdentify());
        entity.setOrganizationName(this.getOrganizationName());
        entity.setMailAddress(this.getMailAddress());
        entity.setRouteId(this.getRouteId());
        entity.setApprovalOrder(this.getApprovalOrder());
        entity.setOrganizationId(this.getOrganizationId());
        entity.setApprovalFinal(this.getApprovalFinal());

        return entity;
    }
}
