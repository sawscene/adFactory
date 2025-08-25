/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.organization;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jp.adtekfuji.adFactory.entity.indirectwork.IndirectWorkInfoEntity;
import jp.adtekfuji.adFactory.enumerate.AuthorityEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 組織情報
 *
 * @author e.mori
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "organization")
public class OrganizationInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlTransient
    private LongProperty organizationIdProperty;
    @XmlTransient
    private LongProperty parentIdProperty;
    @XmlTransient
    private StringProperty organizationIdentifyProperty;
    @XmlTransient
    private StringProperty organizationNameProperty;
    @XmlTransient
    private ObjectProperty<AuthorityEnum> authorityTypeProperty;
    @XmlTransient
    private StringProperty langIdsProperty;
    @XmlTransient
    private StringProperty passwordProperty;
    @XmlTransient
    private StringProperty mailAddressProperty;
    @XmlTransient
    private LongProperty updatePersonIdProperty;
    @XmlTransient
    private ObjectProperty<Date> updateDateTimeProperty;

    @XmlElement(required = true)
    private Long organizationId;// 組織ID
    @XmlElement()
    private Long parentId;// 親組織ID
    @XmlElement()
    private String organizationIdentify;// 組織識別名
    @XmlElement()
    private String organizationName;// 組織名
    @XmlElement()
    private AuthorityEnum authorityType;// 権限
    @XmlElement()
    private String langIds;// 言語(JSON形式)
    @XmlElement()
    private String password;// パスワード
    @XmlElement()
    private String mailAddress;// メールアドレス
    @XmlElement()
    private Long updatePersonId;// 更新者
    @XmlElement()
    private Date updateDatetime;// 更新日時
    @XmlElement()
    private Boolean removeFlag;// 論理削除フラグ
    @XmlElement()
    private Long childCount = 0L;// 子組織数

    @XmlTransient
    private List<OrganizationPropertyInfoEntity> propertyCollection = null;// 組織プロパティ一覧

    @XmlElementWrapper(name = "breaktimes")
    @XmlElement(name = "breaktime")
    private List<Long> breaktimeCollection = new ArrayList<>();// 休憩時間一覧

    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    private List<Long> roleCollection = new ArrayList<>();// ロール一覧

    @XmlElementWrapper(name = "workCategories")
    @XmlElement(name = "workCategory")
    private List<Long> workCategoryCollection = new ArrayList<>();// 作業区分一覧

    @XmlElement()
    private String organizationAddInfo;// 追加情報(JSON形式)
    @XmlElement()
    private String serviceInfo;// サービス情報(JSON形式)

    @XmlElementWrapper(name = "localeFileInfos")
    @XmlElement(name = "localeFileInfo")
    private List<LocaleFileInfoEntity> localeFileInfoCollection;// 言語ファイル

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    @XmlElementWrapper(name = "interruptCategories")
    @XmlElement(name = "id")
    private List<Long> interruptCategoryCollection;
    
    @XmlElementWrapper(name = "delayCategories")
    @XmlElement(name = "id")
    private List<Long> delayCategoryCollection;

    @XmlElementWrapper(name = "callCategories")
    @XmlElement(name = "id")
    private List<Long> callCategoryCollection;
        
    @XmlElementWrapper(name = "indirectWorks")
    @XmlElement(name = "indirectWork")
    private List<IndirectWorkInfoEntity> indirectWorkCollection;
    
    /**
     * コンストラクタ
     */
    public OrganizationInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param organizationId 組織ID
     * @param organizationIdentify 組織識別名
     * @param organizationName 組織名
     * @param authorityType 権限
     */
    public OrganizationInfoEntity(Long organizationId, String organizationIdentify, String organizationName, AuthorityEnum authorityType) {
        this.organizationId = organizationId;
        this.organizationIdentify = organizationIdentify;
        this.organizationName = organizationName;
        this.authorityType = authorityType;
    }

    /**
     * 組織IDプロパティを取得する。
     *
     * @return 組織ID
     */
    public LongProperty organizationIdProperty() {
        if (Objects.isNull(this.organizationIdProperty)) {
            this.organizationIdProperty = new SimpleLongProperty(this.organizationId);
        }
        return this.organizationIdProperty;
    }

    /**
     * 親組織IDプロパティを取得する。
     *
     * @return 親組織ID
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentIdProperty;
    }

    /**
     * 組織識別名プロパティを取得する。
     *
     * @return 組織識別名
     */
    public StringProperty organizationIdentifyProperty() {
        if (Objects.isNull(this.organizationIdentifyProperty)) {
            this.organizationIdentifyProperty = new SimpleStringProperty(this.organizationIdentify);
        }
        return this.organizationIdentifyProperty;
    }

    /**
     * 組織名プロパティを取得する。
     *
     * @return 組織名
     */
    public StringProperty organizationNameProperty() {
        if (Objects.isNull(this.organizationNameProperty)) {
            this.organizationNameProperty = new SimpleStringProperty(this.organizationName);
        }
        return this.organizationNameProperty;
    }

    /**
     * 権限プロパティを取得する。
     *
     * @return 権限
     */
    public ObjectProperty<AuthorityEnum> authorityTypeProperty() {
        if (Objects.isNull(this.authorityTypeProperty)) {
            this.authorityTypeProperty = new SimpleObjectProperty<>(this.authorityType);
        }
        return this.authorityTypeProperty;
    }

    /**
     * 言語プロパティを取得する。
     *
     * @return 言語
     */
    public StringProperty langIdsProperty() {
        if (Objects.isNull(this.langIdsProperty)) {
            this.langIdsProperty = new SimpleStringProperty(this.langIds);
        }
        return this.langIdsProperty;
    }

    /**
     * パスワードプロパティを取得する。
     *
     * @return パスワード
     */
    public StringProperty passwordTypeProperty() {
        if (Objects.isNull(this.passwordProperty)) {
            this.passwordProperty = new SimpleStringProperty(this.password);
        }
        return this.passwordProperty;
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
     * 更新者プロパティを取得する。
     *
     * @return 更新者
     */
    public LongProperty updatePersonIdProperty() {
        if (Objects.isNull(this.updatePersonIdProperty)) {
            this.updatePersonIdProperty = new SimpleLongProperty(this.updatePersonId);
        }
        return this.updatePersonIdProperty;
    }

    /**
     * 更新日時プロパティを取得する。
     *
     * @return 更新日時
     */
    public ObjectProperty<Date> updateDateTimeProperty() {
        if (Objects.isNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty = new SimpleObjectProperty(this.updateDatetime);
        }
        return this.updateDateTimeProperty;
    }

    /**
     * 組織IDを取得する。
     *
     * @return 組織ID
     */
    public Long getOrganizationId() {
        if (Objects.nonNull(this.organizationIdProperty)) {
            return this.organizationIdProperty.get();
        }
        return this.organizationId;
    }

    /**
     * 組織IDを設定する。
     *
     * @param organizationId 組織ID
     */
    public void setOrganizationId(Long organizationId) {
        if (Objects.nonNull(this.organizationIdProperty)) {
            this.organizationIdProperty.set(organizationId);
        } else {
            this.organizationId = organizationId;
        }
    }

    /**
     * 親組織IDを取得する。
     *
     * @return 親組織ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * 親組織IDを設定する。
     *
     * @param parentId 親組織ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * 組織識別名を取得する。
     *
     * @return 組織識別名
     */
    public String getOrganizationIdentify() {
        if (Objects.nonNull(this.organizationIdentifyProperty)) {
            return this.organizationIdentifyProperty.get();
        }
        return this.organizationIdentify;
    }

    /**
     * 組織識別名を設定する。
     *
     * @param organizationIdentify 組織識別名
     */
    public void setOrganizationIdentify(String organizationIdentify) {
        if (Objects.nonNull(this.organizationIdentifyProperty)) {
            this.organizationIdentifyProperty.set(organizationIdentify);
        } else {
            this.organizationIdentify = organizationIdentify;
        }
    }

    /**
     * 組織名を取得する。
     *
     * @return 組織名
     */
    public String getOrganizationName() {
        if (Objects.nonNull(this.organizationNameProperty)) {
            return this.organizationNameProperty.get();
        }
        return this.organizationName;
    }

    /**
     * 組織名を設定する。
     *
     * @param organizationName 組織名
     */
    public void setOrganizationName(String organizationName) {
        if (Objects.nonNull(this.organizationNameProperty)) {
            this.organizationNameProperty.set(organizationName);
        } else {
            this.organizationName = organizationName;
        }
    }

    /**
     * 権限を取得する。
     *
     * @return 権限
     */
    public AuthorityEnum getAuthorityType() {
        if (Objects.nonNull(this.authorityTypeProperty)) {
            return this.authorityTypeProperty.get();
        }
        return this.authorityType;
    }

    /**
     * 権限を設定する。
     *
     * @param authorityType 権限
     */
    public void setAuthorityType(AuthorityEnum authorityType) {
        if (Objects.nonNull(this.authorityTypeProperty)) {
            this.authorityTypeProperty.set(authorityType);
        } else {
            this.authorityType = authorityType;
        }
    }

    /**
     * 言語を取得する。
     *
     * @return 言語
     */
    public String getLangIds() {
        if (Objects.nonNull(this.langIdsProperty)) {
            return this.langIdsProperty.get();
        }
        return this.langIds;
    }

    /**
     * 言語を設定する。
     *
     * @param langIds 言語
     */
    public void setLangIds(String langIds) {
        if (Objects.nonNull(this.langIdsProperty)) {
            this.langIdsProperty.set(langIds);
        } else {
            this.langIds = langIds;
        }
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getPassword() {
        if (Objects.nonNull(this.passwordProperty)) {
            return this.passwordProperty.get();
        }
        return this.password;
    }

    /**
     * パスワードを設定する。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        if (Objects.nonNull(this.passwordProperty)) {
            this.passwordProperty.set(password);
        } else {
            this.password = password;
        }
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
     * 更新者を取得する。
     *
     * @return 更新者
     */
    public Long getUpdatePersonId() {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            return this.updatePersonIdProperty.get();
        }
        return this.updatePersonId;
    }

    /**
     * 更新者を設定する。
     *
     * @param updatePersonId 更新者
     */
    public void setUpdatePersonId(Long updatePersonId) {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            this.updatePersonIdProperty.set(updatePersonId);
        } else {
            this.updatePersonId = updatePersonId;
        }
    }

    /**
     * 更新日時を取得する。
     *
     * @return 更新日時
     */
    public Date getUpdateDateTime() {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            return this.updateDateTimeProperty.get();
        }
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDateTime 更新日時
     */
    public void setUpdateDateTime(Date updateDateTime) {
        if (Objects.nonNull(this.updateDateTimeProperty)) {
            this.updateDateTimeProperty.set(updateDateTime);
        } else {
            this.updateDatetime = updateDateTime;
        }
    }

    /**
     * 論理削除フラグを取得する。
     *
     * @return 論理削除フラグ (true:削除, false:未削除)
     */
    public Boolean getRemoveFlag() {
        return this.removeFlag;
    }

    /**
     * 論理削除フラグを設定する。
     *
     * @param removeFlag 論理削除フラグ (true:削除, false:未削除)
     */
    public void setRemoveFlag(Boolean removeFlag) {
        this.removeFlag = removeFlag;
    }

    /**
     * 子組織数を取得する。
     *
     * @return 子組織数
     */
    public Long getChildCount() {
        return this.childCount;
    }

    /**
     * 子組織数を設定する。
     *
     * @param childCount 子組織数
     */
    public void setChildCount(Long childCount) {
        this.childCount = childCount;
    }

    /**
     * 組織プロパティ一覧を取得する。
     *
     * @return 組織プロパティ一覧
     */
    public List<OrganizationPropertyInfoEntity> getPropertyInfoCollection() {
        
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if(Objects.isNull(this.propertyCollection)){
            // 変換した結果をエンティティにセットする
            this.setPropertyInfoCollection(JsonUtils.jsonToObjects(this.getOrganizationAddInfo(), OrganizationPropertyInfoEntity[].class));
        }
        return this.propertyCollection;
    }

    /**
     * 組織プロパティ一覧を設定する。
     *
     * @param propertyInfoCollection 組織プロパティ一覧
     */
    public void setPropertyInfoCollection(List<OrganizationPropertyInfoEntity> propertyInfoCollection) {
        this.propertyCollection = propertyInfoCollection;
    }

    /**
     * 休憩時間一覧を取得する。
     *
     * @return 休憩時間一覧
     */
    public List<Long> getBreakTimeInfoCollection() {
        return this.breaktimeCollection;
    }
    
    /**
     * 休憩時間一覧を取得する。(Ubuntu)
     *
     * @return 休憩時間一覧
     */
    public List<Long> getBreakTimeCollection() {
        return this.breaktimeCollection;
    }
    
    /**
     * 休憩時間一覧を設定する。
     *
     * @param breakTimeInfoCollection 休憩時間一覧
     */
    public void setBreakTimeInfoCollection(List<Long> breakTimeInfoCollection) {
        this.breaktimeCollection = breakTimeInfoCollection;
    }

    /**
     * ロール一覧を取得する。
     *
     * @return ロール一覧
     */
    public List<Long> getRoleCollection() {
        return this.roleCollection;
    }

    /**
     * ロール一覧を設定する。
     *
     * @param roleCollection ロール一覧
     */
    public void setRoleCollection(List<Long> roleCollection) {
        this.roleCollection = roleCollection;
    }

    /**
     * 作業区分一覧を取得する。
     *
     * @return 作業区分一覧
     */
    public List<Long> getWorkCategoryCollection() {
        return this.workCategoryCollection;
    }

    /**
     * 作業区分一覧を設定する。
     *
     * @param workCategoryCollection 作業区分一覧
     */
    public void setWorkCategoryCollection(List<Long> workCategoryCollection) {
        this.workCategoryCollection = workCategoryCollection;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getOrganizationAddInfo() {
        return this.organizationAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param organizationAddInfo 追加情報(JSON)
     */
    public void setOrganizationAddInfo(String organizationAddInfo) {
        this.organizationAddInfo = organizationAddInfo;
    }

    /**
     * サービス情報(JSON)を取得する。
     *
     * @return サービス情報(JSON)
     */
    public String getServiceInfo() {
        return this.serviceInfo;
    }

    /**
     * サービス情報(JSON)を設定する。
     *
     * @param serviceInfo サービス情報(JSON)
     */
    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * 言語ファイルを取得する。
     *
     * @return 言語ファイル
     */
    public List<LocaleFileInfoEntity> getLocaleFileInfoCollection() {
        return this.localeFileInfoCollection;
    }

    /**
     * 言語ファイルを設定する。
     *
     * @param localeFileInfoCollection 言語ファイル
     */
    public void setLocaleFileInfoCollection(List<LocaleFileInfoEntity> localeFileInfoCollection) {
        this.localeFileInfoCollection = localeFileInfoCollection;
    }

    /**
     * 排他用バーションを取得する。
     *
     * @return 排他用バーション
     */
    public Integer getVerInfo() {
        return this.verInfo;
    }

    /**
     * 排他用バーションを設定する。
     *
     * @param verInfo 排他用バーション
     */
    public void setVerInfo(Integer verInfo) {
        this.verInfo = verInfo;
    }

    /**
     * 中断理由一覧を取得する。
     * 
     * @return 中断理由一覧
     */
    public List<Long> getInterruptCategoryCollection() {
        return interruptCategoryCollection;
    }

    /**
     * 中断理由一覧を設定する。
     * 
     * @param interruptCategoryCollection 中断理由一覧
     */
    public void setInterruptCategoryCollection(List<Long> interruptCategoryCollection) {
        this.interruptCategoryCollection = interruptCategoryCollection;
    }

    /**
     * 遅延理由一覧を取得する。
     * 
     * @return 遅延理由一覧
     */
    public List<Long> getDelayCategoryCollection() {
        return delayCategoryCollection;
    }

    /**
     * 遅延理由一覧を設定する。
     * 
     * @param delayCategoryCollection 遅延理由一覧
     */
    public void setDelayCategoryCollection(List<Long> delayCategoryCollection) {
        this.delayCategoryCollection = delayCategoryCollection;
    }

    /**
     * 呼出理由一覧を取得する。
     * 
     * @return 呼出理由一覧
     */
    public List<Long> getCallCategoryCollection() {
        return callCategoryCollection;
    }

    /**
     * 呼出理由一覧を設定する。
     * 
     * @param callCategoryCollection 呼出理由一覧
     */
    public void setCallCategoryCollection(List<Long> callCategoryCollection) {
        this.callCategoryCollection = callCategoryCollection;
    }
    
    /**
     * 間接作業一覧を取得する。
     * 
     * @return 間接作業一覧
     */
    public List<IndirectWorkInfoEntity> getIndirectWorkCollection() {
        return indirectWorkCollection;
    }

    /**
     * 間接作業一覧を設定する。
     * 
     * @param indirectWorkCollection 間接作業一覧
     */
    public void setIndirectWorkCollection(List<IndirectWorkInfoEntity> indirectWorkCollection) {
        this.indirectWorkCollection = indirectWorkCollection;
    }

    /**
     * ハッシュコードを返す。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.organizationId);
        return hash;
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
        final OrganizationInfoEntity other = (OrganizationInfoEntity) obj;
        return Objects.equals(this.organizationId, other.organizationId);
    }

    /**
     * 文字列表現を返す。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return new StringBuilder("OrganizationInfoEntity{")
                .append("organizationId=").append(this.organizationId)
                .append(", parentId=").append(this.parentId)
                .append(", organizationIdentify=").append(this.organizationIdentify)
                .append(", organizationName=").append(this.organizationName)
                .append(", authorityType=").append(this.authorityType)
                .append(", langIds=").append(this.langIds)
                .append(", password=").append(this.password)
                .append(", mailAddress=").append(this.mailAddress)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", removeFlag=").append(this.removeFlag)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    /**
     * 同じ内部情報を持つエンティティを作成 あくまでcompareInfoによる比較用
     *
     * @return
     */
    public OrganizationInfoEntity clone() {
        OrganizationInfoEntity info = new OrganizationInfoEntity();

        info.setOrganizationId(this.getOrganizationId()); //IDは表示されないが前のやつと判別するための比較に必要
        info.setOrganizationIdentify(this.getOrganizationIdentify());
        info.setOrganizationName(this.getOrganizationName());
        info.setMailAddress(this.getMailAddress());
        info.setPassword(this.getPassword());

        info.setBreakTimeInfoCollection(new ArrayList(this.getBreakTimeInfoCollection()));
        info.setRoleCollection(new ArrayList(this.getRoleCollection()));
        info.setWorkCategoryCollection(new ArrayList(this.getWorkCategoryCollection()));

        //追加情報のコピー
        List<OrganizationPropertyInfoEntity> initialProperties = new LinkedList();
        getPropertyInfoCollection().stream().forEach(c -> initialProperties.add(c.clone()));
        info.setPropertyInfoCollection(initialProperties);

        info.setParentId(this.getParentId());

        // 追加情報
        info.setOrganizationAddInfo(this.getOrganizationAddInfo());
        // サービス情報
        info.setServiceInfo(this.getServiceInfo());

        // 言語
        if (Objects.nonNull(this.localeFileInfoCollection)) {
            List<LocaleFileInfoEntity> initialLocales = this.localeFileInfoCollection
                    .stream()
                    .map(LocaleFileInfoEntity::new)
                    .collect(Collectors.toList());
            info.setLocaleFileInfoCollection(initialLocales);
        } else {
            info.setLocaleFileInfoCollection(null);
        }

        info.setVerInfo(this.getVerInfo());

        return info;
    }

    /**
     * 内容が一致しているかどうかを調べる
     *
     * @param info
     * @return
     */
    public boolean displayInfoEquals(OrganizationInfoEntity info) {
        boolean ret = false;

        //ソートされるためそれに合わせる
        List<Long> sortedRoleSelf = new ArrayList(this.getRoleCollection());
        sortedRoleSelf.sort(Comparator.reverseOrder());
        List<Long> sortedRoleOther = new ArrayList(info.getRoleCollection());
        sortedRoleOther.sort(Comparator.reverseOrder());

        List<Long> sortedBreakTimeSelf = new ArrayList(this.getBreakTimeInfoCollection());
        sortedBreakTimeSelf.sort(Comparator.reverseOrder());
        List<Long> sortedBreakTimeOther = new ArrayList(info.getBreakTimeInfoCollection());
        sortedBreakTimeOther.sort(Comparator.reverseOrder());

        List<Long> sortedWorkCategorySelf = new ArrayList(this.getWorkCategoryCollection());
        sortedWorkCategorySelf.sort(Comparator.reverseOrder());
        List<Long> sortedWorkCategoryOther = new ArrayList(info.getWorkCategoryCollection());
        sortedWorkCategoryOther.sort(Comparator.reverseOrder());

        if (Objects.equals(getOrganizationName(), info.getOrganizationName())
                && Objects.equals(this.getOrganizationIdentify(), info.getOrganizationIdentify())
                && Objects.equals(this.getMailAddress(), info.getMailAddress())
                && Objects.equals(sortedRoleSelf, sortedRoleOther)
                && Objects.equals(sortedBreakTimeSelf, sortedBreakTimeOther)
                && Objects.equals(sortedWorkCategorySelf, sortedWorkCategoryOther)
                && propertyInfoListEquals(this.getPropertyInfoCollection(), info.getPropertyInfoCollection())
                && Objects.equals(this.getLocaleFileInfoCollection(), info.getLocaleFileInfoCollection())
                ) {
            ret = true;
        }
        return ret;
    }

    /**
     * 追加情報が同じ内容であるか調べる
     *
     * @param a
     * @param b
     * @return
     */
    private boolean propertyInfoListEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        java.util.Iterator<OrganizationPropertyInfoEntity> it1 = a.iterator();
        java.util.Iterator<OrganizationPropertyInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            OrganizationPropertyInfoEntity entity1 = it1.next();
            OrganizationPropertyInfoEntity entity2 = it2.next();
            if (!entity1.displayInfoEquals(entity2)) {
                return false;
            }
        }

        return true;
    }
}
