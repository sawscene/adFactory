/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity.work;

import java.io.Serializable;
import java.util.*;

import adtekfuji.utility.StringUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jp.adtekfuji.adFactory.entity.approval.ApprovalInfoEntity;
import jp.adtekfuji.adFactory.entity.equipment.EquipmentInfoEntity;
import jp.adtekfuji.adFactory.enumerate.ApprovalStatusEnum;
import jp.adtekfuji.adFactory.enumerate.ContentTypeEnum;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 工程情報
 *
 * @author ta.ito
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "work")
public class WorkInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongProperty workIdProperty;
    private LongProperty parentIdProperty;
    private StringProperty workNameProperty;
    private IntegerProperty taktTimeProperty;
    private StringProperty contentProperty;
    private ObjectProperty<ContentTypeEnum> contentTypeProperty;
    private LongProperty updatePersonIdProperty;
    private StringProperty fontColorProperty;
    private StringProperty backColorProperty;
    private ObjectProperty<Date> updateDatetimeProperty;
    private StringProperty usePartsProperty;
    private StringProperty workNumberProperty;

    @XmlElement(required = true)
    private Long workId;// 工程ID
    @XmlElement()
    private Long parentId;// 親階層ID
    @XmlElement()
    private String workName;// 工程名
    @XmlElement()
    private Integer workRev; // 版数
    @XmlElement()
    private Integer taktTime;// タクトタイム[ms]
    @XmlElement()
    private String content;// コンテンツ
    @XmlElement()
    private ContentTypeEnum contentType;// コンテンツタイプ
    @XmlElement()
    private Long updatePersonId;// 更新者(組織ID)
    @XmlElement()
    private Date updateDatetime;// 更新日時
    @XmlElement()
    private String fontColor = "#000000";// 文字色
    @XmlElement()
    private String backColor = "#FFFFFF";// 背景色
    @XmlElement()
    private String useParts;// 使用部品
    @XmlElement()
    private String workNumber;// 作業番号

    @XmlElement()
    private String workCheckInfo;// 検査情報(JSON)
    @XmlElement()
    private String workAddInfo;// 追加情報(JSON)
    @XmlElement()
    private String serviceInfo;// サービス情報(JSON)
    @XmlElement()
    private String displayItems;// 表示項目(JSON)

    @XmlElement()
    private Long approvalId; // 申請ID
    @XmlElement()
    private ApprovalStatusEnum approvalState; // 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)

    @XmlElement()
    private Integer verInfo;// 排他用バーション

    @XmlTransient
    private List<WorkPropertyInfoEntity> propertyInfoCollection = null; // 工程プロパティ一覧

    @XmlElementWrapper(name = "workSections")
    @XmlElement(name = "workSection")
    private List<WorkSectionInfoEntity> workSectionCollection; // 工程セクション一覧

    @XmlElementWrapper(name = "devices")
    @XmlElement(name = "device")
    private List<EquipmentInfoEntity> deviceCollection;// デバイス一覧

    @XmlElement()
    private Integer latestRev;// 最新版数

    @XmlElement()
    private ApprovalInfoEntity approval; // 申請情報

    @XmlTransient
    private String parentName; // 親階層名

    /**
     * コンストラクタ
     */
    public WorkInfoEntity() {
    }

    /**
     * コンストラクタ
     *
     * @param workId 工程ID
     * @param parentId 親階層ID
     * @param workName 工程名
     * @param taktTime タクトタイム[ms]
     * @param content コンテンツ
     * @param contentType コンテンツタイプ
     * @param updatePersonId 更新者(組織ID)
     * @param updateDatetime 更新日時
     * @param fontColor 文字色
     * @param backColor 背景色
     */
    public WorkInfoEntity(Long workId, Long parentId, String workName, Integer taktTime, String content, ContentTypeEnum contentType, Long updatePersonId, Date updateDatetime, String fontColor, String backColor) {
        this.workId = workId;
        this.parentId = parentId;
        this.workName = workName;
        this.taktTime = taktTime;
        this.content = content;
        this.contentType = contentType;
        this.updatePersonId = updatePersonId;
        this.updateDatetime = updateDatetime;
        this.fontColor = fontColor;
        this.backColor = backColor;
    }

    /**
     * 工程IDプロパティを取得する。
     *
     * @return 工程ID
     */
    public LongProperty workIdProperty() {
        if (Objects.isNull(this.workIdProperty)) {
            this.workIdProperty = new SimpleLongProperty(this.workId);
        }
        return this.workIdProperty;
    }

    /**
     * 親階層IDプロパティを取得する。
     *
     * @return 親階層ID
     */
    public LongProperty parentIdProperty() {
        if (Objects.isNull(this.parentIdProperty)) {
            this.parentIdProperty = new SimpleLongProperty(this.parentId);
        }
        return this.parentIdProperty;
    }

    /**
     * 工程名プロパティを取得する。
     *
     * @return 工程名
     */
    public StringProperty workNameProperty() {
        if (Objects.isNull(this.workNameProperty)) {
            this.workNameProperty = new SimpleStringProperty(this.workName);
        }
        return this.workNameProperty;
    }

    /**
     * タクトタイム[ms]プロパティを取得する。
     *
     * @return タクトタイム[ms]
     */
    public IntegerProperty taktTimeProperty() {
        if (Objects.isNull(this.taktTimeProperty)) {
            if (Objects.isNull(this.taktTime)) {
                this.taktTimeProperty = new SimpleIntegerProperty(0);
            } else {
                this.taktTimeProperty = new SimpleIntegerProperty(this.taktTime);
            }
        }
        return this.taktTimeProperty;
    }

    /**
     * コンテンツプロパティを取得する。
     *
     * @return コンテンツ
     */
    public StringProperty contentProperty() {
        if (Objects.isNull(this.contentProperty)) {
            this.contentProperty = new SimpleStringProperty(this.content);
        }
        return this.contentProperty;
    }

    /**
     * コンテンツタイププロパティを取得する。
     *
     * @return コンテンツタイプ
     */
    public ObjectProperty<ContentTypeEnum> contentTypeProperty() {
        if (Objects.isNull(this.contentTypeProperty)) {
            this.contentTypeProperty = new SimpleObjectProperty(this.contentType);
        }
        return this.contentTypeProperty;
    }

    /**
     * 更新者(組織ID)プロパティを取得する。
     *
     * @return 更新者(組織ID)
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
        if (Objects.isNull(this.updateDatetimeProperty)) {
            this.updateDatetimeProperty = new SimpleObjectProperty(this.updateDatetime);
        }
        return this.updateDatetimeProperty;
    }

    /**
     * 文字色プロパティを取得する。
     *
     * @return 文字色
     */
    public StringProperty fontColorProperty() {
        if (Objects.isNull(this.fontColorProperty)) {
            this.fontColorProperty = new SimpleStringProperty(this.fontColor);
        }
        return this.fontColorProperty;
    }

    /**
     * 背景色プロパティを取得する。
     *
     * @return 背景色
     */
    public StringProperty backColorProperty() {
        if (Objects.isNull(this.backColorProperty)) {
            this.backColorProperty = new SimpleStringProperty(this.backColor);
        }
        return this.backColorProperty;
    }

    /**
     * 使用部品プロパティを取得する。
     *
     * @return 使用部品
     */
    public StringProperty usePartsProperty() {
        if (Objects.isNull(this.usePartsProperty)) {
            this.usePartsProperty = new SimpleStringProperty(this.useParts);
        }
        return this.usePartsProperty;
    }

    /**
     * 作業番号プロパティを取得する。
     *
     * @return 作業番号
     */
    public StringProperty workNumberProperty() {
        if (Objects.isNull(this.workNumberProperty)) {
            this.workNumberProperty = new SimpleStringProperty(this.workNumber);
        }
        return this.workNumberProperty;
    }

    /**
     * 工程IDを取得する。
     *
     * @return 工程ID
     */
    public Long getWorkId() {
        if (Objects.nonNull(this.workIdProperty)) {
            return this.workIdProperty.get();
        }
        return this.workId;
    }

    /**
     * 工程IDを設定する。
     *
     * @param workId 工程ID
     */
    public void setWorkId(Long workId) {
        if (Objects.nonNull(this.workIdProperty)) {
            this.workIdProperty.set(workId);
        } else {
            this.workId = workId;
        }
    }

    /**
     * 親階層IDを取得する。
     *
     * @return 親階層ID
     */
    public Long getParentId() {
        if (Objects.nonNull(this.parentIdProperty)) {
            return this.parentIdProperty.get();
        }
        return this.parentId;
    }

    /**
     * 親階層IDを設定する。
     *
     * @param parentId 親階層ID
     */
    public void setParentId(Long parentId) {
        if (Objects.nonNull(this.parentIdProperty)) {
            this.parentIdProperty.set(parentId);
        } else {
            this.parentId = parentId;
        }
    }

    /**
     * 工程名を取得する。
     *
     * @return 工程名
     */
    public String getWorkName() {
        if (Objects.nonNull(this.workNameProperty)) {
            return this.workNameProperty.get();
        }
        return this.workName;
    }

    /**
     * 工程名を設定する。
     *
     * @param workName 工程名
     */
    public void setWorkName(String workName) {
        if (Objects.nonNull(this.workNameProperty)) {
            this.workNameProperty.set(workName);
        } else {
            this.workName = workName;
        }
    }

    /**
     * 版数を取得する。
     *
     * @return 版数
     */
    public Integer getWorkRev() {
        return this.workRev;
    }

    /**
     * 版数を設定する。
     *
     * @param workRev 版数
     */
    public void setWorkRev(Integer workRev) {
        this.workRev = workRev;
    }

    /**
     * タクトタイム[ms]を取得する。
     *
     * @return タクトタイム[ms]
     */
    public Integer getTaktTime() {
        if (Objects.nonNull(this.taktTimeProperty)) {
            return taktTimeProperty.get();
        }
        return this.taktTime;
    }

    /**
     * タクトタイム[ms]を設定する。
     *
     * @param taktTime タクトタイム[ms]
     */
    public void setTaktTime(Integer taktTime) {
        if (Objects.nonNull(this.taktTimeProperty)) {
            this.taktTimeProperty.set(taktTime);
        } else {
            this.taktTime = taktTime;
        }
    }

    /**
     * コンテンツを取得する。
     *
     * @return コンテンツ
     */
    public String getContent() {
        if (Objects.nonNull(this.contentProperty)) {
            return this.contentProperty.get();
        }
        return this.content;
    }

    /**
     * コンテンツを設定する。
     *
     * @param content コンテンツ
     */
    public void setContent(String content) {
        if (Objects.nonNull(this.contentProperty)) {
            this.contentProperty.set(content);
        } else {
            this.content = content;
        }
    }

    /**
     * コンテンツタイプを取得する。
     *
     * @return コンテンツタイプ
     */
    public ContentTypeEnum getContentType() {
        if (Objects.nonNull(this.contentTypeProperty)) {
            return this.contentTypeProperty.get();
        }
        return this.contentType;
    }

    /**
     * コンテンツタイプを設定する。
     *
     * @param contentType コンテンツタイプ
     */
    public void setContentType(ContentTypeEnum contentType) {
        if (Objects.nonNull(this.contentTypeProperty)) {
            this.contentTypeProperty.set(contentType);
        } else {
            this.contentType = contentType;
        }
    }

    /**
     * 更新者(組織ID)を取得する。
     *
     * @return 更新者(組織ID)
     */
    public Long getUpdatePersonId() {
        if (Objects.nonNull(this.updatePersonIdProperty)) {
            return this.updatePersonIdProperty.get();
        }
        return this.updatePersonId;
    }

    /**
     * 更新者(組織ID)を設定する。
     *
     * @param updatePersonId 更新者(組織ID)
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
    public Date getUpdateDatetime() {
        if (Objects.nonNull(this.updateDatetimeProperty)) {
            return this.updateDatetimeProperty.get();
        }
        return this.updateDatetime;
    }

    /**
     * 更新日時を設定する。
     *
     * @param updateDatetime 更新日時
     */
    public void setUpdateDatetime(Date updateDatetime) {
        if (Objects.nonNull(this.updateDatetimeProperty)) {
            this.updateDatetimeProperty.set(updateDatetime);
        } else {
            this.updateDatetime = updateDatetime;
        }
    }

    /**
     * 文字色を取得する。
     *
     * @return 文字色
     */
    public String getFontColor() {
        if (Objects.nonNull(this.fontColorProperty)) {
            return this.fontColorProperty.get();
        }
        return this.fontColor;
    }

    /**
     * 文字色を設定する。
     *
     * @param fontColor 文字色
     */
    public void setFontColor(String fontColor) {
        if (Objects.nonNull(this.fontColorProperty)) {
            this.fontColorProperty.set(fontColor);
        } else {
            this.fontColor = fontColor;
        }
    }

    /**
     * 背景色を取得する。
     *
     * @return 背景色
     */
    public String getBackColor() {
        if (Objects.nonNull(this.backColorProperty)) {
            return this.backColorProperty.get();
        }
        return this.backColor;
    }

    /**
     * 背景色を設定する。
     *
     * @param backColor 背景色
     */
    public void setBackColor(String backColor) {
        if (Objects.nonNull(this.backColorProperty)) {
            this.backColorProperty.set(backColor);
        } else {
            this.backColor = backColor;
        }
    }

    /**
     * 使用部品を取得する。
     *
     * @return 使用部品
     */
    public String getUseParts() {
        if (Objects.nonNull(this.usePartsProperty)) {
            return this.usePartsProperty.get();
        }
        return this.useParts;
    }

    /**
     * 使用部品を設定する。
     *
     * @param useParts 使用部品
     */
    public void setUseParts(String useParts) {
        if (Objects.nonNull(this.usePartsProperty)) {
            this.usePartsProperty.set(useParts);
        } else {
            this.useParts = useParts;
        }
    }

    /**
     * 作業番号を取得する。
     *
     * @return 作業番号
     */
    public String getWorkNumber() {
        if (Objects.nonNull(this.workNumberProperty)) {
            return this.workNumberProperty.get();
        }
        return this.workNumber;
    }

    /**
     * 作業番号を設定する。
     *
     * @param workNumber 作業番号
     */
    public void setWorkNumber(String workNumber) {
        if (Objects.nonNull(this.workNumberProperty)) {
            this.workNumberProperty.set(workNumber);
        } else {
            this.workNumber = workNumber;
        }
    }
    
    /**
     * デバイス一覧を取得する。
     *
     * @return デバイス一覧
     */
    public List<EquipmentInfoEntity> getDeviceCollection() {
        return deviceCollection;
    }

    /**
     * デバイス一覧を設定する。
     *
     * @param deviceCollection デバイス一覧
     */
    public void setDeviceCollection(List<EquipmentInfoEntity> deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    /**
     * 検査情報を取得する。
     *
     * @return 検査情報
     */
    public String getWorkCheckInfo() {
        return this.workCheckInfo;
    }

    /**
     * 検査情報を設定する。
     *
     * @param workCheckInfo 検査情報
     */
    public void setWorkCheckInfo(String workCheckInfo) {
        this.workCheckInfo = workCheckInfo;
    }

    /**
     * 追加情報(JSON)を取得する。
     *
     * @return 追加情報(JSON)
     */
    public String getWorkAddInfo() {
        return this.workAddInfo;
    }

    /**
     * 追加情報(JSON)を設定する。
     *
     * @param workAddInfo 追加情報(JSON)
     */
    public void setWorkAddInfo(String workAddInfo) {
        this.workAddInfo = workAddInfo;
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
     * 表示項目(JSON)を取得する。
     *
     * @return 表示項目(JSON)
     */
    public String getDisplayItems() {
        return this.displayItems;
    }

    /**
     * 表示項目(JSON)を設定する。
     *
     * @param displayItems 表示項目(JSON)
     */
    public void setDisplayItems(String displayItems) {
        this.displayItems = displayItems;
    }

    /**
     * 申請IDを取得する。
     *
     * @return 申請ID
     */
    public Long getApprovalId() {
        return this.approvalId;
    }

    /**
     * 申請IDを設定する。
     *
     * @param approvalId 申請ID
     */
    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    /**
     * 承認状態を取得する。
     *
     * @return 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public ApprovalStatusEnum getApprovalState() {
        return this.approvalState;
    }

    /**
     * 承認状態を設定する。
     *
     * @param approvalState 承認状態(0:未承認, 1:申請中, 2:取消, 3:差戻, 4:最終承認済)
     */
    public void setApprovalState(ApprovalStatusEnum approvalState) {
        this.approvalState = approvalState;
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


    public void addPropertyInfo(WorkPropertyInfoEntity entity) {
        if (Objects.isNull(this.propertyInfoCollection)) {
            if (StringUtils.isEmpty(this.workAddInfo)) {
                this.propertyInfoCollection = new ArrayList<>();
            } else {
                this.propertyInfoCollection = JsonUtils.jsonToObjects(this.workCheckInfo, WorkPropertyInfoEntity[].class);
            }
        }
        this.propertyInfoCollection.add(entity);
    }

    /**
     * 工程プロパティ一覧を取得する。
     *
     * @return 工程プロパティ一覧
     */
    public List<WorkPropertyInfoEntity> getPropertyInfoCollection() {
        // リストがnull (JSONから未変換) の場合、JSONから変換してリストにセット
        if (Objects.isNull(this.propertyInfoCollection)) {
            this.propertyInfoCollection = JsonUtils.jsonToObjects(this.workAddInfo, WorkPropertyInfoEntity[].class);

            List<WorkPropertyInfoEntity> checkInfos = JsonUtils.jsonToObjects(this.workCheckInfo, WorkPropertyInfoEntity[].class);
            if (!checkInfos.isEmpty()) {
                this.propertyInfoCollection.addAll(checkInfos);
            }
        }
        return this.propertyInfoCollection;
    }

    /**
     * 工程プロパティ一覧を設定する。
     *
     * @param propertyInfoCollection 工程プロパティ一覧
     */
    public void setPropertyInfoCollection(List<WorkPropertyInfoEntity> propertyInfoCollection) {
        this.propertyInfoCollection = propertyInfoCollection;
    }

    /**
     * 工程セクション一覧を取得する。
     *
     * @return 工程セクション一覧
     */
    public List<WorkSectionInfoEntity> getWorkSectionCollection() {
        if (Objects.isNull(this.workSectionCollection)) {
            this.workSectionCollection = new LinkedList<>();
        }
        return this.workSectionCollection;
    }

    /**
     * 工程セクション一覧を設定する。
     *
     * @param workSectionCollection 工程セクション一覧
     */
    public void setWorkSectionCollection(List<WorkSectionInfoEntity> workSectionCollection) {
        this.workSectionCollection = workSectionCollection;
    }

    /**
     * 工程セクションを取得する。
     *
     * @param workSectionOrder 工程セクション表示順
     * @return 工程セクション
     */
    public WorkSectionInfoEntity getWorkSection(long workSectionOrder) {
        WorkSectionInfoEntity workSection = null;
        Optional<WorkSectionInfoEntity> optional = this.getWorkSectionCollection().stream().filter(o -> o.getWorkSectionOrder() == workSectionOrder).findFirst();
        if (optional.isPresent()) {
            workSection = optional.get();
        } else {
            workSection = new WorkSectionInfoEntity();
        }
        return workSection;
    }

    /**
     * 最新版数を取得する。
     *
     * @return 最新版数
     */
    public Integer getLatestRev() {
        return this.latestRev;
    }

    /**
     * 最新版数を設定する。
     *
     * @param latestRev 最新版数
     */
    public void setLatestRev(Integer latestRev) {
        this.latestRev = latestRev;
    }

    /**
     * 申請情報を取得する。
     *
     * @return 申請情報
     */
    public ApprovalInfoEntity getApproval() {
        return this.approval;
    }

    /**
     * 申請情報を設定する。
     *
     * @param approval 申請情報
     */
    public void setApproval(ApprovalInfoEntity approval) {
        this.approval = approval;
    }

    /**
     * 親階層名を取得する。
     *
     * @return 親階層名
     */
    public String getParentName() {
        return parentName;
    }
    /**
     * 親階層名を設定する。
     *
     * @param parentName 親階層名
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * メンバー更新
     */
    public void updateMember() {
        this.workName = getWorkName();
        this.workRev = getWorkRev();
        this.workNumber = getWorkNumber();
        this.taktTime = getTaktTime();
        this.content = getContent();
        this.backColor = getBackColor();
        this.fontColor = getFontColor();
        this.useParts = getUseParts();
        this.workCheckInfo = getWorkCheckInfo();
        this.workAddInfo = getWorkAddInfo();
        this.serviceInfo = getServiceInfo();
        this.verInfo = getVerInfo();
        this.displayItems = getDisplayItems();
        this.latestRev = getLatestRev();
        this.approvalId = getApprovalId();
        this.approvalState = getApprovalState();
        this.approval = getApproval();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.workId ^ (this.workId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.workName);
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
        final WorkInfoEntity other = (WorkInfoEntity) obj;
        if (!Objects.equals(this.getWorkId(), other.getWorkId())) {
            return false;
        }
        if (!Objects.equals(this.getWorkName(), other.getWorkName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder("WorkInfoEntity{")
                .append("workId=").append(this.workId)
                .append(", workName=").append(this.workName)
                .append(", workRev=").append(this.workRev)
                .append(", taktTime=").append(this.taktTime)
                .append(", content=").append(this.content)
                .append(", contentType=").append(this.contentType)
                .append(", updatePersonId=").append(this.updatePersonId)
                .append(", updateDatetime=").append(this.updateDatetime)
                .append(", fontColor=").append(this.fontColor)
                .append(", backColor=").append(this.backColor)
                .append(", useParts=").append(this.useParts)
                .append(", workNumber=").append(this.workNumber)
                .append(", approvalId=").append(this.approvalId)
                .append(", approvalState=").append(this.approvalState)
                .append(", parentId=").append(this.parentId)
                .append(", latestRev=").append(this.latestRev)
                .append(", verInfo=").append(this.verInfo)
                .append("}")
                .toString();
    }

    /**
     * オブジェクトをコピーする
     *
     * @return
     */
    @Override
    public WorkInfoEntity clone() {
        WorkInfoEntity entity = new WorkInfoEntity();

        entity.setWorkName(this.getWorkName());
        entity.setWorkRev(this.getWorkRev());
        entity.setWorkNumber(this.getWorkNumber());
        entity.setTaktTime(this.getTaktTime());
        entity.setContent(this.getContent());
        entity.setBackColor(this.getBackColor());
        entity.setFontColor(this.getFontColor());
        entity.setUseParts(this.getUseParts());

        // 追加情報
        if (Objects.nonNull(this.propertyInfoCollection)) {
            List<WorkPropertyInfoEntity> list = new LinkedList<>();
            this.propertyInfoCollection.stream().forEach(c -> list.add(c.clone()));
            entity.setPropertyInfoCollection(list);
        }

        // セクション
        if (Objects.nonNull(this.workSectionCollection)) {
            List<WorkSectionInfoEntity> list = new LinkedList();
            this.workSectionCollection.stream().forEach(c -> list.add(c.clone()));
            entity.setWorkSectionCollection(list);
        }

        // 検査情報
        entity.setWorkCheckInfo(this.getWorkCheckInfo());
        // 追加情報
        entity.setWorkAddInfo(this.getWorkAddInfo());
        // サービス情報
        entity.setServiceInfo(this.getServiceInfo());

        entity.setVerInfo(this.getVerInfo());
        // 表示項目
        entity.setDisplayItems(this.getDisplayItems());

        // 最新版数
        entity.setLatestRev(this.getLatestRev());
        // 申請情報
        entity.setApprovalId(this.getApprovalId());
        entity.setApprovalState(this.getApprovalState());
        entity.setApproval(this.getApproval());

        return entity;
    }

    /**
     * 表示される情報が一致するか調べる
     *
     * @param other
     * @return
     */
    public boolean displayInfoEquals(WorkInfoEntity other) {
        if (Objects.equals(getWorkName(), other.getWorkName())
                && Objects.equals(getWorkNumber(), other.getWorkNumber())
                && Objects.equals(getTaktTime(), other.getTaktTime())
                && Objects.equals(getContent(), other.getContent())
                && Objects.equals(getBackColor(), other.getBackColor())
                && Objects.equals(getFontColor(), other.getFontColor())
                && Objects.equals(getUseParts(), other.getUseParts())
                && Objects.equals(getDisplayItems(), other.getDisplayItems())
                && propertyInfoListEquals(getPropertyInfoCollection(), other.getPropertyInfoCollection())
                && workSectionInfoEquals(getWorkSectionCollection(), other.getWorkSectionCollection())
                ) {
            return true;
        }

        return false;
    }

    /**
     * 追加情報のリストが一致するか調べる
     *
     * @param a
     * @param b
     * @return
     */
    private boolean propertyInfoListEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        java.util.Iterator<WorkPropertyInfoEntity> it1 = a.iterator();
        java.util.Iterator<WorkPropertyInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            WorkPropertyInfoEntity entity1 = it1.next();
            WorkPropertyInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * セクションのリストが一致するか調べる
     *
     * @param a
     * @param b
     * @return
     */
    private boolean workSectionInfoEquals(List a, List b) {
        if (a.size() != b.size()) {
            return false;
        }

        java.util.Iterator<WorkSectionInfoEntity> it1 = a.iterator();
        java.util.Iterator<WorkSectionInfoEntity> it2 = b.iterator();

        while (it1.hasNext()) {
            WorkSectionInfoEntity entity1 = it1.next();
            WorkSectionInfoEntity entity2 = it2.next();
            if (!entity1.equalsDisplayInfo(entity2)) {
                return false;
            }
        }

        return true;
    }
}
